/*
 * file:       ProjectDirectoryReader.java
 * author:     Jon Iles
 * date:       2024-02-27
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

package org.mpxj.openplan;

import java.time.LocalTime;
import java.util.List;

import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

/**
 * Read project properties from a project directory.
 */
class ProjectDirectoryReader extends DirectoryReader
{
   /**
    * Constructor.
    *
    * @param root parent directory
    */
   public ProjectDirectoryReader(DirectoryEntry root)
   {
      m_root = root;
      m_file = new ProjectFile();
   }

   /**
    * Read project properties from the name directory.
    *
    * @param name project directory name
    * @return ProjectFile instance
    */
   public ProjectFile read(String name)
   {
      /*
         Project Directory Contents
      
         ACT - Activity
         ASG - Resource Assignment
         BSA - Baseline Activity
         BSU - Baseline Usage
         CST - Resource Cost
         PRJ - Project (OPP_PRJ)
         REL - Relationship
         RSK - Risk Detail
         SUB - Subproject
         USE - Resource Usage
         AVL - Resource Availability
         PSU - Project Summary
         RES - Resource
         RSL - Resource Escalation
         CDR - Code Data
       */

      DirectoryEntry dir = getDirectoryEntry(m_root, name);
      List<Row> rows = new TableReader(dir, "PRJ").read();
      if (rows.size() != 1)
      {
         throw new OpenPlanException("Expecting 1 project row, found " + rows.size());
      }

      readProjectProperties(rows.get(0));

      DependenciesReader dependencies = new DependenciesReader(dir).read();

      CodeDirectoryReader codeReader = new CodeDirectoryReader(m_root);
      dependencies.getCodes().forEach(codeReader::read);

      ActivityCodeReader activityCodeReader = new ActivityCodeReader(dir, m_file);
      activityCodeReader.read(codeReader.getCodes());

      CalendarDirectoryReader calendarReader = new CalendarDirectoryReader(m_root, m_file);
      dependencies.getCalendars().forEach(calendarReader::read);

      ProjectCalendar defaultCalendar = calendarReader.getMap().get("< Default >");
      if (defaultCalendar != null)
      {
         m_file.setDefaultCalendar(defaultCalendar);
      }
      else
      {
         m_file.addDefaultBaseCalendar();
      }

      ResourceDirectoryReader resourceReader = new ResourceDirectoryReader(m_root, m_file);
      dependencies.getResources().forEach(resourceReader::read);

      ActivityReader activityReader = new ActivityReader(dir, m_file);
      activityReader.read(activityCodeReader.getCodeMap(), calendarReader.getMap());

      RelationReader relationReader = new RelationReader(dir, m_file);
      relationReader.read();

      AssignmentReader assignmentReader = new AssignmentReader(dir, m_file);
      assignmentReader.read();

      m_file.readComplete();

      return m_file;
   }

