package org.mpxj.primavera;

import java.util.Set;

import org.mpxj.FieldType;
import org.mpxj.common.ObjectSequence;
import org.mpxj.primavera.schema.APIBusinessObjects;

class XmlWriterState
{
   public XmlWriterState(APIBusinessObjects apibo, Set<FieldType> userDefinedFields)
   {
      m_apibo = apibo;
      m_userDefinedFields = userDefinedFields;
   }

   public APIBusinessObjects getApibo()
   {
      return m_apibo;
   }

   public Integer getRateObjectID()
   {
      return m_rateObjectID.getNext();
   }

   public Integer getWbsNoteObjectID()
   {
      return m_wbsNoteObjectID.getNext();
   }

   public Integer getActivityNoteObjectID()
   {
      return m_activityNoteObjectID.getNext();
   }

   public Set<FieldType> getUserDefinedFields()
   {
      return m_userDefinedFields;
   }

   public boolean getDefaultNotesTopicUsed()
   {
      return m_defaultNotesTopicUsed;
   }

   public void defaultNotesTopicUsed()
   {
      m_defaultNotesTopicUsed = true;
   }

   private final APIBusinessObjects m_apibo;
   private final ObjectSequence m_rateObjectID = new ObjectSequence(1);
   private final ObjectSequence m_wbsNoteObjectID = new ObjectSequence(1);
   private final ObjectSequence m_activityNoteObjectID = new ObjectSequence(1);
   private final Set<FieldType> m_userDefinedFields;
   private boolean m_defaultNotesTopicUsed;
}
