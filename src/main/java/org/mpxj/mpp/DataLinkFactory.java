/*
 * file:       DataLinkFactory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       15/06/2019
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mpxj.DataLink;
import org.mpxj.DataLinkContainer;
import org.mpxj.FieldType;
import org.mpxj.ProjectFile;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.FieldTypeHelper;

/**
 * Extracts data links.
 */
class DataLinkFactory
{
   /**
    * Constructor.
    *
    * @param project parent project
    * @param fixedData fix data blocks
    * @param varData var data blocks
    */
   public DataLinkFactory(ProjectFile project, FixedData fixedData, Var2Data varData)
   {
      m_project = project;
      m_fixedData = fixedData;
      m_varData = varData;
   }

   /**
    * Extract data links.
    */
   public void process() throws IOException
   {
      // System.out.println(m_fixedData);
      // System.out.println(m_varData.getVarMeta());
      // System.out.println(m_varData);

      Integer key = m_project.getProjectProperties().getMppFileType().intValue() == 9 ? PROPS9 : PROPS;

      Map<String, DataLink> map = new HashMap<>();

      int itemCount = m_fixedData.getItemCount();
      for (int index = 0; index < itemCount; index++)
      {
         byte[] data = m_fixedData.getByteArrayValue(index);
         if (data != null && (data[112] & 0x20) == 0)
         {
            int id = ByteArrayHelper.getInt(data, 0);
            byte[] propsData = m_varData.getByteArray(Integer.valueOf(id), key);
            if (propsData != null)
            {
               process(propsData, map);
            }
         }
      }

      DataLinkContainer container = m_project.getDataLinks();
      container.addAll(map.values());
   }

   /**
    * Extract a single data link.
    *
    * @param data fixed data block
    * @param map extracted link data
    */
   private void process(byte[] data, Map<String, DataLink> map) throws IOException
   {
      Props props = new Props14(m_project, new ByteArrayInputStream(data));
      //System.out.println(props);

      String dataLinkID = props.getUnicodeString(PATH);
      DataLink dataLink = map.computeIfAbsent(dataLinkID, DataLink::new);
      Integer rowUniqueID = Integer.valueOf(props.getInt(UNIQUE_ID));
      FieldType fieldType = FieldTypeHelper.getInstance(m_project, props.getInt(FIELD_TYPE));

      if (props.getUnicodeString(VIEW_NAME) == null)
      {
         dataLink.setTargetField(fieldType);
         dataLink.setTargetUniqueID(rowUniqueID);
      }
      else
      {
         dataLink.setSourceField(fieldType);
         dataLink.setSourceUniqueID(rowUniqueID);
      }
   }

   private static final Integer PROPS = Integer.valueOf(6);
   private static final Integer PROPS9 = Integer.valueOf(1);
   private static final Integer FIELD_TYPE = Integer.valueOf(641728535);
   private static final Integer VIEW_NAME = Integer.valueOf(641728536);
   private static final Integer UNIQUE_ID = Integer.valueOf(641728548);
   private static final Integer PATH = Integer.valueOf(641728561);

   private final ProjectFile m_project;
   private final FixedData m_fixedData;
   private final Var2Data m_varData;
}
