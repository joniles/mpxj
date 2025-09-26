package org.mpxj.primavera.eppm;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EppmProject
{
   public String getId()
   {
      return m_id;
   }

   public void setId(String id)
   {
      m_id = id;
   }

   public String getName()
   {
      return m_name;
   }

   public void setName(String name)
   {
      m_name = name;
   }

   public Integer getObjectId()
   {
      return m_objectId;
   }

   public void setObjectId(Integer objectId)
   {
      m_objectId = objectId;
   }

   public String getDataDate()
   {
      return m_dataDate;
   }

   public void setDataDate(String dataDate)
   {
      m_dataDate = dataDate;
   }

   public Integer getCurrentBaselineProjectObjectId()
   {
      return m_currentBaselineProjectObjectId;
   }

   public void setCurrentBaselineProjectObjectId(Integer currentBaselineProjectObjectId)
   {
      m_currentBaselineProjectObjectId = currentBaselineProjectObjectId;
   }

   @JsonProperty("Id") private String m_id;
   @JsonProperty("Name") private String m_name;
   @JsonProperty("ObjectId") private Integer m_objectId;
   @JsonProperty("DataDate") private String m_dataDate;
   @JsonProperty("CurrentBaselineProjectObjectId") private Integer m_currentBaselineProjectObjectId;
}
