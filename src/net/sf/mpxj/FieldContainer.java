/*
 * file:       FieldContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Mar 30, 2005
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

import net.sf.mpxj.listener.FieldListener;

/**
 * This interface is implemented by the Task and Resource classes. It
 * defines the common methods used to set and retrieve field values
 * using their identifiers.
 */
public interface FieldContainer
{
   /**
    * Set a field value.
    *
    * @param field field identifier
    * @param value field value
    */
   public void set(FieldType field, Object value);

   /**
    * Retrieve a field value. Use the cached value. Do not attempt to
    * calculate a value if the cached value is null.
    *
    * @param field field identifier
    * @return field value
    */
   public Object getCachedValue(FieldType field);

   /**
    * Retrieve a field value. If the cached value is null, attempt to
    * calculate the current value.
    *
    * @param field field identifier
    * @return field value
    */
   public Object getCurrentValue(FieldType field);

   /**
    * Add a listener to receive field events.
    *
    * @param listener target listener
    */
   public void addFieldListener(FieldListener listener);

   /**
    * Remove a listener.
    *
    * @param listener target listener
    */
   public void removeFieldListener(FieldListener listener);
}
