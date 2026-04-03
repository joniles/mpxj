/*
 * file:       MsPlannerProject.java
 * author:     Jon Iles
 * date:       2026-01-11
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
 * Represents a Microsoft Planner project. Returned when projects in Microsoft Planner are listed,
 * and can be used when requesting a project export.
 */
public class MsPlannerProject
{
   /**
    * Constructor.
    */
   private MsPlannerProject(Builder builder)
   {
      m_projectId = builder.m_projectId;
      m_projectName = builder.m_projectName;
      m_modifiedOn = builder.m_modifiedOn;
      m_createdOn = builder.m_createdOn;
      m_projectManagerName = builder.m_projectManagerName;
      m_portfolioId = builder.m_portfolioId;
      m_portfolioName = builder.m_portfolioName;
      m_stateCode = builder.m_stateCode;
   }

   /**
    * Retrieve the project's unique ID.
    *
    * @return unique ID
    */
   public UUID getProjectId()
   {
      return m_projectId;
   }

   /**
    * Retrieve the project's name.
    *
    * @return project name
    */
   public String getProjectName()
   {
      return m_projectName;
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

   /**
    * Retrieve the project manager name.
    *
    * @return project manager name
    */
   public String getProjectManagerName()
   {
      return m_projectManagerName;
   }

   /**
    * Retrieve the portfolio ID.
    *
    * @return portfolio ID
    */
   public UUID getPortfolioId()
   {
      return m_portfolioId;
   }

   /**
    * Retrieve the portfolio name.
    *
    * @return portfolio name
    */
   public String getPortfolioName()
   {
      return m_portfolioName;
   }

   /**
    * Retrieve the state code (0=Active, 1=Inactive).
    *
    * @return state code
    */
   public Integer getStateCode()
   {
      return m_stateCode;
   }

   @Override public String toString()
   {
      return "[MsPlannerProject projectId=" + m_projectId + ", projectName=" + m_projectName + "]";
   }

   private final UUID m_projectId;
   private final String m_projectName;
   private final LocalDateTime m_modifiedOn;
   private final LocalDateTime m_createdOn;
   private final String m_projectManagerName;
   private final UUID m_portfolioId;
   private final String m_portfolioName;
   private final Integer m_stateCode;

   public static class Builder
   {
      public MsPlannerProject.Builder projectId(UUID value)
      {
         m_projectId = value;
         return this;
      }

      public MsPlannerProject.Builder projectName(String value)
      {
         m_projectName = value;
         return this;
      }

      public MsPlannerProject.Builder modifiedOn(LocalDateTime value)
      {
         m_modifiedOn = value;
         return this;
      }

      public MsPlannerProject.Builder createdOn(LocalDateTime value)
      {
         m_createdOn = value;
         return this;
      }

      public MsPlannerProject.Builder projectManagerName(String value)
      {
         m_projectManagerName = value;
         return this;
      }

      public MsPlannerProject.Builder portfolioId(UUID value)
      {
         m_portfolioId = value;
         return this;
      }

      public MsPlannerProject.Builder portfolioName(String value)
      {
         m_portfolioName = value;
         return this;
      }

      public MsPlannerProject.Builder stateCode(Integer value)
      {
         m_stateCode = value;
         return this;
      }

      public MsPlannerProject build()
      {
         return new MsPlannerProject(this);
      }

      private UUID m_projectId;
      private String m_projectName;
      private LocalDateTime m_modifiedOn;
      private LocalDateTime m_createdOn;
      private String m_projectManagerName;
      private UUID m_portfolioId;
      private String m_portfolioName;
      private Integer m_stateCode;
   }

}
