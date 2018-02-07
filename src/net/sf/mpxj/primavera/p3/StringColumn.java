
package net.sf.mpxj.primavera.p3;

public class StringColumn extends AbstractColumn
{
   public StringColumn(String name, int offset, int length)
   {
      super(name, offset);
      m_length = length;
   }

   @Override public String read(int offset, byte[] data)
   {
      StringBuilder buffer = new StringBuilder();
      char c;

      for (int loop = 0; loop < m_length; loop++)
      {
         c = (char) data[offset + m_offset + loop];

         if (c == 0)
         {
            break;
         }

         buffer.append(c);
      }

      return buffer.toString().trim();
   }

   private final int m_length;
}
