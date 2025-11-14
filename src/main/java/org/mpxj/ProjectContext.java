/*
 * file:       ProjectContext.java
 * author:     Jon Iles
 * date:       2024-08-22
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

package org.mpxj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

import org.mpxj.common.ObjectSequence;

/**
 * Implements a container for common data which can be shared across multiple ProjectFile instances.
 */
public class ProjectContext implements UniqueIdObjectSequenceProvider
{
   /**
    * Retrieve the locations available in this context.
    *
    * @return locations
    */
   public LocationContainer getLocations()
   {
      return m_locations;
   }

   /**
    * Retrieve the units of measure available in this context.
    *
    * @return units of measure
    */
   public UnitOfMeasureContainer getUnitsOfMeasure()
   {
      return m_unitsOfMeasure;
   }

   /**
    * Retrieves the expense categories available in this context.
    *
    * @return expense categories
    */
   public ExpenseCategoryContainer getExpenseCategories()
   {
      return m_expenseCategories;
   }

   /**
    * Retrieves the cost accounts available in this context.
    *
    * @return cost accounts
    */
   public CostAccountContainer getCostAccounts()
   {
      return m_costAccounts;
   }

   /**
    * Retrieves the work contours available in this context.
    *
    * @return work contours
    */
   public WorkContourContainer getWorkContours()
   {
      return m_workContours;
   }

   /**
    * Retrieves the notes topics available in this context.
    *
    * @return notes topics
    */
   public NotesTopicContainer getNotesTopics()
   {
      return m_notesTopics;
   }

   /**
    * Retrieves the custom fields available in this context.
    *
    * @return custom fields
    */
   public CustomFieldContainer getCustomFields()
   {
      return m_customFields;
   }

   /**
    * Retrieves the user defined fields available in this context.
    *
    * @return user defined fields
    */
   public UserDefinedFieldContainer getUserDefinedFields()
   {
      return m_userDefinedFields;
   }

   /**
    * Retrieves the activity code configurations available in this context.
    *
    * @return activity codes
    */
   public ActivityCodeContainer getActivityCodes()
   {
      return m_activityCodes;
   }

   /**
    * Retrieves the project code configurations available in this context.
    *
    * @return project codes
    */
   public ProjectCodeContainer getProjectCodes()
   {
      return m_projectCodes;
   }

   /**
    * Retrieves the resource code configurations available in this context.
    *
    * @return resource codes
    */
   public ResourceCodeContainer getResourceCodes()
   {
      return m_resourceCodes;
   }

   /**
    * Retrieves the role code configurations available in this context.
    *
    * @return role codes
    */
   public RoleCodeContainer getRoleCodes()
   {
      return m_roleCodes;
   }

   /**
    * Retrieves the resource assignment code configurations available in this context.
    *
    * @return resource assignment codes
    */
   public ResourceAssignmentCodeContainer getResourceAssignmentCodes()
   {
      return m_resourceAssignmentCodes;
   }

   /**
    * Retrieves the shifts available in this context.
    *
    * @return shifts
    */
   public ShiftContainer getShifts()
   {
      return m_shifts;
   }

   /**
    * Retrieves the shift periods available in this context.
    *
    * @return shift periods
    */
   public ShiftPeriodContainer getShiftPeriods()
   {
      return m_shiftPeriods;
   }

   /**
    * Retrieves the currencies available in this context.
    *
    * @return currencies
    */
   public CurrencyContainer getCurrencies()
   {
      return m_currencies;
   }

   /**
    * This method retrieves the  calendars available in this context.
    *
    * @return list of calendars
    */
   public ProjectCalendarContainer getCalendars()
   {
      return m_calendars;
   }

   /**
    * Retrieves a list of resources available in this context.
    *
    * @return list of all resources
    */
   public ResourceContainer getResources()
   {
      return m_resources;
   }

   /**
    * Retrieves a list of the projects represented in this context.
    * 
    * @return list of projects
    */
   public List<ProjectFile> getProjects()
   {
      return m_projects;
   }

   /**
    * Retrieve the time unit defaults to be used when working with projects in this context.
    *
    * @return time unit defaults
    */
   public TimeUnitDefaults getTimeUnitDefaults()
   {
      return m_timeUnitDefaults;
   }

   /**
    * Retrieve the MPXJ configuration to be used by projects in this context.
    *
    * @return MPXJ configuration
    */
   public ProjectConfig getProjectConfig()
   {
      return m_projectConfig;
   }

