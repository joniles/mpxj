
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;

public final class SynchroReader extends AbstractProjectReader
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

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      try
      {
         m_data = new SynchroData();
         m_data.process(inputStream);
         return read();
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }
   }

   private ProjectFile read() throws Exception
   {
      m_project = new ProjectFile();
      m_eventManager = m_project.getEventManager();

      ProjectConfig config = m_project.getProjectConfig();
      config.setAutoCalendarUniqueID(false);
      config.setAutoTaskID(false);
      config.setAutoTaskUniqueID(false);
      config.setAutoResourceUniqueID(false);
      config.setAutoWBS(false);
      config.setAutoOutlineNumber(false);

      m_project.getProjectProperties().setFileApplication("Synchro");
      m_project.getProjectProperties().setFileType("SP");

      m_eventManager.addProjectListeners(m_projectListeners);

      // processProject();
      processCalendars();
      processResources();
      processTasks();
      // processDependencies();
      // processAssignments();

      return m_project;
   }

   private void processCalendars() throws IOException
   {
      CalendarReader reader = new CalendarReader(m_data.getTableData("Calendars"));
      reader.read();
      for (MapRow row : reader.getRows())
      {
         ProjectCalendar calendar = m_project.addCalendar();
         calendar.setName(row.getString("NAME"));
      }
   }

   private void processResources() throws IOException
   {
      CompanyReader reader = new CompanyReader(m_data.getTableData("Companies"));
      reader.read();
   }

   private void processTasks() throws IOException
   {
      TaskReader reader = new TaskReader(m_data.getTableData("Tasks"));
      reader.read();
      for (MapRow row : reader.getRows())
      {
         Task task = m_project.addTask();
         task.setName(row.getString("NAME"));
         task.setText(1, row.getString("ID"));
      }
   }

   private SynchroData m_data;
   private ProjectFile m_project;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
}
