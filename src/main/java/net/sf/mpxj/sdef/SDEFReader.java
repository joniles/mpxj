
package net.sf.mpxj.sdef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.DataType;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;

public final class SDEFReader extends AbstractProjectReader
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
      m_project = new ProjectFile();
      m_eventManager = m_project.getEventManager();

      //      ProjectConfig config = m_project.getProjectConfig();
      //      config.setAutoCalendarUniqueID(false);
      //      config.setAutoTaskID(false);
      //      config.setAutoTaskUniqueID(false);
      //      config.setAutoResourceUniqueID(false);
      //      config.setAutoWBS(false);
      //      config.setAutoOutlineNumber(false);

      m_project.getProjectProperties().setFileApplication("SDEF");
      m_project.getProjectProperties().setFileType("SDEF");

      m_eventManager.addProjectListeners(m_projectListeners);

      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

      try
      {
         while (processLine(reader.readLine()))
         {
            // empty block
         }
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      return m_project;
   }

   private boolean processLine(String line) throws MPXJException
   {
      if (line == null || line.startsWith("END"))
      {
         return false;
      }

      String recordID = line.substring(0, 4);
      Class<? extends SDEFRecord> klass = RECORD_MAP.get(recordID);
      if (klass == null)
      {
         throw new MPXJException("Unknown record type: " + recordID);
      }

      SDEFRecord record;
      try
      {
         record = klass.newInstance();
      }
      
      catch (Exception e)
      {
         throw new MPXJException(MPXJException.READ_ERROR, e);
      }
      
      record.read(line);

      return true;
   }

   private ProjectFile m_project;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;

   private static final Map<String, Class<? extends SDEFRecord>> RECORD_MAP = new HashMap<String, Class<? extends SDEFRecord>>();
   static
   {
      RECORD_MAP.put("VOLM", VolumeRecord.class);
      RECORD_MAP.put("PROJ", ProjectRecord.class);
      RECORD_MAP.put("CLDR", CalendarRecord.class);
      RECORD_MAP.put("HOLI", HolidayRecord.class);
      RECORD_MAP.put("ACTV", ActivityRecord.class);
      RECORD_MAP.put("PRED", PrecedenceRecord.class);
      RECORD_MAP.put("UNIT", UnitCostRecord.class);
      RECORD_MAP.put("PROG", ProgressRecord.class);
   }
}
