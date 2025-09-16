/*
 * file:       PwaReader.java
 * author:     Jon Iles
 * date:       2025-08-19
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

package org.mpxj.pwa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
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
import org.mpxj.AssignmentField;
import org.mpxj.CustomFieldValueDataType;
import org.mpxj.Duration;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectField;
import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceField;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TimeUnit;
import org.mpxj.UserDefinedField;
import org.mpxj.common.FieldTypeHelper;

/**
 * Access schedule data in Microsoft Project Server / Project Web App (PWA) / Project Online.
 */
public class PwaReader
{
   /**
    * Constructor.
    *
    * @param host host URL, expected to be in the form https://example.sharepoint.com/sites/pwa
    * @param token access token
    */
   public PwaReader(String host, String token)
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

   /**
    * Retrieve a list of projects available in PWA for the current user.
    *
    * @return list of PwaProject instances
    */
   public List<PwaProject> getProjects()
   {
      HttpURLConnection connection = createConnection("ProjectServer/Projects?$select=Id,Name");
      int code = getResponseCode(connection);

      if (code != 200)
      {
         throw new PwaException(getExceptionMessage(connection, code));
      }

      MapRow data = getMapRow(connection);

      return data.getList("value").stream().map(d -> new PwaProject(d.getUUID("Id"), d.getString("Name"))).collect(Collectors.toList());
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
         m_resourceMap = new HashMap<>();
         m_taskMap = new HashMap<>();
         m_customFields = new HashMap<>();
         m_lookupEntries = new HashMap<>();

         readProjectProperties();
         readCalendars();
         readResources();
         readTasks();
         readTaskLinks();

         return m_project;
      }

