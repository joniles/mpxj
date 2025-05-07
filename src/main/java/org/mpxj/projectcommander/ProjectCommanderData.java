/*
 * file:       ProjectCommanderData.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       24/05/2020
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj.projectcommander;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.DebugLogPrintWriter;
import org.mpxj.common.InputStreamHelper;

/**
 * Reads a Project Commander file an returns a hierarchical list of blocks.
 */
final class ProjectCommanderData
{
   /**
    * Read a Project Commander file form an input stream.
    *
    * @param is input stream
    */
   public void process(InputStream is) throws IOException
   {
      openLogFile();
      populateBuffer(is);
      populateBlocks();
      updateHierarchy();
      closeLogFile();

      m_buffer = null;
   }

   /**
    * Retrieve the blocks read from the Project Commander file.
    *
    * @return list of blocks
    */
   public List<Block> getBlocks()
   {
      return m_blocks;
   }

   /**
    * Reorganise child blocks into the expected hierarchy.
    */
   private void updateHierarchy()
   {
      m_blocks.stream().filter(x -> "CTask".equals(x.getName())).forEach(this::updateHierarchy);
   }

   /**
    * Reorganise child blocks into the expected hierarchy.
    *
    * @param block root block
    */
   private void updateHierarchy(Block block)
   {
      reparentBlocks(block, "CBar", "CLink");
      reparentBlocks(block, "CBaselineData", "CBar");
   }

   /**
    * Move child blocks under the correct parent blocks.
    *
    * @param block root block
    * @param parentName parent block name
    * @param childName child block name
    */
   private void reparentBlocks(Block block, String parentName, String childName)
   {
      Block lastParent = null;
      Iterator<Block> iter = block.getChildBlocks().iterator();
      while (iter.hasNext())
      {
         Block child = iter.next();
         if (parentName.equals(child.getName()))
         {
            lastParent = child;
         }
         else
         {
            if (childName.equals(child.getName()) && lastParent != null)
            {
               iter.remove();
               lastParent.getChildBlocks().add(child);
            }
         }
      }
   }

   /**
    * Read the file contents into a byte array buffer.
    *
    * @param is input stream
    */
   private void populateBuffer(InputStream is) throws IOException
   {
      try
      {
         m_buffer = InputStreamHelper.readAvailable(is);
      }

      finally
      {
         is.close();
      }
   }

   /**
    * Generate the set of patterns representing block starts.
    *
    * @return list of block patterns
    */
   private List<BlockPattern> selectBlockPatterns()
   {
      Map<String, BlockPattern> map = new HashMap<>();

      // Insert defaults
      Arrays.stream(BLOCK_PATTERNS).forEach(pattern -> map.put(pattern.getName(), pattern));

      m_usageFingerprint = extractFingerprint("CResourceTask", false, 9);

      determineReportDataBlockBoundary(map);
      determineReportGroupBlockBoundary(map);
      determineResourceTaskBlockBoundary(map);
      determineViewBlockBoundary(map);
      determineResourceBlockBoundary(map);
      determineTaskBlockBoundary(map);
      determineLinkBlockBoundary(map);
      determineFilterObjectBlockBoundary(map);

      List<BlockPattern> blockPatterns = new ArrayList<>(Arrays.asList(NAMED_BLOCK_PATTERNS));
      map.values().stream().filter(Objects::nonNull).forEach(blockPatterns::add);

      logPatterns(blockPatterns);

      return blockPatterns;
   }

   /**
    * Heuristic method to determine the CReportData block boundary.
    *
    * @param map block pattern map
    */
   private void determineReportDataBlockBoundary(Map<String, BlockPattern> map)
   {
      map.put("CReportData", identifyPattern("CReportData", null, REPORT_DATA_FINGERPRINT));
   }

   /**
    * Heuristic method to determine the CReportGroup block boundary.
    *
    * @param map block pattern map
    */
   private void determineReportGroupBlockBoundary(Map<String, BlockPattern> map)
   {
      map.put("CReportGroup", identifyPattern("CReportGroup", null, REPORT_GROUP_FINGERPRINT));
   }

   /**
    * Heuristic method to determine the CResourceTask block boundary.
    *
    * @param map block pattern map
    */
   private void determineResourceTaskBlockBoundary(Map<String, BlockPattern> map)
   {
      map.put("CResourceTask", identifyPattern("CResourceTask", (set) -> !set.contains("CReportGroup"), m_usageFingerprint));
   }

