
package net.sf.mpxj.projectcommander;

class BlockReference
{
   public BlockReference(BlockPattern pattern, int index)
   {
      m_pattern = pattern;
      m_index = index;
   }

   public BlockPattern getPattern()
   {
      return m_pattern;
   }
   
   public int getIndex()
   {
      return m_index;
   }
   
   private final BlockPattern m_pattern;
   private final int m_index;
}
