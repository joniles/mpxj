/*
 * file:       Filter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       30/10/2006
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

import java.util.List;
import java.util.Map;

/**
 * This class represents a filter which may be applied to a
 * task or resource view.
 */
public final class Filter
{
   /**
    * Sets the filter's unique ID.
    *
    * @param id unique ID
    */
   public void setID(Integer id)
   {
      m_id = id;
   }

   /**
    * Retrieves the filter's unique ID.
    *
    * @return unique ID
    */
   public Integer getID()
   {
      return (m_id);
   }

   /**
    * Sets the filter's name.
    *
    * @param name filter name
    */
   public void setName(String name)
   {
      m_name = name;
   }

   /**
    * Retrieves the filter's name.
    *
    * @return filter name
    */
   public String getName()
   {
      return (m_name);
   }

   /**
    * Sets the "show related summary rows" flag.
    *
    * @param showRelatedSummaryRows boolean flag
    */
   public void setShowRelatedSummaryRows(boolean showRelatedSummaryRows)
   {
      m_showRelatedSummaryRows = showRelatedSummaryRows;
   }

   /**
    * Retrieves the "show related summary rows" flag.
    *
    * @return boolean flag
    */
   public boolean getShowRelatedSummaryRows()
   {
      return (m_showRelatedSummaryRows);
   }

   /**
    * Sets the criteria associated with this filter.
    *
    * @param criteria filter criteria
    */
   public void setCriteria(GenericCriteria criteria)
   {
      m_criteria = criteria;
   }

   /**
    * Retrieve the criteria used to define this filter.
    *
    * @return list of filter criteria
    */
   public GenericCriteria getCriteria()
   {
      return m_criteria;
   }

   /**
    * Retrieves a flag indicating if this is a task filter.
    *
    * @return boolean flag
    */
   public boolean isTaskFilter()
   {
      return m_isTaskFilter;
   }

   /**
    * Sets the flag indicating if this is a task filter.
    *
    * @param flag task filter flag
    */
   public void setIsTaskFilter(boolean flag)
   {
      m_isTaskFilter = flag;
   }

   /**
    * Retrieves a flag indicating if this is a resource filter.
    *
    * @return boolean flag
    */
   public boolean isResourceFilter()
   {
      return m_isResourceFilter;
   }

   /**
    * Sets the flag indicating if this is a resource filter.
    *
    * @param flag resource filter flag
    */
   public void setIsResourceFilter(boolean flag)
   {
      m_isResourceFilter = flag;
   }

   /**
    * Evaluates the filter, returns true if the supplied Task or Resource
    * instance matches the filter criteria.
    *
    * @param container Task or Resource instance
    * @param promptValues response to prompts
    * @return boolean flag
    */
   public boolean evaluate(FieldContainer container, Map<GenericCriteriaPrompt, Object> promptValues)
   {
      boolean result = true;
      if (m_criteria != null)
      {
         result = m_criteria.evaluate(container, promptValues);

         //
         // If this row has failed, but it is a summary row, and we are
         // including related summary rows, then we need to recursively test
         // its children
         //
         if (!result && m_showRelatedSummaryRows && container instanceof Task)
         {
            for (Task task : ((Task) container).getChildTasks())
            {
               if (evaluate(task, promptValues))
               {
                  result = true;
                  break;
               }
            }
         }
      }

      return (result);
   }

   /**
    * Sets the prompts to supply the parameters required by this filter.
    *
    * @param prompts filter prompts
    */
   public void setPrompts(List<GenericCriteriaPrompt> prompts)
   {
      m_prompts = prompts;
   }

   /**
    * Retrieves the prompts required to supply parameters to this filter.
    *
    * @return filter prompts
    */
   public List<GenericCriteriaPrompt> getPrompts()
   {
      return m_prompts;
   }

   @Override public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("[Filter id=");
      sb.append(m_id);
      sb.append(" name=");
      sb.append(m_name);
      sb.append(" showRelatedSummaryRows=");
      sb.append(m_showRelatedSummaryRows);
      if (m_criteria != null)
      {
         sb.append(" criteria=");
         sb.append(m_criteria);
      }
      sb.append("]");

      return (sb.toString());
   }

   private Integer m_id;
   private String m_name;
   private boolean m_isTaskFilter;
   private boolean m_isResourceFilter;
   private boolean m_showRelatedSummaryRows;
   private GenericCriteria m_criteria;
   private List<GenericCriteriaPrompt> m_prompts;
}
