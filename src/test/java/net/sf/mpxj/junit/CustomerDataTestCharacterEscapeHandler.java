/*
 * file:       CustomerDataTestCharacterEscapeHandler.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       28/01/2022
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

package net.sf.mpxj.junit;

import java.io.IOException;
import java.io.Writer;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

/**
 * This class is used to align the XML output from the JVM version of MPXJ
 * with the XML output from the IKVM version. This makes it easier to
 * manage regression testing with a common set of baseline data.
 * NOTE: this class will only be present in the Java test jar, it
 * won't compile under IKVM due to the different JAXB classes used
 * in each case. This is why the class is instantiated by name when used,
 * it avoids a hard dependency, and if the class is not found its is
 * simply ignored. See {@code MarshallerHelper} for more details.
 */
public final class CustomerDataTestCharacterEscapeHandler implements CharacterEscapeHandler
{
   /*
    * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
    *
    * This program and the accompanying materials are made available under the
    * terms of the Eclipse Distribution License v. 1.0, which is available at
    * http://www.eclipse.org/org/documents/edl-v10.php.
    *
    * SPDX-License-Identifier: BSD-3-Clause
    */
   @Override public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException
   {
      int limit = start + length;
      for (int i = start; i < limit; i++)
      {
         char c = ch[i];
         if (c == '&' || c == '<' || c == '>' || c == '\r' || (c == '\n' && isAttVal) || (c == '\"' && isAttVal) || (c == '\t' && isAttVal))
         {
            if (i != start)
            {
               out.write(ch, start, i - start);
            }

            start = i + 1;
            switch (ch[i])
            {
               case '&':
                  out.write("&amp;");
                  break;
               case '<':
                  out.write("&lt;");
                  break;
               case '>':
                  out.write("&gt;");
                  break;
               case '\"':
                  out.write("&quot;");
                  break;
               case '\t':
                  out.write("&#x9;");
                  break;
               case '\n':
                  out.write("&#xA;");
                  break;
               case '\r':
                  out.write("&#xD;");
                  break;
               default:
                  throw new IllegalArgumentException("Cannot escape: '" + c + "'");
            }
         }
      }

      if (start != limit)
      {
         out.write(ch, start, limit - start);
      }
   }
}
