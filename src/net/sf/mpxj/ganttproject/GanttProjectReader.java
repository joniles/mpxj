/*
 * file:       GanttProjectReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       22 March 2017
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package net.sf.mpxj.ganttproject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.CustomField;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.Pair;
import net.sf.mpxj.common.ResourceFieldLists;
import net.sf.mpxj.ganttproject.schema.Calendars;
import net.sf.mpxj.ganttproject.schema.CustomPropertyDefinition;
import net.sf.mpxj.ganttproject.schema.CustomResourceProperty;
import net.sf.mpxj.ganttproject.schema.DayTypes;
import net.sf.mpxj.ganttproject.schema.DefaultWeek;
import net.sf.mpxj.ganttproject.schema.Project;
import net.sf.mpxj.ganttproject.schema.Resources;
import net.sf.mpxj.ganttproject.schema.Role;
import net.sf.mpxj.ganttproject.schema.Roles;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;

//
// TODO: cleanup member data on exit
//

/**
 * This class creates a new ProjectFile instance by reading a GanttProject file.
 */
public final class GanttProjectReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoResourceUniqueID(false);
         config.setAutoOutlineLevel(true);
         config.setAutoOutlineNumber(true);
         config.setAutoWBS(true);

         m_eventManager.addProjectListeners(m_projectListeners);

         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser = factory.newSAXParser();
         XMLReader xmlReader = saxParser.getXMLReader();
         SAXSource doc = new SAXSource(xmlReader, new InputSource(stream));

         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();

         Project ganttProject = (Project) unmarshaller.unmarshal(doc);

         readProjectProperties(ganttProject);
         readCalendars(ganttProject);
         readTasks(ganttProject, m_projectFile);
         readResources(ganttProject);
         readRelationships(ganttProject);

         //
         // Ensure that the unique ID counters are correct
         //
         config.updateUniqueCounters();

         return m_projectFile;
      }

      catch (ParserConfigurationException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      catch (JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      catch (SAXException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      finally
      {
         m_projectFile = null;
      }
   }

   /**
    * This method extracts project properties from a GanttProject file.
    *
    * @param ganttProject GanttProject file
    */
   private void readProjectProperties(Project ganttProject)
   {
      ProjectProperties mpxjProperties = m_projectFile.getProjectProperties();
      mpxjProperties.setName(ganttProject.getName());
      mpxjProperties.setCompany(ganttProject.getCompany());
      mpxjProperties.setDefaultDurationUnits(TimeUnit.DAYS);

      m_localeDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, new Locale(ganttProject.getLocale()));
   }

   /**
    * This method extracts calendar data from a GanttProject file.
    *
    * @param ganttProject Root node of the GanttProject file
    */
   private void readCalendars(Project ganttProject)
   {
      m_mpxjCalendar = m_projectFile.addCalendar();
      m_mpxjCalendar.setName(ProjectCalendar.DEFAULT_BASE_CALENDAR_NAME);

      Calendars gpCalendar = ganttProject.getCalendars();
      setWorkingDays(m_mpxjCalendar, gpCalendar);
      setExceptions(m_mpxjCalendar, gpCalendar);
   }

   private void setWorkingDays(ProjectCalendar mpxjCalendar, Calendars gpCalendar)
   {
      DayTypes dayTypes = gpCalendar.getDayTypes();
      DefaultWeek defaultWeek = dayTypes.getDefaultWeek();

      mpxjCalendar.setWorkingDay(Day.MONDAY, isWorkingDay(defaultWeek.getMon()));
      mpxjCalendar.setWorkingDay(Day.TUESDAY, isWorkingDay(defaultWeek.getTue()));
      mpxjCalendar.setWorkingDay(Day.WEDNESDAY, isWorkingDay(defaultWeek.getWed()));
      mpxjCalendar.setWorkingDay(Day.THURSDAY, isWorkingDay(defaultWeek.getThu()));
      mpxjCalendar.setWorkingDay(Day.FRIDAY, isWorkingDay(defaultWeek.getFri()));
      mpxjCalendar.setWorkingDay(Day.SATURDAY, isWorkingDay(defaultWeek.getSat()));
      mpxjCalendar.setWorkingDay(Day.SUNDAY, isWorkingDay(defaultWeek.getSun()));

      for (Day day : Day.values())
      {
         if (mpxjCalendar.isWorkingDay(day))
         {
            ProjectCalendarHours hours = mpxjCalendar.addCalendarHours(day);
            hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_MORNING);
            hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_AFTERNOON);
         }
      }
   }

   private boolean isWorkingDay(Integer value)
   {
      return NumberHelper.getInt(value) == 0;
   }

   private void setExceptions(ProjectCalendar mpxjCalendar, Calendars gpCalendar)
   {
      List<net.sf.mpxj.ganttproject.schema.Date> dates = gpCalendar.getDate();
      for (net.sf.mpxj.ganttproject.schema.Date date : dates)
      {
         addException(mpxjCalendar, date);
      }
   }

   private void addException(ProjectCalendar mpxjCalendar, net.sf.mpxj.ganttproject.schema.Date date)
   {
      String year = date.getYear();
      if (year == null || year.isEmpty())
      {
         // In order to process recurring exceptions using MPXJ, we need a start and end date
         // to constrain the number of dates we generate.
         // May need to pre-process the tasks in order to calculate a start and finish date.
         // TODO: handle recurring exceptions
      }
      else
      {
         Calendar calendar = Calendar.getInstance();
         calendar.set(Calendar.YEAR, Integer.parseInt(year));
         calendar.set(Calendar.MONTH, NumberHelper.getInt(date.getMonth()));
         calendar.set(Calendar.DAY_OF_MONTH, NumberHelper.getInt(date.getDate()));
         Date exceptionDate = calendar.getTime();
         ProjectCalendarException exception = mpxjCalendar.addCalendarException(exceptionDate, exceptionDate);

         // TODO: not sure how NEUTRAL should be handled
         if ("WORKING_DAY".equals(date.getType()))
         {
            exception.addRange(ProjectCalendarWeek.DEFAULT_WORKING_MORNING);
            exception.addRange(ProjectCalendarWeek.DEFAULT_WORKING_AFTERNOON);
         }
      }
   }

   /**
    * This method extracts resource data from a GanttProject file.
    *
    * @param ganttProject parent node for resources
    */
   private void readResources(Project ganttProject)
   {
      Resources resources = ganttProject.getResources();
      readCustomPropertyDefinitions(resources);
      readRoleDefinitions(ganttProject);

      for (net.sf.mpxj.ganttproject.schema.Resource gpResource : resources.getResource())
      {
         readResource(gpResource);
      }
   }

   private void readCustomPropertyDefinitions(Resources gpResources)
   {
      CustomField field = m_projectFile.getCustomFields().getCustomField(ResourceField.TEXT1);
      field.setAlias("Phone");

      for (CustomPropertyDefinition definition : gpResources.getCustomPropertyDefinition())
      {
         //
         // Find the next available field of the correct type.
         //
         String type = definition.getType();
         FieldType fieldType = RESOURCE_PROPERTY_TYPES.get(type).getField();

         //
         // If we have run out of fields of the right type, try using a text field.
         //
         if (fieldType == null)
         {
            fieldType = RESOURCE_PROPERTY_TYPES.get("text").getField();
         }

         //
         // If we actually have a field available, set the alias to match
         // the name used in Ganttproject.
         //
         if (fieldType != null)
         {
            field = m_projectFile.getCustomFields().getCustomField(fieldType);
            field.setAlias(definition.getName());
            String defaultValue = definition.getDefaultValue();
            if (defaultValue != null && defaultValue.isEmpty())
            {
               defaultValue = null;
            }
            m_resourcePropertyDefinitions.put(definition.getId(), new Pair<FieldType, String>(fieldType, defaultValue));
         }
      }
   }

   private void readRoleDefinitions(Project ganttProject)
   {
      m_roleDefinitions.put("Default:1", "project manager");

      for (Roles roles : ganttProject.getRoles())
      {
         if ("Default".equals(roles.getRolesetName()))
         {
            continue;
         }

         for (Role role : roles.getRole())
         {
            m_roleDefinitions.put(role.getId(), role.getName());
         }
      }
   }

   /**
    * This method extracts data for a single resource from a GanttProject file.
    *
    * @param gpResource resource data
    */
   private void readResource(net.sf.mpxj.ganttproject.schema.Resource gpResource)
   {
      //
      // Read fixed fields
      //
      Resource mpxjResource = m_projectFile.addResource();
      mpxjResource.setUniqueID(gpResource.getId());
      mpxjResource.setName(gpResource.getName());
      mpxjResource.setEmailAddress(gpResource.getContacts());
      mpxjResource.setText(1, gpResource.getPhone());
      mpxjResource.setGroup(m_roleDefinitions.get(gpResource.getFunction()));

      net.sf.mpxj.ganttproject.schema.Rate gpRate = gpResource.getRate();
      if (gpRate != null)
      {
         mpxjResource.setStandardRate(new Rate(gpRate.getValueAttribute(), TimeUnit.DAYS));
      }

      //
      // Populate custom field default values
      //
      Map<FieldType, Object> customFields = new HashMap<FieldType, Object>();
      for (Pair<FieldType, String> definition : m_resourcePropertyDefinitions.values())
      {
         customFields.put(definition.getFirst(), definition.getSecond());
      }

      //
      // Update with custom field actual values
      //
      for (CustomResourceProperty property : gpResource.getCustomProperty())
      {
         Pair<FieldType, String> definition = m_resourcePropertyDefinitions.get(property.getDefinitionId());
         if (definition != null)
         {
            //
            // Retrieve the value. If it is empty, use the default.
            //
            String value = property.getValueAttribute();
            if (value.isEmpty())
            {
               value = null;
            }

            //
            // If we have a value,convert it to the correct type
            //
            if (value != null)
            {
               Object result;

               switch (definition.getFirst().getDataType())
               {
                  case NUMERIC:
                  {
                     if (value.indexOf('.') == -1)
                     {
                        result = Integer.valueOf(value);
                     }
                     else
                     {
                        result = Double.valueOf(value);
                     }
                     break;
                  }

                  case DATE:
                  {
                     try
                     {
                        result = m_localeDateFormat.parse(value);
                     }
                     catch (ParseException ex)
                     {
                        result = null;
                     }
                     break;
                  }

                  case BOOLEAN:
                  {
                     result = Boolean.valueOf(value.equals("true"));
                     break;
                  }

                  default:
                  {
                     result = value;
                     break;
                  }
               }

               if (result != null)
               {
                  customFields.put(definition.getFirst(), result);
               }
            }
         }
      }

      for (Map.Entry<FieldType, Object> item : customFields.entrySet())
      {
         if (item.getValue() != null)
         {
            mpxjResource.set(item.getKey(), item.getValue());
         }
      }
   }

   private void readTasks(Project ganttProject, ChildTaskContainer parent)
   {
      for (net.sf.mpxj.ganttproject.schema.Task task : ganttProject.getTasks().getTask())
      {
         readTask(parent, task);
      }
   }

   private void readTask(ChildTaskContainer parent, net.sf.mpxj.ganttproject.schema.Task gpTask)
   {
      Task mpxjTask = parent.addTask();
      mpxjTask.setUniqueID(Integer.valueOf(NumberHelper.getInt(gpTask.getId()) + 1));
      mpxjTask.setName(gpTask.getName());
      mpxjTask.setPercentageComplete(gpTask.getComplete());
      mpxjTask.setPriority(getPriority(gpTask.getPriority()));
      // TODO: url

      Duration duration = Duration.getInstance(NumberHelper.getDouble(gpTask.getDuration()), TimeUnit.DAYS);
      mpxjTask.setDuration(duration);

      if (duration.getDuration() == 0)
      {
         mpxjTask.setMilestone(true);
      }
      else
      {
         mpxjTask.setStart(gpTask.getStart());
         mpxjTask.setFinish(m_mpxjCalendar.getDate(gpTask.getStart(), mpxjTask.getDuration(), false));
      }

      mpxjTask.setConstraintDate(gpTask.getThirdDate());
      if (mpxjTask.getConstraintDate() != null)
      {
         // TODO: you don't appear to be able to change this setting in GanttProject
         // task.getThirdDateConstraint()
         mpxjTask.setConstraintType(ConstraintType.START_NO_EARLIER_THAN);
      }
   }

   private Priority getPriority(Integer priority)
   {
      int result;
      if (priority == null)
      {
         result = Priority.MEDIUM;
      }
      else
      {
         int index = priority.intValue();
         if (index < 0 || index >= PRIORITY.length)
         {
            result = Priority.MEDIUM;
         }
         else
         {
            result = PRIORITY[index];
         }
      }
      return Priority.getInstance(result);
   }

   /**
    * Reads Phoenix resource assignments.
    *
    * @param mpxjResource MPXJ resource
    * @param res Phoenix resource
    */
   //   private void readAssignments(Resource mpxjResource, net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Resources.Resource res)
   //   {
   //      for (Assignment assignment : res.getAssignment())
   //      {
   //         readAssignment(mpxjResource, assignment);
   //      }
   //   }

   /**
    * Read a single resource assignment.
    *
    * @param resource MPXJ resource
    * @param assignment Phoenix assignment
    */
   //   private void readAssignment(Resource resource, Assignment assignment)
   //   {
   //      Task task = m_activityMap.get(assignment.getActivity());
   //      if (task != null)
   //      {
   //         task.addResourceAssignment(resource);
   //      }
   //   }

   /**
    * Read task relationships from a Phoenix file.
    *
    * @param ganttProject Phoenix project data
    */
   private void readRelationships(Project ganttProject)
   {
      //      for (Relationship relation : phoenixProject.getRelationships().getRelationship())
      //      {
      //         readRelation(relation);
      //      }
   }

   /**
    * Read an individual Phoenix task relationship.
    *
    * @param relation Phoenix task relationship
    */
   //   private void readRelation(Relationship relation)
   //   {
   //      Task predecessor = m_projectFile.getTaskByUniqueID(relation.getPredecessor());
   //      Task successor = m_projectFile.getTaskByUniqueID(relation.getSuccessor());
   //      if (predecessor != null && successor != null)
   //      {
   //         Duration lag = relation.getLag();
   //         RelationType type = relation.getType();
   //         successor.addPredecessor(predecessor, type, lag);
   //      }
   //   }

   private ProjectFile m_projectFile;
   private ProjectCalendar m_mpxjCalendar;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
   private DateFormat m_localeDateFormat;
   private Map<String, Pair<FieldType, String>> m_resourcePropertyDefinitions = new HashMap<String, Pair<FieldType, String>>();
   private Map<String, String> m_roleDefinitions = new HashMap<String, String>();

   private static final Map<String, CustomProperty> RESOURCE_PROPERTY_TYPES = new HashMap<String, CustomProperty>();
   static
   {
      CustomProperty numeric = new CustomProperty(ResourceFieldLists.CUSTOM_NUMBER);
      RESOURCE_PROPERTY_TYPES.put("int", numeric);
      RESOURCE_PROPERTY_TYPES.put("double", numeric);
      RESOURCE_PROPERTY_TYPES.put("text", new CustomProperty(ResourceFieldLists.CUSTOM_TEXT, 1));
      RESOURCE_PROPERTY_TYPES.put("date", new CustomProperty(ResourceFieldLists.CUSTOM_DATE));
      RESOURCE_PROPERTY_TYPES.put("boolean", new CustomProperty(ResourceFieldLists.CUSTOM_FLAG));
   }

   private static final int[] PRIORITY =
   {
      Priority.LOW, // 0 - Low
      Priority.MEDIUM, // 1 - Normal
      Priority.HIGH, // 2 - High
      Priority.LOWEST, // 3- Lowest
      Priority.HIGHEST, // 4 - Highest
   };

   /**
    * Cached context to minimise construction cost.
    */
   private static JAXBContext CONTEXT;

   /**
    * Note any error occurring during context construction.
    */
   private static JAXBException CONTEXT_EXCEPTION;

   static
   {
      try
      {
         //
         // JAXB RI property to speed up construction
         //
         System.setProperty("com.sun.xml.bind.v2.runtime.JAXBContextImpl.fastBoot", "true");

         //
         // Construct the context
         //
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.ganttproject.schema", GanttProjectReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
