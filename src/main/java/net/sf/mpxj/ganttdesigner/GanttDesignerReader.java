

package net.sf.mpxj.ganttdesigner;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Task;
import net.sf.mpxj.ganttdesigner.schema.Gantt;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;

/**
 * This class creates a new ProjectFile instance by reading a GanttProject file.
 */
public final class GanttDesignerReader extends AbstractProjectReader
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
   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();
         m_taskMap = new HashMap<String, Task>();
         
         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoWBS(false);

         m_projectFile.getProjectProperties().setFileApplication("GanttDesigner");
         m_projectFile.getProjectProperties().setFileType("GNT");

         m_eventManager.addProjectListeners(m_projectListeners);

         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser = factory.newSAXParser();
         XMLReader xmlReader = saxParser.getXMLReader();
         SAXSource doc = new SAXSource(xmlReader, new InputSource(stream));

         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();

         Gantt gantt = (Gantt) unmarshaller.unmarshal(doc);

         readProjectProperties(gantt);
//         readCalendars(gantt);
         readTasks(gantt);
//         readRelationships(gantt);

         //
         // Ensure that the unique ID counters are correct
         //
         config.updateUniqueCounters();

         return m_projectFile;
      }

      catch (ParserConfigurationException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      catch (JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      catch (SAXException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      finally
      {
         m_projectFile = null;
         m_eventManager = null;
         m_projectListeners = null;
         m_taskMap = null;
      }
   }

   private void readProjectProperties(Gantt gantt)
   {
      Gantt.File file = gantt.getFile();
      ProjectProperties props = m_projectFile.getProjectProperties();
      props.setLastSaved(file.getSaved());
      props.setCreationDate(file.getCreated());
      props.setName(file.getName());
   }

   private void readTasks(Gantt gantt)
   {      
     processTasks(gantt); 
     processPredecessors(gantt); 
   }
   
   private void processTasks(Gantt gantt)
   {
      for(Gantt.Tasks.Task ganttTask : gantt.getTasks().getTask())
      {
         String wbs = ganttTask.getID();
         ChildTaskContainer parentTask = getParentTask(wbs);
         
         Task task = parentTask.addTask();
         //ganttTask.getB() // bar type
         //ganttTask.getBC() // bar color
         task.setCost(ganttTask.getC());
         task.setName(ganttTask.getContent());
         task.setDuration(ganttTask.getD());
         task.setDeadline(ganttTask.getDL());
         //ganttTask.getH() // height
         //ganttTask.getIn(); // indent
         task.setWBS(wbs);
         task.setPercentageComplete(ganttTask.getPC());
         task.setStart(ganttTask.getS());
         //ganttTask.getU(); // Unknown
         //ganttTask.getVA(); // Valign
         
         m_taskMap.put(wbs, task);
      }
   }
   
   private void processPredecessors(Gantt gantt)
   {
      for(Gantt.Tasks.Task ganttTask : gantt.getTasks().getTask())
      {
         String predecessors = ganttTask.getP();
         if (predecessors !=  null && !predecessors.isEmpty())
         {
            Task task = m_taskMap.get(ganttTask.getID());
            for (String predecessor : predecessors.split(";"))
            {
               Task predecessorTask = m_taskMap.get(predecessor);
               task.addPredecessor(predecessorTask, RelationType.FINISH_START, ganttTask.getL());
            }
         }
      }
   }
   
   private String getParentWBS(String wbs)
   {
      String result;
      int index = wbs.lastIndexOf('.');
      if (index == -1)
      {
         result = null;
      }
      else
      {
         result = wbs.substring(0, index);
      }
      return result;
   }
   
   private ChildTaskContainer getParentTask(String wbs)
   {
      ChildTaskContainer result;
      String parentWbs = getParentWBS(wbs);
      if (parentWbs == null)
      {
         result = m_projectFile;
      }
      else
      {
         result = m_taskMap.get(parentWbs);
      }
      return result;
   }
   
   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
   Map<String, Task> m_taskMap;

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
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.ganttdesigner.schema", GanttDesignerReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
