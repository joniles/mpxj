/*
 * file:       NilElementFilter.java
 * author:     Petr Janeček
 * date:       2026-10-15
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

import javax.xml.XMLConstants;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * The Primavera schema defines elements as nillable, which by default results
 * in JAXB generating elements like this {@code <element xsi:nil="true"/>},
 * whereas Primavera itself simply omits these elements. This filter sits
 * between the marshaller and the serializer and drops any element marked as
 * nil, passing everything else through as it arrives.
 */
class NilElementFilter extends XMLFilterImpl
{
   @Override public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
   {
      if (m_nilDepth != 0 || "true".equals(atts.getValue(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil")))
      {
         ++m_nilDepth;
         return;
      }
      super.startElement(uri, localName, qName, atts);
   }

   @Override public void endElement(String uri, String localName, String qName) throws SAXException
   {
      if (m_nilDepth != 0)
      {
         --m_nilDepth;
         return;
      }
      super.endElement(uri, localName, qName);
   }

   @Override public void characters(char[] ch, int start, int length) throws SAXException
   {
      if (m_nilDepth == 0)
      {
         super.characters(ch, start, length);
      }
   }

   private int m_nilDepth;
}
