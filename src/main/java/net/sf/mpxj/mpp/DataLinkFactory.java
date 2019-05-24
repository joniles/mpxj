
package net.sf.mpxj.mpp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.DataLink;
import net.sf.mpxj.DataLinkContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.FieldTypeHelper;

class DataLinkFactory
{
   public DataLinkFactory(ProjectFile project, FixedData fixedData, Var2Data varData)
   {
      m_project = project;
      m_fixedData = fixedData;
      m_varData = varData;
   }

   public void process() throws IOException
   {
      //System.out.println(m_fixedData);

      Map<String, DataLink> map = new HashMap<String, DataLink>();

      int itemCount = m_fixedData.getItemCount();
      for (int index = 0; index < itemCount; index++)
      {
         byte[] data = m_fixedData.getByteArrayValue(index);
         if ((data[112] & 0x20) == 0)
         {
            int id = MPPUtility.getInt(data, 0);
            byte[] propsData = m_varData.getByteArray(Integer.valueOf(id), PROPS);
            if (propsData != null)
            {
               process(propsData, map);
            }
         }
      }

      DataLinkContainer container = m_project.getDataLinks();
      for (DataLink dataLink : map.values())
      {
         container.add(dataLink);
      }
   }

   private void process(byte[] data, Map<String, DataLink> map) throws IOException
   {
      Props props = new Props14(new ByteArrayInputStream(data));
      // System.out.println(props);

      String dataLinkID = props.getUnicodeString(PATH);
      DataLink dataLink = map.get(dataLinkID);
      if (dataLink == null)
      {
         dataLink = new DataLink(dataLinkID);
         map.put(dataLinkID, dataLink);
      }

      Integer rowUniqueID = Integer.valueOf(props.getInt(UNIQUE_ID));
      FieldType fieldType = FieldTypeHelper.getInstance14(props.getInt(FIELD_TYPE));

      if (props.getUnicodeString(VIEW_NAME) == null)
      {
         dataLink.setSinkField(fieldType);
         dataLink.setSinkUniqueID(rowUniqueID);
      }
      else
      {
         dataLink.setSourceField(fieldType);
         dataLink.setSourceUniqueID(rowUniqueID);
      }
   }

   private static final Integer PROPS = Integer.valueOf(6);
   private static final Integer FIELD_TYPE = Integer.valueOf(641728535);
   private static final Integer VIEW_NAME = Integer.valueOf(641728536);
   private static final Integer UNIQUE_ID = Integer.valueOf(641728548);
   private static final Integer PATH = Integer.valueOf(641728561);

   private final ProjectFile m_project;
   private final FixedData m_fixedData;
   private final Var2Data m_varData;
}
