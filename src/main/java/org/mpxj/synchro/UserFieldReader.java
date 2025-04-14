/*
 * file:       UserFieldReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       2018-10-11
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

package org.mpxj.synchro;

import java.io.IOException;
import java.util.Map;

/**
 * Reads a user defined data table.
 */
class UserFieldReader extends TableReader
{
   /**
    * Constructor.
    *
    * @param stream input stream
    */
   public UserFieldReader(StreamReader stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      if (stream.getVersion().atLeast(Synchro.VERSION_6_2_0))
      {
         readVersion62Row(stream, map);
      }
      else
      {
         if (stream.getVersion().atLeast(Synchro.VERSION_6_0_0))
         {
            readVersion60Row(stream, map);
         }
         else
         {
            readVersion50Row(stream, map);
         }
      }
   }

   @Override protected boolean hasUUID()
   {
      return false;
   }

   @Override protected int rowMagicNumber()
   {
      return 0x440A7BA3;
   }

   private void readVersion62Row(StreamReader stream, Map<String, Object> map) throws IOException
   {
      int type = stream.readInt();

      switch (type)
      {
         case 2:
         {
            readVersion62BooleanRow(stream, map);
            break;
         }

         case 3:
         {
            readVersion62IntegerRow(stream, map);
            break;
         }

         case 4:
         {
            readVersion62NumberRow(stream, map);
            break;
         }

         case 5:
         {
            readVersion62DateRow(stream, map);
            break;
         }

         case 6:
         {
            readVersion62StringRow(stream, map);
            break;
         }

         default:
         {
            throw new IllegalArgumentException("Unexpected file format");
         }
      }
   }

   private void readVersion62BooleanRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("VALUE", stream.readBoolean());
      map.put("UNKNOWN0", stream.readBytes(1));
      map.put("UNKNOWN1", stream.readBytes(26));
   }

   private void readVersion62IntegerRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("VALUE", stream.readInteger());
      map.put("UNKNOWN1", stream.readBytes(26));
   }

   private void readVersion62NumberRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("VALUE", stream.readDouble());
      map.put("UNKNOWN1", stream.readBytes(26));
   }

   private void readVersion62StringRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("VALUE", stream.readString());
      map.put("UNKNOWN1", stream.readBytes(26));
   }

   private void readVersion62DateRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("VALUE", stream.readDate());
      map.put("UNKNOWN0", stream.readInteger());
      map.put("UNKNOWN1", stream.readBytes(26));
   }

   private void readVersion60Row(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("VALUE", stream.readString());
      map.put("UNKNOWN1", stream.readBytes(26));
   }

   private void readVersion50Row(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("UNKNOWN1", stream.readBytes(16));
      map.put("VALUE", stream.readString());
      map.put("UNKNOWN2", stream.readBytes(26));
   }
}
