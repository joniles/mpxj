
package net.sf.mpxj.sdef;

public class DoubleField extends StringField
{
   public DoubleField(String name, int length)
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
         result = Double.valueOf(value);
      }
      return result;
   }
}
