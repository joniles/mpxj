
package net.sf.mpxj.sdef;

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.ConstraintType;

public class ConstraintTypeField extends StringField
{
   public ConstraintTypeField(String name)
   {
      super(name, 2);
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
         result = TYPE_MAP.get(value);
      }
      return result;
   }
   
   private static final Map<String, ConstraintType> TYPE_MAP = new HashMap<String, ConstraintType>();
   static
   {
      TYPE_MAP.put("ES", ConstraintType.START_NO_EARLIER_THAN);
      TYPE_MAP.put("LF", ConstraintType.FINISH_NO_LATER_THAN);
   }
}
