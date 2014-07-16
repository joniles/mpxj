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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
         if (method.getParameterTypes().length == 0)
         {
            String name = method.getName();
            if (name.startsWith("get") || name.startsWith("is"))
            {
               methods.add(method);
            }
         }
      }

      Collections.sort(methods, new Comparator<Method>()
      {
         @Override public int compare(Method o1, Method o2)
         {
            return o1.getName().compareTo(o2.getName());
         }
      });

      String[] headings = new String[]
      {
         "Property",
         "Value"
      };

      String[][] data = new String[methods.size()][2];
      int rowIndex = 0;
      for (Method method : methods)
      {
         data[rowIndex][0] = getPropertyName(method);
         try
         {
            data[rowIndex][1] = String.valueOf(method.invoke(object));
         }
         catch (Exception ex)
         {
            data[rowIndex][1] = ex.toString();
         }
         rowIndex++;
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

}
