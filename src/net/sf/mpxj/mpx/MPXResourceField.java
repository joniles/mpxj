/*
 * file:       MPXResourceField.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       20-Feb-2006
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

package net.sf.mpxj.mpx;

import net.sf.mpxj.ResourceField;

/**
 * Utility class used to map between the integer values held in an MPX file
 * to represent a task field, and the enumerated type used to represent
 * task fields in MPXJ.
 */
final class MPXResourceField
{   
   /**
    * Retrieve an instance of the ResourceField class based on the data read from an
    * MPX file.
    *
    * @param value value from an MS Project file
    * @return instance of this class
    */
   public static ResourceField getMpxjField (int value)
   {
      ResourceField result = null;

      if (value >=0 && value < MPX_MPXJ_ARRAY.length)
      {
         result = MPX_MPXJ_ARRAY[value];
      }

      return (result);
   }
   
   /**
    * Retrieve the integer value used to represent a resource field in an 
    * MPX file.
    * 
    * @param value MPXJ resource field value
    * @return MPX field value
    */
   public static int getMpxField (int value)
   {
      int result = 0;
   
      if (value >=0 && value < MPXJ_MPX_ARRAY.length)
      {
         result = MPXJ_MPX_ARRAY[value];
      }
   
      return (result);

   }
   
   private static final int PERCENT_WORK_COMPLETE = 26;
   private static final int ACCRUE_AT = 45;
   private static final int ACTUAL_COST = 32;
   private static final int ACTUAL_WORK = 22;
   private static final int BASE_CALENDAR = 48;
   private static final int BASELINE_COST = 31;
   private static final int BASELINE_WORK = 21;
   private static final int CODE = 4;
   private static final int COST = 30;
   private static final int COST_PER_USE = 44;
   private static final int COST_VARIANCE = 34;
   private static final int EMAIL_ADDRESS = 11;
   private static final int GROUP = 3;
   private static final int ID = 40;
   private static final int INITIALS = 2;
   private static final int LINKED_FIELDS = 51;
   private static final int MAX_UNITS = 41;
   private static final int NAME = 1;
   //private static final int NOTES = 10;
   private static final int OBJECTS = 50;
   private static final int OVERALLOCATED = 46;
   private static final int OVERTIME_RATE = 43;
   private static final int OVERTIME_WORK = 24;
   private static final int PEAK_UNITS = 47;
   private static final int REMAINING_COST = 33;
   private static final int REMAINING_WORK = 23;
   private static final int STANDARD_RATE = 42;
   private static final int TEXT1 = 5;
   private static final int TEXT2 = 6;
   private static final int TEXT3 = 7;
   private static final int TEXT4 = 8;
   private static final int TEXT5 = 9;
   private static final int UNIQUE_ID = 49;
   private static final int WORK = 20;
   private static final int WORK_VARIANCE = 25;
   
   public static final int MAX_FIELDS = 52;

   private static final ResourceField[] MPX_MPXJ_ARRAY = new ResourceField[MAX_FIELDS];
   
   static
   {
      MPX_MPXJ_ARRAY[PERCENT_WORK_COMPLETE] = ResourceField.PERCENT_WORK_COMPLETE;
      MPX_MPXJ_ARRAY[ACCRUE_AT] = ResourceField.ACCRUE_AT;
      MPX_MPXJ_ARRAY[ACTUAL_COST] = ResourceField.ACTUAL_COST;
      MPX_MPXJ_ARRAY[ACTUAL_WORK] = ResourceField.ACTUAL_WORK;
      MPX_MPXJ_ARRAY[BASE_CALENDAR] = ResourceField.BASE_CALENDAR;
      MPX_MPXJ_ARRAY[BASELINE_COST] = ResourceField.BASELINE_COST;
      MPX_MPXJ_ARRAY[BASELINE_WORK] = ResourceField.BASELINE_WORK;
      MPX_MPXJ_ARRAY[CODE] = ResourceField.CODE;
      MPX_MPXJ_ARRAY[COST] = ResourceField.COST;
      MPX_MPXJ_ARRAY[COST_PER_USE] = ResourceField.COST_PER_USE;
      MPX_MPXJ_ARRAY[COST_VARIANCE] = ResourceField.COST_VARIANCE;
      MPX_MPXJ_ARRAY[EMAIL_ADDRESS] = ResourceField.EMAIL_ADDRESS;
      MPX_MPXJ_ARRAY[GROUP] = ResourceField.GROUP;
      MPX_MPXJ_ARRAY[ID] = ResourceField.ID;
      MPX_MPXJ_ARRAY[INITIALS] = ResourceField.INITIALS;
      MPX_MPXJ_ARRAY[LINKED_FIELDS] = ResourceField.LINKED_FIELDS;
      MPX_MPXJ_ARRAY[MAX_UNITS] = ResourceField.MAX_UNITS;
      MPX_MPXJ_ARRAY[NAME] = ResourceField.NAME;
      //MPX_MPXJ_ARRAY[NOTES] = ResourceField.NOTES;
      MPX_MPXJ_ARRAY[OBJECTS] = ResourceField.OBJECTS;
      MPX_MPXJ_ARRAY[OVERALLOCATED] = ResourceField.OVERALLOCATED;
      MPX_MPXJ_ARRAY[OVERTIME_RATE] = ResourceField.OVERTIME_RATE;
      MPX_MPXJ_ARRAY[OVERTIME_WORK] = ResourceField.OVERTIME_WORK;
      MPX_MPXJ_ARRAY[PEAK_UNITS] = ResourceField.PEAK;
      MPX_MPXJ_ARRAY[REMAINING_COST] = ResourceField.REMAINING_COST;
      MPX_MPXJ_ARRAY[REMAINING_WORK] = ResourceField.REMAINING_WORK;
      MPX_MPXJ_ARRAY[STANDARD_RATE] = ResourceField.STANDARD_RATE;
      MPX_MPXJ_ARRAY[TEXT1] = ResourceField.TEXT1;
      MPX_MPXJ_ARRAY[TEXT2] = ResourceField.TEXT2;
      MPX_MPXJ_ARRAY[TEXT3] = ResourceField.TEXT3;
      MPX_MPXJ_ARRAY[TEXT4] = ResourceField.TEXT4;
      MPX_MPXJ_ARRAY[TEXT5] = ResourceField.TEXT5;
      MPX_MPXJ_ARRAY[UNIQUE_ID] = ResourceField.UNIQUE_ID;
      MPX_MPXJ_ARRAY[WORK] = ResourceField.WORK;
      MPX_MPXJ_ARRAY[WORK_VARIANCE] = ResourceField.WORK_VARIANCE;
   }
   
