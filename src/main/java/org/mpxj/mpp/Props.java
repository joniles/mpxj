/*
 * file:       Props.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2003
 * date:       27/05/2003
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

package org.mpxj.mpp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.mpxj.common.ByteArrayHelper;

/**
 * This class represents the common structure of Props files found in
 * Microsoft Project MPP files. The MPP8 and MPP9 file formats both
 * implement Props files slightly differently, so this class contains
 * the shared implementation detail, with specific implementations for
 * MPP8 and MPP9 Props files found in the Props8 and Props9 classes.
 */
class Props extends MPPComponent
{
   /**
    * Retrieve property data as a byte array.
    *
    * @param type Type identifier
    * @return  byte array of data
    */
   public byte[] getByteArray(Integer type)
   {
      return (m_map.get(type));
   }

   /**
    * Retrieves a byte value from the property data.
    *
    * @param type Type identifier
    * @return byte value
    */
   public byte getByte(Integer type)
   {
      byte result = 0;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = item[0];
      }

      return (result);
   }

   /**
    * Retrieves a short int value from the property data.
    *
    * @param type Type identifier
    * @return short int value
    */
   public int getShort(Integer type)
   {
      int result = 0;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = ByteArrayHelper.getShort(item, 0);
      }

      return (result);
   }

   /**
    * Retrieves an integer value from the property data.
    *
    * @param type Type identifier
    * @return integer value
    */
   public int getInt(Integer type)
   {
      int result = 0;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = ByteArrayHelper.getInt(item, 0);
      }

      return (result);
   }

   /**
    * Retrieves a double value from the property data.
    *
    * @param type Type identifier
    * @return double value
    */
   public double getDouble(Integer type)
   {
      double result = 0;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = MPPUtility.getDouble(item, 0);
      }

      return (result);
   }

   /**
    * Retrieves a time from the property data.
    *
    * @param type Type identifier
    * @return timestamp
    */
   public LocalTime getTime(Integer type)
   {
      LocalTime result = null;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = MPPUtility.getTime(item, 0);
      }

      return result;
   }

   /**
    * Retrieves a timestamp from the property data.
    *
    * @param type Type identifier
    * @return timestamp
    */
   public LocalDateTime getTimestamp(Integer type)
   {
      LocalDateTime result = null;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = MPPUtility.getTimestamp(item, 0);
      }

      return (result);
   }

   /**
    * Retrieves a boolean value from the property data.
    *
    * @param type Type identifier
    * @return boolean value
    */
   public boolean getBoolean(Integer type)
   {
      boolean result = false;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = !(ByteArrayHelper.getShort(item, 0) == 0);
      }

      return (result);
   }

   /**
    * Retrieves a string value from the property data.
    *
    * @param type Type identifier
    * @return string value
    */
   public String getUnicodeString(Integer type)
   {
      String result = null;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = MPPUtility.getUnicodeString(item, 0);
      }

      return (result);
   }

   /**
    * Retrieves a date value from the property data.
    *
    * @param type Type identifier
    * @return string value
    */
   public LocalDateTime getDate(Integer type)
   {
      LocalDateTime result = null;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = MPPUtility.getDate(item, 0);
      }

      return (result);
   }

   /**
    * Retrieves a UUID value from the property data.
    *
    * @param type Type identifier
    * @return UUID value
    */
   public UUID getUUID(Integer type)
   {
      byte[] item = m_map.get(type);
      if (item == null)
      {
         return null;
      }

      if (item.length > 16)
      {
         // MPP9 stores a string representation of the GUID
         String value = MPPUtility.getUnicodeString(item, 0, 76);
         if (value.length() == 38 && value.charAt(0) == '{' && value.charAt(37) == '}')
         {
            return UUID.fromString(value.substring(1, 37));
         }
      }

      return MPPUtility.getGUID(item, 0);
   }

   /**
    * Retrieve the set of keys represented by this instance.
    *
    * @return key set
    */
   public Set<Integer> keySet()
   {
      return (m_map.keySet());
   }

   /**
    * This method dumps the contents of this properties block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   @Override public String toString()
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.println("BEGIN Props");

      for (Map.Entry<Integer, byte[]> entry : m_map.entrySet())
      {
         pw.println("   Key: " + entry.getKey() + " Value: ");
         pw.println(ByteArrayHelper.hexdump(entry.getValue(), true, 16, "      "));
      }

      pw.println("END Props");

      pw.println();
      pw.close();
      return (sw.toString());
   }

   /**
    * Data types.
    */
   public static final Integer PROJECT_START_DATE = Integer.valueOf(37748738);
   public static final Integer PROJECT_FINISH_DATE = Integer.valueOf(37748739);
   public static final Integer SCHEDULE_FROM = Integer.valueOf(37748740);
   public static final Integer RESOURCE_POOL = Integer.valueOf(37748747);
   public static final Integer DEFAULT_CALENDAR_NAME = Integer.valueOf(37748750);
   public static final Integer CURRENCY_SYMBOL = Integer.valueOf(37748752);
   public static final Integer CURRENCY_PLACEMENT = Integer.valueOf(37748753);
   public static final Integer CURRENCY_DIGITS = Integer.valueOf(37748754);

   public static final Integer CRITICAL_SLACK_LIMIT = Integer.valueOf(37748756);
   public static final Integer DURATION_UNITS = Integer.valueOf(37748757);
   public static final Integer WORK_UNITS = Integer.valueOf(37748758);
   public static final Integer TASK_UPDATES_RESOURCE = Integer.valueOf(37748761);
   public static final Integer SPLIT_TASKS = Integer.valueOf(37748762);
   public static final Integer START_TIME = Integer.valueOf(37748764);
   public static final Integer MINUTES_PER_DAY = Integer.valueOf(37748765);
   public static final Integer MINUTES_PER_WEEK = Integer.valueOf(37748766);
   public static final Integer STANDARD_RATE = Integer.valueOf(37748767);
   public static final Integer OVERTIME_RATE = Integer.valueOf(37748768);
   public static final Integer END_TIME = Integer.valueOf(37748769);

   public static final Integer WEEK_START_DAY = Integer.valueOf(37748773);
   public static final Integer GUID = Integer.valueOf(37748777);
   public static final Integer FISCAL_YEAR_START_MONTH = Integer.valueOf(37748780);
   public static final Integer DEFAULT_TASK_TYPE = Integer.valueOf(37748785);
   public static final Integer HONOR_CONSTRAINTS = Integer.valueOf(37748794);
   public static final Integer FISCAL_YEAR_START = Integer.valueOf(37748801);
   public static final Integer EDITABLE_ACTUAL_COSTS = Integer.valueOf(37748802);

   public static final Integer DAYS_PER_MONTH = Integer.valueOf(37753743);

   public static final Integer CURRENCY_CODE = Integer.valueOf(37753787);

   public static final Integer NEW_TASKS_ARE_MANUAL = Integer.valueOf(37753800);

   public static final Integer MULTIPLE_CRITICAL_PATHS = Integer.valueOf(37748793);

   public static final Integer TASK_FIELD_NAME_ALIASES = Integer.valueOf(1048577);
   public static final Integer RESOURCE_FIELD_NAME_ALIASES = Integer.valueOf(1048578);

   public static final Integer PASSWORD_FLAG = Integer.valueOf(893386752);

   public static final Integer PROTECTION_PASSWORD_HASH = Integer.valueOf(893386756);

   public static final Integer WRITE_RESERVATION_PASSWORD_HASH = Integer.valueOf(893386757);

   public static final Integer ENCRYPTION_CODE = Integer.valueOf(893386759);

   public static final Integer STATUS_DATE = Integer.valueOf(37748805);

   public static final Integer SUBPROJECT_COUNT = Integer.valueOf(37748868);
   public static final Integer SUBPROJECT_DATA = Integer.valueOf(37748898);
   public static final Integer SUBPROJECT_TASK_COUNT = Integer.valueOf(37748900);

   public static final Integer DEFAULT_CALENDAR_HOURS = Integer.valueOf(37753736);

   public static final Integer TASK_FIELD_ATTRIBUTES = Integer.valueOf(37753744);

   public static final Integer FONT_BASES = Integer.valueOf(54525952);

   public static final Integer AUTO_FILTER = Integer.valueOf(893386767);

   public static final Integer PROJECT_FILE_PATH = Integer.valueOf(893386760);

   public static final Integer HYPERLINK_BASE = Integer.valueOf(37748810);

   public static final Integer RESOURCE_CREATION_DATE = Integer.valueOf(205521219);

   public static final Integer SHOW_PROJECT_SUMMARY_TASK = Integer.valueOf(54525961);

   public static final Integer TASK_FIELD_MAP = Integer.valueOf(131092);
   public static final Integer TASK_FIELD_MAP2 = Integer.valueOf(50331668);
   public static final Integer ENTERPRISE_CUSTOM_FIELD_MAP = Integer.valueOf(37753797); // MPP14 37753768?

   public static final Integer RESOURCE_FIELD_MAP = Integer.valueOf(131093);
   public static final Integer RESOURCE_FIELD_MAP2 = Integer.valueOf(50331669);

   public static final Integer RELATION_FIELD_MAP = Integer.valueOf(131094);

   public static final Integer ASSIGNMENT_FIELD_MAP = Integer.valueOf(131095);
   public static final Integer ASSIGNMENT_FIELD_MAP2 = Integer.valueOf(50331671);

   public static final Integer BASELINE_CALENDAR_NAME = Integer.valueOf(37753747);

   public static final Integer BASELINE_DATE = Integer.valueOf(37753749);
   public static final Integer BASELINE1_DATE = Integer.valueOf(37753750);
   public static final Integer BASELINE2_DATE = Integer.valueOf(37753751);
   public static final Integer BASELINE3_DATE = Integer.valueOf(37753752);
   public static final Integer BASELINE4_DATE = Integer.valueOf(37753753);
   public static final Integer BASELINE5_DATE = Integer.valueOf(37753754);
   public static final Integer BASELINE6_DATE = Integer.valueOf(37753755);
   public static final Integer BASELINE7_DATE = Integer.valueOf(37753756);
   public static final Integer BASELINE8_DATE = Integer.valueOf(37753757);
   public static final Integer BASELINE9_DATE = Integer.valueOf(37753758);
   public static final Integer BASELINE10_DATE = Integer.valueOf(37753759);

   public static final Integer CUSTOM_FIELDS = Integer.valueOf(71303169);

   protected final TreeMap<Integer, byte[]> m_map = new TreeMap<>();
}
