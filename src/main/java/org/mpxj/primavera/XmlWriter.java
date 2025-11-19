package org.mpxj.primavera;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeScope;
import org.mpxj.ActivityCodeValue;
import org.mpxj.CustomField;
import org.mpxj.CustomFieldContainer;
import org.mpxj.DataType;
import org.mpxj.Duration;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectContext;
import org.mpxj.TaskField;
import org.mpxj.UserDefinedField;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.ColorHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.common.FieldTypeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.primavera.schema.ActivityCodeType;
import org.mpxj.primavera.schema.ActivityCodeTypeType;
import org.mpxj.primavera.schema.CalendarType;
import org.mpxj.primavera.schema.ObjectFactory;
import org.mpxj.primavera.schema.UDFAssignmentType;
import org.mpxj.primavera.schema.WorkTimeType;

class XmlWriter
{
   public XmlWriter(XmlWriterState state, ProjectContext context)
   {
      m_state = state;
      m_context = context;
   }

   /**
    * Write an activity code definition.
    *
    * @param codes activity codes container
    * @param values activity code values container
    * @param code activity code
    */
   protected void writeActivityCodeDefinition(List<ActivityCodeTypeType> codes, List<ActivityCodeType> values, ActivityCode code)
   {
      ActivityCodeTypeType xml = m_factory.createActivityCodeTypeType();
      codes.add(xml);
      xml.setObjectId(code.getUniqueID());
      xml.setScope(ActivityCodeScopeHelper.getXmlFromInstance(code.getScope()));
      xml.setSequenceNumber(code.getSequenceNumber());
      xml.setName(code.getName());
      xml.setIsSecureCode(Boolean.valueOf(code.getSecure()));
      xml.setLength(WriterHelper.getCodeMaxLength(code));

      if (code.getScope() != ActivityCodeScope.GLOBAL)
      {
         xml.setProjectObjectId(code.getScopeProjectUniqueID());
      }

      Comparator<ActivityCodeValue> comparator = Comparator.comparing(ActivityCodeValue::getSequenceNumber).thenComparing(ActivityCodeValue::getUniqueID);
      code.getChildValues().stream().sorted(comparator).forEach(v -> writeActivityCodeValueDefinition(xml, null, values, v, comparator));
   }

   /**
    * Write an activity code value.
    *
    * @param code parent activity code
    * @param parentValue parent value
    * @param values value container
    * @param value value to write
    * @param comparator sort order for values
    */
   private void writeActivityCodeValueDefinition(ActivityCodeTypeType code, ActivityCodeType parentValue, List<ActivityCodeType> values, ActivityCodeValue value, Comparator<ActivityCodeValue> comparator)
   {
      ActivityCodeType xml = m_factory.createActivityCodeType();
      values.add(xml);
      xml.setObjectId(value.getUniqueID());
      xml.setProjectObjectId(code.getProjectObjectId());
      xml.setCodeTypeObjectId(code.getObjectId());
      xml.setParentObjectId(parentValue == null ? null : parentValue.getObjectId());
      xml.setSequenceNumber(value.getSequenceNumber());
      xml.setCodeValue(value.getName());
      xml.setDescription(value.getDescription());
      xml.setColor(ColorHelper.getHtmlColor(value.getColor()));

      value.getChildValues().stream().sorted(comparator).forEach(v -> writeActivityCodeValueDefinition(code, xml, values, v, comparator));
   }


   /**
    * Writes a list of UDF types.
    *
    * @author lsong
    * @param type parent entity type
    * @param summaryTaskOnly true if we're writing assignments for WBS
    * @param mpxj parent entity
    * @return list of UDFAssignmentType instances
    */
   protected List<UDFAssignmentType> writeUserDefinedFieldAssignments(FieldTypeClass type, boolean summaryTaskOnly, FieldContainer mpxj)
   {
      List<UDFAssignmentType> out = new ArrayList<>();
      CustomFieldContainer customFields = m_context.getCustomFields();

      for (FieldType fieldType : m_state.getUserDefinedFields())
      {
         if (type != fieldType.getFieldTypeClass())
         {
            continue;
         }

         // For the moment we're restricting writing WBS UDF assignments only to
         // UserDefinedField instances with summaryTaskOnly set to true
         // (which will typically be for values read from a P6 schedule originally)
         // TODO: consider if we can map non task user defined fields from other schedules to WBS UDF
         if (type == FieldTypeClass.TASK && summaryTaskOnly)
         {
            if (fieldType instanceof TaskField || (fieldType instanceof UserDefinedField && !((UserDefinedField) fieldType).getSummaryTaskOnly()))
            {
               continue;
            }
         }

         Object value = mpxj.getCachedValue(fieldType);
         if (value == null)
         {
            continue;
         }

         CustomField field = customFields.get(fieldType);
         int uniqueID = field == null ? FieldTypeHelper.getFieldID(fieldType) : NumberHelper.getInt(field.getUniqueID());

         DataType dataType = fieldType.getDataType();
         if (dataType == DataType.CUSTOM)
         {
            dataType = DataType.BINARY;
         }

         UDFAssignmentType udf = m_factory.createUDFAssignmentType();
         udf.setTypeObjectId(uniqueID);
         setUserFieldValue(udf, dataType, value);
         out.add(udf);
      }

      out.sort(Comparator.comparing(UDFAssignmentType::getTypeObjectId));

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
            udf.setTextValue(value == null ? null : value.toString());
            break;
         }

