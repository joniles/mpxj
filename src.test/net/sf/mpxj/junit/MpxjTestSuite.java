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

import net.sf.mpxj.junit.resource.MppResourceTypeTest;
import net.sf.mpxj.junit.task.MppTaskCustomFlagsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite to collect together MPXJ tests.
 * 
 * Ideally this would be generated dynamically, which is fine when running under
 * a JVM, but various approaches to this failed when running under IKVM...
 * so a big list of classes it is! 
 */
@RunWith(Suite.class) @Suite.SuiteClasses(
{
   BasicTest.class,
   LocaleTest.class,
   ProjectCalendarTest.class,
   SplitTaskTest.class,
   MppGraphIndTest.class,
   SlackTest.class,
   MppProjectHeaderTest.class,
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
   TimephasedSegmentTest.class,
   MppAssignmentTest.class,
   TimephasedSegmentTest2.class,
   MppTaskFlags.class,
   MppResourceFlags.class,
   DateUtilityTest.class,
   MppDeletedResourceAssignmentTest.class,
   MppResourceTypeTest.class,
   MppTaskCustomFlagsTest.class,
   CustomerDataTest.class
}) public class MpxjTestSuite
{
   // No class body required
}
