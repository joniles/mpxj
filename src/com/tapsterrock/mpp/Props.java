/*
 * file:       Props.java
 * author:     Jon Iles
 * copyright:  Tapster Rock Limited
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

package com.tapsterrock.mpp;

import java.util.Date;
import java.util.TreeMap;

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
    * Retrieve property data as a byte array
    *
    * @param type Type identifier
    * @return  byte array of data
    */
   public byte[] getByteArray (Integer type)
   {
      return ((byte[])m_map.get (type));
   }

   /**
    * Retrieves a byte value from the property data
    *
    * @param type Type identifier
    * @return byte value
    */
   public byte getByte (Integer type)
   {
      byte result = 0;

      byte[] item = (byte[])m_map.get (type);
      if (item != null)
      {
         result = item[0];
      }

      return (result);
   }

   /**
    * Retrieves a short int value from the property data
    *
    * @param type Type identifier
    * @return short int value
    */
   public int getShort (Integer type)
   {
      int result = 0;

      byte[] item = (byte[])m_map.get (type);
      if (item != null)
      {
         result = MPPUtility.getShort(item);
      }

      return (result);
   }

   /**
    * Retrieves an integer value from the property data
    *
    * @param type Type identifier
    * @return integer value
    */
   public int getInt (Integer type)
   {
      int result = 0;

      byte[] item = (byte[])m_map.get (type);
      if (item != null)
      {
         result = MPPUtility.getInt(item);
      }

      return (result);
   }

   /**
    * Retrieves a double value from the property data
    *
    * @param type Type identifier
    * @return double value
    */
   public double getDouble (Integer type)
   {
      double result = 0;

      byte[] item = (byte[])m_map.get (type);
      if (item != null)
      {
         result = MPPUtility.getDouble(item);
      }

      return (result);
   }

   /**
    * Retrieves a timestamp from the property data
    *
    * @param type Type identifier
    * @return timestamp
    */
   public Date getTime (Integer type)
   {
      Date result = null;

      byte[] item = (byte[])m_map.get (type);
      if (item != null)
      {
         result = MPPUtility.getTime(item);
      }

      return (result);
   }

   /**
    * Retrieves a boolean value from the property data
    *
    * @param type Type identifier
    * @return boolean value
    */
   public boolean getBoolean (Integer type)
   {
      boolean result = false;

      byte[] item = (byte[])m_map.get (type);
      if (item != null)
      {
         result = !(MPPUtility.getShort(item) == 0);
      }

      return (result);
   }

   /**
    * Retrieves a string value from the property data
    *
    * @param type Type identifier
    * @return string value
    */
   public String getUnicodeString (Integer type)
   {
      String result = null;

      byte[] item = (byte[])m_map.get (type);
      if (item != null)
      {
         result = MPPUtility.getUnicodeString(item);
      }

      return (result);
   }


   /**
    * Data types
    */
   public static final Integer CURRENCY_SYMBOL = new Integer (37748752);
   public static final Integer CURRENCY_PLACEMENT = new Integer (37748753);
   public static final Integer CURRENCY_DIGITS = new Integer (37748754);

   public static final Integer DURATION_UNITS = new Integer (37748757);
   public static final Integer WORK_UNITS = new Integer (37748758);
   public static final Integer TASK_UPDATES_RESOURCE = new Integer (37748761);
   public static final Integer SPLIT_TASKS = new Integer (37748762);
   public static final Integer START_TIME = new Integer (37748764);
   public static final Integer HOURS_PER_DAY = new Integer (37748765);
   public static final Integer HOURS_PER_WEEK = new Integer (37748766);
   public static final Integer STANDARD_RATE = new Integer (37748767);
   public static final Integer OVERTIME_RATE = new Integer (37748768);
   public static final Integer END_TIME = new Integer (37748769);

   public static final Integer CALCULATE_MULTIPLE_CRITICAL_PATHS = new Integer (37748793);
   
   public static final Integer TASK_FIELD_NAME_ALIASES = new Integer (1048577);
   public static final Integer RESOURCE_FIELD_NAME_ALIASES = new Integer (1048578);

   public static final Integer PASSWORD_FLAG = new Integer (893386752);

   public static final Integer SUBPROJECT_COUNT = new Integer(37748868);
   public static final Integer SUBPROJECT_DATA = new Integer(37748898);
   public static final Integer SUBPROJECT_TASK_COUNT = new Integer(37748900);
   
   public static final Integer FONT_BASES = new Integer (54525952);
   
   protected TreeMap m_map = new TreeMap ();
}
