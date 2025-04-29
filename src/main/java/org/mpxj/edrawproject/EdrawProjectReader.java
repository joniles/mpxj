

package org.mpxj.edrawproject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.mpxj.edrawproject.schema.Document;
import org.xml.sax.SAXException;

import org.mpxj.EventManager;
import org.mpxj.MPXJException;
import org.mpxj.ProjectConfig;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.common.UnmarshalHelper;
import org.mpxj.reader.AbstractProjectStreamReader;

public final class EdrawProjectReader extends AbstractProjectStreamReader
{
   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();
         m_taskMap = new HashMap<>();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoWBS(false);

         m_projectFile.getProjectProperties().setFileApplication("Edraw Project");
         m_projectFile.getProjectProperties().setFileType("EDPX");

         addListenersToProject(m_projectFile);

         Document gantt = (Document) UnmarshalHelper.unmarshal(CONTEXT, stream);


         m_projectFile.readComplete();

         return m_projectFile;
      }

      catch (ParserConfigurationException | SAXException | JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      finally
      {
         m_projectFile = null;
         m_eventManager = null;
         m_taskMap = null;
      }
   }





   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
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
         CONTEXT = JAXBContext.newInstance("org.mpxj.edrawproject.schema", EdrawProjectReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
