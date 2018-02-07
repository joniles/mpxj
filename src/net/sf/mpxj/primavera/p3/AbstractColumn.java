
package net.sf.mpxj.primavera.p3;

public abstract class AbstractColumn implements ColumnDefinition
{
   public AbstractColumn(String name, int offset)
   {
      m_name = name;
      m_offset = offset;
   }

   @Override public String getName()
   {
      return m_name;
   }

   private final String m_name;
   protected final int m_offset;
}
