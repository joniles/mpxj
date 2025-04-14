/*
 * file:       MPXConstants.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       Jan 17, 2006
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

package org.mpxj.mpx;

/**
 * This class contains definitions of constants used when reading and writing
 * MPX files.
 */
final class MPXConstants
{
   /**
    * Constant containing the end of line characters used in MPX files.
    */
   public static final String EOL = "\r\n";

   /**
    * Comment record number.
    */
   public static final int COMMENTS_RECORD_NUMBER = 0;

   /**
    * Currency settings record number.
    */
   public static final int CURRENCY_SETTINGS_RECORD_NUMBER = 10;

   /**
    * Default settings record number.
    */
   public static final int DEFAULT_SETTINGS_RECORD_NUMBER = 11;

   /**
    * Date time settings record number.
    */
   public static final int DATE_TIME_SETTINGS_RECORD_NUMBER = 12;

   /**
    * Base calendar record number.
    */
   public static final int BASE_CALENDAR_RECORD_NUMBER = 20;

   /**
    * Base calendar hours record number.
    */
   public static final int BASE_CALENDAR_HOURS_RECORD_NUMBER = 25;

   /**
    * Base calendar exception record number.
    */
   public static final int BASE_CALENDAR_EXCEPTION_RECORD_NUMBER = 26;

   /**
    * Project header record number.
    */
   public static final int PROJECT_HEADER_RECORD_NUMBER = 30;

   /**
    * Resource calendar record number.
    */
   public static final int RESOURCE_CALENDAR_RECORD_NUMBER = 55;

   /**
    * Resource calendar hours record number.
    */
   public static final int RESOURCE_CALENDAR_HOURS_RECORD_NUMBER = 56;

   /**
    * Resource calendar exception record number.
    */
   public static final int RESOURCE_CALENDAR_EXCEPTION_RECORD_NUMBER = 57;

   /**
    * Text resource model record number.
    */
   public static final int RESOURCE_MODEL_TEXT_RECORD_NUMBER = 40;

   /**
    * Numeric resource model record number.
    */
   public static final int RESOURCE_MODEL_NUMERIC_RECORD_NUMBER = 41;

   /**
    * Resource record number.
    */
   public static final int RESOURCE_RECORD_NUMBER = 50;

   /**
    * Resource notes record number.
    */
   public static final int RESOURCE_NOTES_RECORD_NUMBER = 51;

   /**
    * Text task model record number.
    */
   public static final int TASK_MODEL_TEXT_RECORD_NUMBER = 60;

   /**
    * Numeric task model record number.
    */
   public static final int TASK_MODEL_NUMERIC_RECORD_NUMBER = 61;

   /**
    * Task record number.
    */
   public static final int TASK_RECORD_NUMBER = 70;

   /**
    * Task notes record number.
    */
   public static final int TASK_NOTES_RECORD_NUMBER = 71;

   /**
    * Recurring task record number.
    */
   public static final int RECURRING_TASK_RECORD_NUMBER = 72;

   /**
    * Resource assignment record number.
    */
   public static final int RESOURCE_ASSIGNMENT_RECORD_NUMBER = 75;

   /**
    * Resource assignment workgroup record number.
    */
   public static final int RESOURCE_ASSIGNMENT_WORKGROUP_FIELDS_RECORD_NUMBER = 76;

   /**
    * Project names record number.
    */
   public static final int PROJECT_NAMES_RECORD_NUMBER = 80;

   /**
    * DDE OLE client links record number.
    */
   public static final int DDE_OLE_CLIENT_LINKS_RECORD_NUMBER = 81;

   /**
    * File creation record number.
    * Note that in this case it is a dummy value, the actual value used
    * in the file is MPX. The dummy value is used to allow all record types
    * to be identified numerically.
    */
   public static final int FILE_CREATION_RECORD_NUMBER = 999;

   /**
    * Placeholder character used in MPX files to represent
    * carriage returns embedded in note text.
    */
   static final char EOL_PLACEHOLDER = (char) 0x7F;
   static final String EOL_PLACEHOLDER_STRING = new String(new byte[]
   {
      EOL_PLACEHOLDER
   });

   /**
    * Constructor.
    */
   private MPXConstants()
   {
      // private constructor to prevent instantiation
   }
}
