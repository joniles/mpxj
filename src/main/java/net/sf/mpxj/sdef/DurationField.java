/*
 * file:       DurationField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       01/07/2019
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

package net.sf.mpxj.sdef;

import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;

/**
 * SDEF duration field.
 */
class DurationField extends IntegerField
{
   /**
    * Constructor.
    * 
    * @param name field name
    * @param length field length
    */
   public DurationField(String name, int length)
   {
      super(name, length);
   }

   @Override public Object read(String line, int offset)
   {
      Object result;
      Integer value = ((Integer)super.read(line, offset));
      if (value == null)
      {
         result = null;
      }
      else
      {
         result = Duration.getInstance(value.intValue(), TimeUnit.DAYS);
      }
      return result;
   }
}
