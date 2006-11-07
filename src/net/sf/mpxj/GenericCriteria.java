/*
 * file:       GenericCriteria.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2006
 * date:       30-Oct-2006
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
 * This class represents the criteria used as part of an evaluation.
 */
public abstract class GenericCriteria
{
   /**
    * Constructor.
    * 
    * @param projectFile parent project file
    */
   public GenericCriteria (ProjectFile projectFile)
   {
      m_projectFile = projectFile;
   }
         
   /**
    * Sets the field used as the LHS of the expression.
    * 
    * @param field field type
    */
   public void setField (FieldType field)
   {
      m_field = field;
   }
   
   /**
    * Retrieves the field used as the RHS of the expression.
    * 
    * @return field type
    */
   public FieldType getField ()
   {
      return (m_field);
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
    * Retrieve the first value.
    * 
    * @return first value
    */
   public Object getValue ()
   {
      return (m_definedValues.get(0));
   }

   /**
    * Retrieve a list of all values.
    * 
    * @return list of values
    */
   public List getValues ()
   {
      return (m_definedValues);
   }

   /**
    * Evaluate the criteria and return a boolean result.
    * 
    * @param container field container
    * @return boolean flag
    */
   protected boolean evaluateCriteria (FieldContainer container)
   {
      //
      // Retrieve the LHS value
      //
      Object lhs = container.get(m_field);
      switch (m_field.getDataType().getType())
      {
         case DataType.DATE_VALUE:
         {
            if (lhs != null)
            {
               lhs = DateUtility.getDayStartDate((Date)lhs);
            }
            break;
         }
         
         case DataType.DURATION_VALUE:
         {
            if (lhs != null)
            {
               Duration dur = (Duration)lhs;
               lhs = dur.convertUnits(TimeUnit.HOURS, m_projectFile.getProjectHeader());
            }
            break;
         }
         
         case DataType.STRING_VALUE:
         {
            lhs = lhs==null?"":lhs;
            break;
         }
      }
      
      //
      // Retrieve the RHS values
      //
      List rhs;      
      if (m_symbolicValues == true)
      {
         rhs = processSymbolicValues (m_workingValues, container);
      }
      else
      {
         rhs = m_workingValues;
      }
      
      //
      // Evaluate
      //
      return (m_operator.evaluate(lhs, rhs));
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
      sb.append("[GenericCriteria");
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
   private FieldType m_field;
   private TestOperator m_operator;
   private List m_definedValues = new LinkedList();
   private List m_workingValues = new LinkedList();
   private boolean m_symbolicValues;
}
