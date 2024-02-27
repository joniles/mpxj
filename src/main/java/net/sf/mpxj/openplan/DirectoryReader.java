
package net.sf.mpxj.openplan;

import java.io.FileNotFoundException;

import org.apache.poi.poifs.filesystem.DirectoryEntry;

abstract class DirectoryReader
{
   protected DirectoryEntry getDirectoryEntry(DirectoryEntry root, String name)
   {
      try
      {
         return (DirectoryEntry) root.getEntry(name);
      }

      catch (FileNotFoundException e)
      {
         throw new OpenPlanException(e);
      }
   }
}
