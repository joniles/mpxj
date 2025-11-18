package org.mpxj.primavera;

import org.mpxj.common.ObjectSequence;
import org.mpxj.primavera.schema.APIBusinessObjects;

class XmlWriterState
{
   public XmlWriterState(APIBusinessObjects apibo)
   {
      m_apibo = apibo;
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

   private final APIBusinessObjects m_apibo;
   private final ObjectSequence m_rateObjectID = new ObjectSequence(1);
   private final ObjectSequence m_wbsNoteObjectID = new ObjectSequence(1);
   private final ObjectSequence m_activityNoteObjectID = new ObjectSequence(1);
}
