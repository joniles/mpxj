package org.mpxj.opc;

import java.io.File;
import java.io.IOException;
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
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.message.GZipEncoder;

public class OpcReader
{
   public static void main(String[] argv) throws IOException
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
//
//      reader.exportProject(project, "/Users/joniles/Downloads/export.xml");

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
      createClient();
      authenticate();
      return getWorkspaces().stream().flatMap(w -> getProjectsInWorkspace(w).stream()).collect(Collectors.toList());
   }

   public void exportProject(OpcProject project, String filename) throws IOException
   {
      try(OutputStream os = Files.newOutputStream(Paths.get(filename)))
      {
         exportProject(project, os);
      }
   }

   public void exportProject(OpcProject project, File file) throws IOException
   {
      try(OutputStream os = Files.newOutputStream(file.toPath()))
      {
         exportProject(project, os);
      }
   }

   public void exportProject(OpcProject project, OutputStream stream)
   {
      createClient();
      authenticate();

      ExportRequest exportRequest = new ExportRequest(project);
      long jobId = getExportJobId(project);


      Invocation.Builder builder = getInvocationBuilder("action/jobStatus/"+ jobId);

      int retryCount = 1;
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

   private boolean jobIsComplete(JobStatus status)
   {
      return status != null && "COMPLETED".equals(status.getJobStatus());
   }

   private long getExportJobId(OpcProject project)
   {
      ExportRequest exportRequest = new ExportRequest(project);

      Invocation.Builder builder = getInvocationBuilder("action/exportP6xml");
      return builder.post(Entity.entity(exportRequest, MediaType.APPLICATION_JSON)).readEntity(JobStatus.class).getJobId();
   }

   private List<OpcWorkspace> getWorkspaces()
   {
      Invocation.Builder builder = getInvocationBuilder("workspace");
      return builder.get().readEntity(new GenericType<List<OpcWorkspace>>() {});
   }

   private List<OpcProject> getProjectsInWorkspace(OpcWorkspace workspace)
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

   private void createClient()
   {
      if (m_client != null)
      {
         return;
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      m_client = ClientBuilder.newClient().register(new JacksonJsonProvider(mapper));
      m_client.register(GZipEncoder.class);
      m_client.register(EncodingFilter.class);
      if (m_logger != null)
      {
         m_client.register(m_logger);
      }
   }

   private Invocation.Builder getInvocationBuilder(String path)
   {
      WebTarget target = m_client.target("https://" + m_host).path("api/restapi").path(path);
      Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
      builder.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
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
