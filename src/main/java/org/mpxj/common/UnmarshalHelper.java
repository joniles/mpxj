/*
 * file:       UnmarshalHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       29/08/2020
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
import java.io.InputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

/**
 * Utility methods wrapping JAXB unmarshal.
 */
public final class UnmarshalHelper
{
   /**
    * Unmarshal from an input stream.
    *
    * @param context JAXB context
    * @param stream input stream
    * @return Unmarshalled root node
    */
   public static final Object unmarshal(JAXBContext context, InputStream stream) throws JAXBException, SAXException, ParserConfigurationException
   {
      return context.createUnmarshaller().unmarshal(new SAXSource(createXmlReader(), new InputSource(stream)));
   }

   /**
    * Unmarshall from an input stream and apply a filter.
    *
    * @param context JAXB context
    * @param stream input stream
    * @param filter XMLFilter instance
    * @return Unmarshalled root node
    */
   public static final Object unmarshal(JAXBContext context, InputStream stream, XMLFilter filter) throws JAXBException, SAXException, ParserConfigurationException, IOException
   {
      return unmarshal(context, new InputSource(stream), filter, false);
   }

   /**
    * Unmarshall from an input source and apply a filter, optionally ignore validation errors.
    *
    * @param context JAXB context
    * @param source input source
    * @param filter XMLFilter instance
    * @param ignoreValidationErrors true if validation errors are ignored
    * @return Unmarshalled root node
    */
   public static final Object unmarshal(JAXBContext context, InputSource source, XMLFilter filter, boolean ignoreValidationErrors) throws JAXBException, SAXException, ParserConfigurationException, IOException
   {
      Unmarshaller unmarshaller = context.createUnmarshaller();

      if (ignoreValidationErrors)
      {
         unmarshaller.setEventHandler(event -> true);
      }

      UnmarshallerHandler unmarshallerHandler = unmarshaller.getUnmarshallerHandler();
      filter.setParent(createXmlReader());
      filter.setContentHandler(unmarshallerHandler);
      filter.parse(source);

      return unmarshallerHandler.getResult();
   }

   /**
    * Create a new XmlReader instance.
    *
    * @return XmlReader instance
    */
   public static final XMLReader createXmlReader() throws SAXException, ParserConfigurationException
   {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      factory.setNamespaceAware(true);
      return factory.newSAXParser().getXMLReader();
   }
}
