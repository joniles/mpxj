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

package net.sf.mpxj;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a filter which may be applied to a 
 * task or resource view.
 */
public class Filter
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
    * Adds a criteria expression to the filter.
    * 
    * @param criteria criteria expression
    */
   public void addCriteria(FilterCriteria criteria)
   {
      m_criteria.add(criteria);
      if (criteria.getPromptTextSet())
      {
         m_promptTextSet = true;
      }
   }

   /**
    * Retrieve the criteria used to define this filter.
    * 
    * @return list of filter criteria
    */
   public List<FilterCriteria> getCriteria()
   {
      return (m_criteria);
   }

   /**
    * Retrieves a flag indicating if this is a task filter.
    * 
    * @return boolean flag
    */
   public boolean isTaskFilter()
   {
      boolean result = true;
      if (!m_criteria.isEmpty())
      {
         result = m_criteria.get(0).getField() instanceof TaskField;
      }
      return (result);
   }

   /**
    * Retrieves a flag indicating if this is a resource filter.
    * 
    * @return boolean flag
    */
   public boolean isResourceFilter()
   {
      boolean result = true;
      if (!m_criteria.isEmpty())
      {
         result = m_criteria.get(0).getField() instanceof ResourceField;
      }
      return (result);
   }

   /**
    * Evaluates the filter, returns true if the supplied Task or Resource
    * instance matches the filter criteria.
    * 
    * @param container Task or Resource instance
    * @return boolean flag
    */
   public boolean evaluate(FieldContainer container)
   {
      boolean result = true;
      if (!m_criteria.isEmpty())
      {
         boolean currentBlockResult = true;
         boolean currentAnd = true;
         boolean lastBlockAnd = true;

         for (FilterCriteria criteria : m_criteria)
         {
            boolean criteriaResult = criteria.evaluate(container);
            if (currentAnd)
            {
               currentBlockResult = currentBlockResult && criteriaResult;
            }
            else
            {
               currentBlockResult = currentBlockResult || criteriaResult;
            }

            switch (criteria.getCriteriaLogic())
            {
               case IN_BLOCK_AND :
               {
                  currentAnd = true;
                  break;
               }

               case IN_BLOCK_OR :
               {
                  currentAnd = false;
                  break;
               }

               case BETWEEN_BLOCK_AND :
               {
                  if (lastBlockAnd)
                  {
                     result = result && currentBlockResult;
                  }
                  else
                  {
                     result = result || currentBlockResult;
                  }

                  currentAnd = true;
                  currentBlockResult = true;
                  lastBlockAnd = true;
                  break;
               }

               case BETWEEN_BLOCK_OR :
               {
                  if (lastBlockAnd)
                  {
                     result = result && currentBlockResult;
                  }
                  else
                  {
                     result = result || currentBlockResult;
                  }
                  currentAnd = true;
                  currentBlockResult = true;
                  lastBlockAnd = false;
                  break;
               }
            }
         }

         if (lastBlockAnd)
         {
            result = result && currentBlockResult;
         }
         else
         {
            result = result || currentBlockResult;
         }

         //
         // If this row has failed, but it is a summary row, and we are
         // including related summary rows, then we need to recursively test
         // its children
         //
         if (!result && m_showRelatedSummaryRows && container instanceof Task)
         {
            for (Task task : ((Task) container).getChildTasks())
            {
               if (evaluate(task))
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
    * Retrieves a flag indicating if prompt text has been set for 
    * any of the criteria associated with the filter. 
    * This saves having to test each criteria item individually if no 
    * prompts are present.
    * 
    * @return boolean flag
    */
   public boolean getPromptTextSet()
   {
      return (m_promptTextSet);
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("[Filter id=");
      sb.append(m_id);
      sb.append(" name=");
      sb.append(m_name);
      sb.append(" showRelatedSummaryRows=");
      sb.append(m_showRelatedSummaryRows);
      sb.append(" criteria=[");

      for (FilterCriteria fc : m_criteria)
      {
         sb.append(fc.toString());
      }

      sb.append("]");
      sb.append("]");

      return (sb.toString());
   }

   private Integer m_id;
   private String m_name;
   private boolean m_showRelatedSummaryRows;
   private List<FilterCriteria> m_criteria = new LinkedList<FilterCriteria>();
   private boolean m_promptTextSet;
}
