/*
 * file:       HyperlinkReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       27/02/2022
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

import java.util.HashMap;
import java.util.Map;

import org.mpxj.AssignmentField;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceField;
import org.mpxj.Task;
import org.mpxj.TaskField;

/**
 * Extracts hyperlink data from the structure used by all versions
 * of Microsoft Project.
 */
final class HyperlinkReader
{
   /**
    * Set to {@code true} to indicate that we're expecting to read
    * a screen tip as part of the hyperlink data. Note that
    * Project 98 is the only version which doesn't include screen tips.
    *
    * @param hasScreenTip true if screen tip should be read
    */
   public void setHasScreenTip(boolean hasScreenTip)
   {
      m_hasScreenTip = hasScreenTip;
   }

   /**
    * Read hyperlink data and populate the relevant fields.
    *
    * @param container container for the hyper link data
    * @param data hyperlink data
    */
   public void read(FieldContainer container, byte[] data)
   {
      if (data != null)
      {
         int offset = 12;

         offset += 12;
         String hyperlink = MPPUtility.getUnicodeString(data, offset);
         offset += ((hyperlink.length() + 1) * 2);

         offset += 12;
         String address = MPPUtility.getUnicodeString(data, offset);
         offset += ((address.length() + 1) * 2);

         offset += 12;
         String subaddress = MPPUtility.getUnicodeString(data, offset);
         offset += ((subaddress.length() + 1) * 2);

         offset += 12;
         String screentip = m_hasScreenTip ? MPPUtility.getUnicodeString(data, offset) : null;

         FieldType[] fields = FIELDS.get(container.getClass());
         container.set(fields[0], hyperlink);
         container.set(fields[1], address);
         container.set(fields[2], subaddress);
         container.set(fields[3], screentip);
      }
   }

   private boolean m_hasScreenTip = true;

   private static final Map<Class<?>, FieldType[]> FIELDS = new HashMap<>();
   static
   {
      FIELDS.put(Task.class, new FieldType[]
      {
         TaskField.HYPERLINK,
         TaskField.HYPERLINK_ADDRESS,
         TaskField.HYPERLINK_SUBADDRESS,
         TaskField.HYPERLINK_SCREEN_TIP
      });

      FIELDS.put(Resource.class, new FieldType[]
      {
         ResourceField.HYPERLINK,
         ResourceField.HYPERLINK_ADDRESS,
         ResourceField.HYPERLINK_SUBADDRESS,
         ResourceField.HYPERLINK_SCREEN_TIP
      });

      FIELDS.put(ResourceAssignment.class, new FieldType[]
      {
         AssignmentField.HYPERLINK,
         AssignmentField.HYPERLINK_ADDRESS,
         AssignmentField.HYPERLINK_SUBADDRESS,
         AssignmentField.HYPERLINK_SCREEN_TIP
      });
   }
}
