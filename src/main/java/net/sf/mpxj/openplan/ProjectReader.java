package net.sf.mpxj.openplan;

import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.List;

import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

class ProjectReader
{
   public ProjectReader(DirectoryEntry root)
   {
      m_root = root;
      m_file = new ProjectFile();
   }

   public ProjectFile read(String name)
   {
      /*
         Project Directory Contents

         EXF - Explorer Folders
         SCA - Code Structure Associations
         USE - Resource Usage
         UVA - User Validation Associations
         SUB - Sub Project Summary Information
         STP - Activity Steps
         PRJ - Project
         RSK - Risk Key Activities
         REL - Activity Relationships
         IRL - Inter Project Relationships
         EXI - Explorer Items
         ACT - Activity Details
       */

//     ProjectConfig config = m_file.getProjectConfig();
//      config.setAutoTaskID(false);
//      config.setAutoTaskUniqueID(false);
//      config.setAutoResourceID(false);
//      config.setAutoResourceUniqueID(false);
//      config.setAutoOutlineLevel(false);
//      config.setAutoOutlineNumber(false);
//      config.setAutoWBS(false);
//      config.setAutoCalendarUniqueID(false);
//      config.setAutoAssignmentUniqueID(false);
//      config.setAutoRelationUniqueID(false);
//
//      addListenersToProject(projectFile);

      DirectoryEntry dir = getDirectoryEntry(m_root, name);
      List<Row> rows = new TableReader(dir, "PRJ").read();
      if (rows.size() != 1)
      {
         throw new OpenPlanException("Expecting 1 project row, found " + rows.size());
      }

      Row row = rows.get(0);
//      System.out.println(row);

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
      props.setDefaultEndTime(LocalTime.of(row.getInteger("DEFENDHR"), row.getInteger("DEFENDMN"))); // TODO: handle null
      // DEFSTARTHR: Default Start Hour
      // DEFSTARTMN: Default Start Minute
      props.setDefaultStartTime(LocalTime.of(row.getInteger("DEFSTARTHR"), row.getInteger("DEFSTARTMN"))); // TODO: handle null
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


      DependenciesReader dependencies = new DependenciesReader(dir).read();

      CalendarReader calendarReader = new CalendarReader(m_root, m_file);
      dependencies.getCalendars().forEach(r -> calendarReader.read(r));

      ResourceReader resourceReader = new ResourceReader(m_root, m_file);
      dependencies.getResources().forEach(r -> resourceReader.read(r));

      ActivityReader activityReader = new ActivityReader(dir, m_file);
      activityReader.read("ACT");

      RelationReader relationReader = new RelationReader(dir, m_file);
      relationReader.read("REL");

      m_file.readComplete();

      return m_file;
   }

   private DirectoryEntry getDirectoryEntry(DirectoryEntry root, String name)
   {
      try
      {
         return (DirectoryEntry) root.getEntry(name);
      }

      catch (FileNotFoundException e)
      {
         throw new OpenPlanException(e);
      }
   }

   private final DirectoryEntry m_root;
   private final ProjectFile m_file;
}
