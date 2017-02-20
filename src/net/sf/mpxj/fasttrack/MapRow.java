
package net.sf.mpxj.fasttrack;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import net.sf.mpxj.Duration;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.NumberHelper;

/**
 * Implementation of the Row interface, wrapping a Map.
 */
class MapRow implements Row
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
    * {@inheritDoc}
    */
   @Override public String getString(String name)
   {
      return (String) getObject(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Integer getInteger(String name)
   {
      return (Integer) getObject(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Double getDouble(String name)
   {
      return (Double) getObject(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Double getCurrency(String name)
   {
      return getDouble(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override public boolean getBoolean(String name)
   {
      boolean result = false;
      Object value = getObject(name);
      if (value != null)
      {
         result = BooleanHelper.getBoolean((Boolean) value);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override public int getInt(String name)
   {
      return (NumberHelper.getInt((Number) getObject(name)));
   }

   @Override public Date getTimestamp(String dateName, String timeName)
   {
      Date result = null;
      Date date = getDate(dateName);
      if (date != null)
      {
         Calendar dateCal = Calendar.getInstance();
         dateCal.setTime(date);

         Date time = getDate(timeName);
         if (time != null)
         {
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(time);
            dateCal.set(Calendar.HOUR, timeCal.get(Calendar.HOUR));
            dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
            dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
            dateCal.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));
         }

         result = dateCal.getTime();
      }

      return result;
   }

   @Override public Date getDate(String name)
   {
      return (Date) getObject(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Duration getDuration(String name)
   {
      return (Duration) getObject(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Duration getWork(String name)
   {
      return (Duration) getObject(name);
   }

   /**
    * Retrieve a value from the map.
    *
    * @param name column name
    * @return column value
    */
   public Object getObject(String name)
   {
      Object result = m_map.get(name);
      return (result);
   }

   /**
    * {@inheritDoc}
    */
   @Override public UUID getUUID(String name)
   {
      String value = getString(name);
      return value == null ? null : UUID.fromString(value.substring(1, value.length() - 1));
   }

   /**
    * {@inheritDoc}
    */
   //   @Override public RelationType getRelationType(String name)
   //   {
   //      RelationType result;
   //      int type = getInt(name);
   //
   //      switch (type)
   //      {
   //         case 1:
   //         {
   //            result = RelationType.START_START;
   //            break;
   //         }
   //
   //         case 2:
   //         {
   //            result = RelationType.FINISH_FINISH;
   //            break;
   //         }
   //
   //         case 3:
   //         {
   //            result = RelationType.START_FINISH;
   //            break;
   //         }
   //
   //         case 0:
   //         default:
   //         {
   //            result = RelationType.FINISH_START;
   //            break;
   //         }
   //      }
   //
   //      return result;
   //   }

   /**
    * {@inheritDoc}
    */
   //   @Override public ResourceType getResourceType(String name)
   //   {
   //      ResourceType result;
   //      Integer value = getInteger(name);
   //      if (value == null)
   //      {
   //         result = ResourceType.WORK;
   //      }
   //      else
   //      {
   //         if (value.intValue() == 1)
   //         {
   //            result = ResourceType.MATERIAL;
   //         }
   //         else
   //         {
   //            result = ResourceType.WORK;
   //         }
   //      }
   //
   //      return result;
   //   }

   /**
    * Retrieve the internal Map instance used to hold row data.
    *
    * @return Map instance
    */
   public Map<String, Object> getMap()
   {
      return m_map;
   }

   //   /**
   //    * {@inheritDoc}
   //    */
   //   @Override public Day getDay(String name)
   //   {
   //      Day result = null;
   //      Integer value = getInteger(name);
   //      if (value != null)
   //      {
   //         result = Day.getInstance(value.intValue() + 1);
   //      }
   //      return result;
   //   }

   protected Map<String, Object> m_map;

   /**
    * 01/01/2001 00:00.
    */
   private static final long TIMESTAMP_EPOCH = 978307200000L;

   /**
    * 07/01/2001 00:00.
    */
   private static final long DATE_EPOCH = 978825600000L;
}
