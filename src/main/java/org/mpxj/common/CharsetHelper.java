/*
 * file:       CharsetHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       16/02/2017
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

package org.mpxj.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Commonly used character sets.
 */
public class CharsetHelper
{
   public static final Charset UTF8 = StandardCharsets.UTF_8;
   public static final Charset UTF16 = StandardCharsets.UTF_16;
   public static final Charset UTF16LE = StandardCharsets.UTF_16LE;
   public static final Charset CP1252 = Charset.forName("Cp1252");
   public static final Charset MAC_ROMAN = Charset.forName("MacRoman");
   public static final Charset CP850 = Charset.forName("Cp850");
   public static final Charset CP437 = Charset.forName("Cp437");
   public static final Charset GB2312 = Charset.forName("GB2312");
   public static final Charset CP1251 = Charset.forName("Cp1251");
}
