/*
 * file:       HtmlHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       23/12/2020
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

package org.mpxj.common;

import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

/**
 * HTML helper methods.
 */
public final class HtmlHelper
{
   /**
    * Rudimentary conversion of plain text to HTML, preserving line breaks.
    *
    * @param text plain text
    * @return HTML document
    */
   public static String getHtmlFromPlainText(String text)
   {
      return "<html><body>" + text.replace("\n", "<br/>\n") + "</body></html>";
   }

   /**
    * Extract plain text from HTML.
    *
    * @param html HTML document
    * @return plain text
    */
   public static String strip(String html)
   {
      if (html == null)
      {
         return null;
      }

      String result = html;

      // Determine if we have HTML
      int htmlIndex = result.indexOf("<HTML>");
      if (htmlIndex == -1)
      {
         htmlIndex = result.indexOf("<html>");
      }

      // Even if the note doesn't contain an HTML tag,
      // it may contain embedded HTML. We treat all text
      // as an HTML body fragment and let the parser sort it out.
      if (htmlIndex == -1)
      {
         result = getPlainTextFromBodyFragment(result);
      }
      else
      {
         result = getPlainTextFromHtml(result.substring(htmlIndex));
      }

      // Trim any whitespace (including nbsp)
      // https://stackoverflow.com/questions/28295504/how-to-trim-no-break-space-in-java/28295597
      result = result.replaceAll("(^\\h*)|(\\h*$)", "").trim();

      // Return null if we have an empty string
      if (result.isEmpty())
      {
         result = null;
      }

      return result;
   }

   /**
    * Extract plain text from a full HTML document.
    *
    * @param html HTML document
    * @return plain text
    */
   public static String getPlainTextFromHtml(String html)
   {
      return getPlainText(Jsoup.parse(html));
   }

   /**
    * Extract plain text from text which may contain HTML elements.
    *
    * @param body body text
    * @return plain text
    */
   public static String getPlainTextFromBodyFragment(String body)
   {
      return getPlainText(Jsoup.parseBodyFragment(body));
   }

   /**
    * Traverse an HTML document extracting plain text.
    *
    * @param document HTML document
    * @return plain text
    */
   private static String getPlainText(Document document)
   {
      FormattingVisitor formatter = new FormattingVisitor();
      NodeTraversor.traverse(formatter, document);
      return formatter.toString();
   }

   /**
    * Based on HtmlToPlainText from <a href="https://github.com/jhy/jsoup">https://github.com/jhy/jsoup</a>.
    */
   private static class FormattingVisitor implements NodeVisitor
   {
      /**
       * Constructor.
       */
      public FormattingVisitor()
      {
         // only here to avoid warning
      }

      /**
       * Called when the node is first seen.
       *
       * @param node current node
       * @param depth depth in tree
       */
      @Override public void head(Node node, int depth)
      {
         String name = node.nodeName();
         if (node instanceof TextNode)
         {
            append(((TextNode) node).text()); // TextNodes carry all user-readable text in the DOM.
         }
         else
         {
            if (name.equals("li"))
            {
               append("\n * ");
            }
            else
            {
               if (name.equals("dt"))
               {
                  append("  ");
               }
               else
               {
                  if (StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr"))
                  {
                     append("\n");
                  }
               }
            }
         }
      }

      /**
       * Called when all of the node's children have been visited.
       *
       * @param node current node
       * @param depth depth in the tree
       */
      @Override public void tail(Node node, int depth)
      {
         String name = node.nodeName();
         if (StringUtil.in(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5"))
         {
            append("\n");
         }
         else
         {
            if (name.equals("a"))
            {
               append(String.format(" <%s>", node.absUrl("href")));
            }
         }
      }

      /**
       * Append new text to the buffer.
       *
       * @param text text to append
       */
      private void append(String text)
      {
         // Avoid long runs of whitespace
         if (!(text.equals(" ") && (m_buffer.length() == 0 || StringUtil.in(m_buffer.substring(m_buffer.length() - 1), " ", "\n"))))
         {
            m_buffer.append(text);
         }
      }

      /**
       * Retrieve the extracted text.
       */
      @Override public String toString()
      {
         return m_buffer.toString();
      }

      private final StringBuilder m_buffer = new StringBuilder();
   }
}