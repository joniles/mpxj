package org.mpxj.primavera;

import java.util.Set;

import org.mpxj.Currency;
import org.mpxj.FieldType;
import org.mpxj.common.ObjectSequence;
import org.mpxj.primavera.schema.APIBusinessObjects;
import org.mpxj.primavera.schema.ObjectFactory;

class XmlWriterState
{
   public XmlWriterState(Set<FieldType> userDefinedFields, Currency defaultCurrency)
   {
      m_userDefinedFields = userDefinedFields;
      m_defaultCurrency = defaultCurrency;
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

   public Currency getDefaultCurrency()
   {
      return m_defaultCurrency;
   }

   private final APIBusinessObjects m_apibo = new ObjectFactory().createAPIBusinessObjects();
   private final ObjectSequence m_rateObjectID = new ObjectSequence(1);
   private final ObjectSequence m_wbsNoteObjectID = new ObjectSequence(1);
   private final ObjectSequence m_activityNoteObjectID = new ObjectSequence(1);
   private final Set<FieldType> m_userDefinedFields;
   private final Currency m_defaultCurrency;
   private boolean m_defaultNotesTopicUsed;
}
