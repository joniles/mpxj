package net.sf.mpxj.projectcommander;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;


public final class ProjectCommanderReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream is) throws MPXJException
   {
      try
      {
         m_projectFile = new ProjectFile();
         m_projectFile.getProjectProperties().setFileApplication("Project Commander");
         m_projectFile.getProjectProperties().setFileType("PC");
         m_eventManager = m_projectFile.getEventManager();

         ProjectCommanderData data = new ProjectCommanderData();
         data.setLogFile("c:/temp/project-commander.log");
         data.process(is);
         
         return m_projectFile;
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         m_eventManager = null;
      }
   }


   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
}
