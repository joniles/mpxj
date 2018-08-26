
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;

final class SynchroUtility
{

   public static final int getInt(byte[] data, int offset)
   {
      int result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   public static final int getShort(byte[] data, int offset)
   {
      int result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 16; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   public static final int getInt(InputStream is) throws IOException
   {
      byte[] data = new byte[4];
      is.read(data);
      return getInt(data, 0);
   }

   public static final int getShort(InputStream is) throws IOException
   {
      byte[] data = new byte[2];
      is.read(data);
      return getShort(data, 0);
   }

   public static final String getString(InputStream is) throws IOException
   {
      int type = is.read();
      if (type != 1)
      {
         throw new IllegalArgumentException("Unexpected string format");
      }

      int length = is.read();
      if (length == 0xFF)
      {
         length = getShort(is);
      }

      byte[] stringData = new byte[length];
      is.read(stringData);
      return new String(stringData);
   }

   public static boolean getBoolean(InputStream is) throws IOException
   {
      return is.read() != 0;
   }
}
