/*
 * file:       MSPDIWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       2005-12-30
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

package org.mpxj.mspdi;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import org.mpxj.AccrueType;
import org.mpxj.AssignmentField;
import org.mpxj.Availability;
import org.mpxj.AvailabilityTable;
import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.CustomField;
import org.mpxj.CustomFieldContainer;
import org.mpxj.CustomFieldLookupTable;
import org.mpxj.CustomFieldValueDataType;
import org.mpxj.CustomFieldValueMask;
import org.mpxj.DataType;
import org.mpxj.LocalDateTimeRange;
import java.time.DayOfWeek;

import org.mpxj.ProjectCalendarDays;
import org.mpxj.TimephasedItem;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.DayType;
import org.mpxj.Duration;
import org.mpxj.EventManager;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectCalendarWeek;
import org.mpxj.ProjectConfig;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.RecurringData;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceField;
import org.mpxj.ResourceType;
import org.mpxj.ScheduleFrom;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TaskMode;
import org.mpxj.LocalTimeRange;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedCost;
import org.mpxj.TimephasedWork;
import org.mpxj.UserDefinedField;
import org.mpxj.common.AssignmentFieldLists;
import org.mpxj.common.FieldLists;
import org.mpxj.common.FieldTypeHelper;
import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.MarshallerHelper;
import org.mpxj.common.MicrosoftProjectConstants;
import org.mpxj.common.MicrosoftProjectUniqueIDMapper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ProjectCalendarHelper;
import org.mpxj.common.ResourceFieldLists;
import org.mpxj.common.StringHelper;
import org.mpxj.common.TaskFieldLists;
import org.mpxj.mpp.UserDefinedFieldMap;
import org.mpxj.mpp.CustomFieldValueItem;
import org.mpxj.mpp.EnterpriseCustomFieldDataType;
import org.mpxj.mspdi.schema.ObjectFactory;
import org.mpxj.mspdi.schema.Project;
import org.mpxj.mspdi.schema.Project.Calendars.Calendar.Exceptions;
import org.mpxj.mspdi.schema.Project.Calendars.Calendar.WorkWeeks;
import org.mpxj.mspdi.schema.Project.Calendars.Calendar.WorkWeeks.WorkWeek;
import org.mpxj.mspdi.schema.Project.Calendars.Calendar.WorkWeeks.WorkWeek.TimePeriod;
import org.mpxj.mspdi.schema.Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays;
import org.mpxj.mspdi.schema.Project.Resources.Resource.AvailabilityPeriods;
import org.mpxj.mspdi.schema.Project.Resources.Resource.AvailabilityPeriods.AvailabilityPeriod;
import org.mpxj.mspdi.schema.Project.Resources.Resource.Rates;
import org.mpxj.mspdi.schema.TimephasedDataType;
import org.mpxj.writer.AbstractProjectWriter;

/**
 * This class creates a new MSPDI file from the contents of an ProjectFile instance.
 */
public final class MSPDIWriter extends AbstractProjectWriter
{
   /**
    * Sets a flag to determine if the output is readable by MS Project, or
    * is "spec compliant".
    *
    * @param flag true if output is readable by MS Project
    */
   public void setMicrosoftProjectCompatibleOutput(boolean flag)
   {
      m_compatibleOutput = flag;
   }

   /**
    * Retrieves a flag which determines if the output is readable by MS Project.
    *
    * @return  true if output is readable by MS Project
    */
   public boolean getMicrosoftProjectCompatibleOutput()
   {
      return m_compatibleOutput;
   }

   /**
    * Sets a flag to control whether timephased assignment data is split
    * into days. The default is true.
    *
    * @param flag boolean flag
    */
   public void setSplitTimephasedAsDays(boolean flag)
   {
      m_splitTimephasedAsDays = flag;
   }

   /**
    * Retrieves a flag to control whether timephased assignment data is split
    * into days. The default is true.
    *
    * @return boolean true
    */
   public boolean getSplitTimephasedAsDays()
   {
      return m_splitTimephasedAsDays;
   }

   /**
    * Sets a flag to control whether timephased resource assignment data
    * is written to the file. The default is false.
    *
    * @param value boolean flag
    */
   public void setWriteTimephasedData(boolean value)
   {
      m_writeTimephasedData = value;
   }

   /**
    * Retrieves the state of the flag which controls whether timephased
    * resource assignment data is written to the file. The default is false.
    *
    * @return boolean flag
    */
   public boolean getWriteTimephasedData()
   {
      return m_writeTimephasedData;
   }

   /**
    * Pass true to this method to enable an experimental feature where
    * timephased data is generated for tasks with no timephased data present.
    * NOTE: this feature is disabled by default.
    *
    * @param value true to enable timephased data generation
    */
   public void setGenerateMissingTimephasedData(boolean value)
   {
      m_generateMissingTimephasedData = value;
   }

   /**
    * Returns true if the experimental feature to generate timephased data
    * for tasks with no timephased data present is enabled.
    * NOTE: this feature is disabled by default.
    *
    * @return true if feature enabled
    */
   public boolean getGenerateMissingTimephasedData()
   {
      return m_generateMissingTimephasedData;
   }

   /**
    * Set the save version to use when generating an MSPDI file.
    *
    * @param version save version
    */
   public void setSaveVersion(SaveVersion version)
   {
      m_saveVersion = version;
   }

   /**
    * Retrieve the save version current set.
    *
    * @return current save version
    */
   public SaveVersion getSaveVersion()
   {
      return m_saveVersion;
   }

   @Override public void write(ProjectFile projectFile, OutputStream stream) throws IOException
   {
      try
      {
         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         m_projectFile = projectFile;
         m_eventManager = m_projectFile.getEventManager();
         DatatypeConverter.setContext(m_projectFile, false);

         Marshaller marshaller = MarshallerHelper.create(CONTEXT);
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

         m_customFieldValueItems = new HashMap<>();
         m_resouceCalendarMap = new HashMap<>();
         m_extendedAttributes = getExtendedAttributesList();

         m_sourceIsMicrosoftProject = MICROSOFT_PROJECT_FILES.contains(m_projectFile.getProjectProperties().getFileType());
         m_sourceIsPrimavera = "Primavera".equals(m_projectFile.getProjectProperties().getFileApplication());
         m_userDefinedFieldMap = new UserDefinedFieldMap(projectFile, MAPPING_TARGET_CUSTOM_FIELDS);

         m_taskMapper = new MicrosoftProjectUniqueIDMapper(m_projectFile.getTasks());
         m_resourceMapper = new MicrosoftProjectUniqueIDMapper(m_projectFile.getResources());
         m_calendarMapper = new MicrosoftProjectUniqueIDMapper(m_projectFile.getCalendars());
         m_assignmentMapper = new MicrosoftProjectUniqueIDMapper(m_projectFile.getResourceAssignments());

         m_factory = new ObjectFactory();
         Project project = m_factory.createProject();

         writeProjectProperties(project);
         writeExtendedAttributeDefinitions(project);
         writeCalendars(project);
         writeResources(project);
         writeTasks(project);
         writeAssignments(project);
         writeOutlineCodes(project);

         marshaller.marshal(m_factory.createProject(project), stream);
      }

      catch (JAXBException ex)
      {
         throw new IOException(ex.toString());
      }

      finally
      {
         m_projectFile = null;
         m_factory = null;
         m_customFieldValueItems = null;
         m_resouceCalendarMap = null;
         m_taskMapper = null;
         m_resourceMapper = null;
         m_calendarMapper = null;
         m_assignmentMapper = null;
      }
   }

   /**
    * This method writes project properties to an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeProjectProperties(Project project)
   {
      ProjectProperties properties = m_projectFile.getProjectProperties();

      // If we don't have a name, use a default value.
      // MS Project uses the file name.
      String name = properties.getName();
      if (name == null || name.isEmpty())
      {
         name = "project.xml";
      }

      // If we don't have a title, provide a default.
      // This is usually the project summary task name,
      // so we'll try the first task name, otherwise we'll
      // use a generic value.
      String title = properties.getProjectTitle();
      if (title == null || title.isEmpty())
      {
         if (!m_projectFile.getTasks().isEmpty())
         {
            title = m_projectFile.getTasks().get(0).getName();
         }

         if (title == null || title.isEmpty())
         {
            title = "project";
         }
      }

      project.setActualsInSync(Boolean.valueOf(properties.getActualsInSync()));
      project.setAdminProject(Boolean.valueOf(properties.getAdminProject()));
      project.setAuthor(properties.getAuthor());
      project.setAutoAddNewResourcesAndTasks(Boolean.valueOf(properties.getAutoAddNewResourcesAndTasks()));
      project.setAutolink(Boolean.valueOf(properties.getAutolink()));
      project.setBaselineCalendar(nullIfEmpty(properties.getBaselineCalendarName()));
      project.setBaselineForEarnedValue(NumberHelper.getBigInteger(properties.getBaselineForEarnedValue()));
      project.setCalendarUID(m_projectFile.getDefaultCalendar() == null ? BigInteger.ONE : NumberHelper.getBigInteger(m_calendarMapper.getUniqueID(m_projectFile.getDefaultCalendar())));
      project.setCategory(properties.getCategory());
      project.setCompany(properties.getCompany());
      project.setCreationDate(properties.getCreationDate());
      project.setCriticalSlackLimit(NumberHelper.getBigInteger(Double.valueOf(properties.getCriticalSlackLimit().convertUnits(TimeUnit.DAYS, properties).getDuration())));
      project.setCurrencyCode(properties.getCurrencyCode());
      project.setCurrencyDigits(BigInteger.valueOf(properties.getCurrencyDigits().intValue()));
      project.setCurrencySymbol(properties.getCurrencySymbol());
      project.setCurrencySymbolPosition(properties.getSymbolPosition());
      project.setCurrentDate(properties.getCurrentDate());
      project.setDaysPerMonth(NumberHelper.getBigInteger(properties.getDaysPerMonth()));
      project.setDefaultFinishTime(properties.getDefaultEndTime());
      project.setDefaultFixedCostAccrual(properties.getDefaultFixedCostAccrual());
      project.setDefaultOvertimeRate(DatatypeConverter.printRate(properties.getDefaultOvertimeRate()));
      project.setDefaultStandardRate(DatatypeConverter.printRate(properties.getDefaultStandardRate()));
      project.setDefaultStartTime(properties.getDefaultStartTime());
      project.setDefaultTaskEVMethod(DatatypeConverter.printEarnedValueMethod(properties.getDefaultTaskEarnedValueMethod()));
      project.setDefaultTaskType(properties.getDefaultTaskType());
      project.setDurationFormat(DatatypeConverter.printDurationTimeUnits(properties.getDefaultDurationUnits(), false));
      project.setEarnedValueMethod(DatatypeConverter.printEarnedValueMethod(properties.getEarnedValueMethod()));
      project.setEditableActualCosts(Boolean.valueOf(properties.getEditableActualCosts()));
      project.setExtendedCreationDate(properties.getExtendedCreationDate());
      project.setFinishDate(properties.getFinishDate());
      project.setFiscalYearStart(Boolean.valueOf(properties.getFiscalYearStart()));
      project.setFYStartDate(NumberHelper.getBigInteger(properties.getFiscalYearStartMonth()));
      project.setGUID(properties.getGUID());
      project.setHonorConstraints(Boolean.valueOf(properties.getHonorConstraints()));
      project.setInsertedProjectsLikeSummary(Boolean.valueOf(properties.getInsertedProjectsLikeSummary()));
      project.setLastSaved(properties.getLastSaved());
      project.setManager(properties.getManager());
      project.setMicrosoftProjectServerURL(Boolean.valueOf(properties.getMicrosoftProjectServerURL()));
      project.setMinutesPerDay(NumberHelper.getBigInteger(properties.getMinutesPerDay()));
      project.setMinutesPerWeek(NumberHelper.getBigInteger(properties.getMinutesPerWeek()));
      project.setMoveCompletedEndsBack(Boolean.valueOf(properties.getMoveCompletedEndsBack()));
      project.setMoveCompletedEndsForward(Boolean.valueOf(properties.getMoveCompletedEndsForward()));
      project.setMoveRemainingStartsBack(Boolean.valueOf(properties.getMoveRemainingStartsBack()));
      project.setMoveRemainingStartsForward(Boolean.valueOf(properties.getMoveRemainingStartsForward()));
      project.setMultipleCriticalPaths(Boolean.valueOf(properties.getMultipleCriticalPaths()));
      project.setName(StringHelper.stripControlCharacters(name));
      project.setNewTasksEffortDriven(Boolean.valueOf(properties.getNewTasksEffortDriven()));
      project.setNewTasksEstimated(Boolean.valueOf(properties.getNewTasksEstimated()));
      project.setNewTaskStartDate(properties.getNewTaskStartIsProjectStart() ? BigInteger.ZERO : BigInteger.ONE);
      project.setNewTasksAreManual(Boolean.valueOf(properties.getNewTasksAreManual()));
      project.setProjectExternallyEdited(Boolean.valueOf(properties.getProjectExternallyEdited()));
      project.setRemoveFileProperties(Boolean.valueOf(properties.getRemoveFileProperties()));
      project.setRevision(NumberHelper.getBigInteger(properties.getRevision()));
      project.setSaveVersion(BigInteger.valueOf(m_saveVersion.getValue()));
      project.setScheduleFromStart(Boolean.valueOf(properties.getScheduleFrom() == ScheduleFrom.START));
      project.setSplitsInProgressTasks(Boolean.valueOf(properties.getSplitInProgressTasks()));
      project.setSpreadActualCost(Boolean.valueOf(properties.getSpreadActualCost()));
      project.setSpreadPercentComplete(Boolean.valueOf(properties.getSpreadPercentComplete()));
      project.setStartDate(properties.getStartDate());
      project.setStatusDate(properties.getStatusDate());
      project.setSubject(properties.getSubject());
      project.setTaskUpdatesResource(Boolean.valueOf(properties.getUpdatingTaskStatusUpdatesResourceStatus()));
      project.setTitle(title);
      project.setWeekStartDay(DatatypeConverter.printDay(properties.getWeekStartDay()));
      project.setWorkFormat(DatatypeConverter.printWorkUnits(properties.getDefaultWorkUnits()));
   }

   /**
    * This method writes project extended attribute data into an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeExtendedAttributeDefinitions(Project project)
   {
      Project.ExtendedAttributes attributes = m_factory.createProjectExtendedAttributes();
      project.setExtendedAttributes(attributes);
      List<Project.ExtendedAttributes.ExtendedAttribute> list = attributes.getExtendedAttribute();

      CustomFieldContainer customFieldContainer = m_projectFile.getCustomFields();
      for (FieldType fieldType : m_extendedAttributes)
      {
         boolean microsoftProjectUserDefinedField = false;
         BigInteger customFieldType = null;
         FieldType mappedFieldType = fieldType;

         if (fieldType instanceof UserDefinedField)
         {
            if (m_sourceIsMicrosoftProject)
            {
               // This looks like it was originally a Microsoft Project enterprise custom field.
               // We'll try to preserve its definition.
               microsoftProjectUserDefinedField = true;
               customFieldType = NumberHelper.getBigInteger(EnterpriseCustomFieldDataType.getIDFromDataType(fieldType.getDataType()));
            }
            else
            {
               // This is a generic user defined field, so we'll try to map it to a custom field
               mappedFieldType = m_userDefinedFieldMap.generateMapping(fieldType);
               if (mappedFieldType == null)
               {
                  // We have run out of fields we can map user defined fields of this type to
                  // continue through the rest of the list to see if there are other
                  // user defined fields of different types we can still map.
                  continue;
               }
            }
         }

         Project.ExtendedAttributes.ExtendedAttribute attribute = m_factory.createProjectExtendedAttributesExtendedAttribute();
         list.add(attribute);
         attribute.setFieldID(String.valueOf(FieldTypeHelper.getFieldID(mappedFieldType)));
         attribute.setFieldName(mappedFieldType.getName());

         if (microsoftProjectUserDefinedField)
         {
            attribute.setUserDef(Boolean.TRUE);
            attribute.setCFType(customFieldType);
         }

         CustomField field = customFieldContainer.get(fieldType);
         if (field != null)
         {
            attribute.setAlias(field.getAlias());
            attribute.setLtuid(field.getLookupTable().getGUID());
         }
      }
   }

   /**
    * Write outline code/custom field lookup tables.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeOutlineCodes(Project project)
   {
      Project.OutlineCodes outlineCodes = null;
      List<CustomField> allCustomFields = m_projectFile.getCustomFields().stream().filter(f -> !f.getLookupTable().isEmpty()).sorted().collect(Collectors.toList());
      for (CustomField field : allCustomFields)
      {
         if (outlineCodes == null)
         {
            outlineCodes = m_factory.createProjectOutlineCodes();
            project.setOutlineCodes(outlineCodes);
         }

         Project.OutlineCodes.OutlineCode outlineCode = m_factory.createProjectOutlineCodesOutlineCode();
         outlineCodes.getOutlineCode().add(outlineCode);
         writeOutlineCode(outlineCode, field);
      }
   }

   /**
    * Write a single outline code or custom field.
    *
    * @param outlineCode outline codes root node
    * @param field custom field
    */
   private void writeOutlineCode(Project.OutlineCodes.OutlineCode outlineCode, CustomField field)
   {
      //
      // Header details
      //
      CustomFieldLookupTable table = field.getLookupTable();
      outlineCode.setFieldID(String.valueOf(FieldTypeHelper.getFieldID(field.getFieldType())));
      outlineCode.setGuid(table.getGUID());
      outlineCode.setEnterprise(Boolean.valueOf(table.getEnterprise()));
      outlineCode.setShowIndent(Boolean.valueOf(table.getShowIndent()));
      outlineCode.setResourceSubstitutionEnabled(Boolean.valueOf(table.getResourceSubstitutionEnabled()));
      outlineCode.setLeafOnly(Boolean.valueOf(table.getLeafOnly()));
      outlineCode.setAllLevelsRequired(Boolean.valueOf(table.getAllLevelsRequired()));
      outlineCode.setOnlyTableValuesAllowed(Boolean.valueOf(table.getOnlyTableValuesAllowed()));

      //
      // Masks
      //
      outlineCode.setMasks(m_factory.createProjectOutlineCodesOutlineCodeMasks());
      if (field.getMasks().isEmpty())
      {
         CustomFieldValueDataType type = table.get(0).getType();
         if (type == null)
         {
            type = CustomFieldValueDataType.TEXT;
         }
         CustomFieldValueMask item = new CustomFieldValueMask(0, 1, ".", type);
         writeMask(outlineCode, item);
      }
      else
      {
         for (CustomFieldValueMask item : field.getMasks())
         {
            writeMask(outlineCode, item);
         }
      }

      //
      // Values
      //
      Project.OutlineCodes.OutlineCode.Values values = m_factory.createProjectOutlineCodesOutlineCodeValues();
      outlineCode.setValues(values);

      for (CustomFieldValueItem item : table)
      {
         Project.OutlineCodes.OutlineCode.Values.Value value = m_factory.createProjectOutlineCodesOutlineCodeValuesValue();
         values.getValue().add(value);
         writeOutlineCodeValue(value, item);
      }
   }

