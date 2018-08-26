
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

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

      String result;
      if (length == 0)
      {
         result = null;
      }
      else
      {
         byte[] stringData = new byte[length];
         is.read(stringData);
         result = new String(stringData);
      }
      return result;
   }

   public static boolean getBoolean(InputStream is) throws IOException
   {
      return is.read() != 0;
   }

   public static final UUID getUUID(InputStream is) throws IOException
   {
      byte[] data = new byte[16];
      is.read(data);

      long long1 = 0;
      long1 |= ((long) (data[3] & 0xFF)) << 56;
      long1 |= ((long) (data[2] & 0xFF)) << 48;
      long1 |= ((long) (data[1] & 0xFF)) << 40;
      long1 |= ((long) (data[0] & 0xFF)) << 32;
      long1 |= ((long) (data[5] & 0xFF)) << 24;
      long1 |= ((long) (data[4] & 0xFF)) << 16;
      long1 |= ((long) (data[7] & 0xFF)) << 8;
      long1 |= ((long) (data[6] & 0xFF)) << 0;

      long long2 = 0;
      long2 |= ((long) (data[8] & 0xFF)) << 56;
      long2 |= ((long) (data[9] & 0xFF)) << 48;
      long2 |= ((long) (data[10] & 0xFF)) << 40;
      long2 |= ((long) (data[11] & 0xFF)) << 32;
      long2 |= ((long) (data[12] & 0xFF)) << 24;
      long2 |= ((long) (data[13] & 0xFF)) << 16;
      long2 |= ((long) (data[14] & 0xFF)) << 8;
      long2 |= ((long) (data[15] & 0xFF)) << 0;

      return new UUID(long1, long2);
   }
}
