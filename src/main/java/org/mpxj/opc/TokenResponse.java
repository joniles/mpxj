package org.mpxj.opc;

import java.time.LocalDateTime;
import java.util.Map;

class TokenResponse
{
   public String getAccessToken()
   {
      return m_accessToken;
   }

   public void setAccessToken(String accessToken)
   {
      m_accessToken = accessToken;
   }

   public int getExpiresIn()
   {
      return m_expiresIn;
   }

   public void setExpiresIn(int expiresIn)
   {
      m_expiresIn = expiresIn;
   }

   public Map<String, String> getRequestHeaders()
   {
      return m_requestHeaders;
   }

   public void setRequestHeaders(Map<String, String> requestHeaders)
   {
      m_requestHeaders = requestHeaders;
   }

   public boolean valid()
   {
      return LocalDateTime.now().isBefore(m_createdAt.plusSeconds(m_expiresIn-60));
   }

   private final LocalDateTime m_createdAt = LocalDateTime.now();
   private String m_accessToken;
   private int m_expiresIn;
   private Map<String, String> m_requestHeaders;

   public static final TokenResponse DEFAULT_TOKEN = new TokenResponse();
}
