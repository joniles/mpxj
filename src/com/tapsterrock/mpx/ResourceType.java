/*
 * file:       ResourceType.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       25/11/2004
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

package com.tapsterrock.mpx;

/**
 * Instances of this class represent enumerated resource type values.
 */
public final class ResourceType
{
   /**
    * Private constructor.
    * 
    * @param value resource type value
    */
   private ResourceType (int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the resource type.
    * 
    * @return resource type value
    */
   public int getValue ()
   {
      return (m_value);
   }
   
   /**
    * Retrieve a ResourceType instance representing the supplied value.
    * 
    * @param value resource type value
    * @return ResourceType instance
    */
   public static ResourceType getInstance (int value)
   {
      ResourceType result;
      
      switch (value)
      {
         case MATERIAL_VALUE:
         {
            result = MATERIAL;
            break;
         }

         default:         
         case WORK_VALUE:
         {
            result = WORK;
            break;
         }
      }
      
      return (result);
   }
   

   
   private int m_value;
   
   /**
    * Constant representing Material
    */
   public static final int MATERIAL_VALUE = 0;

   /**
    * Constant representing Work
    */
   public static final int WORK_VALUE = 1;

   

   /**
    * Constant representing Material
    */
   public static final ResourceType MATERIAL = new ResourceType(MATERIAL_VALUE);

   /**
    * Constant representing Work
    */
   public static final ResourceType WORK = new ResourceType(WORK_VALUE);   
}