   /**
    * Heuristic method to determine the View block boundary.
    *
    * @param map block pattern map
    */
   private void determineViewBlockBoundary(Map<String, BlockPattern> map)
   {
      map.put("View", identifyPattern("View", 0, null, VIEW_FINGERPRINT));
   }

   /**
    * Heuristic method to determine the CResource block boundary.
    *
    * @param map block pattern map
    */
   private void determineResourceBlockBoundary(Map<String, BlockPattern> map)
   {
      BlockPattern test = map.get("CResourceTask");
      if (test == null)
      {
         logMessage("Unable to calculate CResource, no CResourceTask found");
      }
      else
      {
         int value = DatatypeConverter.getShort(test.getPattern(), 0);
         value = value - 3;
         byte[] patternBytes = new byte[2];
         DatatypeConverter.setShort(patternBytes, 0, value);
         BlockPattern blockPattern = new BlockPattern("CResource", (set) -> !set.contains("CReportGroup"), patternBytes);
         map.put(blockPattern.getName(), blockPattern);
      }
   }

   /**
    * Heuristic method to determine the CTask block boundary.
    *
    * @param map block pattern map
    */
   private void determineTaskBlockBoundary(Map<String, BlockPattern> map)
   {
      // This is pretty crude, but it allows us to select between the two
      // different task fingerprints we've come across.
      long fingerprint1 = IntStream.range(0, m_buffer.length - TASK_FINGERPRINT_1.length).filter(index -> matchPattern(TASK_FINGERPRINT_1, index)).count();
      long fingerprint2 = IntStream.range(0, m_buffer.length - TASK_FINGERPRINT_2.length).filter(index -> matchPattern(TASK_FINGERPRINT_2, index)).count();
      byte[] fingerprint = fingerprint1 > fingerprint2 ? TASK_FINGERPRINT_1 : TASK_FINGERPRINT_2;

      int index = findFirstMatch(fingerprint, 0);
      if (index == -1)
      {
         logMessage("Unable to determine CTask boundary: no first task match");
      }
      else
      {
         index = findFirstMatch(fingerprint, index + 1);
         if (index == -1)
         {
            logMessage("Unable to determine CTask boundary: no second task match");
         }
         else
         {
            int secondTaskMatchIndex = index;
            while (index >= 0 && m_buffer[index] != 0)
            {
               --index;
            }

            if (index == -1)
            {
               logMessage("Unable to determine CTask boundary: past data start");
            }
            else
            {
               BlockPattern blockPattern = new BlockPattern("CTask", m_buffer, index + 1);
               map.put(blockPattern.getName(), blockPattern);
               determineUsageTaskBlockBoundary(secondTaskMatchIndex, map);
            }
         }
      }
   }

   /**
    * Heuristic method to determine the CUsageTask block boundary.
    *
    * @param startIndex search start index
    * @param map block pattern map
    */
   private void determineUsageTaskBlockBoundary(int startIndex, Map<String, BlockPattern> map)
   {
      while (true)
      {
         int index = findFirstMatch(m_usageFingerprint, startIndex + 1);
         if (index == -1)
         {
            logMessage("Unable to determine CUsageTask boundary: no fingerprint match");
            break;
         }

         if ((m_buffer[index - 1] & 0x80) == 0)
         {
            startIndex = index;
            continue;
         }

         index -= 2;
         BlockPattern blockPattern = new BlockPattern("CUsageTask", null, m_buffer, index);
         map.put(blockPattern.getName(), blockPattern);

         determineBarBlockBoundary(index, map);
         determineBaselineDataBlockBoundary(index, map);
         break;
      }
   }

   /**
    * Heuristic method to determine the CBaselineData block boundary.
    *
    * @param index search start index
    * @param map block pattern map
    */
   private void determineBaselineDataBlockBoundary(int index, Map<String, BlockPattern> map)
   {
      index = findFirstMatch(BASELINE_DATA_FINGERPRINT, index + 1);
      if (index == -1)
      {
         logMessage("Unable to determine CBaselineData boundary: no fingerprint match");
      }
      else
      {
         index -= 2;
         BlockPattern blockPattern = new BlockPattern("CBaselineData", null, m_buffer, index);
         map.put(blockPattern.getName(), blockPattern);
      }
   }

