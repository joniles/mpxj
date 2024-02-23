package net.sf.mpxj.openplan;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

abstract class AbstractReader
{
   public AbstractReader(DirectoryEntry dir, String name)
   {
      try
      {
         m_is = new DocumentInputStream((DocumentEntry) dir.getEntry(name));
      }
      catch (IOException e)
      {
         throw new OpenPlanException(e);
      }
   }

   protected int getInt()
   {
      try
      {
         int result = 0;
         for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
         {
            result |= ((m_is.read() & 0xff)) << shiftBy;
         }
         return result;
      }
      catch (IOException ex)
      {
         throw new OpenPlanException(ex);
      }
   }

   protected int getShort()
   {
      try
      {
         int result = 0;
         for (int shiftBy = 0; shiftBy < 16; shiftBy += 8)
         {
            result |= ((m_is.read() & 0xff)) << shiftBy;
         }
         return result;
      }
      catch (IOException ex)
      {
         throw new OpenPlanException(ex);
      }
   }


   protected int getByte()
   {
      try
      {
         return m_is.read();
      }
      catch (IOException ex)
      {
         throw new OpenPlanException(ex);
      }
   }

   protected String getString()
   {
      try
      {
         int length = getByte();
         if (length == 0)
         {
            return null;
         }

         if (length == 255)
         {
            length = getShort();
         }

         byte[] bytes = new byte[length];
         if (m_is.read(bytes) != length)
         {
            throw new OpenPlanException("Failed to read expected number of bytes");
         }

         return new String(bytes);
      }

      catch (IOException ex)
      {
         throw new OpenPlanException(ex);
      }
   }

   private final InputStream m_is;
}
