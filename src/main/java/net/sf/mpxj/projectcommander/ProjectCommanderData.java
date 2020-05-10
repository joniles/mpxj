
package net.sf.mpxj.projectcommander;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.sf.mpxj.common.ByteArrayHelper;

class ProjectCommanderData
{
   public void process(InputStream is) throws IOException
   {
      openLogFile();

      int blockIndex = 0;
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

      BlockPattern[] blockPatterns;
      
      switch (m_buffer[0])
      {
         case 0x00:
         {
            blockPatterns = BLOCK_PATTERNS_0;
            break;
         }
         
         case 0x02:
         {
            blockPatterns = BLOCK_PATTERNS_2;
            break;
         }
         
         default:
         {
            throw new RuntimeException("Unexpected first byte: " + m_buffer[0]);
         }
      }
      
      List<BlockReference> blocks = new ArrayList<>();
      for (int index = 0; index < m_buffer.length - 11; index++)
      {
         BlockPattern block = matchPattern(blockPatterns, index);
         if (block != null)
         {
            blocks.add(new BlockReference(block, index));
         }
      }

      int startIndex = 0;
      BlockReference startBlock = null;
      for (BlockReference block : blocks)
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

      closeLogFile();
   }

   private void readBlock(BlockReference block, int blockIndex, int startIndex, int blockLength)
   {
      if (blockLength != 0)
      {
         String name;
         if (block == null)
         {
            name = "First Block";
         }
         else
         {
            if (block.getPattern().getName() == null)
            {
               name = DatatypeConverter.getString(m_buffer, startIndex + 4);
            }
            else
            {
               name = block.getPattern().getName();
            }
         }
         System.out.println(name);
         logBlock(name, blockIndex, startIndex, blockLength);
      }
   }

   private final BlockPattern matchPattern(BlockPattern[] blocks, int bufferIndex)
   {
      BlockPattern match = null;
      for (BlockPattern block : blocks)
      {
         int index = 0;
         match = block;
         for (byte b : block.getPattern())
         {
            if (b != m_buffer[bufferIndex + index])
            {
               match = null;
               break;
            }
            ++index;
         }
         if (match != null)
         {
            break;
         }
      }
      return match;
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

   private static final BlockPattern[] BLOCK_PATTERNS_0 =
   {
      new BlockPattern(null, (byte) 0xFF, (byte) 0xFF, (byte) 0x01, (byte) 0x00),
      new BlockPattern(null, (byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x00),
      new BlockPattern("CSymbol", (byte) 0x01, (byte) 0x80),
      new BlockPattern("Unknown1", (byte) 0x02, (byte) 0x80),
      new BlockPattern("CDayFlag", (byte) 0x07, (byte) 0x80),
      new BlockPattern("CCalendar", (byte) 0x05, (byte) 0x80),
      new BlockPattern("CResource", (byte) 0x1E, (byte) 0x80),
      new BlockPattern("CResourceTask", (byte) 0x21, (byte) 0x80),
      new BlockPattern("CBaselineData", (byte) 0x04, (byte) 0x84),
      new BlockPattern("CBar", (byte) 0x24, (byte) 0x80),
      new BlockPattern("CID", (byte) 0x1A, (byte) 0x80),
      new BlockPattern("CReportData", (byte) 0x53, (byte) 0x80),
      new BlockPattern("Unknown2", (byte) 0x29, (byte) 0x80),
      new BlockPattern("Unknown3", (byte) 0x2B, (byte) 0x80),
      new BlockPattern("Unknown4", (byte) 0x03, (byte) 0x80),
      new BlockPattern("CLink", (byte) 0x0F, (byte) 0x84),
      new BlockPattern("CTask?", (byte) 0xFB, (byte) 0x83),
      new BlockPattern("CUsageTask?", (byte) 0x0B, (byte) 0x84),
      new BlockPattern("Unknown5", (byte) 0x02, (byte) 0x84),
      new BlockPattern("CFormatCellInfo", (byte) 0xB4, (byte) 0x89)
   };

   private static final BlockPattern[] BLOCK_PATTERNS_2 =
   {
      new BlockPattern(null, (byte) 0xFF, (byte) 0xFF, (byte) 0x01, (byte) 0x00),
      new BlockPattern(null, (byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x00),
      new BlockPattern("CSymbol", (byte) 0x01, (byte) 0x80),
      new BlockPattern("Unknown1", (byte) 0x02, (byte) 0x80),
      new BlockPattern("CDayFlag", (byte) 0x07, (byte) 0x80),
      new BlockPattern("CCalendar", (byte) 0x05, (byte) 0x80),
      new BlockPattern("CResource", (byte) 0x1E, (byte) 0x80),
      new BlockPattern("CResourceTask", (byte) 0x21, (byte) 0x80),
      new BlockPattern("CBaselineData", (byte) 0x23, (byte) 0x80),
      new BlockPattern("CBar", (byte) 0x25, (byte) 0x80),
      new BlockPattern("CID", (byte) 0x1D, (byte) 0x80),
      new BlockPattern("CReportData", (byte) 0x4F, (byte) 0x81),
      new BlockPattern("Unknown2", (byte) 0x20, (byte) 0x81),
      new BlockPattern("Unknown3", (byte) 0x22, (byte) 0x81),
      new BlockPattern("Unknown4", (byte) 0x03, (byte) 0x80),
      new BlockPattern("CLink", (byte) 0x8A, (byte) 0x85),
      new BlockPattern("CTask?", (byte) 0x74, (byte) 0x85),
      new BlockPattern("CUsageTask?", (byte) 0x7F, (byte) 0x85),
      new BlockPattern("Unknown5", (byte) 0x7B, (byte) 0x85),
      new BlockPattern("CFormatCellInfo", (byte) 0xB4, (byte) 0x89),
      new BlockPattern("Unknown6", (byte) 0x28, (byte) 0x81)
   };
}
