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
    * Retrieves a short int value from the property data
    * 
    * @param type Type identifier
    * @return short int value
    */      
   public int getShort (Integer type)
   {
      int result = 0;
         
      ByteArray item = (ByteArray)m_map.get (type);         
      if (item != null)
      {
         result = MPPUtility.getShort(item.byteArrayValue());
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
         
      ByteArray item = (ByteArray)m_map.get (type);         
      if (item != null)
      {
         result = MPPUtility.getInt(item.byteArrayValue());
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
         
      ByteArray item = (ByteArray)m_map.get (type);         
      if (item != null)
      {
         result = MPPUtility.getDouble(item.byteArrayValue());
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
         
      ByteArray item = (ByteArray)m_map.get (type);         
      if (item != null)
      {
         result = MPPUtility.getTime(item.byteArrayValue());
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
         
      ByteArray item = (ByteArray)m_map.get (type);         
      if (item != null)
      {
         result = !(MPPUtility.getShort(item.byteArrayValue()) == 0);
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
         
      ByteArray item = (ByteArray)m_map.get (type);         
      if (item != null)
      {
         result = MPPUtility.getUnicodeString(item.byteArrayValue());
      }
         
      return (result);
   }

   
   /**
    * Data types
    */
   public static final Integer CURRENCY_SYMBOL = new Integer (16);
   public static final Integer CURRENCY_PLACEMENT = new Integer (17);   
   public static final Integer CURRENCY_DIGITS = new Integer (18);
      
   public static final Integer DURATION_UNITS = new Integer (21);
   public static final Integer WORK_UNITS = new Integer (22);
   public static final Integer TASK_UPDATES_RESOURCE = new Integer (25);
   public static final Integer SPLIT_TASKS = new Integer (26);
   public static final Integer START_TIME = new Integer (28);
   public static final Integer HOURS_PER_DAY = new Integer (29);
   public static final Integer HOURS_PER_WEEK = new Integer (30);
   public static final Integer STANDARD_RATE = new Integer (31);
   public static final Integer OVERTIME_RATE = new Integer (32);
   public static final Integer END_TIME = new Integer (33);
   

   protected TreeMap m_map = new TreeMap (); 
}
