/*
 * file:       TimephasedItem.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       25/10/2008
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

package org.mpxj;

import java.time.LocalDateTime;

/**
 * This class represents an amount, spread over a period of time.
 *
 * @param <T> payload type
 */
public abstract class TimephasedItem<T>
{
   /**
    * Retrieve the start date.
    *
    * @return start date
    */
   public LocalDateTime getStart()
   {
      return m_start;
   }

   /**
    * Set the start date.
    *
    * @param start start date
    */
   public void setStart(LocalDateTime start)
   {
      m_start = start;
   }

   /**
    * Retrieve the amount per day.
    *
    * @return amount per day
    */
   public T getAmountPerDay()
   {
      return m_amountPerDay;
   }

   /**
    * Set the amount per day.
    *
    * @param amountPerDay amount per day
    */
   public void setAmountPerDay(T amountPerDay)
   {
      m_amountPerDay = amountPerDay;
   }

   /**
    * Retrieve the modified flag.
    *
    * @return modified flag
    */
   public boolean getModified()
   {
      return m_modified;
   }

   /**
    * Set the modified flag.
    *
    * @param modified modified flag
    */
   public void setModified(boolean modified)
   {
      m_modified = modified;
   }

   /**
    * Retrieve the total amount.
    *
    * @return total amount
    */
   public T getTotalAmount()
   {
      return m_totalAmount;
   }

   /**
    * Set the total amount.
    *
    * @param totalAmount total amount
    */
   public void setTotalAmount(T totalAmount)
   {
      m_totalAmount = totalAmount;
   }

   /**
    * Retrieve the finish date.
    *
    * @return finish date
    */
   public LocalDateTime getFinish()
   {
      return m_finish;
   }

   /**
    * Set the finish date.
    *
    * @param finish finish date
    */
   public void setFinish(LocalDateTime finish)
   {
      m_finish = finish;
   }

   @Override public String toString()
   {
      return "[TimephasedItem start=" + m_start + " totalAmount=" + m_totalAmount + " finish=" + m_finish + " amountPerDay=" + m_amountPerDay + " modified=" + m_modified + "]";
   }

   @SuppressWarnings("unchecked") @Override public boolean equals(Object o)
   {
      boolean result = false;

      if (o instanceof TimephasedItem<?>)
      {
         TimephasedItem<T> t = (TimephasedItem<T>) o;
         result = m_start.equals(t.m_start) && m_finish.equals(t.m_finish) && m_totalAmount.equals(t.m_totalAmount) && m_amountPerDay.equals(t.m_amountPerDay);
      }

      return result;
   }

   @Override public int hashCode()
   {
      return m_start.hashCode() + m_finish.hashCode() + m_totalAmount.hashCode() + m_amountPerDay.hashCode();
   }

   private LocalDateTime m_start;
   private T m_totalAmount;
   private LocalDateTime m_finish;
   private T m_amountPerDay;
   private boolean m_modified;
}
