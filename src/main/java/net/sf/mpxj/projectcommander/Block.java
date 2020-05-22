package net.sf.mpxj.projectcommander;

import java.util.ArrayList;
import java.util.List;

class Block
{
   public Block(String name, byte[] data)
   {
      m_name = name;
      m_data = data;
   }
   
   public String getName()
   {
      return m_name;
   }
   
   public byte[] getData()
   {
      return m_data;
   }
   
   public List<Block> getChildBlocks()
   {
      return m_childBlocks;
   }

   public void dumpBlock(String prefix)
   {
      System.out.println(prefix + getName());
      prefix += " ";
      for (Block childBlock : m_childBlocks)
      {
         childBlock.dumpBlock(prefix);
      }
   }

   private final String m_name;
   private final byte[] m_data;
   private final List<Block> m_childBlocks = new ArrayList<>();
}
