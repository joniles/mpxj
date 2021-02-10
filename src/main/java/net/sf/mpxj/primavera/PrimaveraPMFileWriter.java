/*
 * file:       PrimaveraPMFileWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2012
 * date:       2012-03-16
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

package net.sf.mpxj.primavera;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.Availability;
import net.sf.mpxj.AvailabilityTable;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.CostAccount;
import net.sf.mpxj.CostRateTable;
import net.sf.mpxj.CostRateTableEntry;
import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.CustomField;
import net.sf.mpxj.DataType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ExpenseCategory;
import net.sf.mpxj.ExpenseItem;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.FieldTypeClass;
import net.sf.mpxj.HtmlNotes;
import net.sf.mpxj.Notes;
import net.sf.mpxj.ParentNotes;
import net.sf.mpxj.PercentCompleteType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.StructuredNotes;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.FieldTypeHelper;
import net.sf.mpxj.common.HtmlHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.primavera.schema.APIBusinessObjects;
import net.sf.mpxj.primavera.schema.ActivityExpenseType;
import net.sf.mpxj.primavera.schema.ActivityNoteType;
import net.sf.mpxj.primavera.schema.ActivityType;
import net.sf.mpxj.primavera.schema.CalendarType;
import net.sf.mpxj.primavera.schema.CalendarType.HolidayOrExceptions;
import net.sf.mpxj.primavera.schema.CalendarType.HolidayOrExceptions.HolidayOrException;
import net.sf.mpxj.primavera.schema.CalendarType.StandardWorkWeek;
import net.sf.mpxj.primavera.schema.CalendarType.StandardWorkWeek.StandardWorkHours;
import net.sf.mpxj.primavera.schema.CostAccountType;
import net.sf.mpxj.primavera.schema.CurrencyType;
import net.sf.mpxj.primavera.schema.ExpenseCategoryType;
import net.sf.mpxj.primavera.schema.NotebookTopicType;
import net.sf.mpxj.primavera.schema.ObjectFactory;
import net.sf.mpxj.primavera.schema.ProjectNoteType;
import net.sf.mpxj.primavera.schema.ProjectType;
import net.sf.mpxj.primavera.schema.RelationshipType;
import net.sf.mpxj.primavera.schema.ResourceAssignmentType;
import net.sf.mpxj.primavera.schema.ResourceRateType;
import net.sf.mpxj.primavera.schema.ResourceType;
import net.sf.mpxj.primavera.schema.UDFAssignmentType;
import net.sf.mpxj.primavera.schema.UDFTypeType;
import net.sf.mpxj.primavera.schema.WBSType;
import net.sf.mpxj.primavera.schema.WorkTimeType;
import net.sf.mpxj.writer.AbstractProjectWriter;

/**
 * This class creates a new MSPDI file from the contents of an ProjectFile
 * instance.
 */
public final class PrimaveraPMFileWriter extends AbstractProjectWriter
{
   /**
    * {@inheritDoc}
    */
   @Override public void write(ProjectFile projectFile, OutputStream stream) throws IOException
   {
      try
      {
         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         //
         // The Primavera schema defines elements as nillable, which by
         // default results in
         // JAXB generating elements like this <element xsl:nil="true"/>
         // whereas Primavera itself simply omits these elements.
         //
         // The XSLT stylesheet below transforms the XML generated by JAXB on
         // the fly to remove any nil elements.
         //
         TransformerFactory transFact = TransformerFactory.newInstance();
         TransformerHandler handler = ((SAXTransformerFactory) transFact).newTransformerHandler(new StreamSource(new ByteArrayInputStream(NILLABLE_STYLESHEET.getBytes())));
         handler.setResult(new StreamResult(stream));
         Transformer transformer = handler.getTransformer();

         try
         {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
         }

         catch (Exception ex)
         {
            // https://sourceforge.net/p/mpxj/bugs/291/
            // Output indentation is a nice to have.
            // If we're working with a transformer which doesn't
            // support it, swallow any errors raised trying to configure it.
         }

         m_projectFile = projectFile;

         Marshaller marshaller = CONTEXT.createMarshaller();

         marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");

         m_factory = new ObjectFactory();
         m_apibo = m_factory.createAPIBusinessObjects();
         m_topics = new HashMap<>();
         m_activityTypePopulated = m_projectFile.getTasks().getPopulatedFields().contains(TaskField.ACTIVITY_TYPE);

         populateSortedCustomFieldsList();

         writeCurrency();
         writeUserFieldDefinitions();
         writeExpenseCategories();
         writeCostAccounts();
         writeProjectProperties();
         writeCalendars();
         writeResources();
         writeTasks();
         writeAssignments();
         writeExpenseItems();
         writeResourceRates();
         writeTopics();

         marshaller.marshal(m_apibo, handler);
      }

      catch (JAXBException ex)
      {
         throw new IOException(ex.toString());
      }

      catch (TransformerConfigurationException ex)
      {
         throw new IOException(ex.toString());
      }

      finally
      {
         m_projectFile = null;
         m_factory = null;
         m_apibo = null;
         m_project = null;
         m_wbsSequence = 0;
         m_relationshipObjectID = 0;
         m_rateObjectID = 0;
         m_wbsNoteObjectID = 0;
         m_activityNoteObjectID = 0;
         m_sortedCustomFieldsList = null;
         m_topics = null;
      }
   }

   /**
    * Create a handful of default currencies to keep Primavera happy.
    */
   private void writeCurrency()
   {
      ProjectProperties props = m_projectFile.getProjectProperties();
      CurrencyType currency = m_factory.createCurrencyType();
      m_apibo.getCurrency().add(currency);

      String positiveSymbol = getCurrencyFormat(props.getSymbolPosition());
      String negativeSymbol = "(" + positiveSymbol + ")";

      currency.setDecimalPlaces(props.getCurrencyDigits());
      currency.setDecimalSymbol(getSymbolName(props.getDecimalSeparator()));
      currency.setDigitGroupingSymbol(getSymbolName(props.getThousandsSeparator()));
      currency.setExchangeRate(Double.valueOf(1.0));
      currency.setId("CUR");
      currency.setName("Default Currency");
      currency.setNegativeSymbol(negativeSymbol);
      currency.setObjectId(DEFAULT_CURRENCY_ID);
      currency.setPositiveSymbol(positiveSymbol);
      currency.setSymbol(props.getCurrencySymbol());
   }

   /**
    * Map the currency separator character to a symbol name.
    *
    * @param c currency separator character
    * @return symbol name
    */
   private String getSymbolName(char c)
   {
      String result = null;

      switch (c)
      {
         case ',':
         {
            result = "Comma";
            break;
         }

         case '.':
         {
            result = "Period";
            break;
         }
      }

      return result;
   }

