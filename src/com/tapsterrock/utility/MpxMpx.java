/*
 * file:       MpxMpx.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       26/02/2003
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

package com.tapsterrock.utility;

import com.tapsterrock.mpx.MPXFile;


/**
 * This is a trivial utility class used to exercise the MPX functionality
 * by reading and rewriting an MPX file.
 */
public class MpxMpx
{
   /**
    * Main method.
    *
    * @param args array of command line arguments
    */
   public static void main (String[] args)
   {
      try
      {
         if (args.length != 2)
         {
            System.out.println ("Usage: MpxMpx <input mpx file name> <output mpx file name>");
         }
         else
         {
            MPXFile mpx = new MPXFile (args[0]);
            mpx.write(args[1]);
         }
      }

      catch (Exception ex)
      {
         ex.printStackTrace(System.out);
      }
   }
}