   /**
    * Heuristic method to determine the CLink block boundary.
    *
    * @param map block pattern map
    */
   private void determineLinkBlockBoundary(Map<String, BlockPattern> map)
   {
      Map<Integer, Long> valueCounts = IntStream.range(0, m_buffer.length - LINK_FINGERPRINT.length).filter(index -> matchPattern(LINK_FINGERPRINT, index)).mapToObj(index -> Integer.valueOf(DatatypeConverter.getShort(m_buffer, index - 4))).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

      Map.Entry<Integer, Long> entry = valueCounts.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
      if (entry != null)
      {
         byte[] patternBytes = new byte[2];
         DatatypeConverter.setShort(patternBytes, 0, entry.getKey().intValue());
         BlockPattern blockPattern = new BlockPattern("CLink", patternBytes);
         map.put(blockPattern.getName(), blockPattern);
      }
      else
      {
         logMessage("Unable to determine CLink boundary: no fingerprint match");
      }
   }

   /**
    * Heuristic method to determine the CBar block boundary.
    *
    * @param index search start index
    * @param map block pattern map
    */
   private void determineBarBlockBoundary(int index, Map<String, BlockPattern> map)
   {
      int searchLimit = index - 100;
      while (index > searchLimit)
      {
         if (matchPattern(BAR_FINGERPRINT, index))
         {
            BlockPattern blockPattern = new BlockPattern("CBar", null, m_buffer, index - 2);
            map.put(blockPattern.getName(), blockPattern);
            break;
         }
         --index;
      }

      if (index == searchLimit)
      {
         logMessage("Unable to determine CBar boundary: no fingerprint match");
      }
   }

   /**
    * Heuristic method to determine the CFilterObject block boundary.
    *
    * @param map block pattern map
    */
   private void determineFilterObjectBlockBoundary(Map<String, BlockPattern> map)
   {
      int index = findFirstMatch(VIEW_FINGERPRINT, 0);
      if (index == -1)
      {
         logMessage("Unable to determine CFilterObject boundary: no fingerprint match");
      }
      else
      {
         index += 17;
         BlockPattern blockPattern = new BlockPattern("CFilterObject", null, m_buffer, index);
         map.put(blockPattern.getName(), blockPattern);
      }
   }

   /**
    * Generate a list of block starts by matching patterns.
    *
    * @return list of block starts
    */
   private List<BlockReference> populateBlockReferences()
   {
      List<BlockReference> blockReferences = new ArrayList<>();
      List<BlockPattern> blockPatterns = selectBlockPatterns();
      Set<String> matchedPatternNames = new HashSet<>();
      boolean skipImage = false;

      for (int index = 0; index < m_buffer.length - 11; index++)
      {
         BlockPattern block = matchPattern(blockPatterns, index);
         if (block != null && block.getValid(matchedPatternNames))
         {
            // If we hit a CImage we'll skip everything else until we hit a named block.
            // Too many false positive hits in image data, and we don't know the
            // format well enough to predict the end of the block.
            String name = block.getName() == null ? DatatypeConverter.getTwoByteLengthString(m_buffer, index + 4) : null;
            if (skipImage)
            {
               skipImage = name == null;
            }
            else
            {
               skipImage = "CImage".equals(name);
            }

            if (!skipImage)
            {
               blockReferences.add(new BlockReference(block, index));
               matchedPatternNames.add(block.getName());

               // Nothing useful to us after we hit this block
               // stop reading here to avoid false positives
               if ("CFormatCellInfo".equals(name))
               {
                  break;
               }
            }
         }
      }

      return blockReferences;
   }

   /**
    * Convert a list of block starts into populated blocks.
    */
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

   /**
    * Read an individual block.
    *
    * @param blockReference block start
    * @param blockIndex block index number
    * @param startIndex block start index
    * @param blockLength block length
    */
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