   /**
    * Write an outline code value.
    *
    * @param value parent node
    * @param item custom field item
    */
   private void writeOutlineCodeValue(Project.OutlineCodes.OutlineCode.Values.Value value, CustomFieldValueItem item)
   {
      CustomFieldValueDataType type = item.getType();
      if (type == null)
      {
         type = CustomFieldValueDataType.TEXT;
      }
      value.setDescription(item.getDescription());
      value.setFieldGUID(item.getGUID());
      value.setIsCollapsed(Boolean.valueOf(item.getCollapsed()));
      value.setParentValueID(NumberHelper.getBigInteger(item.getParentUniqueID()));
      value.setType(BigInteger.valueOf(type.getValue()));
      value.setValueID(NumberHelper.getBigInteger(item.getUniqueID()));
      value.setValue(DatatypeConverter.printOutlineCodeValue(item.getValue(), type.getDataType()));
   }

   /**
    * Write an outline code mask element.
    *
    * @param outlineCode parent node
    * @param item mask element
    */
   private void writeMask(Project.OutlineCodes.OutlineCode outlineCode, CustomFieldValueMask item)
   {
      Project.OutlineCodes.OutlineCode.Masks.Mask mask = m_factory.createProjectOutlineCodesOutlineCodeMasksMask();
      outlineCode.getMasks().getMask().add(mask);

      mask.setLength(BigInteger.valueOf(item.getLength()));
      mask.setLevel(BigInteger.valueOf(item.getLevel()));
      mask.setSeparator(item.getSeparator());
      mask.setType(BigInteger.valueOf(item.getType().getMaskValue()));
   }

   /**
    * This method writes calendar data to an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeCalendars(Project project)
   {
      //
      // Create the new MSPDI calendar list
      //
      Project.Calendars calendars = m_factory.createProjectCalendars();
      project.setCalendars(calendars);
      List<Project.Calendars.Calendar> calendar = calendars.getCalendar();

      //
      // Identify valid derived calendars, in theory all other calendars should be base calendars
      //
      Map<Integer, List<Resource>> resourceCalendarMap = m_projectFile.getResources().stream().filter(r -> r.getCalendarUniqueID() != null).collect(Collectors.groupingBy(Resource::getCalendarUniqueID));
      Set<ProjectCalendar> derivedCalendarSet = m_projectFile.getResources().stream().map(Resource::getCalendar).filter(c -> isValidDerivedCalendar(resourceCalendarMap, c)).collect(Collectors.toSet());
      List<ProjectCalendar> baseCalendars = m_projectFile.getCalendars().stream().filter(c -> !derivedCalendarSet.contains(c)).collect(Collectors.toList());

      //
      // Create temporary flattened base calendars, derived resource calendars
      //
      baseCalendars = baseCalendars.stream().map(ProjectCalendarHelper::createTemporaryFlattenedCalendar).collect(Collectors.toList());
      baseCalendars.forEach(c -> c.getResources().forEach(r -> derivedCalendarSet.add(createTemporaryDerivedCalendar(c, r))));

      //
      // Write the calendars, base calendars first, derived calendars second, sorted by unique ID.
      //
      baseCalendars.sort(Comparator.comparing(c -> m_calendarMapper.getUniqueID(c)));
      List<ProjectCalendar> derivedCalendars = new ArrayList<>(derivedCalendarSet);
      derivedCalendars.sort(Comparator.comparing(c -> m_calendarMapper.getUniqueID(c)));
      String baselineCalendarName = m_projectFile.getProjectProperties().getBaselineCalendarName() == null ? "" : m_projectFile.getProjectProperties().getBaselineCalendarName();

      baseCalendars.stream().map(c -> writeCalendar(c, true, baselineCalendarName.equals(c.getName()))).forEach(calendar::add);
      derivedCalendars.stream().map(c -> writeCalendar(c, false, baselineCalendarName.equals(c.getName()))).forEach(calendar::add);
   }

   /**
    * Create a temporary derived calendar to ensure that we can write the expected structure
    * for a resource calendar to the MSPDI file.
    *
    * @param baseCalendar calendar to derive from
    * @param resource link the new calendar to this resource
    * @return derived calendar
    */
   private ProjectCalendar createTemporaryDerivedCalendar(ProjectCalendar baseCalendar, Resource resource)
   {
      ProjectCalendar derivedCalendar = ProjectCalendarHelper.createTemporaryDerivedCalendar(baseCalendar, resource);
      m_resouceCalendarMap.put(m_resourceMapper.getUniqueID(resource), m_calendarMapper.getUniqueID(derivedCalendar));
      return derivedCalendar;
   }

   /**
    * Determine if this is a valid derived calendar.
    *
    * @param resourceCalendarMap map of resources using each calendar
    * @param calendar calendar to test
    * @return true if this is a valid resource calendar
    */
   private boolean isValidDerivedCalendar(Map<Integer, List<Resource>> resourceCalendarMap, ProjectCalendar calendar)
   {
      // We treat this as a valid derived (resource) calendar if:
      // 1. It is a derived calendar
      // 2. It's not the base calendar for any other derived calendars
      // 3. It is associated with exactly one resource
      return calendar != null && calendar.isDerived() && calendar.getDerivedCalendars().isEmpty() && resourceCalendarMap.computeIfAbsent(m_calendarMapper.getUniqueID(calendar), k -> Collections.emptyList()).size() == 1;
   }

   /**
    * This method writes data for a single calendar to an MSPDI file.
    *
    * @param mpxjCalendar MPXJ calendar data
    * @param isBaseCalendar true if we're writing a base calendar
    * @param isBaselineCalendar true if we're writing the baseline calendar
    * @return New MSPDI calendar instance
    */
   private Project.Calendars.Calendar writeCalendar(ProjectCalendar mpxjCalendar, boolean isBaseCalendar, boolean isBaselineCalendar)
   {
      //
      // Create a calendar
      //
      Project.Calendars.Calendar calendar = m_factory.createProjectCalendarsCalendar();
      calendar.setUID(NumberHelper.getBigInteger(m_calendarMapper.getUniqueID(mpxjCalendar)));
      calendar.setGUID(mpxjCalendar.getGUID());
      calendar.setIsBaseCalendar(Boolean.valueOf(isBaseCalendar));
      calendar.setIsBaselineCalendar(Boolean.valueOf(isBaselineCalendar));

      ProjectCalendar base = mpxjCalendar.getParent();
      // SF-329: null default required to keep Powerproject happy when importing MSPDI files
      calendar.setBaseCalendarUID(base == null ? NULL_CALENDAR_ID : NumberHelper.getBigInteger(m_calendarMapper.getUniqueID(base)));
      calendar.setName(normalizeCalendarName(mpxjCalendar));

      //
      // Create a list of normal days
      //
      Project.Calendars.Calendar.WeekDays days = m_factory.createProjectCalendarsCalendarWeekDays();
      List<Project.Calendars.Calendar.WeekDays.WeekDay> dayList = days.getWeekDay();
      for (DayOfWeek mpxjDay : DayOfWeekHelper.ORDERED_DAYS)
      {
         writeDay(mpxjCalendar, mpxjDay, dayList);
      }

      //
      // Create a list of exceptions
      //
      writeExceptions(mpxjCalendar, calendar, dayList);

      //
      // Do not add a weekdays tag to the calendar unless it has valid entries.
      // Fixes SourceForge bug 1854747: MPXJ and MSP 2007 XML formats
      //
      if (!dayList.isEmpty())
      {
         calendar.setWeekDays(days);
      }

      writeWorkWeeks(calendar, mpxjCalendar);

      m_eventManager.fireCalendarWrittenEvent(mpxjCalendar);

      return calendar;
   }

   /**
    * Strip control characters, and ensure that the calendar name is no longer than 51 characters.
    *
    * @param calendar calendar
    * @return calendar name
    */
   private String normalizeCalendarName(ProjectCalendarDays calendar)
   {
      String name = calendar.getName();
      if (name == null || name.isEmpty())
      {
         return name;
      }

      name = StringHelper.stripControlCharacters(name);
      if (name.length() > 51)
      {
         name = name.substring(0, 51);
      }

      return name;
   }

   /**
    * Write details for a single day.
    *
    * @param mpxjCalendar parent calendar
    * @param mpxjDay day to write
    * @param dayList MSPDI day list
    */
   private void writeDay(ProjectCalendar mpxjCalendar, DayOfWeek mpxjDay, List<Project.Calendars.Calendar.WeekDays.WeekDay> dayList)
   {
      DayType workingFlag = mpxjCalendar.getCalendarDayType(mpxjDay);

      if (workingFlag != DayType.DEFAULT)
      {
         Project.Calendars.Calendar.WeekDays.WeekDay day = m_factory.createProjectCalendarsCalendarWeekDaysWeekDay();
         dayList.add(day);
         day.setDayType(BigInteger.valueOf(DayOfWeekHelper.getValue(mpxjDay)));
         day.setDayWorking(Boolean.valueOf(workingFlag == DayType.WORKING));

         if (workingFlag == DayType.WORKING)
         {
            Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes times = m_factory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimes();
            day.setWorkingTimes(times);
            List<Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime> timesList = times.getWorkingTime();

            ProjectCalendarHours bch = mpxjCalendar.getCalendarHours(mpxjDay);
            if (bch != null)
            {
               for (LocalTimeRange range : bch)
               {
                  if (range != null)
                  {
                     Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime time = m_factory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimesWorkingTime();
                     timesList.add(time);

                     time.setFromTime(range.getStart());
                     time.setToTime(range.getEnd());
                  }
               }
            }
         }
      }
   }

