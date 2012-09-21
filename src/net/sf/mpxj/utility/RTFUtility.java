/*
 * file:       RTFUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       24/05/2003
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

package net.sf.mpxj.utility;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to collect together utility functions for manipulating
 * RTF encoded text.
 */
public final class RTFUtility
{
   /**
    * Simple heuristic to determine if the string may contain RTF commands.
    * 
    * @param text source text
    * @return true if the text may contain RTF commands
    */
   public static boolean isPlainText(String text)
   {
      return text.indexOf('\\') == -1;
   }

   /**
    * Simple heuristic to determine if the string contains formal RTF.
    * 
    * @param text source text
    * @return true if the text may contain formal RTF
    */
   private static boolean isFormalRTF(String text)
   {
      return text.startsWith("{\\rtf");
   }

   /**
    * This method removes all RTF formatting from a given piece of text.
    *
    * @param text Text from which the RTF formatting is to be removed.
    * @return Plain text
    */
   public static String strip(String text)
   {
      boolean formalRTF = isFormalRTF(text);

      text = normaliseLineEnds(text);
      text = stripLineEnds(text, formalRTF);
      text = processDoubleByteChars(text);
      text = regexpStrip(text);
      text = stripExtraLineEnd(text, formalRTF);

      return text;
   }

   /**
    * Replace Window-style line ends (\r\n) with Java style (\n).
    * 
    * @param text input text
    * @return text with normalised line ends
    */
   private static String normaliseLineEnds(String text)
   {
      if (text.indexOf("\r\n") != -1)
      {
         text = text.replace("\r\n", "\n");
      }
      return text;
   }

   /**
    * This method converts any encoded double byte characters
    * to Unicode.
    * 
    * @param text RTF text
    * @return RTF text with Unicode characters
    */
   private static String processDoubleByteChars(String text)
   {
      String[] tokens = text.split("\\\\");
      int index = 0;
      String currentEncoding = DEFAULT_ENCODING;
      StringBuilder result = new StringBuilder(text.length());
      boolean collectingBytes = false;
      LinkedList<String> bytes = new LinkedList<String>();
      boolean firstWord = true;

      while (index < tokens.length)
      {
         String token = tokens[index];
         if (token.length() != 0)
         {
            if (token.length() > 1 && token.charAt(0) == '\'' && isHexDigit(token.charAt(1)) && isHexDigit(token.charAt(2)))
            {
               if (!collectingBytes)
               {
                  collectingBytes = true;
               }
               bytes.add(token.substring(1, 3));

               if (token.length() > 3)
               {
                  String decodedText = processBytes(bytes, currentEncoding);
                  if (firstWord)
                  {
                     firstWord = false;
                     result.append(' ');
                  }
                  result.append(decodedText);
                  collectingBytes = false;
                  bytes.clear();
                  result.append(token.substring(3));
               }

               ++index;
               continue;
            }

            if (collectingBytes)
            {
               String decodedText = processBytes(bytes, currentEncoding);
               if (firstWord)
               {
                  result.append(' ');
               }
               result.append(decodedText);
               collectingBytes = false;
               bytes.clear();
            }

            if (token.startsWith("lang") || token.startsWith("deflang"))
            {
               currentEncoding = processEncoding(token);
            }
         }

         firstWord = true;
         if (index != 0)
         {
            result.append('\\');
         }
         result.append(token);
         index++;
      }

      return (result.toString());
   }

   /**
    * Determine if the digit is a valid hex character.
    * 
    * @param c digit to test
    * @return true if the digit is a valid hex character
    */
   private static boolean isHexDigit(char c)
   {
      return Character.isDigit(c) || (c - 'a' >= 0 && c - 'a' < 6) || (c - 'A' >= 0 && c - 'A' < 6);
   }

   /**
    * Extracts a locale ID from an RTF command and converts 
    * it to a character encoding.
    * 
    * @param token RTF command
    * @return encoding
    */
   private static String processEncoding(String token)
   {
      String localeID = null;

      int index = 0;
      while (index < token.length() && !Character.isDigit(token.charAt(index)))
      {
         ++index;
      }

      if (index != token.length())
      {
         StringBuilder sb = new StringBuilder(token.length());
         while (index < token.length() && Character.isDigit(token.charAt(index)))
         {
            sb.append(token.charAt(index));
            ++index;
         }
         localeID = sb.toString();
      }

      //
      // Default to Cp1252 if we don't have an explicit mapping
      //
      String encoding = LOCALEID_MAPPING.get(localeID);
      if (encoding == null)
      {
         encoding = DEFAULT_ENCODING;
      }

      return (encoding);
   }

