/*
 * file:       CostRateTableEntry.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2009
 * date:       08/06/2009
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

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;

/**
 * This class represents a row from a resource's cost rate table.
 * Note that MS Project always represents costs as an hourly rate,
 * it holds an additional field to indicate the format used when
 * displaying the rate.
 */
public final class CostRateTableEntry implements Comparable<CostRateTableEntry>
{
   /**
    * Constructor. Used to construct singleton default table entry.
    */
   private CostRateTableEntry()
   {
      this(new Rate(0, TimeUnit.HOURS), TimeUnit.HOURS, new Rate(0, TimeUnit.HOURS), TimeUnit.HOURS, NumberHelper.DOUBLE_ZERO, DateHelper.START_DATE_NA, DateHelper.END_DATE_NA);
   }

   /**
    * Constructor.
    *
    * @param standardRate standard rate
    * @param standardRateFormat standard rate format
    * @param overtimeRate overtime rate
    * @param overtimeRateFormat overtime rate format
    * @param costPerUse cost per use
    * @param startDate start date
    * @param endDate end date
    */
   @Deprecated public CostRateTableEntry(Rate standardRate, TimeUnit standardRateFormat, Rate overtimeRate, TimeUnit overtimeRateFormat, Number costPerUse, Date startDate, Date endDate)
   {
      this(startDate, endDate, costPerUse, standardRate, overtimeRate);
   }

   /**
    * Constructor.
    *
    * @param startDate    start date
    * @param endDate      end date
    * @param costPerUse   cost per use
    * @param rates Rate instances
    */
   public CostRateTableEntry(Date startDate, Date endDate, Number costPerUse, Rate... rates)
   {
      m_startDate = startDate;
      m_endDate = endDate;
      m_costPerUse = costPerUse;
      System.arraycopy(rates, 0, m_rates, 0, rates.length);
   }

   /**
    * Retrieves the start date at which this table entry is valid.
    *
    * @return start date
    */
   public Date getStartDate()
   {
      return m_startDate;
   }

   /**
    * Retrieves the end date after which this table entry is not valid.
    *
    * @return end date
    */
   public Date getEndDate()
   {
      return m_endDate;
   }

   /**
    * Retrieve the rate with the specified index.
    *
    * @param index rate index
    * @return Rate instance
    */
   public Rate getRate(int index)
   {
      return m_rates[index];
   }

   /**
    * Retrieves the standard rate represented by this entry.
    *
    * @return standard rate
    */
   public Rate getStandardRate()
   {
      return getRate(0);
   }

   /**
    * Retrieves the format used when displaying the standard rate.
    *
    * @return standard rate format
    */
   @Deprecated public TimeUnit getStandardRateFormat()
   {
      return getStandardRate().getUnits();
   }

   /**
    * Retrieves the overtime rate represented by this entry.
    *
    * @return overtime rate
    */
   public Rate getOvertimeRate()
   {
      return getRate(1);
   }

   /**
    * Retrieves the format used when displaying the overtime rate.
    *
    * @return overtime rate format
    */
   @Deprecated public TimeUnit getOvertimeRateFormat()
   {
      return getOvertimeRate().getUnits();
   }

   /**
    * Retrieves the cost per use represented by this entry.
    *
    * @return per use rate
    */
   public Number getCostPerUse()
   {
      return m_costPerUse;
   }

   @Override public int compareTo(CostRateTableEntry o)
   {
      return DateHelper.compare(m_endDate, o.m_endDate);
   }

   @Override public String toString()
   {
      String rates = Stream.of(m_rates).map(String::valueOf).collect(Collectors.joining(", "));
      return "[CostRateTableEntry startDate=" + m_startDate + " endDate=" + m_endDate + " costPerUse=" + m_costPerUse + " rates=" + rates + "]";
   }

   private final Date m_startDate;
   private final Date m_endDate;
   private final Number m_costPerUse;
   private final Rate[] m_rates = new Rate[MAX_RATES];
   public static final CostRateTableEntry DEFAULT_ENTRY = new CostRateTableEntry();

   public static final int MAX_RATES = 5;
}
