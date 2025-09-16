/*
 * file:       OpcReader.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

/**
 * Access schedule data in Oracle Primavera Cloud (OPC).
 */
public class OpcReader
{
   /**
    * Constructor.
    *
    * @param host OPC hostname
    * @param user username
    * @param password password
    */
   public OpcReader(String host, String user, String password)
   {
      m_host = host;
      m_user = user;
      m_password = password;

      m_mapper = new ObjectMapper();
      m_mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
   }

   /**
    * Retrieve the start value for number of milliseconds to wait before checking
    * the export job status. Actual interval waited each time is
    * exportPollInterval * exportPollNumber, where exportPollNumber is 1..exportPollCount.
    * The default value is 1000 milliseconds.
    *
    * @return export poll interval in milliseconds
    */
   public long getExportPollInterval()
   {
      return m_exportPollInterval;
   }

   /**
    * Set the start value for number of milliseconds to wait before checking
    * the export job status. Actual interval waited each time is
    * exportPollInterval * exportPollNumber, where exportPollNumber is 1..exportPollCount.
    * The default value is 1000 milliseconds.
    *
    * @param exportPollInterval export poll interval in milliseconds
    */
   public void setExportPollInterval(long exportPollInterval)
   {
      m_exportPollInterval = exportPollInterval;
   }

   /**
    * Retrieve the number of times we'll check the status of the
    * export job before giving up.
    *
    * @return number of times to check the status of the export job
    */
   public int getExportPollCount()
   {
      return m_exportPollCount;
   }

   /**
    * Set the number of times we'll check the status of the
    * export job before giving up.
    *
    * @param exportPollCount number of times to check the status of the export job
    */
   public void setExportPollCount(int exportPollCount)
   {
      m_exportPollCount = exportPollCount;
   }

   /**
    * Retrieves a list of OpcProject instances representing the projects in OPC.
    * NOTE: the implementation of this method assumes you have less than 5000
    * projects per workspace.
    * TODO: implement support for pagination
    *
    * @return list of projects
    */
   public List<OpcProject> getProjects()
   {
      authenticate();
      return getWorkspaces().stream().flatMap(w -> getProjectsInWorkspace(w).stream()).collect(Collectors.toList());
   }

