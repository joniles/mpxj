
package net.sf.mpxj.fasttrack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.common.InputStreamHelper;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.ProjectReader;

public class FastTrackReader implements ProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

   @Override public ProjectFile read(String fileName) throws MPXJException
   {
      return read(new File(fileName));
   }

   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      File file = null;

      try
      {
         file = InputStreamHelper.writeStreamToTempFile(inputStream, ".fts");
         return read(file);
      }
      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }
      finally
      {
         file.delete();
      }
   }

   @Override public ProjectFile read(File file) throws MPXJException
   {
      try
      {
         m_data = new FastTrackData();
         m_data.process(file);
         return read();
      }
      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }
      finally
      {
         m_data = null;
      }
   }

   private ProjectFile read() throws Exception
   {
      m_project = new ProjectFile();
      m_eventManager = m_project.getEventManager();

      ProjectConfig config = m_project.getProjectConfig();
      config.setAutoCalendarUniqueID(false);
      config.setAutoTaskID(false);
      config.setAutoResourceUniqueID(false);

      m_eventManager.addProjectListeners(m_projectListeners);

      //      processProject();
      //      processCalendars();
      processResources();
      processTasks();
      //      processAssignments();
      //      processDependencies();

      return m_project;
   }

   private void processResources()
   {
      // TODO: hyperlinks, rates
      FastTrackTable table = m_data.getTable("RESOURCES");
      for (MapRow row : table)
      {
         Resource resource = m_project.addResource();
         resource.setCode(row.getString("Code"));
         resource.setCostPerUse(row.getCurrency("Per Use Cost"));
         resource.setEmailAddress(row.getString("E-mail Address"));
         resource.setFlag(1, row.getBoolean("Flag 1"));
         resource.setFlag(2, row.getBoolean("Flag 2"));
         resource.setFlag(3, row.getBoolean("Flag 3"));
         resource.setFlag(4, row.getBoolean("Flag 4"));
         resource.setFlag(5, row.getBoolean("Flag 5"));
         resource.setFlag(6, row.getBoolean("Flag 6"));
         resource.setFlag(7, row.getBoolean("Flag 7"));
         resource.setFlag(8, row.getBoolean("Flag 8"));
         resource.setFlag(9, row.getBoolean("Flag 9"));
         resource.setFlag(10, row.getBoolean("Flag 10"));
         resource.setFlag(11, row.getBoolean("Flag 11"));
         resource.setFlag(12, row.getBoolean("Flag 12"));
         resource.setFlag(13, row.getBoolean("Flag 13"));
         resource.setFlag(14, row.getBoolean("Flag 14"));
         resource.setFlag(15, row.getBoolean("Flag 15"));
         resource.setFlag(16, row.getBoolean("Flag 16"));
         resource.setFlag(17, row.getBoolean("Flag 17"));
         resource.setFlag(18, row.getBoolean("Flag 18"));
         resource.setFlag(19, row.getBoolean("Flag 19"));
         resource.setFlag(20, row.getBoolean("Flag 20"));
         resource.setGroup(row.getString("Group"));
         resource.setGUID(row.getUUID("_Resource GUID"));
         resource.setInitials(row.getString("Initials"));
         resource.setMaterialLabel(row.getString("Material Label"));
         resource.setName(row.getString("Resource Name"));
         resource.setNotes(row.getString("Resource Notes"));
         resource.setNumber(1, row.getDouble("Number 1"));
         resource.setNumber(2, row.getDouble("Number 2"));
         resource.setNumber(3, row.getDouble("Number 3"));
         resource.setNumber(4, row.getDouble("Number 4"));
         resource.setNumber(5, row.getDouble("Number 5"));
         resource.setText(1, row.getString("Text 1"));
         resource.setText(2, row.getString("Text 2"));
         resource.setText(3, row.getString("Text 3"));
         resource.setText(4, row.getString("Text 4"));
         resource.setText(5, row.getString("Text 5"));
         resource.setUniqueID(row.getInteger("Resource ID"));
      }
   }

   private void processTasks()
   {
      // TODO: work values, hierarchy, created (string timestamp format), hyperlinks
      FastTrackTable activities = m_data.getTable("ACTIVITIES");
      for (MapRow row : activities)
      {
         Task task = m_project.addTask();
         task.setName(row.getString("Activity Name"));
         task.setID(row.getInteger("Activity Row ID"));
         //  Activity Row Number
         task.setFlag(1, row.getBoolean("Flag 1"));
         task.setFlag(2, row.getBoolean("Flag 2"));
         task.setFlag(3, row.getBoolean("Flag 3"));
         task.setFlag(4, row.getBoolean("Flag 4"));
         task.setFlag(5, row.getBoolean("Flag 5"));
         task.setFlag(6, row.getBoolean("Flag 6"));
         task.setFlag(7, row.getBoolean("Flag 7"));
         task.setFlag(8, row.getBoolean("Flag 8"));
         task.setFlag(9, row.getBoolean("Flag 9"));
         task.setFlag(10, row.getBoolean("Flag 10"));
         task.setFlag(11, row.getBoolean("Flag 11"));
         task.setFlag(12, row.getBoolean("Flag 12"));
         task.setFlag(13, row.getBoolean("Flag 13"));
         task.setFlag(14, row.getBoolean("Flag 14"));
         task.setFlag(15, row.getBoolean("Flag 15"));
         task.setFlag(16, row.getBoolean("Flag 16"));
         task.setFlag(17, row.getBoolean("Flag 17"));
         task.setFlag(18, row.getBoolean("Flag 18"));
         task.setFlag(19, row.getBoolean("Flag 19"));
         task.setFlag(20, row.getBoolean("Flag 20"));
         //   Parent Tree
         task.setText(1, row.getString("Text 1"));
         task.setText(2, row.getString("Text 2"));
         task.setText(3, row.getString("Text 3"));
         task.setText(4, row.getString("Text 4"));
         task.setText(5, row.getString("Text 5"));
         task.setWBS(row.getString("WBS"));
         task.setGUID(row.getUUID("_Activity GUID"));
      }

      FastTrackTable table = m_data.getTable("ACTBARS");
      for (MapRow row : table)
      {
         Task task = m_project.getTaskByID(row.getInteger("Activity Row ID"));
         // % Used
         task.setActualDuration(row.getDuration("Actual Duration"));
         task.setActualFinish(row.getTimestamp("Actual Finish Date", "Actual Finish Time"));
         task.setActualStart(row.getTimestamp("Actual Start Date", "Actual Start Time"));
         // Attendees
         task.setBaselineCost(1, row.getCurrency("Baseline Cost 1"));
         task.setBaselineCost(2, row.getCurrency("Baseline Cost 2"));
         task.setBaselineCost(3, row.getCurrency("Baseline Cost 3"));
         task.setBaselineCost(4, row.getCurrency("Baseline Cost 4"));
         task.setBaselineCost(5, row.getCurrency("Baseline Cost 5"));
         task.setBaselineDuration(1, row.getDuration("Baseline Duration 1"));
         task.setBaselineDuration(2, row.getDuration("Baseline Duration 2"));
         task.setBaselineDuration(3, row.getDuration("Baseline Duration 3"));
         task.setBaselineDuration(4, row.getDuration("Baseline Duration 4"));
         task.setBaselineDuration(5, row.getDuration("Baseline Duration 5"));
         task.setBaselineFinish(1, row.getTimestamp("Baseline Finish Date 1", "Baseline Finish Time 1"));
         task.setBaselineFinish(2, row.getTimestamp("Baseline Finish Date 2", "Baseline Finish Time 2"));
         task.setBaselineFinish(3, row.getTimestamp("Baseline Finish Date 3", "Baseline Finish Time 3"));
         task.setBaselineFinish(4, row.getTimestamp("Baseline Finish Date 4", "Baseline Finish Time 4"));
         task.setBaselineFinish(5, row.getTimestamp("Baseline Finish Date 5", "Baseline Finish Time 5"));
         task.setBaselineStart(1, row.getTimestamp("Baseline Start Date 1", "Baseline Start Time 1"));
         task.setBaselineStart(2, row.getTimestamp("Baseline Start Date 2", "Baseline Start Time 1"));
         task.setBaselineStart(3, row.getTimestamp("Baseline Start Date 3", "Baseline Start Time 1"));
         task.setBaselineStart(4, row.getTimestamp("Baseline Start Date 4", "Baseline Start Time 1"));
         task.setBaselineStart(5, row.getTimestamp("Baseline Start Date 5", "Baseline Start Time 1"));
         //         task.setBaselineWork(1, row.getWork("Baseline Work 1"));
         //         task.setBaselineWork(2, row.getWork("Baseline Work 2"));
         //         task.setBaselineWork(3, row.getWork("Baseline Work 3"));
         //         task.setBaselineWork(4, row.getWork("Baseline Work 4"));
         //         task.setBaselineWork(5, row.getWork("Baseline Work 5"));
         task.setConstraintDate(row.getTimestamp("Constraint Date", "Constraint Time"));
         task.setCost(1, row.getCurrency("Cost 1"));
         task.setCost(2, row.getCurrency("Cost 2"));
         task.setCost(3, row.getCurrency("Cost 3"));
         task.setCost(4, row.getCurrency("Cost 4"));
         task.setCost(5, row.getCurrency("Cost 5"));
         //task.setCreateDate(val)
         task.setCritical(row.getBoolean("Critical"));
         task.setDate(1, row.getDate("Date 1"));
         task.setDate(2, row.getDate("Date 2"));
         task.setDate(3, row.getDate("Date 3"));
         task.setDate(4, row.getDate("Date 4"));
         task.setDate(5, row.getDate("Date 5"));
         task.setDuration(row.getDuration("Duration"));
         task.setDuration(1, row.getDuration("Duration 1"));
         task.setDuration(2, row.getDuration("Duration 2"));
         task.setDuration(3, row.getDuration("Duration 3"));
         task.setDuration(4, row.getDuration("Duration 4"));
         task.setDuration(5, row.getDuration("Duration 5"));
         task.setEarlyFinish(row.getTimestamp("Early Finish Date", "Early Finish Time"));
         task.setEarlyStart(row.getTimestamp("Early Start Date", "Early Start Time"));
         task.setEffortDriven(row.getBoolean("Effort Driven"));
         task.setFinish(row.getTimestamp("Finish Date", "Finish Time"));
         task.setFinish(1, row.getTimestamp("Finish Date 1", "Finish Time 1"));
         task.setFinish(2, row.getTimestamp("Finish Date 2", "Finish Time 2"));
         task.setFinish(3, row.getTimestamp("Finish Date 3", "Finish Time 3"));
         task.setFinish(4, row.getTimestamp("Finish Date 4", "Finish Time 4"));
         task.setFinish(5, row.getTimestamp("Finish Date 5", "Finish Time 5"));
         // Finish Slack = Finish Float ?
         // Is fixed cost a boolean or a currency?
         // Is fixed duration a duration or a currency
         // what is free float?
         task.setIgnoreResourceCalendar(row.getBoolean("Ignore Resource Calendars"));
         task.setLateFinish(row.getTimestamp("Late Finish Date", "Late Finish Time"));
         task.setLateStart(row.getTimestamp("Late Start Date", "Late Start Time"));
         task.setNumber(1, row.getDouble("Number 1"));
         task.setNumber(2, row.getDouble("Number 2"));
         task.setNumber(3, row.getDouble("Number 3"));
         task.setNumber(4, row.getDouble("Number 4"));
         task.setNumber(5, row.getDouble("Number 5"));
         task.setPercentageComplete(row.getDouble("% Complete"));
         // Priority
         // Resource Cost
         task.setResourceNames(row.getString("Resources Assigned"));
         // Revised Duration
         // Revised Finish Date
         // Revised Finish Time
         // Revised Start Date
         // Revised Start Time
         task.setStart(row.getTimestamp("Start Date", "Start Time"));
         // Start Float
         task.setStart(1, row.getTimestamp("Start Date 1", "Start Time 1"));
         task.setStart(2, row.getTimestamp("Start Date 2", "Start Time 1"));
         task.setStart(3, row.getTimestamp("Start Date 3", "Start Time 1"));
         task.setStart(4, row.getTimestamp("Start Date 4", "Start Time 1"));
         task.setStart(5, row.getTimestamp("Start Date 5", "Start Time 1"));
         // Task Calendar
         // Total Cost
         // Total Float
         // Total Resource Duration
         task.setUniqueID(row.getInteger("Bar ID"));
         //task.setWork(row.getWork("Work"));
         // _Activity
         // _BarBits
         // _BarStl
         // _yOffset
      }

      m_project.updateStructure();
   }

   private FastTrackData m_data;
   private ProjectFile m_project;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
}
