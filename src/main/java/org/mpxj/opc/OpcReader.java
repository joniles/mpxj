package org.mpxj.opc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

// TODO - pagination

public class OpcReader
{
   public static void main(String[] argv) throws Exception
   {
      OpcReader reader = new OpcReader(argv[0], argv[1], argv[2]);

//      List<OpcProject> projects = reader.getProjects();
//      projects.forEach(System.out::println);

//      OpcProject project = new OpcProject();
//      project.setProjectId(14501);
//      project.setWorkspaceId(6003);

      //List<OpcProjectBaseline> baselines = reader.getProjectBaselines(project);

      //      28101
      //      34101

      //reader.exportProject(project, "/Users/joniles/Downloads/export.xml", OpcExportType.XML, false);
      //reader.exportProject(project, baselines, "/Users/joniles/Downloads/export.xml.zip", OpcExportType.XML, true);
      //reader.exportProject(project, "/Users/joniles/Downloads/export.xer", OpcExportType.XER, false);
      //reader.exportProject(project, "/Users/joniles/Downloads/export.xer.zip", OpcExportType.XER, true);


      //ProjectFile mpxj = reader.readProject(project);

      System.out.println("done");
   }

   public OpcReader(String host, String user, String password)
   {
      m_host = host;
      m_user = user;
      m_password = password;

      m_mapper = new ObjectMapper();
      m_mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
   }

   public List<OpcProject> getProjects()
   {
      authenticate();
      return getWorkspaces().stream().flatMap(w -> getProjectsInWorkspace(w).stream()).collect(Collectors.toList());
   }

   public List<OpcProjectBaseline> getProjectBaselines(OpcProject project)
   {
      authenticate();

      HttpURLConnection connection = performGetRequest("action/baseline/project/" + project.getProjectId());
      int code = getResponseCode(connection);
      if (code == 204)
      {
         return Collections.emptyList();
      }

      if (code != 200)
      {
         throw new OpcException("List workspaces request failed with code " + code);
      }

      return readValue(connection, new TypeReference<List<OpcProjectBaseline>>() {});
   }

   public void exportProject(OpcProject project, String filename, OpcExportType type, boolean compressed) throws IOException
   {
      exportProject(project, Collections.emptyList(), filename, type, compressed);
   }

   public void exportProject(OpcProject project, List<OpcProjectBaseline> baselines, String filename, OpcExportType type, boolean compressed) throws IOException
   {
      try(OutputStream os = Files.newOutputStream(Paths.get(filename)))
      {
         exportProject(project, baselines, os, type, compressed);
      }
   }

   public void exportProject(OpcProject project, File file, OpcExportType type, boolean compressed) throws IOException
   {
      exportProject(project, Collections.emptyList(), file, type, compressed);
   }

   public void exportProject(OpcProject project, List<OpcProjectBaseline> baselines, File file, OpcExportType type, boolean compressed) throws IOException
   {
      try(OutputStream os = Files.newOutputStream(file.toPath()))
      {
         exportProject(project, baselines, os, type, compressed);
      }
   }

   public void exportProject(OpcProject project, OutputStream stream, OpcExportType type, boolean compressed) throws IOException
   {
      exportProject(project, Collections.emptyList(), stream, type, compressed);
   }

   public void exportProject(OpcProject project, List<OpcProjectBaseline> baselines, OutputStream stream, OpcExportType type, boolean compressed) throws IOException
   {
      InputStreamHelper.writeInputStreamToOutputStream(getInputStreamForProject(project, baselines, type, compressed), stream);
   }

   public ProjectFile readProject(OpcProject project) throws MPXJException
   {
      return readProject(project, Collections.emptyList());
   }

   public ProjectFile readProject(OpcProject project, List<OpcProjectBaseline> baselines) throws MPXJException
   {
      return new UniversalProjectReader().read(getInputStreamForProject(project, baselines, OpcExportType.XML, true));
   }

   private InputStream getInputStreamForProject(OpcProject project, List<OpcProjectBaseline> baselines, OpcExportType type, boolean compressed)
   {
      authenticate();
      long jobId = startExportJob(project, baselines, type, compressed);
      waitForExportJob(jobId);
      return downloadProject(jobId);
   }

   private boolean jobIsComplete(JobStatus status)
   {
      return status != null && "COMPLETED".equals(status.getJobStatus());
   }

   private long startExportJob(OpcProject project, List<OpcProjectBaseline> baselines, OpcExportType type, boolean compressed)
   {
      ExportRequest exportRequest = new ExportRequest(project, baselines, compressed);
      String path = type == OpcExportType.XML ? "action/exportP6xml" : "action/exportP6xer";
      HttpURLConnection connection = performPostRequest(path, exportRequest);
      int code = getResponseCode(connection);
      if (code != 201)
      {
         throw new OpcException("Export request failed with code " + code);
      }

      JobStatus status = readValue(connection, JobStatus.class);
      return status.getJobId();
   }

