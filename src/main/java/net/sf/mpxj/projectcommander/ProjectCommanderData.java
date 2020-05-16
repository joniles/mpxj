
package net.sf.mpxj.projectcommander;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.mpxj.common.ByteArrayHelper;

class ProjectCommanderData
{
   public void process(InputStream is) throws IOException
   {
      openLogFile();
      populateBuffer(is);
      identifyBlockHeaders();
      populateBlocks();
      closeLogFile();
      dumpBlocks();
      
      m_buffer = null;
   }

   public List<Block> getBlocks()
   {
      return m_blocks;
   }
   
   private void dumpBlocks()
   {
      for (Block block : m_blocks)
      {
         dumpBlock("", block);
      }      
   }
   
   private void dumpBlock(String prefix, Block block)
   {
      System.out.println(prefix + block.getName());
      prefix += " ";
      for (Block childBlock : block.getChildBlocks())
      {
         dumpBlock(prefix, childBlock);
      }
   }
   
   private void populateBuffer(InputStream is) throws IOException
   {
      int length = is.available();
      m_buffer = new byte[length];
     
      try
      {
         int bytesRead = is.read(m_buffer);
         if (bytesRead != length)
         {
            throw new RuntimeException("Read count different");
         }
      }
      finally
      {
         is.close();
      }
   }
      
   private List<BlockPattern> selectBlockPatterns()
   {
      List<BlockPattern> blockPatterns = new ArrayList<>();

      switch (m_buffer[0])
      {
         case 0x00:
         {
            blockPatterns.addAll(Arrays.asList(BLOCK_PATTERNS_0));
            break;
         }

         case 0x02:
         {
            blockPatterns.addAll(Arrays.asList(BLOCK_PATTERNS_2));
            break;
         }

         default:
         {
            throw new RuntimeException("Unexpected first byte: " + m_buffer[0]);
         }
      }
      return blockPatterns;
   }
  
   private List<BlockReference> populateBlockReferences()
   {
      List<BlockPattern> blockPatterns = selectBlockPatterns();

      List<BlockReference> blockReferences = new ArrayList<>();
      for (int index = 0; index < m_buffer.length - 11; index++)
      {
         BlockPattern block = matchPattern(blockPatterns, index);
         if (block != null)
         {
            blockReferences.add(new BlockReference(block, index));
         }
      }

      return blockReferences;
   }
   
   private void populateBlocks()
   {
      List<BlockReference> blockReferences = populateBlockReferences();

      int blockIndex = 0;
      int startIndex = 0;
      BlockReference startBlock = null;
      for (BlockReference block : blockReferences)
      {
         int endIndex = block.getIndex();
         int blockLength = endIndex - startIndex;
         readBlock(startBlock, blockIndex, startIndex, blockLength);
         startIndex = endIndex;
         startBlock = block;
         ++blockIndex;
      }

      int blockLength = m_buffer.length - startIndex;
      readBlock(startBlock, blockIndex, startIndex, blockLength);   
   }
   
   private void readBlock(BlockReference blockReference, int blockIndex, int startIndex, int blockLength)
   {
      if (blockLength != 0)
      {
         int offset;
         String name;
         if (blockReference == null)
         {
            name = "First Block";
            offset = 0;
         }
         else
         {
            if (blockReference.getPattern().getName() == null)
            {
               name = DatatypeConverter.getTwoByteLengthString(m_buffer, startIndex + 4);              
               offset = 6 + (name == null ? 0 : name.length());
            }
            else
            {
               name = blockReference.getPattern().getName();
               offset = 2;
            }
         }

         byte[] data = new byte[blockLength - offset];
         System.arraycopy(m_buffer, startIndex + offset, data, 0, data.length);
         Block block = new Block(name, data);
         addBlockToHierarchy(block);
         logBlock(name, blockIndex, startIndex, blockLength);         
      }
   }

