
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

public class MethodGenerator
{

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

   public void process(File directory) throws IOException
   {
      writeAttributeTypes(directory, "Property", ProjectField.values());
      writeAttributeTypes(directory, "Resource", ResourceField.values());
      writeAttributeTypes(directory, "Task", TaskField.values());
      writeAttributeTypes(directory, "Assignment", AssignmentField.values());
   }

   private void writeAttributeTypes(File directory, String name, FieldType[] types) throws IOException
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
