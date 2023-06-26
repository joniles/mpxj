
package net.sf.mpxj;

import java.time.LocalDate;

import net.sf.mpxj.common.LocalDateHelper;

/**
 * This class represents a period of time.
 */
public final class LocalDateRange implements Comparable<LocalDateRange>
{
   /**
    * Constructor.
    *
    * @param startDate start date
    * @param endDate end date
    */
   public LocalDateRange(LocalDate startDate, LocalDate endDate)
   {
      m_start = startDate;
      m_end = endDate;
   }

   /**
    * Retrieve the date at the start of the range.
    *
    * @return start date
    */
   public LocalDate getStart()
   {
      return m_start;
   }

   /**
    * Retrieve the date at the end of the range.
    *
    * @return end date
    */
   public LocalDate getEnd()
   {
      return m_end;
   }

   /**
    * This method compares a target date with a date range. The method will
    * return 0 if the date is within the range, less than zero if the date
    * is before the range starts, and greater than zero if the date is after
    * the range ends.
    *
    * @param date target date
    * @return comparison result
    */
   public int compareTo(LocalDate date)
   {
      return LocalDateHelper.compare(m_start, m_end, date);
   }

   @Override public int compareTo(LocalDateRange o)
   {
      int result = LocalDateHelper.compare(m_start, o.m_start);
      if (result == 0)
      {
         result = LocalDateHelper.compare(m_end, o.m_end);
      }
      return result;
   }

   @Override public boolean equals(Object o)
   {
      boolean result = false;
      if (o instanceof LocalDateRange)
      {
         LocalDateRange rhs = (LocalDateRange) o;
         result = (compareTo(rhs) == 0);
      }
      return result;
   }

   @Override public int hashCode()
   {
      long start = m_start == null ? 0 : m_start.toEpochDay();
      long end = m_end == null ? 0 : m_end.toEpochDay();
      return ((int) start ^ (int) (start >> 32)) ^ ((int) end ^ (int) (end >> 32));
   }

   @Override public String toString()
   {
      return ("[DateRange start=" + m_start + " end=" + m_end + "]");
   }

   private final LocalDate m_start;
   private final LocalDate m_end;
}
