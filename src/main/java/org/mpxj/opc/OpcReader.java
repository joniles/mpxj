package org.mpxj.opc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.message.GZipEncoder;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.common.InputStreamHelper;
import org.mpxj.reader.UniversalProjectReader;

// TODO - pagination

public class OpcReader
{
   public static void main(String[] argv) throws Exception
   {
      Logger log = Logger.getLogger("OPCReader");
      log.setLevel(Level.INFO);
      ConsoleHandler handler = new ConsoleHandler();
      handler.setFormatter(new SimpleFormatter());
      log.addHandler(handler);

      OpcReader reader = new OpcReader(argv[0], argv[1], argv[2]);
      reader.setLogger(log, Level.INFO);


      List<OpcProject> projects = reader.getProjects();
      projects.forEach(System.out::println);

//      OpcProject project = new OpcProject();
//      project.setProjectId(14501);
//      project.setWorkspaceId(6003);

        //List<OpcProjectBaseline> baselines = reader.getProjectBaselines(project);

      //      28101
      //      34101

      //reader.exportProject(project, baselines, "/Users/joniles/Downloads/export.xml", ExportType.XML, false);
//      reader.exportProject(project, "/Users/joniles/Downloads/export.xml.zip", ExportType.XML, true);
      //reader.exportProject(project, "/Users/joniles/Downloads/export.xer", ExportType.XER, false);
//      reader.exportProject(project, "/Users/joniles/Downloads/export.xer.zip", ExportType.XER, true);


      //ProjectFile mpxj = reader.readProject(project);

      System.out.println("done");
   }

   public OpcReader(String host, String user, String password)
   {
      m_host = host;
      m_user = user;
      m_password = password;
   }

   public void setLogger(Logger logger, Level level)
   {
      m_logger = new LoggingFeature(logger, level, null, null);
   }

   public List<OpcProject> getProjects()
   {
      createDefaultClientNew();
      authenticate();
      return getWorkspaces().stream().flatMap(w -> getProjectsInWorkspace(w).stream()).collect(Collectors.toList());
   }

   public List<OpcProjectBaseline> getProjectBaselines(OpcProject project)
   {
      createDefaultClientNew();
      authenticate();
      Invocation.Builder builder = getInvocationBuilder("action/baseline/project/" + project.getProjectId());
      List<OpcProjectBaseline> result = builder.get().readEntity(new GenericType<List<OpcProjectBaseline>>() {});
      return result == null ? Collections.emptyList() : result;
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
      createDefaultClientNew();
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
      Invocation.Builder builder = getInvocationBuilder(path);
      return builder.post(Entity.entity(exportRequest, MediaType.APPLICATION_JSON)).readEntity(JobStatus.class).getJobId();
   }

   private void waitForExportJob(long jobId)
   {
      Invocation.Builder builder = getInvocationBuilder("action/jobStatus/"+ jobId);

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

         jobStatus = builder.get().readEntity(JobStatus.class);
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
      Client client = JerseyClientBuilder.newClient();
      if (m_logger != null)
      {
         client.register(m_logger);
      }
      client.register(GZipEncoder.class);
      client.register(EncodingFilter.class);

      Response response = getInvocationBuilder("action/download/job/" + jobId, MediaType.WILDCARD_TYPE).get();
      if(response.getStatus() != Response.Status.OK.getStatusCode())
      {
         throw new OpcDownloadException("Download failed with status " + response.getStatus());
      }

      return response.readEntity(InputStream.class);
   }

   private List<Workspace> getWorkspaces()
   {
      Invocation.Builder builder = getInvocationBuilder("workspace");
      List<Workspace> result = builder.get().readEntity(new GenericType<List<Workspace>>() {});
      return result == null ? Collections.emptyList() : result;
   }

   private List<OpcProject> getProjectsInWorkspace(Workspace workspace)
   {
      Invocation.Builder builder = getInvocationBuilder("project/workspace/" + workspace.getWorkspaceId());
      List<OpcProject> result = builder.get().readEntity(new GenericType<List<OpcProject>>() {});
      return result == null ? Collections.emptyList() : result;
   }

   private void authenticate()
   {
      if (m_tokenResponse.valid())
      {
         return;
      }

      try
      {
         m_client.register(HttpAuthenticationFeature.basic(m_user, m_password));
         WebTarget webTarget = m_client.target("https://" + m_host)
            .path("primediscovery/apitoken/request")
            .queryParam("scope", "http://" + m_host + "/api");

         m_tokenResponse = webTarget
            .request(MediaType.APPLICATION_JSON)
            .post(null, TokenResponse.class);
      }

      catch (Exception ex)
      {
         throw new OpcAuthenticationException(ex);
      }
   }

   private void createDefaultClientNew()
   {
      if (m_client != null)
      {
         return;
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      m_client = JerseyClientBuilder.newClient().register(new JacksonJsonProvider(mapper));
      if (m_logger != null)
      {
         m_client.register(m_logger);
      }

      m_client.register(GZipEncoder.class);
      m_client.register(EncodingFilter.class);
   }

   private Invocation.Builder getInvocationBuilder(String path)
   {
      return getInvocationBuilder(path, MediaType.APPLICATION_JSON_TYPE);
   }

   private Invocation.Builder getInvocationBuilder(String path, MediaType mediaType)
   {
      WebTarget target = m_client.target("https://" + m_host).path("api/restapi").path(path);
      Invocation.Builder builder = target.request(mediaType);
      builder.header("Version", "3");
      builder.header("Authorization", "Bearer " + m_tokenResponse.getAccessToken());
      m_tokenResponse.getRequestHeaders().forEach(builder::header);
      return builder;
   }

   private final String m_host;
   private final String m_user;
   private final String m_password;
   private LoggingFeature m_logger;
   private Client m_client;
   private TokenResponse m_tokenResponse = TokenResponse.DEFAULT_TOKEN;
}
