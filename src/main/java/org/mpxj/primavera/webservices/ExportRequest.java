package org.mpxj.primavera.webservices;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

class ExportRequest
{
   public void setFileType(String fileType)
   {
      m_fileType = fileType;
   }

   public void setProjectObjectId(List<Integer> projectObjectId)
   {
      m_projectObjectId = projectObjectId;
   }

   @JsonProperty("FileType") private String m_fileType;
   @JsonProperty("ProjectObjectId") private List<Integer> m_projectObjectId;
}