   /**
    * Main entry point used to determine the format used to write
    * calendar exceptions.
    *
    * @param mpxjCalendar MPXJ calendar data
    * @param calendar parent calendar
    * @param dayList list of calendar days
    */
   private void writeExceptions(ProjectCalendar mpxjCalendar, Project.Calendars.Calendar calendar, List<Project.Calendars.Calendar.WeekDays.WeekDay> dayList)
   {
      // Always write legacy exception data:
      // Powerproject appears not to recognise new format data at all,
      // and legacy data is ignored in preference to new data post MSP 2003
      writeExceptions9(mpxjCalendar, dayList);

      if (m_saveVersion.getValue() > SaveVersion.Project2003.getValue())
      {
         writeExceptions12(mpxjCalendar, calendar);
      }
   }

   /**
    * Write exceptions in the format used by MSPDI files prior to Project 2007.
    *
    * @param mpxjCalendar MPXJ calendar data
    * @param dayList list of calendar days
    */
   private void writeExceptions9(ProjectCalendar mpxjCalendar, List<Project.Calendars.Calendar.WeekDays.WeekDay> dayList)
   {
      List<ProjectCalendarException> exceptions = mpxjCalendar.getExpandedCalendarExceptionsWithWorkWeeks();

      // Exceptions in an MSPDI file need to be sorted, or they are ignored.
      // Expanded exceptions from a calendar are sorted by default.
      for (ProjectCalendarException exception : exceptions)
      {
         boolean working = exception.getWorking();

         Project.Calendars.Calendar.WeekDays.WeekDay day = m_factory.createProjectCalendarsCalendarWeekDaysWeekDay();
         dayList.add(day);
         day.setDayType(BIGINTEGER_ZERO);
         day.setDayWorking(Boolean.valueOf(working));

         Project.Calendars.Calendar.WeekDays.WeekDay.TimePeriod period = m_factory.createProjectCalendarsCalendarWeekDaysWeekDayTimePeriod();
         day.setTimePeriod(period);
         period.setFromDate(exception.getFromDate().atStartOfDay());
         period.setToDate(LocalDateHelper.getDayEndDate(exception.getToDate()));

         if (working)
         {
            Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes times = m_factory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimes();
            day.setWorkingTimes(times);
            List<Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime> timesList = times.getWorkingTime();

            for (LocalTimeRange range : exception)
            {
               Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime time = m_factory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimesWorkingTime();
               timesList.add(time);

               time.setFromTime(range.getStart());
               time.setToTime(range.getEnd());
            }
         }
      }
   }

   /**
    * Write exceptions into the format used by MSPDI files from
    * Project 2007 onwards.
    *
    * @param mpxjCalendar MPXJ calendar data
    * @param calendar parent calendar
    */
   private void writeExceptions12(ProjectCalendar mpxjCalendar, Project.Calendars.Calendar calendar)
   {
      // Exceptions in an MSPDI file need to be sorted, or they are ignored.
      List<ProjectCalendarException> exceptions = new ArrayList<>(mpxjCalendar.getCalendarExceptions());
      if (exceptions.isEmpty())
      {
         return;
      }
      Collections.sort(exceptions);

      Exceptions ce = m_factory.createProjectCalendarsCalendarExceptions();
      calendar.setExceptions(ce);
      List<Exceptions.Exception> el = ce.getException();

      for (ProjectCalendarException exception : exceptions)
      {
         Exceptions.Exception ex = m_factory.createProjectCalendarsCalendarExceptionsException();
         el.add(ex);

         ex.setName(StringHelper.stripControlCharacters(exception.getName()));
         boolean working = exception.getWorking();
         ex.setDayWorking(Boolean.valueOf(working));

         if (exception.getRecurring() == null)
         {
            ex.setEnteredByOccurrences(Boolean.FALSE);
            ex.setOccurrences(BigInteger.ONE);
            ex.setType(BigInteger.ONE);
         }
         else
         {
            populateRecurringException(exception, ex);
         }

         Project.Calendars.Calendar.Exceptions.Exception.TimePeriod period = m_factory.createProjectCalendarsCalendarExceptionsExceptionTimePeriod();
         ex.setTimePeriod(period);
         period.setFromDate(exception.getFromDate().atStartOfDay());
         period.setToDate(LocalDateHelper.getDayEndDate(exception.getToDate()));

         if (working)
         {
            Project.Calendars.Calendar.Exceptions.Exception.WorkingTimes times = m_factory.createProjectCalendarsCalendarExceptionsExceptionWorkingTimes();
            ex.setWorkingTimes(times);
            List<Project.Calendars.Calendar.Exceptions.Exception.WorkingTimes.WorkingTime> timesList = times.getWorkingTime();

            for (LocalTimeRange range : exception)
            {
               Project.Calendars.Calendar.Exceptions.Exception.WorkingTimes.WorkingTime time = m_factory.createProjectCalendarsCalendarExceptionsExceptionWorkingTimesWorkingTime();
               timesList.add(time);

               time.setFromTime(range.getStart());
               time.setToTime(range.getEnd());
            }
         }
      }
   }

   /**
    * Writes the details of a recurring exception.
    *
    * @param mpxjException source MPXJ calendar exception
    * @param xmlException target MSPDI exception
    */
   private void populateRecurringException(ProjectCalendarException mpxjException, Exceptions.Exception xmlException)
   {
      RecurringData data = mpxjException.getRecurring();
      xmlException.setEnteredByOccurrences(Boolean.TRUE);
      xmlException.setOccurrences(NumberHelper.getBigInteger(data.getOccurrences()));

      switch (data.getRecurrenceType())
      {
         case DAILY:
         {
            xmlException.setType(BigInteger.valueOf(7));
            xmlException.setPeriod(NumberHelper.getBigInteger(data.getFrequency()));
            break;
         }

         case WEEKLY:
         {
            xmlException.setType(BigInteger.valueOf(6));
            xmlException.setPeriod(NumberHelper.getBigInteger(data.getFrequency()));
            xmlException.setDaysOfWeek(getDaysOfTheWeek(data));
            break;
         }

         case MONTHLY:
         {
            xmlException.setPeriod(NumberHelper.getBigInteger(data.getFrequency()));
            if (data.getRelative())
            {
               xmlException.setType(BigInteger.valueOf(5));
               xmlException.setMonthItem(BigInteger.valueOf(DayOfWeekHelper.getValue(data.getDayOfWeek()) + 2));
               xmlException.setMonthPosition(BigInteger.valueOf(NumberHelper.getInt(data.getDayNumber()) - 1));
            }
            else
            {
               xmlException.setType(BigInteger.valueOf(4));
               xmlException.setMonthDay(NumberHelper.getBigInteger(data.getDayNumber()));
            }
            break;
         }

         case YEARLY:
         {
            xmlException.setMonth(BigInteger.valueOf(NumberHelper.getInt(data.getMonthNumber()) - 1));
            if (data.getRelative())
            {
               xmlException.setType(BigInteger.valueOf(3));
               xmlException.setMonthItem(BigInteger.valueOf(DayOfWeekHelper.getValue(data.getDayOfWeek()) + 2));
               xmlException.setMonthPosition(BigInteger.valueOf(NumberHelper.getInt(data.getDayNumber()) - 1));
            }
            else
            {
               xmlException.setType(BigInteger.valueOf(2));
               xmlException.setMonthDay(NumberHelper.getBigInteger(data.getDayNumber()));
            }
         }
      }
   }

   /**
    * Converts days of the week into a bit field.
    *
    * @param data recurring data
    * @return bit field
    */
   private BigInteger getDaysOfTheWeek(RecurringData data)
   {
      int value = 0;
      for (DayOfWeek day : DayOfWeek.values())
      {
         if (data.getWeeklyDay(day))
         {
            value = value | DAY_MASKS[DayOfWeekHelper.getValue(day)];
         }
      }
      return BigInteger.valueOf(value);
   }

