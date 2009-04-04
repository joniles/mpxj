/*
 * file:       GanttBarShowForCriteria.java
 * author:     Tom Ollar
 * copyright:  (c) Packwood Software Limited 2009
 * date:       04/04/2009
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

package net.sf.mpxj.mpp;

/**
 * Represents the criteria used to define when a Gantt bar of a gien style
 * is displayed.
 */
public class GanttBarShowForCriteria
{
   /**
    * Constructor.
    * 
    * @param aEnumValue criteria
    * @param aNot not flag
    */
   public GanttBarShowForCriteria(GanttBarShowForCriteriaEnum aEnumValue, boolean aNot)
   {
      m_enumValue = aEnumValue;
      m_not = aNot;
   }

   /**
    * Retrieve the not flag.
    * 
    * @return boolean flag
    */
   public boolean getNot()
   {
      return m_not;
   }

   /**
    * Retrieve the criteria enumeration value.
    * 
    * @return criteria value
    */
   public GanttBarShowForCriteriaEnum getEnumValue()
   {
      return m_enumValue;
   }

   /**
    * {@inheritDoc}
    */
   @Override public int hashCode()
   {
      int offset;
      if (m_not)
      {
         offset = 43;
      }
      else
      {
         offset = 0;
      }
      return m_enumValue.getValue() + offset;
   }

   private GanttBarShowForCriteriaEnum m_enumValue;
   private boolean m_not;
}