   /**
    * Read project properties.
    *
    * @param row project data
    */
   private void readProjectProperties(Row row)
   {
      ProjectProperties props = m_file.getProjectProperties();
      props.setFileApplication("Deltek OpenPlan");
      props.setFileType("BK3");

      // ACTDATEOPT: Actual Date Option
      // ACTTYPE: Default Activity Type
      // ACWP_LAB: ACWP Labor
      // ACWP_MAT: ACWP Material
      // ACWP_ODC: ACWP Other Direct Cost
      // ACWP_QTY: ACWP Labor Actual Units
      // ACWP_SUB: ACWP Subcontractors
      // ANCILMODE: Ancillary File Open Mode (E: Exclusive, S: Shared, R: Read Only)
      // ASG_LEVEL_VALUE: Default Resource Curve
      // AUTOANAL: Auto Time Analysis Option
      // AUTOPROGACT: Auto Progress Activities
      // AUTOPROGBASE: Auto Progress Based On
      // AUTOPROGCFB: Auto Progress Complete If Finished
      // AUTOPROGPPC: Auto Progress PPC
      // AUTOPROGPSB: Auto Progress Complete If Started
      // AUTOPROGRES: Auto Progress Resources
      // AUTOPROGTYPE: Auto Progress Type
      // BAC_LAB: Budget At Completion Labor
      // BAC_MAT: Budget At Completion Material
      // BAC_ODC: Budget At Completion Other Direct Cost
      // BAC_QTY: Budget At Completion Labor Units
      // BAC_SUB: Budget At Completion Subcontractors
      // BCWP_LAB: BCWP Labor
      // BCWP_MAT: BCWP Material
      // BCWP_ODC: BCWP Other Dircet Costs
      // BCWP_QTY: BCWP Labor Units
      // BCWP_SUB: BCWP Subcontractors
      // BCWS_LAB: BCWS Labor
      // BCWS_MAT: BCWS Material
      // BCWS_ODC: BCWS Other Direct Costs
      // BCWS_QTY: BCWS Labor Units
      // BCWS_SUB: BCWS Subcontractors
      // BFDATE: Baseline Finish
      props.setBaselineFinish(row.getDate("BFDATE"));
      // BSDATE: Baseline Start
      props.setBaselineStart(row.getDate("BSDATE"));
      // CALACTCST: Calculate Actual Cost
      // CALBUDCST: Calculate Budgeted Cost
      // CALCCOSTBASE: Calculate Cost Based On
      // CALCSTESC: Calculate Escalated Cost
      // CALEVCST: Calculate Earned Value
      // CALREMCST: Calculate Remaining Cost
      // CLD_ID: Calendar  File Name
      // CLD_UID: Calendar File Unique ID
      // CST_ROLLUP: Rollup Calculated Cost
      // DEFACTDUR: Default Activity Duration
      // DEFDURUNIT: Default Duration Unit
      // DEFENDHR: Default Finish Hour
      // DEFENDMN: Default Finish Minute
      props.setDefaultEndTime(LocalTime.of(row.getInteger("DEFENDHR").intValue(), row.getInteger("DEFENDMN").intValue())); // TODO: handle null
      // DEFSTARTHR: Default Start Hour
      // DEFSTARTMN: Default Start Minute
      props.setDefaultStartTime(LocalTime.of(row.getInteger("DEFSTARTHR").intValue(), row.getInteger("DEFSTARTMN").intValue())); // TODO: handle null
      // DESCRIPTION: Project Name
      props.setName(row.getString("DESCRIPTION"));
      // DFORMAT: Project Date Format
      // DIR_ID: Project Object Directory Name
      // DIR_UID: Project Object Directory UID
      // EFDATE: Early Finish Date
      // ESDATE: Early Start Date
      // ETC_LAB: Estimate To Complete Labor Cost
      // ETC_MAT: Estimate To Complete Material Cost
      // ETC_ODC: Estimate To Complete Other Direct Cost
      // ETC_QTY: Estimate To Complete Labor Units
      // ETC_SUB: Estimate To Complete Subcontractor Cost
      // EVT: Earned Value Technique (A: Level of Effort, C: Percent Complete, E: 50-50, F: 0-100, G: 100-0, H: user defined percentage, K: Planning package, L: resource % complete, s: Steps)
      // HARDZERO: Resource scheduling option
      // LASTUPDATE: Project Last Update Date
      props.setLastSaved(row.getDate("LASTUPDATE"));
      // LFDATE: Late Finish
      // LSDATE: Late Start
      // MEAN_EF: Mean Early Finish Date
      // MINCALCDU: Minimum Calculated Duration Unit
      // MINTOTFT: Minimum Total Float
      // MNPERDAY: Minutes Per Day
      props.setMinutesPerDay(row.getInteger("MNPERDAY"));
      // MNPERMON: Minutes Per Month
      props.setMinutesPerMonth(row.getInteger("MNPERMON"));
      // MNPERWK: Minutes Per Week
      props.setMinutesPerWeek(row.getInteger("MNPERWK"));
      // MULTIEND: Time Analysis Multiple End Option
      // NRISKSIMULS: Number of Simulations Risk Analysis Option
      // OPCLIENT: Client Name
      // OPCOMPANY: Company Name
      props.setCompany(row.getString("OPCOMPANY"));
      // OPENMODE
      // OPMANAGER: Project Manager Name
      props.setManager(row.getString("OPMANAGER"));
      // OPSTAT: Project Status (0: Planned, 1: In Progress, 2: Complete)
      // OUTOFSEQ: Out of Sequence Time Analysis Option
      // OWNER_ID	SYSADMIN (String)
      // PCOMPLETE: Percent Complete
      // PRIORITY1: Priority 1 Resource Scheduling Option
      // PRJ_FLAG
      // PROGPRIO: In Progress Resource Scheduling Option
      // PROJSTATUS: Project Phase (P: Proposed, O: Open, C: Closed)
      // RCL_ID: Reporting Calendar Name
      // RDS_ID: Resource File Name
      // RDS_UID: Resource File Unique ID
      // REFDATE: Reporting Reference Date
      // RISKSEED: Fixed Seed Risk Analysis Option
      // RSK_CALSD
      // RS_ACTDATE: Ignore Actual Dates Resource Scheduling Option
      // RS_ALTPRTY: Use Alternate Resource Resource Scheduling Option
      // RS_CONUSE: Consider Usage on Higher Resource Scheduling Option
      // RS_OVLLATE: Force Overloaded Activities to Late Dates Resource Scheduling Option
      // RS_PRIORTY: Project Priority Resource Scheduling Option
      // RS_REPROF: Limit Re-profiling to Original Level Resource Scheduling Option
      // RS_SUMDATE: Project Summary Usage Date Resource Scheduling Option
      // RS_SUMMARY: Create Project Summary Usage Resource Scheduling Option
      // SCHMETHOD:  Resource Scheduling Method
      // SDEV_EF: Standard Deviation of Early Finish
      // SEQUENCE
      // SFDATE: Scheduled Finish
      props.setFinishDate(row.getDate("SFDATE"));
      // SMOOTHING: Smoothing Resource Scheduling Option
      // SSDATE: Scheduled Start Date
      // STARTDATE: Start Date
      props.setStartDate(row.getDate("STARTDATE"));
      // STARTPC: User Defined Estimate to Complete Split %
      // STARTVIEW: Startup View
      // STATDATE: Time Now Date
      props.setStatusDate(row.getDate("STATDATE"));
      // TABLE_TYPE: Table Type (PRJ)
      // TARGCOST
      // TA_BEFORE_RK: Time Analyze Before Risk Analyze
      // TA_SUBEND: Subproject End Activity Time Analysis Option
      // TA_SUMMARY: Show Summary Dates Time Analysis Option
      // TFTYPE: Target Finish Type
      // TIMEUNIT: Scheduling Interval Resource Scheduling Option
      // TOTACT: Number of Activities
      // TOTACTCOM: Number of Completed Activities
      // TOTACTPRG: Number of In Progress Activities
      // TOTRELSHP: Number of Relationships
      // TOTRESO: Number of Resource Assignments
      // TSTYPE: Target Start Type
      // USR_ID
   }

   private final DirectoryEntry m_root;
   private final ProjectFile m_file;
}
