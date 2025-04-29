package org.mpxj.edrawproject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DatatypeConverter
{
   public static final Boolean parseBoolean(String value)
   {
      return (value == null || value.charAt(0) != '1' || value.equalsIgnoreCase("false") ? Boolean.FALSE : Boolean.TRUE);
   }

   public static final String printBoolean(Boolean value)
   {
      throw new UnsupportedOperationException();
   }

   public static final LocalDateTime parseTimestamp(String value)
   {
      LocalDateTime result = null;

      if (value != null && !value.isEmpty())
      {
         try
         {
            result = LocalDateTime.parse(value, TIMESTAMP_FORMAT);
         }

         catch (DateTimeParseException ex)
         {
            // Ignore parse exception
         }
      }

      return (result);
   }

   public static final String printTimestamp(LocalDateTime value)
   {
      throw new UnsupportedOperationException();
   }

   private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
}