   /**
    * Write the work weeks associated with this calendar.
    *
    * @param xmlCalendar XML calendar instance
    * @param mpxjCalendar MPXJ calendar instance
    */
   private void writeWorkWeeks(Project.Calendars.Calendar xmlCalendar, ProjectCalendar mpxjCalendar)
   {
      List<ProjectCalendarWeek> weeks = mpxjCalendar.getWorkWeeks();
      if (!weeks.isEmpty())
      {
         WorkWeeks xmlWorkWeeks = m_factory.createProjectCalendarsCalendarWorkWeeks();
         xmlCalendar.setWorkWeeks(xmlWorkWeeks);
         List<WorkWeek> xmlWorkWeekList = xmlWorkWeeks.getWorkWeek();

         for (ProjectCalendarWeek week : weeks)
         {
            WorkWeek xmlWeek = m_factory.createProjectCalendarsCalendarWorkWeeksWorkWeek();
            xmlWorkWeekList.add(xmlWeek);

            xmlWeek.setName(normalizeCalendarName(week));
            TimePeriod xmlTimePeriod = m_factory.createProjectCalendarsCalendarWorkWeeksWorkWeekTimePeriod();
            xmlWeek.setTimePeriod(xmlTimePeriod);
            xmlTimePeriod.setFromDate(week.getDateRange().getStart().atStartOfDay());
            xmlTimePeriod.setToDate(LocalDateHelper.getDayEndDate(week.getDateRange().getEnd()));

            WeekDays xmlWeekDays = m_factory.createProjectCalendarsCalendarWorkWeeksWorkWeekWeekDays();
            xmlWeek.setWeekDays(xmlWeekDays);

            List<Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay> dayList = xmlWeekDays.getWeekDay();

            for (int loop = 1; loop < 8; loop++)
            {
               DayType workingFlag = week.getCalendarDayType(DayOfWeekHelper.getInstance(loop));

               if (workingFlag != DayType.DEFAULT)
               {
                  Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay day = m_factory.createProjectCalendarsCalendarWorkWeeksWorkWeekWeekDaysWeekDay();
                  dayList.add(day);
                  day.setDayType(BigInteger.valueOf(loop));
                  day.setDayWorking(Boolean.valueOf(workingFlag == DayType.WORKING));

                  if (workingFlag == DayType.WORKING)
                  {
                     Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay.WorkingTimes times = m_factory.createProjectCalendarsCalendarWorkWeeksWorkWeekWeekDaysWeekDayWorkingTimes();
                     day.setWorkingTimes(times);
                     List<Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay.WorkingTimes.WorkingTime> timesList = times.getWorkingTime();

                     ProjectCalendarHours bch = week.getCalendarHours(DayOfWeekHelper.getInstance(loop));
                     if (bch != null)
                     {
                        for (LocalTimeRange range : bch)
                        {
                           if (range != null)
                           {
                              Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay.WorkingTimes.WorkingTime time = m_factory.createProjectCalendarsCalendarWorkWeeksWorkWeekWeekDaysWeekDayWorkingTimesWorkingTime();
                              timesList.add(time);

                              time.setFromTime(range.getStart());
                              time.setToTime(range.getEnd());
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   /**
    * This method writes resource data to an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeResources(Project project)
   {
      Project.Resources resources = m_factory.createProjectResources();
      project.setResources(resources);
      List<Project.Resources.Resource> list = resources.getResource();

      for (Resource resource : m_projectFile.getResources())
      {
         list.add(writeResource(resource));
      }
   }

   /**
    * This method writes data for a single resource to an MSPDI file.
    *
    * @param mpx Resource data
    * @return New MSPDI resource instance
    */
   private Project.Resources.Resource writeResource(Resource mpx)
   {
      Project.Resources.Resource xml = m_factory.createProjectResourcesResource();
      ProjectCalendar cal = mpx.getCalendar();
      if (cal != null)
      {
         // If we've created a temporary derived calendar for this resource
         // ensure that we use the correct calendar ID.
         Integer calendarUniqueID = m_resouceCalendarMap.get(m_resourceMapper.getUniqueID(mpx));
         xml.setCalendarUID(NumberHelper.getBigInteger(calendarUniqueID == null ? m_calendarMapper.getUniqueID(cal) : calendarUniqueID));
      }

      xml.setAccrueAt(mpx.getAccrueAt());
      xml.setActiveDirectoryGUID(mpx.getActiveDirectoryGUID());
      xml.setActualCost(DatatypeConverter.printCurrency(mpx.getActualCost()));
      xml.setActualOvertimeCost(DatatypeConverter.printCurrency(mpx.getActualOvertimeCost()));
      xml.setActualOvertimeWork(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWork()));
      xml.setActualOvertimeWorkProtected(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWorkProtected()));
      xml.setActualWork(DatatypeConverter.printDuration(this, mpx.getActualWork()));
      xml.setActualWorkProtected(DatatypeConverter.printDuration(this, mpx.getActualWorkProtected()));
      xml.setACWP(DatatypeConverter.printCurrency(mpx.getACWP()));
      xml.setAvailableFrom(mpx.getAvailableFrom());
      xml.setAvailableTo(mpx.getAvailableTo());
      xml.setBCWS(DatatypeConverter.printCurrency(mpx.getBCWS()));
      xml.setBCWP(DatatypeConverter.printCurrency(mpx.getBCWP()));
      xml.setBookingType(mpx.getBookingType());
      xml.setIsBudget(Boolean.valueOf(mpx.getBudget()));
      xml.setCanLevel(Boolean.valueOf(mpx.getCanLevel()));
      xml.setCode(mpx.getCode());
      xml.setCost(DatatypeConverter.printCurrency(mpx.getCost()));
      xml.setCostCenter(mpx.getCostCenter());
      xml.setCostPerUse(DatatypeConverter.printCurrency(mpx.getCostPerUse()));
      xml.setCostVariance(DatatypeConverter.printCurrency(mpx.getCostVariance()));
      xml.setCreationDate(mpx.getCreationDate());
      xml.setCV(DatatypeConverter.printCurrency(mpx.getCV()));
      xml.setEmailAddress(mpx.getEmailAddress());
      xml.setFinish(mpx.getFinish());
      xml.setGroup(mpx.getGroup());
      xml.setGUID(mpx.getGUID());
      xml.setHyperlink(mpx.getHyperlink());
      xml.setHyperlinkAddress(mpx.getHyperlinkAddress());
      xml.setHyperlinkSubAddress(mpx.getHyperlinkSubAddress());
      xml.setID(NumberHelper.getBigInteger(mpx.getID()));
      xml.setInitials(mpx.getInitials());
      xml.setIsEnterprise(Boolean.valueOf(mpx.getEnterprise()));
      xml.setIsGeneric(Boolean.valueOf(mpx.getGeneric()));
      xml.setIsInactive(Boolean.valueOf(!mpx.getActive()));
      xml.setIsNull(Boolean.valueOf(mpx.getNull()));
      xml.setMaterialLabel(formatMaterialLabel(mpx));
      xml.setMaxUnits(DatatypeConverter.printUnits(mpx.getMaxUnits()));
      xml.setName(normalizeResourceName(mpx));
      xml.setNotes(nullIfEmpty(mpx.getNotes()));
      xml.setNTAccount(mpx.getNtAccount());
      xml.setOverAllocated(Boolean.valueOf(mpx.getOverAllocated()));
      xml.setOvertimeCost(DatatypeConverter.printCurrency(mpx.getOvertimeCost()));
      xml.setOvertimeRate(DatatypeConverter.printRate(mpx.getOvertimeRate()));
      xml.setOvertimeRateFormat(DatatypeConverter.printOvertimeRateFormat(mpx, mpx.getOvertimeRate()));
      xml.setOvertimeWork(DatatypeConverter.printDuration(this, mpx.getOvertimeWork()));
      xml.setPeakUnits(DatatypeConverter.printUnits(mpx.getPeakUnits()));
      xml.setPercentWorkComplete(mpx.getPercentWorkComplete());
      xml.setPhonetics(mpx.getPhonetics());
      xml.setRegularWork(DatatypeConverter.printDuration(this, mpx.getRegularWork()));
      xml.setRemainingCost(DatatypeConverter.printCurrency(mpx.getRemainingCost()));
      xml.setRemainingOvertimeCost(DatatypeConverter.printCurrency(mpx.getRemainingOvertimeCost()));
      xml.setRemainingOvertimeWork(DatatypeConverter.printDuration(this, mpx.getRemainingOvertimeWork()));
      xml.setRemainingWork(DatatypeConverter.printDuration(this, mpx.getRemainingWork()));
      xml.setStandardRate(DatatypeConverter.printRate(mpx.getStandardRate()));
      xml.setStandardRateFormat(DatatypeConverter.printStandardRateFormat(mpx, mpx.getStandardRate()));
      xml.setStart(mpx.getStart());
      xml.setSV(DatatypeConverter.printCurrency(mpx.getSV()));
      xml.setUID(m_resourceMapper.getUniqueID(mpx));
      xml.setWork(DatatypeConverter.printDuration(this, mpx.getWork()));
      xml.setWorkGroup(mpx.getWorkGroup());
      xml.setWorkVariance(DatatypeConverter.printDurationInDecimalThousandthsOfMinutes(mpx.getWorkVariance()));

      if (mpx.getType() == ResourceType.COST)
      {
         xml.setType(ResourceType.MATERIAL);
         xml.setIsCostResource(Boolean.TRUE);
      }
      else
      {
         xml.setType(mpx.getType());
      }

      writeResourceExtendedAttributes(xml, mpx);
      writeResourceOutlineCodes(xml, mpx);

      writeResourceBaselines(xml, mpx);

      writeCostRateTables(xml, mpx);

      writeAvailability(xml, mpx);

      return (xml);
   }

   /**
    * Strip control characters and ensure that the resource name does not contain , [ or ] characters.
    * Replace , with ; to match MS Project behaviour. Replace [ and ] with a space character.
    *
    * @param resource resource
    * @return resource name
    */
   private String normalizeResourceName(Resource resource)
   {
      String name = resource.getName();
      if (name == null || name.isEmpty())
      {
         return name;
      }

      name = StringHelper.stripControlCharacters(name);
      if (name.contains(","))
      {
         name = name.replace(',', ';');
      }

      if (name.contains("["))
      {
         name = name.replace('[', ' ');
      }

      if (name.contains("]"))
      {
         name = name.replace(']', ' ');
      }

      return name;
   }

   /**
    * Writes resource baseline data.
    *
    * @param xmlResource MSPDI resource
    * @param mpxjResource MPXJ resource
    */
   private void writeResourceBaselines(Project.Resources.Resource xmlResource, Resource mpxjResource)
   {
      Project.Resources.Resource.Baseline baseline = m_factory.createProjectResourcesResourceBaseline();
      boolean populated = false;

      Number cost = mpxjResource.getBaselineCost();
      if (cost != null && cost.intValue() != 0)
      {
         populated = true;
         baseline.setCost(DatatypeConverter.printCurrency(cost));
      }

      Duration work = mpxjResource.getBaselineWork();
      if (work != null && work.getDuration() != 0)
      {
         populated = true;
         baseline.setWork(DatatypeConverter.printDuration(this, work));
      }

      if (populated)
      {
         xmlResource.getBaseline().add(baseline);
         baseline.setNumber(BigInteger.ZERO);
      }

      for (int loop = 1; loop <= 10; loop++)
      {
         baseline = m_factory.createProjectResourcesResourceBaseline();
         populated = false;

         cost = mpxjResource.getBaselineCost(loop);
         if (cost != null && cost.intValue() != 0)
         {
            populated = true;
            baseline.setCost(DatatypeConverter.printCurrency(cost));
         }

         work = mpxjResource.getBaselineWork(loop);
         if (work != null && work.getDuration() != 0)
         {
            populated = true;
            baseline.setWork(DatatypeConverter.printDuration(this, work));
         }

         if (populated)
         {
            xmlResource.getBaseline().add(baseline);
            baseline.setNumber(BigInteger.valueOf(loop));
         }
      }
   }

   /**
    * This method writes extended attribute data for a resource.
    *
    * @param xml MSPDI resource
    * @param mpx MPXJ resource
    */
   private void writeResourceExtendedAttributes(Project.Resources.Resource xml, Resource mpx)
   {
      List<Project.Resources.Resource.ExtendedAttribute> extendedAttributes = xml.getExtendedAttribute();
      Set<FieldType> outlineCodes = new HashSet<>(Arrays.asList(ResourceFieldLists.CUSTOM_OUTLINE_CODE));
      m_extendedAttributes.stream().filter(f -> f.getFieldTypeClass() == FieldTypeClass.RESOURCE && !outlineCodes.contains(f)).forEach(f -> writeResourceExtendedAttribute(extendedAttributes, mpx, f));
   }

   private void writeResourceExtendedAttribute(List<Project.Resources.Resource.ExtendedAttribute> extendedAttributes, Resource mpx, FieldType mpxFieldID)
   {
      Object value = mpx.getCachedValue(mpxFieldID);

      if (FieldTypeHelper.valueIsNotDefault(mpxFieldID, value))
      {
         FieldType mappedFieldType = m_userDefinedFieldMap.getTarget(mpxFieldID);
         if (mappedFieldType instanceof ResourceField)
         {
            Project.Resources.Resource.ExtendedAttribute attrib = m_factory.createProjectResourcesResourceExtendedAttribute();
            extendedAttributes.add(attrib);
            attrib.setFieldID(Integer.toString(FieldTypeHelper.getFieldID(mappedFieldType)));
            attrib.setValue(DatatypeConverter.printCustomField(this, value, mappedFieldType.getDataType()));
            attrib.setDurationFormat(printCustomFieldDurationFormat(value));
            setValueGUID(attrib, mappedFieldType);
         }
      }
   }

   /**
    * Set the GUID of a value selected from a lookup table.
    *
    * @param attrib parent attribute
    * @param fieldType field type
    */
   private void setValueGUID(Project.Resources.Resource.ExtendedAttribute attrib, FieldType fieldType)
   {
      CustomFieldValueItem valueItem = getValueItem(fieldType, attrib.getValue());
      if (valueItem != null)
      {
         attrib.setValueGUID(valueItem.getGUID());
      }
   }

   /**
    * This method writes outline codes for a resource.
    *
    * @param xml MSPDI resource
    * @param mpx MPXJ resource
    */
   private void writeResourceOutlineCodes(Project.Resources.Resource xml, Resource mpx)
   {
      List<Project.Resources.Resource.OutlineCode> outlineCodes = xml.getOutlineCode();

      for (ResourceField mpxFieldID : ResourceFieldLists.CUSTOM_OUTLINE_CODE)
      {
         Object value = mpx.getCachedValue(mpxFieldID);

         if (FieldTypeHelper.valueIsNotDefault(mpxFieldID, value))
         {
            Project.Resources.Resource.OutlineCode attrib = m_factory.createProjectResourcesResourceOutlineCode();
            outlineCodes.add(attrib);
            attrib.setFieldID(Integer.toString(FieldTypeHelper.getFieldID(mpxFieldID)));
            setValueID(attrib, mpxFieldID, DatatypeConverter.printCustomField(this, value, mpxFieldID.getDataType()));
         }
      }
   }

   /**
    * Set the ID of a value selected from a lookup table.
    *
    * @param attrib parent attribute
    * @param fieldType field type
    * @param formattedValue formatted value
    */
   private void setValueID(Project.Resources.Resource.OutlineCode attrib, FieldType fieldType, String formattedValue)
   {
      CustomFieldValueItem valueItem = getValueItem(fieldType, formattedValue);
      if (valueItem != null)
      {
         attrib.setValueID(NumberHelper.getBigInteger(valueItem.getUniqueID()));
      }
   }

   /**
    * Writes a resource's cost rate tables.
    *
    * @param xml MSPDI resource
    * @param mpx MPXJ resource
    */
   private void writeCostRateTables(Project.Resources.Resource xml, Resource mpx)
   {
      List<Project.Resources.Resource.Rates.Rate> ratesList = null;

      for (int tableIndex = 0; tableIndex < CostRateTable.MAX_TABLES; tableIndex++)
      {
         CostRateTable table = mpx.getCostRateTable(tableIndex);
         if (costRateTableWriteRequired(tableIndex, mpx, table))
         {
            for (CostRateTableEntry entry : table)
            {
               if (costRateTableEntryWriteRequired(entry))
               {
                  if (ratesList == null)
                  {
                     Rates rates = m_factory.createProjectResourcesResourceRates();
                     xml.setRates(rates);
                     ratesList = rates.getRate();
                  }

                  Project.Resources.Resource.Rates.Rate rate = m_factory.createProjectResourcesResourceRatesRate();
                  ratesList.add(rate);

                  rate.setCostPerUse(DatatypeConverter.printCurrencyMandatory(entry.getCostPerUse()));
                  rate.setOvertimeRate(DatatypeConverter.printRateMandatory(entry.getOvertimeRate()));
                  rate.setOvertimeRateFormat(DatatypeConverter.printTimeUnit(entry.getOvertimeRate()));
                  rate.setRatesFrom(entry.getStartDate());
                  rate.setRatesTo(entry.getEndDate());
                  rate.setRateTable(BigInteger.valueOf(tableIndex));
                  rate.setStandardRate(DatatypeConverter.printRateMandatory(entry.getStandardRate()));
                  rate.setStandardRateFormat(DatatypeConverter.printTimeUnit(entry.getStandardRate()));
               }
            }
         }
      }
   }

   /**
    * This method determines whether the cost rate table entry should be written.
    * A default cost rate table should not be written to the file.
    *
    * @param entry cost rate table entry
    * @return boolean flag
    */
   private boolean costRateTableEntryWriteRequired(CostRateTableEntry entry)
   {
      boolean fromDate = (LocalDateTimeHelper.compare(entry.getStartDate(), LocalDateTimeHelper.START_DATE_NA) > 0);
      boolean toDate = (LocalDateTimeHelper.compare(entry.getEndDate(), LocalDateTimeHelper.END_DATE_NA) > 0);
      boolean costPerUse = (NumberHelper.getDouble(entry.getCostPerUse()) != 0);
      boolean overtimeRate = (entry.getOvertimeRate() != null && entry.getOvertimeRate().getAmount() != 0);
      boolean standardRate = (entry.getStandardRate() != null && entry.getStandardRate().getAmount() != 0);
      return (fromDate || toDate || costPerUse || overtimeRate || standardRate);
   }

   /**
    * Determine if the cost rate table should be written.
    *
    * @param index table index
    * @param resource parent resource
    * @param table table data
    * @return true if the table should be written
    */
   private boolean costRateTableWriteRequired(int index, Resource resource, CostRateTable table)
   {
      // Don't write anything if we don't have a table
      if (table.isEmpty())
      {
         return false;
      }

      // Always write if it's not the default table, or if we have more than one entry
      if (index != 0 || table.size() > 1)
      {
         return true;
      }

      // Don't write if we're the default table and the rate attributes on the resource match what we have here
      CostRateTableEntry entry = table.get(0);
      return !Rate.equals(entry.getStandardRate(), resource.getStandardRate()) || !Rate.equals(entry.getOvertimeRate(), resource.getOvertimeRate()) || !NumberHelper.equals(entry.getCostPerUse(), resource.getCostPerUse());
   }

   /**
    * This method writes a resource's availability table.
    *
    * @param xml MSPDI resource
    * @param mpx MPXJ resource
    */
   private void writeAvailability(Project.Resources.Resource xml, Resource mpx)
   {
      AvailabilityTable table = mpx.getAvailability();
      if (table.hasDefaultDateRange())
      {
         return;
      }

      AvailabilityPeriods periods = m_factory.createProjectResourcesResourceAvailabilityPeriods();
      xml.setAvailabilityPeriods(periods);

      List<AvailabilityPeriod> list = periods.getAvailabilityPeriod();
      for (Availability availability : table)
      {
         AvailabilityPeriod period = m_factory.createProjectResourcesResourceAvailabilityPeriodsAvailabilityPeriod();
         list.add(period);
         LocalDateTimeRange range = availability.getRange();

         period.setAvailableFrom(range.getStart());
         period.setAvailableTo(range.getEnd());
         period.setAvailableUnits(DatatypeConverter.printUnits(availability.getUnits()));
      }
   }

   /**
    * This method writes task data to an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeTasks(Project project)
   {
      Project.Tasks tasks = m_factory.createProjectTasks();
      project.setTasks(tasks);
      List<Project.Tasks.Task> list = tasks.getTask();

      int taskIdOffset = 0;
      for (Task task : m_projectFile.getTasks().stream().sorted(Comparator.comparing(Task::getID)).collect(Collectors.toList()))
      {
         if (task.getExternalTask())
         {
            taskIdOffset++;
         }
         else
         {
            list.add(writeTask(taskIdOffset, task));
         }
      }
   }

   /**
    * This method writes data for a single task to an MSPDI file.
    *
    * @param mpx Task data
    * @param taskIdOffset offset applied to the task ID
    * @return new task instance
    */
   private Project.Tasks.Task writeTask(int taskIdOffset, Task mpx)
   {
      Project.Tasks.Task xml = m_factory.createProjectTasksTask();
      int taskID = mpx.getID().intValue() - taskIdOffset;

      xml.setActive(Boolean.valueOf(mpx.getActive()));
      xml.setActualCost(DatatypeConverter.printCurrency(mpx.getActualCost()));
      xml.setActualDuration(DatatypeConverter.printDuration(this, mpx.getActualDuration()));
      xml.setActualFinish(mpx.getActualFinish());
      xml.setActualOvertimeCost(DatatypeConverter.printCurrency(mpx.getActualOvertimeCost()));
      xml.setActualOvertimeWork(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWork()));
      xml.setActualOvertimeWorkProtected(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWorkProtected()));
      xml.setActualStart(mpx.getActualStart());
      xml.setActualWork(DatatypeConverter.printDuration(this, mpx.getActualWork()));
      xml.setActualWorkProtected(DatatypeConverter.printDuration(this, mpx.getActualWorkProtected()));
      xml.setACWP(DatatypeConverter.printCurrency(mpx.getACWP()));
      xml.setBCWP(DatatypeConverter.printCurrency(mpx.getBCWP()));
      xml.setBCWS(DatatypeConverter.printCurrency(mpx.getBCWS()));
      xml.setCalendarUID(getTaskCalendarID(mpx));
      xml.setConstraintDate(mpx.getConstraintDate());
      xml.setConstraintType(DatatypeConverter.printConstraintType(mpx.getConstraintType()));
      xml.setContact(mpx.getContact());
      xml.setCost(DatatypeConverter.printCurrency(mpx.getCost()));
      xml.setCreateDate(mpx.getCreateDate());
      xml.setCritical(Boolean.valueOf(mpx.getCritical()));
      xml.setCV(DatatypeConverter.printCurrency(mpx.getCV()));
      xml.setDeadline(mpx.getDeadline());
      xml.setDuration(DatatypeConverter.printDurationMandatory(this, mpx.getDuration()));
      xml.setDurationText(mpx.getDurationText());
      xml.setDurationFormat(DatatypeConverter.printDurationTimeUnits(mpx.getDuration(), mpx.getEstimated()));
      xml.setEarlyFinish(mpx.getEarlyFinish());
      xml.setEarlyStart(mpx.getEarlyStart());
      xml.setEarnedValueMethod(DatatypeConverter.printEarnedValueMethod(mpx.getEarnedValueMethod()));
      xml.setEffortDriven(Boolean.valueOf(mpx.getEffortDriven()));
      xml.setEstimated(Boolean.valueOf(mpx.getEstimated()));
      xml.setExternalTask(Boolean.valueOf(mpx.getExternalTask()));
      xml.setExternalTaskProject(mpx.getProject());
      xml.setFinish(mpx.getFinish());
      xml.setFinishSlack(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getFinishSlack()));
      xml.setFinishText(mpx.getFinishText());
      xml.setFinishVariance(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getFinishVariance()));
      xml.setFixedCost(DatatypeConverter.printCurrency(mpx.getFixedCost()));

      AccrueType fixedCostAccrual = mpx.getFixedCostAccrual();
      if (fixedCostAccrual == null)
      {
         fixedCostAccrual = AccrueType.PRORATED;
      }
      xml.setFixedCostAccrual(fixedCostAccrual);
      xml.setFreeSlack(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getFreeSlack()));
      xml.setGUID(mpx.getGUID());
      xml.setHideBar(Boolean.valueOf(mpx.getHideBar()));
      xml.setIsNull(Boolean.valueOf(mpx.getNull()));
      xml.setIsSubproject(Boolean.valueOf(mpx.getExternalProject()));
      xml.setIsSubprojectReadOnly(Boolean.valueOf(mpx.getSubprojectReadOnly()));
      xml.setHyperlink(mpx.getHyperlink());
      xml.setHyperlinkAddress(mpx.getHyperlinkAddress());
      xml.setHyperlinkSubAddress(mpx.getHyperlinkSubAddress());
      xml.setID(BigInteger.valueOf(taskID));
      xml.setIgnoreResourceCalendar(Boolean.valueOf(mpx.getIgnoreResourceCalendar()));
      xml.setLateFinish(mpx.getLateFinish());
      xml.setLateStart(mpx.getLateStart());
      xml.setLevelAssignments(Boolean.valueOf(mpx.getLevelAssignments()));
      xml.setLevelingCanSplit(Boolean.valueOf(mpx.getLevelingCanSplit()));

      if (mpx.getLevelingDelay() == null)
      {
         if (mpx.getLevelingDelayFormat() != null)
         {
            // We don't have a leveling delay, but we do have a format specified, so preserve that.
            xml.setLevelingDelayFormat(DatatypeConverter.printDurationTimeUnits(mpx.getLevelingDelayFormat(), false));
         }
      }
      else
      {
         Duration levelingDelay = mpx.getLevelingDelay();
         double tenthMinutes = 10.0 * Duration.convertUnits(levelingDelay.getDuration(), levelingDelay.getUnits(), TimeUnit.MINUTES, m_projectFile.getProjectProperties()).getDuration();
         xml.setLevelingDelay(BigInteger.valueOf((long) tenthMinutes));
         // We're assuming that the caller has configured the leveling delay with the correct units,
         // so we're not using the leveling delay format attribute of the task.
         xml.setLevelingDelayFormat(DatatypeConverter.printDurationTimeUnits(levelingDelay, false));
      }

      xml.setManual(Boolean.valueOf(mpx.getTaskMode() == TaskMode.MANUALLY_SCHEDULED));

      if (mpx.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
      {
         xml.setManualDuration(DatatypeConverter.printDuration(this, mpx.getDuration()));
         xml.setManualFinish(mpx.getFinish());
         xml.setManualStart(mpx.getStart());
      }

      xml.setMilestone(Boolean.valueOf(mpx.getMilestone()));
      xml.setName(StringHelper.stripControlCharacters(mpx.getName()));
      xml.setNotes(nullIfEmpty(mpx.getNotes()));
      xml.setOutlineLevel(NumberHelper.getBigInteger(mpx.getOutlineLevel()));
      xml.setOutlineNumber(mpx.getOutlineNumber());
      xml.setOverAllocated(Boolean.valueOf(mpx.getOverAllocated()));
      xml.setOvertimeCost(DatatypeConverter.printCurrency(mpx.getOvertimeCost()));
      xml.setOvertimeWork(DatatypeConverter.printDuration(this, mpx.getOvertimeWork()));
      xml.setPercentComplete(mpx.getPercentageComplete());
      xml.setPercentWorkComplete(mpx.getPercentageWorkComplete());
      xml.setPhysicalPercentComplete(mpx.getPhysicalPercentComplete());
      xml.setPriority(DatatypeConverter.printPriority(mpx.getPriority()));
      xml.setRecurring(Boolean.valueOf(mpx.getRecurring()));
      xml.setRegularWork(DatatypeConverter.printDuration(this, mpx.getRegularWork()));
      xml.setRemainingCost(DatatypeConverter.printCurrency(mpx.getRemainingCost()));

      if (mpx.getRemainingDuration() == null)
      {
         Duration duration = mpx.getDuration();

         if (duration != null)
         {
            double amount = duration.getDuration();
            amount -= ((amount * NumberHelper.getDouble(mpx.getPercentageComplete())) / 100);
            xml.setRemainingDuration(DatatypeConverter.printDuration(this, Duration.getInstance(amount, duration.getUnits())));
         }
      }
      else
      {
         xml.setRemainingDuration(DatatypeConverter.printDuration(this, mpx.getRemainingDuration()));
      }

      xml.setRemainingOvertimeCost(DatatypeConverter.printCurrency(mpx.getRemainingOvertimeCost()));
      xml.setRemainingOvertimeWork(DatatypeConverter.printDuration(this, mpx.getRemainingOvertimeWork()));
      xml.setRemainingWork(DatatypeConverter.printDuration(this, mpx.getRemainingWork()));
      xml.setResume(mpx.getResume());
      xml.setResumeValid(Boolean.valueOf(mpx.getResumeValid()));
      xml.setRollup(Boolean.valueOf(mpx.getRollup()));
      xml.setStart(mpx.getStart());
      xml.setStartSlack(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getStartSlack()));
      xml.setStartText(mpx.getStartText());
      xml.setStartVariance(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getStartVariance()));
      xml.setStop(mpx.getStop());
      xml.setSubprojectName(mpx.getSubprojectFile());
      xml.setSummary(Boolean.valueOf(mpx.hasChildTasks()));
      xml.setTotalSlack(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getTotalSlack()));
      xml.setType(mpx.getType());
      xml.setUID(m_taskMapper.getUniqueID(mpx));
      xml.setWBS(mpx.getWBS());
      xml.setWork(DatatypeConverter.printDuration(this, mpx.getWork()));
      xml.setWorkVariance(DatatypeConverter.printDurationInDecimalThousandthsOfMinutes(mpx.getWorkVariance()));

      if (mpx.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
      {
         xml.setManualDuration(DatatypeConverter.printDuration(this, mpx.getManualDuration()));
      }

      writePredecessors(xml, mpx);

      writeTaskExtendedAttributes(xml, mpx);
      writeTaskOutlineCodes(xml, mpx);

      writeTaskBaselines(xml, mpx);

      return (xml);
   }

   /**
    * Writes task baseline data.
    *
    * @param xmlTask MSPDI task
    * @param mpxjTask MPXJ task
    */
   private void writeTaskBaselines(Project.Tasks.Task xmlTask, Task mpxjTask)
   {
      Project.Tasks.Task.Baseline baseline = m_factory.createProjectTasksTaskBaseline();
      boolean populated = false;

      Number cost = mpxjTask.getBaselineCost();
      if (cost != null && cost.intValue() != 0)
      {
         populated = true;
         baseline.setCost(DatatypeConverter.printCurrency(cost));
      }

      Duration duration = mpxjTask.getBaselineDuration();
      if (duration != null && duration.getDuration() != 0)
      {
         populated = true;
         baseline.setDuration(DatatypeConverter.printDuration(this, duration));
         baseline.setDurationFormat(DatatypeConverter.printDurationTimeUnits(duration, false));
      }

      LocalDateTime date = mpxjTask.getBaselineFinish();
      if (date != null)
      {
         populated = true;
         baseline.setFinish(date);
      }

      date = mpxjTask.getBaselineStart();
      if (date != null)
      {
         populated = true;
         baseline.setStart(date);
      }

      duration = mpxjTask.getBaselineWork();
      if (duration != null && duration.getDuration() != 0)
      {
         populated = true;
         baseline.setWork(DatatypeConverter.printDuration(this, duration));
      }

      if (populated)
      {
         baseline.setNumber(BigInteger.ZERO);
         xmlTask.getBaseline().add(baseline);
      }

      for (int loop = 1; loop <= 10; loop++)
      {
         baseline = m_factory.createProjectTasksTaskBaseline();
         populated = false;

         cost = mpxjTask.getBaselineCost(loop);
         if (cost != null && cost.intValue() != 0)
         {
            populated = true;
            baseline.setCost(DatatypeConverter.printCurrency(cost));
         }

         duration = mpxjTask.getBaselineDuration(loop);
         if (duration != null && duration.getDuration() != 0)
         {
            populated = true;
            baseline.setDuration(DatatypeConverter.printDuration(this, duration));
            baseline.setDurationFormat(DatatypeConverter.printDurationTimeUnits(duration, false));
         }

         date = mpxjTask.getBaselineFinish(loop);
         if (date != null)
         {
            populated = true;
            baseline.setFinish(date);
         }

         date = mpxjTask.getBaselineStart(loop);
         if (date != null)
         {
            populated = true;
            baseline.setStart(date);
         }

         duration = mpxjTask.getBaselineWork(loop);
         if (duration != null && duration.getDuration() != 0)
         {
            populated = true;
            baseline.setWork(DatatypeConverter.printDuration(this, duration));
         }

         if (populated)
         {
            baseline.setNumber(BigInteger.valueOf(loop));
            xmlTask.getBaseline().add(baseline);
         }
      }
   }

   /**
    * This method writes extended attribute data for a task.
    *
    * @param xml MSPDI task
    * @param mpx MPXJ task
    */
   private void writeTaskExtendedAttributes(Project.Tasks.Task xml, Task mpx)
   {
      List<Project.Tasks.Task.ExtendedAttribute> extendedAttributes = xml.getExtendedAttribute();
      Set<FieldType> outlineCodes = new HashSet<>(Arrays.asList(TaskFieldLists.CUSTOM_OUTLINE_CODE));
      m_extendedAttributes.stream().filter(f -> f.getFieldTypeClass() == FieldTypeClass.TASK && !outlineCodes.contains(f)).forEach(f -> writeTaskExtendedAttribute(extendedAttributes, mpx, f));
   }

   private void writeTaskExtendedAttribute(List<Project.Tasks.Task.ExtendedAttribute> extendedAttributes, Task mpx, FieldType mpxFieldID)
   {
      Object value = mpx.getCachedValue(mpxFieldID);

      if (FieldTypeHelper.valueIsNotDefault(mpxFieldID, value))
      {
         FieldType mappedFieldType = m_userDefinedFieldMap.getTarget(mpxFieldID);
         if (mappedFieldType instanceof TaskField)
         {
            Project.Tasks.Task.ExtendedAttribute attrib = m_factory.createProjectTasksTaskExtendedAttribute();
            extendedAttributes.add(attrib);
            attrib.setFieldID(Integer.toString(FieldTypeHelper.getFieldID(mappedFieldType)));
            attrib.setValue(DatatypeConverter.printCustomField(this, value, mappedFieldType.getDataType()));
            attrib.setDurationFormat(printCustomFieldDurationFormat(value));
            setValueGUID(attrib, mappedFieldType);
         }
      }
   }

   /**
    * This method writes outline codes for a task.
    *
    * @param xml MSPDI task
    * @param mpx MPXJ task
    */
   private void writeTaskOutlineCodes(Project.Tasks.Task xml, Task mpx)
   {
      List<Project.Tasks.Task.OutlineCode> outlineCodes = xml.getOutlineCode();

      for (TaskField mpxFieldID : TaskFieldLists.CUSTOM_OUTLINE_CODE)
      {
         Object value = mpx.getCachedValue(mpxFieldID);

         if (FieldTypeHelper.valueIsNotDefault(mpxFieldID, value))
         {
            Project.Tasks.Task.OutlineCode attrib = m_factory.createProjectTasksTaskOutlineCode();
            outlineCodes.add(attrib);
            attrib.setFieldID(Integer.toString(FieldTypeHelper.getFieldID(mpxFieldID)));
            setValueID(attrib, mpxFieldID, DatatypeConverter.printCustomField(this, value, mpxFieldID.getDataType()));
         }
      }
   }

   /**
    * Set the GUID of a value selected from a lookup table.
    *
    * @param attrib parent attribute
    * @param fieldType field type
    */
   private void setValueGUID(Project.Tasks.Task.ExtendedAttribute attrib, FieldType fieldType)
   {
      CustomFieldValueItem valueItem = getValueItem(fieldType, attrib.getValue());
      if (valueItem != null)
      {
         attrib.setValueGUID(valueItem.getGUID());
      }
   }

   /**
    * Set the ID of a value selected from a lookup table.
    *
    * @param attrib parent attribute
    * @param fieldType field type
    * @param formattedValue formatted value
    */
   private void setValueID(Project.Tasks.Task.OutlineCode attrib, FieldType fieldType, String formattedValue)
   {
      CustomFieldValueItem valueItem = getValueItem(fieldType, formattedValue);
      if (valueItem != null)
      {
         attrib.setValueID(NumberHelper.getBigInteger(valueItem.getUniqueID()));
      }
   }

   /**
    * Given a formatted value, retrieve the equivalent lookup table entry.
    *
    * @param fieldType field type
    * @param formattedValue formatted value
    * @return lookup table entry
    */
   private CustomFieldValueItem getValueItem(FieldType fieldType, String formattedValue)
   {
      CustomFieldValueItem result = null;

      CustomField field = m_projectFile.getCustomFields().get(fieldType);
      if (field != null)
      {
         List<CustomFieldValueItem> items = field.getLookupTable();
         if (!items.isEmpty())
         {
            result = m_customFieldValueItems.getOrDefault(fieldType, getCustomFieldValueItemMap(fieldType, items)).get(formattedValue);
         }
      }

      return result;
   }

   /**
    * Populate a cache of lookup table entries.
    *
    * @param fieldType field type
    * @param items list of lookup table entries
    * @return cache of lookup table entries
    */
   private HashMap<String, CustomFieldValueItem> getCustomFieldValueItemMap(FieldType fieldType, List<CustomFieldValueItem> items)
   {
      DataType dataType = fieldType.getDataType();
      HashMap<String, CustomFieldValueItem> result = new HashMap<>();
      // TODO: this doesn't handle hierarchical value lookup
      items.forEach(item -> result.put(DatatypeConverter.printCustomField(this, item.getValue(), dataType), item));
      return result;
   }

   /**
    * Converts a duration to duration time units.
    *
    * @param value duration value
    * @return duration time units
    */
   private BigInteger printCustomFieldDurationFormat(Object value)
   {
      BigInteger result = null;
      if (value instanceof Duration)
      {
         result = DatatypeConverter.printDurationTimeUnits(((Duration) value).getUnits(), false);
      }
      return (result);
   }

   /**
    * This method retrieves the UID for a calendar associated with a task.
    *
    * @param mpx MPX Task instance
    * @return calendar UID
    */
   private BigInteger getTaskCalendarID(Task mpx)
   {
      BigInteger result;
      ProjectCalendar cal = mpx.getCalendar();
      if (cal != null)
      {
         result = NumberHelper.getBigInteger(m_calendarMapper.getUniqueID(cal));
      }
      else
      {
         result = NULL_CALENDAR_ID;
      }
      return (result);
   }

   /**
    * This method writes predecessor data to an MSPDI file.
    * We have to deal with a slight anomaly in this method that is introduced
    * by the MPX file format. It would be possible for someone to create an
    * MPX file with both the predecessor list and the unique ID predecessor
    * list populated... which means that we must process both and avoid adding
    * duplicate predecessors. Also interesting to note is that MSP98 populates
    * the predecessor list, not the unique ID predecessor list, as you might
    * expect.
    *
    * @param xml MSPDI task data
    * @param mpx MPX task data
    */
   private void writePredecessors(Project.Tasks.Task xml, Task mpx)
   {
      List<Project.Tasks.Task.PredecessorLink> list = xml.getPredecessorLink();

      List<Relation> predecessors = mpx.getPredecessors();
      for (Relation rel : predecessors)
      {
         list.add(writePredecessor(rel.getPredecessorTask(), rel.getType(), rel.getLag()));
         m_eventManager.fireRelationWrittenEvent(rel);
      }
   }

   /**
    * This method writes a single predecessor link to the MSPDI file.
    *
    * @param predecessor predecessor task
    * @param type The predecessor type
    * @param lag The lag duration
    * @return A new link to be added to the MSPDI file
    */
   private Project.Tasks.Task.PredecessorLink writePredecessor(Task predecessor, RelationType type, Duration lag)
   {
      Project.Tasks.Task.PredecessorLink link = m_factory.createProjectTasksTaskPredecessorLink();

      link.setPredecessorUID(NumberHelper.getBigInteger(m_taskMapper.getUniqueID(predecessor)));
      link.setCrossProject(Boolean.valueOf(predecessor.getExternalTask()));
      link.setType(BigInteger.valueOf(type.getValue()));
      if (lag != null && lag.getDuration() != 0)
      {
         double linkLag = lag.getDuration();
         if (lag.getUnits() != TimeUnit.PERCENT && lag.getUnits() != TimeUnit.ELAPSED_PERCENT)
         {
            linkLag = 10.0 * Duration.convertUnits(linkLag, lag.getUnits(), TimeUnit.MINUTES, m_projectFile.getProjectProperties()).getDuration();
         }
         link.setLinkLag(BigInteger.valueOf((long) linkLag));
         link.setLagFormat(DatatypeConverter.printDurationTimeUnits(lag.getUnits(), false));
      }
      else
      {
         // SF-329: default required to keep Powerproject happy when importing MSPDI files
         link.setLinkLag(BIGINTEGER_ZERO);
         link.setLagFormat(DatatypeConverter.printDurationTimeUnits(m_projectFile.getProjectProperties().getDefaultDurationUnits(), false));
      }

      if (predecessor.getExternalTask())
      {
         // Note that MS Project doesn't actually read external task data correctly,
         // even if it wrote the file itself. We'll just replicate what MS Project writes.
         link.setCrossProjectName(predecessor.getSubprojectFile() + "\\" + predecessor.getSubprojectTaskID());
      }

      return link;
   }

   /**
    * This method writes assignment data to an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeAssignments(Project project)
   {
      Project.Assignments assignments = m_factory.createProjectAssignments();
      project.setAssignments(assignments);
      List<Project.Assignments.Assignment> list = assignments.getAssignment();

      // As we now allow a resource to be assigned multiple times to a task
      // we need to handle this for file formats which allow a resource to be
      // assigned only once. The code below attempts to preserve the original
      // behaviour when we ignored multiple assignments of the same resource.
      // TODO: implement more intelligent rollup of multiple resource assignments
      Function<ResourceAssignment, String> assignmentKey = (a) -> a.getTaskUniqueID() + " " + a.getResourceUniqueID();
      Map<String, ResourceAssignment> map = m_projectFile.getResourceAssignments().stream().collect(Collectors.toMap(assignmentKey, Function.identity(), (a1, a2) -> a1));
      m_projectFile.getResourceAssignments().stream().filter(a -> map.get(assignmentKey.apply(a)) == a).forEach(a -> list.add(writeAssignment(a)));

      //
      // Check to see if we have any tasks that have a percent complete value
      // but do not have resource assignments. If any exist, then we must
      // write a dummy resource assignment record to ensure that the MSPDI
      // file shows the correct percent complete amount for the task.
      //
      ProjectConfig config = m_projectFile.getProjectConfig();
      boolean autoUniqueID = config.getAutoAssignmentUniqueID();
      if (!autoUniqueID)
      {
         config.setAutoAssignmentUniqueID(true);
      }

      for (Task task : m_projectFile.getTasks())
      {
         double percentComplete = NumberHelper.getDouble(task.getPercentageComplete());
         if (percentComplete != 0 && task.getResourceAssignments().isEmpty())
         {
            ResourceAssignment dummy = new ResourceAssignment(m_projectFile, task);
            Duration duration = task.getDuration();
            if (duration == null)
            {
               duration = Duration.getInstance(0, TimeUnit.HOURS);
            }
            double durationValue = duration.getDuration();
            TimeUnit durationUnits = duration.getUnits();
            double actualWork = (durationValue * percentComplete) / 100;
            double remainingWork = durationValue - actualWork;

            if (m_generateMissingTimephasedData)
            {
               // I'm being conservative here... I'm sure there is no issue with including
               // this in the MSPDI file, but asI've only added this for the "generate
               // missing timephased data" feature, I'll keep it optional for now.
               dummy.setActualStart(task.getActualStart());
            }

            dummy.setResourceUniqueID(MicrosoftProjectConstants.ASSIGNMENT_NULL_RESOURCE_ID);
            dummy.setWork(duration);
            dummy.setActualWork(Duration.getInstance(actualWork, durationUnits));
            dummy.setRemainingWork(Duration.getInstance(remainingWork, durationUnits));

            // Without this, MS Project will mark a 100% complete milestone as 99% complete
            if (percentComplete == 100 && duration.getDuration() == 0)
            {
               dummy.setActualFinish(task.getActualStart());
            }

            list.add(writeAssignment(dummy));
         }
      }

      config.setAutoAssignmentUniqueID(autoUniqueID);
   }

   /**
    * This method writes data for a single assignment to an MSPDI file.
    *
    * @param mpx Resource assignment data
    * @return New MSPDI assignment instance
    */
   private Project.Assignments.Assignment writeAssignment(ResourceAssignment mpx)
   {
      Project.Assignments.Assignment xml = m_factory.createProjectAssignmentsAssignment();

      xml.setActualCost(DatatypeConverter.printCurrency(mpx.getActualCost()));
      xml.setActualFinish(mpx.getActualFinish());
      xml.setActualOvertimeCost(DatatypeConverter.printCurrency(mpx.getActualOvertimeCost()));
      xml.setActualOvertimeWork(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWork()));
      xml.setActualStart(mpx.getActualStart());
      xml.setActualWork(DatatypeConverter.printDuration(this, mpx.getActualWork()));
      xml.setACWP(DatatypeConverter.printCurrency(mpx.getACWP()));
      xml.setBCWP(DatatypeConverter.printCurrency(mpx.getBCWP()));
      xml.setBCWS(DatatypeConverter.printCurrency(mpx.getBCWS()));
      xml.setBudgetCost(DatatypeConverter.printCurrency(mpx.getBudgetCost()));
      xml.setBudgetWork(DatatypeConverter.printDuration(this, mpx.getBudgetWork()));
      xml.setCost(DatatypeConverter.printCurrency(mpx.getCost()));

      if (mpx.getCostRateTableIndex() != 0)
      {
         xml.setCostRateTable(BigInteger.valueOf(mpx.getCostRateTableIndex()));
      }

      xml.setCreationDate(mpx.getCreateDate());
      xml.setCV(DatatypeConverter.printCurrency(mpx.getCV()));
      xml.setDelay(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getDelay()));
      xml.setFinish(mpx.getFinish());
      xml.setGUID(mpx.getGUID());
      xml.setHasFixedRateUnits(Boolean.valueOf(mpx.getVariableRateUnits() == null));
      xml.setFixedMaterial(Boolean.valueOf(mpx.getResource() != null && mpx.getResource().getType() == ResourceType.MATERIAL));
      xml.setHyperlink(mpx.getHyperlink());
      xml.setHyperlinkAddress(mpx.getHyperlinkAddress());
      xml.setHyperlinkSubAddress(mpx.getHyperlinkSubAddress());
      xml.setLevelingDelay(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getLevelingDelay()));
      xml.setLevelingDelayFormat(DatatypeConverter.printDurationTimeUnits(mpx.getLevelingDelay(), false));
      xml.setNotes(nullIfEmpty(mpx.getNotes()));
      xml.setOvertimeCost(DatatypeConverter.printCurrency(mpx.getOvertimeCost()));
      xml.setOvertimeWork(DatatypeConverter.printDuration(this, mpx.getOvertimeWork()));
      xml.setPercentWorkComplete(mpx.getPercentageWorkComplete());
      xml.setRateScale(mpx.getVariableRateUnits() == null ? null : DatatypeConverter.printTimeUnit(mpx.getVariableRateUnits()));
      xml.setRegularWork(DatatypeConverter.printDuration(this, mpx.getRegularWork()));
      xml.setRemainingCost(DatatypeConverter.printCurrency(mpx.getRemainingCost()));
      xml.setRemainingOvertimeCost(DatatypeConverter.printCurrency(mpx.getRemainingOvertimeCost()));
      xml.setRemainingOvertimeWork(DatatypeConverter.printDuration(this, mpx.getRemainingOvertimeWork()));

      if (mpx.getRemainingWork() == null)
      {
         Duration work = mpx.getWork();

         if (work != null)
         {
            double amount = work.getDuration();
            amount -= ((amount * NumberHelper.getDouble(mpx.getPercentageWorkComplete())) / 100);
            xml.setRemainingWork(DatatypeConverter.printDuration(this, Duration.getInstance(amount, work.getUnits())));
         }
      }
      else
      {
         xml.setRemainingWork(DatatypeConverter.printDuration(this, mpx.getRemainingWork()));
      }

      xml.setResourceUID(mpx.getResource() == null ? BigInteger.valueOf(MicrosoftProjectConstants.ASSIGNMENT_NULL_RESOURCE_ID.intValue()) : BigInteger.valueOf(NumberHelper.getInt(m_resourceMapper.getUniqueID(mpx.getResource()))));
      xml.setResume(mpx.getResume());
      xml.setStart(mpx.getStart());
      xml.setStop(mpx.getStop());
      xml.setSV(DatatypeConverter.printCurrency(mpx.getSV()));
      xml.setTaskUID(NumberHelper.getBigInteger(m_taskMapper.getUniqueID(mpx.getTask())));
      xml.setUID(NumberHelper.getBigInteger(m_assignmentMapper.getUniqueID(mpx)));
      xml.setUnits(DatatypeConverter.printUnits(mpx.getUnits()));
      xml.setVAC(DatatypeConverter.printCurrency(mpx.getVAC()));
      xml.setWork(DatatypeConverter.printDuration(this, mpx.getWork()));
      xml.setWorkContour(mpx.getWorkContour());

      xml.setCostVariance(DatatypeConverter.printCurrency(mpx.getCostVariance()));
      xml.setWorkVariance(DatatypeConverter.printDurationInDecimalThousandthsOfMinutes(mpx.getWorkVariance()));
      xml.setStartVariance(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getStartVariance()));
      xml.setFinishVariance(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getFinishVariance()));

      //
      // MS Project is a bit picky when it reads an MSPDI file. Even if a resource assignment
      // is marked as being 100% complete, unless there is an actual finish date
      // specified, MS Project will only show 99% complete. We try to fix this here by ensuring
      // that an actual finish date is populated when the task is 100% complete.
      //
      double percentComplete = NumberHelper.getDouble(mpx.getTask().getPercentageComplete());
      if (percentComplete == 100 && xml.getActualFinish() == null)
      {
         xml.setActualFinish(mpx.getTask().getActualFinish());
      }

      writeAssignmentBaselines(xml, mpx);

      writeAssignmentExtendedAttributes(xml, mpx);

      writeAssignmentTimephasedData(mpx, xml);

      m_eventManager.fireAssignmentWrittenEvent(mpx);

      return (xml);
   }

   /**
    * Writes assignment baseline data.
    *
    * @param xml MSPDI assignment
    * @param mpxj MPXJ assignment
    */
   private void writeAssignmentBaselines(Project.Assignments.Assignment xml, ResourceAssignment mpxj)
   {
      Project.Assignments.Assignment.Baseline baseline = m_factory.createProjectAssignmentsAssignmentBaseline();
      boolean populated = false;

      Number cost = mpxj.getBaselineCost();
      if (cost != null && cost.intValue() != 0)
      {
         populated = true;
         baseline.setCost(DatatypeConverter.printCustomFieldCurrency(cost));
      }

      LocalDateTime date = mpxj.getBaselineFinish();
      if (date != null)
      {
         populated = true;
         baseline.setFinish(DatatypeConverter.printCustomFieldDate(date));
      }

      date = mpxj.getBaselineStart();
      if (date != null)
      {
         populated = true;
         baseline.setStart(DatatypeConverter.printCustomFieldDate(date));
      }

      Duration duration = mpxj.getBaselineWork();
      if (duration != null && duration.getDuration() != 0)
      {
         populated = true;
         baseline.setWork(DatatypeConverter.printDuration(this, duration));
      }

      if (populated)
      {
         baseline.setNumber("0");
         xml.getBaseline().add(baseline);
      }

      for (int loop = 1; loop <= 10; loop++)
      {
         baseline = m_factory.createProjectAssignmentsAssignmentBaseline();
         populated = false;

         cost = mpxj.getBaselineCost(loop);
         if (cost != null && cost.intValue() != 0)
         {
            populated = true;
            baseline.setCost(DatatypeConverter.printCustomFieldCurrency(cost));
         }

         date = mpxj.getBaselineFinish(loop);
         if (date != null)
         {
            populated = true;
            baseline.setFinish(DatatypeConverter.printCustomFieldDate(date));
         }

         date = mpxj.getBaselineStart(loop);
         if (date != null)
         {
            populated = true;
            baseline.setStart(DatatypeConverter.printCustomFieldDate(date));
         }

         duration = mpxj.getBaselineWork(loop);
         if (duration != null && duration.getDuration() != 0)
         {
            populated = true;
            baseline.setWork(DatatypeConverter.printDuration(this, duration));
         }

         if (populated)
         {
            baseline.setNumber(Integer.toString(loop));
            xml.getBaseline().add(baseline);
         }
      }
   }

   /**
    * This method writes extended attribute data for an assignment.
    *
    * @param xml MSPDI assignment
    * @param mpx MPXJ assignment
    */
   private void writeAssignmentExtendedAttributes(Project.Assignments.Assignment xml, ResourceAssignment mpx)
   {
      List<Project.Assignments.Assignment.ExtendedAttribute> extendedAttributes = xml.getExtendedAttribute();
      m_extendedAttributes.stream().filter(f -> f.getFieldTypeClass() == FieldTypeClass.ASSIGNMENT).forEach(f -> writeAssignmentExtendedAttribute(extendedAttributes, mpx, f));
   }

   private void writeAssignmentExtendedAttribute(List<Project.Assignments.Assignment.ExtendedAttribute> extendedAttributes, ResourceAssignment mpx, FieldType mpxFieldID)
   {
      Object value = mpx.getCachedValue(mpxFieldID);

      if (FieldTypeHelper.valueIsNotDefault(mpxFieldID, value))
      {
         FieldType mappedFieldType = m_userDefinedFieldMap.getTarget(mpxFieldID);
         if (mappedFieldType instanceof AssignmentField)
         {
            Project.Assignments.Assignment.ExtendedAttribute attrib = m_factory.createProjectAssignmentsAssignmentExtendedAttribute();
            extendedAttributes.add(attrib);
            attrib.setFieldID(Integer.toString(FieldTypeHelper.getFieldID(mappedFieldType)));
            attrib.setValue(DatatypeConverter.printCustomField(this, value, mappedFieldType.getDataType()));
            attrib.setDurationFormat(printCustomFieldDurationFormat(value));
         }
      }
   }

   /**
    * Writes the timephased data for a resource assignment.
    *
    * @param mpx MPXJ assignment
    * @param xml MSPDI assignment
    */
   private void writeAssignmentTimephasedData(ResourceAssignment mpx, Project.Assignments.Assignment xml)
   {
      if (!m_writeTimephasedData || m_sourceIsPrimavera)
      {
         return;
      }

      if (!mpx.getHasTimephasedData() && !m_generateMissingTimephasedData)
      {
         return;
      }

      ProjectCalendar calendar = getCalendar(mpx);
      List<TimephasedWork> complete = mpx.getTimephasedActualWork();
      List<TimephasedWork> planned = mpx.getTimephasedWork();
      List<TimephasedWork> completeOvertime = mpx.getTimephasedActualOvertimeWork();

      if ((planned == null || planned.isEmpty()) && m_generateMissingTimephasedData)
      {
         planned = generateTimephasedPlannedWork(mpx);
      }

      if ((complete == null || complete.isEmpty()) && m_generateMissingTimephasedData)
      {
         complete = generateTimephasedCompleteWork(mpx);
      }

      complete = splitCompleteWork(calendar, planned, complete);
      planned = splitPlannedWork(calendar, planned, complete);
      completeOvertime = splitDays(calendar, completeOvertime, null, null);

      BigInteger assignmentID = xml.getUID();
      List<TimephasedDataType> list = xml.getTimephasedData();
      writeAssignmentTimephasedWorkData(assignmentID, list, complete, 2);
      writeAssignmentTimephasedWorkData(assignmentID, list, planned, 1);
      writeAssignmentTimephasedWorkData(assignmentID, list, completeOvertime, 3);

      // Write the baselines
      for (int index = 0; index < TIMEPHASED_BASELINE_WORK_TYPES.length; index++)
      {
         writeAssignmentTimephasedWorkData(assignmentID, list, splitDays(calendar, mpx.getTimephasedBaselineWork(index), null, null), TIMEPHASED_BASELINE_WORK_TYPES[index]);
      }

      for (int index = 0; index < TIMEPHASED_BASELINE_COST_TYPES.length; index++)
      {
         writeAssignmentTimephasedCostData(assignmentID, list, splitDays(calendar, mpx.getTimephasedBaselineCost(index)), TIMEPHASED_BASELINE_COST_TYPES[index]);
      }
   }

   private List<TimephasedWork> generateTimephasedPlannedWork(ResourceAssignment assignment)
   {
      if (assignment.getActualFinish() != null)
      {
         return null;
      }

      LocalDateTime start;
      ProjectCalendar calendar = assignment.getEffectiveCalendar();

      if (assignment.getActualStart() == null)
      {
         start = assignment.getStart();
      }
      else
      {
         start = calendar.getNextWorkStart(calendar.getDate(assignment.getActualStart(), assignment.getActualWork()));
      }

      TimephasedWork work = new TimephasedWork();
      work.setStart(start);
      work.setFinish(assignment.getFinish());
      work.setTotalAmount(assignment.getRemainingWork());
      work.setAmountPerDay(Duration.getInstance(NumberHelper.getInt(calendar.getMinutesPerDay()), TimeUnit.MINUTES));
      return Collections.singletonList(work);
   }

   private List<TimephasedWork> generateTimephasedCompleteWork(ResourceAssignment assignment)
   {
      if (assignment.getActualStart() == null)
      {
         return null;
      }

      LocalDateTime finish;
      ProjectCalendar calendar = assignment.getEffectiveCalendar();

      if (assignment.getActualFinish() == null)
      {
         finish = calendar.getDate(assignment.getActualStart(), assignment.getActualWork());
      }
      else
      {
         finish = assignment.getActualFinish();
      }

      TimephasedWork work = new TimephasedWork();
      work.setStart(assignment.getActualStart());
      work.setFinish(finish);
      work.setTotalAmount(assignment.getActualWork());
      work.setAmountPerDay(Duration.getInstance(NumberHelper.getInt(calendar.getMinutesPerDay()), TimeUnit.MINUTES));
      return Collections.singletonList(work);
   }

   private List<TimephasedWork> splitCompleteWork(ProjectCalendar calendar, List<TimephasedWork> planned, List<TimephasedWork> complete)
   {
      if (!m_splitTimephasedAsDays || complete == null)
      {
         return complete;
      }

      TimephasedWork firstPlanned = null;
      if (planned != null && !planned.isEmpty())
      {
         firstPlanned = planned.get(0);
      }

      return splitDays(calendar, complete, firstPlanned, null);
   }

   private List<TimephasedWork> splitPlannedWork(ProjectCalendar calendar, List<TimephasedWork> planned, List<TimephasedWork> complete)
   {
      if (!m_splitTimephasedAsDays || planned == null)
      {
         return planned;
      }

      TimephasedWork lastComplete = null;
      if (complete != null && !complete.isEmpty())
      {
         lastComplete = complete.get(complete.size() - 1);
      }

      return splitDays(calendar, planned, null, lastComplete);
   }

   /**
    * Determine the calendar to use when working with timephased resource assignment data.
    *
    * @param assignment resource assignment
    * @return calendar to use
    */
   private ProjectCalendar getCalendar(ResourceAssignment assignment)
   {
      return assignment.getEffectiveCalendar();
   }

   /**
    * Splits timephased data into individual days.
    *
    * @param calendar current calendar
    * @param list list of timephased assignment data
    * @param first first planned assignment
    * @param last last completed assignment
    * @return list of timephased data ready for output
    */
   private List<TimephasedWork> splitDays(ProjectCalendar calendar, List<TimephasedWork> list, TimephasedWork first, TimephasedWork last)
   {
      if (!m_splitTimephasedAsDays || list == null || list.isEmpty())
      {
         return list;
      }

      List<TimephasedWork> result = new ArrayList<>();
      for (TimephasedWork assignment : list)
      {
         LocalDateTime startDate = assignment.getStart();
         LocalDateTime finishDate = assignment.getFinish();
         LocalDateTime startDay = LocalDateTimeHelper.getDayStartDate(startDate);
         LocalDateTime finishDay = LocalDateTimeHelper.getDayStartDate(finishDate);
         if (startDay.equals(finishDay))
         {
            LocalTime startTime = calendar.getStartTime(LocalDateHelper.getLocalDate(startDay));
            LocalDateTime currentStart = startTime == null ? null : LocalDateTime.of(startDay.toLocalDate(), startTime);
            if (currentStart != null && startDate.isAfter(currentStart))
            {
               boolean paddingRequired = true;

               if (last != null)
               {
                  LocalDateTime lastFinish = last.getFinish();
                  if (lastFinish.equals(startDate))
                  {
                     paddingRequired = false;
                  }
                  else
                  {
                     LocalDateTime lastFinishDay = LocalDateTimeHelper.getDayStartDate(lastFinish);
                     if (startDay.equals(lastFinishDay))
                     {
                        currentStart = lastFinish;
                     }
                  }
               }

               if (paddingRequired)
               {
                  Duration zeroHours = Duration.getInstance(0, TimeUnit.HOURS);
                  TimephasedWork padding = new TimephasedWork();
                  padding.setStart(currentStart);
                  padding.setFinish(startDate);
                  padding.setTotalAmount(zeroHours);
                  padding.setAmountPerDay(zeroHours);
                  result.add(padding);
               }
            }

            result.add(assignment);

            LocalTime finishTime = calendar.getFinishTime(LocalDateHelper.getLocalDate(startDay));
            LocalDateTime currentFinish = finishTime == null ? null : LocalDateTime.of(startDay.toLocalDate(), finishTime);
            if (currentFinish != null && finishDate.isBefore(currentFinish))
            {
               boolean paddingRequired = true;

               if (first != null)
               {
                  LocalDateTime firstStart = first.getStart();
                  if (firstStart.equals(finishDate))
                  {
                     paddingRequired = false;
                  }
                  else
                  {
                     LocalDateTime firstStartDay = LocalDateTimeHelper.getDayStartDate(firstStart);
                     if (finishDay.equals(firstStartDay))
                     {
                        currentFinish = firstStart;
                     }
                  }
               }

               if (paddingRequired)
               {
                  Duration zeroHours = Duration.getInstance(0, TimeUnit.HOURS);
                  TimephasedWork padding = new TimephasedWork();
                  padding.setStart(finishDate);
                  padding.setFinish(currentFinish);
                  padding.setTotalAmount(zeroHours);
                  padding.setAmountPerDay(zeroHours);
                  result.add(padding);
               }
            }
         }
         else
         {
            LocalDateTime currentStart = startDate;
            boolean isWorking = calendar.isWorkingDate(LocalDateHelper.getLocalDate(currentStart));
            while (currentStart.isBefore(finishDate))
            {
               if (isWorking)
               {
                  LocalDateTime currentFinish = LocalDateTime.of(currentStart.toLocalDate(), calendar.getFinishTime(LocalDateHelper.getLocalDate(currentStart)));
                  if (currentFinish.isAfter(finishDate))
                  {
                     currentFinish = finishDate;
                  }

                  TimephasedWork split = new TimephasedWork();
                  split.setStart(currentStart);
                  split.setFinish(currentFinish);
                  split.setTotalAmount(assignment.getAmountPerDay());
                  split.setAmountPerDay(assignment.getAmountPerDay());
                  result.add(split);
               }

               currentStart = currentStart.plusDays(1);
               isWorking = calendar.isWorkingDate(LocalDateHelper.getLocalDate(currentStart));
               if (isWorking)
               {
                  currentStart = LocalDateTime.of(currentStart.toLocalDate(), calendar.getStartTime(LocalDateHelper.getLocalDate(currentStart)));
               }
            }
         }
      }

      return result;
   }

   private List<TimephasedCost> splitDays(ProjectCalendar calendar, List<TimephasedCost> list)
   {
      if (!m_splitTimephasedAsDays || list == null || list.isEmpty())
      {
         return list;
      }

      List<TimephasedCost> result = new ArrayList<>();
      for (TimephasedCost assignment : list)
      {
         LocalDateTime startDate = assignment.getStart();
         LocalDateTime finishDate = assignment.getFinish();
         LocalDateTime startDay = LocalDateTimeHelper.getDayStartDate(startDate);
         LocalDateTime finishDay = LocalDateTimeHelper.getDayStartDate(finishDate);
         if (startDay.equals(finishDay))
         {
            LocalTime startTime = calendar.getStartTime(LocalDateHelper.getLocalDate(startDay));
            LocalDateTime currentStart = startTime == null ? null : LocalDateTime.of(startDay.toLocalDate(), startTime);
            if (currentStart != null && startDate.isAfter(currentStart))
            {
               TimephasedCost padding = new TimephasedCost();
               padding.setStart(currentStart);
               padding.setFinish(startDate);
               padding.setTotalAmount(Integer.valueOf(0));
               padding.setAmountPerDay(Integer.valueOf(0));
               result.add(padding);
            }

            result.add(assignment);

            LocalTime finishTime = calendar.getFinishTime(LocalDateHelper.getLocalDate(startDay));
            LocalDateTime currentFinish = finishTime == null ? null : LocalDateTime.of(startDay.toLocalDate(), finishTime);
            if (currentFinish != null && finishDate.isBefore(currentFinish))
            {
               TimephasedCost padding = new TimephasedCost();
               padding.setStart(finishDate);
               padding.setFinish(currentFinish);
               padding.setTotalAmount(Integer.valueOf(0));
               padding.setAmountPerDay(Integer.valueOf(0));
               result.add(padding);
            }
         }
         else
         {
            LocalDateTime currentStart = startDate;
            boolean isWorking = calendar.isWorkingDate(LocalDateHelper.getLocalDate(currentStart));
            while (currentStart.isBefore(finishDate))
            {
               if (isWorking)
               {
                  LocalDateTime currentFinish = LocalDateTime.of(currentStart.toLocalDate(), calendar.getFinishTime(LocalDateHelper.getLocalDate(currentStart)));
                  if (currentFinish.isAfter(finishDate))
                  {
                     currentFinish = finishDate;
                  }

                  TimephasedCost split = new TimephasedCost();
                  split.setStart(currentStart);
                  split.setFinish(currentFinish);
                  split.setTotalAmount(assignment.getAmountPerDay());
                  split.setAmountPerDay(assignment.getAmountPerDay());
                  result.add(split);
               }

               currentStart = currentStart.plusDays(1);
               isWorking = calendar.isWorkingDate(LocalDateHelper.getLocalDate(currentStart));
               if (isWorking)
               {
                  currentStart = LocalDateTime.of(currentStart.toLocalDate(), calendar.getStartTime(LocalDateHelper.getLocalDate(currentStart)));
               }
            }
         }
      }

      return result;
   }

   /**
    * Writes a list of timephased data to the MSPDI file.
    *
    * @param assignmentID current assignment ID
    * @param list output list of timephased data items
    * @param data input list of timephased data
    * @param type list type (planned or completed)
    */
   private void writeAssignmentTimephasedWorkData(BigInteger assignmentID, List<TimephasedDataType> list, List<TimephasedWork> data, int type)
   {
      if (data == null)
      {
         return;
      }

      for (TimephasedWork mpx : data)
      {
         TimephasedDataType xml = m_factory.createTimephasedDataType();
         list.add(xml);

         xml.setStart(mpx.getStart());
         xml.setFinish(mpx.getFinish());
         xml.setType(BigInteger.valueOf(type));
         xml.setUID(assignmentID);
         xml.setUnit(timephasedDataPeriodUnit(mpx));
         xml.setValue(DatatypeConverter.printDuration(this, mpx.getTotalAmount()));
      }
   }

   /**
    * The unit attribute of a timephased item appears to relate to the duration of the period covered
    * by the item, not the amount of work in that period.
    *
    * @param item timephased item
    * @return units for the timephased item
    */
   private BigInteger timephasedDataPeriodUnit(TimephasedItem<?> item)
   {
      long itemDuration = item.getStart().until(item.getFinish(), ChronoUnit.DAYS);
      if (itemDuration >= 364)
      {
         return TIMEPHASED_DATA_PERIOD_YEARS;
      }

      if (itemDuration >= 28)
      {
         return TIMEPHASED_DATA_PERIOD_MONTHS;
      }

      if (itemDuration >= 7)
      {
         return TIMEPHASED_DATA_PERIOD_WEEKS;
      }

      if (itemDuration >= 1)
      {
         return TIMEPHASED_DATA_PERIOD_DAYS;
      }

      itemDuration = item.getStart().until(item.getFinish(), ChronoUnit.MINUTES);
      if (itemDuration >= 60)
      {
         return TIMEPHASED_DATA_PERIOD_HOURS;
      }

      return TIMEPHASED_DATA_PERIOD_MINUTES;
   }

   private void writeAssignmentTimephasedCostData(BigInteger assignmentID, List<TimephasedDataType> list, List<TimephasedCost> data, int type)
   {
      if (data == null)
      {
         return;
      }

      for (TimephasedCost mpx : data)
      {
         TimephasedDataType xml = m_factory.createTimephasedDataType();
         list.add(xml);

         BigDecimal value = DatatypeConverter.printCurrency(mpx.getTotalAmount());

         xml.setStart(mpx.getStart());
         xml.setFinish(mpx.getFinish());
         xml.setType(BigInteger.valueOf(type));
         xml.setUID(assignmentID);
         xml.setUnit(timephasedDataPeriodUnit(mpx));
         xml.setValue(value == null ? null : value.toString());
      }
   }

   /**
    * Package-private accessor method used to retrieve the project file
    * currently being processed by this writer.
    *
    * @return project file instance
    */
   ProjectFile getProjectFile()
   {
      return (m_projectFile);
   }

   private List<FieldType> getExtendedAttributesList()
   {
      // All custom fields with configuration
      Set<FieldType> set = m_projectFile.getCustomFields().stream().map(CustomField::getFieldType).filter(Objects::nonNull).collect(Collectors.toSet());

      // All user defined fields
      set.addAll(m_projectFile.getUserDefinedFields());

      // All custom fields with values
      set.addAll(m_projectFile.getPopulatedFields().stream().filter(FieldLists.CUSTOM_FIELDS_SET::contains).collect(Collectors.toSet()));

      // Remove unknown fields
      set.removeIf(f -> FieldTypeHelper.getFieldID(f) == -1);

      return set.stream().sorted(Comparator.comparing(FieldTypeHelper::getFieldID)).collect(Collectors.toList());
   }

   /**
    * Format a material label to meet MS Project's requirements.
    *
    * @param resource resource
    * @return material label acceptable to MS Project
    */
   private String formatMaterialLabel(Resource resource)
   {
      if (resource.getType() != ResourceType.MATERIAL)
      {
         return null;
      }

      String text = resource.getMaterialLabel();
      if (text == null || text.isEmpty())
      {
         return text;
      }

      // Can't contain square brackets
      int index = text.indexOf('[');
      if (index != -1)
      {
         text = text.replace("[", "");
      }

      index = text.indexOf(']');
      if (index != -1)
      {
         text = text.replace("]", "");
      }

      // Can't contain time unit names
      if (TIME_UNIT_NAMES.contains(text.trim()))
      {
         text = text.trim() + ".";
      }

      // Can't be longer than 32 characters
      if (text.length() > 32)
      {
         text = text.substring(0, 32);
      }

      return text;
   }

   private String nullIfEmpty(String value)
   {
      return value != null && !value.isEmpty() ? value : null;
   }

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
         CONTEXT = JAXBContext.newInstance("org.mpxj.mspdi.schema", MSPDIWriter.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }

   // TODO share this
   private static final int[] DAY_MASKS =
   {
      0x00,
      0x01, // Sunday
      0x02, // Monday
      0x04, // Tuesday
      0x08, // Wednesday
      0x10, // Thursday
      0x20, // Friday
      0x40, // Saturday
   };

   private static final Set<String> MICROSOFT_PROJECT_FILES = new HashSet<>(Arrays.asList("MPP", "MPX", "MSPDI", "MPD"));

   private ObjectFactory m_factory;

   private ProjectFile m_projectFile;

   private EventManager m_eventManager;

   private Map<FieldType, Map<String, CustomFieldValueItem>> m_customFieldValueItems;

   private Map<Integer, Integer> m_resouceCalendarMap;

   private List<FieldType> m_extendedAttributes;

   private boolean m_sourceIsMicrosoftProject;
   private UserDefinedFieldMap m_userDefinedFieldMap;

   private boolean m_compatibleOutput = true;

   private boolean m_splitTimephasedAsDays = true;

   private boolean m_writeTimephasedData;

   private boolean m_generateMissingTimephasedData;

   private boolean m_sourceIsPrimavera;

   private SaveVersion m_saveVersion = SaveVersion.Project2016;

   private MicrosoftProjectUniqueIDMapper m_taskMapper;
   private MicrosoftProjectUniqueIDMapper m_resourceMapper;
   private MicrosoftProjectUniqueIDMapper m_calendarMapper;
   private MicrosoftProjectUniqueIDMapper m_assignmentMapper;

   private static final BigInteger BIGINTEGER_ZERO = BigInteger.valueOf(0);

   private static final BigInteger NULL_CALENDAR_ID = BigInteger.valueOf(-1);

   private static final List<FieldType> MAPPING_TARGET_CUSTOM_FIELDS = new ArrayList<>();
   static
   {
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_TEXT));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_DATE));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_START));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_FINISH));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_COST));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_FLAG));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_NUMBER));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_DURATION));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_TEXT));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_DATE));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_START));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_FINISH));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_COST));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_FLAG));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_NUMBER));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_DURATION));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_TEXT));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_DATE));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_START));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_FINISH));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_COST));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_FLAG));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_NUMBER));
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_DURATION));
   }

   private static final int[] TIMEPHASED_BASELINE_WORK_TYPES =
   {
      4,
      16,
      22,
      28,
      34,
      40,
      46,
      52,
      58,
      64,
      70
   };

   private static final int[] TIMEPHASED_BASELINE_COST_TYPES =
   {
      5,
      17,
      23,
      29,
      35,
      41,
      47,
      53,
      59,
      65,
      71,
   };

   private static final Set<String> TIME_UNIT_NAMES = new HashSet<>(Arrays.stream(TimeUnit.values()).map(TimeUnit::getName).collect(Collectors.toList()));

   private static final BigInteger TIMEPHASED_DATA_PERIOD_YEARS = BigInteger.valueOf(8);
   private static final BigInteger TIMEPHASED_DATA_PERIOD_MONTHS = BigInteger.valueOf(5);
   private static final BigInteger TIMEPHASED_DATA_PERIOD_WEEKS = BigInteger.valueOf(3);
   private static final BigInteger TIMEPHASED_DATA_PERIOD_DAYS = BigInteger.valueOf(2);
   private static final BigInteger TIMEPHASED_DATA_PERIOD_HOURS = BigInteger.valueOf(1);
   private static final BigInteger TIMEPHASED_DATA_PERIOD_MINUTES = BigInteger.valueOf(0);
}
