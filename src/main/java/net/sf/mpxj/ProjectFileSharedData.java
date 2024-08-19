package net.sf.mpxj;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.Arrays;

import net.sf.mpxj.common.ObjectSequence;

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
    * Retrieve the ObjectSequence instance used to generate Unique ID values for a given class.
    *
    * @param c target class
    * @return ObjectSequence instance
    */
   @Override public ObjectSequence getUniqueIdObjectSequence(Class<?> c)
   {
      return m_uniqueIdObjectSequences.computeIfAbsent(c.getName(), x -> new ObjectSequence(1));
   }

   private final LocationContainer m_locations = new LocationContainer(this);
   private final UnitOfMeasureContainer m_unitsOfMeasure = new UnitOfMeasureContainer(this);
   private final ExpenseCategoryContainer m_expenseCategories = new ExpenseCategoryContainer(this);
   private final Map<String, ObjectSequence> m_uniqueIdObjectSequences = new HashMap<>();

   public static final Set<String> HOSTED_CLASS_NAMES = new HashSet<>(Arrays.asList(Location.class.getName(), UnitOfMeasure.class.getName(), ExpenseCategory.class.getName()));
}
