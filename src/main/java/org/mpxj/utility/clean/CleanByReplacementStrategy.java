/*
 * file:       CleanByReplacementStrategy.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       03/01/2022
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

package org.mpxj.utility.clean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleans text by replacing it with random replacements words.
 */
public class CleanByReplacementStrategy implements CleanStrategy
{
   /**
    * Constructor.
    */
   public CleanByReplacementStrategy()
   {
      loadDictionary();
   }

   @Override public String generateReplacementText(String oldText)
   {
      StringBuilder sb = new StringBuilder(oldText);
      Matcher matcher = WORD_PATTERN.matcher(oldText);

      while (matcher.find())
      {
         String word = matcher.group(1);
         String replacement;

         if (word.length() > MIN_WORD_LENGTH && isNotNumeric(word))
         {
            replacement = m_words.computeIfAbsent(word, this::generateReplacementWord);
            int end = matcher.end();
            sb.replace(matcher.start(), end, matchCase(word, replacement));
         }
      }

      return sb.toString();
   }

   /**
    * Returns true if the supplied word is not a numeric value.
    *
    * @param word text to test
    * @return true if the text is not numeric
    */
   private boolean isNotNumeric(String word)
   {
      try
      {
         Double.parseDouble(word);
         return false;
      }

      catch (Exception ex)
      {
         return true;
      }
   }

   /**
    * Generate a replacement word which is different to the supplied word.
    *
    * @param word word to replace
    * @return replacement word
    */
   private String generateReplacementWord(String word)
   {
      Integer key = Integer.valueOf(word.length());
      List<String> words = m_dictionary.get(key);
      if (words == null)
      {
         return generateRandomWord(word);
      }

      if (words.size() == 1 && words.get(0).equalsIgnoreCase(word))
      {
         return generateRandomWord(word);
      }

      String replacement;
      do
      {
         int wordIndex = m_random.nextInt(words.size());
         replacement = words.get(wordIndex);
      }
      while (replacement.equalsIgnoreCase(word));

      return replacement;
   }

   /**
    * Where a replacement word can't be sourced directly from the dictionary
    * generate a random word by concatenating dictionary words together
    * until the desired length is reached.
    *
    * @param word original word
    * @return replacement word
    */
   private String generateRandomWord(String word)
   {
      StringBuilder sb = new StringBuilder();
      int targetLength = word.length();

      while (sb.length() < targetLength)
      {
         int wordLength = m_random.nextInt(targetLength);
         List<String> words = m_dictionary.get(Integer.valueOf(wordLength));
         if (words == null)
         {
            continue;
         }
         sb.append(words.get(m_random.nextInt(words.size())));
      }

      sb.setLength(targetLength);

      return sb.toString();
   }

   /**
    * Ensure the case of the replacement word matches the original word.
    *
    * @param oldWord original word
    * @param newWord replacement word
    * @return replacement word with matching case
    */
   private String matchCase(String oldWord, String newWord)
   {
      StringBuilder sb = new StringBuilder(newWord);
      for (int index = 0; index < oldWord.length(); index++)
      {
         if (Character.isUpperCase(oldWord.charAt(index)))
         {
            sb.setCharAt(index, Character.toUpperCase(sb.charAt(index)));
         }
      }
      return sb.toString();
   }

   /**
    * Load the dictionary words.
    */
   private void loadDictionary()
   {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("org/mpxj/utility/clean/words.txt"))))
      {
         while (reader.ready())
         {
            processWord(reader.readLine());
         }
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   /**
    * Populate a map of words keyed by word length.
    *
    * @param word word to add to map
    */
   private void processWord(String word)
   {
      m_dictionary.computeIfAbsent(Integer.valueOf(word.length()), ArrayList::new).add(word);
   }

   private final Map<String, String> m_words = new HashMap<>();
   private final Map<Integer, List<String>> m_dictionary = new HashMap<>();
   private final Random m_random = new Random(8118055L);

   private static final int MIN_WORD_LENGTH = 3;
   private static final Pattern WORD_PATTERN = Pattern.compile("(\\w+)");
}