   /**
    * Generate a currency format.
    *
    * @param position currency symbol position
    * @return currency format
    */
   private String getCurrencyFormat(CurrencySymbolPosition position)
   {
      String result;

      switch (position)
      {
         case AFTER:
         {
            result = "1.1#";
            break;
         }

         case AFTER_WITH_SPACE:
         {
            result = "1.1 #";
            break;
         }

         case BEFORE_WITH_SPACE:
         {
            result = "# 1.1";
            break;
         }

         default:
         case BEFORE:
         {
            result = "#1.1";
            break;
         }
      }

      return result;
   }

   /**
    * Add UDFType objects to a PM XML file.
    *
    * @author kmahan
    * @date 2014-09-24
    * @author lsong
    * @date 2015-7-24
    */
   private void writeUserFieldDefinitions()
   {
      for (CustomField cf : m_sortedCustomFieldsList)
      {
         UDFTypeType udf = m_factory.createUDFTypeType();
         udf.setObjectId(Integer.valueOf(FieldTypeHelper.getFieldID(cf.getFieldType())));

         udf.setDataType(UserFieldDataType.inferUserFieldDataType(cf.getFieldType().getDataType()));
         udf.setSubjectArea(UserFieldDataType.inferUserFieldSubjectArea(cf.getFieldType()));
         udf.setTitle(cf.getAlias());
         m_apibo.getUDFType().add(udf);
      }
   }

   /**
    * Write expense categories.
    */
   private void writeExpenseCategories()
   {
      List<ExpenseCategoryType> expenseCategories = m_apibo.getExpenseCategory();
      for (ExpenseCategory category : m_projectFile.getExpenseCategories())
      {
         ExpenseCategoryType ect = m_factory.createExpenseCategoryType();
         ect.setObjectId(category.getUniqueID());
         ect.setName(category.getName());
         ect.setSequenceNumber(category.getSequence());
         expenseCategories.add(ect);
      }
   }

   /**
    * Write cost accounts.
    */
   private void writeCostAccounts()
   {
      List<CostAccountType> costAccounts = m_apibo.getCostAccount();
      for (CostAccount account : m_projectFile.getCostAccounts())
      {
         CostAccountType cat = m_factory.createCostAccountType();
         cat.setObjectId(account.getUniqueID());
         cat.setId(account.getID());
         cat.setName(account.getName());
         cat.setDescription(account.getDescription());
         cat.setSequenceNumber(account.getSequence());

         if (account.getParent() != null)
         {
            cat.setParentObjectId(account.getParent().getUniqueID());
         }

         costAccounts.add(cat);
      }
   }

   /**
    * This method writes project properties data to a PM XML file.
    */
   private void writeProjectProperties()
   {
      m_project = m_factory.createProjectType();
      m_apibo.getProject().add(m_project);

      ProjectProperties mpxj = m_projectFile.getProjectProperties();
      Task rootTask = m_projectFile.getTaskByUniqueID(Integer.valueOf(0));
      UUID guid = rootTask == null ? null : rootTask.getGUID();

      m_project.setActivityDefaultActivityType("Task Dependent");
      m_project.setActivityDefaultCalendarObjectId(getCalendarUniqueID(m_projectFile.getDefaultCalendar()));
      m_project.setActivityDefaultDurationType("Fixed Duration and Units");
      m_project.setActivityDefaultPercentCompleteType("Duration");
      m_project.setActivityDefaultPricePerUnit(NumberHelper.DOUBLE_ZERO);
      m_project.setActivityIdBasedOnSelectedActivity(Boolean.TRUE);
      m_project.setActivityIdIncrement(Integer.valueOf(10));
      m_project.setActivityIdPrefix("A");
      m_project.setActivityIdSuffix(Integer.valueOf(1000));
      m_project.setActivityPercentCompleteBasedOnActivitySteps(Boolean.FALSE);
      m_project.setAddActualToRemaining(Boolean.FALSE);
      m_project.setAllowNegativeActualUnitsFlag(Boolean.FALSE);
      m_project.setAssignmentDefaultDrivingFlag(Boolean.TRUE);
      m_project.setAssignmentDefaultRateType("Price / Unit");
      m_project.setCheckOutStatus(Boolean.FALSE);
      m_project.setCostQuantityRecalculateFlag(Boolean.FALSE);
      m_project.setCreateDate(mpxj.getCreationDate());
      m_project.setCriticalActivityFloatLimit(NumberHelper.DOUBLE_ZERO);
      m_project.setCriticalActivityPathType("Critical Float");
      m_project.setDataDate(m_projectFile.getProjectProperties().getStatusDate());
      m_project.setDefaultPriceTimeUnits("Hour");
      m_project.setDiscountApplicationPeriod("Month");
      m_project.setEarnedValueComputeType("Activity Percent Complete");
      m_project.setEarnedValueETCComputeType("ETC = Remaining Cost for Activity");
      m_project.setEarnedValueETCUserValue(Double.valueOf(0.88));
      m_project.setEarnedValueUserPercent(Double.valueOf(0.06));
      m_project.setEnableSummarization(Boolean.TRUE);
      m_project.setFiscalYearStartMonth(Integer.valueOf(1));
      m_project.setFinishDate(mpxj.getFinishDate());
      m_project.setGUID(DatatypeConverter.printUUID(guid));
      m_project.setId(PROJECT_ID);
      m_project.setLastUpdateDate(mpxj.getLastSaved());
      m_project.setLevelingPriority(Integer.valueOf(10));
      m_project.setLinkActualToActualThisPeriod(Boolean.TRUE);
      m_project.setLinkPercentCompleteWithActual(Boolean.TRUE);
      m_project.setLinkPlannedAndAtCompletionFlag(Boolean.TRUE);
      m_project.setName(mpxj.getName() == null ? PROJECT_ID : mpxj.getName());
      m_project.setObjectId(PROJECT_OBJECT_ID);
      m_project.setPlannedStartDate(mpxj.getStartDate());
      m_project.setPrimaryResourcesCanMarkActivitiesAsCompleted(Boolean.TRUE);
      m_project.setResetPlannedToRemainingFlag(Boolean.FALSE);
      m_project.setResourceCanBeAssignedToSameActivityMoreThanOnce(Boolean.TRUE);
      m_project.setResourcesCanAssignThemselvesToActivities(Boolean.TRUE);
      m_project.setResourcesCanEditAssignmentPercentComplete(Boolean.FALSE);
      m_project.setResourcesCanMarkAssignmentAsCompleted(Boolean.FALSE);
      m_project.setResourcesCanViewInactiveActivities(Boolean.FALSE);
      m_project.setRiskLevel("Medium");
      m_project.setStartDate(mpxj.getStartDate());
      m_project.setStatus("Active");
      m_project.setStrategicPriority(Integer.valueOf(500));
      m_project.setSummarizeToWBSLevel(Integer.valueOf(2));
      m_project.setSummaryLevel("Assignment Level");
      m_project.setUseProjectBaselineForEarnedValue(Boolean.TRUE);
      m_project.setWBSCodeSeparator(PrimaveraReader.DEFAULT_WBS_SEPARATOR);
      m_project.getUDF().addAll(writeUDFType(FieldTypeClass.PROJECT, mpxj));
   }

