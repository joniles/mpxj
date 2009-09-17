/*
 * file:       GanttBarShowForCriteriaEnum.java
 * author:     Tom Ollar
 * copyright:  (c) Packwood Software 2009
 * date:       04/04/2009
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

package net.sf.mpxj.mpp;

import net.sf.mpxj.utility.NumberUtility;
import net.sf.mpxj.utility.MpxjEnum;

import java.util.EnumSet;

/**
 * Represents the criteria used to define when a Gantt bar is displayed.
 */
public enum GanttBarShowForCriteriaEnum implements MpxjEnum
{
   NORMAL(0, "Normal"),
   MILESTONE(1, "Milestone"),
   SUMMARY(2, "Summary"),
   CRITICAL(3, "Critical"),
   NONCRITICAL(4, "NonCritical"),
   MARKED(5, "Marked"),
   FINISHED(6, "Finished"),
   INPROGRESS(7, "In Progress"),
   NOTFINISHED(8, "Not Finished"),
   NOTSTARTED(9, "Not Started"),
   STARTEDLATE(10, "Started Late"),
   FINISHEDLATE(11, "Finished Late"),
   STARTEDEARLY(12, "Started Early"),
   FINISHEDEARLY(13, "Finished Early"),
   STARTEDONTIME(14, "Started On Time"),
   FINISHEDONTIME(15, "Finished On Time"),
   FLAG1(16, "Flag1"),
   FLAG2(17, "Flag2"),
   FLAG3(18, "Flag3"),
   FLAG4(19, "Flag4"),
   FLAG5(20, "Flag5"),
   FLAG6(21, "Flag6"),
   FLAG7(22, "Flag7"),
   FLAG8(23, "Flag8"),
   FLAG9(24, "Flag9"),
   FLAG10(25, "Flag10"),
   ROLLEDUP(26, "Rolled Up"),
   PROJECTSUMMARY(27, "Project Summary"),
   SPLIT(28, "Split"),
   EXTERNAL(29, "External"),
   FLAG11(30, "Flag11"),
   FLAG12(31, "Flag12"),
   FLAG13(32, "Flag13"),
   FLAG14(33, "Flag14"),
   FLAG15(34, "Flag15"),
   FLAG16(35, "Flag16"),
   FLAG17(36, "Flag17"),
   FLAG18(37, "Flag18"),
   FLAG19(38, "Flag19"),
   FLAG20(39, "Flag20"),
   GROUPBYSUMMARY(40, "Group By Summary"),
   DELIVERABLE(41, "Deliverable"),
   DEPENDENCY(42, "Dependency");

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name name of the enum
    */
   private GanttBarShowForCriteriaEnum(int type, String name)
   {
      m_value = type;
      m_name = name;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static GanttBarShowForCriteriaEnum getInstance(int type)
   {
      GanttBarShowForCriteriaEnum result;
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         result = NORMAL;
      }
      else
      {
         result = (TYPE_VALUES[type]);
      }
      return result;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static GanttBarShowForCriteriaEnum getInstance(Number type)
   {
      int value;
      if (type == null)
      {
         value = -1;
      }
      else
      {
         value = NumberUtility.getInt(type);
      }
      return (getInstance(value));
   }

   /**
    * Accessor method used to retrieve the numeric representation of the enum.
    *
    * @return int representation of the enum
    */
   public int getValue()
   {
      return (m_value);
   }

   /**
    * Retrieve the line style name. Currently this is not localised.
    *
    * @return style name
    */
   public String getName()
   {
      return (m_name);
   }

   /**
    * Retrieve the String representation of this line style.
    *
    * @return String representation of this line style
    */
   @Override public String toString()
   {
      return (getName());
   }

   /**
    * Array mapping int types to enums.
    */
   private static final GanttBarShowForCriteriaEnum[] TYPE_VALUES = new GanttBarShowForCriteriaEnum[43];
   static
   {
      for (GanttBarShowForCriteriaEnum e : EnumSet.range(GanttBarShowForCriteriaEnum.NORMAL, GanttBarShowForCriteriaEnum.DEPENDENCY))
      {
         TYPE_VALUES[e.getValue()] = e;
      }
   }

   /**
    * Internal representation of the enum int type.
    */
   private int m_value;
   private String m_name;
}
