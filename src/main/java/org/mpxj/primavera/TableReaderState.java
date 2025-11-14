/*
 * file:       TableReaderState.java
 * author:     Jon Iles
 * date:       2025-11-12
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

package org.mpxj.primavera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.FieldType;
import org.mpxj.ProjectContext;

/**
 * State data shared between tabular P6 data readers.
 */
class TableReaderState
{
   /**
    * Constructor.
    *
    * @param resourceFields resource field map
    * @param roleFields role field map
    * @param wbsFields wbs field map
    * @param taskFields task field map
    * @param assignmentFields resource assignment field map
    * @param matchPrimaveraWbs true if wbs value should match P6
    * @param wbsIsFullPath true if wbs should contain the full path
    * @param ignoreErrors true if errors are ignored
    */
   public TableReaderState(Map<FieldType, String> resourceFields, Map<FieldType, String> roleFields, Map<FieldType, String> wbsFields, Map<FieldType, String> taskFields, Map<FieldType, String> assignmentFields, boolean matchPrimaveraWbs, boolean wbsIsFullPath, boolean ignoreErrors)
   {
      m_resourceFields = resourceFields;
      m_roleFields = roleFields;
      m_wbsFields = wbsFields;
      m_taskFields = taskFields;
      m_assignmentFields = assignmentFields;
      m_matchPrimaveraWBS = matchPrimaveraWbs;
      m_wbsIsFullPath = wbsIsFullPath;
      m_ignoreErrors = ignoreErrors;
   }

   /**
    * Retrieve the project context.
    *
    * @return project context
    */
   public ProjectContext getContext()
   {
      return m_context;
   }

   /**
    * Retrieve UDF values.
    *
    * @return UDF values
    */
   public Map<String, Map<Integer, List<Row>>> getUdfValues()
   {
      return m_udfValues;
   }

   /**
    * Retrieve the role clash map.
    *
    * @return role clash map
    */
   public ClashMap getRoleClashMap()
   {
      return m_roleClashMap;
   }

   /**
    * retrieve the resource field map.
    *
    * @return resource field map
    */
   public Map<FieldType, String> getResourceFields()
   {
      return m_resourceFields;
   }

   /**
    * Retrieve the role field map.
    *
    * @return role field map
    */
   public Map<FieldType, String> getRoleFields()
   {
      return m_roleFields;
   }

   /**
    * Retrieve the ignore errors flag.
    *
    * @return ignore erros flag
    */
   public boolean getIgnoreErrors()
   {
      return m_ignoreErrors;
   }

   /**
    * Retrieve the wbs field map.
    *
    * @return wbs field map
    */
   public Map<FieldType, String> getWbsFields()
   {
      return m_wbsFields;
   }

   /**
    * retrieve the task field map.
    *
    * @return task field map
    */
   public Map<FieldType, String> getTaskFields()
   {
      return m_taskFields;
   }

   /**
    * Retrieve the resource assignment field map.
    *
    * @return resource assignment field map
    */
   public Map<FieldType, String> getAssignmentFields()
   {
      return m_assignmentFields;
   }

   /**
    * Retrieve the match Primavera WBS flag.
    *
    * @return match Primavera WBS flag
    */
   public boolean getMatchPrimaveraWBS()
   {
      return m_matchPrimaveraWBS;
   }

   /**
    * Retrieve the WBS is full path flag.
    *
    * @return WBS is full path flag
    */
   public boolean getWbsIsFullPath()
   {
      return m_wbsIsFullPath;
   }

   private final ProjectContext m_context = new ProjectContext();
   private final Map<String, Map<Integer, List<Row>>> m_udfValues = new HashMap<>();
   private final ClashMap m_roleClashMap = new ClashMap();
   private final Map<FieldType, String> m_resourceFields;
   private final Map<FieldType, String> m_roleFields;
   private final Map<FieldType, String> m_wbsFields;
   private final Map<FieldType, String> m_taskFields;
   private final Map<FieldType, String> m_assignmentFields;
   private final boolean m_matchPrimaveraWBS;
   private final boolean m_wbsIsFullPath;
   private final boolean m_ignoreErrors;
}
