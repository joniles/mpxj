
package net.sf.mpxj.fasttrack;

class FixedSizeItemsBlock
{

   public FixedSizeItemsBlock read(byte[] buffer, int offset)
   {
      // Offset to data
      offset += 2;

      int numberOfItems = FastTrackUtility.getInt(buffer, offset);
      offset += 4;

      FastTrackUtility.validateSize(numberOfItems);

      int itemLength = FastTrackUtility.getShort(buffer, offset);
      offset += 2;

      FastTrackUtility.validateSize(itemLength);

      // Offset to end
      offset += 4;

      m_data = new byte[numberOfItems][];
      for (int index = 0; index < m_data.length; index++)
      {
         byte[] item = new byte[itemLength];
         m_data[index] = item;

         FastTrackUtility.validateOffset(buffer, offset + itemLength);

         System.arraycopy(buffer, offset, item, 0, itemLength);
         offset += itemLength;
      }

      m_offset = offset;

      return this;
   }

   public byte[][] getData()
   {
      return m_data;
   }

   public int getOffset()
   {
      return m_offset;
   }

   private byte[][] m_data;
   private int m_offset;
}
