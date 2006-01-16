/*
 * file:       MPXRecord.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       15/08/2002
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

package com.tapsterrock.mpx;

import java.util.Date;

/**
 * This is the base class from which all classes representing records found
 * in an MPX file are derived. It contains common funciotnality and
 * attribute storage used by all of the derived classes.
 */
class MPXRecord
{
   /**
    * Constructor.
    *
    * @param mpx Parent MPX file
    */
   protected MPXRecord (ProjectFile mpx)
   {
      m_mpx = mpx;    
   }
   
   /**
    * This method removes line breaks from a piece of text, and replaces
    * them with the supplied text.
    *
    * @param text source text
    * @param replacement line break replacement text
    * @return text with line breaks removed.
    */
   protected final String stripLineBreaks (String text, String replacement)
   {
      if (text.indexOf('\r') != -1 || text.indexOf('\n') != -1)
      {
         StringBuffer sb = new StringBuffer (text);

         int index;

         while ((index = sb.indexOf("\r\n")) != -1)
         {
            sb.replace(index, index+2, replacement);
         }

         while ((index = sb.indexOf("\n\r")) != -1)
         {
            sb.replace(index, index+2, replacement);
         }

         while ((index = sb.indexOf("\r")) != -1)
         {
            sb.replace(index, index+1, replacement);
         }

         while ((index = sb.indexOf("\n")) != -1)
         {
            sb.replace(index, index+1, replacement);
         }

         text = sb.toString();
      }

      return (text);
   }

   /**
    * This method is called when double quotes are found as part of
    * a value. The quotes are escaped by adding a second quote character
    * and the entire value is quoted.
    * 
    * @param value text containing quote characters
    * @return escaped and quoted text
    */
   private String escapeQuotes (String value)
   {
      StringBuffer sb = new StringBuffer();
      int length = value.length();
      char c;
      
      sb.append('"');
      for (int index = 0; index < length; index++)
      {
         c = value.charAt(index);
         sb.append(c);
         
         if (c == '"')
         {
            sb.append('"');
         }         
      }
      sb.append('"');
      
      return (sb.toString());
   }
   
   /**
    * This method returns the string representation of an object. In most
    * cases this will simply involve calling the normal toString method
    * on the object, but a couple of exceptions are handled here.
    *
    * @param sepchar separator character
    * @param o the object to formatted
    * @return formatted string representing input Object
    */
   protected final String format (char sepchar, Object o)
   {
      String result;

      if (o == null)
      {
         result = "";
      }
      else
      {
         if (o instanceof Boolean == true)
         {
            result = LocaleData.getString(m_mpx.getLocale(), (((Boolean)o).booleanValue() == true?LocaleData.YES:LocaleData.NO));
         }
         else
         {
            if (o instanceof Float == true || o instanceof Double == true)
            {
               result = (m_mpx.getDecimalFormat().format(((Number)o).doubleValue()));
            }
            else
            {
               if (o instanceof ToStringRequiresFile == true)
               {
                  result = ((ToStringRequiresFile)o).toString(m_mpx);
               }
               else
               {
                  result = o.toString();
               }
            }
         }

         //
         // At this point there should be no line break characters in
         // the file. If we find any, replace them with spaces
         //
         result = stripLineBreaks(result, EOL_PLACEHOLDER_STRING);

         //
         // Finally we check to ensure that there are no embedded
         // quotes or separator characters in the value. If there are, then
         // we quote the value and escape any existing quote characters.
         //         
         if (result.indexOf('"') != -1)
         {
            result = escapeQuotes(result);
         }
         else
         {
            if (result.indexOf(sepchar) != -1)
            {
               result = '"' + result + '"';
            }
         }
      }

      return (result);
   }

  /**
    * This method removes trailing delimiter characters.
    *
    * @param buffer input sring buffer
    * @param delimiter delimiter character
    */
   protected final void stripTrailingDelimiters (StringBuffer buffer, char delimiter)
   {
      int index = buffer.length() - 1;

      while (index > 0 && buffer.charAt(index) == delimiter)
      {
         --index;
      }

      buffer.setLength (index+1);
   }

   
   /**
    * This method is called to ensure that a Date value is actually
    * represented as an MPXDate instance rather than a raw date
    * type.
    *
    * @param value date value
    * @return date value
    */
   protected MPXDate toDate (Date value)
   {
      MPXDate result = null;

      if (value != null)
      {
         if (value instanceof MPXDate == false)
         {
            result = new MPXDate (m_mpx.getDateTimeFormat(), value);
         }
         else
         {
            result = (MPXDate)value;
         }
      }

      return (result);
   }

   /**
    * This method is called to ensure that a Number value is actually
    * represented as an MPXCurrency instance rather than a raw numeric
    * type.
    *
    * @param value numeric value
    * @return currency value
    */
   protected MPXCurrency toCurrency (Number value)
   {
      MPXCurrency result = null;

      if (value != null)
      {
         if (value instanceof MPXCurrency == false)
         {
            if (value.doubleValue() == 0)
            {
               result = m_mpx.getZeroCurrency();
            }
            else
            {
               result = new MPXCurrency (m_mpx.getCurrencyFormat(), value.doubleValue());
            }
         }
         else
         {
            result = (MPXCurrency)value;
         }
      }

      return (result);
   }



   /**
    * This method is called to ensure that a Number value is actually
    * represented as an MPXUnits instance rather than a raw numeric
    * type.
    *
    * @param value numeric value
    * @return currency value
    */   
   protected MPXUnits toUnits (Number value)
   {
      MPXUnits result;
      
      if (value != null && value instanceof MPXUnits == false)
      {
         result = new MPXUnits (value);
      }
      else
      {
         result = (MPXUnits)value;
      }

      return (result);
   }

   /**
    * This method is called to ensure that a Number value is actually
    * represented as an MPXPercentage instance rather than a raw numeric
    * type.
    *
    * @param value numeric value
    * @return percentage value
    */
   protected MPXPercentage toPercentage (Number value)
   {
      MPXPercentage result = null;

      if (value != null)
      {
         if (value instanceof MPXPercentage == false)
         {
            result = MPXPercentage.getInstance(value);
         }
         else
         {
            result = (MPXPercentage)value;
         }
      }

      return (result);
   }


   /**
    * Convert a generic Date instance to an MPXTime instance.
    * 
    * @param value Date instance
    * @return MPXTime instance
    */
   protected MPXTime toTime (Date value)
   {
      MPXTime result = null;
      
      if (value != null)
      {
         if (value instanceof MPXTime == false)
         {
            result = new MPXTime (m_mpx.getTimeFormat(), value);
         }      
         else
         {
            result = (MPXTime)value;
         }
      }
      
      return (result);
   }
   
   /**
    * Accessor method allowing retreival of MPXFile reference.
    *
    * @return reference to this MPXFile
    */
   public final ProjectFile getParentFile ()
   {
      return (m_mpx);
   }


   /**
    * Reference to parent MPXFile.
    */
   private ProjectFile m_mpx;

   /**
    * Placeholder character used in MPX files to represent
    * carriage returns embedded in note text.
    */
   static final char EOL_PLACEHOLDER = (char)0x7F;
   static final String EOL_PLACEHOLDER_STRING = new String(new byte[]{EOL_PLACEHOLDER});
}
