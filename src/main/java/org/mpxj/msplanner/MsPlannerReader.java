package org.mpxj.msplanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.ProjectField;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.common.NumberHelper;
import org.mpxj.pwa.PwaException;

public class MsPlannerReader
{

   public static void main(String[] argv)
   {
      MsPlannerReader reader = new MsPlannerReader(argv[0], argv[1]);
      List<MsPlannerProject> projects = reader.getProjects();
      projects.forEach(System.out::println);

      for (MsPlannerProject project : projects)
      {
         ProjectFile file = reader.readProject(project.getProjectId());
      }
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
    * Read a project from PWA using its unique ID.
    *
    * @param id project unique ID
    * @return ProjectFile instance representing the project in PWA
    */
   public ProjectFile readProject(UUID id)
   {
      try
      {
         m_projectID = id;
         m_project = new ProjectFile();
         m_data = readData();
         //         m_resourceMap = new HashMap<>();
         //         m_taskMap = new HashMap<>();
         //         m_customFields = new HashMap<>();
         //         m_lookupEntries = new HashMap<>();
         //
         readProjectProperties();
         //         readCalendars();
         //         readResources();
         //         readTasks();
         //         readTaskLinks();

         return m_project;
      }

      finally
      {
         m_projectID = null;
         m_project = null;
         m_data = null;
         //         m_resourceMap = null;
         //         m_taskMap = null;
         //         m_customFields = null;
         //         m_lookupEntries = null;
      }
   }

   /**
    * Read the bulk of the required project data from Microsoft Planner using a single OData query.
    *
    * @return MapRow instance representing deserialized JSON
    */
   private MapRow readData()
   {
      String query = "msdyn_projects(" + m_projectID + ")"
         + "?$expand=" +
         String.join(",",
            "msdyn_msdyn_project_msdyn_projecttask_project",
            "msdyn_msdyn_project_msdyn_projecttaskdependency_Project",
            "msdyn_msdyn_project_msdyn_resourceassignment_projectid");

      //System.out.println(query);

      HttpURLConnection connection = createConnection(query);

      int code = getResponseCode(connection);

      if (code != 200)
      {
         throw new MsPlannerException(getExceptionMessage(connection, code));
      }

      return getMapRow(connection);
   }

   /**
    * Read project property data.
    */
   private void readProjectProperties()
   {
      ProjectProperties props = m_project.getProjectProperties();
      populateFieldContainer(props, PROJECT_FIELDS, m_data);

      props.setDaysPerMonth(m_data.getInteger("msdyn_dayspermonth"));
      props.setMinutesPerDay(Integer.valueOf((int)(m_data.getDoubleValue("msdyn_hoursperday") * 60)));
      props.setMinutesPerWeek(Integer.valueOf((int)(m_data.getDoubleValue("msdyn_hoursperweek") * 60)));
      props.setMinutesPerMonth(Integer.valueOf(NumberHelper.getInt(props.getDaysPerMonth()) * NumberHelper.getInt(props.getMinutesPerDay())));
      props.setMinutesPerYear(Integer.valueOf(NumberHelper.getInt(props.getMinutesPerMonth() * 12)));
   }

   /**
    * Using an index which maps Microsoft Planner fields to MPXJ fields, populate a field container.
    *
    * @param container target field container
    * @param index index mapping Microsoft Planner fields to MPXJ fields
    * @param data response data use to populate the supplied container
    */
   private void populateFieldContainer(FieldContainer container, Map<String, ? extends FieldType> index, MapRow data)
   {
      data.setProject(m_project);
      for (Map.Entry<String, ? extends FieldType> entry : index.entrySet())
      {
         container.set(entry.getValue(), data.getObject(entry.getKey(), entry.getValue().getDataType()));
      }
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
         URL url = new URL(m_host + "/api/data/v9.2/" + path);
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
   private UUID m_projectID;
   private ProjectFile m_project;
   private MapRow m_data;

   private static final Map<String, ProjectField> PROJECT_FIELDS = new HashMap<>();
   static
   {
      //"@odata.context": "https://example.api.crm11.dynamics.com/api/data/v9.1/$metadata#msdyn_projects(msdyn_msdyn_project_msdyn_projecttask_project(),msdyn_msdyn_project_msdyn_projecttaskdependency_Project(),msdyn_msdyn_project_msdyn_resourceassignment_projectid())/$entity",
      //"@odata.etag": "W/\"8163751\"",
      //"msdyn_tzafinish": "2019-11-04T17:00:00Z",
      PROJECT_FIELDS.put("modifiedon", ProjectField.LAST_SAVED);
      //"_msdyn_replaylogheader_value": null,
      //"_owninguser_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"_msdyn_pfwcreatedby_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"_stageid_value": null,
      PROJECT_FIELDS.put("msdyn_duration", ProjectField.DURATION);
      //"msdyn_scheduler": 192350000,
      //"msdyn_plannerlastsavedrevisiontoken": "msxrm_org7a979f91.crm11.dynamics.com_18702d8b-e2e4-f011-8406-6045bd0ae75a_0000000022",
      //"msdyn_calendarid": "19702D8B-E2E4-F011-8406-6045BD0AE75A",
      //"msdyn_teamschannelmappingbackfilled": true,
      //"msdyn_schedulemode": 192350001,
      PROJECT_FIELDS.put("msdyn_effortcompleted", ProjectField.ACTUAL_WORK);
      //"overriddencreatedon": null,
      //"msdyn_copyprojectcorrelationid": null,
      //"msdyn_effortremaining": 80.0000000000,
      //"_msdyn_projectteamid_value": null,
      //"msdyn_hoursperweek": 40.0000000000,
      //"msdyn_scheduledstart": "2019-10-15T08:00:00Z",
      //"importsequencenumber": null,
      //"msdyn_businesscase": null,
      //"msdyn_progress": 0.0000000000,
      //"msdyn_valuestatement": null,
      PROJECT_FIELDS.put("msdyn_effort", ProjectField.WORK);
      //"_modifiedonbehalfby_value": null,
      //"msdyn_dayspermonth": 20,
      PROJECT_FIELDS.put("msdyn_taskearlieststart", ProjectField.START_DATE);
      //"statecode": 0,
      //"_msdyn_pfwmodifiedby_value": null,
      //"msdyn_copyprojectsessionid": null,
      //"_msdyn_contractorganizationalunitid_value": "b77518de-3cff-ee11-9f8a-000d3a86b5a3",
      //"msdyn_bulkgenerationstatus": null,
      //"versionnumber": 8163751,
      //"utcconversiontimezonecode": null,
      PROJECT_FIELDS.put("msdyn_subject", ProjectField.SUBJECT);
      //"processid": null,
      //"msdyn_hoursperday": 8.0000000000,
      //"_createdonbehalfby_value": null,
      //"_msdyn_msprojectdocument_value": "40e2eeae-e2e4-f011-8406-002248c7142a",
      //"msdyn_tzascheduledstart": "2019-10-15T08:00:00Z",
      //"_msdyn_projectimportstagingid_value": null,
      PROJECT_FIELDS.put("msdyn_projectid", ProjectField.GUID);
      //"_modifiedby_value": "ee4563e5-33ff-ee11-9f8a-000d3a86b5a3",
      PROJECT_FIELDS.put("createdon", ProjectField.CREATION_DATE);
      //"_msdyn_projectmanager_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"_owningbusinessunit_value": "a3cb50c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"msdyn_globalrevisiontoken": "msxrm_org7a979f91.crm11.dynamics.com_18702d8b-e2e4-f011-8406-6045bd0ae75a_0000000022",
      //"traversedpath": null,
      //"msdyn_description": null,
      //"msdyn_comments": null,
      //"statuscode": 1,
      //"_msdyn_workhourtemplate_value": "be7518de-3cff-ee11-9f8a-000d3a86b5a3",
      //"_msdyn_program_value": null,
      PROJECT_FIELDS.put("msdyn_finish", ProjectField.FINISH_DATE);
      //"_createdby_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"_owningteam_value": null,
      //"msdyn_disablecreateofteammemberformanager": false,
      //"_ownerid_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"timezoneruleversionnumber": 4,
   }
}