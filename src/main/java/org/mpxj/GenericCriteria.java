/*
 * file:       GenericCriteria.java
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import org.mpxj.common.LocalDateTimeHelper;

/**
 * This class represents the criteria used as part of an evaluation.
 */
public class GenericCriteria
{
   /**
    * Constructor.
    *
    * @param properties project properties
    */
   public GenericCriteria(ProjectProperties properties)
   {
      m_properties = properties;
   }

   /**
    * Sets the LHS of the expression.
    *
    * @param value LHS value
    */
   public void setLeftValue(FieldType value)
   {
      m_leftValue = value;
   }

   /**
    * Retrieves the LHS of the expression.
    *
    * @return LHS value
    */
   public FieldType getLeftValue()
   {
      return (m_leftValue);
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
   public void setRightValue(int index, Object value)
   {
      m_definedRightValues[index] = value;

      if (value instanceof FieldType)
      {
         m_symbolicValues = true;
      }
      else
      {
         if (value instanceof Duration)
         {
            if (((Duration) value).getUnits() != TimeUnit.HOURS)
            {
               value = ((Duration) value).convertUnits(TimeUnit.HOURS, m_properties);
            }
         }
      }

      m_workingRightValues[index] = value;
   }

   /**
    * Retrieve the first value.
    *
    * @param index position in the list
    * @return first value
    */
   public Object getValue(int index)
   {
      return (m_definedRightValues[index]);
   }

   /**
    * Evaluate the criteria and return a boolean result.
    *
    * @param container field container
    * @param promptValues responses to prompts
    * @return boolean flag
    */
   public boolean evaluate(FieldContainer container, Map<GenericCriteriaPrompt, Object> promptValues)
   {
      //
      // Retrieve the LHS value
      //
      FieldType field = m_leftValue;
      Object lhs;

      if (field == null)
      {
         lhs = null;
      }
      else
      {
         lhs = container.get(field);
         switch (field.getDataType())
         {
            case DATE:
            {
               if (lhs != null)
               {
                  lhs = LocalDateTimeHelper.getDayStartDate((LocalDateTime) lhs);
               }
               break;
            }

            case DURATION:
            {
               if (lhs != null)
               {
                  Duration dur = (Duration) lhs;
                  lhs = dur.convertUnits(TimeUnit.HOURS, m_properties);
               }
               else
               {
                  lhs = Duration.getInstance(0, TimeUnit.HOURS);
               }
               break;
            }

            case STRING:
            {
               lhs = lhs == null ? "" : lhs;
               break;
            }

            default:
            {
               break;
            }
         }
      }

      //
      // Retrieve the RHS values
      //
      Object[] rhs;
      if (m_symbolicValues)
      {
         rhs = processSymbolicValues(m_workingRightValues, container, promptValues);
      }
      else
      {
         rhs = m_workingRightValues;
      }

      //
      // Evaluate
      //
      boolean result;
      switch (m_operator)
      {
         case AND:
         case OR:
         {
            result = evaluateLogicalOperator(container, promptValues);
            break;
         }

         default:
         {
            result = m_operator.evaluate(lhs, rhs);
            break;
         }
      }

      return result;
   }

   /**
    * Evaluates AND and OR operators.
    *
    * @param container data context
    * @param promptValues responses to prompts
    * @return operator result
    */
   private boolean evaluateLogicalOperator(FieldContainer container, Map<GenericCriteriaPrompt, Object> promptValues)
   {
      boolean result = false;

      if (m_criteriaList.isEmpty())
      {
         result = true;
      }
      else
      {
         for (GenericCriteria criteria : m_criteriaList)
         {
            result = criteria.evaluate(container, promptValues);
            if ((m_operator == TestOperator.AND && !result) || (m_operator == TestOperator.OR && result))
            {
               break;
            }
         }
      }

      return result;
   }

   /**
    * This method is called to create a new list of values, converting from
    * any symbolic values (represented by FieldType instances) to actual
    * values retrieved from a Task or Resource instance.
    *
    * @param oldValues list of old values containing symbolic items
    * @param container Task or Resource instance
    * @param promptValues response to prompts
    * @return new list of actual values
    */
   private Object[] processSymbolicValues(Object[] oldValues, FieldContainer container, Map<GenericCriteriaPrompt, Object> promptValues)
   {
      Object[] newValues = new Object[2];

      for (int loop = 0; loop < oldValues.length; loop++)
      {
         Object value = oldValues[loop];
         if (value == null)
         {
            continue;
         }

         if (value instanceof FieldType)
         {
            FieldType type = (FieldType) value;
            value = container.getCachedValue(type);

            switch (type.getDataType())
            {
               case DATE:
               {
                  if (value != null)
                  {
                     value = LocalDateTimeHelper.getDayStartDate((LocalDateTime) value);
                  }
                  break;
               }

               case DURATION:
               {
                  if (value != null && ((Duration) value).getUnits() != TimeUnit.HOURS)
                  {
                     value = ((Duration) value).convertUnits(TimeUnit.HOURS, m_properties);
                  }
                  else
                  {
                     value = Duration.getInstance(0, TimeUnit.HOURS);
                  }
                  break;
               }

               case STRING:
               {
                  value = value == null ? "" : value;
                  break;
               }

               default:
               {
                  break;
               }
            }
         }
         else
         {
            if (value instanceof GenericCriteriaPrompt && promptValues != null)
            {
               GenericCriteriaPrompt prompt = (GenericCriteriaPrompt) value;
               value = promptValues.get(prompt);
            }
         }
         newValues[loop] = value;
      }
      return (newValues);
   }

   /**
    * Retrieves the list of child criteria associated with the current criteria.
    *
    * @return list of criteria
    */
   public List<GenericCriteria> getCriteriaList()
   {
      return m_criteriaList;
   }

   /**
    * Adds an item to the list of child criteria.
    *
    * @param criteria criteria item to add
    */
   public void addCriteria(GenericCriteria criteria)
   {
      m_criteriaList.add(criteria);
   }

   @Override public String toString()
   {
      StringBuilder sb = new StringBuilder();
      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
      sb.append("(");

      switch (m_operator)
      {
         case AND:
         case OR:
         {
            int index = 0;
            for (GenericCriteria c : m_criteriaList)
            {
               sb.append(c);
               ++index;
               if (index < m_criteriaList.size())
               {
                  sb.append(" ");
                  sb.append(m_operator);
                  sb.append(" ");
               }
            }
            break;
         }

         default:
         {
            sb.append(m_leftValue);
            sb.append(" ");
            sb.append(m_operator);
            sb.append(" ");
            sb.append(m_definedRightValues[0] instanceof LocalDateTime ? df.format((LocalDateTime) m_definedRightValues[0]) : m_definedRightValues[0]);
            if (m_definedRightValues[1] != null)
            {
               sb.append(",");
               sb.append(m_definedRightValues[1] instanceof LocalDateTime ? df.format((LocalDateTime) m_definedRightValues[1]) : m_definedRightValues[1]);
            }
         }
      }

      sb.append(")");
      return (sb.toString());
   }

   private final ProjectProperties m_properties;
   private FieldType m_leftValue;
   private TestOperator m_operator;
   private final Object[] m_definedRightValues = new Object[2];
   private final Object[] m_workingRightValues = new Object[2];
   private boolean m_symbolicValues;
   private final List<GenericCriteria> m_criteriaList = new ArrayList<>();
}
