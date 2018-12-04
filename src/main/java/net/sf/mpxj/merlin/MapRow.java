/*
 * file:       MapRow.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2016
 * date:       17/11/2016
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

package net.sf.mpxj.merlin;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.DateHelper;
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
      Object value = getObject(name);
      String result;
      if (value instanceof byte[])
      {
         result = new String((byte[]) value);
      }
      else
      {
         result = (String) value;
      }
      return (result);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Integer getInteger(String name)
   {
      Object result = getObject(name);
      if (result != null)
      {
         if (result instanceof Integer == false)
         {
            result = Integer.valueOf(((Number) result).intValue());
         }
      }
      return ((Integer) result);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Double getDouble(String name)
   {
      Object result = getObject(name);
      if (result != null)
      {
         if (!(result instanceof Double))
         {
            if (result instanceof byte[])
            {
               result = Double.valueOf(new String((byte[]) result));
            }
            else
            {
               result = Double.valueOf(((Number) result).doubleValue());
            }
         }
      }
      return ((Double) result);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Double getCurrency(String name)
   {
      Double value = getDouble(name);
      if (value != null)
      {
         value = Double.valueOf(value.doubleValue() / 100);
      }
      return (value);
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
         if (value instanceof Boolean)
         {
            result = BooleanHelper.getBoolean((Boolean) value);
         }
         else
         {
            result = (((Number) value).intValue() == 1);
         }
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

   /**
    * {@inheritDoc}
    */
   @Override public Date getTimestamp(String name)
   {
      Date result;
      // They are stored as seconds since Jan 1st, 2001 00:00
      Integer value = getInteger(name);
      if (value == null)
      {
         result = null;
      }
      else
      {
         result = new Date(TIMESTAMP_EPOCH + (value.longValue() * 1000));
      }
      return result;
   }

   @Override public Date getDate(String name)
   {
      Date result;
      // They are stored as days since Jan 7th, 2001 00:00
      Integer value = getInteger(name);
      if (value == null)
      {
         result = null;
      }
      else
      {
         Calendar cal = DateHelper.popCalendar(DATE_EPOCH);
         cal.add(Calendar.DAY_OF_YEAR, value.intValue());
         result = cal.getTime();
         DateHelper.pushCalendar(cal);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override public Duration getDuration(String name)
   {
      Duration result;
      String value = getString(name);
      if (value == null)
      {
         result = null;
      }
      else
      {
         result = parseDuration(value);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override public Duration getWork(String name)
   {
      Duration result;
      String value = getString(name);
      if (value == null)
      {
         result = null;
      }
      else
      {
         result = parseDuration(value);
      }
      return result;
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
      value = value.replace("-", "+").replace("_", "/");

      byte[] data = javax.xml.bind.DatatypeConverter.parseBase64Binary(value + "==");
      long msb = 0;
      long lsb = 0;

      for (int i = 0; i < 8; i++)
      {
         msb = (msb << 8) | (data[i] & 0xff);
      }

      for (int i = 8; i < 16; i++)
      {
         lsb = (lsb << 8) | (data[i] & 0xff);
      }

      return new UUID(msb, lsb);
   }

   /**
    * {@inheritDoc}
    */
   @Override public RelationType getRelationType(String name)
   {
      RelationType result;
      int type = getInt(name);

      switch (type)
      {
         case 1:
         {
            result = RelationType.START_START;
            break;
         }

         case 2:
         {
            result = RelationType.FINISH_FINISH;
            break;
         }

         case 3:
         {
            result = RelationType.START_FINISH;
            break;
         }

         case 0:
         default:
         {
            result = RelationType.FINISH_START;
            break;
         }
      }

      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override public ResourceType getResourceType(String name)
   {
      ResourceType result;
      Integer value = getInteger(name);
      if (value == null)
      {
         result = ResourceType.WORK;
      }
      else
      {
         if (value.intValue() == 1)
         {
            result = ResourceType.MATERIAL;
         }
         else
         {
            result = ResourceType.WORK;
         }
      }

      return result;
   }

   /**
    * Retrieve the internal Map instance used to hold row data.
    *
    * @return Map instance
    */
   public Map<String, Object> getMap()
   {
      return m_map;
   }

   /**
    * Convert the string representation of a duration to a Duration instance.
    *
    * @param value string representation of a duration
    * @return Duration instance
    */
   private Duration parseDuration(String value)
   {
      //
      // Let's assume that we always receive well-formed values.
      //
      int unitsLength = 1;
      char unitsChar = value.charAt(value.length() - unitsLength);

      //
      // Handle an estimated duration
      //
      if (unitsChar == '?')
      {
         unitsLength = 2;
         unitsChar = value.charAt(value.length() - unitsLength);
      }

      double durationValue = Double.parseDouble(value.substring(0, value.length() - unitsLength));

      //
      // Note that we don't handle 'u' the material type here
      //
      TimeUnit durationUnits;
      switch (unitsChar)
      {
         case 's':
         {
            durationUnits = TimeUnit.MINUTES;
            durationValue /= 60;
            break;
         }

         case 'm':
         {
            durationUnits = TimeUnit.MINUTES;
            break;
         }

         case 'h':
         {
            durationUnits = TimeUnit.HOURS;
            break;
         }

         case 'w':
         {
            durationUnits = TimeUnit.WEEKS;
            break;
         }

         case 'M':
         {
            durationUnits = TimeUnit.MONTHS;
            break;
         }

         case 'q':
         {
            durationUnits = TimeUnit.MONTHS;
            durationValue *= 3;
            break;
         }

         case 'y':
         {
            durationUnits = TimeUnit.YEARS;
            break;
         }

         case 'f':
         {
            durationUnits = TimeUnit.PERCENT;
            break;
         }

         case 'd':
         default:
         {
            durationUnits = TimeUnit.DAYS;
            break;
         }
      }

      return Duration.getInstance(durationValue, durationUnits);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Day getDay(String name)
   {
      Day result = null;
      Integer value = getInteger(name);
      if (value != null)
      {
         result = Day.getInstance(value.intValue() + 1);
      }
      return result;
   }

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
