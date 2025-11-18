package org.mpxj.primavera;

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

   public PrimaveraPMObjectSequences getSequences()
   {
      return sequences;
   }

   private final APIBusinessObjects m_apibo;
   private final PrimaveraPMObjectSequences sequences = new PrimaveraPMObjectSequences();
}
