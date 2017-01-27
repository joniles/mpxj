/*
 * file:       MPXJDateFormat.java
 * author:     Jon Iles
 *             Scott Melville
 * copyright:  (c) Packwood Software 2002-2006
 * date:       15/08/2002
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

package net.sf.mpxj.mpx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class wraps the functionality provided by the SimpleDateFormat class
 * to make it suitable for use with the date conventions used in MPX files.
 */
public final class MPXJDateFormat extends MPXJBaseFormat
{
   /**
    * Constructor. Note the initialisation of the internal SimpleDateFormat
    * object used to carry out the formatting work.
    * We force the locale to English at this point. This is done
    * because it is not clear whether MPX date formats that contain the names
    * of the days of the week support localised day names, or just the
    * English names. To make things consistent and to ensure we generate
    * MPX files that can be moved between locales, we default to using the
    * English day names.
    */
   public MPXJDateFormat()
   {
      m_formats = new SimpleDateFormat[]
      {
         new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
      };
   }

   /**
    * This method is called when the locale of the parent file is updated.
    * It resets the locale specific date attributes to the default values
    * for the new locale.
    *
    * @param locale new locale
    */
   public void setLocale(Locale locale)
   {
      List<SimpleDateFormat> formats = new ArrayList<SimpleDateFormat>();
      for (SimpleDateFormat format : m_formats)
      {
         formats.add(new SimpleDateFormat(format.toPattern(), locale));
      }

      m_formats = formats.toArray(new SimpleDateFormat[formats.size()]);
   }
}
