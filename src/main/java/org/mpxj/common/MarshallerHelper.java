/*
 * file:       MarshallerHelper.java
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

package org.mpxj.common;

import java.io.IOException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Helper methods relating to the JAXB marshaller.
 */
public final class MarshallerHelper
{
   /**
    * Create a new marshaller.
    *
    * @param context JAXB context
    * @return Marshaller instance
    */
   public static Marshaller create(JAXBContext context) throws IOException
   {
      Marshaller marshaller;
      try
      {
         marshaller = context.createMarshaller();
      }

      catch (JAXBException ex)
      {
         throw new IOException(ex.toString());
      }

      return marshaller;
   }
}
