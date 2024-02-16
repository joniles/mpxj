package net.sf.mpxj.openplan;

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.common.NumberHelper;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

class ActivityReader
{
   public ActivityReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   public void read(String name)
   {
      Map<String, Task> map = new HashMap<>();

      for (Row row : new TableReader(m_root, "ACT").read())
      {
         Task task = m_file.addTask();

         // USER_NUM10	0.0 (Double)
         // DSHAPE	  (String)
         task.setPlannedDuration(row.getDuration("ORIG_DUR"));
         // MSPUNIQUEID	0 (String)
         // USR_ID	SYSADMIN (String)
         // BCWP_MAT	0.0 (Double)
         // STARTPC	0 (String)
         // DHIGH	601.0d (Duration)
         // ETC_QTY	7858.0 (Double)
         // ACWP_MAT	0.0 (Double)
         // COMPSTAT	0 (String)
         // BAC_ODC	0.0 (Double)
         // FINFREEFLT	0.0h (Duration)
         // DIR_ID	CLEAN (String)
         task.setFinish(row.getDate("SFDATE"));
         // SFDATE	2022-03-08T16:00 (LocalDateTime)
         // MINSPLITD	0 (String)
         // SSINDEX	99.97 (Double)
         // SEP_ASG	F (String)
         // OUTOFSEQ	false (Boolean)
         // CRITINDEX	100.0 (Double)
         // ACWP_SUB	0.0 (Double)
         // BCWS_ODC	0.0 (Double)
         task.setBaselineStart(row.getDate("BSDATE"));
         task.setEarlyStart(row.getDate("ESDATE"));
         // DIR_UID	d8acbddc-030d-4ca7-4cdc-38c8c7df9300 (UUID)
         // BCWS_SUB	0.0 (Double)
         // ACWP_ODC	0.0 (Double)
         // MAXDUR	0 (String)
         // PPC	0.0 (Double)
         // BAC_MAT	148800.0 (Double)
         // C1	IGOQ-];BZ@&_R4X*VOPZZ# (String)
         // C2	C&!Z&B0@P1&H$MO#)$\^~( (String)
         // DLOW	451.0d (Duration)
         task.setName(row.getString("DESCRIPTION"));
         // BCWS_MAT	0.0 (Double)
         task.setStart(row.getDate("SSDATE"));
         // RS_FLOAT	0.0h (Duration)
         // ACTIVEINDEX	100.0 (Double)
         // BCWP_SUB	0.0 (Double)
         // ETC_LAB	440321.0 (Double)
         // BAC_SUB	0.0 (Double)
         task.setBaselineFinish(row.getDate("BFDATE"));
         // PROGTYPE	  (String)
         // BCWP_ODC	0.0 (Double)
         task.setGUID(row.getUuid("ACT_UID"));
         task.setEarlyFinish(row.getDate("EFDATE"));
         task.setRemainingDuration(row.getDuration("REM_DUR"));
         // COMP_RS_C	  (String)
         // ETC_ODC	0.0 (Double)
         task.setLateFinish(row.getDate("LFDATE"));
         // MEAN_LF	2022-03-07T16:00 (LocalDateTime)
         // RSCLASS	  (String)
         // RS_SUPRESS	false (Boolean)
         // SDEV_LS	6.0h (Duration)
         // BCWS_LAB	0.0 (Double)
         // PRIORITY	0 (String)
         // LASTUPDATE	2002-06-25T08:11 (LocalDateTime)
         // ETC_SUB	0.0 (Double)
         // BAC_QTY	7858.0 (Double)
         // DCMA_12_TF_Testing	0 (String)
         // MEAN_TF	0.0h (Duration)
         // SDEV_EF	6.0d (Duration)
         task.setCritical(Boolean.valueOf(NumberHelper.getInt(row.getInteger("CRITICAL")) == 0));
         // CRITICAL	2 (String)
         // LOGICFLAG	SF (String)
         // ACWP_QTY	0.0 (Double)
         // PROGVALUE	0 (String)
         task.setTotalSlack(row.getDuration("TOTALFLOAT"));
         // TOTALFLOAT	0.0h (Duration)
         // TARGSTYPE	  (String)
         // DELAYRES_UID	00000000-0000-0000-0000-000000000000 (UUID)
         // SDEV_TF	0.0h (Duration)
         // BAC_LAB	440321.0 (Double)
         task.setDuration(row.getDuration("SCHED_DUR"));
         // FEDATE	2020-01-02T08:00 (LocalDateTime)
         // ETC_MAT	148800.0 (Double)
         // SDEV_LF	6.0d (Duration)
         // BCWS_QTY	0.0 (Double)
         // ACT_TYPE	P (String)
         // OPKEY	false (Boolean)
         // EVT	C (String)
         // FINTOTFLT	0.0h (Duration)
         task.setActivityID(row.getString("ACT_ID"));
         // MEAN_FF	0.0h (Duration)
         // MAXSPLITS	0 (String)
         // BCWP_LAB	0.0 (Double)
         task.setLateStart(row.getDate("LSDATE"));
         // SEQUENCE	3 (Integer)
         // TARGFTYPE	  (String)
         // ACT_PROBABILITY	100.0 (Double)
         // ACWP_LAB	0.0 (Double)
         // MEAN_ES	2020-01-02T08:00 (LocalDateTime)
         // USER_NUM01	0.0 (Double)
         // CLH_UID	f312fdc3-9044-2cef-5709-e22ba6eb8500 (UUID)
         // USER_NUM03	0.0 (Double)
         // USER_NUM02	0.0 (Double)
         // USER_NUM05	0.0 (Double)
         // USER_NUM04	0.0 (Double)
         // SDEV_ES	0.0h (Duration)
         // USER_NUM07	0.0 (Double)
         // BCWP_QTY	0.0 (Double)
         // USER_NUM06	0.0 (Double)
         // USER_NUM09	0.0 (Double)
         // USER_NUM08	0.0 (Double)
         // MEAN_EF	2022-03-07T16:00 (LocalDateTime)
         task.setFreeSlack(row.getDuration("FREEFLOAT"));
         // SOURCE_BASELINE	PMB (String)
         // MEAN_LS	2020-01-02T13:00 (LocalDateTime)
         // CLH_ID	1 (String)
         // SDEV_FF	0.0h (Duration)
         // DCMA_12_TF_Current	0 (String)

         map.put(task.getActivityID(), task);
      }

      //
      // Create hierarchical structure
      //
      m_file.getChildTasks().clear();
      for (Task task : map.values())
      {
         String parentActivityID = getParentActivityID(task.getActivityID());
         Task parentTask = map.get(parentActivityID);
         if (parentTask == null)
         {
            m_file.getChildTasks().add(task);
         }
         else
         {
            parentTask.addChildTask(task);
         }
      }

      updateStructure();
   }

   private String getParentActivityID(String activityID)
   {
      int index = activityID.lastIndexOf('.');
      if (index == -1)
      {
         return null;
      }
      return activityID.substring(0,index);
   }

   /**
    * Iterates through the tasks setting the correct
    * outline level and ID values.
    */
   private void updateStructure()
   {
      int id = 1;
      Integer outlineLevel = Integer.valueOf(1);
      for (Task task : m_file.getChildTasks())
      {
         id = updateStructure(id, task, outlineLevel);
      }
   }

   /**
    * Iterates through the tasks setting the correct
    * outline level and ID values.
    *
    * @param id current ID value
    * @param task current task
    * @param outlineLevel current outline level
    * @return next ID value
    */
   private int updateStructure(int id, Task task, Integer outlineLevel)
   {
      task.setID(Integer.valueOf(id++));
      task.setOutlineLevel(outlineLevel);
      outlineLevel = Integer.valueOf(outlineLevel.intValue() + 1);
      for (Task childTask : task.getChildTasks())
      {
         id = updateStructure(id, childTask, outlineLevel);
      }
      return id;
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;
}
