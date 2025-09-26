package org.mpxj.primavera.eppm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.opc.OpcException;
import org.mpxj.opc.OpcExportType;
import org.mpxj.opc.OpcProject;
import org.mpxj.opc.OpcProjectBaseline;
import org.mpxj.reader.UniversalProjectReader;

public class EppmReader
{
   public static void main(String[] argv)
   {
      EppmReader reader = new EppmReader(argv[0], argv[1], argv[2], argv[3]);
      List<EppmProject> projects = reader.getProjects();
   }

   public EppmReader(String url, String databaseName, String user, String password)
   {
      m_url = url;
      m_databaseName = databaseName;
      m_user = user;
      m_password = password;

      m_mapper = new ObjectMapper();
      m_mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
   }

   public List<EppmProject> getProjects()
   {
      authenticate();

      HttpURLConnection connection = performGetRequest("project?Fields=ObjectId,Id,Name,DataDate,CurrentBaselineProjectObjectId");
      int code = getResponseCode(connection);
      if (code != 200)
      {
         throw new EppmException(getExceptionMessage(connection, code, "List projects request failed"));
      }

      return readValue(connection, new TypeReference<List<EppmProject>>()
      {
         // Empty block
      });
   }

   public ProjectFile readProject(EppmProject project) throws MPXJException
   {
      //return new UniversalProjectReader().read(getInputStreamForProject(project, EppmExportType.XML, false));
      return null;
   }

   private InputStream getInputStreamForProject(OpcProject project, List<OpcProjectBaseline> baselines, OpcExportType type, boolean compressed)
   {
      authenticate();

      return null;
   }

   private void authenticate()
   {
      if (m_cookies != null)
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
            throw new EppmAuthenticationException(getExceptionMessage(connection, code, "Authentication request failed"));
         }

         List<String> cookiesHeader = connection.getHeaderFields().get("Set-Cookie");
         if (cookiesHeader == null)
         {
            throw new EppmAuthenticationException("No cookies received");
         }

         m_cookies = cookiesHeader.stream().flatMap(c -> HttpCookie.parse(c).stream()).map(HttpCookie::toString).collect(Collectors.joining(";"));
      }

      catch (Exception ex)
      {
         throw new EppmAuthenticationException(ex);
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
         throw new EppmException(ex);
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
         throw new OpcException(ex);
      }
   }

   private HttpURLConnection createConnection(String path, String accept) throws IOException
   {
      URL url = new URL(m_url + "/restapi/" + path);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Accept", accept);
      connection.setRequestProperty("Accept-Encoding", "gzip");
      connection.setRequestProperty("Cookie", m_cookies);
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
         throw new EppmException(ex);
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
         throw new EppmException(ex);
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
         throw new EppmException(ex);
      }
   }

   private final String m_url;
   private final String m_databaseName;
   private final String m_user;
   private final String m_password;
   private String m_cookies;
   private final ObjectMapper m_mapper;
}