   /**
    * Takes the string representation of a collection of bytes
    * and converts it to a Unicode string.
    * 
    * @param bytes list of bytes represented by strings of hex digits
    * @param currentEncoding current character set encoding
    * @return Unicode string
    */
   private static String processBytes(LinkedList<String> bytes, String currentEncoding)
   {
      byte[] raw = new byte[bytes.size()];
      int byteIndex = 0;
      for (String hexByte : bytes)
      {
         raw[byteIndex++] = Integer.decode("0x" + hexByte).byteValue();
      }

      String result = "";

      try
      {
         result = new String(raw, currentEncoding);
      }

      catch (UnsupportedEncodingException ex)
      {
         // Ignored
      }

      return (result);
   }

   /**
    * Strip RTF file using regular expressions to remove most
    * of the RTF commands.
    * 
    * @param rtf input RTF text
    * @return stripped text
    */
   private static String regexpStrip(String rtf)
   {
      rtf = stripCommands("{\\object", rtf);

      StringBuilder sb = new StringBuilder();
      try
      {
         Matcher m = RTF_PATTERN.matcher(rtf);
         int index = 0;

         while (m.find())
         {
            if (m.start() != index)
            {
               String value = rtf.substring(index, m.start());
               sb.append(value);
            }

            String group = m.group().trim();
            String mapped = RTF_MAPPING.get(group);
            if (mapped != null)
            {
               sb.append(mapped);
            }

            index = m.end();
         }

         //
         // If we had no matches, return the text we passed to the regexp
         //
         if (index == 0)
         {
            sb.append(rtf);
         }
      }
      catch (Exception ex)
      {
         // Ignored
      }

      return sb.toString();
   }

   /**
    * Utility method to explicitly strip individual commands not managed 
    * by the regular expression.
    * 
    * @param command command to strip
    * @param rtf RTF text
    * @return stripped text
    */
   private static String stripCommands(String command, String rtf)
   {
      //
      // Do we have any of these commands present in the RTF?
      //
      int startIndex = rtf.indexOf(command);
      if (startIndex != -1)
      {
         StringBuilder sb = new StringBuilder(rtf);
         do
         {
            //
            // Find the end of the enclosing block
            //
            int endIndex = startIndex + 1;
            int nesting = 1;
            while (nesting != 0 && endIndex < sb.length())
            {
               char c = sb.charAt(endIndex);
               switch (c)
               {
                  case '{':
                  {
                     ++nesting;
                     break;
                  }

                  case '}':
                  {
                     --nesting;
                     break;
                  }
               }

               ++endIndex;
            }

            //
            // Unexpected format - bail out
            //
            if (nesting != 0)
            {
               break;
            }
            --endIndex;

            //
            // Remove the block
            //
            sb.replace(startIndex, endIndex, "");

            //
            // Find the next entry
            //
            startIndex = sb.indexOf(command, startIndex);
         }
         while (startIndex != -1);

         rtf = sb.toString();
      }

      return rtf;
   }

   /**
    * If we really do have a block of RTF - not just plain text
    * with RTF commands embedded, then strip end of line characters.
    * These will be represented as \par in RTF if they really are line
    * breaks.
    *
    * @param text source text
    * @param formalRTF true if this is real RTF
    * @return text with line ends stripped
    */
   private static String stripLineEnds(String text, boolean formalRTF)
   {
      if (formalRTF)
      {
         int index = text.indexOf('\n');
         if (index != -1)
         {
            StringBuilder sb = new StringBuilder(text);
            while (index != -1)
            {
               if (index != 0 && sb.charAt(index - 1) == '}')
               {
                  //
                  // We follow a close command character - no problem to remove
                  //
                  sb.replace(index, index + 1, "");
               }
               else
               {
                  //
                  // We need to maintain some white space for the stripping to work as expected
                  //
                  sb.replace(index, index + 1, " ");
               }
               index = sb.indexOf("\n", index);
            }
            text = sb.toString();
         }
         text = text.trim();
      }
      return text;
   }