      finally
      {
         m_projectID = null;
         m_project = null;
         m_data = null;
         m_resourceMap = null;
         m_taskMap = null;
         m_customFields = null;
         m_lookupEntries = null;
      }
   }

   /**
    * Read the bulk of the required project data from PWA using a single OData query.
    *
    * @return MapRow instance representing deserialized JSON
    */
   private MapRow readData()
   {
      String query = "ProjectServer/Projects(guid'" + m_projectID + "')"
               + "?$expand=" +
               String.join(",",
                  "ProjectResources",
                  "ProjectResources/CustomFields",
                  "ProjectResources/CustomFields/LookupEntries",
                  "Tasks",
                  "TaskLinks",
                  "Tasks/Parent",
                  "Tasks/CustomFields",
                  "Tasks/CustomFields/LookupEntries",
                  "Tasks/Assignments",
                  "Tasks/Assignments/Resource")
               + "&$select=" +
               String.join(",",
                  "*",
                  "ProjectResources/*",
                  "ProjectResources/CustomFields/Id",
                  "ProjectResources/CustomFields/Name",
                  "ProjectResources/CustomFields/InternalName",
                  "ProjectResources/CustomFields/FieldType",
                  "ProjectResources/CustomFields/LookupEntries/InternalName",
                  "ProjectResources/CustomFields/LookupEntries/Value",
                  "Tasks/*",
                  "Tasks/Parent/Id",
                  "Tasks/Assignments/*",
                  "Tasks/CustomFields/Id",
                  "Tasks/CustomFields/Name",
                  "Tasks/CustomFields/InternalName",
                  "Tasks/CustomFields/FieldType",
                  "Tasks/CustomFields/LookupEntries/InternalName",
                  "Tasks/CustomFields/LookupEntries/Value",
                  "Tasks/Assignments/Resource/Id");

      //System.out.println(query);

      HttpURLConnection connection = createConnection(query);

      int code = getResponseCode(connection);

      if (code != 200)
      {
         throw new PwaException(getExceptionMessage(connection, code));
      }

      return getMapRow(connection);
   }

   /**
    * Read project property data. Note that we're using data from both
    * ProjectServer/Projects and ProjectData/Projects endpoints to
    * capture the maximum detail possible.
    */
   private void readProjectProperties()
   {
      HttpURLConnection connection = createConnection("ProjectData/Projects(guid'" + m_projectID + "')");
      int code = getResponseCode(connection);

      if (code != 200)
      {
         throw new PwaException(getExceptionMessage(connection, code));
      }

      populateFieldContainer(m_project.getProjectProperties(), PROJECT_DATA_PROJECT_FIELDS, getMapRow(connection));
      populateFieldContainer(m_project.getProjectProperties(), PROJECT_SERVER_PROJECT_FIELDS, m_data);
   }

   /**
    * Read calendars, or at least as much data as we can.
    *
    * Issues with PWA:
    * 1. WorkWeeks are not available, although allegedly there is an endpoint (in some versions of PWA?)
    * 2. Resource calendars are not available
    */
   private void readCalendars()
   {
      HttpURLConnection connection = createConnection("ProjectServer/Calendars?$expand=BaseCalendarExceptions");
      int code = getResponseCode(connection);

      if (code != 200)
      {
         throw new PwaException(getExceptionMessage(connection, code));
      }

      getMapRow(connection).getList("value").forEach(this::readCalendar);
   }

   /**
    * Create a calendar from PWA data.
    *
    * @param data calendar data
    */
   private void readCalendar(MapRow data)
   {
      ProjectCalendar calendar = m_project.addDefaultBaseCalendar();
      //"odata.type": "PS.Calendar",
      //"odata.id": "https://example.sharepoint.com/sites/pwa/_api/ProjectServer/Calendars('9410ae84-5878-f011-97be-080027fff3b7')",
      //"odata.editLink": "ProjectServer/Calendars('9410ae84-5878-f011-97be-080027fff3b7')",
      //"Created": "2025-08-13T15:18:27.837",
      calendar.setGUID(data.getUUID("Id"));
      //"IsStandardCalendar": false,
      //"Modified": "2025-08-13T15:18:27.837",
      calendar.setName(data.getString("Name"));

      data.getList("BaseCalendarExceptions").forEach(item -> readCalendarException(calendar, item));

      if (data.getBool("IsStandardCalendar"))
      {
         m_project.setDefaultCalendar(calendar);
      }
   }

   /**
    * Add an exception to a calendar.
    *
    * @param calendar parent calendar
    * @param data exception data
    */
   private void readCalendarException(ProjectCalendar calendar, MapRow data)
   {
      ProjectCalendarException exception = calendar.addCalendarException(data.getLocalDate("Start"), data.getLocalDate("Finish"));
      //"odata.type": "PS.BaseCalendarException",
      //"odata.id": "https://example.sharepoint.com/sites/pwa/_api/ProjectServer/Calendars('b6635b2e-e747-4771-a78b-24f7509629d0')/BaseCalendarExceptions(0)",
      //"odata.editLink": "ProjectServer/Calendars('b6635b2e-e747-4771-a78b-24f7509629d0')/BaseCalendarExceptions(0)",
      //"Start": "2025-08-18T00:00:00"
      //"Finish": "2025-08-18T00:00:00",
      //"Id": 0,
      exception.setName(data.getString("Name"));
      addRange(exception, data, 1);
      addRange(exception, data, 2);
      addRange(exception, data, 3);
      addRange(exception, data, 4);
      addRange(exception, data, 5);

      // TODO: consider implementing support for recurring exceptions
      //"RecurrenceDays": 0,
      //"RecurrenceFrequency": 1,
      //"RecurrenceMonth": 0,
      //"RecurrenceMonthDay": 0,
      //"RecurrenceType": 0,
      //"RecurrenceWeek": 0,
   }

   /**
    * Add a time range to a calendar exception.
    *
    * @param exception parent calendar exception
    * @param data exception data
    * @param index shift number
    */
   private void addRange(ProjectCalendarException exception, MapRow data, int index)
   {
      String shift = "Shift" + index;
      int start = data.getInt(shift + "Start");
      int finish = data.getInt(shift + "Finish");
      if (start == finish)
      {
         return;
      }

      exception.add(new LocalTimeRange(LocalTime.MIDNIGHT.plusMinutes(start), LocalTime.MIDNIGHT.plusMinutes(finish)));
   }

   /**
    * Read resources for the current project.
    */
   private void readResources()
   {
      m_data.getList("ProjectResources").forEach(this::readResource);
   }

   /**
    * Read a single resource.
    *
    * @param data resource data
    */
   private void readResource(MapRow data)
   {
      Resource resource = m_project.addResource();
      populateFieldContainer(resource, RESOURCE_FIELDS, data);
      readCustomFields(data, resource, FieldTypeClass.RESOURCE);
      m_resourceMap.put(resource.getGUID(), resource);
   }

   /**
    * Read tasks for the current project.
    */
   private void readTasks()
   {
      // At the moment we're assuming that the tasks arrive in the correct order for the hierarchy.
      m_data.getList("Tasks").forEach(this::readTask);
   }

   /**
    * Read an individual task.
    *
    * @param data task data
    */
   private void readTask(MapRow data)
   {
      Task parentTask = m_taskMap.get(getParentID(data));
      Task task = (parentTask == null ? m_project : parentTask).addTask();
      populateFieldContainer(task, TASK_FIELDS, data);
      readCustomFields(data, task, FieldTypeClass.TASK);
      readResourceAssignments(data, task);
      m_taskMap.put(task.getGUID(), task);
   }

   /**
    * Retrieve the parent task unique ID, or null if the task has no parent.
    *
    * @param data task data
    * @return parent task unique ID
    */
   private UUID getParentID(MapRow data)
   {
      MapRow parent = data.getMapRow("Parent");
      if (parent == null)
      {
         return null;
      }
      return parent.getUUID("Id");
   }

   /**
    * Read project and enterprise custom fields.
    *
    * @param data entity data
    * @param container target container
    * @param fieldTypeClass target container type
    */
   private void readCustomFields(MapRow data, FieldContainer container, FieldTypeClass fieldTypeClass)
   {
      data.keySet().stream().filter(key -> key.startsWith("LocalCustom")).forEach(key -> readLocalCustomField(data, key, container));
      data.keySet().stream().filter(key -> key.startsWith("Custom_")).forEach(key -> readEnterpriseCustomField(data, key, container, fieldTypeClass));
   }

   /**
    * Read a project custom field.
    *
    * @param data entity data
    * @param internalName custom field internal name
    * @param container target container
    */
   private void readLocalCustomField(MapRow data, String internalName, FieldContainer container)
   {
      Object value = data.get(internalName);
      if (value == null)
      {
         return;
      }

      FieldType type = m_customFields.get(internalName);
      if (type == null)
      {
         type = getFieldTypeFromIdentifier(internalName);
         if (type == null)
         {
            return;
         }

         m_customFields.put(internalName, type);

         final FieldType t = type;
         MapRow customField = data.getList("CustomFields").stream().filter(f -> t.equals(getFieldTypeFromIdentifier(f.getString("Id")))).findFirst().orElse(null);
         if (customField != null)
         {
            m_project.getCustomFields().getOrCreate(type).setAlias(customField.getString("Name"));

            // Currently we're assuming that lookup entries have a globally unique identifier
            customField.getList("LookupEntries").forEach(e -> m_lookupEntries.put(e.getString("InternalName"), e.getObject("Value", t.getDataType())));
         }
      }

      if (value instanceof List)
      {
         // Note: we don't currently support multi-select values,
         // so we just use the first value in the list
         @SuppressWarnings("unchecked")
         List<String> list = (List<String>) value;
         value = list.isEmpty() ? null : m_lookupEntries.get(list.get(0));
      }
      else
      {
         value = data.getObject(internalName, type.getDataType());
      }

      container.set(type, value);
   }

   /**
    * Read an enterprise custom field.
    *
    * @param data entity data
    * @param internalName custom field internal name
    * @param container target container
    * @param fieldTypeClass target container type
    */
   private void readEnterpriseCustomField(MapRow data, String internalName, FieldContainer container, FieldTypeClass fieldTypeClass)
   {
      Object value = data.get(internalName);
      if (value == null)
      {
         return;
      }

      FieldType type = m_customFields.get(internalName);
      if (type == null)
      {
         // substring used to remove Custom_ prefix
         MapRow customField = data.getList("CustomFields").stream().filter(f -> internalName.endsWith(f.getString("InternalName").substring(7))).findFirst().orElse(null);
         if (customField == null)
         {
            return;
         }

         UserDefinedField field = new UserDefinedField.Builder(m_project)
            .fieldTypeClass(fieldTypeClass)
            .dataType(CustomFieldValueDataType.getInstance(customField.getInt("FieldType")).getDataType())
            .internalName(customField.getString("InternalName"))
            .externalName(customField.getString("Name"))
            .build();
         type = field;

         m_project.getUserDefinedFields().add(field);
         m_project.getCustomFields().add(field).setAlias(field.getName());

         m_customFields.put(internalName, type);
         customField.getList("LookupEntries").forEach(e -> m_lookupEntries.put(e.getString("InternalName"), e.getObject("Value", field.getDataType())));
      }

      if (value instanceof List)
      {
         // Note: we don't currently support multi-select values,
         // so we just use the first value in the list
         @SuppressWarnings("unchecked")
         List<String> list = (List<String>) value;
         value = list.isEmpty() ? null : m_lookupEntries.get(list.get(0));
      }
      else
      {
         value = data.getObject(internalName, type.getDataType());
      }

      container.set(type, value);
   }

   /**
    * For a project (local) custom field, use the last 8 hex digits of the internal name
    * to determine the field type.
    *
    * @param internalName custom field internal name
    * @return FieldType instance
    */
   private FieldType getFieldTypeFromIdentifier(String internalName)
   {
      // LocalCustom_x005f_Published_x005f_47bd06f02703ef11ba8c00155d805832_x005f_000039b78bbe4ceb82c4fa8c0b400033
      String fieldID = internalName.substring(internalName.length() - 8);
      return FieldTypeHelper.getInstance(m_project, Integer.parseInt(fieldID, 16));
   }

   /**
    * Read a task's resource assignments.
    *
    * @param data resource assignment data
    * @param task parent task
    */
   private void readResourceAssignments(MapRow data, Task task)
   {
      data.getList("Assignments").forEach(d -> readResourceAssignment(d, task));
   }

   /**
    * Read a resource assignment.
    *
    * @param data resource assignment data
    * @param task parent task
    */
   private void readResourceAssignment(MapRow data, Task task)
   {
      MapRow resourceData = data.getMapRow("Resource");
      if (resourceData == null)
      {
         return;
      }

      Resource resource = m_resourceMap.get(resourceData.getUUID("Id"));
      if (resource == null)
      {
         return;
      }

      ResourceAssignment assignment = task.addResourceAssignment(resource);

      populateFieldContainer(assignment, ASSIGNMENT_FIELDS, data);
   }

   /**
    * Read the predecessor relationships between tasks in a project.
    */
   private void readTaskLinks()
   {
      m_data.getList("TaskLinks").forEach(this::readTaskLink);
   }

   /**
    * Read an individua predecessor relationship from a project.
    *
    * @param data predecessor relationship data
    */
   private void readTaskLink(MapRow data)
   {
      Task predecessor = m_taskMap.get(data.getUUID("PredecessorTaskId"));
      Task successor = m_taskMap.get(data.getUUID("SuccessorTaskId"));
      if (predecessor == null || successor == null)
      {
         return;
      }

      RelationType type = RelationType.getInstance(data.getInt("DependencyType"));
      double lag = data.getInt("LinkLag") / 600.0;

      successor.addPredecessor(new Relation.Builder()
         .lag(Duration.getInstance(lag, TimeUnit.HOURS))
         .type(type)
         .predecessorTask(predecessor));
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
         URL url = new URL(m_host + "/_api/" + path);
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
         throw new PwaException(ex);
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
         throw new PwaException(ex);
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
    * Deserializes a PWA response into a MapRow instance.
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
         throw new PwaException(ex);
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
         throw new PwaException(ex);
      }
   }

   /**
    * Using an index which maps PWA fields to MPXJ fields, populate a field container.
    *
    * @param container target field container
    * @param index index mapping PWA fields to MPXJ fields
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

   private final String m_host;
   private final String m_token;
   private final ObjectMapper m_mapper;
   private UUID m_projectID;
   private ProjectFile m_project;
   private MapRow m_data;
   private Map<UUID, Task> m_taskMap;
   private Map<UUID, Resource> m_resourceMap;
   private Map<String, FieldType> m_customFields;
   private Map<String, Object> m_lookupEntries;

   private static final Map<String, ProjectField> PROJECT_DATA_PROJECT_FIELDS = new HashMap<>();
   static
   {
      //PROJECT_DATA_PROJECT_FIELDS.put("odata.metadata", "https://example.sharepoint.com/sites/pwa/_api/ProjectData/$metadata#Projects/@Element");
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectId", ProjectField.GUID);
      //PROJECT_DATA_PROJECT_FIELDS.put("EnterpriseProjectTypeDescription", "For when you want more control over the project. You will have to approve all updates.");
      //PROJECT_DATA_PROJECT_FIELDS.put("EnterpriseProjectTypeId", "09fa52b4-059b-4527-926e-99f9be96437a");
      //PROJECT_DATA_PROJECT_FIELDS.put("EnterpriseProjectTypeIsDefault", true);
      //PROJECT_DATA_PROJECT_FIELDS.put("EnterpriseProjectTypeName", "Enterprise Project");
      //PROJECT_DATA_PROJECT_FIELDS.put("OptimizerCommitDate", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("OptimizerDecisionAliasLookupTableId", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("OptimizerDecisionAliasLookupTableValueId", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("OptimizerDecisionID", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("OptimizerDecisionName", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("OptimizerSolutionName", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("ParentProjectId", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("PlannerCommitDate", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("PlannerDecisionAliasLookupTableId", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("PlannerDecisionAliasLookupTableValueId", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("PlannerDecisionID", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("PlannerDecisionName", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("PlannerEndDate", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("PlannerSolutionName", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("PlannerStartDate", null);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectActualCost", ProjectField.ACTUAL_COST);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectActualDuration", ProjectField.ACTUAL_DURATION);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectActualFinishDate", ProjectField.ACTUAL_FINISH);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectActualOvertimeCost", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectActualOvertimeWork", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectActualRegularCost", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectActualRegularWork", "0.000000");
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectActualStartDate", ProjectField.ACTUAL_START);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectActualWork", ProjectField.ACTUAL_WORK);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectACWP", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectAuthorName", "i:0#.f|membership|example@example.onmicrosoft.com");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectBCWP", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectBCWS", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectBudgetCost", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectBudgetWork", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectCalculationsAreStale", false);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectCalendarDuration", 0);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectCategoryName", ProjectField.CATEGORY);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectCompanyName", ProjectField.COMPANY);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectCost", ProjectField.COST);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectCostVariance", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectCPI", "0.000000");
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectCreatedDate", ProjectField.CREATION_DATE);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectCurrency", ProjectField.CURRENCY_CODE);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectCV", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectCVP", "0.0000000000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectDescription", "");
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectDuration", ProjectField.DURATION);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectDurationVariance", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectEAC", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectEarlyFinish", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectEarlyStart", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectEarnedValueIsStale", true);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectEnterpriseFeatures", true);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectFinishDate", ProjectField.FINISH_DATE);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectFinishVariance", ProjectField.FINISH_VARIANCE);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectFixedCost", "0.000000");
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectIdentifier", ProjectField.PROJECT_ID);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectKeywords", ProjectField.KEYWORDS);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectLateFinish", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectLateStart", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectLastPublishedDate", "2024-04-25T17:19:25.19");
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectManagerName", ProjectField.MANAGER);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectModifiedDate", ProjectField.LAST_SAVED);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectName", ProjectField.NAME);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectOvertimeCost", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectOvertimeWork", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectOwnerId", "64a2ddfe-fe02-ef11-92fb-00155d80a707");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectOwnerName", "Jon Iles");
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectPercentCompleted", ProjectField.PERCENTAGE_COMPLETE);
      ///PROJECT_DATA_PROJECT_FIELDS.put("ProjectPercentWorkCompleted", 0);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectRegularCost", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectRegularWork", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectRemainingCost", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectRemainingDuration", "8.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectRemainingOvertimeCost", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectRemainingOvertimeWork", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectRemainingRegularCost", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectRemainingRegularWork", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectRemainingWork", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectResourcePlanWork", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectSPI", "0.000000");
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectStartDate", ProjectField.START_DATE);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectStartVariance", ProjectField.START_VARIANCE);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectStatusDate", ProjectField.STATUS_DATE);
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectSubject", ProjectField.SUBJECT);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectSV", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectSVP", "0.0000000000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectTCPI", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectTimephased", "Disabled");
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectTitle", ProjectField.PROJECT_TITLE);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectType", 0);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectVAC", "0.000000");
      PROJECT_DATA_PROJECT_FIELDS.put("ProjectWork", ProjectField.WORK);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectWorkspaceInternalUrl", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectWorkVariance", "0.000000");
      //PROJECT_DATA_PROJECT_FIELDS.put("ResourcePlanUtilizationDate", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("ResourcePlanUtilizationType", 0);
      //PROJECT_DATA_PROJECT_FIELDS.put("WorkflowCreatedDate", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("WorkflowError", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("WorkflowErrorResponseCode", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("WorkflowInstanceId", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("WorkflowOwnerId", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("WorkflowOwnerName", null);
      //PROJECT_DATA_PROJECT_FIELDS.put("ProjectDepartments", null);
   }

   private static final Map<String, ProjectField> PROJECT_SERVER_PROJECT_FIELDS = new HashMap<>();
   static
   {
      //PROJECT_SERVER_PROJECT_FIELDS.put("odata.metadata", "https://example.sharepoint.com/sites/pwa/_api/$metadata#SP.ApiData.PublishedProjects/@Element");
      //PROJECT_SERVER_PROJECT_FIELDS.put("odata.type", "PS.PublishedProject");
      //PROJECT_SERVER_PROJECT_FIELDS.put("odata.id", "https://example.sharepoint.com/sites/pwa/_api/ProjectServer/Projects('47bd06f0-2703-ef11-ba8c-00155d805832')");
      //PROJECT_SERVER_PROJECT_FIELDS.put("odata.editLink", "ProjectServer/Projects('47bd06f0-2703-ef11-ba8c-00155d805832')");
      //PROJECT_SERVER_PROJECT_FIELDS.put("ApprovedEnd", "0001-01-01T00:00:00");
      //PROJECT_SERVER_PROJECT_FIELDS.put("ApprovedStart", "0001-01-01T00:00:00");
      //PROJECT_SERVER_PROJECT_FIELDS.put("CalculateActualCosts", true);
      //PROJECT_SERVER_PROJECT_FIELDS.put("CalculatesActualCosts", false);
      //PROJECT_SERVER_PROJECT_FIELDS.put("CheckedOutDate", "2025-08-13T09:04:03.67");
      //PROJECT_SERVER_PROJECT_FIELDS.put("CheckOutDescription", "DESKTOP-T0OAJG6\\https://example.sharepoint.com");
      //PROJECT_SERVER_PROJECT_FIELDS.put("CheckOutId", "00000000-0000-0000-0000-000000000000");
      //PROJECT_SERVER_PROJECT_FIELDS.put("CreatedDate", "2024-04-25T17:19:18.21");
      PROJECT_SERVER_PROJECT_FIELDS.put("CriticalSlackLimit", ProjectField.CRITICAL_SLACK_LIMIT);
      PROJECT_SERVER_PROJECT_FIELDS.put("DefaultFinishTime", ProjectField.DEFAULT_END_TIME);
      //PROJECT_SERVER_PROJECT_FIELDS.put("DefaultOvertimeRateUnits", 2);
      //PROJECT_SERVER_PROJECT_FIELDS.put("DefaultStandardRateUnits", 2);
      PROJECT_SERVER_PROJECT_FIELDS.put("DefaultStartTime", ProjectField.DEFAULT_START_TIME);
      PROJECT_SERVER_PROJECT_FIELDS.put("HonorConstraints", ProjectField.HONOR_CONSTRAINTS);
      //PROJECT_SERVER_PROJECT_FIELDS.put("Id", "47bd06f0-2703-ef11-ba8c-00155d805832");
      //PROJECT_SERVER_PROJECT_FIELDS.put("IsCheckedOut", false);
      //PROJECT_SERVER_PROJECT_FIELDS.put("LastPublishedDate", "2025-08-13T09:16:40.123");
      //PROJECT_SERVER_PROJECT_FIELDS.put("LastSavedDate", "2025-08-13T09:16:01.33");
      //PROJECT_SERVER_PROJECT_FIELDS.put("MoveActualIfLater", false);
      //PROJECT_SERVER_PROJECT_FIELDS.put("MoveActualToStatus", false);
      //PROJECT_SERVER_PROJECT_FIELDS.put("MoveRemainingIfEarlier", false);
      //PROJECT_SERVER_PROJECT_FIELDS.put("MoveRemainingToStatus", false);
      PROJECT_SERVER_PROJECT_FIELDS.put("MultipleCriticalPaths", ProjectField.MULTIPLE_CRITICAL_PATHS);
      //PROJECT_SERVER_PROJECT_FIELDS.put("OptimizerDecision", 0);
      //PROJECT_SERVER_PROJECT_FIELDS.put("PercentComplete", 7);
      //PROJECT_SERVER_PROJECT_FIELDS.put("PlannerDecision", 0);
      //PROJECT_SERVER_PROJECT_FIELDS.put("ProjectType", 0);
      PROJECT_SERVER_PROJECT_FIELDS.put("SplitInProgress", ProjectField.SPLIT_IN_PROGRESS_TASKS);
      PROJECT_SERVER_PROJECT_FIELDS.put("SpreadActualCostsToStatus", ProjectField.SPREAD_ACTUAL_COST);
      PROJECT_SERVER_PROJECT_FIELDS.put("SpreadPercentCompleteToStatus", ProjectField.SPREAD_PERCENT_COMPLETE);
      //PROJECT_SERVER_PROJECT_FIELDS.put("SummaryTaskId", "53bd06f0-2703-ef11-ba8c-00155d805832");
      //PROJECT_SERVER_PROJECT_FIELDS.put("CurrencyCode", "USD");
      PROJECT_SERVER_PROJECT_FIELDS.put("CurrencyDigits", ProjectField.CURRENCY_DIGITS);
      PROJECT_SERVER_PROJECT_FIELDS.put("CurrencyPosition", ProjectField.CURRENCY_SYMBOL_POSITION);
      PROJECT_SERVER_PROJECT_FIELDS.put("CurrencySymbol", ProjectField.CURRENCY_SYMBOL);
      PROJECT_SERVER_PROJECT_FIELDS.put("CurrentDate", ProjectField.CURRENT_DATE);
      PROJECT_SERVER_PROJECT_FIELDS.put("DaysPerMonth", ProjectField.DAYS_PER_MONTH);
      PROJECT_SERVER_PROJECT_FIELDS.put("DefaultEffortDriven", ProjectField.NEW_TASKS_EFFORT_DRIVEN);
      PROJECT_SERVER_PROJECT_FIELDS.put("DefaultEstimatedDuration", ProjectField.NEW_TASKS_ESTIMATED);
      PROJECT_SERVER_PROJECT_FIELDS.put("DefaultFixedCostAccrual", ProjectField.DEFAULT_FIXED_COST_ACCRUAL);
      PROJECT_SERVER_PROJECT_FIELDS.put("DefaultOvertimeRate", ProjectField.DEFAULT_OVERTIME_RATE);
      PROJECT_SERVER_PROJECT_FIELDS.put("DefaultStandardRate", ProjectField.DEFAULT_STANDARD_RATE);
      PROJECT_SERVER_PROJECT_FIELDS.put("DefaultTaskType", ProjectField.DEFAULT_TASK_TYPE);
      //PROJECT_SERVER_PROJECT_FIELDS.put("DefaultWorkFormat", 2);
      //PROJECT_SERVER_PROJECT_FIELDS.put("Description", "");
      //PROJECT_SERVER_PROJECT_FIELDS.put("FinishDate", "2024-05-07T17:00:00");
      PROJECT_SERVER_PROJECT_FIELDS.put("FiscalYearStartMonth", ProjectField.FISCAL_YEAR_START_MONTH);
      PROJECT_SERVER_PROJECT_FIELDS.put("MinutesPerDay", ProjectField.MINUTES_PER_DAY);
      PROJECT_SERVER_PROJECT_FIELDS.put("MinutesPerWeek", ProjectField.MINUTES_PER_WEEK);
      //PROJECT_SERVER_PROJECT_FIELDS.put("Name", "Test Project");
      PROJECT_SERVER_PROJECT_FIELDS.put("NewTasksAreManual", ProjectField.NEW_TASKS_ARE_MANUAL);
      PROJECT_SERVER_PROJECT_FIELDS.put("NumberFiscalYearFromStart", ProjectField.FISCAL_YEAR_START);
      //PROJECT_SERVER_PROJECT_FIELDS.put("ProjectIdentifier", "100000");
      PROJECT_SERVER_PROJECT_FIELDS.put("ProtectedActualsSynch", ProjectField.ACTUALS_IN_SYNC);
      PROJECT_SERVER_PROJECT_FIELDS.put("ScheduledFromStart", ProjectField.SCHEDULE_FROM);
      //PROJECT_SERVER_PROJECT_FIELDS.put("ShowEstimatedDurations", true);
      //PROJECT_SERVER_PROJECT_FIELDS.put("StartDate", "2024-04-25T08:00:00");
      //PROJECT_SERVER_PROJECT_FIELDS.put("StatusDate", "0001-01-01T00:00:00");
      //PROJECT_SERVER_PROJECT_FIELDS.put("TrackingMode", 0);
      //PROJECT_SERVER_PROJECT_FIELDS.put("UtilizationDate", "0001-01-01T00:00:00");
      //PROJECT_SERVER_PROJECT_FIELDS.put("UtilizationType", 0);
      PROJECT_SERVER_PROJECT_FIELDS.put("WeekStartDay", ProjectField.WEEK_START_DAY);
      //PROJECT_SERVER_PROJECT_FIELDS.put("WinprojVersion", "14.1790290000");
   }

   private static final Map<String, ResourceField> RESOURCE_FIELDS = new HashMap<>();
   static
   {
      //RESOURCE_FIELDS.put("odata.type", "PS.PublishedProjectResource");
      //RESOURCE_FIELDS.put("odata.id", "https://example.sharepoint.com/sites/pwa/_api/ProjectServer/Projects('47bd06f0-2703-ef11-ba8c-00155d805832')/ProjectResources('323acc7e-2578-f011-b51f-00155d80b22e')");
      //RESOURCE_FIELDS.put("odata.editLink", "ProjectServer/Projects('47bd06f0-2703-ef11-ba8c-00155d805832')/ProjectResources('323acc7e-2578-f011-b51f-00155d80b22e')");
      RESOURCE_FIELDS.put("ActualCost", ResourceField.ACTUAL_COST);
      //RESOURCE_FIELDS.put("ActualCostWorkPerformed", "0h");
      RESOURCE_FIELDS.put("ActualCostWorkPerformedMilliseconds", ResourceField.ACWP);
      //RESOURCE_FIELDS.put("ActualCostWorkPerformedTimeSpan", "PT0S");
      RESOURCE_FIELDS.put("ActualOvertimeCost", ResourceField.ACTUAL_OVERTIME_COST);
      //RESOURCE_FIELDS.put("ActualOvertimeWork", "0h");
      RESOURCE_FIELDS.put("ActualOvertimeWorkMilliseconds", ResourceField.ACTUAL_OVERTIME_WORK);
      //RESOURCE_FIELDS.put("ActualOvertimeWorkTimeSpan", "PT0S");
      //RESOURCE_FIELDS.put("ActualWork", "0h");
      RESOURCE_FIELDS.put("ActualWorkMilliseconds", ResourceField.ACTUAL_WORK);
      //RESOURCE_FIELDS.put("ActualWorkTimeSpan", "PT0S");
      RESOURCE_FIELDS.put("AvailableFrom", ResourceField.AVAILABLE_FROM);
      RESOURCE_FIELDS.put("AvailableTo", ResourceField.AVAILABLE_TO);
      RESOURCE_FIELDS.put("BaselineCost", ResourceField.BASELINE_COST);
      //RESOURCE_FIELDS.put("BaselineWork", "0h");
      RESOURCE_FIELDS.put("BaselineWorkMilliseconds", ResourceField.BASELINE_WORK);
      //RESOURCE_FIELDS.put("BaselineWorkTimeSpan", "PT0S");
      RESOURCE_FIELDS.put("BudetCostWorkPerformed", ResourceField.BCWP);
      RESOURCE_FIELDS.put("BudgetedCost", ResourceField.BUDGET_COST);
      RESOURCE_FIELDS.put("BudgetedCostWorkScheduled", ResourceField.BCWS);
      //RESOURCE_FIELDS.put("BudgetedWork", "0h");
      RESOURCE_FIELDS.put("BudgetedWorkMilliseconds", ResourceField.BUDGET_WORK);
      //RESOURCE_FIELDS.put("BudgetedWorkTimeSpan", "PT0S");
      RESOURCE_FIELDS.put("Cost", ResourceField.COST);
      RESOURCE_FIELDS.put("CostVariance", ResourceField.COST_VARIANCE);
      //RESOURCE_FIELDS.put("CostVarianceAtCompletion", 0.0);
      RESOURCE_FIELDS.put("Created", ResourceField.CREATED);
      //RESOURCE_FIELDS.put("CurrentCostVariance", 0.0);
      RESOURCE_FIELDS.put("Finish", ResourceField.FINISH);
      RESOURCE_FIELDS.put("Id", ResourceField.GUID);
      RESOURCE_FIELDS.put("IsBudgeted", ResourceField.BUDGET);
      RESOURCE_FIELDS.put("IsGenericResource", ResourceField.GENERIC);
      RESOURCE_FIELDS.put("IsOverAllocated", ResourceField.OVERALLOCATED);
      //RESOURCE_FIELDS.put("Modified", "2025-08-13T09:16:01.197");
      RESOURCE_FIELDS.put("Notes", ResourceField.NOTES);
      RESOURCE_FIELDS.put("OvertimeCost", ResourceField.OVERTIME_COST);
      //RESOURCE_FIELDS.put("OvertimeWork", "0h");
      RESOURCE_FIELDS.put("OvertimeWorkMilliseconds", ResourceField.OVERTIME_WORK);
      //RESOURCE_FIELDS.put("OvertimeWorkTimeSpan", "PT0S");
      //RESOURCE_FIELDS.put("PeakWork", "0h");
      //RESOURCE_FIELDS.put("PeakWorkMilliseconds", 0);
      //RESOURCE_FIELDS.put("PeakWorkTimeSpan", "PT0S");
      RESOURCE_FIELDS.put("PercentWorkComplete", ResourceField.PERCENT_WORK_COMPLETE);
      //RESOURCE_FIELDS.put("RegularWork", "0h");
      RESOURCE_FIELDS.put("RegularWorkMilliseconds", ResourceField.REGULAR_WORK);
      //RESOURCE_FIELDS.put("RegularWorkTimeSpan", "PT0S");
      RESOURCE_FIELDS.put("RemainingCost", ResourceField.REMAINING_COST);
      RESOURCE_FIELDS.put("RemainingOvertimeCost", ResourceField.REMAINING_OVERTIME_COST);
      //RESOURCE_FIELDS.put("RemainingOvertimeWork", "0h");
      RESOURCE_FIELDS.put("RemainingOvertimeWorkMilliseconds", ResourceField.REMAINING_OVERTIME_WORK);
      //RESOURCE_FIELDS.put("RemainingOvertimeWorkTimeSpan", "PT0S");
      //RESOURCE_FIELDS.put("RemainingWork", "0h");
      RESOURCE_FIELDS.put("RemainingWorkMilliseconds", ResourceField.REMAINING_WORK);
      //RESOURCE_FIELDS.put("RemainingWorkTimeSpan", "PT0S");
      //RESOURCE_FIELDS.put("ScheduleCostVariance", 0.0);
      RESOURCE_FIELDS.put("Start", ResourceField.START);
      //RESOURCE_FIELDS.put("Work", "0h");
      RESOURCE_FIELDS.put("WorkMilliseconds", ResourceField.WORK);
      //RESOURCE_FIELDS.put("WorkTimeSpan", "PT0S");
      //RESOURCE_FIELDS.put("WorkVariance", "0h");
      RESOURCE_FIELDS.put("WorkVarianceMilliseconds", ResourceField.WORK_VARIANCE);
      //RESOURCE_FIELDS.put("WorkVarianceTimeSpan", "PT0S");
      RESOURCE_FIELDS.put("CanLevel", ResourceField.CAN_LEVEL);
      RESOURCE_FIELDS.put("Code", ResourceField.CODE);
      RESOURCE_FIELDS.put("CostAccrual", ResourceField.ACCRUE_AT);
      RESOURCE_FIELDS.put("CostCenter", ResourceField.COST_CENTER);
      RESOURCE_FIELDS.put("CostPerUse", ResourceField.COST_PER_USE);
      RESOURCE_FIELDS.put("DefaultBookingType", ResourceField.BOOKING_TYPE);
      RESOURCE_FIELDS.put("Email", ResourceField.EMAIL_ADDRESS);
      RESOURCE_FIELDS.put("Group", ResourceField.GROUP);
      RESOURCE_FIELDS.put("Initials", ResourceField.INITIALS);
      RESOURCE_FIELDS.put("MaterialLabel", ResourceField.MATERIAL_LABEL);
      RESOURCE_FIELDS.put("MaximumCapacity", ResourceField.MAX_UNITS);
      RESOURCE_FIELDS.put("Name", ResourceField.NAME);
      RESOURCE_FIELDS.put("OvertimeRate", ResourceField.OVERTIME_RATE);
      RESOURCE_FIELDS.put("OvertimeRateUnits", ResourceField.OVERTIME_RATE_UNITS);
      RESOURCE_FIELDS.put("Phonetics", ResourceField.PHONETICS);
      RESOURCE_FIELDS.put("StandardRate", ResourceField.STANDARD_RATE);
      RESOURCE_FIELDS.put("StandardRateUnits", ResourceField.STANDARD_RATE_UNITS);
   }

   private static final Map<String, TaskField> TASK_FIELDS = new HashMap<>();
   static
   {
      //TASK_FIELDS.put("odata.type", "PS.PublishedTask");
      //TASK_FIELDS.put("odata.id", "https://example.sharepoint.com/sites/pwa/_api/ProjectServer/Projects('47bd06f0-2703-ef11-ba8c-00155d805832')/Tasks('de57d9b7-a356-f011-97c7-080027c4b287')");
      //TASK_FIELDS.put("odata.editLink", "ProjectServer/Projects('47bd06f0-2703-ef11-ba8c-00155d805832')/Tasks('de57d9b7-a356-f011-97c7-080027c4b287')");
      TASK_FIELDS.put("ActualCostWorkPerformed", TaskField.ACWP);
      //TASK_FIELDS.put("ActualDuration", "0d");
      TASK_FIELDS.put("ActualDurationMilliseconds", TaskField.ACTUAL_DURATION);
      //TASK_FIELDS.put("ActualDurationTimeSpan", "PT0S");
      TASK_FIELDS.put("ActualOvertimeCost", TaskField.ACTUAL_OVERTIME_COST);
      //TASK_FIELDS.put("ActualOvertimeWork", "0h");
      TASK_FIELDS.put("ActualOvertimeWorkMilliseconds", TaskField.ACTUAL_OVERTIME_WORK);
      //TASK_FIELDS.put("ActualOvertimeWorkTimeSpan", "PT0S");
      TASK_FIELDS.put("BaselineCost", TaskField.BASELINE_COST);
      //TASK_FIELDS.put("BaselineDuration", null);
      TASK_FIELDS.put("BaselineDurationMilliseconds", TaskField.BASELINE_DURATION);
      //TASK_FIELDS.put("BaselineDurationTimeSpan", "PT0S");
      TASK_FIELDS.put("BaselineFinish", TaskField.BASELINE_FINISH);
      TASK_FIELDS.put("BaselineStart", TaskField.BASELINE_START);
      //TASK_FIELDS.put("BaselineWork", null);
      TASK_FIELDS.put("BaselineWorkMilliseconds", TaskField.BASELINE_WORK);
      //TASK_FIELDS.put("BaselineWorkTimeSpan", "PT0S");
      TASK_FIELDS.put("BudgetCost", TaskField.BUDGET_COST);
      TASK_FIELDS.put("BudgetedCostWorkPerformed", TaskField.BCWP);
      TASK_FIELDS.put("BudgetedCostWorkScheduled", TaskField.BCWS);
      TASK_FIELDS.put("Contact", TaskField.CONTACT);
      TASK_FIELDS.put("CostPerformanceIndex", TaskField.CPI);
      TASK_FIELDS.put("CostVariance", TaskField.COST_VARIANCE);
      //TASK_FIELDS.put("CostVarianceAtCompletion", 0.0);
      //TASK_FIELDS.put("CostVariancePercentage", 0);
      TASK_FIELDS.put("Created", TaskField.CREATED);
      //TASK_FIELDS.put("CurrentCostVariance", 0.0);
      //TASK_FIELDS.put("DurationVariance", "3d");
      TASK_FIELDS.put("DurationVarianceMilliseconds", TaskField.DURATION_VARIANCE);
      //TASK_FIELDS.put("DurationVarianceTimeSpan", "P1D");
      TASK_FIELDS.put("EarliestFinish", TaskField.EARLY_FINISH);
      TASK_FIELDS.put("EarliestStart", TaskField.EARLY_START);
      TASK_FIELDS.put("EstimateAtCompletion", TaskField.EAC);
      //TASK_FIELDS.put("ExternalProjectUid", "00000000-0000-0000-0000-000000000000");
      //TASK_FIELDS.put("ExternalTaskUid", "00000000-0000-0000-0000-000000000000");
      //TASK_FIELDS.put("FinishSlack", "6d");
      TASK_FIELDS.put("FinishSlackMilliseconds", TaskField.FINISH_SLACK);
      //TASK_FIELDS.put("FinishSlackTimeSpan", "P2D");
      //TASK_FIELDS.put("FinishVariance", "0d");
      TASK_FIELDS.put("FinishVarianceMilliseconds", TaskField.FINISH_VARIANCE);
      //TASK_FIELDS.put("FinishVarianceTimeSpan", "PT0S");
      //TASK_FIELDS.put("FreeSlack", "6d");
      TASK_FIELDS.put("FreeSlackMilliseconds", TaskField.FREE_SLACK);
      //TASK_FIELDS.put("FreeSlackTimeSpan", "P2D");
      TASK_FIELDS.put("Id", TaskField.GUID);
      TASK_FIELDS.put("IgnoreResourceCalendar", TaskField.IGNORE_RESOURCE_CALENDAR);
      TASK_FIELDS.put("IsCritical", TaskField.CRITICAL);
      TASK_FIELDS.put("IsDurationEstimate", TaskField.ESTIMATED);
      TASK_FIELDS.put("IsExternalTask", TaskField.EXTERNAL_TASK);
      TASK_FIELDS.put("IsOverAllocated", TaskField.OVERALLOCATED);
      TASK_FIELDS.put("IsRecurring", TaskField.RECURRING);
      //TASK_FIELDS.put("IsRecurringSummary", false);
      TASK_FIELDS.put("IsRolledUp", TaskField.ROLLUP);
      //TASK_FIELDS.put("IsSubProject", false);
      TASK_FIELDS.put("IsSubProjectReadOnly", TaskField.SUBPROJECT_READ_ONLY);
      //TASK_FIELDS.put("IsSubProjectScheduledFromFinish", false);
      TASK_FIELDS.put("IsSummary", TaskField.SUMMARY);
      TASK_FIELDS.put("LatestFinish", TaskField.LATE_FINISH);
      TASK_FIELDS.put("LatestStart", TaskField.LATE_START);
      //TASK_FIELDS.put("LevelingDelay", "0ed");
      TASK_FIELDS.put("LevelingDelayMilliseconds", TaskField.LEVELING_DELAY);
      //TASK_FIELDS.put("LevelingDelayTimeSpan", "PT0S");
      //TASK_FIELDS.put("Modified", "2025-07-01T17:49:44.46");
      TASK_FIELDS.put("Notes", TaskField.NOTES);
      TASK_FIELDS.put("OutlinePosition", TaskField.OUTLINE_NUMBER);
      TASK_FIELDS.put("OvertimeCost", TaskField.OVERTIME_COST);
      //TASK_FIELDS.put("OvertimeWork", "0h");
      TASK_FIELDS.put("OvertimeWorkMilliseconds", TaskField.OVERTIME_WORK);
      //TASK_FIELDS.put("OvertimeWorkTimeSpan", "PT0S");
      TASK_FIELDS.put("PercentWorkComplete", TaskField.PERCENT_WORK_COMPLETE);
      TASK_FIELDS.put("PreLevelingFinish", TaskField.PRELEVELED_FINISH);
      TASK_FIELDS.put("PreLevelingStart", TaskField.PRELEVELED_START);
      //TASK_FIELDS.put("RegularWork", "0h");
      TASK_FIELDS.put("RegularWorkMilliseconds", TaskField.REGULAR_WORK);
      //TASK_FIELDS.put("RegularWorkTimeSpan", "PT0S");
      TASK_FIELDS.put("RemainingCost", TaskField.REMAINING_COST);
      TASK_FIELDS.put("RemainingOvertimeCost", TaskField.REMAINING_OVERTIME_COST);
      //TASK_FIELDS.put("RemainingOvertimeWork", "0h");
      TASK_FIELDS.put("RemainingOvertimeWorkMilliseconds", TaskField.REMAINING_OVERTIME_WORK);
      //TASK_FIELDS.put("RemainingOvertimeWorkTimeSpan", "PT0S");
      //TASK_FIELDS.put("RemainingWork", "0h");
      TASK_FIELDS.put("RemainingWorkMilliseconds", TaskField.REMAINING_WORK);
      //TASK_FIELDS.put("RemainingWorkTimeSpan", "PT0S");
      TASK_FIELDS.put("Resume", TaskField.RESUME);
      //TASK_FIELDS.put("ScheduleCostVariance", 0.0);
      //TASK_FIELDS.put("ScheduledDuration", "3d");
      TASK_FIELDS.put("ScheduledDurationMilliseconds", TaskField.SCHEDULED_DURATION);
      //TASK_FIELDS.put("ScheduledDurationTimeSpan", "P1D");
      TASK_FIELDS.put("ScheduledFinish", TaskField.SCHEDULED_FINISH);
      TASK_FIELDS.put("ScheduledStart", TaskField.SCHEDULED_START);
      TASK_FIELDS.put("SchedulePerformanceIndex", TaskField.SPI);
      TASK_FIELDS.put("ScheduleVariancePercentage", TaskField.SVPERCENT);
      //TASK_FIELDS.put("StartSlack", "6d");
      TASK_FIELDS.put("StartSlackMilliseconds", TaskField.START_SLACK);
      //TASK_FIELDS.put("StartSlackTimeSpan", "P2D");
      //TASK_FIELDS.put("StartVariance", "0d");
      TASK_FIELDS.put("StartVarianceMilliseconds", TaskField.START_VARIANCE);
      //TASK_FIELDS.put("StartVarianceTimeSpan", "PT0S");
      TASK_FIELDS.put("Stop", TaskField.STOP);
      TASK_FIELDS.put("ToCompletePerformanceIndex", TaskField.TCPI);
      //TASK_FIELDS.put("TotalSlack", "6d");
      TASK_FIELDS.put("TotalSlackMilliseconds", TaskField.TOTAL_SLACK);
      //TASK_FIELDS.put("TotalSlackTimeSpan", "P2D");
      TASK_FIELDS.put("WorkBreakdownStructure", TaskField.WBS);
      //TASK_FIELDS.put("WorkVariance", "0h");
      TASK_FIELDS.put("WorkVarianceMilliseconds", TaskField.WORK_VARIANCE);
      //TASK_FIELDS.put("WorkVarianceTimeSpan", "PT0S");
      TASK_FIELDS.put("ActualCost", TaskField.ACTUAL_COST);
      TASK_FIELDS.put("ActualFinish", TaskField.ACTUAL_FINISH);
      TASK_FIELDS.put("ActualStart", TaskField.ACTUAL_START);
      //TASK_FIELDS.put("ActualWork", "0h");
      TASK_FIELDS.put("ActualWorkMilliseconds", TaskField.ACTUAL_WORK);
      //TASK_FIELDS.put("ActualWorkTimeSpan", "PT0S");
      //TASK_FIELDS.put("BudgetWork", "0h");
      TASK_FIELDS.put("BudgetWorkMilliseconds", TaskField.BUDGET_WORK);
      //TASK_FIELDS.put("BudgetWorkTimeSpan", "PT0S");
      //TASK_FIELDS.put("Completion", "0001-01-01T00:00:00");
      TASK_FIELDS.put("ConstraintStartEnd", TaskField.CONSTRAINT_DATE);
      TASK_FIELDS.put("ConstraintType", TaskField.CONSTRAINT_TYPE);
      TASK_FIELDS.put("Cost", TaskField.COST);
      TASK_FIELDS.put("Deadline", TaskField.DEADLINE);
      //TASK_FIELDS.put("Duration", "3d");
      TASK_FIELDS.put("DurationMilliseconds", TaskField.DURATION);
      //TASK_FIELDS.put("DurationTimeSpan", "P1D");
      TASK_FIELDS.put("Finish", TaskField.FINISH);
      TASK_FIELDS.put("FinishText", TaskField.FINISH_TEXT);
      TASK_FIELDS.put("FixedCost", TaskField.FIXED_COST);
      TASK_FIELDS.put("FixedCostAccrual", TaskField.FIXED_COST_ACCRUAL);
      TASK_FIELDS.put("IsActive", TaskField.ACTIVE);
      TASK_FIELDS.put("IsEffortDriven", TaskField.EFFORT_DRIVEN);
      //TASK_FIELDS.put("IsLockedByManager", false);
      TASK_FIELDS.put("IsManual", TaskField.TASK_MODE);
      TASK_FIELDS.put("IsMarked", TaskField.MARKED);
      TASK_FIELDS.put("IsMilestone", TaskField.MILESTONE);
      TASK_FIELDS.put("LevelingAdjustsAssignments", TaskField.LEVEL_ASSIGNMENTS);
      TASK_FIELDS.put("LevelingCanSplit", TaskField.LEVELING_CAN_SPLIT);
      TASK_FIELDS.put("Name", TaskField.NAME);
      TASK_FIELDS.put("OutlineLevel", TaskField.OUTLINE_LEVEL);
      TASK_FIELDS.put("PercentComplete", TaskField.PERCENT_COMPLETE);
      TASK_FIELDS.put("PercentPhysicalWorkComplete", TaskField.PHYSICAL_PERCENT_COMPLETE);
      TASK_FIELDS.put("Priority", TaskField.PRIORITY);
      //TASK_FIELDS.put("RemainingDuration", "3d");
      TASK_FIELDS.put("RemainingDurationMilliseconds", TaskField.REMAINING_DURATION);
      //TASK_FIELDS.put("RemainingDurationTimeSpan", "P1D");
      TASK_FIELDS.put("Start", TaskField.START);
      TASK_FIELDS.put("StartText", TaskField.START_TEXT);
      TASK_FIELDS.put("TaskType", TaskField.TYPE);
      //TASK_FIELDS.put("UsePercentPhysicalWorkComplete", false);
      //TASK_FIELDS.put("Work", "0h");
      TASK_FIELDS.put("WorkMilliseconds", TaskField.WORK);
      //TASK_FIELDS.put("WorkTimeSpan", "PT0S");
   }

   private static final Map<String, AssignmentField> ASSIGNMENT_FIELDS = new HashMap<>();
   static
   {
      //ASSIGNMENT_FIELDS.put("odata.type", "PS.PublishedAssignment");
      //ASSIGNMENT_FIELDS.put("odata.id", "https://example.sharepoint.com/sites/pwa/_api/ProjectServer/Projects('47bd06f0-2703-ef11-ba8c-00155d805832')/Assignments('8f946826-5578-f011-97be-080027fff3b7')");
      //ASSIGNMENT_FIELDS.put("odata.editLink", "ProjectServer/Projects('47bd06f0-2703-ef11-ba8c-00155d805832')/Assignments('8f946826-5578-f011-97be-080027fff3b7')");
      ASSIGNMENT_FIELDS.put("ActualCostWorkPerformed", AssignmentField.ACWP);
      ASSIGNMENT_FIELDS.put("ActualOvertimeCost", AssignmentField.ACTUAL_OVERTIME_COST);
      ASSIGNMENT_FIELDS.put("BaselineCost", AssignmentField.BASELINE_COST);
      //ASSIGNMENT_FIELDS.put("BaselineCostPerUse", 0.0);
      ASSIGNMENT_FIELDS.put("BaselineFinish", AssignmentField.BASELINE_FINISH);
      ASSIGNMENT_FIELDS.put("BaselineStart", AssignmentField.BASELINE_START);
      //ASSIGNMENT_FIELDS.put("BaselineWork", null);
      ASSIGNMENT_FIELDS.put("BaselineWorkMilliseconds", AssignmentField.BASELINE_WORK);
      //ASSIGNMENT_FIELDS.put("BaselineWorkTimeSpan", "PT0S");
      ASSIGNMENT_FIELDS.put("BudgetedCostWorkPerformed", AssignmentField.BCWP);
      ASSIGNMENT_FIELDS.put("BudgetedCostWorkScheduled", AssignmentField.BCWS);
      ASSIGNMENT_FIELDS.put("CostVariance", AssignmentField.COST_VARIANCE);
      //ASSIGNMENT_FIELDS.put("CostVarianceAtCompletion", 0.0);
      ASSIGNMENT_FIELDS.put("Created", AssignmentField.CREATED);
      //ASSIGNMENT_FIELDS.put("CurrentCostVariance", -234.0);
      ASSIGNMENT_FIELDS.put("Finish", AssignmentField.FINISH);
      //ASSIGNMENT_FIELDS.put("FinishVariance", "0d");
      ASSIGNMENT_FIELDS.put("FinishVarianceMilliseconds", AssignmentField.FINISH_VARIANCE);
      //ASSIGNMENT_FIELDS.put("FinishVarianceTimeSpan", "PT0S");
      ASSIGNMENT_FIELDS.put("Id", AssignmentField.GUID);
      ASSIGNMENT_FIELDS.put("IsConfirmed", AssignmentField.CONFIRMED);
      ASSIGNMENT_FIELDS.put("IsOverAllocated", AssignmentField.OVERALLOCATED);
      //ASSIGNMENT_FIELDS.put("IsPublished", true);
      ASSIGNMENT_FIELDS.put("IsResponsePending", AssignmentField.RESPONSE_PENDING);
      ASSIGNMENT_FIELDS.put("IsUpdateNeeded", AssignmentField.UPDATE_NEEDED);
      //ASSIGNMENT_FIELDS.put("LevelingDelay", "0d");
      ASSIGNMENT_FIELDS.put("LevelingDelayMilliseconds", AssignmentField.LEVELING_DELAY);
      //ASSIGNMENT_FIELDS.put("LevelingDelayTimeSpan", "PT0S");
      //ASSIGNMENT_FIELDS.put("Modified", "2025-08-13T15:39:09.04");
      ASSIGNMENT_FIELDS.put("Notes", AssignmentField.NOTES);
      ASSIGNMENT_FIELDS.put("OvertimeCost", AssignmentField.OVERTIME_COST);
      ASSIGNMENT_FIELDS.put("RemainingCost", AssignmentField.REMAINING_COST);
      ASSIGNMENT_FIELDS.put("RemainingOvertimeCost", AssignmentField.REMAINING_OVERTIME_COST);
      ASSIGNMENT_FIELDS.put("Resume", AssignmentField.RESUME);
      //ASSIGNMENT_FIELDS.put("ScheduleCostVariance", 0.0);
      ASSIGNMENT_FIELDS.put("Start", AssignmentField.START);
      //ASSIGNMENT_FIELDS.put("StartVariance", "0d");
      ASSIGNMENT_FIELDS.put("StartVarianceMilliseconds", AssignmentField.START_VARIANCE);
      //ASSIGNMENT_FIELDS.put("StartVarianceTimeSpan", "PT0S");
      //ASSIGNMENT_FIELDS.put("Stop", "2024-04-25T17:00:00");
      //ASSIGNMENT_FIELDS.put("TimephasedAssignmentModCounter", 11);
      ASSIGNMENT_FIELDS.put("WorkContourType", AssignmentField.WORK_CONTOUR);
      //ASSIGNMENT_FIELDS.put("WorkVariance", "24h");
      ASSIGNMENT_FIELDS.put("WorkVarianceMilliseconds", AssignmentField.WORK_VARIANCE);
      //ASSIGNMENT_FIELDS.put("WorkVarianceTimeSpan", "P1D");
      ASSIGNMENT_FIELDS.put("ActualCost", AssignmentField.ACTUAL_COST);
      ASSIGNMENT_FIELDS.put("ActualFinish", AssignmentField.ACTUAL_FINISH);
      //ASSIGNMENT_FIELDS.put("ActualOvertimeWork", null);
      ASSIGNMENT_FIELDS.put("ActualOvertimeWorkMilliseconds", AssignmentField.ACTUAL_OVERTIME_WORK);
      //ASSIGNMENT_FIELDS.put("ActualOvertimeWorkTimeSpan", "PT0S");
      ASSIGNMENT_FIELDS.put("ActualStart", AssignmentField.ACTUAL_START);
      //ASSIGNMENT_FIELDS.put("ActualWork", "8h");
      ASSIGNMENT_FIELDS.put("ActualWorkMilliseconds", AssignmentField.ACTUAL_WORK);
      //ASSIGNMENT_FIELDS.put("ActualWorkTimeSpan", "PT8H");
      ASSIGNMENT_FIELDS.put("BudgetedCost", AssignmentField.BUDGET_COST);
      //ASSIGNMENT_FIELDS.put("BudgetedWork", null);
      ASSIGNMENT_FIELDS.put("BudgetedWorkMilliseconds", AssignmentField.BUDGET_WORK);
      //ASSIGNMENT_FIELDS.put("BudgetedWorkTimeSpan", "PT0S");
      ASSIGNMENT_FIELDS.put("Cost", AssignmentField.COST);
      ASSIGNMENT_FIELDS.put("CostRateTable", AssignmentField.COST_RATE_TABLE);
      //ASSIGNMENT_FIELDS.put("DefaultBookingType", 1);
      //ASSIGNMENT_FIELDS.put("Delay", "0d");
      ASSIGNMENT_FIELDS.put("DelayMilliseconds", AssignmentField.ASSIGNMENT_DELAY);
      //ASSIGNMENT_FIELDS.put("DelayTimeSpan", "PT0S");
      //ASSIGNMENT_FIELDS.put("IsLockedByManager", false);
      //ASSIGNMENT_FIELDS.put("IsWorkResource", false);
      //ASSIGNMENT_FIELDS.put("OvertimeWork", null);
      ASSIGNMENT_FIELDS.put("OvertimeWorkMilliseconds", AssignmentField.OVERTIME_WORK);
      //ASSIGNMENT_FIELDS.put("OvertimeWorkTimeSpan", "PT0S");
      ASSIGNMENT_FIELDS.put("PercentWorkComplete", AssignmentField.PERCENT_WORK_COMPLETE);
      //ASSIGNMENT_FIELDS.put("RegularWork", "24h");
      ASSIGNMENT_FIELDS.put("RegularWorkMilliseconds", AssignmentField.REGULAR_WORK);
      //ASSIGNMENT_FIELDS.put("RegularWorkTimeSpan", "P1D");
      //ASSIGNMENT_FIELDS.put("RemainingOvertimeWork", null);
      ASSIGNMENT_FIELDS.put("RemainingOvertimeWorkMilliseconds", AssignmentField.REMAINING_OVERTIME_WORK);
      //ASSIGNMENT_FIELDS.put("RemainingOvertimeWorkTimeSpan", "PT0S");
      //ASSIGNMENT_FIELDS.put("RemainingWork", "16h");
      ASSIGNMENT_FIELDS.put("RemainingWorkMilliseconds", AssignmentField.REMAINING_WORK);
      //ASSIGNMENT_FIELDS.put("RemainingWorkTimeSpan", "PT16H");
      ASSIGNMENT_FIELDS.put("ResourceCapacity", AssignmentField.ASSIGNMENT_UNITS);
      //ASSIGNMENT_FIELDS.put("Work", "24h");
      ASSIGNMENT_FIELDS.put("WorkMilliseconds", AssignmentField.WORK);
      //ASSIGNMENT_FIELDS.put("WorkTimeSpan", "P1D");
   }
}