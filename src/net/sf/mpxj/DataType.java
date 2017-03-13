/*
 * file:       DataType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Jan 18, 2006
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

import net.sf.mpxj.common.EnumHelper;
import net.sf.mpxj.common.NumberHelper;

/**
 * This class represents the data type of an attribute.
 */
public enum DataType implements MpxjEnum
{
   STRING(1),
   DATE(2),
   CURRENCY(3),
   BOOLEAN(4),
   NUMERIC(5),
   DURATION(6),
   UNITS(7),
   PERCENTAGE(8),
   ACCRUE(9),
   CONSTRAINT(10),
   RATE(11),
   PRIORITY(12),
   RELATION_LIST(13),
   TASK_TYPE(14),
   RESOURCE_TYPE(15),
   TIME_UNITS(15),
   WORK(16),
   INTEGER(17),
   ASCII_STRING(18),
   SHORT(19),
   BINARY(20),
   DELAY(21),
   WORK_UNITS(22),
   WORKGROUP(23),
   GUID(24),
   RATE_UNITS(25),
   EARNED_VALUE_METHOD(26),
   RESOURCE_REQUEST_TYPE(27),
   CURRENCY_SYMBOL_POSITION(28),
   CHAR(29),
   DATE_ORDER(30),
   PROJECT_TIME_FORMAT(31),
   PROJECT_DATE_FORMAT(32),
   SCHEDULE_FROM(33),
   DAY(34),
   MAP(35),
   MPX_FILE_VERSION(36),
   MPX_CODE_PAGE(37),
   BOOKING_TYPE(38);

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    */
   private DataType(int type)
   {
      m_value = type;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static DataType getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = STRING.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static DataType getInstance(Number type)
   {
      int value;
      if (type == null)
      {
         value = -1;
      }
      else
      {
         value = NumberHelper.getInt(type);
      }
      return (getInstance(value));
   }

   /**
    * Accessor method used to retrieve the numeric representation of the enum.
    *
    * @return int representation of the enum
    */
   @Override public int getValue()
   {
      return (m_value);
   }

   /**
    * Array mapping int types to enums.
    */
   private static final DataType[] TYPE_VALUES = EnumHelper.createTypeArray(DataType.class, 1);

   /**
    * Internal representation of the enum int type.
    */
   private int m_value;
}
