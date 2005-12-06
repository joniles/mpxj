/*
 * file:       CodePage.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2005
 * date:       14/02/2005
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

import java.nio.charset.Charset;

/**
 * Instances of this class represent enumerated code page values.
 */
public final class CodePage
{
   /**
    * Private constructor.
    *
    * @param value MPX code page name
    * @param charset Java character set name
    */
   private CodePage (String value, String charset)
   {
      m_value = value;
      m_charset = charset;
   }

   /**
    * Retrieve a CodePage instance representing the supplied value.
    *
    * @param value MPX code page name
    * @return CodePage instance
    */
   public static CodePage getInstance (String value)
   {
      CodePage result;

      if (value == null || value.equalsIgnoreCase(ANSI.toString()) == true)
      {
         result = ANSI;
      }
      else
      {
         if (value.equalsIgnoreCase(MAC.toString()) == true)
         {
            result = MAC;
         }
         else
         {
            if (value.equalsIgnoreCase(LATIN.toString()) == true)
            {
               result = LATIN;
            }
            else
            {
               if (value.equalsIgnoreCase(US.toString()) == true)
               {
                  result = US;
               }
               else
               {
                  result = ANSI;
               }
            }
         }
      }

      return (result);
   }

   /**
    * Retrieve the Java character set represented by the codepage.
    *
    * @return Java charset
    */
   public Charset getCharset ()
   {
      return (Charset.forName(m_charset));
   }

   /**
    * Returns the string representation of the codepage.
    *
    * @return codepage
    */
   public String toString ()
   {
      return (m_value);
   }

   private String m_value;
   private String m_charset;

   public static final CodePage ANSI = new CodePage("ANSI", "Cp1252");
   public static final CodePage MAC = new CodePage("MAC", "MacRoman");
   public static final CodePage LATIN = new CodePage("850", "Cp850");
   public static final CodePage US = new CodePage("437", "Cp437");
}
