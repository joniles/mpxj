package org.mpxj.opc;

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
   public static void main(String[] argv)
   {
      Logger log = Logger.getLogger("OPCReader");
      log.setLevel(Level.INFO);
      ConsoleHandler handler = new ConsoleHandler();
      handler.setFormatter(new SimpleFormatter());
      log.addHandler(handler);

      OpcReader reader = new OpcReader(argv[0], argv[1], argv[2]);
      reader.setLogger(log, Level.INFO);
      List<OpcProject> projects = reader.getProjects();
      System.out.println("done");
   }

   public void setLogger(Logger logger, Level level)
   {
      m_logger = new LoggingFeature(logger, level, null, null);
   }

   public OpcReader(String host, String user, String password)
   {
      m_host = host;
      m_user = user;
      m_password = password;
   }

   public List<OpcProject> getProjects()
   {
      authenticate();
      return getWorkspaces().stream().flatMap(w -> getProjectsInWorkspace(w).stream()).collect(Collectors.toList());
   }

   private List<OpcWorkspace> getWorkspaces()
   {
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Client client = ClientBuilder.newClient().register(new JacksonJsonProvider(mapper));
      client.register(GZipEncoder.class);
      client.register(EncodingFilter.class);
      if (m_logger != null)
      {
         client.register(m_logger);
      }

      WebTarget target = client.target("https://" + m_host).path("api/restapi").path("workspace");

      Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
      builder.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
      builder.header("Version", "3");
      builder.header("Authorization", "Bearer " + m_tokenResponse.getAccessToken());
      m_tokenResponse.getRequestHeaders().forEach(builder::header);

      return builder.get().readEntity(new GenericType<List<OpcWorkspace>>() {});
   }

   private List<OpcProject> getProjectsInWorkspace(OpcWorkspace workspace)
   {
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Client client = ClientBuilder.newClient().register(new JacksonJsonProvider(mapper));
      client.register(GZipEncoder.class);
      client.register(EncodingFilter.class);
      if (m_logger != null)
      {
         client.register(m_logger);
      }

      WebTarget target = client.target("https://" + m_host).path("api/restapi").path("project/workspace/" + workspace.getWorkspaceId());

      Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
      builder.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
      builder.header("Version", "3");
      builder.header("Authorization", "Bearer " + m_tokenResponse.getAccessToken());
      m_tokenResponse.getRequestHeaders().forEach(builder::header);

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
         ObjectMapper mapper = new ObjectMapper();
         mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         Client client = ClientBuilder.newClient().register(new JacksonJsonProvider(mapper));
         if (m_logger != null)
         {
            client.register(m_logger);
         }

         client.register(HttpAuthenticationFeature.basic(m_user, m_password));
         WebTarget webTarget = client.target("https://" + m_host)
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


   private final String m_host;
   private final String m_user;
   private final String m_password;
   private LoggingFeature m_logger;
   private TokenResponse m_tokenResponse = TokenResponse.DEFAULT_TOKEN;
}