         if (offset > blockLength)
         {
            logMessage("Skipping block " + name + " (blockLength=" + blockLength + " offset=" + offset + ")");
         }
         else
         {
            byte[] data = new byte[blockLength - offset];
            System.arraycopy(m_buffer, startIndex + offset, data, 0, data.length);
            Block block = new Block(name, data);
            addBlockToHierarchy(block);
            logBlock(name, blockIndex, startIndex, blockLength);
         }
      }
   }

   /**
    * Add a block to the hierarchy.
    *
    * @param block block to add
    */
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

   /**
    * Add a parent block to the hierarchy.
    *
    * @param block block
    */
   private void addParentBlockToHierarchy(Block block)
   {
      if (EXPECTED_CHILD_CLASSES.containsKey(block.getName()))
      {
         m_parentStack.push(block);
      }
   }

   /**
    * Determine if the data at the current index in the file matches a block start pattern.
    *
    * @param blocks list of block start patterns
    * @param bufferIndex current index in file
    * @return matching block pattern or null
    */
   private BlockPattern matchPattern(List<BlockPattern> blocks, int bufferIndex)
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

   private byte[] extractFingerprint(String name, boolean skipBlockStartString, int fingerprintLength)
   {
      byte[] fingerprint = null;
      byte[] namePattern = new byte[name.length() + 2];
      namePattern[0] = (byte) name.length();
      System.arraycopy(name.getBytes(), 0, namePattern, 2, name.length());
      int index = findFirstMatch(namePattern, 0);

      if (index == -1)
      {
         logMessage("Unable to extract fingerprint for " + name + ": no named block");
      }
      else
      {
         index += (name.length() + 2);

         if (skipBlockStartString)
         {
            index += (DatatypeConverter.getByte(m_buffer, index) + 1);
         }

         fingerprint = new byte[fingerprintLength];
         System.arraycopy(m_buffer, index, fingerprint, 0, fingerprintLength);
      }

      logMessage("Fingerprint for " + name + ": " + ByteArrayHelper.hexdump(fingerprint, false));

      return fingerprint;
   }

   /**
    * Identify a block start pattern using a fingerprint, skipping the named block.
    *
    * @param name block name
    * @param validator optional validator method to include with the pattern
    * @param fingerprint fingerprint used to match block
    * @return BlockPattern instance or null if not identifier
    */
   private BlockPattern identifyPattern(String name, BlockPatternValidator validator, byte[] fingerprint)
   {
      BlockPattern result = null;

      // Find the named block so we can skip it
      byte[] namePattern = new byte[name.length() + 2];
      namePattern[0] = (byte) name.length();
      System.arraycopy(name.getBytes(), 0, namePattern, 2, name.length());
      int index = findFirstMatch(namePattern, 0);

      if (index == -1)
      {
         logMessage("No " + name + " named block");
      }
      else
      {
         index += (name.length() + 2 + 1);
         result = identifyPattern(name, index, validator, fingerprint);
      }

      return result;
   }

   /**
    * Identify a block start pattern using a fingerprint.
    *
    * @param name block name
    * @param index start index for search
    * @param validator optional validator method to include with the pattern
    * @param fingerprint fingerprint used to match block
    * @return BlockPattern instance or null if not identifier
    */
   private BlockPattern identifyPattern(String name, int index, BlockPatternValidator validator, byte[] fingerprint)
   {
      BlockPattern result = null;

      index = findFirstMatch(fingerprint, index);
      if (index == -1)
      {
         logMessage("No " + name + " fingerprint");
      }
      else
      {
         if ((m_buffer[index - 1] & 0x80) == 0)
         {
            logMessage("Matched " + name + " fingerprint but found " + ByteArrayHelper.hexdump(m_buffer, index - 2, 2, false));
         }
         else
         {
            result = new BlockPattern(name, validator, m_buffer, index - 2);
         }
      }
      return result;
   }

   /**
    * Find first match for a byte pattern from an offset in the file.
    *
    * @param pattern byte pattern
    * @param offset file offset
    * @return offset of match or -1 if no match
    */
   private int findFirstMatch(byte[] pattern, int offset)
   {
      int result = -1;
      for (int bufferIndex = offset; bufferIndex < m_buffer.length - pattern.length; bufferIndex++)
      {
         if (matchPattern(pattern, bufferIndex))
         {
            result = bufferIndex;
            break;
         }
      }
      return result;
   }

   /**
    * Determine if a location in the byte array matches a byte pattern.
    *
    * @param pattern byte pattern
    * @param bufferIndex index to check in byte buffer
    * @return true if pattern matches at this location
    */
   private boolean matchPattern(byte[] pattern, int bufferIndex)
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
    * Open the log file for writing.
    */
   private void openLogFile()
   {
      m_log = DebugLogPrintWriter.getInstance();
   }

   /**
    * Close the log file.
    */
   private void closeLogFile()
   {
      if (m_log != null)
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

   private void logPatterns(List<BlockPattern> blockPatterns)
   {
      if (m_log != null)
      {
         m_log.println();
         m_log.println("Patterns:");
         blockPatterns.forEach(pattern -> m_log.println(pattern));
         m_log.println();
      }
   }

   private void logMessage(String message)
   {
      if (m_log != null)
      {
         m_log.println(message);
      }
   }

   private byte[] m_buffer;
   private byte[] m_usageFingerprint;
   private PrintWriter m_log;
   private final List<Block> m_blocks = new ArrayList<>();
   private final Deque<Block> m_parentStack = new ArrayDeque<>();

   private static final BlockPattern[] NAMED_BLOCK_PATTERNS =
   {
      new BlockPattern(null, (byte) 0xFF, (byte) 0xFF, (byte) 0x01, (byte) 0x00),
      new BlockPattern(null, (byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x00),
   };

   private static final BlockPattern[] BLOCK_PATTERNS =
   {
      new BlockPattern("CSymbol", (byte) 0x01, (byte) 0x80),
      new BlockPattern("Unknown1", (byte) 0x02, (byte) 0x80),
      new BlockPattern("CCalendar", (byte) 0x05, (byte) 0x80),
      new BlockPattern("CDayFlag", (byte) 0x07, (byte) 0x80),
      new BlockPattern("CShape", (byte) 0x03, (byte) 0x80),
   };

   // Basic Basic
   private static final byte[] REPORT_DATA_FINGERPRINT =
   {
      0x05,
      0x42,
      0x61,
      0x73,
      0x69,
      0x63,
      0x05,
      0x42,
      0x61,
      0x73,
      0x69
   };

   // Earned Value Analysis
   private static final byte[] REPORT_GROUP_FINGERPRINT =
   {
      0x15,
      0x45,
      0x61,
      0x72,
      0x6E,
      0x65,
      0x64,
      0x20,
      0x56,
      0x61,
      0x6C,
      0x75,
      0x65,
      0x20,
      0x41,
      0x6E,
      0x61,
      0x6C,
      0x79,
      0x73,
      0x69,
      0x73,
      0x01,
      0x00
   };

   private static final byte[] VIEW_FINGERPRINT =
   {
      0x0C,
      0x42,
      0x6F,
      0x72,
      0x64,
      0x65,
      0x72,
      0x4C,
      0x61,
      0x79,
      0x6F,
      0x75,
      0x74
   };

   private static final byte[] BASELINE_DATA_FINGERPRINT =
   {
      0x0A,
      0x00,
      0x00,
      (byte) 0x80
   };

   private static final byte[] BAR_FINGERPRINT =
   {
      0x00,
      0x0A,
      0x00,
      0x00
   };

   private static final byte[] TASK_FINGERPRINT_1 =
   {
      0x40,
      0x00,
      0x01,
      0x00,
      0x00,
      0x00,
      0x00,
      0x00,
      0x00
   };

   private static final byte[] TASK_FINGERPRINT_2 =
   {
      0x42,
      0x00,
      0x01,
      0x00,
      0x00,
      0x00,
      0x00,
      0x00,
      0x00
   };

   private static final byte[] LINK_FINGERPRINT =
   {
      0x08,
      0x03,
      0x05,
      0x00
   };

   private static final Map<String, Set<String>> EXPECTED_CHILD_CLASSES = new HashMap<>();
   static
   {
      EXPECTED_CHILD_CLASSES.put("CCalendar", new HashSet<>(Collections.singletonList("CDayFlag")));
      EXPECTED_CHILD_CLASSES.put("CResource", new HashSet<>(Arrays.asList("CSymbol", "CResourceTask", "CBaselineData", "CBar", "CCalendar")));
      EXPECTED_CHILD_CLASSES.put("CTask", new HashSet<>(Arrays.asList("CPlanObject", "CCalendar", "CBaselineIndex", "CBaselineData", "CBar", "CUsageTask", "CLink")));
      EXPECTED_CHILD_CLASSES.put("CUsageTask", new HashSet<>(Collections.singletonList("CBaselineData")));
   }
}
