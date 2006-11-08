/*
 * file:       Filter.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2006
 * date:       Oct 30, 2006
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

import java.util.Iterator;
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
   public void setID (Integer id)
   {
      m_id = id;
   }
   
   /**
    * Retrieves the filter's unique ID.
    * 
    * @return unique ID
    */
   public Integer getID ()
   {
      return (m_id);
   }
   
   /**
    * Sets the filter's name.
    * 
    * @param name filter name
    */
   public void setName (String name)
   {
      m_name = name;
   }
   
   /**
    * Retrieves the filter's name.
    * 
    * @return filter name
    */
   public String getName ()
   {
      return (m_name);
   }
   
   /**
    * Sets the "show related summary rows" flag.
    * 
    * @param showRelatedSummaryRows boolean flag
    */
   public void setShowRelatedSummaryRows (boolean showRelatedSummaryRows)
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
    * @param criteria criteri aexpression
    */
   public void addCriteria (FilterCriteria criteria)
   {
      m_criteria.add(criteria);
   }
   
   /**
    * Retrieve the criteria used to define this filter.
    * 
    * @return list of filter criteria
    */
   public List getCriteria ()
   {
      return (m_criteria);
   }
   
   /**
    * Retrieves a flag indicating if this is a task filter.
    * 
    * @return boolean flag
    */
   public boolean isTaskFilter ()
   {
      boolean result = true;
      if (!m_criteria.isEmpty())
      {
         result = ((FilterCriteria)m_criteria.get(0)).getField() instanceof TaskField;            
      }
      return (result);
   }
   
   /**
    * Retrieves a flag indicating if this is a resource filter.
    * 
    * @return boolean flag
    */
   public boolean isResourceFilter ()
   {
      boolean result = true;
      if (!m_criteria.isEmpty())
      {
         result = ((FilterCriteria)m_criteria.get(0)).getField() instanceof ResourceField;            
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
   public boolean evaluate (FieldContainer container)
   {
      boolean result = true;
      if (!m_criteria.isEmpty())
      {
         boolean logicalAnd = true;
         Iterator iter = m_criteria.iterator();
         while (iter.hasNext())
         {
            FilterCriteria criteria = (FilterCriteria)iter.next();
            boolean criteriaResult = criteria.evaluate(container);
            if (logicalAnd)
            {
               result = result && criteriaResult;               
            }
            else
            {
               result = result || criteriaResult;
            }
            logicalAnd = criteria.getLogiclAnd();
         }
      }
      return (result);
   }
   
   /**
    * {@inheritDoc}
    */
   public String toString ()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("[Filter id=");
      sb.append(m_id);
      sb.append(" name=");
      sb.append(m_name);
      sb.append(" showRelatedSummaryRows=");
      sb.append(m_showRelatedSummaryRows);
      sb.append(" criteria=[");
      
      Iterator iter = m_criteria.iterator();
      while (iter.hasNext())
      {
         sb.append(iter.next().toString());
      }
      
      sb.append("]");
      sb.append("]");
      
      return (sb.toString());
   }
   
   private Integer m_id;
   private String m_name;
   private boolean m_showRelatedSummaryRows;
   private List m_criteria = new LinkedList();
}
