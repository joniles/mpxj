
package net.sf.mpxj.projectcommander;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.common.ByteArrayHelper;
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

         m_data = new ProjectCommanderData();
         m_data.setLogFile("c:/temp/project-commander.log");
         m_data.process(is);

         readCalendars();
         readTasks();
         readResources();

         return m_projectFile;
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         m_eventManager = null;
         m_data = null;
      }
   }

   private void readCalendars()
   {
      m_data.getBlocks().stream().filter(block -> "CCalendar".equals(block.getName())).forEach(block -> readCalendar(block));
   }


   private void readTasks()
   {
      m_data.getBlocks().stream().filter(block -> "CTask".equals(block.getName())).forEach(block -> readTask(block));
   }

   private void readResources()
   {
      m_data.getBlocks().stream().filter(block -> "CResource".equals(block.getName())).forEach(block -> readResource(block));
   }

   private void readCalendar(Block block)
   {
      byte[] data = block.getData();
      ProjectCalendar calendar = m_projectFile.addCalendar();
      calendar.setName(DatatypeConverter.getString(data, 0));
      m_eventManager.fireCalendarReadEvent(calendar);
   }

   private void readTask(Block block)
   {
      byte[] data = block.getData();
      Task task = m_projectFile.addTask();
      task.setName(DatatypeConverter.getString(data, 0));
      m_eventManager.fireTaskReadEvent(task);
      
      int offset = (task.getName() == null ? 0 : task.getName().length()) + 1;
      int length = data.length - offset;
      System.out.println(task.getID() + "\t" + ByteArrayHelper.hexdump(data, offset, length, true));
      /*
       * Summary tasks are shorter. The last two bytes is a count of the number of child tasks, the next n gasks are the child tasks
       */
   }

   private void readResource(Block block)
   {
      byte[] data = block.getData();
      Resource resource = m_projectFile.addResource();
      resource.setName(DatatypeConverter.getString(data, 0));
      m_eventManager.fireResourceReadEvent(resource);            
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
   private ProjectCommanderData m_data;
}
