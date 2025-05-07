/*
 * file:       FieldListener.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       March 30, 2005
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

package org.mpxj.listener;

import org.mpxj.FieldContainer;
import org.mpxj.FieldType;

/**
 * Classes implementing this interface can be used to receive notification
 * of changes to task or resource fields.
 */
public interface FieldListener
{
   /**
    * Called when a field value is changed.
    *
    * @param container field container
    * @param type field type
    * @param oldValue old value
    * @param newValue new value
    */
   void fieldChange(FieldContainer container, FieldType type, Object oldValue, Object newValue);
}
