package org.mpxj.msplanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class MsPlannerReader
{

   public static void main(String[] argv)
   {
      MsPlannerReader reader = new MsPlannerReader(argv[0], argv[1]);
      List<MsPlannerProject> projects = reader.getProjects();
      projects.forEach(System.out::println);
   }

   public MsPlannerReader(String host, String token)
   {
      m_host = host;
      m_token = token;
      m_mapper = new ObjectMapper();
      m_mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      m_mapper.registerModule(new SimpleModule().addDeserializer(Map.class, new JsonDeserializer<MapRow>()
      {
         @Override public MapRow deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
         {
            return ctxt.readValue(p, MapRow.class);
         }
      }));
   }

   public List<MsPlannerProject> getProjects()
   {
      HttpURLConnection connection = createConnection("msdyn_projects?$select=msdyn_projectid,msdyn_subject");
      int code = getResponseCode(connection);

      if (code != 200)
      {
         throw new MsPlannerException(getExceptionMessage(connection, code));
      }

      MapRow data = getMapRow(connection);

      return data.getList("value").stream()
         .map(d -> new MsPlannerProject(d.getUUID("msdyn_projectid"), d.getString("msdyn_subject")))
         .collect(Collectors.toList());
   }

   /**
    * Create an HttpURLConnection instance.
    *
    * @param path target path
    * @return HttpURLConnection instance
    */
   private HttpURLConnection createConnection(String path)
   {
      try
      {
         URL url = new URL(m_host + "/api/data/v9.1/" + path);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestProperty("Accept", "application/json");
         connection.setRequestProperty("Accept-Encoding", "gzip");
         connection.setRequestProperty("Authorization", "Bearer " + m_token);
         connection.setRequestMethod("GET");
         connection.connect();
         return connection;
      }

      catch (IOException ex)
      {
         throw new MsPlannerException(ex);
      }
   }

   /**
    * Retrieve the response code after making a request.
    *
    * @param connection request connection
    * @return response code
    */
   private int getResponseCode(HttpURLConnection connection)
   {
      try
      {
         return connection.getResponseCode();
      }

      catch (IOException ex)
      {
         throw new MsPlannerException(ex);
      }
   }

   /**
    * Generate an exception message detailing the request made and the response received.
    *
    * @param connection request connection
    * @param code response code
    * @return exception message
    */
   private String getExceptionMessage(HttpURLConnection connection, int code)
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

      return connection.getRequestMethod() + " " + connection.getURL() + " failed: " + "\nresponseCode=" + code + "\nresponseBody=" + responseBody;
   }

   /**
    * Deserializes a Microsoft Planner response into a MapRow instance.
    *
    * @param connection request connection
    * @return MapRow instance
    */
   private MapRow getMapRow(HttpURLConnection connection)
   {
      try
      {
         return m_mapper.readValue(getInputStream(connection), MapRow.class);
      }

      catch (IOException ex)
      {
         throw new MsPlannerException(ex);
      }
   }

   /**
    * Retrieves an InputStream instance from a PWA response.
    * Handles gzipped responses.
    *
    * @param connection request connection
    * @return InputStream instance
    */
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
         throw new MsPlannerException(ex);
      }
   }

   private final String m_host;
   private final String m_token;
   private final ObjectMapper m_mapper;
}
