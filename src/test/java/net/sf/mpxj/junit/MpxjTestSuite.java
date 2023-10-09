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

package net.sf.mpxj.junit;

import net.sf.mpxj.junit.assignment.EffectiveRateTest;
import net.sf.mpxj.mspdi.XsdDurationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.sf.mpxj.junit.assignment.AssignmentAssignmentsTest;
import net.sf.mpxj.junit.assignment.AssignmentFlagsTest;
import net.sf.mpxj.junit.assignment.AssignmentTextTest;
import net.sf.mpxj.junit.assignment.DeletedAssignmentTest;
import net.sf.mpxj.junit.calendar.CalendarCalendarsTest;
import net.sf.mpxj.junit.calendar.InvalidCalendarTest;
import net.sf.mpxj.junit.calendar.MultiDayExceptionsTest;
import net.sf.mpxj.junit.calendar.RecurringExceptionsTest;
import net.sf.mpxj.junit.legacy.BasicTest;
import net.sf.mpxj.junit.primavera.PrimaveraDatabaseReaderTest;
import net.sf.mpxj.junit.project.DataLinksTest;
import net.sf.mpxj.junit.project.DefaultDurationFormatTest;
import net.sf.mpxj.junit.project.ProjectPropertiesOnlyTest;
import net.sf.mpxj.junit.project.ProjectPropertiesTest;
import net.sf.mpxj.junit.project.ProjectValueListsTest;
import net.sf.mpxj.junit.project.TaskContainerTest;
import net.sf.mpxj.junit.resource.MppResourceTypeTest;
import net.sf.mpxj.junit.resource.ResourceFlagsTest;
import net.sf.mpxj.junit.resource.ResourceMiscTest;
import net.sf.mpxj.junit.resource.ResourceNumbersTest;
import net.sf.mpxj.junit.resource.ResourceTextTest;
import net.sf.mpxj.junit.resource.ResourceTypeTest;
import net.sf.mpxj.junit.task.TaskBaselinesTest;
import net.sf.mpxj.junit.task.TaskCostsTest;
import net.sf.mpxj.junit.task.TaskDatesTest;
import net.sf.mpxj.junit.task.TaskDeletionTest;
import net.sf.mpxj.junit.task.TaskDurationsTest;
import net.sf.mpxj.junit.task.TaskFinishesTest;
import net.sf.mpxj.junit.task.TaskFlagsTest;
import net.sf.mpxj.junit.task.TaskLinksTest;
import net.sf.mpxj.junit.task.TaskNumbersTest;
import net.sf.mpxj.junit.task.TaskOutlineCodesTest;
import net.sf.mpxj.junit.task.TaskPercentCompleteTest;
import net.sf.mpxj.junit.task.TaskStartsTest;
import net.sf.mpxj.junit.task.TaskTextTest;
import net.sf.mpxj.junit.task.TaskTextValuesTest;

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
   ProjectWriterUtilityTest.class,
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
   CustomerDataTest.class
}) public class MpxjTestSuite
{
   // No class body required
}
