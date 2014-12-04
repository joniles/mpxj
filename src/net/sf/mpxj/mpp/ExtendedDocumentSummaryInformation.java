/*
 * file:       ExtendedDocumentSummaryInformation.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       04/12/2014
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

package net.sf.mpxj.mpp;

import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.UnexpectedPropertySetTypeException;

/**
 * An extension to the original POI class which provides access to additional
 * document summary properties supported by MS Project.
 */
public class ExtendedDocumentSummaryInformation extends org.apache.poi.hpsf.DocumentSummaryInformation
{
   /**
    * Constructor.
    * 
    * @param ps property set
    */
   public ExtendedDocumentSummaryInformation(PropertySet ps)
      throws UnexpectedPropertySetTypeException
   {
      super(ps);
   }

   /**
    * Retrieve the content type property.
    * 
    * @return property value
    */
   public String getContentType()
   {
      return getPropertyStringValue(PID_CONTENT_TYPE);
   }

   /**
    * Retrieve the content status property.
    * 
    * @return property value
    */
   public String getContentStatus()
   {
      return getPropertyStringValue(PID_CONTENT_STATUS);
   }

   /**
    * Retrieve the language property.
    * 
    * @return property value
    */
   public String getLanguage()
   {
      return getPropertyStringValue(PID_LANGUAGE);
   }

   /**
    * Retrieve the document version property.
    * 
    * @return property value
    */
   public String getDocumentVersion()
   {
      return getPropertyStringValue(PID_DOCUMENT_VERSION);
   }

   private static final int PID_CONTENT_TYPE = 26;
   private static final int PID_CONTENT_STATUS = 27;
   private static final int PID_LANGUAGE = 28;
   private static final int PID_DOCUMENT_VERSION = 29;
}
