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

package org.mpxj.primavera;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This class has two purposes. The first is to ensure that JAXB sees the namespace
 * it is expecting when it reads the file, so we force our hard-coded namespace.
 * The second issue we deal with is that older versions of the PMXML file format
 * appear to be identical to the modern versions, except that the root element
 * is `BusinessObjects` rather than `APIBusinessObjects`. We use this class to
 * rename the element. Note that we try to be efficient about this, so we only
 * check the first start element, and ignore all other elements if it doesn't
 * need to be renamed.
 */
class NamespaceFilter extends XMLFilterImpl
{
   @Override public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
   {
      if (m_firstElement)
      {
         m_replacing = ELEMENT_MAP.containsKey(localName);
         if (m_replacing)
         {
            localName = ELEMENT_MAP.get(localName);
         }
         m_firstElement = false;
      }
      super.startElement(NAMESPACE, localName, qName, atts);
   }

   @Override public void endElement(String uri, String localName, String qName) throws SAXException
   {
      if (m_replacing && ELEMENT_MAP.containsKey(localName))
      {
         localName = ELEMENT_MAP.get(localName);
      }
      super.endElement(NAMESPACE, localName, qName);
   }

   private boolean m_firstElement = true;
   private boolean m_replacing;

   private static final Map<String, String> ELEMENT_MAP = new HashMap<>();
   static
   {
      ELEMENT_MAP.put("BusinessObjects", "APIBusinessObjects");
   }

   private static final String NAMESPACE = "http://xmlns.oracle.com/Primavera/P6/V24.12/API/BusinessObjects";
}