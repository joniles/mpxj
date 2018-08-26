
package net.sf.mpxj.synchro;

import java.util.Date;
import java.util.Map;

import net.sf.mpxj.Duration;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.common.BooleanHelper;

/**
 * Wraps a simple map which contains name value
 * pairs representing the column values
 * from an individual row. Provides type-specific
 * methods to retrieve the column values.
 */
class MapRow
{
   /**
    * Constructor.
    *
    * @param map map to be wrapped by this instance
    */
   public MapRow(Map<String, Object> map)
   {
      m_map = map;
   }

   /**
    * Retrieve a string value.
    *
    * @param name column name
    * @return string value
    */
   public final String getString(String name)
   {
      return (String) getObject(name);
   }

   /**
    * Retrieve an integer value.
    *
    * @param name column name
    * @return integer value
    */
   public final Integer getInteger(String name)
   {
      return (Integer) getObject(name);
   }

   /**
    * Retrieve a relation type value.
    *
    * @param name column name
    * @return relation type value
    */
   public final RelationType getRelationType(String name)
   {
      return (RelationType) getObject(name);
   }

   /**
    * Retrieve a boolean value.
    *
    * @param name column name
    * @return boolean value
    */
   public final boolean getBoolean(String name)
   {
      boolean result = false;
      Boolean value = (Boolean) getObject(name);
      if (value != null)
      {
         result = BooleanHelper.getBoolean(value);
      }
      return result;
   }

   /**
    * Retrieve a duration value.
    *
    * @param name column name
    * @return duration value
    */
   public final Duration getDuration(String name)
   {
      return (Duration) getObject(name);
   }

   /**
    * Retrieve a date value.
    *
    * @param name column name
    * @return date value
    */
   public final Date getDate(String name)
   {
      return (Date) getObject(name);
   }

   /**
    * Retrieve a value without being specific about its type.
    *
    * @param name column name
    * @return value
    */
   private final Object getObject(String name)
   {
      return m_map.get(name);
   }

   protected Map<String, Object> m_map;
}
