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

package net.sf.mpxj.common;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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

      /*
       * The problem we're trying to solve here is that MPXJ running in a JVM will
       * escape characters slightly differently in XML than the IKVM version.
       * This makes no difference to end users, but for regression testing
       * we can use this code to inject a custom escape handler
       * which will align the JVM XML output with the IKVM XML output.
       */
      if (ENABLE_CUSTOM_ESCAPE_HANDLING)
      {
         try
         {
            marshaller.setProperty("com.sun.xml.bind.characterEscapeHandler", Class.forName(CUSTOM_ESCAPE_HANDLER).newInstance());
         }

         catch (Exception ex)
         {
            // If we can't set the handler, ignore the error
         }
      }

      return marshaller;
   }

   /**
    * Called to enable custom escape handling. Normally only
    * used to support regression testing.
    *
    * @param enabled true to enable the custom escape handler
    */
   public static void enableCustomEscapeHandling(boolean enabled)
   {
      ENABLE_CUSTOM_ESCAPE_HANDLING = enabled;
   }

   private static boolean ENABLE_CUSTOM_ESCAPE_HANDLING;
   private static final String CUSTOM_ESCAPE_HANDLER = "net.sf.mpxj.junit.CustomerDataTestCharacterEscapeHandler";
}