   private void waitForExportJob(long jobId)
   {
      String path = "action/jobStatus/"+ jobId;

      long retryCount = 1;
      JobStatus jobStatus = null;

      while (retryCount < 15)
      {
         try
         {
            Thread.sleep(retryCount * 1000);
         }

         catch (InterruptedException ex)
         {
            // ignore
         }

         HttpURLConnection connection = performGetRequest(path);
         int code = getResponseCode(connection);
         if (code != 200)
         {
            throw new OpcException("Export job status request failed with code " + code);
         }

         jobStatus = readValue(connection, JobStatus.class);
         if (jobIsComplete(jobStatus))
         {
            break;
         }

         ++retryCount;
      }

      if (!jobIsComplete(jobStatus))
      {
         throw new OpcExportJobTimeoutException();
      }
   }

   private InputStream downloadProject(long jobId)
   {
      HttpURLConnection connection = performGetRequest("action/download/job/" + jobId, "*/*");
      int code = getResponseCode(connection);
      if (code != 200)
      {
         throw new OpcException("Export download request failed with code " + code);
      }
      return getInputStream(connection);
   }

   private List<Workspace> getWorkspaces()
   {
      HttpURLConnection connection = performGetRequest("workspace");
      int code = getResponseCode(connection);
      if (code == 204)
      {
         return Collections.emptyList();
      }

      if (code != 200)
      {
         throw new OpcException("List workspaces request failed with status " + code);
      }

      return readValue(connection, new TypeReference<List<Workspace>>() {});
   }

   private List<OpcProject> getProjectsInWorkspace(Workspace workspace)
   {
      HttpURLConnection connection = performGetRequest("project/workspace/" + workspace.getWorkspaceId());
      int code = getResponseCode(connection);
      if (code == 204)
      {
         return Collections.emptyList();
      }

      if (code != 200)
      {
         throw new OpcException("List workspaces request failed with status " + code);
      }

      return readValue(connection, new TypeReference<List<OpcProject>>() {});
   }

   private void authenticate()
   {
      if (m_tokenResponse.valid())
      {
         return;
      }

      try
      {
         URL url = new URL("https://" + m_host + "/primediscovery/apitoken/request?scope=http://" + m_host + "/api");
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("POST");
         connection.setRequestProperty("Accept", "application/json");
         connection.setRequestProperty("Accept-Encoding", "gzip");

         String auth = m_user + ":" + m_password;
         byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
         String authHeaderValue = "Basic " + new String(encodedAuth);
         connection.setRequestProperty("Authorization", authHeaderValue);

         connection.connect();
         int code = connection.getResponseCode();
         if (code != 200)
         {
            throw new OpcAuthenticationException("Authentication request failed with status " + code);
         }

         m_tokenResponse = m_mapper.readValue(getInputStream(connection), TokenResponse.class);
      }

      catch (Exception ex)
      {
         throw new OpcAuthenticationException(ex);
      }
   }

   private HttpURLConnection performGetRequest(String path)
   {
      return performGetRequest(path, "application/json");
   }

   private HttpURLConnection performGetRequest(String path, String accept)
   {
      try
      {
         URL url = new URL("https://" + m_host + "/api/restapi/" + path);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestProperty("Accept", accept);
         connection.setRequestProperty("Accept-Encoding", "gzip");
         connection.setRequestProperty("Version", "3");
         connection.setRequestProperty("Authorization", "Bearer " + m_tokenResponse.getAccessToken());
         m_tokenResponse.getRequestHeaders().forEach(connection::setRequestProperty);

         connection.setRequestMethod("GET");
         connection.connect();
         return connection;
      }

      catch (IOException ex)
      {
         throw new OpcException(ex);
      }
   }

   private HttpURLConnection performPostRequest(String path, Object body)
   {
      try
      {
         URL url = new URL("https://" + m_host + "/api/restapi/" + path);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestProperty("Accept", "application/json");
         connection.setRequestProperty("Accept-Encoding", "gzip");
         connection.setRequestProperty("Version", "3");
         connection.setRequestProperty("Authorization", "Bearer " + m_tokenResponse.getAccessToken());
         m_tokenResponse.getRequestHeaders().forEach(connection::setRequestProperty);

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
         throw new OpcException(ex);
      }
   }

   private int getResponseCode(HttpURLConnection connection)
   {
      try
      {
         return connection.getResponseCode();
      }

      catch (IOException ex)
      {
         throw new OpcException(ex);
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
         throw new OpcException(ex);
      }
   }

   private <T> T readValue(HttpURLConnection connection, Class<T> clazz)
   {
      try
      {
         return m_mapper.readValue(getInputStream(connection), clazz);
      }

      catch (IOException ex)
      {
         throw new OpcException(ex);
      }
   }

   private final String m_host;
   private final String m_user;
   private final String m_password;
   private final ObjectMapper m_mapper;
   private TokenResponse m_tokenResponse = TokenResponse.DEFAULT_TOKEN;
}
