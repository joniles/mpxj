/*
 * file:       CustomPropertiesMap.java
 * author:     Jon Iles
 * date:       2023-08-19
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

package net.sf.mpxj.primavera;

import java.util.Collections;
import java.util.Map;

import net.sf.mpxj.ProjectFile;

/**
 * Class used to wrap the custom properties dictionary to make
 * type-safe access easier.
 * TODO: refactor custom properties into project properties and remove this class
 */
class CustomPropertiesMap
{
   /**
    * Constructor.
    *
    * @param project parent project
    * @param map map to wrap
    */
   public CustomPropertiesMap(ProjectFile project, Map<String, Object> map)
   {
      m_project = project;
      m_map = map == null ? Collections.emptyMap() : map;
   }

   /**
    * Retrieve the parent project.
    *
    * @return parent project
    */
   public ProjectFile getProject()
   {
      return m_project;
   }

   /**
    * Retrieve a Boolean value.
    *
    * @param key key to look up
    * @param defaultValue default value if key not found
    * @return value from map or default value
    */
   public Boolean getBoolean(String key, Boolean defaultValue)
   {
      Object result = m_map.get(key);
      return result instanceof Boolean ? (Boolean) result : defaultValue;
   }

   /**
    * Retrieve an Integer value.
    *
    * @param key key to look up
    * @param defaultValue default value if key not found
    * @return value from map or default value
    */
   public Integer getInteger(String key, Integer defaultValue)
   {
      Object result = m_map.get(key);
      if (result instanceof Integer)
      {
         return (Integer) result;
      }

      if (result instanceof Number)
      {
         return Integer.valueOf(((Number) result).intValue());
      }

      return defaultValue;
   }

   /**
    * Retrieve a Double value.
    *
    * @param key key to look up
    * @param defaultValue default value if key not found
    * @return value from map or default value
    */
   public Double getDouble(String key, Double defaultValue)
   {
      Object result = m_map.get(key);
      if (result instanceof Double)
      {
         return (Double) result;
      }

      if (result instanceof Number)
      {
         return Double.valueOf(((Number) result).doubleValue());
      }

      return defaultValue;
   }

   /**
    * Retrieve a String value.
    *
    * @param key key to look up
    * @param defaultValue default value if key not found
    * @return value from map or default value
    */
   public String getString(String key, String defaultValue)
   {
      Object result = m_map.get(key);
      if (result instanceof String)
      {
         return (String) result;
      }

      return defaultValue;
   }

   private final ProjectFile m_project;
   private final Map<String, Object> m_map;
}
