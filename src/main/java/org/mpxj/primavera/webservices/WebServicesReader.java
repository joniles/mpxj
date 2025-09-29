/*
 * file:       WebServicesReader.java
 * author:     Jon Iles
 * date:       2025-09-29
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

package org.mpxj.primavera.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.common.InputStreamHelper;
import org.mpxj.reader.UniversalProjectReader;

/**
 * Access schedule data via P6 Web Services.
 */
public class WebServicesReader
{
   public WebServicesReader(String url, String databaseName, String user, String password)
   {
      this(url);
      m_databaseName = databaseName;
      m_user = user;
      m_password = password;
   }

   public WebServicesReader(String url, String bearerToken)
   {
      this(url);
      m_bearerToken = bearerToken;
   }

   private WebServicesReader(String url)
   {
      m_url = url;
      m_mapper = new ObjectMapper();
      m_mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
   }

   public List<WebServicesProject> getProjects()
   {
      authenticate();

      HttpURLConnection connection = performGetRequest("project?Fields=ObjectId,Id,Name,DataDate,CurrentBaselineProjectObjectId");
      int code = getResponseCode(connection);
      if (code != 200)
      {
         throw new WebServicesException(getExceptionMessage(connection, code, "List projects request failed"));
      }

      return readValue(connection, new TypeReference<List<WebServicesProject>>()
      {
         // Empty block
      });
   }

   public void exportProject(WebServicesProject project, String filename, WebServicesExportType type, boolean includeBaseline, boolean compressed) throws IOException
   {
      try (OutputStream os = Files.newOutputStream(Paths.get(filename)))
      {
         exportProject(project, os, type, includeBaseline, compressed);
      }
   }

   public void exportProject(WebServicesProject project, OutputStream stream, WebServicesExportType type, boolean inclueBaseline, boolean compressed) throws IOException
   {
      InputStreamHelper.writeInputStreamToOutputStream(getInputStreamForProject(project, type, inclueBaseline, compressed), stream);
   }

   public ProjectFile readProject(WebServicesProject project) throws MPXJException
   {
      return readProject(project, true);
   }

   public ProjectFile readProject(WebServicesProject project, boolean includeBaseline) throws MPXJException
   {
      return new UniversalProjectReader().read(getInputStreamForProject(project, WebServicesExportType.XML, includeBaseline, false));
   }

   private InputStream getInputStreamForProject(WebServicesProject project, WebServicesExportType type, boolean includeBaseline, boolean compressed)
   {
      authenticate();

      String path;
      ExportRequest request;
      if (type == WebServicesExportType.XML)
      {
         request = new XmlExportRequest();
         path = "export/exportProjects";
         request.setFileType(compressed ? "ZIP" : "XML");
         if (includeBaseline && project.getCurrentBaselineProjectObjectId() != null)
         {
            request.setProjectObjectId(Arrays.asList(project.getObjectId(), project.getCurrentBaselineProjectObjectId()));
         }
         else
         {
            request.setProjectObjectId(Collections.singletonList(project.getObjectId()));
         }
      }
      else
      {
         request = new ExportRequest();
         path = "export/exportXERProject";
         request.setFileType(compressed ? "ZIP" : "XER");
         request.setProjectObjectId(Collections.singletonList(project.getObjectId()));
      }

      HttpURLConnection connection = performPostRequest(path, request);
      int code = getResponseCode(connection);
      if (code != 200)
      {
         throw new WebServicesException(getExceptionMessage(connection, code, "Export project request failed"));
      }

      return getInputStream(connection);
   }

   private void authenticate()
   {
      if (m_cookies != null || m_bearerToken != null)
      {
         return;
      }

      try
      {
         URL url = new URL(m_url + "/restapi/login?DatabaseName=" + m_databaseName);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("POST");

         String auth = m_user + ":" + m_password;
         byte[] authToken = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
         connection.setRequestProperty("authToken", new String(authToken));

         connection.connect();
         int code = connection.getResponseCode();
         if (code != 200)
         {
            throw new WebServicesAuthenticationException(getExceptionMessage(connection, code, "Authentication request failed"));
         }

         List<String> cookiesHeader = connection.getHeaderFields().get("Set-Cookie");
         if (cookiesHeader == null)
         {
            throw new WebServicesAuthenticationException("No cookies received");
         }

         m_cookies = cookiesHeader.stream().flatMap(c -> HttpCookie.parse(c).stream()).map(HttpCookie::toString).collect(Collectors.joining(";"));
      }

      catch (Exception ex)
      {
         throw new WebServicesAuthenticationException(ex);
      }
   }

   private String getExceptionMessage(HttpURLConnection connection, int code, String message)
   {
      String responseBody = "";

      try
      {
         InputStream stream = connection.getErrorStream();
         if (stream == null)
         {
            stream = connection.getInputStream();
         }

         try (BufferedReader br = new BufferedReader(new InputStreamReader(stream)))
         {
            responseBody = br.lines().collect(Collectors.joining(System.lineSeparator()));
         }
      }

      catch (IOException ex)
      {
         // Ignore exceptions when trying to retrieve the response body
      }

      return message + "\nresponseCode=" + code + "\nresponseBody=" + responseBody;
   }

   private HttpURLConnection performGetRequest(String path)
   {
      return performGetRequest(path, "application/json");
   }

   private HttpURLConnection performGetRequest(String path, String accept)
   {
      try
      {
         HttpURLConnection connection = createConnection(path, accept);
         connection.setRequestMethod("GET");
         connection.connect();
         return connection;
      }

      catch (IOException ex)
      {
         throw new WebServicesException(ex);
      }
   }

   private HttpURLConnection performPostRequest(String path, Object body)
   {
      try
      {
         HttpURLConnection connection = createConnection(path, "application/json");
         connection.setRequestMethod("POST");
         connection.setRequestProperty("Content-Type", "application/json");
         connection.setDoOutput(true);
         m_mapper.writeValue(connection.getOutputStream(), body);
         connection.connect();
         return connection;
      }

      catch (IOException ex)
      {
         throw new WebServicesException(ex);
      }
   }

   private HttpURLConnection createConnection(String path, String accept) throws IOException
   {
      URL url = new URL(m_url + "/restapi/" + path);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Accept", accept);
      connection.setRequestProperty("Accept-Encoding", "gzip");

      if (m_bearerToken == null)
      {
         connection.setRequestProperty("Cookie", m_cookies);
      }
      else
      {
         connection.setRequestProperty("Authorization", "Bearer " + m_bearerToken);
      }

      return connection;
   }

   private int getResponseCode(HttpURLConnection connection)
   {
      try
      {
         return connection.getResponseCode();
      }

      catch (IOException ex)
      {
         throw new WebServicesException(ex);
      }
   }

   private <T> T readValue(HttpURLConnection connection, TypeReference<T> valueTypeRef)
   {
      try
      {
         return m_mapper.readValue(getInputStream(connection), valueTypeRef);
      }

      catch (IOException ex)
      {
         throw new WebServicesException(ex);
      }
   }

   private InputStream getInputStream(HttpURLConnection connection)
   {
      try
      {
         if ("gzip".equals(connection.getContentEncoding()))
         {
            return new GZIPInputStream(connection.getInputStream());
         }
         return connection.getInputStream();
      }

      catch (IOException ex)
      {
         throw new WebServicesException(ex);
      }
   }

   private final String m_url;
   private String m_databaseName;
   private String m_user;
   private String m_password;
   private String m_cookies;
   private String m_bearerToken;
   private final ObjectMapper m_mapper;
}
