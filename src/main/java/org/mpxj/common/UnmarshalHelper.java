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
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

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
    * @param filter XMLFilter instance; any name it substitutes must be interned,
    * as a string constant is, because JAXB matches names by identity
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
    * @param filter XMLFilter instance; any name it substitutes must be interned,
    * as a string constant is, because JAXB matches names by identity
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

      // JAXB is handed the reader rather than fed the events, so it can see that the
      // parser already interns names, and skip interning every one of them again
      filter.setParent(createXmlReader());
      ParseRecorder reader = new ParseRecorder(filter);
      try
      {
         return unmarshaller.unmarshal(new SAXSource(reader, source));
      }

      catch (UnmarshalException ex)
      {
         // JAXB wraps what the parse throws, losing the parser's line and column,
         // so throw what the parse threw instead
         reader.rethrowFailure();
         throw ex;
      }
   }

   /**
    * Create a new XmlReader instance.
    *
    * @return XmlReader instance
    */
   public static final XMLReader createXmlReader() throws SAXException, ParserConfigurationException
   {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setFeature(DISALLOW_DOCTYPE_DECL, true);
      factory.setNamespaceAware(true);
      XMLReader reader = factory.newSAXParser().getXMLReader();

      // Since Java 24 the JDK's parser counts every reference to a predefined entity
      // such as &lt; in a document towards limits of 100,000, so a file with many
      // notes stored as escaped HTML can no longer be read. With no DOCTYPE allowed
      // no other entity can be declared, so these limits guard nothing here.
      for (String limit : ENTITY_SIZE_LIMITS)
      {
         try
         {
            reader.setProperty(limit, "0");
         }

         catch (SAXException ex)
         {
            // A parser other than the JDK's does not know these limits
         }
      }

      return reader;
   }

   /**
    * Create a new DocumentBuilder instance.
    *
    * @return DocumentBuilder instance
    */
   public static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException
   {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature(DISALLOW_DOCTYPE_DECL, true);
      return factory.newDocumentBuilder();
   }

   /**
    * Passes everything through to the reader it wraps, remembering what its parse throws.
    * The parser's warnings and recoverable errors are ignored, as JAXB would otherwise treat
    * them as validation errors and abort the read.
    */
   private static final class ParseRecorder extends XMLFilterImpl
   {
      /**
       * Constructor.
       *
       * @param parent reader to wrap
       */
      ParseRecorder(XMLReader parent)
      {
         super(parent);
      }

      @Override public void parse(InputSource input) throws SAXException, IOException
      {
         try
         {
            super.parse(input);
         }

         catch (SAXException | IOException ex)
         {
            m_failure = ex;
            throw ex;
         }
      }

      @Override public void warning(SAXParseException ex)
      {
         // Ignored
      }

      @Override public void error(SAXParseException ex)
      {
         // Ignored
      }

      /**
       * Throw what the parse threw, if it threw anything.
       */
      void rethrowFailure() throws SAXException, IOException
      {
         if (m_failure instanceof SAXException)
         {
            throw (SAXException) m_failure;
         }

         if (m_failure instanceof IOException)
         {
            throw (IOException) m_failure;
         }
      }

      private Exception m_failure;
   }

   private static final String DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
   private static final String[] ENTITY_SIZE_LIMITS =
   {
      "jdk.xml.maxGeneralEntitySizeLimit",
      "jdk.xml.totalEntitySizeLimit"
   };
}
