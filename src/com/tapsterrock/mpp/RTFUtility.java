/*
 * file:       RTFUtility.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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
 
package com.tapsterrock.mpp;

import java.io.StringReader;

import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * This class is used to collect together utility functions for manipulating 
 * RTF encoded text.
 */
final class RTFUtility
{
   /**
    * This method removes all RTF formatting from a given piece of text.
    * 
    * @param text Text from which the RTF formatting is to be removed.
    * @return Plain text
    */
   public String strip (String text)
   {
      initialise();
      
      String result;
      
      try
      {
         int length = m_doc.getLength();
         if (length != 0)
         {
            m_doc.remove(0, length);
         }
           
         StringReader reader = new StringReader (text);
               
         m_editor.read(reader, m_doc, 0);
               
         result = m_doc.getText(0, m_doc.getLength());
      }
      
      catch (Exception ex)
      {
         result = text;
      }         

      return (result);            
   }
   
   /**
    * This method is used to initialise the underlying objects that are used
    * by this utility class. We use lazy instantiation here to avoid the
    * overhead of creating these objects if they are not used.
    */
   private void initialise ()
   {
      if (m_editor == null)
      {
         m_editor = new RTFEditorKit ();
      }
      
      if (m_doc == null)
      {
         m_doc = m_editor.createDefaultDocument();
      }                  
   }
   
   private RTFEditorKit m_editor = null;
   private Document m_doc = null;
}
