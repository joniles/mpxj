/*
 * This is a modified version of the MapFileGenerator from
 * http://www.frijters.net/MapFileGenerator.java. 
 * 
 * The original copyright notice appears below.  
 */

/*
  Copyright (C) 2005 Valdemar Mejstad
  Copyright (C) 2005 Jeroen Frijters

  This software is provided 'as-is', without any express or implied
  warranty.  In no event will the authors be held liable for any damages
  arising from the use of this software.

  Permission is granted to anyone to use this software for any purpose,
  including commercial applications, and to alter it and redistribute it
  freely, subject to the following restrictions:

  1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated but is not required.
  2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
  3. This notice may not be removed or altered from any source distribution.
*/

package net.sf.mpxj.ikvm;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Generate a map file for conversion of MPXJ using IKVM.
 */
public class MapFileGenerator
{

   /**
    * Generate a map file from a jar file.
    * 
    * @param jarFile jar file 
    * @param mapFileName map file name
    * @throws XMLStreamException
    * @throws IOException
    * @throws ClassNotFoundException
    * @throws IntrospectionException
    */
   public void generateMapFile(File jarFile, String mapFileName) throws XMLStreamException, IOException, ClassNotFoundException, IntrospectionException
   {
      FileWriter fw = new FileWriter(mapFileName);
      XMLOutputFactory xof = XMLOutputFactory.newInstance();
      XMLStreamWriter writer = xof.createXMLStreamWriter(fw);
      //XMLStreamWriter writer = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(fw));

      // UTF8 and indented

      writer.writeStartDocument();
      writer.writeStartElement("root");
      writer.writeStartElement("assembly");

      // add <class> tags to xml document
      addClasses(writer, jarFile);

      writer.writeEndElement();
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.flush();
      writer.close();

      fw.flush();
      fw.close();
   }

   /**
    * Add classes to the map file.
    * 
    * @param writer XML stream writer
    * @param jarFile jar file
    * @throws IOException
    * @throws ClassNotFoundException
    * @throws XMLStreamException
    * @throws IntrospectionException
    */
   private void addClasses(XMLStreamWriter writer, File jarFile) throws IOException, ClassNotFoundException, XMLStreamException, IntrospectionException
   {
      ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();

      URLClassLoader loader = new URLClassLoader(new URL[]
      {
         jarFile.toURI().toURL()
      }, currentThreadClassLoader);

      // find all classes in jar file
      Enumeration<JarEntry> enumeration = new JarFile(jarFile).entries();
      while (enumeration.hasMoreElements())
      {
         JarEntry jarEntry = enumeration.nextElement();
         if (!jarEntry.isDirectory() && jarEntry.getName().endsWith(".class"))
         {
            addClass(loader, jarEntry, writer);
         }
      }
   }

   /**
    * Add an individual class to the map file.
    * 
    * @param loader jar file class loader
    * @param jarEntry jar file entry
    * @param writer XML stream writer
    * @throws ClassNotFoundException
    * @throws XMLStreamException
    * @throws IntrospectionException
    */
   private void addClass(URLClassLoader loader, JarEntry jarEntry, XMLStreamWriter writer) throws ClassNotFoundException, XMLStreamException, IntrospectionException
   {
      String className = jarEntry.getName().replaceAll("\\.class", "").replaceAll("/", ".");

      // find all properties defined in the class
      Class<?> aClass = loader.loadClass(className);
      BeanInfo beanInfo = Introspector.getBeanInfo(aClass, aClass.getSuperclass());
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

      if (propertyDescriptors.length > 0)
      {
         writer.writeStartElement("class");
         writer.writeAttribute("name", className);
      }

      for (int i = 0; i < propertyDescriptors.length; i++)
      {
         PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
         if (propertyDescriptor.getPropertyType() != null)
         {
            String name = propertyDescriptor.getName();

            if (propertyDescriptor instanceof IndexedPropertyDescriptor)
            {
               IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor) propertyDescriptor;
               String readMethod = ipd.getIndexedReadMethod() == null ? null : ipd.getIndexedReadMethod().getName();
               String writeMethod = ipd.getIndexedWriteMethod() == null ? null : ipd.getIndexedWriteMethod().getName();
               addIndexedProperty(writer, name, ipd.getIndexedPropertyType(), readMethod, writeMethod);
            }
            else
            {
               String readMethod = propertyDescriptor.getReadMethod() == null ? null : propertyDescriptor.getReadMethod().getName();
               String writeMethod = propertyDescriptor.getWriteMethod() == null ? null : propertyDescriptor.getWriteMethod().getName();
               addProperty(writer, name, propertyDescriptor.getPropertyType(), readMethod, writeMethod);
            }
         }
         else
         {
            processAmbiguousProperty(writer, aClass, propertyDescriptor);
         }
      }

