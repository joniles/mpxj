/*
 * file:       Record.java
 * author:     Bruno Gasnier
 * copyright:  (c) Packwood Software 2002-2011
 * date:       2011-02-16
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

package net.sf.mpxj.primavera;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents and parses Primavera compound record data.
 */
class Record
{
   /**
    * Constructor. Parse an entire string.
    *
    * @param text string to parse
    */
   protected Record(String text)
   {
      this(text, 0, text.length());
   }

   /**
    * Constructor. Parse a segment of a string.
    *
    * @param text string to parse
    * @param start start position
    * @param end end position
    */
   protected Record(String text, int start, int end)
   {
      parse(text, start, end);
   }

   /**
    * Retrieve the field represented by this record.
    *
    * @return field
    */
   public String getField()
   {
      return m_field;
   }

   /**
    * Retrieve the value represented by this record.
    *
    * @return value
    */
   public String getValue()
   {
      return m_value;
   }

   /**
    * Retrieve all child records.
    *
    * @return list of child records
    */
   public List<Record> getChildren()
   {
      return m_records;
   }

   /**
    * Retrieve a child record by name.
    *
    * @param key child record name
    * @return child record
    */
   public Record getChild(String key)
   {
      Record result = null;
      if (key != null)
      {
         for (Record record : m_records)
         {
            if (key.equals(record.getField()))
            {
               result = record;
               break;
            }
         }
      }
      return result;
   }

   /**
    * Parse a block of text into records.
    *
    * @param text text to parse
    * @param start start index
    * @param end end index
    */
   private void parse(String text, int start, int end)
   {
      int closing = getClosingParenthesisPosition(text, start);
      if (closing == -1 || closing > end)
      {
         throw new IllegalStateException("Error in parenthesis hierarchy");
      }
      if (!text.startsWith("(0||"))
      {
         throw new IllegalStateException("Not a valid record");
      }

      int valueStart = getNextOpeningParenthesisPosition(text, start);
      int valueStop = getClosingParenthesisPosition(text, valueStart);
      int dictStart = getNextOpeningParenthesisPosition(text, valueStop);
      int dictStop = getClosingParenthesisPosition(text, dictStart);
      parse(text, start + 4, valueStart, valueStop, dictStart, dictStop);
   }

   /**
    * Parse a block of text into records.
    *
    * @param text text to be parsed
    * @param start start index
    * @param valueStart value start index
    * @param valueStop value end index
    * @param dictStart dictionary start index
    * @param dictStop dictionary end index
    */
   private void parse(String text, int start, int valueStart, int valueStop, int dictStart, int dictStop)
   {
      m_field = text.substring(start, valueStart);
      if (valueStop - valueStart <= 1)
      {
         m_value = null;
      }
      else
      {
         m_value = text.substring(valueStart + 1, valueStop);
      }
      if (dictStop - dictStart > 1)
      {
         for (int s = getNextOpeningParenthesisPosition(text, dictStart); s >= 0 && s < dictStop;)
         {
            int e = getClosingParenthesisPosition(text, s);
            m_records.add(new Record(text, s, e));
            s = getNextOpeningParenthesisPosition(text, e);
         }
      }
   }

   /**
    * Look for the closing parenthesis corresponding to the one at position
    * represented by the opening index.
    *
    * @param text input expression
    * @param opening opening parenthesis index
    * @return closing parenthesis index
    */
   private int getClosingParenthesisPosition(String text, int opening)
   {
      if (text.charAt(opening) != '(')
      {
         return -1;
      }

      int count = 0;
      for (int i = opening; i < text.length(); i++)
      {
         char c = text.charAt(i);
         switch (c)
         {
            case '(':
            {
               ++count;
               break;
            }

            case ')':
            {
               --count;
               if (count == 0)
               {
                  return i;
               }
               break;
            }
         }
      }

      return -1;
   }

   /**
    * Retrieve the position of the next opening parenthesis.
    *
    * @param text text being parsed
    * @param position start position
    * @return index of parenthesis
    */
   private int getNextOpeningParenthesisPosition(String text, int position)
   {
      return text.indexOf('(', position + 1);
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      return this.toString(1);
   }

   /**
    * Method to build hierarchical string representation - called recursively.
    *
    * @param spaces number of spaces to use to format the string
    * @return formatted string
    */
   protected String toString(int spaces)
   {
      StringBuilder result = new StringBuilder("(0||" + (m_field == null ? "" : m_field) + "(" + (m_value == null ? "" : m_value) + ")(");
      for (Record record : m_records)
      {
         if (spaces != 0)
         {
            result.append(SEPARATOR);
            for (int i = 0; i < spaces * 2; i++)
            {
               result.append(" ");
            }
         }
         result.append(record.toString(spaces + 1));
      }
      result.append("))");
      return result.toString();
   }

   private String m_field;
   private String m_value;
   private List<Record> m_records = new ArrayList<Record>();

   private static final String SEPARATOR = new String(new byte[]
   {
      0x7f,
      0x7f
   });
}