/*
 * file:       TimescaleTier.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Apr 7, 2005
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

/**
 * This class collects together the properties which represent a
 * single tier of the timescale on a Gantt chart.
 */
public final class TimescaleTier
{
   /**
    * Retrieves the tier count.
    *
    * @return tier count
    */
   public int getCount()
   {
      return (m_count);
   }

   /**
    * Sets the tier count.
    *
    * @param count tier count
    */
   public void setCount(int count)
   {
      m_count = count;
   }

   /**
    * Retrieves the tier label format.
    *
    * @return tier label format
    */
   public TimescaleFormat getFormat()
   {
      return (m_format);
   }

   /**
    * Sets the tier label format.
    *
    * @param format tier label format
    */
   public void setFormat(TimescaleFormat format)
   {
      m_format = format;
   }

   /**
    * Retrieves the tick lines flag.
    *
    * @return tick lines flag
    */
   public boolean getTickLines()
   {
      return (m_tickLines);
   }

   /**
    * Sets the tick lines flag.
    *
    * @param tickLines tick lines flag
    */
   public void setTickLines(boolean tickLines)
   {
      m_tickLines = tickLines;
   }

   /**
    * Retrieves the timescale units.
    *
    * @return timescale units
    */
   public TimescaleUnits getUnits()
   {
      return (m_units);
   }

   /**
    * Sets the timescale units.
    *
    * @param units timescale units
    */
   public void setUnits(TimescaleUnits units)
   {
      m_units = units;
   }

   /**
    * Retrieves the uses fiscal year flag.
    *
    * @return uses fiscal year flag
    */
   public boolean getUsesFiscalYear()
   {
      return (m_usesFiscalYear);
   }

   /**
    * Sets the uses fiscal year flag.
    *
    * @param usesFiscalYear uses fiscal year flag
    */
   public void setUsesFiscalYear(boolean usesFiscalYear)
   {
      m_usesFiscalYear = usesFiscalYear;
   }

   /**
    * Retrieve the timescale label alignment.
    *
    * @return label alignment
    */
   public TimescaleAlignment getAlignment()
   {
      return (m_alignment);
   }

   /**
    * Set the timescale label alignment.
    *
    * @param alignment label alignment
    */
   public void setAlignment(TimescaleAlignment alignment)
   {
      m_alignment = alignment;
   }

   /**
    * Generate a string representation of this instance.
    *
    * @return string representation of this instance
    */
   @Override public String toString()
   {
      return ("[TimescaleTier UsesFiscalYear=" + m_usesFiscalYear + " TickLines=" + m_tickLines + " Units=" + m_units + " Count=" + m_count + " Format=[" + m_format + "] Alignment=" + m_alignment + "]");
   }

   private boolean m_usesFiscalYear;
   private boolean m_tickLines;
   private TimescaleUnits m_units;
   private int m_count;
   private TimescaleFormat m_format;
   private TimescaleAlignment m_alignment;
}
