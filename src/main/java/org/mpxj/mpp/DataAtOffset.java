package org.mpxj.mpp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.mpxj.Duration;
import org.mpxj.TimeUnit;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.LocalDateHelper;

public class DataAtOffset
{
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
         m_date = LocalDateHelper.getLocalDate(MPPUtility.getDate(data, offset));
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

   public Integer getShort()
   {
      return m_short;
   }

   public TimeUnit getTimeUnit()
   {
      return m_timeUnit;
   }

   public Double getPercentage()
   {
      return m_percentage;
   }

   public LocalDate getDate()
   {
      return m_date;
   }

   public LocalTime getTime()
   {
      return m_time;
   }

   public TimeUnit getWorkTimeUnit()
   {
      return m_workTimeUnit;
   }

   public LocalDateTime getTimestamp()
   {
      return m_timestamp;
   }

   public Long getLongSix()
   {
      return m_longSix;
   }

   public Long getLong()
   {
      return m_long;
   }

   public Double getDouble()
   {
      return m_double;
   }

   public Duration getDuration()
   {
      return m_duration;
   }

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