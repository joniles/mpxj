
package net.sf.mpxj.turboproject;

import java.util.Date;

import net.sf.mpxj.common.DateHelper;

final class PEPUtility
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

   public static final String getString(byte[] data, int offset)
   {
      return getString(data, offset, data.length - offset);
   }

   public static final String getString(byte[] data, int offset, int maxLength)
   {
      StringBuilder buffer = new StringBuilder();
      char c;

      for (int loop = 0; loop < maxLength; loop++)
      {
         c = (char) data[offset + loop];

         if (c == 0)
         {
            break;
         }

         buffer.append(c);
      }

      return (buffer.toString());
   }

   public static final Date getStartDate(byte[] data, int offset)
   {
      Date result;
      long days = getShort(data, offset);

      if (days == 0x8000)
      {
         result = null;
      }
      else
      {
         result = DateHelper.getDateFromLong(EPOCH + (days * DateHelper.MS_PER_DAY));
      }

      return (result);
   }

   public static final Date getFinishDate(byte[] data, int offset)
   {
      Date result;
      long days = getShort(data, offset);

      if (days == 0x8000)
      {
         result = null;
      }
      else
      {
         result = DateHelper.getDateFromLong(EPOCH + ((days - 1) * DateHelper.MS_PER_DAY));
      }

      return (result);
   }

   private static final long EPOCH = 946598400000L;

}