   /**
    * This method writes calendar data to a PM XML file.
    */
   private void writeCalendars()
   {
      for (ProjectCalendar calendar : m_projectFile.getCalendars())
      {
         writeCalendar(calendar);
      }
   }

   /**
    * This method writes data for an individual calendar to a PM XML file.
    *
    * @param mpxj ProjectCalander instance
    */
   private void writeCalendar(ProjectCalendar mpxj)
   {
      CalendarType xml = m_factory.createCalendarType();
      m_apibo.getCalendar().add(xml);
      String type = mpxj.getResource() == null ? "Global" : "Resource";

      xml.setBaseCalendarObjectId(getCalendarUniqueID(mpxj.getParent()));
      xml.setIsDefault(Boolean.valueOf(mpxj == m_projectFile.getDefaultCalendar()));
      xml.setIsPersonal(mpxj.getResource() == null ? Boolean.FALSE : Boolean.TRUE);
      xml.setName(mpxj.getName());
      xml.setObjectId(mpxj.getUniqueID());
      xml.setType(type);

      xml.setHoursPerDay(Double.valueOf(mpxj.getMinutesPerDay() / 60.0));
      xml.setHoursPerWeek(Double.valueOf(mpxj.getMinutesPerWeek() / 60.0));
      xml.setHoursPerMonth(Double.valueOf(mpxj.getMinutesPerMonth() / 60.0));
      xml.setHoursPerYear(Double.valueOf(mpxj.getMinutesPerYear() / 60.0));

      StandardWorkWeek xmlStandardWorkWeek = m_factory.createCalendarTypeStandardWorkWeek();
      xml.setStandardWorkWeek(xmlStandardWorkWeek);

      for (Day day : EnumSet.allOf(Day.class))
      {
         StandardWorkHours xmlHours = m_factory.createCalendarTypeStandardWorkWeekStandardWorkHours();
         xmlStandardWorkWeek.getStandardWorkHours().add(xmlHours);
         xmlHours.setDayOfWeek(getDayName(day));

         for (DateRange range : mpxj.getHours(day))
         {
            WorkTimeType xmlWorkTime = m_factory.createWorkTimeType();
            xmlHours.getWorkTime().add(xmlWorkTime);

            xmlWorkTime.setStart(range.getStart());
            xmlWorkTime.setFinish(getEndTime(range.getEnd()));
         }
      }

      HolidayOrExceptions xmlExceptions = m_factory.createCalendarTypeHolidayOrExceptions();
      xml.setHolidayOrExceptions(xmlExceptions);

      if (!mpxj.getCalendarExceptions().isEmpty())
      {
         Calendar calendar = DateHelper.popCalendar();
         for (ProjectCalendarException mpxjException : mpxj.getCalendarExceptions())
         {
            calendar.setTime(mpxjException.getFromDate());
            while (calendar.getTimeInMillis() < mpxjException.getToDate().getTime())
            {
               HolidayOrException xmlException = m_factory.createCalendarTypeHolidayOrExceptionsHolidayOrException();
               xmlExceptions.getHolidayOrException().add(xmlException);

               xmlException.setDate(calendar.getTime());

               for (DateRange range : mpxjException)
               {
                  WorkTimeType xmlHours = m_factory.createWorkTimeType();
                  xmlException.getWorkTime().add(xmlHours);

                  xmlHours.setStart(range.getStart());

                  if (range.getEnd() != null)
                  {
                     xmlHours.setFinish(getEndTime(range.getEnd()));
                  }
               }
               calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
         }
         DateHelper.pushCalendar(calendar);
      }
   }

   /**
    * This method writes resource data to a PM XML file.
    */
   private void writeResources()
   {
      for (Resource resource : m_projectFile.getResources())
      {
         if (resource.getUniqueID().intValue() != 0)
         {
            writeResource(resource);
         }
      }
   }

   /**
    * Write a single resource.
    *
    * @param mpxj Resource instance
    */
   private void writeResource(Resource mpxj)
   {
      ResourceType xml = m_factory.createResourceType();
      m_apibo.getResource().add(xml);

      xml.setAutoComputeActuals(Boolean.TRUE);
      xml.setCalculateCostFromUnits(Boolean.TRUE);
      xml.setCalendarObjectId(getCalendarUniqueID(mpxj.getResourceCalendar()));
      xml.setCurrencyObjectId(DEFAULT_CURRENCY_ID);
      xml.setDefaultUnitsPerTime(Double.valueOf(1.0));
      xml.setEmailAddress(mpxj.getEmailAddress());
      xml.setEmployeeId(mpxj.getCode());
      xml.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      xml.setId(getResourceID(mpxj));
      xml.setIsActive(Boolean.TRUE);
      xml.setMaxUnitsPerTime(getPercentage(mpxj.getMaxUnits()));
      xml.setName(mpxj.getName());
      xml.setObjectId(mpxj.getUniqueID());
      xml.setParentObjectId(mpxj.getParentID());
      xml.setResourceNotes(getResourceNotes(mpxj.getNotesObject()));
      xml.setResourceType(getResourceType(mpxj));
      xml.getUDF().addAll(writeUDFType(FieldTypeClass.RESOURCE, mpxj));
   }

   /**
    * Retrieve the resource notes text. If an HTML representation
    * is already available, use that, otherwise generate HTML from
    * the plain text of the note.
    *
    * @param notes notes text
    * @return Notes instance
    */
   private String getResourceNotes(Notes notes)
   {
      String result;
      if (notes == null || notes.isEmpty())
      {
         // TODO: switch to null to remove the tag - check import
         result = "";
      }
      else
      {
         result = notes instanceof HtmlNotes ? ((HtmlNotes) notes).getHtml() : HtmlHelper.getHtmlFromPlainText(notes.toString());
      }

      return result;
   }

   /**
    * This method writes task data to a PM XML file.
    *
    */
   private void writeTasks()
   {
      List<Task> tasks = new ArrayList<>(m_projectFile.getTasks());
      Collections.sort(tasks, (t1, t2) -> NumberHelper.compare(t1.getUniqueID(), t2.getUniqueID()));

      for (Task task : tasks)
      {
         writeTask(task);
      }
   }

   /**
    * Given a Task instance, this task determines if it should be written to the
    * PM XML file as an activity or as a WBS item, and calls the appropriate
    * method.
    *
    * @param task Task instance
    */
   private void writeTask(Task task)
   {
      if (!task.getNull())
      {
         // If it's a summary task... it's a WBS entry
         // If the task has come from P6, and the activity type is not set, its a WBS entry
         if (task.getSummary() || (m_activityTypePopulated && task.getActivityType() == null))
         {
            writeWBS(task);
         }
         else
         {
            writeActivity(task);
         }
      }
   }

   /**
    * Writes a WBS entity to the PM XML file.
    *
    * @param mpxj MPXJ Task entity
    */
   private void writeWBS(Task mpxj)
   {
      if (mpxj.getUniqueID().intValue() != 0)
      {
         WBSType xml = m_factory.createWBSType();
         m_project.getWBS().add(xml);

         Task parentTask = mpxj.getParentTask();
         Integer parentObjectID = parentTask == null ? null : parentTask.getUniqueID();

         xml.setCode(getWbsCode(mpxj));
         xml.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
         xml.setName(mpxj.getName());

         xml.setObjectId(mpxj.getUniqueID());
         xml.setParentObjectId(parentObjectID);
         xml.setProjectObjectId(PROJECT_OBJECT_ID);
         xml.setSequenceNumber(Integer.valueOf(m_wbsSequence++));

         xml.setStatus("Active");

         writeWbsNote(mpxj);
      }
   }

   /**
    * Retrieve the WBS code attribute.
    *
    * @param task Task instance
    * @return WBS code attribute
    */
   private String getWbsCode(Task task)
   {
      // If we don't have a WBS code, use a default value
      String code = task.getWBS();
      if (code == null || code.length() == 0)
      {
         code = DEFAULT_WBS_CODE;
      }
      else
      {
         if (task.getParentTask() != null)
         {
            // If we have a parent task, and it looks like WBS contains the full path
            // (including the parent's WBS), remove the parent's WBS. This matches
            // how P6 exports this value. This test is brittle as it assumes the
            // the default WBS separator has been used.
            String parentWbs = task.getParentTask().getWBS() + PrimaveraReader.DEFAULT_WBS_SEPARATOR;
            if (code.startsWith(parentWbs))
            {
               code = code.substring(parentWbs.length());
            }
         }
      }
      return code;
   }

   /**
    * Writes an activity to a PM XML file.
    *
    * @param mpxj MPXJ Task instance
    */
   private void writeActivity(Task mpxj)
   {
      ActivityType xml = m_factory.createActivityType();
      m_project.getActivity().add(xml);

      Task parentTask = mpxj.getParentTask();
      Integer parentObjectID = parentTask == null ? null : parentTask.getUniqueID();

      // Not required, but keeps Asta import happy if we ensure that planned start and finish are populated.
      Date plannedStart = mpxj.getPlannedStart() == null ? mpxj.getStart() : mpxj.getPlannedStart();
      Date plannedFinish = mpxj.getPlannedFinish() == null ? mpxj.getFinish() : mpxj.getPlannedFinish();

      xml.setActualStartDate(mpxj.getActualStart());
      xml.setActualDuration(getDuration(mpxj.getActualDuration()));
      xml.setActualFinishDate(mpxj.getActualFinish());
      xml.setAtCompletionDuration(getDuration(mpxj.getDuration()));
      xml.setCalendarObjectId(getCalendarUniqueID(mpxj.getCalendar()));
      xml.setDurationPercentComplete(getPercentage(mpxj.getPercentageComplete()));
      xml.setDurationType(DURATION_TYPE_MAP.get(mpxj.getType()));
      xml.setFinishDate(mpxj.getFinish());
      xml.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      xml.setId(mpxj.getActivityID() == null ? mpxj.getWBS() : mpxj.getActivityID());
      xml.setName(mpxj.getName());
      xml.setObjectId(mpxj.getUniqueID());
      xml.setPercentCompleteType(getPercentCompleteType(mpxj.getPercentCompleteType()));
      xml.setPercentComplete(getPercentComplete(mpxj));
      xml.setPhysicalPercentComplete(getPercentage(mpxj.getPhysicalPercentComplete()));
      xml.setPrimaryConstraintType(CONSTRAINT_TYPE_MAP.get(mpxj.getConstraintType()));
      xml.setPrimaryConstraintDate(mpxj.getConstraintDate());
      xml.setPrimaryResourceObjectId(mpxj.getPrimaryResourceID());
      xml.setPlannedDuration(getDuration(mpxj.getPlannedDuration() == null ? mpxj.getDuration() : mpxj.getPlannedDuration()));
      xml.setPlannedFinishDate(plannedFinish);
      xml.setPlannedStartDate(plannedStart);
      xml.setProjectObjectId(PROJECT_OBJECT_ID);
      xml.setRemainingDuration(getDuration(mpxj.getRemainingDuration()));
      xml.setRemainingLaborCost(NumberHelper.DOUBLE_ZERO);
      xml.setRemainingLaborUnits(NumberHelper.DOUBLE_ZERO);
      xml.setRemainingNonLaborCost(NumberHelper.DOUBLE_ZERO);
      xml.setRemainingNonLaborUnits(NumberHelper.DOUBLE_ZERO);

      // Trying to ensure data from other scheduling applications makes sense in P6.
      // We won't populate the resume date unless we have a suspend date,
      // i.e. the activity has been suspended. 
      if (mpxj.getSuspendDate() != null)
      {
         xml.setResumeDate(mpxj.getResume());
      }

      xml.setStartDate(mpxj.getStart());
      xml.setStatus(getActivityStatus(mpxj));
      xml.setSuspendDate(mpxj.getSuspendDate());
      xml.setType(getTaskType(mpxj));
      xml.setUnitsPercentComplete(getPercentage(mpxj.getPercentageWorkComplete()));
      xml.setWBSObjectId(parentObjectID);
      xml.getUDF().addAll(writeUDFType(FieldTypeClass.TASK, mpxj));

      writeActivityNote(mpxj);
      writePredecessors(mpxj);
   }

   /**
    * Attempts to locate the activity type value extracted from an existing P6 schedule.
    * If present, we assume the value is valid.
    * Returns "Resource Dependent" as the default value.
    *
    * @param task parent task
    * @return activity type
    */
   private String getTaskType(Task task)
   {
      return task.getActivityType() == null ? "Resource Dependent" : ACTIVITY_TYPE_MAP.get(task.getActivityType());
   }

   /**
    * Writes assignment data to a PM XML file.
    */
   private void writeAssignments()
   {
      List<ResourceAssignment> assignments = new ArrayList<>();
      m_projectFile.getTasks().forEach(t -> assignments.addAll(t.getResourceAssignments()));

      for (ResourceAssignment assignment : assignments)
      {
         Resource resource = assignment.getResource();
         if (resource != null)
         {
            Task task = assignment.getTask();
            if (task != null && task.getUniqueID().intValue() != 0 && !task.getSummary())
            {
               writeAssignment(assignment);
            }
         }
      }
   }

   /**
    * Writes a resource assignment to a PM XML file.
    *
    * @param mpxj MPXJ ResourceAssignment instance
    */
   private void writeAssignment(ResourceAssignment mpxj)
   {
      ResourceAssignmentType xml = m_factory.createResourceAssignmentType();
      m_project.getResourceAssignment().add(xml);
      Task task = mpxj.getTask();
      Task parentTask = task.getParentTask();
      Integer parentTaskUniqueID = parentTask == null ? null : parentTask.getUniqueID();

      xml.setActivityObjectId(mpxj.getTaskUniqueID());
      xml.setActualCost(getDouble(mpxj.getActualCost()));
      xml.setActualFinishDate(mpxj.getActualFinish());
      xml.setActualOvertimeCost(getDouble(mpxj.getActualOvertimeCost()));
      xml.setActualOvertimeUnits(getDuration(mpxj.getActualOvertimeWork()));
      xml.setActualRegularUnits(getDuration(mpxj.getActualWork()));
      xml.setActualStartDate(mpxj.getActualStart());
      xml.setActualUnits(getDuration(mpxj.getActualWork()));
      xml.setAtCompletionCost(NumberHelper.sumAsDouble(mpxj.getActualCost(), mpxj.getRemainingCost()));
      xml.setAtCompletionUnits(getDuration(Duration.add(mpxj.getActualWork(), mpxj.getRemainingWork(), m_projectFile.getProjectProperties())));
      xml.setFinishDate(mpxj.getFinish());
      xml.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      xml.setObjectId(mpxj.getUniqueID());
      xml.setPlannedCost(getDouble(mpxj.getPlannedCost()));
      xml.setPlannedFinishDate(mpxj.getPlannedFinish());
      xml.setPlannedStartDate(mpxj.getPlannedStart());
      xml.setPlannedUnits(getDuration(mpxj.getPlannedWork()));
      xml.setPlannedUnitsPerTime(getPercentage(mpxj.getUnits()));
      xml.setProjectObjectId(PROJECT_OBJECT_ID);
      xml.setRateSource("Resource");
      xml.setRemainingCost(getDouble(mpxj.getRemainingCost()));
      xml.setRemainingDuration(getDuration(mpxj.getRemainingWork()));
      xml.setRemainingUnits(getDuration(mpxj.getRemainingWork()));
      xml.setRemainingUnitsPerTime(getPercentage(mpxj.getUnits()));
      xml.setResourceObjectId(mpxj.getResourceUniqueID());
      xml.setStartDate(mpxj.getStart());
      xml.setWBSObjectId(parentTaskUniqueID);
      xml.getUDF().addAll(writeUDFType(FieldTypeClass.ASSIGNMENT, mpxj));
   }

   /**
    * Writes task predecessor links to a PM XML file.
    *
    * @param task MPXJ Task instance
    */
   private void writePredecessors(Task task)
   {
      List<Relation> relations = task.getPredecessors();
      for (Relation mpxj : relations)
      {
         RelationshipType xml = m_factory.createRelationshipType();
         m_project.getRelationship().add(xml);

         xml.setLag(getDuration(mpxj.getLag()));
         xml.setObjectId(Integer.valueOf(++m_relationshipObjectID));
         xml.setPredecessorActivityObjectId(mpxj.getTargetTask().getUniqueID());
         xml.setSuccessorActivityObjectId(mpxj.getSourceTask().getUniqueID());
         xml.setPredecessorProjectObjectId(PROJECT_OBJECT_ID);
         xml.setSuccessorProjectObjectId(PROJECT_OBJECT_ID);
         xml.setType(RELATION_TYPE_MAP.get(mpxj.getType()));
      }
   }

   /**
    * Write all expense items for project.
    */
   private void writeExpenseItems()
   {
      List<Task> tasks = new ArrayList<>(m_projectFile.getTasks());
      Collections.sort(tasks, (t1, t2) -> NumberHelper.compare(t1.getUniqueID(), t2.getUniqueID()));

      tasks.forEach(t -> writeExpenseItems(t));
   }

   /**
    * Write expense items for a task.
    *
    * @param task Task instance
    */
   private void writeExpenseItems(Task task)
   {
      List<ExpenseItem> items = task.getExpenseItems();
      if (items != null && !items.isEmpty())
      {
         List<ExpenseItem> expenseItems = new ArrayList<>(items);
         expenseItems.sort((i1, i2) -> NumberHelper.compare(i1.getUniqueID(), i2.getUniqueID()));

         List<ActivityExpenseType> expenses = m_project.getActivityExpense();
         Integer parentObjectID = task.getParentTask() == null ? null : task.getParentTask().getUniqueID();

         for (ExpenseItem item : expenseItems)
         {
            ActivityExpenseType expense = m_factory.createActivityExpenseType();
            expenses.add(expense);

            expense.setAccrualType(ACCRUE_TYPE_MAP.get(item.getAccrueType()));
            //expense.setActivityId(value);
            //expense.setActivityName(value);
            expense.setActivityObjectId(task.getUniqueID());
            expense.setActualCost(item.getActualCost());
            expense.setActualUnits(item.getActualUnits());
            //expense.setAtCompletionCost(item.getAtCompletionCost());
            //expense.setAtCompletionUnits(item.getAtCompletionUnits());
            expense.setAutoComputeActuals(Boolean.valueOf(item.getAutoComputeActuals()));
            //expense.setCBSCode(value);
            //expense.setCBSId(value);

            if (item.getAccount() != null)
            {
               expense.setCostAccountObjectId(item.getAccount().getUniqueID());
            }

            //expense.setCreateDate(value);
            //expense.setCreateUser(value);
            expense.setDocumentNumber(item.getDocumentNumber());

            if (item.getCategory() != null)
            {
               expense.setExpenseCategoryObjectId(item.getCategory().getUniqueID());
            }

            expense.setExpenseDescription(item.getDescription());

            expense.setExpenseItem(item.getName());
            //expense.setExpensePercentComplete(value);
            //expense.setIsBaseline(value);
            //expense.setIsTemplate(value);
            //expense.setLastUpdateDate(value);
            //expense.setLastUpdateUser(value);
            expense.setObjectId(item.getUniqueID());
            expense.setPlannedCost(item.getPlannedCost());
            expense.setPlannedUnits(item.getPlannedUnits());
            expense.setPricePerUnit(item.getPricePerUnit());
            //expense.setProjectId(PROJECT_ID);
            expense.setProjectObjectId(PROJECT_OBJECT_ID);
            expense.setRemainingCost(item.getRemainingCost());
            expense.setRemainingUnits(item.getRemainingUnits());
            expense.setUnitOfMeasure(item.getUnitOfMeasure());
            expense.setVendor(item.getVendor());
            expense.setWBSObjectId(parentObjectID);
         }
      }
   }

   /**
    * Write rate information for each resource.
    */
   private void writeResourceRates()
   {
      m_projectFile.getResources().forEach(r -> writeResourceRates(r));
   }

   /**
    * Write rate information for a single resource.
    *
    * @param resource Resource instance
    */
   private void writeResourceRates(Resource resource)
   {
      CostRateTable table = resource.getCostRateTable(0);
      AvailabilityTable availabilityTable = resource.getAvailability();

      if (table != null)
      {
         for (CostRateTableEntry entry : table)
         {
            if (costRateTableWriteRequired(entry))
            {
               Availability availability = availabilityTable.getEntryByDate(entry.getStartDate());
               Double maxUnits = availability == null ? Double.valueOf(1) : Double.valueOf(availability.getUnits().doubleValue() / 100.0);

               ResourceRateType rate = m_factory.createResourceRateType();
               m_apibo.getResourceRate().add(rate);

               //rate.setCreateDate(value);
               //rate.setCreateUser(value);
               rate.setEffectiveDate(entry.getStartDate());
               //rate.setLastUpdateDate(value);
               //rate.setLastUpdateUser(value);
               rate.setMaxUnitsPerTime(maxUnits);
               rate.setObjectId(Integer.valueOf(++m_rateObjectID));
               rate.setPricePerUnit(Double.valueOf(entry.getStandardRate().getAmount()));
               //rate.setPricePerUnit2(value);
               //rate.setPricePerUnit3(value);
               //rate.setPricePerUnit4(value);
               //rate.setPricePerUnit5(value);
               //rate.setResourceId(value);
               //rate.setResourceName(value);
               rate.setResourceObjectId(resource.getUniqueID());
               //rate.setShiftPeriodObjectId(value);
            }
         }
      }
   }

   /**
    * This method determines whether the cost rate table should be written.
    * A default cost rate table should not be written to the file.
    *
    * @param entry cost rate table entry
    * @return boolean flag
    */
   private boolean costRateTableWriteRequired(CostRateTableEntry entry)
   {
      boolean fromDate = (DateHelper.compare(entry.getStartDate(), DateHelper.START_DATE_NA) > 0);
      boolean toDate = (DateHelper.compare(entry.getEndDate(), DateHelper.END_DATE_NA) > 0);
      boolean overtimeRate = (entry.getOvertimeRate() != null && entry.getOvertimeRate().getAmount() != 0);
      boolean standardRate = (entry.getStandardRate() != null && entry.getStandardRate().getAmount() != 0);
      return (fromDate || toDate || overtimeRate || standardRate);
   }

   /**
    * Write any notebook topics used by this schedule.
    */
   private void writeTopics()
   {
      int sequenceNumber = 1;
      for (Map.Entry<Integer, String> entry : m_topics.entrySet())
      {
         NotebookTopicType topic = m_factory.createNotebookTopicType();
         m_apibo.getNotebookTopic().add(topic);

         topic.setAvailableForActivity(Boolean.TRUE);
         topic.setAvailableForWBS(Boolean.TRUE);
         topic.setName(entry.getValue());
         topic.setObjectId(entry.getKey());
         topic.setSequenceNumber(Integer.valueOf(sequenceNumber++));
      }
   }

   /**
    * Write notes for a WBS entry.
    *
    * @param task WBS entry.
    */
   private void writeWbsNote(Task task)
   {
      String notes = task.getNotes();
      if (notes.isEmpty())
      {
         return;
      }

      if (notesAreNativeFormat(task.getNotesObject()))
      {
         writeNativeWbsNote(task);
      }
      else
      {
         writeDefaultWbsNote(task);
      }
   }

   /**
    * Generate a notebook entry from plain text.
    *
    * @param task WBS entry
    */
   private void writeDefaultWbsNote(Task task)
   {
      ProjectNoteType xml = m_factory.createProjectNoteType();
      m_project.getProjectNote().add(xml);

      m_topics.put(NOTEBOOK_TOPIC_OBJECT_ID, "Notes");
      xml.setNote(HtmlHelper.getHtmlFromPlainText(task.getNotes()));
      xml.setNotebookTopicObjectId(NOTEBOOK_TOPIC_OBJECT_ID);
      xml.setObjectId(Integer.valueOf(++m_wbsNoteObjectID));
      xml.setProjectObjectId(PROJECT_OBJECT_ID);
      xml.setWBSObjectId(task.getUniqueID());
   }

   /**
    * Generate notebook entries from structured notes.
    *
    * @param task WBS entry
    */
   private void writeNativeWbsNote(Task task)
   {
      for (Notes note : ((ParentNotes) task.getNotesObject()).getChildNotes())
      {
         StructuredNotes structuredNotes = (StructuredNotes) note;
         HtmlNotes htmlNotes = (HtmlNotes) structuredNotes.getNotes();

         ProjectNoteType xml = m_factory.createProjectNoteType();
         m_project.getProjectNote().add(xml);

         m_topics.put(structuredNotes.getTopicID(), structuredNotes.getTopicName());
         xml.setNote(htmlNotes.getHtml());
         xml.setNotebookTopicObjectId(structuredNotes.getTopicID());
         xml.setObjectId(Integer.valueOf(++m_wbsNoteObjectID));
         xml.setProjectObjectId(PROJECT_OBJECT_ID);
         xml.setWBSObjectId(task.getUniqueID());
      }
   }

   /**
    * Write notes for an Activity entry.
    *
    * @param task activity entry.
    */
   private void writeActivityNote(Task task)
   {
      String notes = task.getNotes();
      if (notes.isEmpty())
      {
         return;
      }

      if (notesAreNativeFormat(task.getNotesObject()))
      {
         writeNativeActivityNote(task);
      }
      else
      {
         writeDefaultActivityNote(task);
      }
   }

   /**
    * Generate a notebook entry from plain text.
    *
    * @param task activity entry
    */
   private void writeDefaultActivityNote(Task task)
   {
      ActivityNoteType xml = m_factory.createActivityNoteType();
      m_project.getActivityNote().add(xml);

      m_topics.put(NOTEBOOK_TOPIC_OBJECT_ID, "Notes");
      xml.setNote(HtmlHelper.getHtmlFromPlainText(task.getNotes()));
      xml.setNotebookTopicObjectId(NOTEBOOK_TOPIC_OBJECT_ID);
      xml.setObjectId(Integer.valueOf(++m_activityNoteObjectID));
      xml.setProjectObjectId(PROJECT_OBJECT_ID);
      xml.setActivityObjectId(task.getUniqueID());
   }

   /**
    * Generate notebook entries from structured notes.
    *
    * @param task activity entry
    */
   private void writeNativeActivityNote(Task task)
   {
      for (Notes note : ((ParentNotes) task.getNotesObject()).getChildNotes())
      {
         StructuredNotes structuredNotes = (StructuredNotes) note;
         HtmlNotes htmlNotes = (HtmlNotes) structuredNotes.getNotes();

         ActivityNoteType xml = m_factory.createActivityNoteType();
         m_project.getActivityNote().add(xml);

         m_topics.put(structuredNotes.getTopicID(), structuredNotes.getTopicName());
         xml.setNote(htmlNotes.getHtml());
         xml.setNotebookTopicObjectId(structuredNotes.getTopicID());
         xml.setObjectId(Integer.valueOf(++m_activityNoteObjectID));
         xml.setProjectObjectId(PROJECT_OBJECT_ID);
         xml.setActivityObjectId(task.getUniqueID());
      }
   }

   /**
    * Returns true if the notes are in a form that can be exported
    * as P6 notepad entries.
    *
    * @param notes Notes instance
    * @return true if the notes can be exported as notepad entries
    */
   private boolean notesAreNativeFormat(Notes notes)
   {
      return notes instanceof ParentNotes && ((ParentNotes) notes).getChildNotes().stream().allMatch(n -> n instanceof StructuredNotes && ((StructuredNotes) n).getNotes() instanceof HtmlNotes);
   }

   /**
    * Writes a list of UDF types.
    *
    * @author lsong
    * @param type parent entity type
    * @param mpxj parent entity
    * @return list of UDFAssignmentType instances
    */
   private List<UDFAssignmentType> writeUDFType(FieldTypeClass type, FieldContainer mpxj)
   {
      List<UDFAssignmentType> out = new ArrayList<>();
      for (CustomField cf : m_sortedCustomFieldsList)
      {
         FieldType fieldType = cf.getFieldType();
         if (type == fieldType.getFieldTypeClass())
         {
            Object value = mpxj.getCachedValue(fieldType);
            if (FieldTypeHelper.valueIsNotDefault(fieldType, value))
            {
               UDFAssignmentType udf = m_factory.createUDFAssignmentType();
               udf.setTypeObjectId(FieldTypeHelper.getFieldID(fieldType));
               setUserFieldValue(udf, fieldType.getDataType(), value);
               out.add(udf);
            }
         }
      }
      return out;
   }

   /**
    * Sets the value of a UDF.
    *
    * @param udf user defined field
    * @param dataType MPXJ data type
    * @param value field value
    */
   private void setUserFieldValue(UDFAssignmentType udf, DataType dataType, Object value)
   {
      switch (dataType)
      {
         case DURATION:
         {
            udf.setTextValue(((Duration) value).toString());
            break;
         }

         case CURRENCY:
         {
            if (!(value instanceof Double))
            {
               value = Double.valueOf(((Number) value).doubleValue());
            }
            udf.setCostValue((Double) value);
            break;
         }

         case BINARY:
         {
            udf.setTextValue("");
            break;
         }

         case STRING:
         {
            udf.setTextValue((String) value);
            break;
         }

         case DATE:
         {
            udf.setStartDateValue((Date) value);
            break;
         }

         case NUMERIC:
         {
            if (!(value instanceof Double))
            {
               value = Double.valueOf(((Number) value).doubleValue());
            }
            udf.setDoubleValue((Double) value);
            break;
         }

         case BOOLEAN:
         {
            udf.setIntegerValue(BooleanHelper.getBoolean((Boolean) value) ? Integer.valueOf(1) : Integer.valueOf(0));
            break;
         }

         case INTEGER:
         case SHORT:
         {
            udf.setIntegerValue(NumberHelper.getInteger((Number) value));
            break;
         }

         default:
         {
            throw new RuntimeException("Unconvertible data type: " + dataType);
         }
      }
   }

   /**
    * Retrieve a duration in the form required by Primavera.
    *
    * @param duration Duration instance
    * @return formatted duration
    */
   private Double getDuration(Duration duration)
   {
      Double result;
      if (duration == null)
      {
         result = null;
      }
      else
      {
         if (duration.getUnits() != TimeUnit.HOURS)
         {
            duration = duration.convertUnits(TimeUnit.HOURS, m_projectFile.getProjectProperties());
         }

         result = Double.valueOf(duration.getDuration());
      }
      return result;
   }

   /**
    * Formats a day name.
    *
    * @param day MPXJ Day instance
    * @return Primavera day instance
    */
   private String getDayName(Day day)
   {
      return DAY_NAMES[day.getValue() - 1];
   }

   /**
    * Formats a resource type.
    *
    * @param resource MPXJ resource
    * @return Primavera resource type
    */
   private String getResourceType(Resource resource)
   {
      String result;
      net.sf.mpxj.ResourceType type = resource.getType();
      if (type == null)
      {
         type = net.sf.mpxj.ResourceType.WORK;
      }

      switch (type)
      {
         case MATERIAL:
         {
            result = "Material";
            break;
         }

         case COST:
         {
            result = "Nonlabor";
            break;
         }

         default:
         {
            result = "Labor";
            break;
         }
      }

      return result;
   }

   /**
    * Formats a percentage value.
    *
    * @param number MPXJ percentage value
    * @return Primavera percentage value
    */
   private Double getPercentage(Number number)
   {
      Double result = null;

      if (number != null)
      {
         result = Double.valueOf(number.doubleValue() / 100);
      }

      return result;
   }

   private String getPercentCompleteType(PercentCompleteType value)
   {
      return PERCENT_COMPLETE_TYPE.get(value == null ? PercentCompleteType.DURATION : value);
   }

   /**
    * Returns the reported percent complete value for this task.
    *
    * @param task task
    * @return percent complete value
    */
   private Double getPercentComplete(Task task)
   {
      Number result;
      PercentCompleteType type = task.getPercentCompleteType();

      if (type == null)
      {
         type = PercentCompleteType.DURATION;
      }

      switch (type)
      {
         case PHYSICAL:
         {
            result = task.getPhysicalPercentComplete();
            break;
         }

         case UNITS:
         {
            result = task.getPercentageWorkComplete();
            break;
         }

         case DURATION:
         case SCOPE:
         default:
         {
            result = task.getPercentageComplete();
            break;
         }
      }

      return getPercentage(result);
   }

   /**
    * Formats a double value.
    *
    * @param number numeric value
    * @return Double instance
    */
   private Double getDouble(Number number)
   {
      Double result = null;

      if (number != null)
      {
         result = Double.valueOf(number.doubleValue());
      }

      return result;
   }

   /**
    * The end of a Primavera time range finishes on the last minute
    * of the period, so a range of 12:00 -> 13:00 is represented by
    * Primavera as 12:00 -> 12:59.
    *
    * @param date MPXJ end time
    * @return Primavera end time
    */
   private Date getEndTime(Date date)
   {
      return new Date(date.getTime() - 60000);
   }

   /**
    * Retrieve a calendar unique ID.
    *
    * @param calendar ProjectCalendar instance
    * @return calendar unique ID
    */
   private Integer getCalendarUniqueID(ProjectCalendar calendar)
   {
      return calendar == null ? null : calendar.getUniqueID();
   }

   /**
    * Retrieve an activity status.
    *
    * @param mpxj MPXJ Task instance
    * @return activity status
    */
   private String getActivityStatus(Task mpxj)
   {
      String result;
      if (mpxj.getActualStart() == null)
      {
         result = "Not Started";
      }
      else
      {
         if (mpxj.getActualFinish() == null)
         {
            result = "In Progress";
         }
         else
         {
            result = "Completed";
         }
      }
      return result;
   }

   /**
    * Generate a default Resource ID for a resource.
    *
    * @param resource Resource instance
    * @return generated Resource ID
    */
   private String getResourceID(Resource resource)
   {
      String result = resource.getResourceID();
      if (result == null)
      {
         result = RESOURCE_ID_PREFIX + resource.getUniqueID();
      }
      return result;
   }

   /**
    * Populate a sorted list of custom fields to ensure that these fields
    * are written to the file in a consistent order.
    */
   private void populateSortedCustomFieldsList()
   {
      m_sortedCustomFieldsList = new ArrayList<>();

      for (CustomField field : m_projectFile.getCustomFields())
      {
         FieldType fieldType = field.getFieldType();
         if (fieldType != null && fieldType.getDataType() != null)
         {
            m_sortedCustomFieldsList.add(field);
         }
      }

      // Sort to ensure consistent order in file
      Collections.sort(m_sortedCustomFieldsList, new Comparator<CustomField>()
      {
         @Override public int compare(CustomField customField1, CustomField customField2)
         {
            FieldType o1 = customField1.getFieldType();
            FieldType o2 = customField2.getFieldType();
            String name1 = o1.getClass().getSimpleName() + "." + o1.getName() + " " + customField1.getAlias();
            String name2 = o2.getClass().getSimpleName() + "." + o2.getName() + " " + customField2.getAlias();
            return name1.compareTo(name2);
         }
      });
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
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.primavera.schema", PrimaveraPMFileWriter.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }

   private static final String NILLABLE_STYLESHEET = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><xsl:output method=\"xml\" indent=\"yes\"/><xsl:template match=\"node()[not(@xsi:nil = 'true')]|@*\"><xsl:copy><xsl:apply-templates select=\"node()|@*\"/></xsl:copy></xsl:template></xsl:stylesheet>";
   private static final Integer PROJECT_OBJECT_ID = Integer.valueOf(1);
   private static final Integer NOTEBOOK_TOPIC_OBJECT_ID = Integer.valueOf(1);
   private static final String PROJECT_ID = "PROJECT";
   private static final String RESOURCE_ID_PREFIX = "RESOURCE-";
   private static final String DEFAULT_WBS_CODE = "WBS";
   private static final Integer DEFAULT_CURRENCY_ID = Integer.valueOf(1);

   private static final String[] DAY_NAMES =
   {
      "Sunday",
      "Monday",
      "Tuesday",
      "Wednesday",
      "Thursday",
      "Friday",
      "Saturday"
   };

   private static final Map<RelationType, String> RELATION_TYPE_MAP = new HashMap<>();
   static
   {
      RELATION_TYPE_MAP.put(RelationType.FINISH_START, "Finish to Start");
      RELATION_TYPE_MAP.put(RelationType.FINISH_FINISH, "Finish to Finish");
      RELATION_TYPE_MAP.put(RelationType.START_START, "Start to Start");
      RELATION_TYPE_MAP.put(RelationType.START_FINISH, "Start to Finish");
   }

   private static final Map<TaskType, String> DURATION_TYPE_MAP = new HashMap<>();
   static
   {
      DURATION_TYPE_MAP.put(TaskType.FIXED_DURATION, "Fixed Duration and Units/Time");
      DURATION_TYPE_MAP.put(TaskType.FIXED_UNITS, "Fixed Units");
      DURATION_TYPE_MAP.put(TaskType.FIXED_WORK, "Fixed Duration and Units");
   }

   private static final Map<ConstraintType, String> CONSTRAINT_TYPE_MAP = new HashMap<>();
   static
   {
      CONSTRAINT_TYPE_MAP.put(ConstraintType.START_ON, "Start On");
      CONSTRAINT_TYPE_MAP.put(ConstraintType.START_NO_LATER_THAN, "Start On or Before");
      CONSTRAINT_TYPE_MAP.put(ConstraintType.START_NO_EARLIER_THAN, "Start On or After");
      CONSTRAINT_TYPE_MAP.put(ConstraintType.FINISH_ON, "Finish On");
      CONSTRAINT_TYPE_MAP.put(ConstraintType.FINISH_NO_LATER_THAN, "Finish On or Before");
      CONSTRAINT_TYPE_MAP.put(ConstraintType.FINISH_NO_EARLIER_THAN, "Finish On or After");
      CONSTRAINT_TYPE_MAP.put(ConstraintType.AS_LATE_AS_POSSIBLE, "As Late As Possible");
      CONSTRAINT_TYPE_MAP.put(ConstraintType.MUST_START_ON, "Mandatory Start");
      CONSTRAINT_TYPE_MAP.put(ConstraintType.MUST_FINISH_ON, "Mandatory Finish");
   }

   private static final Map<AccrueType, String> ACCRUE_TYPE_MAP = new HashMap<>();
   static
   {
      ACCRUE_TYPE_MAP.put(AccrueType.PRORATED, "Uniform Over Activity");
      ACCRUE_TYPE_MAP.put(AccrueType.END, "End of Activity");
      ACCRUE_TYPE_MAP.put(AccrueType.START, "Start of Activity");
   }

   private static final Map<PercentCompleteType, String> PERCENT_COMPLETE_TYPE = new HashMap<>();
   static
   {
      PERCENT_COMPLETE_TYPE.put(PercentCompleteType.PHYSICAL, "Physical");
      PERCENT_COMPLETE_TYPE.put(PercentCompleteType.DURATION, "Duration");
      PERCENT_COMPLETE_TYPE.put(PercentCompleteType.UNITS, "Units");
      PERCENT_COMPLETE_TYPE.put(PercentCompleteType.SCOPE, "Scope");
   }

   private static final Map<net.sf.mpxj.ActivityType, String> ACTIVITY_TYPE_MAP = new HashMap<>();
   static
   {
      ACTIVITY_TYPE_MAP.put(net.sf.mpxj.ActivityType.TASK_DEPENDENT, "Task Dependent");
      ACTIVITY_TYPE_MAP.put(net.sf.mpxj.ActivityType.RESOURCE_DEPENDENT, "Resource Dependent");
      ACTIVITY_TYPE_MAP.put(net.sf.mpxj.ActivityType.LEVEL_OF_EFFORT, "Level of Effort");
      ACTIVITY_TYPE_MAP.put(net.sf.mpxj.ActivityType.START_MILESTONE, "Start Milestone");
      ACTIVITY_TYPE_MAP.put(net.sf.mpxj.ActivityType.FINISH_MILESTONE, "Finish Milestone");
      ACTIVITY_TYPE_MAP.put(net.sf.mpxj.ActivityType.WBS_SUMMARY, "WBS Summary");
   }

   private ProjectFile m_projectFile;
   private ObjectFactory m_factory;
   private APIBusinessObjects m_apibo;
   private ProjectType m_project;
   private int m_wbsSequence;
   private int m_relationshipObjectID;
   private int m_rateObjectID;
   private int m_wbsNoteObjectID;
   private int m_activityNoteObjectID;
   private List<CustomField> m_sortedCustomFieldsList;
   private Map<Integer, String> m_topics;
   boolean m_activityTypePopulated;
}
