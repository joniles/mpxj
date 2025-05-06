/*
 * file:       ObjectPropertiesController.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       16/07/2014
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

package org.mpxj.explorer;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.mpxj.Duration;

/**
 * Implements the controller component of the ObjectProperties MVC.
 */
public class ObjectPropertiesController
{
   private final DateTimeFormatter m_dateFormat;
   private final ObjectPropertiesModel m_model;

   /**
    * Constructor.
    *
    * @param model model component
    */
   public ObjectPropertiesController(ObjectPropertiesModel model)
   {
      m_model = model;
      m_dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
   }

   /**
    * Populate the model with the object's properties.
    *
    * @param object object whose properties we're displaying
    * @param excludedMethods method names to exclude
    */
   public void loadObject(Object object, Set<String> excludedMethods)
   {
      m_model.setTableModel(createTableModel(object, excludedMethods));
   }

   /**
    * Create a table model from an object's properties.
    *
    * @param object target object
    * @param excludedMethods method names to exclude
    * @return table model
    */
   private TableModel createTableModel(Object object, Set<String> excludedMethods)
   {
      List<Method> methods = new ArrayList<>();
      for (Method method : object.getClass().getMethods())
      {
         if ((method.getParameterTypes().length == 0) || (method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == int.class))
         {
            String name = method.getName();
            if (!excludedMethods.contains(name) && (name.startsWith("get") || name.startsWith("is")))
            {
               methods.add(method);
            }
         }
      }

      Map<String, String> map = new TreeMap<>();
      for (Method method : methods)
      {
         if (method.getParameterTypes().length == 0)
         {
            getSingleValue(method, object, map);
         }
         else
         {
            getMultipleValues(method, object, map);
         }
      }

      String[] headings = new String[]
      {
         "Property",
         "Value"
      };

      String[][] data = new String[map.size()][2];
      int rowIndex = 0;
      for (Entry<String, String> entry : map.entrySet())
      {
         data[rowIndex][0] = entry.getKey();
         data[rowIndex][1] = entry.getValue();
         ++rowIndex;
      }

      return new DefaultTableModel(data, headings)
      {
         @Override public boolean isCellEditable(int r, int c)
         {
            return false;
         }
      };
   }

   /**
    * Replace default values will null, allowing them to be ignored.
    *
    * @param value value to test
    * @return filtered value
    */
   private Object filterValue(Object value)
   {
      if (value instanceof Boolean && !((Boolean) value).booleanValue())
      {
         value = null;
      }
      if (value instanceof String && ((String) value).isEmpty())
      {
         value = null;
      }
      if (value instanceof Double && ((Double) value).doubleValue() == 0.0)
      {
         value = null;
      }
      if (value instanceof Integer && ((Integer) value).intValue() == 0)
      {
         value = null;
      }
      if (value instanceof Duration && ((Duration) value).getDuration() == 0.0)
      {
         value = null;
      }

      return value;
   }

   /**
    * Retrieve a single value property.
    *
    * @param method method definition
    * @param object target object
    * @param map parameter values
    */
   private void getSingleValue(Method method, Object object, Map<String, String> map)
   {
      Object value;
      try
      {
         value = filterValue(method.invoke(object));
      }
      catch (Exception ex)
      {
         value = ex.toString();
      }

      if (value != null)
      {
         map.put(getPropertyName(method), formatValue(value));
      }
   }

   /**
    * Retrieve multiple properties.
    *
    * @param method method definition
    * @param object target object
    * @param map parameter values
    */
   private void getMultipleValues(Method method, Object object, Map<String, String> map)
   {
      try
      {
         int index = 1;
         // Let's assume we never have more than 1000 entries for anything
         while (index < 1000)
         {
            Object value = filterValue(method.invoke(object, Integer.valueOf(index)));
            if (value != null)
            {
               map.put(getPropertyName(method, index), formatValue(value));
            }
            ++index;
         }
      }
      catch (Exception ex)
      {
         // Reached the end of the valid indexes
      }
   }

   /**
    * Format a property value.
    *
    * @param value "raw" property value
    * @return formatted property value
    */
   private String formatValue(Object value)
   {
      String result;
      if (value instanceof LocalDateTime)
      {
         result = m_dateFormat.format((LocalDateTime) value);
      }
      else
      {
         result = String.valueOf(value);
      }
      return result;
   }

   /**
    * Convert a method name into a property name.
    *
    * @param method target method
    * @return property name
    */
   private String getPropertyName(Method method)
   {
      String result = method.getName();
      if (result.startsWith("get"))
      {
         result = result.substring(3);
      }
      return result;
   }

   /**
    * Convert a method name into a property name.
    *
    * @param method target method
    * @param index property index
    * @return property name
    */
   private String getPropertyName(Method method, int index)
   {
      return method.getName().substring(3) + index;
   }
}
