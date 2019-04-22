
package net.sf.mpxj.merlin;

import java.io.InputStream;
import java.sql.SQLException;
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

import net.sf.mpxj.Day;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.merlin.schema.Project;
import net.sf.mpxj.reader.AbstractProjectReader;

public final class MerlinXmlReader extends AbstractProjectReader
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
         m_project = new ProjectFile();
         m_eventManager = m_project.getEventManager();

         ProjectConfig config = m_project.getProjectConfig();
         config.setAutoCalendarUniqueID(false);
         config.setAutoTaskUniqueID(false);
         config.setAutoResourceUniqueID(false);

         m_project.getProjectProperties().setFileApplication("Merlin");
         m_project.getProjectProperties().setFileType("XML");

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

         Project merlinProject = (Project) unmarshaller.unmarshal(doc);
         processProject(merlinProject);
         
         //
         // Ensure that the unique ID counters are correct
         //
         config.updateUniqueCounters();

         return (m_project);
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
         m_project = null;
      }
   }


   /**
    * Read project properties.
    */
   private void processProject(Project merlin)
   {
      ProjectProperties props = m_project.getProjectProperties();
      
      
      props.setWeekStartDay(DAY_MAP.get(merlin.getFirstDayOfWeek().getValue()));
      props.setScheduleFrom(merlin.getSchedulingDirection().getValue().equals("forward") ? ScheduleFrom.START : ScheduleFrom.FINISH);
      props.setMinutesPerDay(Integer.valueOf(NumberHelper.getInt(merlin.getHoursPerDay().getValue()) * 60));
      props.setDaysPerMonth(merlin.getDaysPerMonth().getValue());
      props.setMinutesPerWeek(Integer.valueOf(NumberHelper.getInt(merlin.getHoursPerWeek().getValue()) * 60));
      props.setCurrencySymbol(merlin.getCurrencySymbol().getValue());
      props.setName(merlin.getTitle());
      //props.setUniqueID(DatatypeConverter.parseUUID()); TODO
   }



   private static final Map<String, Day> DAY_MAP = new HashMap<String,Day>();
   static
   {
      DAY_MAP.put("monday", Day.MONDAY);
      DAY_MAP.put("tuesday", Day.TUESDAY);
      DAY_MAP.put("wednesday", Day.WEDNESDAY);
      DAY_MAP.put("thursday", Day.THURSDAY);
      DAY_MAP.put("friday", Day.FRIDAY);
      DAY_MAP.put("saturday", Day.SATURDAY);
      DAY_MAP.put("sunday", Day.SUNDAY);
   }
   
   private ProjectFile m_project;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;

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
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.merlin.schema", MerlinXmlReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