   /**
    * Add an error which has been ignored while reading context data.
    *
    * @param ex ignored error
    */
   public void addIgnoredError(Exception ex)
   {
      m_ignoredErrors.add(ex);
   }

   /**
    * Retrieve a list of errors ignored when reading context data.
    *
    * @return list of errors
    */
   public List<Exception> getIgnoredErrors()
   {
      return m_ignoredErrors;
   }

   /**
    * Retrieve the event manager for this project.
    *
    * @return event manager
    */
   public EventManager getEventManager()
   {
      return m_eventManager;
   }

   /**
    * Retrieve the ObjectSequence instance used to generate Unique ID values for a given class.
    *
    * @param c target class
    * @return ObjectSequence instance
    */
   @Override public ObjectSequence getUniqueIdObjectSequence(Class<?> c)
   {
      return m_uniqueIdObjectSequences.computeIfAbsent(c.getName(), x -> new ObjectSequence(1));
   }

   /**
    * Indicates if this container is responsible for managing instances of the supplied class.
    *
    * @param c class to test
    * @return true if this container manages instances of the supplied class
    */
   public static boolean contains(Class<?> c)
   {
      return HOSTED_CLASS_NAMES.contains(c.getName());
   }

   private final LocationContainer m_locations = new LocationContainer(this);
   private final UnitOfMeasureContainer m_unitsOfMeasure = new UnitOfMeasureContainer(this);
   private final ExpenseCategoryContainer m_expenseCategories = new ExpenseCategoryContainer(this);
   private final CostAccountContainer m_costAccounts = new CostAccountContainer(this);
   private final WorkContourContainer m_workContours = new WorkContourContainer(this);
   private final NotesTopicContainer m_notesTopics = new NotesTopicContainer(this);
   private final CustomFieldContainer m_customFields = new CustomFieldContainer();
   private final UserDefinedFieldContainer m_userDefinedFields = new UserDefinedFieldContainer(m_customFields);
   private final ActivityCodeContainer m_activityCodes = new ActivityCodeContainer(this);
   private final ProjectCodeContainer m_projectCodes = new ProjectCodeContainer(this);
   private final ResourceCodeContainer m_resourceCodes = new ResourceCodeContainer(this);
   private final RoleCodeContainer m_roleCodes = new RoleCodeContainer(this);
   private final ResourceAssignmentCodeContainer m_resourceAssignmentCodes = new ResourceAssignmentCodeContainer(this);
   private final ShiftContainer m_shifts = new ShiftContainer(this);
   private final ShiftPeriodContainer m_shiftPeriods = new ShiftPeriodContainer(this);
   private final CurrencyContainer m_currencies = new CurrencyContainer(this);
   private final ProjectCalendarContainer m_calendars = new ProjectCalendarContainer(this);
   private final ResourceContainer m_resources = new ResourceContainer(this);

   private final TimeUnitDefaults m_timeUnitDefaults = new TimeUnitDefaults();
   private final ProjectConfig m_projectConfig = new ProjectConfig();
   private final Map<String, ObjectSequence> m_uniqueIdObjectSequences = new HashMap<>();
   private final List<Exception> m_ignoredErrors = new ArrayList<>();
   private final EventManager m_eventManager = new EventManager();

   private final List<ProjectFile> m_projects = new ArrayList<>();

   private static final Set<String> HOSTED_CLASS_NAMES = new HashSet<>(
      Arrays.asList(
         Location.class.getName(),
         UnitOfMeasure.class.getName(),
         ExpenseCategory.class.getName(),
         CostAccount.class.getName(),
         WorkContour.class.getName(),
         NotesTopic.class.getName(),
         UserDefinedField.class.getName(),
         ActivityCode.class.getName(),
         ActivityCodeValue.class.getName(),
         ProjectCode.class.getName(),
         ProjectCodeValue.class.getName(),
         ResourceCode.class.getName(),
         ResourceCodeValue.class.getName(),
         RoleCode.class.getName(),
         RoleCodeValue.class.getName(),
         ResourceAssignmentCode.class.getName(),
         ResourceAssignmentCodeValue.class.getName(),
         Shift.class.getName(),
         ShiftPeriod.class.getName(),
         Currency.class.getName(),
         ProjectCalendar.class.getName(),
         Resource.class.getName()));
}
