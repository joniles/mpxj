
package net.sf.mpxj.primavera.p3;

import java.util.ArrayList;
import java.util.List;

public class WbsFormat
{
   public WbsFormat(MapRow row)
   {
      int index = 1;
      while (true)
      {
         String suffix = String.format("%02d", Integer.valueOf(index++));
         Integer length = row.getInteger("WBSW_" + suffix);
         if (length == null || length.intValue() == 0)
         {
            break;
         }
         m_lengths.add(length);
         m_separators.add(row.getString("WBSS_" + suffix));
      }
   }

   public void parseRawValue(String value)
   {
      int valueIndex = 0;
      int elementIndex = 0;
      m_elements.clear();

      while (valueIndex < value.length())
      {
         int elementLength = m_lengths.get(elementIndex).intValue();
         if (elementIndex > 0)
         {
            m_elements.add(m_separators.get(elementIndex - 1));
         }
         String element = value.substring(valueIndex, valueIndex + elementLength);
         m_elements.add(element);
         valueIndex += elementLength;
         elementIndex++;
      }
   }

   public String getFormatedValue()
   {
      return joinElements(m_elements.size());
   }

   public String getFormattedParentValue()
   {
      String result = null;
      if (m_elements.size() > 2)
      {
         result = joinElements(m_elements.size() - 2);
      }
      return result;
   }

   private String joinElements(int length)
   {
      StringBuilder sb = new StringBuilder();
      for (int index = 0; index < length; index++)
      {
         sb.append(m_elements.get(index));
      }
      return sb.toString();
   }

   private final List<String> m_elements = new ArrayList<String>();
   private final List<Integer> m_lengths = new ArrayList<Integer>();
   private final List<String> m_separators = new ArrayList<String>();
}
