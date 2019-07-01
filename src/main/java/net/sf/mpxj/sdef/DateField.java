/*
 * file:       DateField.java
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * SDEF Date Field.
 */
class DateField extends StringField
{
   /**
    * Constructor.
    * 
    * @param name field name
    */
   public DateField(String name)
   {
      super(name, 7);
   }

   @Override public Object read(String line, int offset)
   {
      Object result;
      String value = ((String)super.read(line, offset));
      if (value == null || value.isEmpty())
      {
         result = null;
      }
      else
      {
         try
         {
            result = DATE_FORMAT.get().parse(value);
         }
         
         catch (ParseException e)
         {
            result = null;
         }
      }
      return result;
   }
   
   private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>()
   {
      @Override protected DateFormat initialValue()
      {
         return new SimpleDateFormat("ddMMMyy");
      }
   };            

}