      if (propertyDescriptors.length > 0)
      {
         writer.writeEndElement();
      }
   }

   /**
    * Add an indexed property entry to the map file.
    * 
    * @param writer xml stream writer
    * @param name property name
    * @param propertyType property type
    * @param readMethod read method name
    * @param writeMethod write method name
    * @throws XMLStreamException
    */
   private void addIndexedProperty(XMLStreamWriter writer, String name, Class<?> propertyType, String readMethod, String writeMethod) throws XMLStreamException
   {
      if (name.length() != 0)
      {
         writer.writeStartElement("property");

         // convert property name to .NET style (i.e. first letter uppercase)
         String propertyName = name.substring(0, 1).toUpperCase() + name.substring(1);
         writer.writeAttribute("name", propertyName);

         String type = getTypeString(propertyType);

         writer.writeAttribute("sig", "(I)" + type);
         if (readMethod != null)
         {
            writer.writeStartElement("getter");
            writer.writeAttribute("name", readMethod);
            writer.writeAttribute("sig", "(I)" + type);
            writer.writeEndElement();
         }

         if (writeMethod != null)
         {
            writer.writeStartElement("setter");
            writer.writeAttribute("name", writeMethod);
            writer.writeAttribute("sig", "(I" + type + ")V");
            writer.writeEndElement();
         }

         writer.writeEndElement();
      }
   }

   /**
    * Add a simple property to the map file.
    * 
    * @param writer xml stream writer
    * @param name property name
    * @param propertyType property type
    * @param readMethod read method name
    * @param writeMethod write method name
    * @throws XMLStreamException
    */
   private void addProperty(XMLStreamWriter writer, String name, Class<?> propertyType, String readMethod, String writeMethod) throws XMLStreamException
   {
      if (name.length() != 0)
      {
         writer.writeStartElement("property");

         // convert property name to .NET style (i.e. first letter uppercase)
         String propertyName = name.substring(0, 1).toUpperCase() + name.substring(1);
         writer.writeAttribute("name", propertyName);

         String type = getTypeString(propertyType);

         writer.writeAttribute("sig", "()" + type);
         if (readMethod != null)
         {
            writer.writeStartElement("getter");
            writer.writeAttribute("name", readMethod);
            writer.writeAttribute("sig", "()" + type);
            writer.writeEndElement();
         }
         if (writeMethod != null)
         {
            writer.writeStartElement("setter");
            writer.writeAttribute("name", writeMethod);
            writer.writeAttribute("sig", "(" + type + ")V");
            writer.writeEndElement();
         }

         writer.writeEndElement();
      }
   }

   /**
    * Converts a class into a signature token.
    * 
    * @param c class
    * @return signature token text
    */
   private String getTypeString(Class<?> c)
   {
      String result = TYPE_MAP.get(c);
      if (result == null)
      {
         result = c.getName();
         if (!result.endsWith(";") && !result.startsWith("["))
         {
            result = "L" + result + ";";
         }
      }
      return result;
   }

   /**
    * Where bean introspection is confused by getProperty() and getProperty(int index), this method determines the correct
    * properties to add.
    * 
    * @param writer XML stream writer
    * @param aClass Java class
    * @param propertyDescriptor Java property
    * @throws SecurityException
    * @throws XMLStreamException
    */
   private void processAmbiguousProperty(XMLStreamWriter writer, Class<?> aClass, PropertyDescriptor propertyDescriptor) throws SecurityException, XMLStreamException
   {
      //
      // Do we have a non-indexed property?
      //
      String name = propertyDescriptor.getName();
      name = name.toUpperCase().charAt(0) + name.substring(1);

      Method readMethod = null;
      try
      {
         readMethod = aClass.getMethod("get" + name, (Class<?>[]) null);
      }
      catch (NoSuchMethodException ex)
      {
         // Silently ignore
      }

      if (readMethod != null)
      {
         Method writeMethod = null;
         try
         {
            writeMethod = aClass.getMethod("set" + name, readMethod.getReturnType());
         }
         catch (NoSuchMethodException ex)
         {
            // Silently ignore
         }

         String readMethodName = readMethod.getName();
         String writeMethodName = writeMethod == null ? null : writeMethod.getName();
         addProperty(writer, name, readMethod.getReturnType(), readMethodName, writeMethodName);
      }

      //
      // In theory, the code below should allow access to indexed properties, for example: task.Text[1], however
      // this part of the remapper doesn't seem to be working as expected. I assumed that there may have been a 
      // conflict between task.Text and task.Text[1] and updated the code to rename the indexed property
      // as task.Texts[1]... but still no luck. Any insights greatly appreciated... I expect that this
      // is simply my misunderstanding of what the remapper is designed to achieve.
      //
      /*      
            //
            // Do we have an indexed property?
            //
            readMethod = null;
            try
            {
               readMethod = aClass.getMethod("get" + name, int.class);
            }
            catch (NoSuchMethodException ex)
            {
               // Silently ignore
            }
            
            //
            // If we have at least a read method, then add an indexed property
            //      
            if (readMethod != null)
            {
               Method writeMethod = null;
               try
               {
                  writeMethod = aClass.getMethod("set" + name, int.class, readMethod.getReturnType());
               }
               catch (NoSuchMethodException ex)
               {
                  // Silently ignore
               }

               String readMethodName = readMethod.getName();
               String writeMethodName = writeMethod == null ? null : writeMethod.getName();
               addIndexedProperty(writer, name, readMethod.getReturnType(), readMethodName, writeMethodName);
            }    
      */
   }

   /**
    * Command line entry point.
    * 
    * @param args command line arguments
    * @throws ClassNotFoundException
    * @throws XMLStreamException
    * @throws IOException
    * @throws IntrospectionException
    */
   public static void main(String[] args) throws ClassNotFoundException, XMLStreamException, IOException, IntrospectionException
   {
      if (args.length != 2)
      {
         System.out.println("Usage: MapFileGenerator <file.jar> <outfile.xml>");
      }
      else
      {
         MapFileGenerator generator = new MapFileGenerator();
         generator.generateMapFile(new File(args[0]), args[1]);
      }
   }

   private static final Map<Class<?>, String> TYPE_MAP = new HashMap<Class<?>, String>();
   static
   {
      TYPE_MAP.put(boolean.class, "Z");
      TYPE_MAP.put(byte.class, "B");
      TYPE_MAP.put(short.class, "S");
      TYPE_MAP.put(char.class, "C");
      TYPE_MAP.put(int.class, "I");
      TYPE_MAP.put(long.class, "J");
      TYPE_MAP.put(float.class, "F");
      TYPE_MAP.put(double.class, "D");
   }
}
