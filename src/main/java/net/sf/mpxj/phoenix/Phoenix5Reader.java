/*
 * file:       Phoenix5Reader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       28 November 2015
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

package net.sf.mpxj.phoenix;

import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.mpxj.ActivityType;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.CostRateTable;
import net.sf.mpxj.CostRateTableEntry;
import net.sf.mpxj.ActivityCode;
import net.sf.mpxj.ActivityCodeValue;
import net.sf.mpxj.RecurrenceType;
import net.sf.mpxj.RecurringData;
import net.sf.mpxj.RelationshipLagCalendar;
import net.sf.mpxj.Relation;
import net.sf.mpxj.SchedulingProgressedActivities;
import net.sf.mpxj.common.LocalDateHelper;
import net.sf.mpxj.common.LocalDateTimeHelper;
import net.sf.mpxj.common.SlackHelper;
import org.xml.sax.SAXException;

import net.sf.mpxj.ChildTaskContainer;
import java.time.DayOfWeek;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarDays;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.AlphanumComparator;
import net.sf.mpxj.common.DebugLogPrintWriter;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.UnmarshalHelper;
import net.sf.mpxj.phoenix.schema.phoenix5.Project;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Layouts.GanttLayout;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Layouts.GanttLayout.CodeOptions;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Layouts.GanttLayout.CodeOptions.CodeOption;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Settings;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Activities.Activity;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.ActivityCodes;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.ActivityCodes.Code;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.ActivityCodes.Code.Value;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Calendars;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Calendars.Calendar;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Calendars.Calendar.NonWork;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Relationships;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Relationships.Relationship;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Resources;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Resources.Resource.Assignment;
import net.sf.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading a Phoenix Project Manager file.
 */
final class Phoenix5Reader extends AbstractProjectStreamReader
{
   public Phoenix5Reader(boolean useActivityCodesForTaskHierarchy)
   {
      m_useActivityCodesForTaskHierarchy = useActivityCodesForTaskHierarchy;
   }

   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         Project phoenixProject = (Project) UnmarshalHelper.unmarshal(CONTEXT, new SkipNulInputStream(stream));
         Storepoint storepoint = getCurrentStorepoint(phoenixProject);

         return new Phoenix5ProjectReader(m_useActivityCodesForTaskHierarchy).read(phoenixProject, storepoint);
      }

      catch (ParserConfigurationException | SAXException | JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }
   }

   /**
    * Retrieve the most recent storepoint.
    *
    * @param phoenixProject project data
    * @return Storepoint instance
    */
   private Project.Storepoints.Storepoint getCurrentStorepoint(Project phoenixProject)
   {
      List<Project.Storepoints.Storepoint> storepoints = phoenixProject.getStorepoints() == null ? Collections.emptyList() : phoenixProject.getStorepoints().getStorepoint();
      storepoints.sort((o1, o2) -> LocalDateTimeHelper.compare(o2.getCreationTime(), o1.getCreationTime()));
      return storepoints.get(0);
   }

   private final boolean m_useActivityCodesForTaskHierarchy;

   /**
    * Cached context to minimise construction cost.
    */
   private static JAXBContext CONTEXT;

   /**
    * Note any error occurring during context construction.
    */
   private static JAXBException CONTEXT_EXCEPTION;

   static
   {
      try
      {
         //
         // JAXB RI property to speed up construction
         //
         System.setProperty("com.sun.xml.bind.v2.runtime.JAXBContextImpl.fastBoot", "true");

         //
         // Construct the context
         //
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.phoenix.schema.phoenix5", Phoenix5Reader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}