package org.mpxj.edrawproject;

public class DatatypeConverter
{
   public static final Boolean parseBoolean(String value)
   {
      return (value == null || value.charAt(0) != '1' || value.equalsIgnoreCase("false") ? Boolean.FALSE : Boolean.TRUE);
   }
}
