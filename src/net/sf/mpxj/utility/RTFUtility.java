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
    * This method removes all RTF formatting from a given piece of text.
    *
    * @param text Text from which the RTF formatting is to be removed.
    * @return Plain text
    */
   public String strip(String text)
   {
      text = processDoubleByteChars(text);
      text = regexpStrip(text);
      return text;
   }

   /**
    * This method converts any encoded double byte characters
    * to Unicode.
    * 
    * @param text RTF text
    * @return RTF text with Unicode characters
    */
   private String processDoubleByteChars(String text)
   {
      String[] tokens = text.split("\\\\");
      int index = 0;
      String currentEncoding = null;
      StringBuffer result = new StringBuffer(text.length());
      boolean collectingBytes = false;
      LinkedList<String> bytes = new LinkedList<String>();
      boolean firstWord = true;

      while (index < tokens.length)
      {
         String token = tokens[index];
         if (token.length() != 0)
         {
            if (token.charAt(0) == '\'')
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
    * Extracts a locale ID from an RTF command and converts 
    * it to a character encoding.
    * 
    * @param token RTF command
    * @return encoding
    */
   private String processEncoding(String token)
   {
      String localeID = null;

      int index = 0;
      while (index < token.length() && !Character.isDigit(token.charAt(index)))
      {
         ++index;
      }

      if (index != token.length())
      {
         StringBuffer sb = new StringBuffer(token.length());
         while (index < token.length() && Character.isDigit(token.charAt(index)))
         {
            sb.append(token.charAt(index));
            ++index;
         }
         localeID = sb.toString();
      }

      String encoding = LOCALEID_MAPPING.get(localeID);

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
   private String processBytes(LinkedList<String> bytes, String currentEncoding)
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
         ex.printStackTrace();
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
   private String regexpStrip(String rtf)
   {
      rtf = stripCommands("{\\object", rtf);

      //System.out.println(RTF_PATTERN.pattern());
      StringBuffer sb = new StringBuffer();
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
      }
      catch (Exception ex)
      {
         System.out.println(ex);
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
   private String stripCommands(String command, String rtf)
   {
      //
      // Do we have embedded binary data?
      //
      int startIndex = rtf.indexOf(command);
      if (startIndex != -1)
      {
         StringBuffer sb = new StringBuffer(rtf);
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
                  case '{' :
                  {
                     ++nesting;
                     break;
                  }

                  case '}' :
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
    * Pattern used to match RTF syntax.
    */
   private static final Pattern RTF_PATTERN = Pattern.compile("(\\\\\\\\)|(\\\\~)|(\\{\\\\stylesheet.*\\{.*\\}\\{.*\\}\\})|(\\{\\\\[A-Za-z]* .*\\})|(\\\\[A-Za-z]* .*;\\})|(\\\\[A-Za-z]*-?[0-9]* .*;\\})|(\\\\[A-Za-z]*-?[0-9]+ )|(\\\\[A-Za-z]*-?[0-9]+)|(\\\\\\*)|(\\\\[A-Za-z]* )|(\\\\[A-Za-z]*)|(\\\\\\{)|(\\\\\\})|(\\{)|(\\})|(\\r\\n)");

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
      LOCALEID_MAPPING.put("1027", "Cp1252"); // Catalan
      LOCALEID_MAPPING.put("1028", "Cp950"); // Chinese (Taiwan)
      LOCALEID_MAPPING.put("1029", "Cp1250"); // Czech
      LOCALEID_MAPPING.put("1030", "Cp1252"); // Danish
      LOCALEID_MAPPING.put("1031", "Cp1252"); // German (Germany)
      LOCALEID_MAPPING.put("1032", "Cp1253"); // Greek
      LOCALEID_MAPPING.put("1033", "Cp1252"); // English (United States)
      LOCALEID_MAPPING.put("1034", "Cp1252"); // Spanish (Traditional Sort)
      LOCALEID_MAPPING.put("1035", "Cp1252"); // Finnish
      LOCALEID_MAPPING.put("1036", "Cp1252"); // French (France)
      LOCALEID_MAPPING.put("1037", "Cp1255"); // Hebrew
      LOCALEID_MAPPING.put("1038", "Cp1250"); // Hungarian
      LOCALEID_MAPPING.put("1039", "Cp1252"); // Icelandic
      LOCALEID_MAPPING.put("1040", "Cp1252"); // Italian (Italy)
      LOCALEID_MAPPING.put("1041", "SJIS"); // Japanese
      LOCALEID_MAPPING.put("1042", "Cp949"); // Korean
      LOCALEID_MAPPING.put("1043", "Cp1252"); // Dutch (Netherlands)
      LOCALEID_MAPPING.put("1044", "Cp1252"); // Norwegian (Bokmal)
      LOCALEID_MAPPING.put("1045", "Cp1250"); // Polish
      LOCALEID_MAPPING.put("1046", "Cp1252"); // Portuguese (Brazil)
      LOCALEID_MAPPING.put("1048", "Cp1250"); // Romanian
      LOCALEID_MAPPING.put("1049", "Cp1251"); // Russian
      LOCALEID_MAPPING.put("1050", "Cp1250"); // Croatian
      LOCALEID_MAPPING.put("1051", "Cp1250"); // Slovak
      LOCALEID_MAPPING.put("1052", "Cp1250"); // Albanian
      LOCALEID_MAPPING.put("1053", "Cp1252"); // Swedish
      LOCALEID_MAPPING.put("1054", "Cp874"); // Thai
      LOCALEID_MAPPING.put("1055", "Cp1254"); // Turkish
      LOCALEID_MAPPING.put("1056", "Cp1256"); // Urdu
      LOCALEID_MAPPING.put("1057", "Cp1252"); // Indonesian
      LOCALEID_MAPPING.put("1058", "Cp1251"); // Ukrainian
      LOCALEID_MAPPING.put("1059", "Cp1251"); // Belarusian
      LOCALEID_MAPPING.put("1060", "Cp1250"); // Slovenian
      LOCALEID_MAPPING.put("1061", "Cp1257"); // Estonian
      LOCALEID_MAPPING.put("1062", "Cp1257"); // Latvian
      LOCALEID_MAPPING.put("1063", "Cp1257"); // Lithuanian
      LOCALEID_MAPPING.put("1065", "Cp1256"); // Farsi
      LOCALEID_MAPPING.put("1066", "Cp1258"); // Vietnamese
      LOCALEID_MAPPING.put("1068", "Cp1254"); // Azeri (Latin)
      LOCALEID_MAPPING.put("1069", "Cp1252"); // Basque
      LOCALEID_MAPPING.put("1071", "Cp1251"); // FYRO Macedonian
      LOCALEID_MAPPING.put("1078", "Cp1252"); // Afrikaans
      LOCALEID_MAPPING.put("1080", "Cp1252"); // Faroese
      LOCALEID_MAPPING.put("1086", "Cp1252"); // Malay (Malaysia)
      LOCALEID_MAPPING.put("1087", "Cp1251"); // Kazakh
      LOCALEID_MAPPING.put("1088", "Cp1251"); // Kyrgyz (Cyrillic)
      LOCALEID_MAPPING.put("1089", "Cp1252"); // Swahili
      LOCALEID_MAPPING.put("1091", "Cp1254"); // Uzbek (Latin)
      LOCALEID_MAPPING.put("1092", "Cp1251"); // Tatar
      LOCALEID_MAPPING.put("1104", "Cp1251"); // Mongolian (Cyrillic)
      LOCALEID_MAPPING.put("1110", "Cp1252"); // Galician
      LOCALEID_MAPPING.put("2049", "Cp1256"); // Arabic (Iraq)
      LOCALEID_MAPPING.put("2052", "MS936"); // Chinese (PRC)
      LOCALEID_MAPPING.put("2055", "Cp1252"); // German (Switzerland)
      LOCALEID_MAPPING.put("2057", "Cp1252"); // English (United Kingdom)
      LOCALEID_MAPPING.put("2058", "Cp1252"); // Spanish (Mexico)
      LOCALEID_MAPPING.put("2060", "Cp1252"); // French (Belgium)
      LOCALEID_MAPPING.put("2064", "Cp1252"); // Italian (Switzerland)
      LOCALEID_MAPPING.put("2067", "Cp1252"); // Dutch (Belgium)
      LOCALEID_MAPPING.put("2068", "Cp1252"); // Norwegian (Nynorsk)
      LOCALEID_MAPPING.put("2070", "Cp1252"); // Portuguese (Portugal)
      LOCALEID_MAPPING.put("2074", "Cp1250"); // Serbian (Latin)
      LOCALEID_MAPPING.put("2077", "Cp1252"); // Swedish (Finland)
      LOCALEID_MAPPING.put("2092", "Cp1251"); // Azeri (Cyrillic)
      LOCALEID_MAPPING.put("2110", "Cp1252"); // Malay (Brunei Darussalam)
      LOCALEID_MAPPING.put("2115", "Cp1251"); // Uzbek (Cyrillic)
      LOCALEID_MAPPING.put("3073", "Cp1256"); // Arabic (Egypt)
      LOCALEID_MAPPING.put("3076", "Cp950"); // Chinese (Hong Kong S.A.R.)
      LOCALEID_MAPPING.put("3079", "Cp1252"); // German (Austria)
      LOCALEID_MAPPING.put("3081", "Cp1252"); // English (Australia)
      LOCALEID_MAPPING.put("3082", "Cp1252"); // Spanish (International Sort)
      LOCALEID_MAPPING.put("3084", "Cp1252"); // French (Canada)
      LOCALEID_MAPPING.put("3098", "Cp1251"); // Serbian (Cyrillic)
      LOCALEID_MAPPING.put("4097", "Cp1256"); // Arabic (Libya)
      LOCALEID_MAPPING.put("4100", "MS936"); // Chinese (Singapore)
      LOCALEID_MAPPING.put("4103", "Cp1252"); // German (Luxembourg)
      LOCALEID_MAPPING.put("4105", "Cp1252"); // English (Canada)
      LOCALEID_MAPPING.put("4106", "Cp1252"); // Spanish (Guatemala)
      LOCALEID_MAPPING.put("4108", "Cp1252"); // French (Switzerland)
      LOCALEID_MAPPING.put("5121", "Cp1256"); // Arabic (Algeria)
      LOCALEID_MAPPING.put("5124", "Cp950"); // Chinese (Macau S.A.R.)
      LOCALEID_MAPPING.put("5127", "Cp1252"); // German (Liechtenstein)
      LOCALEID_MAPPING.put("5129", "Cp1252"); // English (New Zealand)
      LOCALEID_MAPPING.put("5130", "Cp1252"); // Spanish (Costa Rica)
      LOCALEID_MAPPING.put("5132", "Cp1252"); // French (Luxembourg)
      LOCALEID_MAPPING.put("6145", "Cp1256"); // Arabic (Morocco)
      LOCALEID_MAPPING.put("6153", "Cp1252"); // English (Ireland)
      LOCALEID_MAPPING.put("6154", "Cp1252"); // Spanish (Panama)
      LOCALEID_MAPPING.put("6156", "Cp1252"); // French (Monaco)
      LOCALEID_MAPPING.put("7169", "Cp1256"); // Arabic (Tunisia)
      LOCALEID_MAPPING.put("7177", "Cp1252"); // English (South Africa)
      LOCALEID_MAPPING.put("7178", "Cp1252"); // Spanish (Dominican Republic)
      LOCALEID_MAPPING.put("8193", "Cp1256"); // Arabic (Oman)
      LOCALEID_MAPPING.put("8201", "Cp1252"); // English (Jamaica)
      LOCALEID_MAPPING.put("8202", "Cp1252"); // Spanish (Venezuela)
      LOCALEID_MAPPING.put("9217", "Cp1256"); // Arabic (Yemen)
      LOCALEID_MAPPING.put("9225", "Cp1252"); // English (Caribbean)
      LOCALEID_MAPPING.put("9226", "Cp1252"); // Spanish (Colombia)
      LOCALEID_MAPPING.put("10241", "Cp1256"); // Arabic (Syria)
      LOCALEID_MAPPING.put("10249", "Cp1252"); // English (Belize)
      LOCALEID_MAPPING.put("10250", "Cp1252"); // Spanish (Peru)
      LOCALEID_MAPPING.put("11265", "Cp1256"); // Arabic (Jordan)
      LOCALEID_MAPPING.put("11273", "Cp1252"); // English (Trinidad)
      LOCALEID_MAPPING.put("11274", "Cp1252"); // Spanish (Argentina)
      LOCALEID_MAPPING.put("12289", "Cp1256"); // Arabic (Lebanon)
      LOCALEID_MAPPING.put("12297", "Cp1252"); // English (Zimbabwe)
      LOCALEID_MAPPING.put("12298", "Cp1252"); // Spanish (Ecuador)
      LOCALEID_MAPPING.put("13313", "Cp1256"); // Arabic (Kuwait)
      LOCALEID_MAPPING.put("13321", "Cp1252"); // English (Philippines)
      LOCALEID_MAPPING.put("13322", "Cp1252"); // Spanish (Chile)
      LOCALEID_MAPPING.put("14337", "Cp1256"); // Arabic (U.A.E.)
      LOCALEID_MAPPING.put("14346", "Cp1252"); // Spanish (Uruguay)
      LOCALEID_MAPPING.put("15361", "Cp1256"); // Arabic (Bahrain)
      LOCALEID_MAPPING.put("15370", "Cp1252"); // Spanish (Paraguay)
      LOCALEID_MAPPING.put("16385", "Cp1256"); // Arabic (Qatar)
      LOCALEID_MAPPING.put("16394", "Cp1252"); // Spanish (Bolivia)
      LOCALEID_MAPPING.put("17418", "Cp1252"); // Spanish (El Salvador)
      LOCALEID_MAPPING.put("18442", "Cp1252"); // Spanish (Honduras)
      LOCALEID_MAPPING.put("19466", "Cp1252"); // Spanish (Nicaragua)
      LOCALEID_MAPPING.put("20490", "Cp1252"); // Spanish (Puerto Rico)      
   }
}
