/*
 * file:       CodePage.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2002-2005
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

package net.sf.mpxj;

import java.nio.charset.Charset;

/**
 * Instances of this class represent enumerated code page values.
 */
public enum CodePage
{
   ANSI("ANSI", "Cp1252"),
   MAC("MAC", "MacRoman"),
   LATIN("850", "Cp850"),
   US("437", "Cp437"),
   ZH("ZH", "GB2312");

   /**
    * Private constructor.
    *
    * @param value MPX code page name
    * @param charset Java character set name
    */
   private CodePage(String value, String charset)
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
   public static CodePage getInstance(String value)
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
                  if (value.equalsIgnoreCase(ZH.toString()) == true)
                  {
                     result = ZH;
                  }
                  else
                  {
                     result = ANSI;
                  }
               }
            }
         }
      }

      return (result);
   }

   /**
    * Retrieve the Java character set represented by the codepage.
    *
    * @return Java Charset instance
    */
   public Charset getCharset()
   {
      return (Charset.forName(m_charset));
   }

   /**
    * Returns the string representation of the codepage.
    *
    * @return codepage
    */
   @Override public String toString()
   {
      return (m_value);
   }

   private String m_value;
   private String m_charset;

}
