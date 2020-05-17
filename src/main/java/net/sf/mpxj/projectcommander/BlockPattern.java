package net.sf.mpxj.projectcommander;

import java.util.Set;

import net.sf.mpxj.common.ByteArrayHelper;

class BlockPattern
{
   public BlockPattern(String name, BlockPatternValidator validator, byte... pattern)
   {
      m_name = name;
      m_validator = validator;
      m_pattern = pattern;      
   }

   public BlockPattern(String name, byte... pattern)
   {
      this(name, null, pattern);
   }
   
   public BlockPattern(String name, byte[] data, int offset)
   {
      this(name, data[offset], data[offset+1]);
   }

   public BlockPattern(String name, BlockPatternValidator validator, byte[] data, int offset)
   {
      this(name, validator, data[offset], data[offset+1]);
   }

   public String getName()
   {
      return m_name;
   }
   
   public byte[] getPattern()
   {
      return m_pattern;
   }
   
   public boolean getValid(Set<String> matchedPatternNames)
   {
      return m_validator == null ? true : m_validator.valid(matchedPatternNames);
   }
   
   @Override public String toString()
   {
      return m_name + ": " + ByteArrayHelper.hexdump(m_pattern, false);
   }
   
   private final String m_name;
   private final byte[] m_pattern;
   private final BlockPatternValidator m_validator;
}
