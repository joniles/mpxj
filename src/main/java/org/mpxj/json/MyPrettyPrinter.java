package org.mpxj.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;

public class MyPrettyPrinter implements PrettyPrinter
{
   private final String lf = System.getProperty("line.separator");
   private int indentation = 0;
   private boolean isNewline = true;

   @Override
   public void writeRootValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException
   {
      jg.writeRaw(' ');
   }

   @Override
   public void writeStartObject(JsonGenerator jg) throws IOException, JsonGenerationException
   {
      if (!isNewline)
         newline(jg);
      jg.writeRaw('{');
      ++indentation;
      isNewline = false;
   }

   @Override
   public void writeEndObject(JsonGenerator jg, int nrOfEntries) throws IOException, JsonGenerationException
   {
      --indentation;
      newline(jg);
      jg.writeRaw('}');
      isNewline = indentation == 0;
   }

   @Override
   public void writeObjectEntrySeparator(JsonGenerator jg) throws IOException, JsonGenerationException
   {
      jg.writeRaw(",");
      newline(jg);
   }

   @Override
   public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException
   {
      jg.writeRaw(": ");
      isNewline = false;
   }

   @Override
   public void writeStartArray(JsonGenerator jg) throws IOException, JsonGenerationException
   {
      newline(jg);
      jg.writeRaw("[");
      ++indentation;
      isNewline = false;
   }

   @Override
   public void writeEndArray(JsonGenerator jg, int nrOfValues) throws IOException, JsonGenerationException
   {
      --indentation;
      newline(jg);
      jg.writeRaw(']');
      isNewline = false;
   }

   @Override
   public void writeArrayValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException
   {
      jg.writeRaw(", ");
      isNewline = false;
   }

   @Override
   public void beforeArrayValues(JsonGenerator jg) throws IOException, JsonGenerationException
   {
      newline(jg);
   }

   @Override
   public void beforeObjectEntries(JsonGenerator jg) throws IOException, JsonGenerationException
   {
      newline(jg);
   }

   /**
    * Writes a newline and indentation.
    * <p>
    * @param jg the JsonGenerator to write to
    * @throws IOException if an I/O error occurs
    */
   private void newline(JsonGenerator jg) throws IOException
   {
      jg.writeRaw(lf);
      for (int i = 0; i < indentation; ++i)
         jg.writeRaw("  ");
      isNewline = true;
   }
}