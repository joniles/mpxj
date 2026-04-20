/*
 * file:       DataAtOffset.java
 * author:     Jon Iles
 * date:       2026-04-19
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

package org.mpxj.mpp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.mpxj.Duration;
import org.mpxj.TimeUnit;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.LocalDateHelper;

/**
 * Class used to assist understanding data storage in an MPP file.
 * Given a byte array and an offset, generate the different values
 * which that data might represent.
 */
public class DataAtOffset
{
   /**
    * Constructor.
    *
    * @param data bye array
    * @param offset offset into byte array
    */
   public DataAtOffset(byte[] data, int offset)
   {
      //
      // 1 byte
      //
      m_workTimeUnit = MPPUtility.getWorkTimeUnits(MPPUtility.getByte(data,  offset));

      //
      // 2 bytes
      //
      if (offset + 2 <= data.length)
      {
         m_short = Integer.valueOf(ByteArrayHelper.getShort(data, offset));
         m_timeUnit = MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(data, offset));
         m_percentage = MPPUtility.getPercentage(data,  offset);
         m_date = MPPUtility.getDate(data, offset);
         m_time = MPPUtility.getTime(data,  offset);
      }
      else
      {
         m_short = null;
         m_timeUnit = null;
         m_percentage = null;
         m_date = null;
         m_time = null;
      }

      //
      // 4 bytes
      //
      if (offset + 4 <= data.length)
      {
         m_timestamp = MPPUtility.getTimestamp(data,  offset);
      }
      else
      {
         m_timestamp = null;
      }

      //
      // 6 bytes
      //
      if (offset + 6 <= data.length)
      {
         m_longSix = Long.valueOf(MPPUtility.getLong6(data, offset));
      }
      else
      {
         m_longSix = null;
      }

      //
      // 8 bytes
      //
      if (offset + 8 <= data.length)
      {
         m_long = Long.valueOf(ByteArrayHelper.getLong(data, offset));
         m_double = Double.valueOf(MPPUtility.getDouble(data,  offset));
         m_duration = Duration.getInstance(MPPUtility.getDouble(data, offset) / 60000, TimeUnit.HOURS);
      }
      else
      {
         m_long = null;
         m_double = null;
         m_duration = null;
      }

      //
      // 16 bytes
      //
      if (offset + 16 <= data.length)
      {
         m_guid = MPPUtility.getGUID(data, offset);
      }
      else
      {
         m_guid = null;
      }
   }

   /**
    * Retrieve a short value.
    *
    * @return short value
    */
   public Integer getShort()
   {
      return m_short;
   }

   /**
    * Retrieve a time unit value.
    *
    * @return time unit value
    */
   public TimeUnit getTimeUnit()
   {
      return m_timeUnit;
   }

   /**
    * Retrieve a percentage value.
    *
    * @return percentage value
    */
   public Double getPercentage()
   {
      return m_percentage;
   }

   /**
    * Retrieve a date value.
    *
    * @return date value
    */
   public LocalDate getDate()
   {
      return m_date;
   }

   /**
    * Retrieve a time value.
    *
    * @return time value
    */
   public LocalTime getTime()
   {
      return m_time;
   }

   /**
    * Retrieve a work time unit value.
    *
    * @return work time unit value
    */
   public TimeUnit getWorkTimeUnit()
   {
      return m_workTimeUnit;
   }

   /**
    * Retrieve a timestamp value.
    *
    * @return timestamp value
    */
   public LocalDateTime getTimestamp()
   {
      return m_timestamp;
   }

   /**
    * Retrieve a six byte long value.
    *
    * @return six byte long value
    */
   public Long getLongSix()
   {
      return m_longSix;
   }

   /**
    * Retrieve a long value.
    *
    * @return long value
    */
   public Long getLong()
   {
      return m_long;
   }

   /**
    * Retrieve a double value.
    *
    * @return double value
    */
   public Double getDouble()
   {
      return m_double;
   }

   /**
    * Retrieve a duration value.
    *
    * @return duration value
    */
   public Duration getDuration()
   {
      return m_duration;
   }

   /**
    * Retrieve a UUID value.
    *
    * @return UUID value
    */
   public UUID getGuid()
   {
      return m_guid;
   }

   private final Integer m_short;
   private final TimeUnit m_timeUnit;
   private final Double m_percentage;
   private final LocalDate m_date;
   private final LocalTime m_time;
   private final TimeUnit m_workTimeUnit;
   private final LocalDateTime m_timestamp;
   private final Long m_longSix;
   private final Long m_long;
   private final Double m_double;
   private final Duration m_duration;
   private final UUID m_guid;
}