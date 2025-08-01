/*
 * file:       TokenResponse.java
 * author:     Jon Iles
 * date:       2025-07-09
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj.opc;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents the response to a request for an auth token.
 */
class TokenResponse
{
   /**
    * Retrieve the access token.
    *
    * @return access token
    */
   public String getAccessToken()
   {
      return m_accessToken;
   }

   /**
    * Sets the access token.
    *
    * @param accessToken access token
    */
   public void setAccessToken(String accessToken)
   {
      m_accessToken = accessToken;
   }

   /**
    * Retrieves the number of seconds the auth token expires.
    *
    * @return seconds to expiry
    */
   public int getExpiresIn()
   {
      return m_expiresIn;
   }

   /**
    * Sets the auth token expiry in seconds.
    *
    * @param expiresIn seconds to expiry
    */
   public void setExpiresIn(int expiresIn)
   {
      m_expiresIn = expiresIn;
   }

   /**
    * Retrieve the headers OPC requires to be passed with all requests.
    *
    * @return required headers
    */
   public Map<String, String> getRequestHeaders()
   {
      return m_requestHeaders;
   }

   /**
    * Sets the headers OPC requires to be passed with all requests.
    *
    * @param requestHeaders required headers
    */
   public void setRequestHeaders(Map<String, String> requestHeaders)
   {
      m_requestHeaders = requestHeaders;
   }

   /**
    * Returns true if the access token is still valid.
    * Note: adds a 60-second buffer time, so the access token will be renewed before it expires.
    *
    * @return true if the access token is still valid
    */
   public boolean valid()
   {
      return LocalDateTime.now().isBefore(m_createdAt.plusSeconds(m_expiresIn - 60));
   }

   private final LocalDateTime m_createdAt = LocalDateTime.now();
   private String m_accessToken;
   private int m_expiresIn;
   private Map<String, String> m_requestHeaders;

   public static final TokenResponse DEFAULT_TOKEN = new TokenResponse();
}
