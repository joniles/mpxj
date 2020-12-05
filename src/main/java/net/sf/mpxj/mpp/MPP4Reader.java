

package net.sf.mpxj.mpp;

import java.io.IOException;

import org.apache.poi.poifs.filesystem.DirectoryEntry;

import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;

final class MPP4Reader implements MPPVariantReader
{
   @Override public void process(MPPReader reader, ProjectFile file, DirectoryEntry root) throws MPXJException, IOException
   {
      try
      {
         populateMemberData(reader, file, root);

         if (!reader.getReadPropertiesOnly())
         {
         }
      }

      finally
      {
         clearMemberData();
      }
   }

   /**
    * Populate member data used by the rest of the reader.
    *
    * @param reader parent file reader
    * @param file parent MPP file
    * @param root Root of the POI file system.
    */
   private void populateMemberData(MPPReader reader, ProjectFile file, DirectoryEntry root) throws IOException
   {
      m_reader = reader;
      m_root = root;
      m_file = file;
      m_eventManager = file.getEventManager();

      m_file.getProjectProperties().setMppFileType(Integer.valueOf(4));
   }

   /**
    * Clear transient member data.
    */
   private void clearMemberData()
   {
      m_reader = null;
      m_root = null;
      m_eventManager = null;
      m_file = null;
   }




   private MPPReader m_reader;
   private ProjectFile m_file;
   private EventManager m_eventManager;
   private DirectoryEntry m_root;
}
