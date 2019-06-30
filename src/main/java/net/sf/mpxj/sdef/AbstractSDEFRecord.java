
package net.sf.mpxj.sdef;

abstract class AbstractSDEFRecord implements SDEFRecord
{
   @Override public void read(String line)
   {
      int index = 0;
      int offset = 4;
      for (SDEFField field : getFieldDefinitions())
      {
         m_fields[index] = field.read(line, offset);
         offset += field.getLength();
      }
   }
   
   protected abstract SDEFField[] getFieldDefinitions();
   
   private final Object[] m_fields = new Object[getFieldDefinitions().length];
}
