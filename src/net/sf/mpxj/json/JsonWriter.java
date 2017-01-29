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
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.CustomField;
import net.sf.mpxj.DataType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectField;
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
    * Retrieve the encoding to used when writing the JSON file.
    *
    * @return encoding
    */
   public Charset getEncoding()
   {
      return m_encoding;
   }

   /**
    * Set the encoding to used when writing the JSON file.
    *
    * @param encoding encoding to use
    */
   public void setEncoding(Charset encoding)
   {
      m_encoding = encoding;
   }

   /**
    * {@inheritDoc}
    */
   @Override public void write(ProjectFile projectFile, OutputStream stream) throws IOException
   {
      try
      {
         m_projectFile = projectFile;
         m_writer = new JsonStreamWriter(stream, m_encoding);
         m_writer.setPretty(m_pretty);

         m_writer.writeStartObject(null);
         writeCustomFields();
         writeProperties();
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
    * Write a list of custom field attributes.
    */
   private void writeCustomFields() throws IOException
   {
      m_writer.writeStartList("custom_fields");
      for (CustomField field : m_projectFile.getCustomFields())
      {
         writeCustomField(field);
      }
      m_writer.writeEndList();
   }

   /**
    * Write attributes for an individual custom field.
    * Note that at present we are only writing a subset of the
    * available data... in this instance the field alias.
    * If the field does not have an alias we won't write an
    * entry.
    *
    * @param field custom field to write
    * @throws IOException
    */
   private void writeCustomField(CustomField field) throws IOException
   {
      if (field.getAlias() != null)
      {
         m_writer.writeStartObject(null);
         m_writer.writeNameValuePair("field_type_class", field.getFieldType().getFieldTypeClass().name().toLowerCase());
         m_writer.writeNameValuePair("field_type", field.getFieldType().name().toLowerCase());
         m_writer.writeNameValuePair("field_alias", field.getAlias());
         m_writer.writeEndObject();
      }
   }

   /**
    * This method writes project property data to a JSON file.
    */
   private void writeProperties() throws IOException
   {
      writeAttributeTypes("property_types", ProjectField.values());
      writeFields("property_values", m_projectFile.getProjectProperties(), ProjectField.values());
   }

   /**
    * This method writes resource data to a JSON file.
    */
   private void writeResources() throws IOException
   {
      writeAttributeTypes("resource_types", ResourceField.values());

      m_writer.writeStartList("resources");
      for (Resource resource : m_projectFile.getAllResources())
      {
         writeFields(null, resource, ResourceField.values());
      }
      m_writer.writeEndList();
   }

   /**
    * This method writes task data to a JSON file.
    * Note that we write the task hierarchy in order to make rebuilding the hierarchy easier.
    */
   private void writeTasks() throws IOException
   {
      writeAttributeTypes("task_types", TaskField.values());

      m_writer.writeStartList("tasks");
      for (Task task : m_projectFile.getChildTasks())
      {
         writeTask(task);
      }
      m_writer.writeEndList();
   }

   /**
    * This method is called recursively to write a task and its child tasks
    * to the JSON file.
    *
    * @param task task to write
    */
   private void writeTask(Task task) throws IOException
   {
      writeFields(null, task, TaskField.values());
      for (Task child : task.getChildTasks())
      {
         writeTask(child);
      }
   }

   /**
    * This method writes assignment data to a JSON file.
    */
   private void writeAssignments() throws IOException
   {
      writeAttributeTypes("assignment_types", AssignmentField.values());

      m_writer.writeStartList("assignments");
      for (ResourceAssignment assignment : m_projectFile.getAllResourceAssignments())
      {
         writeFields(null, assignment, AssignmentField.values());
      }
      m_writer.writeEndList();

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
    * @param objectName name of the object, or null if no name required
    * @param container field container
    * @param fields fields to write
    */
   private void writeFields(String objectName, FieldContainer container, FieldType[] fields) throws IOException
   {
      m_writer.writeStartObject(objectName);
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
      writeField(fieldName, field.getDataType(), value);
   }

   /**
    * Write the appropriate data for a field to the JSON file based on its type.
    *
    * @param fieldName field name
    * @param fieldType field type
    * @param value field value
    */
   private void writeField(String fieldName, DataType fieldType, Object value) throws IOException
   {
      switch (fieldType)
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

         case MAP:
         {
            writeMap(fieldName, value);
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
      if (value instanceof String)
      {
         m_writer.writeNameValuePair(fieldName + "_text", (String) value);
      }
      else
      {
         Duration val = (Duration) value;
         if (val.getDuration() != 0)
         {
            Duration minutes = val.convertUnits(TimeUnit.MINUTES, m_projectFile.getProjectProperties());
            long seconds = (long) (minutes.getDuration() * 60.0);
            m_writer.writeNameValuePair(fieldName, seconds);
         }
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
      if (value instanceof String)
      {
         m_writer.writeNameValuePair(fieldName + "_text", (String) value);
      }
      else
      {
         Date val = (Date) value;
         m_writer.writeNameValuePair(fieldName, val);
      }
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
      if (val != m_projectFile.getProjectProperties().getDefaultDurationUnits())
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
    * Write a map field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeMap(String fieldName, Object value) throws IOException
   {
      @SuppressWarnings("unchecked")
      Map<String, Object> map = (Map<String, Object>) value;
      m_writer.writeStartObject(fieldName);
      for (Map.Entry<String, Object> entry : map.entrySet())
      {
         Object entryValue = entry.getValue();
         if (entryValue != null)
         {
            DataType type = TYPE_MAP.get(entryValue.getClass().getName());
            if (type == null)
            {
               type = DataType.STRING;
               entryValue = entryValue.toString();
            }
            writeField(entry.getKey(), type, entryValue);
         }
      }
      m_writer.writeEndObject();
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
      if (!list.isEmpty())
      {
         m_writer.writeStartList(fieldName);
         for (Relation relation : list)
         {
            m_writer.writeStartObject(null);
            writeIntegerField("task_unique_id", relation.getTargetTask().getUniqueID());
            writeDurationField("lag", relation.getLag());
            writeStringField("type", relation.getType());
            m_writer.writeEndObject();
         }
         m_writer.writeEndList();
      }
   }

   private ProjectFile m_projectFile;
   private JsonStreamWriter m_writer;
   private boolean m_pretty;
   private Charset m_encoding = DEFAULT_ENCODING;

   private static final Charset DEFAULT_ENCODING = Charset.forName("UTF-8");

   private static Map<String, DataType> TYPE_MAP = new HashMap<String, DataType>();
   static
   {
      TYPE_MAP.put(Boolean.class.getName(), DataType.BOOLEAN);
      TYPE_MAP.put(Date.class.getName(), DataType.DATE);
      TYPE_MAP.put(Double.class.getName(), DataType.NUMERIC);
      TYPE_MAP.put(Duration.class.getName(), DataType.DURATION);
      TYPE_MAP.put(Integer.class.getName(), DataType.INTEGER);
   }
}
