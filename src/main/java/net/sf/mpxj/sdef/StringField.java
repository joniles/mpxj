
package net.sf.mpxj.sdef;

public class StringField implements SDEFField
{
   public StringField(String name, int length)
   {
      m_name = name;
      m_length = length;
   }

   public String getName()
   {
      return m_name;
   }

   public int getLength()
   {
      return m_length;
   }

   @Override public Object read(String line, int offset)
   {
      return line.substring(offset, offset + m_length);
   }

   private final String m_name;
   private final int m_length;
}
