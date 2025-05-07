/*
 * file:       ActivityCodeReader.java
 * author:     Jon Iles
 * date:       2024-02-27
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

package org.mpxj.openplan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeValue;
import org.mpxj.ProjectFile;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

/**
 * Given a map of codes, determine which of these are mapped to activities, populate
 * the ProjectFile with the activity code definitions, and finally populate a map between the
 * column name the code value will appear in, and the code value definitions.
 */
class ActivityCodeReader
{
   /**
    * Constructor.
    *
    * @param root parent directory
    * @param file project file
    */
   public ActivityCodeReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   /**
    * Determine which codes are mapped as activity codes
    * and add them to the parent project.
    *
    * @param map code definitions
    */
   public void read(Map<String, Code> map)
   {
      int codeSequence = 1;
      List<Row> rows = new TableReader(m_root, "SCA").read();
      for (Row row : rows)
      {
         if (!"ACT".equals(row.getString("TABLE_TYPE")))
         {
            continue;
         }

         Code code = map.get(row.getString("COD_ID"));
         if (code == null)
         {
            continue;
         }

         ActivityCode ac = new ActivityCode.Builder(m_file).name(code.getPromptText()).sequenceNumber(Integer.valueOf(codeSequence++)).build();
         Map<String, ActivityCodeValue> valueMap = new HashMap<>();

         int valueSequence = 1;
         for (CodeValue value : code.getValues())
         {
            ActivityCodeValue acv = new ActivityCodeValue.Builder(m_file).activityCode(ac).name(value.getID()).description(value.getDescription()).sequenceNumber(Integer.valueOf(valueSequence++)).build();

            ac.addValue(acv);
            valueMap.put(value.getUniqueID(), acv);
         }

         m_codeMap.put(row.getString("SCA_ID"), valueMap);

         m_file.getActivityCodes().add(ac);
      }
   }

   /**
    * Retrieve a mapping between column names, code value unique IDs,
    * and ActivityCodeValue instances.
    *
    * @return column to activity code value map
    */
   public Map<String, Map<String, ActivityCodeValue>> getCodeMap()
   {
      return m_codeMap;
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;
   private final Map<String, Map<String, ActivityCodeValue>> m_codeMap = new HashMap<>();
}