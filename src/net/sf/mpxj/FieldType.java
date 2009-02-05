/*
 * file:       FieldType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       16/04/2005
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

import java.util.Locale;

import net.sf.mpxj.utility.MpxjEnum;

/**
 * This interface is implemented by classes which represent a field
 * in either the Task or Resource entity.
 */
public interface FieldType extends MpxjEnum
{
   /**
    * Retrieve the name of this field using the default locale.
    *
    * @return field name
    */
   public String getName();

   /**
    * Retrieve the name of this field using the supplied locale.
    *
    * @param locale target locale
    * @return field name
    */
   public String getName(Locale locale);

   /**
    * Retrieve the data type of this field.
    * 
    * @return data type
    */
   public DataType getDataType();
}