   /**
    * Retrieves a list of baselines available for a given project.
    * Note: the implementation of this method assumes you have less
    * than 5000 baselines per project.
    * TODO: implement support for pagination
    *
    * @param project project details
    * @return list of baselines
    */
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
         throw new OpcException(getExceptionMessage(connection, code, "List project baselines request failed"));
      }

      return readValue(connection, new TypeReference<List<OpcProjectBaseline>>()
      {
         // Empty block
      });
   }

   /**
    * Export a project to a named file.
    *
    * @param project project to export
    * @param filename target filename
    * @param type target file type
    * @param compressed true if the output is written as a zip file
    */
   public void exportProject(OpcProject project, String filename, OpcExportType type, boolean compressed) throws IOException
   {
      exportProject(project, Collections.emptyList(), filename, type, compressed);
   }

   /**
    * Export a project with baselines to a named file.
    *
    * @param project project to export
    * @param baselines baselines to export
    * @param filename target filename
    * @param type target file type
    * @param compressed true if the output is written as a zip file
    */
   public void exportProject(OpcProject project, List<OpcProjectBaseline> baselines, String filename, OpcExportType type, boolean compressed) throws IOException
   {
      try (OutputStream os = Files.newOutputStream(Paths.get(filename)))
      {
         exportProject(project, baselines, os, type, compressed);
      }
   }

   /**
    * Export a project to a file identified by a File instance.
    *
    * @param project project to export
    * @param file target File instance
    * @param type target file type
    * @param compressed true if the output is written as a zip file
    */
   public void exportProject(OpcProject project, File file, OpcExportType type, boolean compressed) throws IOException
   {
      exportProject(project, Collections.emptyList(), file, type, compressed);
   }

   /**
    * Export a project with baselines to a file identified by a File instance.
    *
    * @param project project to export
    * @param baselines list of baselines to export
    * @param file target File instance
    * @param type target file type
    * @param compressed true if the output is written as a zip file
    */
   public void exportProject(OpcProject project, List<OpcProjectBaseline> baselines, File file, OpcExportType type, boolean compressed) throws IOException
   {
      try (OutputStream os = Files.newOutputStream(file.toPath()))
      {
         exportProject(project, baselines, os, type, compressed);
      }
   }

   /**
    * Export a project to an output stream.
    *
    * @param project project to export
    * @param stream target output stream
    * @param type target file type
    * @param compressed true if the output is written as a zip file
    */
   public void exportProject(OpcProject project, OutputStream stream, OpcExportType type, boolean compressed) throws IOException
   {
      exportProject(project, Collections.emptyList(), stream, type, compressed);
   }

   /**
    * Export a project with baselines to an output stream.
    *
    * @param project project to export
    * @param baselines list of baselines to export
    * @param stream target output stream
    * @param type target file type
    * @param compressed true if the output is written as a zip file
    */
   public void exportProject(OpcProject project, List<OpcProjectBaseline> baselines, OutputStream stream, OpcExportType type, boolean compressed) throws IOException
   {
      InputStreamHelper.writeInputStreamToOutputStream(getInputStreamForProject(project, baselines, type, compressed), stream);
   }

   /**
    * Read a project.
    *
    * @param project project details
    * @return ProjectFile instance
    */
   public ProjectFile readProject(OpcProject project) throws MPXJException
   {
      return readProject(project, Collections.emptyList());
   }

   /**
    * Read a project with baselines.
    *
    * @param project project details
    * @param baselines baselines to read
    * @return ProjectFile instance
    */
   public ProjectFile readProject(OpcProject project, List<OpcProjectBaseline> baselines) throws MPXJException
   {
      return new UniversalProjectReader().read(getInputStreamForProject(project, baselines, OpcExportType.XML, false));
   }

   /**
    * Retrieve a project from OPC as an input stream.
    *
    * @param project project details
    * @param baselines list of baselines
    * @param type required file type
    * @param compressed true if the output is returned as a zip file
    * @return InputStream instance
    */
   private InputStream getInputStreamForProject(OpcProject project, List<OpcProjectBaseline> baselines, OpcExportType type, boolean compressed)
   {
      authenticate();
      long jobId = startExportJob(project, baselines, type, compressed);
      waitForExportJob(jobId);
      return downloadProject(jobId);
   }

   /**
    * Returns true if the job status indicates that the job is complete.
    *
    * @param status job status
    * @return true if the job is complete
    */
   private boolean jobIsComplete(JobStatus status)
   {
      return status != null && "COMPLETED".equals(status.getJobStatus());
   }

   /**
    * Send a request to OPC to start a project export.
    *
    * @param project project to export
    * @param baselines baselines to export
    * @param type required file type
    * @param compressed true if the output is written as a zip file
    * @return ID of the export job
    */
   private long startExportJob(OpcProject project, List<OpcProjectBaseline> baselines, OpcExportType type, boolean compressed)
   {
      ExportRequest exportRequest = new ExportRequest(project, baselines, compressed);
      String path = type == OpcExportType.XML ? "action/exportP6xml" : "action/exportP6xer";
      HttpURLConnection connection = performPostRequest(path, exportRequest);
      int code = getResponseCode(connection);
      if (code != 201)
      {
         throw new OpcException(getExceptionMessage(connection, code, "Export project request failed"));
      }

      JobStatus status = readValue(connection, JobStatus.class);
      return status.getJobId();
   }

   /**
    * Wait for an export request to complete.
    * Utilises a form of exponential backoff to
    * sleep between status requests.
    *
    * @param jobId ID of the export job
    */
   private void waitForExportJob(long jobId)
   {
      String path = "action/jobStatus/" + jobId;

      long retryCount = 1;
      JobStatus jobStatus = null;

      while (retryCount < m_exportPollCount)
      {
         try
         {
            Thread.sleep(retryCount * m_exportPollInterval);
         }

         catch (InterruptedException ex)
         {
            // ignore
         }

         HttpURLConnection connection = performGetRequest(path);
         int code = getResponseCode(connection);
         if (code != 200)
         {
            throw new OpcException(getExceptionMessage(connection, code, "Export job status request failed"));
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

   /**
    * Download the exported project and return an input stream representing
    * the project data.
    *
    * @param jobId export job ID
    * @return exported project data as an input stream
    */
   private InputStream downloadProject(long jobId)
   {
      HttpURLConnection connection = performGetRequest("action/download/job/" + jobId, "*/*");
      int code = getResponseCode(connection);
      if (code != 200)
      {
         throw new OpcException(getExceptionMessage(connection, code, "Export download request failed"));
      }
      return getInputStream(connection);
   }

   /**
    * Retrieve a list of the available workspaces.
    *
    * @return list of workspaces
    */
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
         throw new OpcException(getExceptionMessage(connection, code, "List workspaces request failed"));
      }

      return readValue(connection, new TypeReference<List<Workspace>>()
      {
         // Empty block
      });
   }

   /**
    * Retrieve a list of projects in a workspace.
    *
    * @param workspace target workspace
    * @return list of projects in the workspace
    */
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
         throw new OpcException(getExceptionMessage(connection, code, "List projects in workspace request failed"));
      }

      return readValue(connection, new TypeReference<List<OpcProject>>()
      {
         // Empty block
      });
   }

   /**
    * Authenticate with OPC if the current auth token is invalid, and store the new auth token.
    */
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
            throw new OpcAuthenticationException(getExceptionMessage(connection, code, "Authentication request failed"));
         }

         m_tokenResponse = m_mapper.readValue(getInputStream(connection), TokenResponse.class);
      }

      catch (Exception ex)
      {
         throw new OpcAuthenticationException(ex);
      }
   }

   /**
    * Perform a GET request with a JSON response.
    *
    * @param path request path
    * @return connection ready to read status and response
    */
   private HttpURLConnection performGetRequest(String path)
   {
      return performGetRequest(path, "application/json");
   }

   /**
    * Perform a GET request and accept the provided content type.
    *
    * @param path request path
    * @param accept accepted content type
    * @return connection ready to read status and response
    */
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
         throw new OpcException(ex);
      }
   }

   /**
    * Perform a POST request.
    *
    * @param path request path
    * @param body request body to be serialized to JSON
    * @return connection ready to read status and response
    */
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

   /**
    * Create a connection to the supplied path.
    *
    * @param path target path
    * @param accept content type to accept
    * @return HttpURLConnection configured connection
    */
   private HttpURLConnection createConnection(String path, String accept) throws IOException
   {
      URL url = new URL("https://" + m_host + "/api/restapi/" + path);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Accept", accept);
      connection.setRequestProperty("Accept-Encoding", "gzip");
      connection.setRequestProperty("Version", "3");
      connection.setRequestProperty("Authorization", "Bearer " + m_tokenResponse.getAccessToken());
      m_tokenResponse.getRequestHeaders().forEach(connection::setRequestProperty);
      return connection;
   }

   /**
    * Retrieve an input stream from a connection, handling gzipped content.
    *
    * @param connection connection
    * @return input stream
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
         throw new OpcException(ex);
      }
   }

   /**
    * Retrieve the response code from a connection,
    * wrap any IOException in an OPCException.
    *
    * @param connection target connection
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
         throw new OpcException(ex);
      }
   }

   /**
    * Unmarshall a collection of JSON values.
    *
    * @param <T> unmarshalled type
    * @param connection target connection
    * @param valueTypeRef generic collection type
    * @return collection of object instances representing unmarshalled JSON values
    */
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

   /**
    * Unmarshall a JSON value as an object.
    *
    * @param <T> unmarshalled type
    * @param connection target connection
    * @param clazz target object class
    * @return unmarshalled data as a object
    */
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

   /**
    * Augment the supplied exception message with the response code and
    * response body from the connection.
    *
    * @param connection target connection
    * @param code response code
    * @param message message to augment
    * @return augmented message including response code and response body
    */
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

   private final String m_host;
   private final String m_user;
   private final String m_password;
   private final ObjectMapper m_mapper;
   private int m_exportPollCount = 15;
   private long m_exportPollInterval = 1000;
   private TokenResponse m_tokenResponse = TokenResponse.DEFAULT_TOKEN;
}
