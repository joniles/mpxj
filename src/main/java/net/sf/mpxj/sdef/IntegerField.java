
package net.sf.mpxj.sdef;

public class IntegerField extends StringField
{
   public IntegerField(String name, int length)
   {
      super(name, length);
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
         result = Integer.valueOf(value);
      }
      return result;
   }
}
