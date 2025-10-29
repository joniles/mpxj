package org.mpxj.primavera;

import java.util.ArrayList;
import java.util.List;

import org.mpxj.ProjectContext;
import org.mpxj.primavera.schema.APIBusinessObjects;

class XmlReaderState
{
   public XmlReaderState(APIBusinessObjects apibo)
   {
      m_apibo = apibo;
   }

   public APIBusinessObjects getApibo()
   {
      return m_apibo;
   }

   public ProjectContext getContext()
   {
      return m_context;
   }

   public ClashMap getRoleClashMap()
   {
      return m_roleClashMap;
   }

   public List<ExternalRelation> getExternalRelations()
   {
      return externalRelations;
   }

   private final APIBusinessObjects m_apibo;
   private final ProjectContext m_context = new ProjectContext();
   private final ClashMap m_roleClashMap = new ClashMap();
   private final List<ExternalRelation> externalRelations = new ArrayList<>();
}
