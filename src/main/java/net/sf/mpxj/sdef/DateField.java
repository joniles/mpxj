
package net.sf.mpxj.sdef;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateField extends StringField
{
   public DateField(String name)
   {
      super(name, 7);
   }

   @Override public Object read(String line, int offset)
   {
      Object result;
      String value = ((String)super.read(line, offset)).trim();
      if (value.isEmpty())
      {
         result = null;
      }
      else
      {
         try
         {
            result = DATE_FORMAT.get().parse(value);
         }
         
         catch (ParseException e)
         {
            result = null;
         }
      }
      return result;
   }
   
   private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>()
   {
      @Override protected DateFormat initialValue()
      {
         return new SimpleDateFormat("ddMMMyy");
      }
   };            

}
