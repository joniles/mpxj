/*
 * file:       MPXJTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       28-Feb-2006
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

package org.mpxj.junit;

import org.mpxj.junit.assignment.EffectiveRateTest;
import org.mpxj.mspdi.XsdDurationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.mpxj.junit.assignment.AssignmentAssignmentsTest;
import org.mpxj.junit.assignment.AssignmentFlagsTest;
import org.mpxj.junit.assignment.AssignmentTextTest;
import org.mpxj.junit.assignment.DeletedAssignmentTest;
import org.mpxj.junit.calendar.CalendarCalendarsTest;
import org.mpxj.junit.calendar.InvalidCalendarTest;
import org.mpxj.junit.calendar.MultiDayExceptionsTest;
import org.mpxj.junit.calendar.RecurringExceptionsTest;
import org.mpxj.junit.legacy.BasicTest;
import org.mpxj.junit.primavera.PrimaveraDatabaseReaderTest;
import org.mpxj.junit.project.DataLinksTest;
import org.mpxj.junit.project.DefaultDurationFormatTest;
import org.mpxj.junit.project.ProjectPropertiesOnlyTest;
import org.mpxj.junit.project.ProjectPropertiesTest;
import org.mpxj.junit.project.ProjectValueListsTest;
import org.mpxj.junit.project.TaskContainerTest;
import org.mpxj.junit.resource.MppResourceTypeTest;
import org.mpxj.junit.resource.ResourceFlagsTest;
import org.mpxj.junit.resource.ResourceMiscTest;
import org.mpxj.junit.resource.ResourceNumbersTest;
import org.mpxj.junit.resource.ResourceTextTest;
import org.mpxj.junit.resource.ResourceTypeTest;
import org.mpxj.junit.task.TaskBaselinesTest;
import org.mpxj.junit.task.TaskCostsTest;
import org.mpxj.junit.task.TaskDatesTest;
import org.mpxj.junit.task.TaskDeletionTest;
import org.mpxj.junit.task.TaskDurationsTest;
import org.mpxj.junit.task.TaskFinishesTest;
import org.mpxj.junit.task.TaskFlagsTest;
import org.mpxj.junit.task.TaskLinksTest;
import org.mpxj.junit.task.TaskNumbersTest;
import org.mpxj.junit.task.TaskOutlineCodesTest;
import org.mpxj.junit.task.TaskPercentCompleteTest;
import org.mpxj.junit.task.TaskStartsTest;
import org.mpxj.junit.task.TaskTextTest;
import org.mpxj.junit.task.TaskTextValuesTest;

/**
 * Test suite to collect together MPXJ tests.
 *
 * Ideally this would be generated dynamically, which is fine when running under
 * a JVM, but various approaches to this failed when running under IKVM...
 * so a big list of classes it is!
 *
 * TODO: revisit making this dynamic when working with the modern IKVM implementation
 */
@RunWith(Suite.class) @Suite.SuiteClasses(
{
   BasicTest.class,
   LocaleTest.class,
   ProjectCalendarTest.class,
   SplitTaskTest.class,
   MppGraphIndTest.class,
   SlackTest.class,
   MppProjectPropertiesTest.class,
   MppTaskTest.class,
   MppResourceTest.class,
   MppSubprojectTest.class,
   MppViewTest.class,
   MppFilterTest.class,
   MppAutoFilterTest.class,
   MppViewStateTest.class,
   MppGroupTest.class,
   MppCalendarTest.class,
   MppEnterpriseTest.class,
   MppBaselineTest.class,
   MppEmbeddedTest.class,
   MppRecurringTest.class,
   MppNullTaskTest.class,
   PlannerCalendarTest.class,
   PlannerResourceTest.class,
   TimephasedTest.class,
   DurationTest.class,
   MppFilterLogicTest.class,
   CostRateTableTest.class,
   AvailabilityTest.class,
   MppColumnsTest.class,
   MppBarStyleTest.class,
   MppGanttTest.class,
   TimephasedWorkSegmentTest.class,
   MppAssignmentTest.class,
   TimephasedWorkCostSegmentTest.class,
   MppTaskFlagsTest.class,
   MppResourceFlagsTest.class,
   DateUtilityTest.class,
   DeletedAssignmentTest.class,
   MppResourceTypeTest.class,
   TaskFlagsTest.class,
   TaskNumbersTest.class,
   TaskDurationsTest.class,
   TaskLinksTest.class,
   TaskDurationsTest.class,
   TaskDatesTest.class,
   TaskStartsTest.class,
   TaskFinishesTest.class,
   TaskCostsTest.class,
   TaskTextTest.class,
   TaskOutlineCodesTest.class,
   TaskDeletionTest.class,
   TaskTextValuesTest.class,
   ProjectPropertiesTest.class,
   ProjectPropertiesOnlyTest.class,
   TaskBaselinesTest.class,
   TaskPercentCompleteTest.class,
   InvalidCalendarTest.class,
   DefaultDurationFormatTest.class,
   MppPasswordTest.class,
   ProjectValueListsTest.class,
   CalendarCalendarsTest.class,
   PrimaveraDatabaseReaderTest.class,
   TaskContainerTest.class,
   AvailabilityTableTest.class,
   AssignmentAssignmentsTest.class,
   AssignmentFlagsTest.class,
   AssignmentTextTest.class,
   ResourceMiscTest.class,
   ResourceFlagsTest.class,
   ResourceNumbersTest.class,
   ResourceTypeTest.class,
   ResourceTextTest.class,
   RecurringExceptionsTest.class,
   RecurringDataTest.class,
   DataLinksTest.class,
   LocaleDataTest.class,
   MultiDayExceptionsTest.class,
   SemVerTest.class,
   EffectiveRateTest.class,
   CombinedCalendarTest.class,
   XsdDurationTest.class,
   CalendarExceptionPrecedenceTest.class,
   TimescaleUtilityTest.class,
   ResourceHierarchyTest.class,
   XmlRelationshipLagCalendarTest.class,
   XerRelationshipLagCalendarTest.class,
   TimephasedWorkSegmentManualOffsetTest.class,
   TimephasedWorkSegmentManualTest.class,
   ListProjectsTest.class,
   CustomerDataTest.class
}) public class MpxjTestSuite
{
   // No class body required
}
