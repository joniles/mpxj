package org.mpxj.primavera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.FieldType;
import org.mpxj.ProjectContext;

class TableReaderState
{
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

   public ProjectContext getContext()
   {
      return m_context;
   }

   public Map<String, Map<Integer, List<Row>>> getUdfValues()
   {
      return m_udfValues;
   }

   public ClashMap getRoleClashMap()
   {
      return m_roleClashMap;
   }

   public Map<FieldType, String> getResourceFields()
   {
      return m_resourceFields;
   }

   public Map<FieldType, String> getRoleFields()
   {
      return m_roleFields;
   }

   public boolean getIgnoreErrors()
   {
      return m_ignoreErrors;
   }

   public Map<FieldType, String> getWbsFields()
   {
      return m_wbsFields;
   }

   public Map<FieldType, String> getTaskFields()
   {
      return m_taskFields;
   }

   public Map<FieldType, String> getAssignmentFields()
   {
      return m_assignmentFields;
   }

   public boolean getMatchPrimaveraWBS()
   {
      return m_matchPrimaveraWBS;
   }

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
