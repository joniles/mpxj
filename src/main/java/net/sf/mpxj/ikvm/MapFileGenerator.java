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
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
      if (args.length != 3)
      {
         System.out.println("Usage: MapFileGenerator <file.jar> <remapfile.xml> <map class methods flag>");
      }
      else
      {
         MapFileGenerator generator = new MapFileGenerator();
         generator.generateMapFile(new File(args[0]), args[1], Boolean.parseBoolean(args[2]));
      }
   }

   /**
    * Generate a map file from a jar file.
    *
    * @param jarFile jar file
    * @param mapFileName map file name
    * @param mapClassMethods true if we want to produce .Net style class method names
    * @throws XMLStreamException
    * @throws IOException
    * @throws ClassNotFoundException
    * @throws IntrospectionException
    */
   public void generateMapFile(File jarFile, String mapFileName, boolean mapClassMethods) throws XMLStreamException, IOException, ClassNotFoundException, IntrospectionException
   {
      m_responseList = new LinkedList<String>();
      writeMapFile(mapFileName, jarFile, mapClassMethods);
   }

   /**
    * Generate an IKVM map file.
    *
    * @param mapFileName map file name
    * @param jarFile jar file containing code to be mapped
    * @param mapClassMethods true if we want to produce .Net style class method names
    * @throws IOException
    * @throws XMLStreamException
    * @throws ClassNotFoundException
    * @throws IntrospectionException
    */
   private void writeMapFile(String mapFileName, File jarFile, boolean mapClassMethods) throws IOException, XMLStreamException, ClassNotFoundException, IntrospectionException
   {
      FileWriter fw = new FileWriter(mapFileName);
      XMLOutputFactory xof = XMLOutputFactory.newInstance();
      XMLStreamWriter writer = xof.createXMLStreamWriter(fw);
      //XMLStreamWriter writer = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(fw));

      writer.writeStartDocument();
      writer.writeStartElement("root");
      writer.writeStartElement("assembly");

      addClasses(writer, jarFile, mapClassMethods);

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
    * @param mapClassMethods true if we want to produce .Net style class method names
    * @throws IOException
    * @throws ClassNotFoundException
    * @throws XMLStreamException
    * @throws IntrospectionException
    */
   private void addClasses(XMLStreamWriter writer, File jarFile, boolean mapClassMethods) throws IOException, ClassNotFoundException, XMLStreamException, IntrospectionException
   {
      ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();

      URLClassLoader loader = new URLClassLoader(new URL[]
      {
         jarFile.toURI().toURL()
      }, currentThreadClassLoader);

      JarFile jar = new JarFile(jarFile);
      Enumeration<JarEntry> enumeration = jar.entries();
      while (enumeration.hasMoreElements())
      {
         JarEntry jarEntry = enumeration.nextElement();
         if (!jarEntry.isDirectory() && jarEntry.getName().endsWith(".class"))
         {
            addClass(loader, jarEntry, writer, mapClassMethods);
         }
      }
      jar.close();
   }

   /**
    * Add an individual class to the map file.
    *
    * @param loader jar file class loader
    * @param jarEntry jar file entry
    * @param writer XML stream writer
    * @param mapClassMethods true if we want to produce .Net style class method names
    * @throws ClassNotFoundException
    * @throws XMLStreamException
    * @throws IntrospectionException
    */
   private void addClass(URLClassLoader loader, JarEntry jarEntry, XMLStreamWriter writer, boolean mapClassMethods) throws ClassNotFoundException, XMLStreamException, IntrospectionException
   {
      String className = jarEntry.getName().replaceAll("\\.class", "").replaceAll("/", ".");
      writer.writeStartElement("class");
      writer.writeAttribute("name", className);

      Set<Method> methodSet = new HashSet<Method>();
      Class<?> aClass = loader.loadClass(className);

      processProperties(writer, methodSet, aClass);

      if (mapClassMethods && !Modifier.isInterface(aClass.getModifiers()))
      {
         processClassMethods(writer, aClass, methodSet);
      }
      writer.writeEndElement();
   }

   /**
    * Process class properties.
    *
    * @param writer output stream
    * @param methodSet set of methods processed
    * @param aClass class being processed
    * @throws IntrospectionException
    * @throws XMLStreamException
    */
   private void processProperties(XMLStreamWriter writer, Set<Method> methodSet, Class<?> aClass) throws IntrospectionException, XMLStreamException
   {
      BeanInfo beanInfo = Introspector.getBeanInfo(aClass, aClass.getSuperclass());
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

      for (int i = 0; i < propertyDescriptors.length; i++)
      {
         PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
         if (propertyDescriptor.getPropertyType() != null)
         {
            String name = propertyDescriptor.getName();
            Method readMethod = propertyDescriptor.getReadMethod();
            Method writeMethod = propertyDescriptor.getWriteMethod();

            String readMethodName = readMethod == null ? null : readMethod.getName();
            String writeMethodName = writeMethod == null ? null : writeMethod.getName();
            addProperty(writer, name, propertyDescriptor.getPropertyType(), readMethodName, writeMethodName);

            if (readMethod != null)
            {
               methodSet.add(readMethod);
            }

            if (writeMethod != null)
            {
               methodSet.add(writeMethod);
            }
         }
         else
         {
            processAmbiguousProperty(writer, methodSet, aClass, propertyDescriptor);
         }
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
    * @param methodSet set of methods processed
    * @param aClass Java class
    * @param propertyDescriptor Java property
    * @throws SecurityException
    * @throws XMLStreamException
    */
   private void processAmbiguousProperty(XMLStreamWriter writer, Set<Method> methodSet, Class<?> aClass, PropertyDescriptor propertyDescriptor) throws SecurityException, XMLStreamException
   {
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

         methodSet.add(readMethod);
         if (writeMethod != null)
         {
            methodSet.add(writeMethod);
         }
      }
   }

   /**
    * Hides the original Java-style method name using an attribute
    * which should be respected by Visual Studio, the creates a new
    * wrapper method using a .Net style method name.
    *
    * Note that this does not work for VB as it is case insensitive. Even
    * though Visual Studio won't show you the Java-style method name,
    * the VB compiler sees both and thinks they are the same... which
    * causes it to fail.
    *
    * @param writer output stream
    * @param aClass class being processed
    * @param methodSet set of methods which have been processed.
    * @throws XMLStreamException
    */
   private void processClassMethods(XMLStreamWriter writer, Class<?> aClass, Set<Method> methodSet) throws XMLStreamException
   {
      Method[] methods = aClass.getDeclaredMethods();
      for (Method method : methods)
      {
         if (!methodSet.contains(method) && Modifier.isPublic(method.getModifiers()) && !Modifier.isInterface(method.getModifiers()))
         {
            if (Modifier.isStatic(method.getModifiers()))
            {
               // TODO Handle static methods here
            }
            else
            {
               String name = method.getName();
               String methodSignature = createMethodSignature(method);
               String fullJavaName = aClass.getCanonicalName() + "." + name + methodSignature;

               if (!ignoreMethod(fullJavaName))
               {
                  //
                  // Hide the original method
                  //
                  writer.writeStartElement("method");
                  writer.writeAttribute("name", name);
                  writer.writeAttribute("sig", methodSignature);

                  writer.writeStartElement("attribute");
                  writer.writeAttribute("type", "System.ComponentModel.EditorBrowsableAttribute");
                  writer.writeAttribute("sig", "(Lcli.System.ComponentModel.EditorBrowsableState;)V");
                  writer.writeStartElement("parameter");
                  writer.writeCharacters("Never");
                  writer.writeEndElement();
                  writer.writeEndElement();
                  writer.writeEndElement();

                  //
                  // Create a wrapper method
                  //
                  name = name.toUpperCase().charAt(0) + name.substring(1);

                  writer.writeStartElement("method");
                  writer.writeAttribute("name", name);
                  writer.writeAttribute("sig", methodSignature);
                  writer.writeAttribute("modifiers", "public");

                  writer.writeStartElement("body");

                  for (int index = 0; index <= method.getParameterTypes().length; index++)
                  {
                     if (index < 4)
                     {
                        writer.writeEmptyElement("ldarg_" + index);
                     }
                     else
                     {
                        writer.writeStartElement("ldarg_s");
                        writer.writeAttribute("argNum", Integer.toString(index));
                        writer.writeEndElement();
                     }
                  }

                  writer.writeStartElement("callvirt");
                  writer.writeAttribute("class", aClass.getName());
                  writer.writeAttribute("name", method.getName());
                  writer.writeAttribute("sig", methodSignature);
                  writer.writeEndElement();

                  if (!method.getReturnType().getName().equals("void"))
                  {
                     writer.writeEmptyElement("ldnull");
                     writer.writeEmptyElement("pop");
                  }
                  writer.writeEmptyElement("ret");
                  writer.writeEndElement();
                  writer.writeEndElement();

                  /*
                   * The private method approach doesn't work... so
                   * 3. Add EditorBrowsableAttribute (Never) to original methods
                   * 4. Generate C Sharp and VB variants of the DLL to avid case-sensitivity issues
                   * 5. Implement static method support?
                  <attribute type="System.ComponentModel.EditorBrowsableAttribute" sig="(Lcli.System.ComponentModel.EditorBrowsableState;)V">
                  914                       <parameter>Never</parameter>
                  915                   </attribute>
                  */

                  m_responseList.add(fullJavaName);
               }
            }
         }
      }
   }

   /**
    * Used to determine if the current method should be ignored.
    *
    * @param name method name
    * @return true if the method should be ignored
    */
   private boolean ignoreMethod(String name)
   {
      boolean result = false;

      for (String ignoredName : IGNORED_METHODS)
      {
         if (name.matches(ignoredName))
         {
            result = true;
            break;
         }
      }

      return result;
   }

   /**
    * Creates a method signature.
    *
    * @param method Method instance
    * @return method signature
    */
   private String createMethodSignature(Method method)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("(");
      for (Class<?> type : method.getParameterTypes())
      {
         sb.append(getTypeString(type));
      }
      sb.append(")");
      Class<?> type = method.getReturnType();
      if (type.getName().equals("void"))
      {
         sb.append("V");
      }
      else
      {
         sb.append(getTypeString(type));
      }
      return sb.toString();
   }

   private List<String> m_responseList;

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

   private static final String[] IGNORED_METHODS = new String[]
   {
      ".*\\.toString\\(\\)Ljava.lang.String;"
   };
}
