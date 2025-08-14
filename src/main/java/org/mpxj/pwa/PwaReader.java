package org.mpxj.pwa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectField;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceField;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.opc.OpcException;

public class PwaReader
{
   public static void main(String[] argv)
   {
      PwaReader reader = new PwaReader(argv[0], argv[1]);
      reader.readProject(UUID.fromString("47bd06f0-2703-ef11-ba8c-00155d805832"));
   }

   public PwaReader(String host, String token)
   {
      m_host = host;
      m_token = token;
      m_mapper = new ObjectMapper();
      m_mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
   }

   public ProjectFile readProject(UUID id)
   {
      m_projectID = id;
      m_project = new ProjectFile();

      readProjectProperties();
      readCalendars();
      readResources();

      return m_project;
   }

   private void readProjectProperties()
   {
      HttpURLConnection connection = createConnection("ProjectData/Projects(guid'" + m_projectID + "')");
      int code = getResponseCode(connection);

      if (code != 200)
      {
         throw new PwaException(getExceptionMessage(connection, code));
      }

      populateFieldContainer(m_project.getProjectProperties(), PROJECT_DATA_PROJECT_FIELDS, readMapRow(connection));

      connection = createConnection("ProjectServer/Projects(guid'" + m_projectID + "')");
      code = getResponseCode(connection);

      if (code != 200)
      {
         throw new PwaException(getExceptionMessage(connection, code));
      }

      populateFieldContainer(m_project.getProjectProperties(), PROJECT_SERVER_PROJECT_FIELDS, readMapRow(connection));
   }

   /**
    * Issues with PWA:
    * 1. WorkWeeks are not available, although allegedly there is an endpoint
    * 2. Resource calendars are not available
    */
   private void readCalendars()
   {
      HttpURLConnection connection = createConnection("ProjectServer/Calendars");
      int code = getResponseCode(connection);

      if (code != 200)
      {
         throw new PwaException(getExceptionMessage(connection, code));
      }

      readValue(connection, ListContainer.class).getValue().forEach(item -> readCalendar(new MapRow(item)));
   }

   private void readCalendar(MapRow row)
   {
      ProjectCalendar calendar = m_project.addDefaultBaseCalendar();
      //"odata.type": "PS.Calendar",
      //"odata.id": "https://example.sharepoint.com/sites/pwa/_api/ProjectServer/Calendars('9410ae84-5878-f011-97be-080027fff3b7')",
      //"odata.editLink": "ProjectServer/Calendars('9410ae84-5878-f011-97be-080027fff3b7')",
      //"Created": "2025-08-13T15:18:27.837",
      calendar.setGUID(row.getUUID("Id"));
      //"IsStandardCalendar": false,
      //"Modified": "2025-08-13T15:18:27.837",
      calendar.setName(row.getString("Name"));

      readCalendarExceptions(calendar);
   }

   private void readCalendarExceptions(ProjectCalendar calendar)
   {
      HttpURLConnection connection = createConnection("ProjectServer/Calendars('" + calendar.getGUID() + "')/BaseCalendarExceptions");
      int code = getResponseCode(connection);

      if (code != 200)
      {
         throw new PwaException(getExceptionMessage(connection, code));
      }

      readValue(connection, ListContainer.class).getValue().forEach(item -> readCalendarException(calendar, new MapRow(item)));
   }

   private void readCalendarException(ProjectCalendar calendar, MapRow row)
   {
      ProjectCalendarException exception = calendar.addCalendarException(row.getLocalDate("Start"), row.getLocalDate("Finish"));
      //"odata.type": "PS.BaseCalendarException",
      //"odata.id": "https://example.sharepoint.com/sites/pwa/_api/ProjectServer/Calendars('b6635b2e-e747-4771-a78b-24f7509629d0')/BaseCalendarExceptions(0)",
      //"odata.editLink": "ProjectServer/Calendars('b6635b2e-e747-4771-a78b-24f7509629d0')/BaseCalendarExceptions(0)",
      //"Start": "2025-08-18T00:00:00"
      //"Finish": "2025-08-18T00:00:00",
      //"Id": 0,
      exception.setName(row.getString("Name"));
      addRange(exception, row, 1);
      addRange(exception, row, 2);
      addRange(exception, row, 3);
      addRange(exception, row, 4);
      addRange(exception, row, 5);

      //"RecurrenceDays": 0,
      //"RecurrenceFrequency": 1,
      //"RecurrenceMonth": 0,
      //"RecurrenceMonthDay": 0,
      //"RecurrenceType": 0,
      //"RecurrenceWeek": 0,
   }

   private void addRange(ProjectCalendarException exception, MapRow row, int index)
   {
      String shift = "Shift" + index;
      int start = row.getInt(shift + "Start");
      int finish = row.getInt(shift + "Finish");

      // TODO check 24 hour
      if (start == finish)
      {
         return;
      }


      exception.add(new LocalTimeRange(LocalTime.MIDNIGHT.plusMinutes(start), LocalTime.MIDNIGHT.plusMinutes(finish)));
   }

   private void readResources()
   {
      HttpURLConnection connection = createConnection("ProjectServer/Projects(guid'" + m_projectID + "')/ProjectResources");
      int code = getResponseCode(connection);

      if (code != 200)
      {
         throw new PwaException(getExceptionMessage(connection, code));
      }

      readValue(connection, ListContainer.class).getValue().forEach(item -> populateFieldContainer(m_project.addResource(), RESOURCE_FIELDS, new MapRow(item)));
   }

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

      return connection.getRequestMethod() + " " + connection.getURL() + " failed: "+ "\nresponseCode=" + code + "\nresponseBody=" + responseBody;
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

   private MapRow readMapRow(HttpURLConnection connection)
   {
      return new MapRow(readValue(connection));
   }

   private Map<String, Object> readValue(HttpURLConnection connection)
   {
      return readValue(connection, new TypeReference<Map<String, Object>>()
      {
      });
   }

   private <T> T readValue(HttpURLConnection connection, TypeReference<T> valueTypeRef)
   {
      try
      {
         return m_mapper.readValue(getInputStream(connection), valueTypeRef);
      }

      catch (IOException ex)
      {
         throw new PwaException(ex);
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
         throw new PwaException(ex);
      }
   }

   private void populateFieldContainer(FieldContainer container, Map<String, ? extends FieldType> index, MapRow data)
   {
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
      //RESOURCE_FIELDS.put("ActualCostWorkPerformedMilliseconds", 0);
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
      //RESOURCE_FIELDS.put("BudetCostWorkPerformed", 0.0);
      RESOURCE_FIELDS.put("BudgetedCost", ResourceField.BUDGET_COST);
      //RESOURCE_FIELDS.put("BudgetedCostWorkScheduled", 0.0);
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
}