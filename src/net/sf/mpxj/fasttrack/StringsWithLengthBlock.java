
package net.sf.mpxj.fasttrack;

class StringsWithLengthBlock
{

   public StringsWithLengthBlock read(byte[] buffer, int offset, boolean inclusive)
   {
      int numberOfItems = FastTrackUtility.getInt(buffer, offset);
      offset += 4;

      FastTrackUtility.validateSize(numberOfItems);

      if (inclusive)
      {
         ++numberOfItems;
      }

      m_data = new String[numberOfItems];
      for (int index = 0; index < m_data.length; index++)
      {
         // Two bytes
         offset += 2;
         int itemNameLength = FastTrackUtility.getInt(buffer, offset);
         offset += 4;
         m_data[index] = new String(buffer, offset, itemNameLength, FastTrackUtility.UTF16LE);
         offset += itemNameLength;
      }

      m_offset = offset;

      return this;
   }

   public String[] getData()
   {
      return m_data;
   }

   public int getOffset()
   {
      return m_offset;
   }

   private String[] m_data;
   private int m_offset;
}
