/*
 * file:       JsonWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       18/02/2015
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

package net.sf.mpxj.json;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.Duration;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.writer.AbstractProjectWriter;

/**
 * This class creates a new JSON file from the contents of 
 * a ProjectFile instance.
 */
public final class JsonWriter extends AbstractProjectWriter
{
   /**
    * Retrieve the pretty-print flag.
    * 
    * @return true if pretty printing is enabled
    */
   public boolean getPretty()
   {
      return m_pretty;
   }

   /**
    * Set the pretty-print flag.
    * 
    * @param pretty true if pretty printing is enabled 
    */
   public void setPretty(boolean pretty)
   {
      m_pretty = pretty;
   }

   /**
    * {@inheritDoc}
    */
   @Override public void write(ProjectFile projectFile, OutputStream stream) throws IOException
   {
      try
      {
         m_projectFile = projectFile;
         m_writer = new JsonStreamWriter(stream);
         m_writer.setPretty(m_pretty);

         m_writer.writeStartObject();
         writeResources();
         writeTasks();
         writeAssignments();
         m_writer.writeEndObject();

         m_writer.flush();
      }

      finally
      {
         m_projectFile = null;
      }
   }

   /**
    * This method writes resource data to a JSON file. 
    */
   private void writeResources() throws IOException
   {
      writeAttributeTypes("resource_types", ResourceField.values());

      m_writer.writeStartObject("resources");
      for (Resource resource : m_projectFile.getAllResources())
      {
         writeFields(resource, resource.getUniqueID(), ResourceField.values());
      }
      m_writer.writeEndObject();
   }

   /**
    * This method writes task data to a JSON file. 
    */
   private void writeTasks() throws IOException
   {
      writeAttributeTypes("task_types", TaskField.values());

      m_writer.writeStartObject("tasks");
      for (Task task : m_projectFile.getAllTasks())
      {
         writeFields(task, task.getUniqueID(), TaskField.values());
      }
      m_writer.writeEndObject();
   }

   /**
    * This method writes assignment data to a JSON file. 
    */
   private void writeAssignments() throws IOException
   {
      writeAttributeTypes("assignment_types", AssignmentField.values());

      m_writer.writeStartObject("assignments");
      for (ResourceAssignment assignment : m_projectFile.getAllResourceAssignments())
      {
         writeFields(assignment, assignment.getUniqueID(), AssignmentField.values());
      }
      m_writer.writeEndObject();

   }

   /**
    * Generates a mapping between attribute names and data types.
    * 
    * @param name name of the map
    * @param types types to write
    */
   private void writeAttributeTypes(String name, FieldType[] types) throws IOException
   {
      m_writer.writeStartObject(name);
      for (FieldType field : types)
      {
         m_writer.writeNameValuePair(field.name().toLowerCase(), field.getDataType().getValue());
      }
      m_writer.writeEndObject();
   }

   /**
    * Write a set of fields from a field container to a JSON file.
    * 
    * @param container field container
    * @param uniqueID unique ID for the field container
    * @param fields fields to write
    */
   private void writeFields(FieldContainer container, Integer uniqueID, FieldType[] fields) throws IOException
   {
      m_writer.writeStartObject(uniqueID.toString());
      for (FieldType field : fields)
      {
         Object value = container.getCurrentValue(field);
         if (value != null)
         {
            writeField(field, value);
         }
      }
      m_writer.writeEndObject();
   }

   /**
    * Write the appropriate data for a field to the JSON file based on its type.
    * 
    * @param field field type
    * @param value field value
    */
   private void writeField(FieldType field, Object value) throws IOException
   {
      String fieldName = field.name().toLowerCase();
      switch (field.getDataType())
      {
         case INTEGER:
         {
            writeIntegerField(fieldName, value);
            break;
         }

         case PERCENTAGE:
         case CURRENCY:
         case NUMERIC:
         case UNITS:
         {
            writeDoubleField(fieldName, value);
            break;
         }

         case BOOLEAN:
         {
            writeBooleanField(fieldName, value);
            break;
         }

         case WORK:
         case DURATION:
         {
            writeDurationField(fieldName, value);
            break;
         }

         case DATE:
         {
            writeDateField(fieldName, value);
            break;
         }

         case TIME_UNITS:
         {
            writeTimeUnitsField(fieldName, value);
            break;
         }

         case PRIORITY:
         {
            writePriorityField(fieldName, value);
            break;
         }

         case RELATION_LIST:
         {
            writeRelationList(fieldName, value);
            break;
         }

         default:
         {
            writeStringField(fieldName, value);
            break;
         }
      }
   }

