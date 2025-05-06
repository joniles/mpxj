/*
 * file:       RTFEmbeddedObject.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Jun 28, 2005
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

package org.mpxj.mpp;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.mpxj.RtfNotes;
import org.mpxj.common.ByteArrayHelper;

/**
 * This class represents embedded object data contained within an RTF
 * document. According to the RTF specification, this data has been written using
 * the OLESaveToStream, although I have been unable to locate any existing
 * Java implementations of the equivalent OLELoadFromStream in order to
 * read this data, hence the current implementation.
 *
 * To use this class with note fields in MPXJ, call
 * getNotesObject() to allow retrieval of the raw RTF
 * document and unformatted text from the note fields. If you want
 * to extract any embedded objects from the document, call the
 * RTFEmbeddedObject.getEmbeddedObjects() method, passing in the raw RTF
 * document.
 *
 * The structure of data embedded in a notes field is beyond the scope
 * of the MPXJ documentation. However, generally speaking, you will find that
 * each item of embedded data will be made up of two RTFEmbeddedObject instances,
 * the first is a header usually containing string data, the second is the
 * actual payload data, which will typically be binary. You can retrieve the
 * String data using the RTFEmbeddedObject.getStringData() method, and the
 * binary data using the RTFEmbeddedObject.getData() method.
 *
 * For each embedded item in the document you will typically find two
 * groups of these objects. The first group of two RTFEmbeddedObject instances
 * (one header object and one data object) represent either the location of a
 * linked document, or the binary data for the document itself. The second
 * group of two RTFEmbeddedObject instances contain a METAFILEPICT, which
 * either contains the icon image used as a placeholder for the embedded
 * document, or it contains an image of the document contents, again used
 * as a placeholder.
 *
 * Warning: this functionality is experimental, please submit bugs for any
 * example files containing embedded objects which fail to parse when using this
 * class.
 */
public final class RTFEmbeddedObject
{
   /**
    * Constructor.
    *
    * @param blocks list of data blocks
    * @param type expected type of next block.
    */
   private RTFEmbeddedObject(List<byte[]> blocks, int type)
   {
      switch (type)
      {
         case 2:
         case 5:
         {
            m_typeFlag1 = getInt(blocks);
            m_typeFlag2 = getInt(blocks);
            int length = getInt(blocks);
            m_data = getData(blocks, length);
            break;
         }

         case 1:
         {
            int length = getInt(blocks);
            m_data = getData(blocks, length);
            break;
         }
      }
   }

   /**
    * Retrieve type flag 1.
    *
    * @return type flag 1
    */
   public int getTypeFlag1()
   {
      return (m_typeFlag1);
   }

   /**
    * Retrieve type flag 2.
    *
    * @return type flag 2
    */
   public int getTypeFlag2()
   {
      return (m_typeFlag2);
   }

   /**
    * Retrieve the data associated with this block as a byte array.
    *
    * @return byte array of data
    */
   public byte[] getData()
   {
      return (m_data);
   }

   /**
    * Retrieve the data associated with this block as a string.
    *
    * @return string data
    */
   public String getDataString()
   {
      return (m_data == null ? "" : new String(m_data));
   }

   /**
    * This method generates a list of lists. Each list represents the data
    * for an embedded object, and contains set set of RTFEmbeddedObject instances
    * that make up the embedded object. This method will return null
    * if there are no embedded objects in the RTF document.
    *
    * @param notes Notes instance
    * @return list of lists of RTFEmbeddedObject instances
    */
   public static List<List<RTFEmbeddedObject>> getEmbeddedObjects(RtfNotes notes)
   {
      List<List<RTFEmbeddedObject>> objects = null;
      List<RTFEmbeddedObject> objectData;
      String rtf = notes.getRtf();

      int offset = rtf.indexOf(OBJDATA);
      if (offset != -1)
      {
         objects = new ArrayList<>();

         while (offset != -1)
         {
            objectData = new ArrayList<>();
            objects.add(objectData);
            offset = readObjectData(offset, rtf, objectData);
            offset = rtf.indexOf(OBJDATA, offset);
         }
      }

      return (objects);
   }

   /**
    * Internal method used to retrieve a integer from an
    * embedded data block.
    *
    * @param blocks list of data blocks
    * @return int value
    */
   private int getInt(List<byte[]> blocks)
   {
      int result;
      if (!blocks.isEmpty())
      {
         byte[] data = blocks.remove(0);
         result = ByteArrayHelper.getInt(data, 0);
      }
      else
      {
         result = 0;
      }
      return (result);
   }

