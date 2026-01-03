package org.mpxj.msplanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
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
import org.mpxj.Duration;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.HtmlNotes;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectField;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceField;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TimeUnit;
import org.mpxj.common.HierarchyHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.explorer.ProjectExplorer;

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
         ProjectExplorer.view(file);
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
         m_calendarIndex = 1;
         m_calendarMap = new HashMap<>();
         m_resourceMap = new HashMap<>();
         m_taskMap = new HashMap<>();

         readProjectProperties();
         readTasks();
         readDependencies();
         readResourceAssignments();

         return m_project;
      }

      finally
      {
         m_projectID = null;
         m_project = null;
         m_data = null;
         m_calendarMap = null;
         m_resourceMap = null;
         m_resourceDataMap = null;
         m_taskMap = null;
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

      props.setDefaultCalendar(getCalendar(m_data.getUUID("msdyn_calendarid")));
      props.setDaysPerMonth(m_data.getInteger("msdyn_dayspermonth"));
      props.setMinutesPerDay(Integer.valueOf((int) (m_data.getDoubleValue("msdyn_hoursperday") * 60)));
      props.setMinutesPerWeek(Integer.valueOf((int) (m_data.getDoubleValue("msdyn_hoursperweek") * 60)));
      props.setMinutesPerMonth(Integer.valueOf(NumberHelper.getInt(props.getDaysPerMonth()) * NumberHelper.getInt(props.getMinutesPerDay())));
      props.setMinutesPerYear(Integer.valueOf(NumberHelper.getInt(props.getMinutesPerMonth() * 12)));
   }

   private void readTasks()
   {
      HierarchyHelper.sortHierarchy(
         m_data.getList("msdyn_msdyn_project_msdyn_projecttask_project"),
         t -> t.getUUID("msdyn_projecttaskid"),
         t -> t.getUUID("_msdyn_parenttask_value"),
         Comparator.comparing(o -> o.getDouble("msdyn_displaysequence"))
      ).forEach(this::readTask);
   }

   private void readTask(MapRow data)
   {
      UUID parentID = data.getUUID("_msdyn_parenttask_value");
      Task parentTask = m_taskMap.get(parentID);
      Task task = (parentTask == null ? m_project : parentTask).addTask();

      populateFieldContainer(task, TASK_FIELDS, data);

      addNotes(task, data);
      // TODO: priority
      // TODO: msdyn_ismanual
      m_taskMap.put(task.getGUID(), task);
   }

   private void readDependencies()
   {
      m_data.getList("msdyn_msdyn_project_msdyn_projecttaskdependency_Project").forEach(this::readDependency);
   }

   private void readDependency(MapRow data)
   {
      Task predecessorTask = m_taskMap.get(data.getUUID("_msdyn_predecessortask_value"));
      Task successorTask = m_taskMap.get(data.getUUID("_msdyn_successortask_value"));
      if (predecessorTask == null || successorTask == null)
      {
         return;
      }

      RelationType type = data.getRelationType("msdyn_projecttaskdependencylinktype");
      Duration lag = Duration.getInstance(data.getInt("msdyn_projecttaskdependencylinklag") / (60.0 * 60.0), TimeUnit.HOURS);
      successorTask.addPredecessor(new Relation.Builder().predecessorTask(predecessorTask).type(type).lag(lag));

      //"@odata.etag": "W/\"8162366\"",
      //"_msdyn_predecessortask_value": "a7c97a92-e2e4-f011-89f4-6045bd0b8013",
      //"msdyn_projecttaskdependencylinktype": 1,
      //"modifiedon": "2025-12-29T18:17:50Z",
      //"_owninguser_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"overriddencreatedon": null,
      //"_msdyn_successortask_value": "a8c97a92-e2e4-f011-89f4-6045bd0b8013",
      //"importsequencenumber": null,
      //"_modifiedonbehalfby_value": null,
      //"msdyn_projecttaskdependencylinklaginseconds": null,
      //"statecode": 0,
      //"versionnumber": 8162366,
      //"utcconversiontimezonecode": null,
      //"_createdonbehalfby_value": null,
      //"_modifiedby_value": "ee4563e5-33ff-ee11-9f8a-000d3a86b5a3",
      //"createdon": "2025-12-29T18:17:50Z",
      //"_owningbusinessunit_value": "a3cb50c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"msdyn_description": null,
      //"msdyn_projecttaskdependencylinklag": 0,
      //"statuscode": 1,
      //"_msdyn_project_value": "18702d8b-e2e4-f011-8406-6045bd0ae75a",
      //"_ownerid_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"_owningteam_value": null,
      //"_createdby_value": "ee4563e5-33ff-ee11-9f8a-000d3a86b5a3",
      //"timezoneruleversionnumber": null,
      //"msdyn_projecttaskdependencyid": "aac97a92-e2e4-f011-89f4-6045bd0b8013"
   }

   private void readResourceAssignments()
   {
      m_data.getList("msdyn_msdyn_project_msdyn_resourceassignment_projectid").forEach(this::readResourceAssignment);
   }

   private void readResourceAssignment(MapRow data)
   {
      Task task = m_taskMap.get(data.getUUID("_msdyn_taskid_value"));
      if (task == null)
      {
         return;
      }

      Resource resource = getResource(data.getUUID("_msdyn_bookableresourceid_value"));
      if (resource == null)
      {
         return;
      }
   }

   private void addNotes(Task task, MapRow data)
   {
      String html = data.getString("msdyn_description")  ;
      if (html == null || html.isEmpty())
      {
         return;
      }

      task.setNotesObject(new HtmlNotes(html));
   }

   private Resource getResource(UUID id)
   {
      Resource resource = m_resourceMap.get(id);
      if (resource != null)
      {
         return resource;
      }

      if (m_resourceDataMap == null)
      {
         loadResourceData();
      }

      MapRow data = m_resourceDataMap.get(id);
      if (data == null)
      {
         return null;
      }

      resource = m_project.addResource();
      populateFieldContainer(resource, RESOURCE_FIELDS, data);
      resource.setCalendar(getCalendar(data.getUUID("_calendarid_value")));
      return resource;
   }

   private void loadResourceData()
   {
      HttpURLConnection connection = createConnection("bookableresources");

      int code = getResponseCode(connection);

      if (code != 200)
      {
         throw new MsPlannerException(getExceptionMessage(connection, code));
      }

      m_resourceDataMap = getMapRow(connection)
         .getList("value")
         .stream()
         .collect(Collectors.toMap(v -> v.getUUID("bookableresourceid"), v -> v));
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

   private ProjectCalendar getCalendar(UUID id)
   {
      if (id == null)
      {
         return null;
      }

      ProjectCalendar calendar = m_calendarMap.get(id);
      if (calendar != null)
      {
         return calendar;
      }

      HttpURLConnection connection = createConnection("calendars(" + id + ")?$expand=calendar_calendar_rules");
      int code = getResponseCode(connection);

      if (code != 200)
      {
         throw new MsPlannerException(getExceptionMessage(connection, code));
      }

      MapRow data = getMapRow(connection);
      String name = data.getString("name");
      if (name == null || name.isEmpty())
      {
         name = data.getString("description");
         if (name == null)
         {
            name = "Unititled " + (m_calendarIndex++);
         }
      }

      calendar = m_project.addCalendar();
      calendar.setName(name);
      calendar.setGUID(id);

      final ProjectCalendar finalCalendar = calendar;
      data.getList("calendar_calendar_rules").forEach(r -> readCalendarRules(finalCalendar, r));

      ProjectCalendar innerCalendar = getCalendar(data.getUUID("_innercalendarid_value"));
      if (innerCalendar != null)
      {
         throw new RuntimeException("inner calendar has value");
      }

      m_calendarMap.put(id, calendar);

      return calendar;
   }

   private void readCalendarRules(ProjectCalendar calendar, MapRow data)
   {
      Map<String, String> pattern = getMapFromPattern(data.getString("pattern"));

      if (data.getDate("starttime") == null && data.getDate("endtime") == null)
      {
         // Default working hours
         if ("WEEKLY".equals(pattern.get("FREQ")) && "1".equals(pattern.get("INTERVAL")))
         {
            addWorkingDaysWithDefaultHours(calendar, pattern.get("BYDAY"));
         }
         else
         {
            // TODO - handle exceptions
            throw new MsPlannerException("Unknown calendar pattern: " + data.getString("pattern"));
         }
      }
      else
      {
         if ("DAILY".equals(pattern.get("FREQ"))
            && "1".equals(pattern.get("INTERVAL"))
            && "1".equals(pattern.get("COUNT")))
         {
            // simple single day exception
            LocalDate exceptionDate = data.getLocalDate("starttime");
            
            throw new UnsupportedOperationException();
         }
         else
         {
            throw new UnsupportedOperationException();
         }
      }
   }

   private void addWorkingDaysWithDefaultHours(ProjectCalendar calendar, String days)
   {
      for (String dayName : days.split(","))
      {
         DayOfWeek dayOfWeek = CALENDAR_DAYS.get(dayName);
         if (dayOfWeek == null)
         {
            throw new MsPlannerException("Unknown day " + dayName);
         }

         calendar.addCalendarHours(dayOfWeek).add(DEFAULT_HOURS);
      }
   }

   private Map<String, String> getMapFromPattern(String pattern)
   {
      return Arrays.stream(pattern.split(";"))
         .map(v -> v.split("="))
         .collect(Collectors.toMap(k -> k[0], v -> v[1]));
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
   private int m_calendarIndex;
   private Map<UUID, ProjectCalendar> m_calendarMap;
   private Map<UUID, MapRow> m_resourceDataMap;
   private Map<UUID, Resource> m_resourceMap;
   private Map<UUID, Task> m_taskMap;

   private static final LocalTimeRange DEFAULT_HOURS = new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));

   private static final Map<String, DayOfWeek> CALENDAR_DAYS = new HashMap<>();
   static
   {
      CALENDAR_DAYS.put("MO", DayOfWeek.MONDAY);
      CALENDAR_DAYS.put("TU", DayOfWeek.TUESDAY);
      CALENDAR_DAYS.put("WE", DayOfWeek.WEDNESDAY);
      CALENDAR_DAYS.put("TH", DayOfWeek.THURSDAY);
      CALENDAR_DAYS.put("FR", DayOfWeek.FRIDAY);
      CALENDAR_DAYS.put("SA", DayOfWeek.SATURDAY);
      CALENDAR_DAYS.put("SU", DayOfWeek.SUNDAY);
   }

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
      PROJECT_FIELDS.put("msdyn_progress", ProjectField.PERCENTAGE_COMPLETE);
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

   private static final Map<String, ResourceField> RESOURCE_FIELDS = new HashMap<>();
   static
   {
      //"@odata.etag": "W/\"1834801\"",
      //"msdyn_generictype": null,
      //"_calendarid_value": "de52412c-17b6-4d84-b4f4-15cb995c02e0",
      //"msdyn_endlocation": 690970002,
      //"modifiedon": "2024-04-20T17:39:08Z",
      //"_owninguser_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"msdyn_pooltype": null,
      //"_transactioncurrencyid_value": null,
      //"overriddencreatedon": null,
      //"stageid": null,
      //"_msdyn_organizationalunit_value": null,
      //"timezone": 0,
      //"msdyn_enableappointments": 192350001,
      //"importsequencenumber": null,
      //"_modifiedonbehalfby_value": null,
      //"msdyn_startlocation": 690970002,
      //"msdyn_displayonscheduleboard": true,
      //"exchangerate": null,
      //"statecode": 0,
      //"msdyn_displayonscheduleassistant": true,
      //"msdyn_derivecapacity": false,
      RESOURCE_FIELDS.put("name", ResourceField.NAME);
      //"msdyn_isgenericresourceprojectscoped": true,
      //"versionnumber": 1834801,
      //"utcconversiontimezonecode": null,
      //"processid": null,
      //"_createdonbehalfby_value": null,
      //"_modifiedby_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      RESOURCE_FIELDS.put("createdon", ResourceField.CREATED);
      //"resourcetype": 1,
      //"_owningbusinessunit_value": "a3cb50c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"traversedpath": null,
      //"msdyn_targetutilization": null,
      //"_accountid_value": null,
      //"msdyn_enableoutlookschedules": 192350001,
      //"msdyn_optimalcrewsize": null,
      //"_userid_value": null,
      //"_contactid_value": null,
      //"statuscode": 1,
      RESOURCE_FIELDS.put("bookableresourceid", ResourceField.GUID);
      //"_createdby_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      RESOURCE_FIELDS.put("msdyn_primaryemail", ResourceField.EMAIL_ADDRESS);
      //"_owningteam_value": null,
      //"_ownerid_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"timezoneruleversionnumber": null
   }

   private static final Map<String, TaskField> TASK_FIELDS = new HashMap<>();
   static
   {
      //"@odata.etag": "W/\"8163420\"",
      //"_msdyn_projectsprint_value": null,
      TASK_FIELDS.put("msdyn_duration",  TaskField.DURATION);
      TASK_FIELDS.put("msdyn_finish",  TaskField.FINISH);
      //"_msdyn_resourceorganizationalunitid_value": null,
      //"statuscode": 1,
      //"_createdby_value": "ee4563e5-33ff-ee11-9f8a-000d3a86b5a3",
      //"_owninguser_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"msdyn_tzascheduledend": "2019-10-21T17:00:00Z",
      //"_msdyn_projectbucket_value": "afc97a92-e2e4-f011-89f4-6045bd0b8013",
      //"_modifiedby_value": "ee4563e5-33ff-ee11-9f8a-000d3a86b5a3",
      //"msdyn_descriptionplaintext": null,
      //"msdyn_tzascheduledstart": "2019-10-15T09:00:00Z",
      //"msdyn_scheduledstart": "2019-10-15T09:00:00Z",
      //"msdyn_priority": 5,
      //"msdyn_description": null,
      TASK_FIELDS.put("msdyn_progress",  TaskField.PERCENT_COMPLETE);
      //"_modifiedonbehalfby_value": null,
      //"_ownerid_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"processid": null,
      TASK_FIELDS.put("msdyn_projecttaskid",  TaskField.GUID);
      //"importsequencenumber": null,
      //"modifiedon": "2025-12-31T14:12:39Z",
      //"utcconversiontimezonecode": null,
      TASK_FIELDS.put("msdyn_effort",  TaskField.WORK);
      //"traversedpath": null,
      //"_createdonbehalfby_value": null,
      //"msdyn_displaysequence": 2.0000000000,
      //"_msdyn_resourcecategory_value": null,
      //"_msdyn_pfwcreatedby_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"_owningteam_value": null,
      //"_owningbusinessunit_value": "a3cb50c4-9dfe-ee11-9f8a-000d3a875b5f",
      TASK_FIELDS.put("msdyn_ismilestone",  TaskField.MILESTONE);
      //"msdyn_scheduledend": "2019-10-21T17:00:00Z",
      //"statecode": 0,
      TASK_FIELDS.put("msdyn_effortremaining",  TaskField.REMAINING_WORK);
      TASK_FIELDS.put("msdyn_subject",  TaskField.NAME);
      //"msdyn_summary": false,
      TASK_FIELDS.put("msdyn_start",  TaskField.START);
      //"timezoneruleversionnumber": 4,
      //"overriddencreatedon": null,
      TASK_FIELDS.put("msdyn_effortcompleted",  TaskField.ACTUAL_WORK);
      //"_msdyn_project_value": "18702d8b-e2e4-f011-8406-6045bd0ae75a",
      //"_stageid_value": null,
      TASK_FIELDS.put("msdyn_iscritical",  TaskField.CRITICAL);
      //"_msdyn_parenttask_value": null,
      //"_msdyn_pfwmodifiedby_value": "96d250c4-9dfe-ee11-9f8a-000d3a875b5f",
      //"msdyn_ismanual": false,
      //"createdon": "2025-12-29T18:17:48Z",
      TASK_FIELDS.put("createdon",  TaskField.CREATED);
      //"versionnumber": 8163420,
      TASK_FIELDS.put("msdyn_outlinelevel",  TaskField.OUTLINE_LEVEL);
   }
}