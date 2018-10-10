
package net.sf.mpxj.synchro;

class SynchroTable
{
   public SynchroTable(String name, int offset)
   {
      m_name = name;
      m_offset = offset;
   }

   public int getOffset()
   {
      return m_offset;
   }

   public String getName()
   {
      return m_name;
   }

   public int getLength()
   {
      return m_length;
   }

   public void setLength(int length)
   {
      m_length = length;
   }

   @Override public String toString()
   {
      return "[SynchroTable\t name=" + m_name + "\toffset=" + m_offset + "\tlength=" + m_length + "]";
   }

   private String m_name;
   private int m_offset;
   private int m_length = -1;
}
