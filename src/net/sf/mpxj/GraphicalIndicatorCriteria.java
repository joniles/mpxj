/*
 * file:       GraphicalIndicatorCriteria.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       15-Feb-2006
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
 * This class represents the criteria used to determine if a graphical
 * indicator is displayed in place of an attribute value.
 */
public class GraphicalIndicatorCriteria
{
   /**
    * Retrieve the number of the indicator to be displayed.
    * 
    * @return indicator number
    */
   public int getIndicator()
   {
      return m_indicator;
   }
   
   /**
    * Set the number of the indicator to be displayed.
    * 
    * @param indicator indicator number
    */
   public void setIndicator(int indicator)
   {
      m_indicator = indicator;
   }
   
   /**
    * Retrieve the operator used in the test.
    * 
    * @return test operator
    */
   public TestOperator getOperator()
   {
      return m_operator;
   }
   
   /**
    * Set the operator used in the test.
    * 
    * @param operator test operator
    */
   public void setOperator(TestOperator operator)
   {
      m_operator = operator;
   }
   
   /**
    * Add the value to list of values to be used as part of the
    * evaluation of this indicator.
    * 
    * @param value evaluation value
    */
   public void addValue (Object value)
   {
      m_values.add(value);
   }
   
   /**
    * {@inheritDoc}
    */
   public String toString ()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("[GraphicalIndicatorCriteria indicator=");
      sb.append(m_indicator);
      sb.append(" operator=");
      sb.append(m_operator);
      if (m_values.size() == 1)
      {
         sb.append(" value=" + m_values.get(0));
      }
      if (m_values.size() == 2)
      {
         sb.append(" values=" + m_values.get(0) + "," + m_values.get(1));
      }      
      sb.append("]");
      return (sb.toString());
   }
   
   private int m_indicator;
   private TestOperator m_operator;
   private List m_values = new LinkedList();
}