         case DATE:
         {
            udf.setStartDateValue((LocalDateTime) value);
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
    * This method writes data for an individual calendar to a PMXML file.
    *
    * @param calendars calendars container
    * @param calendar ProjectCalendar instance
    */
   protected void writeCalendar(List<CalendarType> calendars, ProjectCalendar calendar)
   {
      ProjectCalendar mpxj = ProjectCalendarHelper.normalizeCalendar(calendar);
      CalendarType xml = m_factory.createCalendarType();
      calendars.add(xml);

      String name = mpxj.getName();
      if (name == null || name.isEmpty())
      {
         name = "(blank)";
      }

      if (calendar.getType() == org.mpxj.CalendarType.PROJECT)
      {
         xml.setProjectObjectId(mpxj.getProjectUniqueID());
      }

      xml.setBaseCalendarObjectId(mpxj.getParentUniqueID());
      xml.setIsDefault(Boolean.valueOf(mpxj.getDefault()));
      xml.setIsPersonal(Boolean.valueOf(mpxj.getPersonal()));
      xml.setName(name);
      xml.setObjectId(mpxj.getUniqueID());
      xml.setType(CalendarTypeHelper.getXmlFromInstance(mpxj.getType()));

      xml.setHoursPerDay(Double.valueOf(NumberHelper.getDouble(mpxj.getMinutesPerDay()) / 60.0));
      xml.setHoursPerWeek(Double.valueOf(NumberHelper.getDouble(mpxj.getMinutesPerWeek()) / 60.0));
      xml.setHoursPerMonth(Double.valueOf(NumberHelper.getDouble(mpxj.getMinutesPerMonth()) / 60.0));
      xml.setHoursPerYear(Double.valueOf(NumberHelper.getDouble(mpxj.getMinutesPerYear()) / 60.0));

      CalendarType.StandardWorkWeek xmlStandardWorkWeek = m_factory.createCalendarTypeStandardWorkWeek();
      xml.setStandardWorkWeek(xmlStandardWorkWeek);

      for (DayOfWeek day : DayOfWeekHelper.ORDERED_DAYS)
      {
         CalendarType.StandardWorkWeek.StandardWorkHours xmlHours = m_factory.createCalendarTypeStandardWorkWeekStandardWorkHours();
         xmlStandardWorkWeek.getStandardWorkHours().add(xmlHours);
         xmlHours.setDayOfWeek(getDayName(day));

         // Working days/hours are not inherited between calendars, just exceptions.
         for (LocalTimeRange range : mpxj.getHours(day))
         {
            WorkTimeType xmlWorkTime = m_factory.createWorkTimeType();
            xmlHours.getWorkTime().add(xmlWorkTime);

            xmlWorkTime.setStart(range.getStart());
            xmlWorkTime.setFinish(getEndTime(range.getEnd()));
         }
      }

      CalendarType.HolidayOrExceptions xmlExceptions = m_factory.createCalendarTypeHolidayOrExceptions();
      xml.setHolidayOrExceptions(xmlExceptions);

      List<ProjectCalendarException> expandedExceptions = mpxj.getExpandedCalendarExceptionsWithWorkWeeks();
      if (!expandedExceptions.isEmpty())
      {
         Set<LocalDate> exceptionDates = new HashSet<>();

         for (ProjectCalendarException mpxjException : expandedExceptions)
         {
            LocalDate date = mpxjException.getFromDate();
            while (!date.isAfter(mpxjException.getToDate()))
            {
               // Prevent duplicate exception dates being written.
               // P6 will fail to import files with duplicate exceptions.
               if (exceptionDates.add(date))
               {
                  CalendarType.HolidayOrExceptions.HolidayOrException xmlException = m_factory.createCalendarTypeHolidayOrExceptionsHolidayOrException();
                  xmlExceptions.getHolidayOrException().add(xmlException);

                  xmlException.setDate(date.atStartOfDay());

                  for (LocalTimeRange range : mpxjException)
                  {
                     WorkTimeType xmlHours = m_factory.createWorkTimeType();
                     xmlException.getWorkTime().add(xmlHours);

                     xmlHours.setStart(range.getStart());

                     if (range.getEnd() != null)
                     {
                        xmlHours.setFinish(getEndTime(range.getEnd()));
                     }
                  }
               }
               date = date.plusDays(1);
            }
         }
      }
   }

   /**
    * The end of a Primavera time range finishes on the last minute
    * of the period, so a range of 12:00 -> 13:00 is represented by
    * Primavera as 12:00 -> 12:59.
    *
    * @param date MPXJ end time
    * @return Primavera end time
    */
   private LocalTime getEndTime(LocalTime date)
   {
      return date.minusMinutes(1);
   }

   /**
    * Formats a day name.
    *
    * @param day MPXJ Day instance
    * @return Primavera day instance
    */
   private String getDayName(DayOfWeek day)
   {
      return DAY_NAMES[DayOfWeekHelper.getValue(day) - 1];
   }

   protected final XmlWriterState m_state;
   protected final ProjectContext m_context;
   protected final ObjectFactory m_factory = new ObjectFactory();

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
}
