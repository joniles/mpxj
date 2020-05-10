package net.sf.mpxj.projectcommander;

class BlockPattern
{
   public BlockPattern(String name, byte... pattern)
   {
      m_name = name;
      m_pattern = pattern;
   }
   
   public String getName()
   {
      return m_name;
   }
   
   public byte[] getPattern()
   {
      return m_pattern;
   }
   
   private final String m_name;
   private final byte[] m_pattern;   
}