   private void addBlockToHierarchy(Block block)
   {
      if (m_parentStack.isEmpty())
      {
         m_blocks.add(block);
         addParentBlockToHierarchy(block);
      }
      else
      {
         Block parentBlock = m_parentStack.getFirst();
         Set<String> set = EXPECTED_CHILD_CLASSES.get(parentBlock.getName());
         if (set != null && set.contains(block.getName()))
         {
            parentBlock.getChildBlocks().add(block);
            addParentBlockToHierarchy(block);
         }
         else
         {
            m_parentStack.pop();
            addBlockToHierarchy(block);
         }
      }
   }
   
   private void addParentBlockToHierarchy(Block block)
   {
      if (EXPECTED_CHILD_CLASSES.containsKey(block.getName()))
      {
         m_parentStack.push(block);
      }
   }
   
   private final BlockPattern matchPattern(List<BlockPattern> blocks, int bufferIndex)
   {
      BlockPattern match = null;
      for (BlockPattern block : blocks)
      {
         if (matchPattern(block.getPattern(), bufferIndex))
         {
            match = block;
            break;
         }
      }
      return match;
   }

   public void identifyBlockHeaders()
   {
      int index = findFirstMatch(REPORT_DATA_FINGERPRINT);
      if (index == -1)
      {
         System.out.println("No CReportData fingerprint");
      }
      else
      {
         BlockPattern x = new BlockPattern("CReportData", m_buffer, index-2);
         System.out.println(x);
      }
   }
   
   private int findFirstMatch(byte[] pattern)
   {
      int result = -1;
      for(int bufferIndex=0; bufferIndex < m_buffer.length - pattern.length; bufferIndex++)
      {
         if (matchPattern(pattern, bufferIndex)) 
         {
            result = bufferIndex;
            break;
         }
      }
      return result;
   }
   
   private final boolean matchPattern(byte[] pattern, int bufferIndex)
   {
      boolean result = true;
      int index = 0;
      for (byte b : pattern)
      {
         if (b != m_buffer[bufferIndex + index])
         {
            result = false;
            break;
         }
         ++index;
      }
      return result;
   }
   
   /**
    * Provide the file path for rudimentary logging to support development.
    *
    * @param logFile full path to log file
    */
   public void setLogFile(String logFile)
   {
      m_logFile = logFile;
   }

   /**
    * Open the log file for writing.
    */
   private void openLogFile() throws IOException
   {
      if (m_logFile != null)
      {
         System.out.println("ProjectCommanderLogger Configured");
         m_log = new PrintWriter(new FileWriter(m_logFile));
      }
   }

   /**
    * Close the log file.
    */
   private void closeLogFile()
   {
      if (m_logFile != null)
      {
         m_log.flush();
         m_log.close();
      }
   }

   private void logBlock(String name, int blockIndex, int startIndex, int blockLength)
   {
      if (m_log != null)
      {
         m_log.println("Block Index: " + blockIndex);
         m_log.println("Block Name: " + name);
         m_log.println("Length: " + blockLength + " (" + Integer.toHexString(blockLength) + ")");
         m_log.println();
         m_log.println(ByteArrayHelper.hexdump(m_buffer, startIndex, blockLength, true, 16, ""));
         m_log.flush();
      }
   }

   private byte[] m_buffer;
   private String m_logFile;
   private PrintWriter m_log;
   private List<Block> m_blocks = new ArrayList<>();
   private Deque<Block> m_parentStack = new ArrayDeque<>();

