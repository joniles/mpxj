package net.sf.mpxj.projectcommander;

import net.sf.mpxj.common.ByteArrayHelper;

class BlockPattern
{
   public BlockPattern(String name, byte... pattern)
   {
      m_name = name;
      m_pattern = pattern;
   }
   
   public BlockPattern(String name, byte[] data, int offset)
   {
      this(name, data[offset], data[offset+1]);
   }
   
   public String getName()
   {
      return m_name;
   }
   
   public byte[] getPattern()
   {
      return m_pattern;
   }
   
   @Override public String toString()
   {
      return m_name + ": " + ByteArrayHelper.hexdump(m_pattern, false);
   }
   
   private final String m_name;
   private final byte[] m_pattern;   
}
