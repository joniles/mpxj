/*
 * file:       CalendarReader.java
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
import java.util.UUID;

/**
 * Reads a calendar table.
 */
class CalendarReader extends TableReader
{
   /**
    * Constructor.
    *
    * @param stream input stream
    */
   public CalendarReader(StreamReader stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("NAME", stream.readString());
      map.put("UNKNOWN1", stream.readTable(CalendarDetailReader.class));
      map.put("UNKNOWN2", stream.readBytes(4));
      map.put("SUNDAY_DAY_TYPE", stream.readUUID());
      map.put("MONDAY_DAY_TYPE", stream.readUUID());
      map.put("TUESDAY_DAY_TYPE", stream.readUUID());
      map.put("WEDNESDAY_DAY_TYPE", stream.readUUID());
      map.put("THURSDAY_DAY_TYPE", stream.readUUID());
      map.put("FRIDAY_DAY_TYPE", stream.readUUID());
      map.put("SATURDAY_DAY_TYPE", stream.readUUID());
      map.put("UNKNOWN3", stream.readBytes(4));
      map.put("DAY_TYPE_ASSIGNMENTS", stream.readTable(DayTypeAssignmentReader.class));
      map.put("DAY_TYPES", stream.readTable(DayTypeReader.class));
      map.put("UNKNOWN4", stream.readBytes(8));
   }

   @Override protected void postTrailer(StreamReader stream) throws IOException
   {
      m_defaultCalendarUUID = stream.readUUID();
   }

   @Override protected int rowMagicNumber()
   {
      return 0x7FEC261D;
   }

   /**
    * Retrieve the default calendar UUID.
    *
    * @return Default calendar UUID
    */
   public UUID getDefaultCalendarUUID()
   {
      return m_defaultCalendarUUID;
   }

   private UUID m_defaultCalendarUUID;
}
