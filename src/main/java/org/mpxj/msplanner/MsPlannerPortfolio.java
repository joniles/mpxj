/*
 * file:       MsPlannerPortfolio.java
 * author:     Jon Iles
 * date:       2026-04-03
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

package org.mpxj.msplanner;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a Microsoft Planner portfolio. Returned when portfolios in Microsoft Planner are listed.
 */
public class MsPlannerPortfolio
{
   /**
    * Constructor.
    */
   private MsPlannerPortfolio(Builder builder)
   {
      m_portfolioId = builder.m_portfolioId;
      m_portfolioName = builder.m_portfolioName;
      m_modifiedOn = builder.m_modifiedOn;
      m_createdOn = builder.m_createdOn;
   }

   /**
    * Retrieve the portfolio's unique ID.
    *
    * @return unique ID
    */
   public UUID getPortfolioId()
   {
      return m_portfolioId;
   }

   /**
    * Retrieve the portfolio's name.
    *
    * @return portfolio name
    */
   public String getPortfolioName()
   {
      return m_portfolioName;
   }

   /**
    * Retrieve the last modified date.
    *
    * @return modified date
    */
   public LocalDateTime getModifiedOn()
   {
      return m_modifiedOn;
   }

   /**
    * Retrieve the creation date.
    *
    * @return creation date
    */
   public LocalDateTime getCreatedOn()
   {
      return m_createdOn;
   }

   @Override public String toString()
   {
      return "[MsPlannerPortfolio portfolioId=" + m_portfolioId + ", portfolioName=" + m_portfolioName + "]";
   }

   private final UUID m_portfolioId;
   private final String m_portfolioName;
   private final LocalDateTime m_modifiedOn;
   private final LocalDateTime m_createdOn;

   public static class Builder
   {
      public MsPlannerPortfolio.Builder portfolioId(UUID value)
      {
         m_portfolioId = value;
         return this;
      }

      public MsPlannerPortfolio.Builder portfolioName(String value)
      {
         m_portfolioName = value;
         return this;
      }

      public MsPlannerPortfolio.Builder modifiedOn(LocalDateTime value)
      {
         m_modifiedOn = value;
         return this;
      }

      public MsPlannerPortfolio.Builder createdOn(LocalDateTime value)
      {
         m_createdOn = value;
         return this;
      }

      public MsPlannerPortfolio build()
      {
         return new MsPlannerPortfolio(this);
      }

      private UUID m_portfolioId;
      private String m_portfolioName;
      private LocalDateTime m_modifiedOn;
      private LocalDateTime m_createdOn;
   }
}
