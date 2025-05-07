/*
 * file:       RelationReader.java
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Task;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

/**
 * Read activity relationships.
 */
class RelationReader
{
   /**
    * Constructor.
    *
    * @param root parent directory
    * @param file project file
    */
   public RelationReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   /**
    * Read activity relations.
    */
   public void read()
   {
      Map<UUID, Task> map = m_file.getTasks().stream().collect(Collectors.toMap(Task::getGUID, t -> t));

      for (Row row : new TableReader(m_root, "REL").read())
      {
         // CLH_UID: Calendar Header Unique ID
         // DIR_ID: Project Object Directory Name
         // DIR_UID: Project Object Directory UID
         // LASTUPDATE: Last Update Date
         // PRED_ACT_ID: Predecessor Activity ID
         // PRED_ACT_UID: Predecessor Activity Unique ID
         // REL_FF: Relationship Free Float
         // REL_PROBABILITY: Relationship Branch Probability
         // REL_TF: Relationship Total Float
         // REL_TYPE: Relationship Type (FS: Finish to Start, SS: Start to Start, SF: Start to Finish, FF: Finish to Finish)
         // REL_UID: Relationship Unique ID
         // SEQUENCE: Update Count
         // SUCC_ACT_ID: Successor Activity ID
         // SUCC_ACT_UID: Successor Activity Unique ID
         // USR_ID: Last Update User

         Task predecessor = map.get(row.getUuid("PRED_ACT_UID"));
         Task successor = map.get(row.getUuid("SUCC_ACT_UID"));
         if (predecessor != null && successor != null)
         {
            successor.addPredecessor(new Relation.Builder().predecessorTask(predecessor).type(TYPE_MAP.getOrDefault(row.getString("REL_TYPE"), RelationType.FINISH_START)).lag(row.getDuration("REL_LAG")));
         }
      }
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;

   private static final Map<String, RelationType> TYPE_MAP = new HashMap<>();
   static
   {
      TYPE_MAP.put("SS", RelationType.START_START);
      TYPE_MAP.put("SF", RelationType.START_FINISH);
      TYPE_MAP.put("FS", RelationType.FINISH_START);
      TYPE_MAP.put("FF", RelationType.FINISH_FINISH);
   }
}
