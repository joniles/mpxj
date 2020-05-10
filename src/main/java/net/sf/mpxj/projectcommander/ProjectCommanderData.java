
package net.sf.mpxj.projectcommander;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.sf.mpxj.common.ByteArrayHelper;

public class ProjectCommanderData
{
   public static void main(String[] argv) throws Exception
   {
      ProjectCommanderData data = new ProjectCommanderData();
      data.setLogFile("c:/temp/project-commander.log");
      data.process(new File("c:/temp/project1.pc"));
   }

   public void process(File file) throws Exception
   {
      openLogFile();

      int blockIndex = 0;
      int length = (int) file.length();
      m_buffer = new byte[length];
      FileInputStream is = new FileInputStream(file);
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

      List<BlockReference> blocks = new ArrayList<>();
      for (int index = 0; index < m_buffer.length - 11; index++)
      {
         BlockPattern block = matchPattern(BLOCK_PATTERNS, index);
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

   private void readBlock(BlockReference block, int blockIndex, int startIndex, int blockLength) throws Exception
   {
      String name;
      if (block == null)
      {
         name = "First Block";
      }
      else
      {
         name = block.getPattern().getName();
      }
      logBlock(name, blockIndex, startIndex, blockLength);
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

   private static final BlockPattern[] BLOCK_PATTERNS =
   {
      // Named Class
//      new BlockPattern("Named Class", (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00),

      // Named Class
      new BlockPattern("Named Class", (byte) 0xFF, (byte) 0xFF, (byte) 0x01, (byte) 0x00),

      // Named Class
      new BlockPattern("Named Class", (byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x00),

      // CSymbol 0x8001 2 strings + 16 bytes
      new BlockPattern("CSymbol", (byte) 0x01, (byte) 0x80),

      // Unknown1 6 bytes?
      new BlockPattern("Unknown1", (byte) 0x02, (byte) 0x80),

      // CDayFlag  0x8007 4 bytes
      new BlockPattern("CDayFlag", (byte) 0x07, (byte) 0x80),

      // CCalendar  0x8005
      new BlockPattern("CCalendar", (byte) 0x05, (byte) 0x80),

      // CResource 0x801E
      new BlockPattern("CResource", (byte) 0x1E, (byte) 0x80),

      // CResourceTask 0x8021
      new BlockPattern("CResourceTask", (byte) 0x21, (byte) 0x80),

      // CBaselineData 0x8023
      new BlockPattern("CBaselineData", (byte) 0x23, (byte) 0x80),

      // CBar 0x8025
      new BlockPattern("CBar", (byte) 0x25, (byte) 0x80),

      // CLayout
      // CFilterObject
      // CResourceDescription
      // CTotalResourceWork
      // CResourceAllocationColour
      // CResourceHistogram
      // CShadeCalendar
      // CSecondTimescaleCalendar
      // CName

      // CID 0x801D
      new BlockPattern("CID", (byte) 0x1D, (byte) 0x80),

      // CReportGroup

      // CReportData 0x814F
      new BlockPattern("CReportData", (byte) 0x4F, (byte) 0x81),

      // Unknown2 0x8120
      new BlockPattern("Unknown2", (byte) 0x20, (byte) 0x81),

      // Unknown3 0x8122
      new BlockPattern("Unknown3", (byte) 0x22, (byte) 0x81),

      // Unknown4 0x8003
      new BlockPattern("Unknown4", (byte) 0x03, (byte) 0x80),
      
      // CLink 0x858A
      new BlockPattern("CLink", (byte) 0x8A, (byte) 0x85),
      
      // CTask? 0x8574
      new BlockPattern("CTask?", (byte) 0x74, (byte) 0x85),
      
      // CUsageTask? 0x8574
      new BlockPattern("CUsageTask?", (byte) 0x7F, (byte) 0x85),

      // Unknown5 0x857B
      new BlockPattern("Unknown5", (byte) 0x7B, (byte) 0x85),
      
      // CFormatCellInfo 0x89B4
      new BlockPattern("CFormatCellInfo", (byte) 0xB4, (byte) 0x89)

   };  
}
