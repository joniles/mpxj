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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.utility.DateUtility;

/**
 * This class represents the criteria used to determine if a graphical
 * indicator is displayed in place of an attribute value.
 */
public class GraphicalIndicatorCriteria
{
   /**
    * Constructor.
    * 
    * @param projectFile parent project file
    */
   public GraphicalIndicatorCriteria (ProjectFile projectFile)
   {
      m_projectFile = projectFile;
   }
   
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
      m_definedValues.add(value);
      
      if (value instanceof FieldType)
      {
         m_symbolicValues = true;                  
      }
      else
      {
         if (value instanceof Duration)
         {
            if (((Duration)value).getUnits() != TimeUnit.HOURS)
            {
               value = ((Duration)value).convertUnits(TimeUnit.HOURS, m_projectFile.getProjectHeader());
            }
         }
      }
      
      m_workingValues.add(value);      
   }

   /**
    * Evaluate this criteria to determine if a graphical indicator should
    * be displayed. This method will return -1 if no indicator should
    * be displayed, or it will return a positive integer identifying the
    * required indicator.
    * 
    * @param operand operand value
    * @param container field container
    * @return boolean flag
    */
   public int evaluate (Object operand, FieldContainer container)
   {
      List values;
      
      if (m_symbolicValues == true)
      {
         values = processSymbolicValues (m_workingValues, container);
      }
      else
      {
         values = m_workingValues;
      }
      
      return (m_operator.evaluate(operand, values)?m_indicator:-1);
   }
   
   /**
    * This method is called to create a new list of values, converting from
    * any symbolic values (represented by FieldType instances) to actual
    * values retrieved from a Task or Resource instance.
    * 
    * @param oldValues list of old values containing symbold items
    * @param container Task or Resource instance
    * @return new list of actual values
    */
   private List processSymbolicValues (List oldValues, FieldContainer container)
   {
      List newValues = new ArrayList(oldValues.size());
      Iterator iter = oldValues.iterator();
      while (iter.hasNext() == true)
      {
         Object value = iter.next();
         if (value instanceof FieldType)
         {
            FieldType type = (FieldType)value;
            value = container.get(type);
            
            switch (type.getDataType().getType())
            {
               case DataType.DATE_VALUE:
               {
                  if (value != null)
                  {
                     value = DateUtility.getDayStartDate((Date)value);
                  }
                  break;
               }

               case DataType.DURATION_VALUE:
               {
                  if (value != null && ((Duration)value).getUnits() != TimeUnit.HOURS)
                  {
                     value = ((Duration)value).convertUnits(TimeUnit.HOURS, m_projectFile.getProjectHeader());
                  }
                  break;
               }
               
               case DataType.STRING_VALUE:
               {
                  value = value==null?"":value;
                  break;
               }
            }
         }
         newValues.add(value);
      }      
      return (newValues);
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
      if (m_definedValues.size() == 1)
      {
         sb.append(" value=" + m_definedValues.get(0));
      }
      if (m_definedValues.size() == 2)
      {
         sb.append(" values=" + m_definedValues.get(0) + "," + m_definedValues.get(1));
      }      
      sb.append("]");
      return (sb.toString());
   }
   

   private ProjectFile m_projectFile;
   private int m_indicator;
   private TestOperator m_operator;
   private List m_definedValues = new LinkedList();
   private List m_workingValues = new LinkedList();
   private boolean m_symbolicValues;
}
