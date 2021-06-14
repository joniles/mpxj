/*
 * file:       MethodGenerator.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       14/06/2021
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

package net.sf.mpxj.ruby;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.DataType;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectField;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.TaskField;

/**
 * Generates methods to read attributes for ruby classes.
 */
public class MethodGenerator
{

   /**
    * Main entry point.
    * 
    * @param argv command line arguments
    */
   public static void main(String[] argv) throws IOException
   {
      if (argv.length != 1)
      {
         System.out.println("Usage: MethodGenerator <directory>");
      }
      else
      {
         MethodGenerator generator = new MethodGenerator();
         generator.process(new File(argv[0]));
      }
   }

   /**
    * Generate ruby class methods.
    * 
    * @param directory target directory for method files.
    */
   public void process(File directory) throws IOException
   {
      writeAttributeMethods(directory, "Property", ProjectField.values());
      writeAttributeMethods(directory, "Resource", ResourceField.values());
      writeAttributeMethods(directory, "Task", TaskField.values());
      writeAttributeMethods(directory, "Assignment", AssignmentField.values());
   }

   /**
    * Write attribute methods for a specific entity.
    * 
    * @param directory target directory
    * @param name entity name
    * @param types entity attributes
    */
   private void writeAttributeMethods(File directory, String name, FieldType[] types) throws IOException
   {
      List<FieldType> list = Arrays.asList(types);
      list.sort((t1, t2) -> t1.name().compareTo(t2.name()));

      String filename = name.toLowerCase() + "_methods.rb";

      try (Writer writer = new FileWriter(new File(directory, filename)))
      {
         writer.write("module MPXJ\n");
         writer.write("  module " + name + "Methods\n");

         boolean first = true;
         for (FieldType type : list)
         {
            if (first)
            {
               first = false;
            }
            else
            {
               writer.write('\n');
            }
            writeMethod(writer, type);
         }
         writer.write("  end\n");
         writer.write("end\n");
      }
   }

   /**
    * Write a single method definition.
    * 
    * @param writer output writer
    * @param field attribute to write
    */
   private void writeMethod(Writer writer, FieldType field) throws IOException
   {
      String methodName = getMethodName(field.getDataType());
      String attributeName = field.name().toLowerCase();

      writer.write("    # Retrieve the " + field.getName() + " value\n");
      writer.write("    #\n");
      writer.write("    # @return " + field.getName() + " value\n");
      writer.write("    def " + attributeName);
      writer.write('\n');

      if (methodName == null)
      {
         writer.write("      attribute_values['" + attributeName + "']\n");
      }
      else
      {
         writer.write("      " + methodName + "(attribute_values['" + attributeName + "'])\n");
      }

      writer.write("    end");
      writer.write("\n");
   }

   /**
    * Generate the name of the method used to process a specific data type.
    * 
    * @param type data type 
    * @return method name
    */
   private String getMethodName(DataType type)
   {
      String methodName;
      switch (type)
      {
         case PRIORITY:
         case INTEGER:
         case SHORT:
         {
            methodName = "get_integer_value";
            break;
         }

         case CURRENCY:
         case NUMERIC:
         case UNITS:
         case PERCENTAGE:
         {
            methodName = "get_float_value";
            break;
         }

         case DATE:
         {
            methodName = "get_date_value";
            break;
         }

         case DURATION:
         case WORK:
         case DELAY:
         {
            methodName = "get_duration_value";
            break;
         }

         case BOOLEAN:
         {
            methodName = "get_boolean_value";
            break;
         }

         default:
         {
            methodName = null;
            break;
         }
      }

      return methodName;
   }
}
