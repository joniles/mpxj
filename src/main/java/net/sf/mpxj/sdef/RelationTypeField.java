
package net.sf.mpxj.sdef;

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.RelationType;

public class RelationTypeField extends StringField
{
   public RelationTypeField(String name)
   {
      super(name, 1);
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
   
   private static final Map<String, RelationType> TYPE_MAP = new HashMap<String, RelationType>();
   static
   {
      TYPE_MAP.put("S", RelationType.START_START);
      TYPE_MAP.put("F", RelationType.FINISH_FINISH);
      TYPE_MAP.put("C", RelationType.FINISH_START);
   }
}
