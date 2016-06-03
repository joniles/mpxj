/*
 * file:       ProjectField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       22/03/2015
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

package net.sf.mpxj;

import java.util.EnumSet;
import java.util.Locale;

/**
 * Instances of this type represent project properties.
 */
public enum ProjectField implements FieldType
{
   START_DATE(DataType.DATE), // Must always be first value
   CURRENCY_SYMBOL(DataType.STRING),
   CURRENCY_SYMBOL_POSITION(DataType.CURRENCY_SYMBOL_POSITION),
   CURRENCY_DIGITS(DataType.INTEGER),
   THOUSANDS_SEPARATOR(DataType.CHAR),
   DECIMAL_SEPARATOR(DataType.CHAR),
   DEFAULT_DURATION_UNITS(DataType.TIME_UNITS),
   DEFAULT_DURATION_IS_FIXED(DataType.BOOLEAN),
   DEFAULT_WORK_UNITS(DataType.TIME_UNITS),
   DEFAULT_STANDARD_RATE(DataType.RATE),
   DEFAULT_OVERTIME_RATE(DataType.RATE),
   UPDATING_TASK_STATUS_UPDATES_RESOURCE_STATUS(DataType.BOOLEAN),
   SPLIT_IN_PROGRESS_TASKS(DataType.BOOLEAN),
   DATE_ORDER(DataType.DATE_ORDER),
   TIME_FORMAT(DataType.PROJECT_TIME_FORMAT),
   DEFAULT_START_TIME(DataType.DATE),
   DATE_SEPARATOR(DataType.CHAR),
   TIME_SEPARATOR(DataType.CHAR),
   AM_TEXT(DataType.STRING),
   PM_TEXT(DataType.STRING),
   DATE_FORMAT(DataType.PROJECT_DATE_FORMAT),
   BAR_TEXT_DATE_FORMAT(DataType.PROJECT_DATE_FORMAT),
   PROJECT_TITLE(DataType.STRING),
   COMPANY(DataType.STRING),
   MANAGER(DataType.STRING),
   DEFAULT_CALENDAR_NAME(DataType.STRING),
   SCHEDULE_FROM(DataType.SCHEDULE_FROM),
   CURRENT_DATE(DataType.DATE),
   COMMENTS(DataType.STRING),
   COST(DataType.CURRENCY),
   BASELINE_COST(DataType.CURRENCY),
   ACTUAL_COST(DataType.CURRENCY),
   WORK(DataType.WORK),
   BASELINE_WORK(DataType.WORK),
   ACTUAL_WORK(DataType.WORK),
   WORK2(DataType.NUMERIC),
   DURATION(DataType.DURATION),
   BASELINE_DURATION(DataType.DURATION),
   ACTUAL_DURATION(DataType.DURATION),
   PERCENTAGE_COMPLETE(DataType.PERCENTAGE),
   BASELINE_START(DataType.DATE),
   BASELINE_FINISH(DataType.DATE),
   ACTUAL_START(DataType.DATE),
   ACTUAL_FINISH(DataType.DATE),
   START_VARIANCE(DataType.DURATION),
   FINISH_VARIANCE(DataType.DURATION),
   SUBJECT(DataType.STRING),
   AUTHOR(DataType.STRING),
   KEYWORDS(DataType.STRING),
   HYPERLINK_BASE(DataType.STRING),
   DEFAULT_END_TIME(DataType.DATE),
   PROJECT_EXTERNALLY_EDITED(DataType.BOOLEAN),
   CATEGORY(DataType.STRING),
   MINUTES_PER_DAY(DataType.INTEGER),
   DAYS_PER_MONTH(DataType.INTEGER),
   MINUTES_PER_WEEK(DataType.INTEGER),
   FISCAL_YEAR_START(DataType.BOOLEAN),
   DEFAULT_TASK_EARNED_VALUE_METHOD(DataType.EARNED_VALUE_METHOD),
   REMOVE_FILE_PROPERTIES(DataType.BOOLEAN),
   MOVE_COMPLETED_ENDS_BACK(DataType.BOOLEAN),
   NEW_TASKS_ESTIMATED(DataType.BOOLEAN),
   SPREAD_ACTUAL_COST(DataType.BOOLEAN),
   MULTIPLE_CRITICAL_PATHS(DataType.BOOLEAN),
   AUTO_ADD_NEW_RESOURCES_AND_TASKS(DataType.BOOLEAN),
   LAST_SAVED(DataType.DATE),
   STATUS_DATE(DataType.DATE),
   MOVE_REMAINING_STARTS_BACK(DataType.BOOLEAN),
   AUTO_LINK(DataType.BOOLEAN),
   MICROSOFT_PROJECT_SERVER_URL(DataType.BOOLEAN),
   HONOR_CONSTRAINTS(DataType.BOOLEAN),
   ADMIN_PROJECT(DataType.BOOLEAN),
   INSERTED_PROJECTS_LIKE_SUMMARY(DataType.BOOLEAN),
   NAME(DataType.STRING),
   SPREAD_PERCENT_COMPLETE(DataType.BOOLEAN),
   MOVE_COMPLETED_ENDS_FORWARD(DataType.BOOLEAN),
   EDITABLE_ACTUAL_COSTS(DataType.BOOLEAN),
   UNIQUE_ID(DataType.STRING),
   REVISION(DataType.INTEGER),
   NEW_TASKS_EFFORT_DRIVEN(DataType.BOOLEAN),
   MOVE_REMAINING_STARTS_FORWARD(DataType.BOOLEAN),
   ACTUALS_IN_SYNC(DataType.BOOLEAN),
   DEFAULT_TASK_TYPE(DataType.TASK_TYPE),
   EARNED_VALUE_METHOD(DataType.EARNED_VALUE_METHOD),
   CREATION_DATE(DataType.DATE),
   EXTENDED_CREATION_DATE(DataType.DATE),
   DEFAULT_FIXED_COST_ACCRUAL(DataType.ACCRUE),
   CRITICAL_SLACK_LIMIT(DataType.INTEGER),
   BASELINE_FOR_EARNED_VALUE(DataType.INTEGER),
   FISCAL_YEAR_START_MONTH(DataType.INTEGER),
   NEW_TASK_START_IS_PROJECT_START(DataType.BOOLEAN),
   WEEK_START_DAY(DataType.DAY),
   CALCULATE_MULTIPLE_CRITICAL_PATHS(DataType.BOOLEAN),
   CUSTOM_PROPERTIES(DataType.MAP),
   CURRENCY_CODE(DataType.STRING),
   SHOW_PROJECT_SUMMARY_TASK(DataType.BOOLEAN),
   BASELINE_DATE(DataType.DATE),
   BASELINE1_DATE(DataType.DATE),
   BASELINE2_DATE(DataType.DATE),
   BASELINE3_DATE(DataType.DATE),
   BASELINE4_DATE(DataType.DATE),
   BASELINE5_DATE(DataType.DATE),
   BASELINE6_DATE(DataType.DATE),
   BASELINE7_DATE(DataType.DATE),
   BASELINE8_DATE(DataType.DATE),
   BASELINE9_DATE(DataType.DATE),
   BASELINE10_DATE(DataType.DATE),
   TEMPLATE(DataType.STRING),
   LAST_AUTHOR(DataType.STRING),
   LASTPRINTED(DataType.DATE),
   SHORT_APPLICATION_NAME(DataType.STRING),
   EDITING_TIME(DataType.INTEGER),
   PRESENTATION_FORMAT(DataType.STRING),
   CONTENT_TYPE(DataType.STRING),
   CONTENT_STATUS(DataType.STRING),
   LANGUAGE(DataType.STRING),
   DOCUMENT_VERSION(DataType.STRING),
   MPX_DELIMITER(DataType.CHAR),
   MPX_PROGRAM_NAME(DataType.STRING),
   MPX_FILE_VERSION(DataType.MPX_FILE_VERSION),
   MPX_CODE_PAGE(DataType.MPX_CODE_PAGE),
   PROJECT_FILE_PATH(DataType.STRING),
   FULL_APPLICATION_NAME(DataType.STRING),
   APPLICATION_VERSION(DataType.INTEGER),
   MPP_FILE_TYPE(DataType.INTEGER),
   AUTOFILTER(DataType.BOOLEAN),

