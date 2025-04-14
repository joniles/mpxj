/*
 * file:       CodePage.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2005
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

package org.mpxj;

import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.mpxj.common.CharsetHelper;

/**
 * Instances of this class represent enumerated code page values.
 */
public enum CodePage
{
   ANSI("ANSI", CharsetHelper.CP1252),
   MAC("MAC", CharsetHelper.MAC_ROMAN),
   LATIN("850", CharsetHelper.CP850),
   US("437", CharsetHelper.CP437),
   ZH("ZH", CharsetHelper.GB2312),
   RU("RU", CharsetHelper.CP1251);

   /**
    * Private constructor.
    *
    * @param value MPX code page name
    * @param charset Java character set name
    */
   CodePage(String value, Charset charset)
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
      return NAME_MAP.getOrDefault(value, ANSI);
   }

   /**
    * Retrieve the Java character set represented by the codepage.
    *
    * @return Java Charset instance
    */
   public Charset getCharset()
   {
      return m_charset;
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

   private final String m_value;
   private final Charset m_charset;

   private static final Map<String, CodePage> NAME_MAP = new HashMap<>();
   static
   {
      for (CodePage e : EnumSet.allOf(CodePage.class))
      {
         NAME_MAP.put(e.m_value, e);
      }
   }
}