   private static final BlockPattern[] BLOCK_PATTERNS_0 =
   {
      new BlockPattern(null, (byte) 0xFF, (byte) 0xFF, (byte) 0x01, (byte) 0x00),
      new BlockPattern(null, (byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x00),
      new BlockPattern("CSymbol", (byte) 0x01, (byte) 0x80),
      new BlockPattern("Unknown1", (byte) 0x02, (byte) 0x80),
      new BlockPattern("CCalendar", (byte) 0x05, (byte) 0x80),
      new BlockPattern("CDayFlag", (byte) 0x07, (byte) 0x80),      
      new BlockPattern("CResource", (byte) 0x1E, (byte) 0x80),
      
      new BlockPattern("CResourceTask", (byte) 0x21, (byte) 0x80),
      new BlockPattern("CBaselineData", (byte) 0x04, (byte) 0x84),
      new BlockPattern("CBar", (byte) 0x24, (byte) 0x80),
      new BlockPattern("CID", (byte) 0x1A, (byte) 0x80),          
      new BlockPattern("CReportGroup", (byte) 0x51, (byte) 0x80),
      new BlockPattern("CReportData", (byte) 0x53, (byte) 0x80),
      new BlockPattern("View", (byte) 0x29, (byte) 0x80),
      new BlockPattern("CFilterObject", (byte) 0x2B, (byte) 0x80),
      new BlockPattern("CShape", (byte) 0x03, (byte) 0x80),
      new BlockPattern("CLink", (byte) 0x0F, (byte) 0x84),
      new BlockPattern("CTask", (byte) 0xFB, (byte) 0x83),
      new BlockPattern("CUsageTask", (byte) 0x0B, (byte) 0x84),
      new BlockPattern("CFormatCellInfo", (byte) 0xB4, (byte) 0x89)
   };

   private static final BlockPattern[] BLOCK_PATTERNS_2 =
   {
      new BlockPattern(null, (byte) 0xFF, (byte) 0xFF, (byte) 0x01, (byte) 0x00),
      new BlockPattern(null, (byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x00),
      new BlockPattern("CSymbol", (byte) 0x01, (byte) 0x80),
      new BlockPattern("Unknown1", (byte) 0x02, (byte) 0x80),
      new BlockPattern("CCalendar", (byte) 0x05, (byte) 0x80),
      new BlockPattern("CDayFlag", (byte) 0x07, (byte) 0x80),      
      new BlockPattern("CResource", (byte) 0x1E, (byte) 0x80),
      
      new BlockPattern("CResourceTask", (byte) 0x21, (byte) 0x80),
      new BlockPattern("CBaselineData", (byte) 0x23, (byte) 0x80),
      new BlockPattern("CBar", (byte) 0x25, (byte) 0x80),
      new BlockPattern("CID", (byte) 0x1D, (byte) 0x80),
      new BlockPattern("CReportGroup", (byte) 0x4D, (byte) 0x81),
      new BlockPattern("CReportData", (byte) 0x4F, (byte) 0x81),
      new BlockPattern("View", (byte) 0x20, (byte) 0x81),
      new BlockPattern("CFilterObject", (byte) 0x22, (byte) 0x81),
      new BlockPattern("CShape", (byte) 0x03, (byte) 0x80),
      new BlockPattern("CLink", (byte) 0x8A, (byte) 0x85),
      new BlockPattern("CTask", (byte) 0x74, (byte) 0x85),
      new BlockPattern("CUsageTask", (byte) 0x7F, (byte) 0x85),
      new BlockPattern("CFormatCellInfo", (byte) 0xB4, (byte) 0x89),
      new BlockPattern("Unknown6", (byte) 0x28, (byte) 0x81)
   };

   
   private static final byte[] REPORT_DATA_FINGERPRINT = { 0x05, 0x42, 0x61, 0x73, 0x69, 0x63, 0x05, 0x42, 0x61, 0x73, 0x69 };
   
   private static final Map<String, Set<String>> EXPECTED_CHILD_CLASSES= new HashMap<>();
   static 
   {
      EXPECTED_CHILD_CLASSES.put("CCalendar", new HashSet<>(Arrays.asList("CDayFlag")));
      EXPECTED_CHILD_CLASSES.put("CResource", new HashSet<>(Arrays.asList("CSymbol", "CResourceTask", "CBaselineData", "CBar", "CCalendar")));
      EXPECTED_CHILD_CLASSES.put("CTask", new HashSet<>(Arrays.asList("CPlanObject", "CCalendar", "CBaselineIndex", "CBaselineData", "CBar", "CUsageTask", "CLink")));
      EXPECTED_CHILD_CLASSES.put("CUsageTask", new HashSet<>(Arrays.asList("CBaselineData")));           
   }  
}
