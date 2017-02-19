
package net.sf.mpxj.fasttrack;

import java.util.Date;
import java.util.UUID;

import net.sf.mpxj.Duration;

/**
 * This interface represents a row in a database table. It is envisaged that
 * rows could be retrieved from a database either via Jackcess, in which case
 * the row data will be in the form of a Map, or from a JDBC data source,
 * in which case the row will be in the form of a Result set. Classes that
 * implement this interface will wrap one of these types to provide a consistent
 * interface to MPXJ.
 */
interface Row
{
   /**
    * Retrieve a string attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public String getString(String name);

   /**
    * Retrieve an Integer attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Integer getInteger(String name);

   /**
    * Retrieve a Double attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Double getDouble(String name);

   /**
    * Retrieve a currency attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Double getCurrency(String name);

   /**
    * Retrieve a boolean attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public boolean getBoolean(String name);

   /**
    * Retrieve an in attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public int getInt(String name);

   /**
    * Retrieve a timestamp attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   //public Date getTimestamp(String name);

   /**
    * Retrieve a duration attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Duration getDuration(String name);

   /**
    * Retrieve a duration attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Duration getWork(String name);

   /**
    * Retrieve a UUID attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public UUID getUUID(String name);

   /**
    * Retrieve a relation type attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   //   public RelationType getRelationType(String name);

   /**
    * Retrieve a day type attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   //public Day getDay(String name);

   /**
    * Retrieve a date attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Date getDate(String name);

   /**
    * Retrieve a resource type attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   //public ResourceType getResourceType(String name);
}
