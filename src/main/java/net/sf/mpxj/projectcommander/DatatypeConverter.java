package net.sf.mpxj.projectcommander;

class DatatypeConverter
{
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
         result = new String(data, offset+2, length);
      }
      return result;
   }
}
