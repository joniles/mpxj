package org.mpxj.primavera.eppm;

import com.fasterxml.jackson.annotation.JsonProperty;

class XmlExportRequest extends ExportRequest
{
   public void setEncoding(String encoding)
   {
      m_encoding = encoding;
   }

   @JsonProperty("Encoding") private String m_encoding = "UTF-8";
}
