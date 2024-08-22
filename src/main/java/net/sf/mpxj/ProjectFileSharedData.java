/*
 * file:       ProjectFileSharedData.java
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

package net.sf.mpxj;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

import net.sf.mpxj.common.ObjectSequence;

/**
 * Implements a container for common data which can be shared across multiple ProjectFile instances.
 */
public class ProjectFileSharedData implements UniqueIdObjectSequenceProvider
{
   /**
    * Retrieve the locations available for this schedule.
    *
    * @return locations
    */
   public LocationContainer getLocations()
   {
      return m_locations;
   }

   /**
    * Retrieve the units of measure available for this schedule.
    *
    * @return units of measure
    */
   public UnitOfMeasureContainer getUnitsOfMeasure()
   {
      return m_unitsOfMeasure;
   }

   /**
    * Retrieves the expense categories available for this schedule.
    *
    * @return expense categories
    */
   public ExpenseCategoryContainer getExpenseCategories()
   {
      return m_expenseCategories;
   }

   /**
    * Retrieves the cost accounts available for this schedule.
    *
    * @return cost accounts
    */
   public CostAccountContainer getCostAccounts()
   {
      return m_costAccounts;
   }

   /**
    * Retrieves the work contours available for this schedule.
    *
    * @return work contours
    */
   public WorkContourContainer getWorkContours()
   {
      return m_workContours;
   }

   /**
    * Retrieves the notes topics available for this schedule.
    *
    * @return notes topics
    */
   public NotesTopicContainer getNotesTopics()
   {
      return m_notesTopics;
   }

   /**
    * Retrieves the custom fields for this project.
    *
    * @return custom fields
    */
   public CustomFieldContainer getCustomFields()
   {
      return m_customFields;
   }

   /**
    * Retrieves the user defined fields available for this schedule.
    *
    * @return user defined fields
    */
   public UserDefinedFieldContainer getUserDefinedFields()
   {
      return m_userDefinedFields;
   }

   /**
    * Retrieves the activity code configuration for this project.
    *
    * @return activity codes
    */
   public ActivityCodeContainer getActivityCodes()
   {
      return m_activityCodes;
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
   private final Map<String, ObjectSequence> m_uniqueIdObjectSequences = new HashMap<>();

   private static final Set<String> HOSTED_CLASS_NAMES = new HashSet<>(
      Arrays.asList(
         Location.class.getName(),
         UnitOfMeasure.class.getName(),
         ExpenseCategory.class.getName(),
         CostAccount.class.getName(),
         WorkContour.class.getName(),
         NotesTopic.class.getName(),
         UserDefinedField.class.getName(),
         ActivityCode.class.getName()));
}
