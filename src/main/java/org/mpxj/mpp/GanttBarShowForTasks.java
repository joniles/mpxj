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

package org.mpxj.mpp;

import org.mpxj.MpxjEnum;
import org.mpxj.common.EnumHelper;
import org.mpxj.common.NumberHelper;

/**
 * Represents the criteria used to define when a Gantt bar is displayed.
 * Note that the value attribute has been chosen to allow the normal and
 * negated types to be determined. value &amp; 0x64 will be zero for normal types,
 * and non-zero for negative types. value &amp; 0x63 will convert a negative type
 * to a normal type, the type can then be retrieved using the getInstance
 * method.
 */
public enum GanttBarShowForTasks implements MpxjEnum
{
   NORMAL(0, "Normal"),
   MILESTONE(1, "Milestone"),
   SUMMARY(2, "Summary"),
   CRITICAL(3, "Critical"),
   NONCRITICAL(4, "Noncritical"),
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
   EXTERNAL(29, "External Tasks"),
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
   DEPENDENCY(42, "Dependency"),
   ACTIVE(43, "Active"),
   MANUALLYSCHEDULED(44, "Manually Scheduled"),
   WARNING(45, "Warning"),
   PLACEHOLDERSTART(46, "Placeholder (Start)"),
   PLACEHOLDERFINISH(47, "Placeholder (Finish)"),
   PLACEHOLDERDURATION(48, "Placeholder (Duration)"),
   PLACEHOLDER(49, "Placeholder"),
   LATE(50, "Late"),

   NOT_NORMAL(64, "Not Normal"),
   NOT_MILESTONE(65, "Not Milestone"),
   NOT_SUMMARY(66, "Not Summary"),
   NOT_CRITICAL(67, "Not Critical"),
   //NOT_NONCRITICAL(68, "Not NonCritical"), // Not used by MSP
   NOT_MARKED(69, "Not Marked"),
   //NOT_FINISHED(70, "Not Not Finished"), // Not used by MSP
   NOT_INPROGRESS(71, "Not In Progress"),
   //NOT_NOTFINISHED(72, "Not Not Finished"), // Not used by MSP
   //NOT_NOTSTARTED(73, "Not Not Started"), // Not used by MSP
   NOT_STARTEDLATE(74, "Not Started Late"),
   NOT_FINISHEDLATE(75, "Not Finished Late"),
   NOT_STARTEDEARLY(76, "Not Started Early"),
   NOT_FINISHEDEARLY(77, "Not Finished Early"),
   NOT_STARTEDONTIME(78, "Not Started On Time"),
   NOT_FINISHEDONTIME(79, "Not Finished On Time"),
   NOT_FLAG1(80, "Not Flag1"),
   NOT_FLAG2(81, "Not Flag2"),
   NOT_FLAG3(82, "Not Flag3"),
   NOT_FLAG4(83, "Not Flag4"),
   NOT_FLAG5(84, "Not Flag5"),
   NOT_FLAG6(85, "Not Flag6"),
   NOT_FLAG7(86, "Not Flag7"),
   NOT_FLAG8(87, "Not Flag8"),
   NOT_FLAG9(88, "Not Flag9"),
   NOT_FLAG10(89, "Not Flag10"),
   NOT_ROLLEDUP(90, "Not Rolled Up"),
   NOT_PROJECTSUMMARY(91, "Not Project Summary"),
   NOT_SPLIT(92, "Not Split"),
   NOT_EXTERNAL(93, "Not External Tasks"),
   NOT_FLAG11(94, "Not Flag11"),
   NOT_FLAG12(95, "Not Flag12"),
   NOT_FLAG13(96, "Not Flag13"),
   NOT_FLAG14(97, "Not Flag14"),
   NOT_FLAG15(98, "Not Flag15"),
   NOT_FLAG16(99, "Not Flag16"),
   NOT_FLAG17(100, "Not Flag17"),
   NOT_FLAG18(101, "Not Flag18"),
   NOT_FLAG19(102, "Not Flag19"),
   NOT_FLAG20(103, "Not Flag20"),
   NOT_GROUPBYSUMMARY(104, "Not Group By Summary"),
   NOT_DELIVERABLE(105, "Not Deliverable"),
   NOT_DEPENDENCY(106, "Not Dependency"),
   NOT_ACTIVE(107, "Not Active"),
   NOT_MANUALLYSCHEDULED(108, "Not Manually Scheduled"),
   NOT_WARNING(109, "Not Warning"),
   NOT_PLACEHOLDERSTART(110, "Not Placeholder (Start)"),
   NOT_PLACEHOLDERFINISH(111, "Not Placeholder (Finish)"),
   NOT_PLACEHOLDERDURATION(112, "Placeholder (Duration)"),
   NOT_PLACEHOLDER(113, "Not Placeholder"),
   NOT_LATE(114, "Not Late");

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name name of the enum
    */
   GanttBarShowForTasks(int type, String name)
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
   public static GanttBarShowForTasks getInstance(int type)
   {
      GanttBarShowForTasks result;
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
   public static GanttBarShowForTasks getInstance(Number type)
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
   private static final GanttBarShowForTasks[] TYPE_VALUES = EnumHelper.createTypeArray(GanttBarShowForTasks.class, 26);
   static
   {
      //
      // This values should in theory represent "Not Not Finished", and we
      // wouldn't expect to see it used. However it has turned up in the
      // wild and is appears to map to "Not Finished", hence this
      // additional mapping.
      //
      TYPE_VALUES[70] = GanttBarShowForTasks.NOTFINISHED;
   }

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
   private final String m_name;
}