   /**
    * Internal method used to retrieve a byte array from one
    * or more embedded data blocks. Consecutive data blocks may
    * need to be concatenated by this method in order to retrieve
    * the complete set of data.
    *
    * @param blocks list of data blocks
    * @param length expected length of the data
    * @return byte array
    */
   private byte[] getData(List<byte[]> blocks, int length)
   {
      byte[] result;

      if (!blocks.isEmpty())
      {
         if (length < 4)
         {
            length = 4;
         }

         result = new byte[length];
         int offset = 0;
         byte[] data;

         while (offset < length)
         {
            data = blocks.remove(0);
            System.arraycopy(data, 0, result, offset, data.length);
            offset += data.length;
         }
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * This method extracts byte arrays from the embedded object data
    * and converts them into RTFEmbeddedObject instances, which
    * it then adds to the supplied list.
    *
    * @param offset offset into the RTF document
    * @param text RTF document
    * @param objects destination for RTFEmbeddedObject instances
    * @return new offset into the RTF document
    */
   private static int readObjectData(int offset, String text, List<RTFEmbeddedObject> objects)
   {
      List<byte[]> blocks = new ArrayList<>();

      offset += (OBJDATA.length());
      offset = skipEndOfLine(text, offset);
      int length;
      int lastOffset = offset;

      while (offset != -1)
      {
         length = getBlockLength(text, offset);
         lastOffset = readDataBlock(text, offset, length, blocks);
         offset = skipEndOfLine(text, lastOffset);
      }

      RTFEmbeddedObject headerObject;
      RTFEmbeddedObject dataObject;

      while (!blocks.isEmpty())
      {
         headerObject = new RTFEmbeddedObject(blocks, 2);
         objects.add(headerObject);

         if (!blocks.isEmpty())
         {
            dataObject = new RTFEmbeddedObject(blocks, headerObject.getTypeFlag2());
            objects.add(dataObject);
         }
      }

      return (lastOffset);
   }

   /**
    * This method skips the end-of-line markers in the RTF document.
    * It also indicates if the end of the embedded object has been reached.
    *
    * @param text RTF document test
    * @param offset offset into the RTF document
    * @return new offset
    */
   private static int skipEndOfLine(String text, int offset)
   {
      char c;
      boolean finished = false;

      while (!finished)
      {
         c = text.charAt(offset);
         switch (c)
         {
            case ' ': // found that OBJDATA could be followed by a space the EOL
            case '\r':
            case '\n':
            {
               ++offset;
               break;
            }

            case '}':
            {
               offset = -1;
               finished = true;
               break;
            }

            default:
            {
               finished = true;
               break;
            }
         }
      }

      return (offset);
   }

   /**
    * Calculates the length of the next block of RTF data.
    *
    * @param text RTF data
    * @param offset current offset into this data
    * @return block length
    */
   private static int getBlockLength(String text, int offset)
   {
      int startIndex = offset;
      boolean finished = false;
      char c;

      while (!finished)
      {
         c = text.charAt(offset);
         switch (c)
         {
            case '\r':
            case '\n':
            case '}':
            {
               finished = true;
               break;
            }

            default:
            {
               ++offset;
               break;
            }
         }
      }

      return (offset - startIndex);
   }

   /**
    * Reads a data block and adds it to the list of blocks.
    *
    * @param text RTF data
    * @param offset current offset
    * @param length next block length
    * @param blocks list of blocks
    * @return next offset
    */
   private static int readDataBlock(String text, int offset, int length, List<byte[]> blocks)
   {
      int bytes = length / 2;
      byte[] data = new byte[bytes];

      for (int index = 0; index < bytes; index++)
      {
         data[index] = (byte) Integer.parseInt(text.substring(offset, offset + 2), 16);
         offset += 2;
      }

      blocks.add(data);
      return (offset);
   }

   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);

      pw.println("[RTFObject");
      pw.println("   Flag1=" + m_typeFlag1);
      pw.println("   Flag2=" + m_typeFlag2);
      pw.println("   Data=");
      pw.println(ByteArrayHelper.hexdump(m_data, true, 16, "  "));
      pw.println("]");
      pw.flush();

      return (os.toString());
   }

   private int m_typeFlag1;
   private int m_typeFlag2;
   private byte[] m_data;

   private static final String OBJDATA = "\\objdata";
}
