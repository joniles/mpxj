
package net.sf.mpxj.projectcommander;

import java.util.Date;

import net.sf.mpxj.Duration;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.DateHelper;

class DatatypeConverter
{
   public static final int getByte(byte[] data, int offset)
   {
      int result = (data[offset] & 0xFF);
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

   public static final void setShort(byte[] data, int offset, int value)
   {
      data[offset] = (byte) (value & 0x00FF);
      data[offset + 1] = (byte) ((value & 0xFF00) >> 8);
   }

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

   public static final int getShort(byte[] data, int offset, int defaultValue)
   {
      return offset + 2 > data.length ? defaultValue : getShort(data, offset);
   }

   public static final int getInt(byte[] data, int offset, int defaultValue)
   {
      return offset + 4 > data.length ? defaultValue : getInt(data, offset);
   }

   public static final String getString(byte[] data, int offset, String defaultValue)
   {
      return offset >= data.length ? defaultValue : getString(data, offset);
   }

   public static final String getTwoByteLengthString(byte[] data, int offset)
   {
      int length = getShort(data, offset);
      String result;
      if (length == 0)
      {
         result = null;
      }
      else
      {
         result = new String(data, offset + 2, length);
      }
      return result;
   }

   public static final String getString(byte[] data, int offset)
   {
      int length = getByte(data, offset);
      String result;
      if (length == 0 || length + offset + 1 > data.length)
      {
         result = null;
      }
      else
      {
         result = new String(data, offset + 1, length);
      }
      return result;
   }

   public static final Duration getDuration(byte[] data, int offset)
   {
      int durationInMinutes = getInt(data, offset, 0);
      return Duration.getInstance(durationInMinutes / 60, TimeUnit.HOURS);
   }

   public static final Date getTimestamp(byte[] data, int offset)
   {
      long timestampInSeconds = DatatypeConverter.getInt(data, offset, 0);
      long timestampInMilliseconds = timestampInSeconds * 1000;
      return DateHelper.getTimestampFromLong(timestampInMilliseconds);
   }

   public static final RelationType getRelationType(byte[] data, int offset)
   {
      RelationType result;
      int value = getByte(data, offset);
      switch (value)
      {
         case 5:
         {
            result = RelationType.START_FINISH;
            break;
         }

         case 6:
         {
            result = RelationType.START_START;
            break;
         }

         case 7:
         {
            result = RelationType.FINISH_FINISH;
            break;
         }

         case 8:
         default:
         {
            result = RelationType.FINISH_START;
            break;
         }
      }

      return result;
   }
}
