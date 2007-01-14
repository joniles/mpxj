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

import java.util.Date;

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
    * @param index position in the list
    * @param value evaluation value
    */
   public void setValue (int index, Object value)
   {
      m_definedValues[index] = value;
      
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
      
      m_workingValues[index] = value;      
   }

   /**
    * Retrieve the first value.
    * 
    * @param index position in the list
    * @return first value
    */
   public Object getValue (int index)
   {
      return (m_definedValues[index]);
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
      Object lhs = container.getCurrentValue(m_field);
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
      Object[] rhs;      
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
   private Object[] processSymbolicValues (Object[] oldValues, FieldContainer container)
   {
      Object[] newValues = new Object[2];
      
      for (int loop=0; loop < oldValues.length; loop++)
      {
         Object value = oldValues[loop];
         if (value == null)
         {
            continue;
         }
         
         if (value instanceof FieldType)
         {
            FieldType type = (FieldType)value;
            value = container.getCachedValue(type);
            
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
         newValues[loop]=value;
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
      sb.append(" field=");
      sb.append(m_field);
      sb.append(" operator=");
      sb.append(m_operator);
      sb.append(" value=[");
      sb.append(m_definedValues[0]);
      sb.append(",");
      sb.append(m_definedValues[1]);
      sb.append("]");
      sb.append("]");
      return (sb.toString());
   }
   

   private ProjectFile m_projectFile;
   private FieldType m_field;
   private TestOperator m_operator;
   private Object[] m_definedValues = new Object[2];
   private Object[] m_workingValues = new Object[2];
   private boolean m_symbolicValues;
}
