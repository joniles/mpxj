package org.mpxj.primavera.eppm;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

class ExportRequest
{
   @JsonProperty("Encoding") private String m_encoding;
   @JsonProperty("FileType") private String m_fileType;
   @JsonProperty("ProjectObjectId") private List<Integer> m_projectObjectId;
}