   private static final int[] MPXJ_MPX_ARRAY = new int[ResourceField.MAX_VALUE];   
   
   static
   {
      MPXJ_MPX_ARRAY[ResourceField.PERCENT_WORK_COMPLETE_VALUE] = PERCENT_WORK_COMPLETE;
      MPXJ_MPX_ARRAY[ResourceField.ACCRUE_AT_VALUE] = ACCRUE_AT;
      MPXJ_MPX_ARRAY[ResourceField.ACTUAL_COST_VALUE] = ACTUAL_COST;
      MPXJ_MPX_ARRAY[ResourceField.ACTUAL_WORK_VALUE] = ACTUAL_WORK;
      MPXJ_MPX_ARRAY[ResourceField.BASE_CALENDAR_VALUE] = BASE_CALENDAR;
      MPXJ_MPX_ARRAY[ResourceField.BASELINE_COST_VALUE] = BASELINE_COST;
      MPXJ_MPX_ARRAY[ResourceField.BASELINE_WORK_VALUE] = BASELINE_WORK;
      MPXJ_MPX_ARRAY[ResourceField.CODE_VALUE] = CODE;
      MPXJ_MPX_ARRAY[ResourceField.COST_VALUE] = COST;
      MPXJ_MPX_ARRAY[ResourceField.COST_PER_USE_VALUE] = COST_PER_USE;
      MPXJ_MPX_ARRAY[ResourceField.COST_VARIANCE_VALUE] = COST_VARIANCE;
      MPXJ_MPX_ARRAY[ResourceField.EMAIL_ADDRESS_VALUE] = EMAIL_ADDRESS;
      MPXJ_MPX_ARRAY[ResourceField.GROUP_VALUE] = GROUP;
      MPXJ_MPX_ARRAY[ResourceField.ID_VALUE] = ID;
      MPXJ_MPX_ARRAY[ResourceField.INITIALS_VALUE] = INITIALS;
      MPXJ_MPX_ARRAY[ResourceField.LINKED_FIELDS_VALUE] = LINKED_FIELDS;
      MPXJ_MPX_ARRAY[ResourceField.MAX_UNITS_VALUE] = MAX_UNITS;
      MPXJ_MPX_ARRAY[ResourceField.NAME_VALUE] = NAME;
      //MPXJ_MPX_ARRAY[ResourceField.NOTES_VALUE] = NOTES;
      MPXJ_MPX_ARRAY[ResourceField.OBJECTS_VALUE] = OBJECTS;
      MPXJ_MPX_ARRAY[ResourceField.OVERALLOCATED_VALUE] = OVERALLOCATED;
      MPXJ_MPX_ARRAY[ResourceField.OVERTIME_RATE_VALUE] = OVERTIME_RATE;
      MPXJ_MPX_ARRAY[ResourceField.OVERTIME_WORK_VALUE] = OVERTIME_WORK;
      MPXJ_MPX_ARRAY[ResourceField.PEAK_VALUE] = PEAK_UNITS;
      MPXJ_MPX_ARRAY[ResourceField.REMAINING_COST_VALUE] = REMAINING_COST;
      MPXJ_MPX_ARRAY[ResourceField.REMAINING_WORK_VALUE] = REMAINING_WORK;
      MPXJ_MPX_ARRAY[ResourceField.STANDARD_RATE_VALUE] = STANDARD_RATE;
      MPXJ_MPX_ARRAY[ResourceField.TEXT1_VALUE] = TEXT1;
      MPXJ_MPX_ARRAY[ResourceField.TEXT2_VALUE] = TEXT2;
      MPXJ_MPX_ARRAY[ResourceField.TEXT3_VALUE] = TEXT3;
      MPXJ_MPX_ARRAY[ResourceField.TEXT4_VALUE] = TEXT4;
      MPXJ_MPX_ARRAY[ResourceField.TEXT5_VALUE] = TEXT5;
      MPXJ_MPX_ARRAY[ResourceField.UNIQUE_ID_VALUE] = UNIQUE_ID;
      MPXJ_MPX_ARRAY[ResourceField.WORK_VALUE] = WORK;
      MPXJ_MPX_ARRAY[ResourceField.WORK_VARIANCE_VALUE] = WORK_VARIANCE;
   }
}