   /**
    * Write an integer field to the JSON file.
    * 
    * @param fieldName field name
    * @param value field value
    */
   private void writeIntegerField(String fieldName, Object value) throws IOException
   {
      int val = ((Number) value).intValue();
      if (val != 0)
      {
         m_writer.writeNameValuePair(fieldName, val);
      }
   }

   /**
    * Write an double field to the JSON file.
    * 
    * @param fieldName field name
    * @param value field value
    */
   private void writeDoubleField(String fieldName, Object value) throws IOException
   {
      double val = ((Number) value).doubleValue();
      if (val != 0)
      {
         m_writer.writeNameValuePair(fieldName, val);
      }
   }

   /**
    * Write a boolean field to the JSON file.
    * 
    * @param fieldName field name
    * @param value field value
    */
   private void writeBooleanField(String fieldName, Object value) throws IOException
   {
      boolean val = ((Boolean) value).booleanValue();
      if (val)
      {
         m_writer.writeNameValuePair(fieldName, val);
      }
   }

   /**
    * Write a duration field to the JSON file.
    * 
    * @param fieldName field name
    * @param value field value
    */
   private void writeDurationField(String fieldName, Object value) throws IOException
   {
      Duration val = (Duration) value;
      if (val.getDuration() != 0)
      {
         Duration minutes = val.convertUnits(TimeUnit.MINUTES, m_projectFile.getProjectHeader());
         long seconds = (long) (minutes.getDuration() * 60.0);
         m_writer.writeNameValuePair(fieldName, seconds);
      }
   }

   /**
    * Write a date field to the JSON file.
    * 
    * @param fieldName field name
    * @param value field value
    */
   private void writeDateField(String fieldName, Object value) throws IOException
   {
      Date val = (Date) value;
      m_writer.writeNameValuePair(fieldName, val);
   }

   /**
    * Write a time units field to the JSON file.
    * 
    * @param fieldName field name
    * @param value field value
    */
   private void writeTimeUnitsField(String fieldName, Object value) throws IOException
   {
      TimeUnit val = (TimeUnit) value;
      if (val != m_projectFile.getProjectHeader().getDefaultDurationUnits())
      {
         m_writer.writeNameValuePair(fieldName, val.toString());
      }
   }

   /**
    * Write a priority field to the JSON file.
    * 
    * @param fieldName field name
    * @param value field value
    */
   private void writePriorityField(String fieldName, Object value) throws IOException
   {
      m_writer.writeNameValuePair(fieldName, ((Priority) value).getValue());
   }

   /**
    * Write a string field to the JSON file.
    * 
    * @param fieldName field name
    * @param value field value
    */
   private void writeStringField(String fieldName, Object value) throws IOException
   {
      String val = value.toString();
      if (!val.isEmpty())
      {
         m_writer.writeNameValuePair(fieldName, val);
      }
   }

   /**
    * Write a relation list field to the JSON file.
    * 
    * @param fieldName field name
    * @param value field value
    */
   private void writeRelationList(String fieldName, Object value) throws IOException
   {
      @SuppressWarnings("unchecked")
      List<Relation> list = (List<Relation>) value;

      m_writer.writeStartList(fieldName);
      for (Relation relation : list)
      {
         m_writer.writeStartObject();
         writeIntegerField("task", relation.getTargetTask().getUniqueID());
         writeDurationField("lag", relation.getLag());
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
   }

   private ProjectFile m_projectFile;
   private JsonStreamWriter m_writer;
   private boolean m_pretty = true;
}
