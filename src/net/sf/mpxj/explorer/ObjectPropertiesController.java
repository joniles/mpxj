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

package net.sf.mpxj.explorer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * Implements the controller component of the ObjectProperties MVC.
 */
public class ObjectPropertiesController
{
   private final ObjectPropertiesModel m_model;

   /**
    * Constructor.
    *
    * @param model model component
    */
   public ObjectPropertiesController(ObjectPropertiesModel model)
   {
      m_model = model;
   }

   /**
    * Populate the model with the object's properties.
    *
    * @param object object whose properties we're displaying
    */
   public void loadObject(Object object)
   {
      m_model.setTableModel(createTableModel(object));
   }

   /**
    * Create a table model from an object's properties.
    *
    * @param object target object
    * @return table model
    */
   private TableModel createTableModel(Object object)
   {
      List<Method> methods = new ArrayList<Method>();
      for (Method method : object.getClass().getMethods())
      {
         if ((method.getParameterTypes().length == 0) || (method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == int.class))
         {
            String name = method.getName();
            if (name.startsWith("get") || name.startsWith("is"))
            {
               methods.add(method);
            }
         }
      }

      Map<String, String> map = new TreeMap<String, String>();
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

      TableModel tableModel = new DefaultTableModel(data, headings)
      {
         @Override public boolean isCellEditable(int r, int c)
         {
            return false;
         }
      };

      return tableModel;
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
         value = method.invoke(object);
         if (value instanceof Boolean && !((Boolean) value).booleanValue())
         {
            value = null;
         }
         if (value instanceof String && ((String) value).isEmpty())
         {
            value = null;
         }
      }
      catch (Exception ex)
      {
         value = ex.toString();
      }

      if (value != null)
      {
         map.put(getPropertyName(method), String.valueOf(value));
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
         while (true)
         {
            Object value = method.invoke(object, Integer.valueOf(index));
            if (value instanceof Boolean && !((Boolean) value).booleanValue())
            {
               value = null;
            }
            if (value instanceof String && ((String) value).isEmpty())
            {
               value = null;
            }
            if (value != null)
            {
               map.put(getPropertyName(method, index), String.valueOf(value));
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
