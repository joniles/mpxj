/*
 * file:       NamespaceFilter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       07/06/2018
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

package net.sf.mpxj.mspdi;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This class ensures that JAXB sees the namespace it is expecting when it reads the file.
 */
class NamespaceFilter extends XMLFilterImpl
{
   @Override public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
   {
      super.startElement(NAMESPACE, localName, qName, atts);
   }

   @Override public void endElement(String uri, String localName, String qName) throws SAXException
   {
      super.endElement(NAMESPACE, localName, qName);
   }

   private static final String NAMESPACE = "http://schemas.microsoft.com/project";
}