   FINISH_DATE(DataType.DATE); // Must always be last value

   /**
    * Constructor.
    *
    * @param dataType field data type
    * @param unitsType field units type
    */
   private ProjectField(DataType dataType, FieldType unitsType)
   {
      m_dataType = dataType;
      m_unitsType = unitsType;
   }

   /**
    * Constructor.
    *
    * @param dataType field data type
    */
   private ProjectField(DataType dataType)
   {
      this(dataType, null);
   }

   /**
    * {@inheritDoc}
    */
   @Override public FieldTypeClass getFieldTypeClass()
   {
      return FieldTypeClass.PROJECT;
   }

   /**
    * {@inheritDoc}
    */
   @Override public String getName()
   {
      return (getName(Locale.ENGLISH));
   }

   /**
    * {@inheritDoc}
    */
   @Override public String getName(Locale locale)
   {
      String[] titles = LocaleData.getStringArray(locale, LocaleData.PROJECT_COLUMNS);
      String result = null;

      if (m_value >= 0 && m_value < titles.length)
      {
         result = titles[m_value];
      }

      return (result);
   }

   /**
    * {@inheritDoc}
    */
   @Override public int getValue()
   {
      return (m_value);
   }

   /**
    * {@inheritDoc}
    */
   @Override public DataType getDataType()
   {
      return (m_dataType);
   }

   /**
    * {@inheritDoc}
    */
   @Override public FieldType getUnitsType()
   {
      return m_unitsType;
   }

   /**
    * Retrieves the string representation of this instance.
    *
    * @return string representation
    */
   @Override public String toString()
   {
      return (getName());
   }

   /**
    * This method takes the integer enumeration of a property field
    * and returns an appropriate class instance.
    *
    * @param type integer property field enumeration
    * @return PropertyField instance
    */
   public static ProjectField getInstance(int type)
   {
      ProjectField result = null;

      if (type >= 0 && type < MAX_VALUE)
      {
         result = TYPE_VALUES[type];
      }

      return (result);
   }

   public static final int MAX_VALUE = EnumSet.allOf(ProjectField.class).size();
   private static final ProjectField[] TYPE_VALUES = new ProjectField[MAX_VALUE];
   static
   {
      int value = 0;
      for (ProjectField e : EnumSet.allOf(ProjectField.class))
      {
         e.m_value = value++;
         TYPE_VALUES[e.getValue()] = e;
      }
   }

   private int m_value;
   private DataType m_dataType;
   private FieldType m_unitsType;
}
