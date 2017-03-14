
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
class MapRow
{
   /**
    * Constructor.
    *
    * @param table parent table
    * @param map map to be wrapped by this instance
    */
   public MapRow(FastTrackTable table, Map<FastTrackField, Object> map)
   {
      m_table = table;
      m_map = map;
   }

   public String getString(FastTrackField name)
   {
      return (String) getObject(name);
   }

   public Integer getInteger(FastTrackField name)
   {
      return (Integer) getObject(name);
   }

   public Double getDouble(FastTrackField name)
   {
      return (Double) getObject(name);
   }

   public Double getCurrency(FastTrackField name)
   {
      return getDouble(name);
   }

   public boolean getBoolean(FastTrackField name)
   {
      boolean result = false;
      Object value = getObject(name);
      if (value != null)
      {
         result = BooleanHelper.getBoolean((Boolean) value);
      }
      return result;
   }

   public int getInt(FastTrackField name)
   {
      return (NumberHelper.getInt((Number) getObject(name)));
   }

   public Date getTimestamp(FastTrackField dateName, FastTrackField timeName)
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
            dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
            dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
            dateCal.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));
         }

         result = dateCal.getTime();
      }

      return result;
   }

   public Date getDate(FastTrackField name)
   {
      return (Date) getObject(name);
   }

   public Duration getDuration(FastTrackField name)
   {
      Double value = (Double) getObject(name);
      return value == null ? null : Duration.getInstance(value.doubleValue(), m_table.getDurationTimeUnit());
   }

   public Duration getWork(FastTrackField name)
   {
      Double value = (Double) getObject(name);
      return value == null ? null : Duration.getInstance(value.doubleValue(), m_table.getWorkTimeUnit());
   }

   /**
    * Retrieve a value from the map.
    *
    * @param name column name
    * @return column value
    */
   public Object getObject(FastTrackField name)
   {
      Object result = m_map.get(name);
      return (result);
   }

   public UUID getUUID(FastTrackField name)
   {
      String value = getString(name);
      return value == null || value.isEmpty() ? null : UUID.fromString(value.substring(1, value.length() - 1));
   }

   /**
    * Retrieve the internal Map instance used to hold row data.
    *
    * @return Map instance
    */
   public Map<FastTrackField, Object> getMap()
   {
      return m_map;
   }

   protected final Map<FastTrackField, Object> m_map;
   private final FastTrackTable m_table;
}
