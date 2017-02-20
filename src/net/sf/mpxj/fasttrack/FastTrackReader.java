
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
      config.setAutoTaskUniqueID(false);
      config.setAutoResourceUniqueID(false);

      m_eventManager.addProjectListeners(m_projectListeners);

      //      processProject();
      //      processCalendars();
      //      processResources();
      //      processTasks();
      //      processAssignments();
      //      processDependencies();

      return m_project;
   }

   private FastTrackData m_data;
   private ProjectFile m_project;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
}