   /**
    * Remove the trailing line end from an RTF block.
    * 
    * @param text source text
    * @param formalRTF true if this is a real RTF block
    * @return text with line end stripped
    */
   private static String stripExtraLineEnd(String text, boolean formalRTF)
   {
      if (formalRTF && text.endsWith("\n"))
      {
         text = text.substring(0, text.length() - 1);
      }
      return text;
   }

   /**
    * Pattern used to match RTF syntax.
    * 
    * Despite its size, this is actually a relatively simple expression. The format
    * below breaks this down into a series of "or" statements... so what we're saying is
    * return a matched group to the caller if our input text matches expression 1 
    * or matches expression 2... and so on. Broadly speaking we're just tokenizing the RTF
    * syntax.
    * 
    * The expression is complicated by the requirement to escape special characters -
    * backslash in particular. To manipulate this I'd recommend using System.println to print the expression
    * out (to get rid of the Java character escaping) and then use a copy of RegExBuilder
    * (http://www.redfernplace.com/software-projects/regex-builder/) to visualise
    * what the expression is doing.
    */
   private static final String RTF_PATTERN_TEXT = "" //
            + "(\\\\\\{)|(\\\\\\})|" //
            + "(\\{)|(\\})|" //
            + "(\\\\\\\\)|" //
            + "(\\\\~)|" //
            + "(\\{\\\\stylesheet.*\\{.*\\}\\{.*\\}\\})|" //
            + "(\\{\\\\[A-Za-z]* .*\\})|" //
            + "(\\\\[A-Za-z]* .*;\\})|" //
            + "(\\\\[A-Za-z]*-?[0-9]* .*;\\})|" //
            + "(\\\\[A-Za-z]*-?[0-9]+ )|" //
            + "(\\\\[A-Za-z]*-?[0-9]+)|" //
            + "(\\\\\\*)|" //
            + "(\\\\[A-Za-z]* )|" // 
            + "(\\\\[A-Za-z]*)|" //
            + "(\\r\\n)";

   private static final Pattern RTF_PATTERN = Pattern.compile(RTF_PATTERN_TEXT);

   private static final Map<String, String> RTF_MAPPING = new HashMap<String, String>();
   static
   {
      RTF_MAPPING.put("\\par", "\n");
      RTF_MAPPING.put("\\tab", "\t");
      RTF_MAPPING.put("\\\\", "\\");
      RTF_MAPPING.put("\\{", "{");
      RTF_MAPPING.put("\\}", "}");
      RTF_MAPPING.put("\\rquote", "’");
      RTF_MAPPING.put("\\endash", "–");
      RTF_MAPPING.put("\\ldblquote", "“");
      RTF_MAPPING.put("\\rdblquote", "”");
      RTF_MAPPING.put("\\~", " ");
   }

