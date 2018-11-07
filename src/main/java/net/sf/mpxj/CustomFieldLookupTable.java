/*
 * file:       CustomFieldLookupTable.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-20015
 * date:       28/04/2015
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

import net.sf.mpxj.mpp.CustomFieldValueItem;

/**
 * Lookup table defined for a custom field.
 */
public class CustomFieldLookupTable extends ListWithCallbacks<CustomFieldValueItem>
{
   /**
    * Constructor.
    *
    * @param parent parent container
    */
   public CustomFieldLookupTable(CustomFieldContainer parent)
   {
      m_parent = parent;
   }

   @Override protected void added(CustomFieldValueItem item)
   {
      m_parent.registerValue(item);
   }

   @Override protected void removed(CustomFieldValueItem item)
   {
      m_parent.deregisterValue(item);
   }

   private CustomFieldContainer m_parent;
}
