/*
 * file:       UserFieldCounters.java
 * author:     Mario Fuentes
 * copyright:  (c) Packwood Software 2013
 * date:       22/03/2010
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

/**
 * User defined field data types. 
 */
public enum UserFieldDataType
{
   FT_TEXT("TEXT"),
   FT_START_DATE("START"),
   FT_END_DATE("FINISH"),
   FT_FLOAT_2_DECIMALS("NUMBER"),
   FT_INT("NUMBER"),
   FT_STATICTYPE("FLAG"),
   FT_MONEY("COST");

   /**
    * Constructor.
    * 
    * @param fieldName default field name used to 
    * store user defined data of this type.
    */
   private UserFieldDataType(String fieldName)
   {
      this.m_defaultFieldName = fieldName;
   }

   /**
    * Retrieve the default field name.
    * 
    * @return default field name
    */
   public String getDefaultFieldName()
   {
      return m_defaultFieldName;
   }

   private String m_defaultFieldName;
}