   /**
    * Mapping between locale IDs and Java character encoding names.
    */
   private static final Map<String, String> LOCALEID_MAPPING = new HashMap<String, String>();
   static
   {
      LOCALEID_MAPPING.put("1025", "Cp1256"); // Arabic (Saudi Arabia)
      LOCALEID_MAPPING.put("1026", "Cp1251"); // Bulgarian      
      LOCALEID_MAPPING.put("1028", "Cp950"); // Chinese (Taiwan)
      LOCALEID_MAPPING.put("1029", "Cp1250"); // Czech      
      LOCALEID_MAPPING.put("1032", "Cp1253"); // Greek            
      LOCALEID_MAPPING.put("1037", "Cp1255"); // Hebrew
      LOCALEID_MAPPING.put("1038", "Cp1250"); // Hungarian      
      LOCALEID_MAPPING.put("1041", "SJIS"); // Japanese
      LOCALEID_MAPPING.put("1042", "Cp949"); // Korean      
      LOCALEID_MAPPING.put("1045", "Cp1250"); // Polish      
      LOCALEID_MAPPING.put("1048", "Cp1250"); // Romanian
      LOCALEID_MAPPING.put("1049", "Cp1251"); // Russian
      LOCALEID_MAPPING.put("1050", "Cp1250"); // Croatian
      LOCALEID_MAPPING.put("1051", "Cp1250"); // Slovak
      LOCALEID_MAPPING.put("1052", "Cp1250"); // Albanian     
      LOCALEID_MAPPING.put("1054", "Cp874"); // Thai
      LOCALEID_MAPPING.put("1055", "Cp1254"); // Turkish
      LOCALEID_MAPPING.put("1056", "Cp1256"); // Urdu      
      LOCALEID_MAPPING.put("1058", "Cp1251"); // Ukrainian
      LOCALEID_MAPPING.put("1059", "Cp1251"); // Belarusian
      LOCALEID_MAPPING.put("1060", "Cp1250"); // Slovenian
      LOCALEID_MAPPING.put("1061", "Cp1257"); // Estonian
      LOCALEID_MAPPING.put("1062", "Cp1257"); // Latvian
      LOCALEID_MAPPING.put("1063", "Cp1257"); // Lithuanian
      LOCALEID_MAPPING.put("1065", "Cp1256"); // Farsi
      LOCALEID_MAPPING.put("1066", "Cp1258"); // Vietnamese
      LOCALEID_MAPPING.put("1068", "Cp1254"); // Azeri (Latin)      
      LOCALEID_MAPPING.put("1071", "Cp1251"); // FYRO Macedonian      
      LOCALEID_MAPPING.put("1087", "Cp1251"); // Kazakh
      LOCALEID_MAPPING.put("1088", "Cp1251"); // Kyrgyz (Cyrillic)      
      LOCALEID_MAPPING.put("1091", "Cp1254"); // Uzbek (Latin)
      LOCALEID_MAPPING.put("1092", "Cp1251"); // Tatar
      LOCALEID_MAPPING.put("1104", "Cp1251"); // Mongolian (Cyrillic)      
      LOCALEID_MAPPING.put("2049", "Cp1256"); // Arabic (Iraq)
      LOCALEID_MAPPING.put("2052", "MS936"); // Chinese (PRC)      
      LOCALEID_MAPPING.put("2074", "Cp1250"); // Serbian (Latin)      
      LOCALEID_MAPPING.put("2092", "Cp1251"); // Azeri (Cyrillic)      
      LOCALEID_MAPPING.put("2115", "Cp1251"); // Uzbek (Cyrillic)
      LOCALEID_MAPPING.put("3073", "Cp1256"); // Arabic (Egypt)
      LOCALEID_MAPPING.put("3076", "Cp950"); // Chinese (Hong Kong S.A.R.)      
      LOCALEID_MAPPING.put("3098", "Cp1251"); // Serbian (Cyrillic)
      LOCALEID_MAPPING.put("4097", "Cp1256"); // Arabic (Libya)
      LOCALEID_MAPPING.put("4100", "MS936"); // Chinese (Singapore)      
      LOCALEID_MAPPING.put("5121", "Cp1256"); // Arabic (Algeria)
      LOCALEID_MAPPING.put("5124", "Cp950"); // Chinese (Macau S.A.R.)      
      LOCALEID_MAPPING.put("6145", "Cp1256"); // Arabic (Morocco)      
      LOCALEID_MAPPING.put("7169", "Cp1256"); // Arabic (Tunisia)      
      LOCALEID_MAPPING.put("8193", "Cp1256"); // Arabic (Oman)      
      LOCALEID_MAPPING.put("9217", "Cp1256"); // Arabic (Yemen)      
      LOCALEID_MAPPING.put("10241", "Cp1256"); // Arabic (Syria)      
      LOCALEID_MAPPING.put("11265", "Cp1256"); // Arabic (Jordan)      
      LOCALEID_MAPPING.put("12289", "Cp1256"); // Arabic (Lebanon)      
      LOCALEID_MAPPING.put("13313", "Cp1256"); // Arabic (Kuwait)      
      LOCALEID_MAPPING.put("14337", "Cp1256"); // Arabic (U.A.E.)      
      LOCALEID_MAPPING.put("15361", "Cp1256"); // Arabic (Bahrain)      
      LOCALEID_MAPPING.put("16385", "Cp1256"); // Arabic (Qatar)
   }

   private static final String DEFAULT_ENCODING = "Cp1252";
}
