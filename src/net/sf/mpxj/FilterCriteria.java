/*
 * file:       FilterCriteria.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2006
 * date:       30/10/2006
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

package net.sf.mpxj;

/**
 * This class represents the criteria used to determine if a row is filtered
 * from a view.
 */
public final class FilterCriteria extends GenericCriteria
{
   /**
    * Constructor.
    * 
    * @param projectFile parent project file
    */
   public FilterCriteria(ProjectFile projectFile)
   {
      super(projectFile);
   }

   /**
    * Called to evaluate whether a row on a task or resource view should 
    * be filtered. Returns false if the row should be filtered out.
    * 
    * @param container task or resource data container
    * @return boolean flag
    */
   public boolean evaluate(FieldContainer container)
   {
      return (evaluateCriteria(container));
   }

   /**
    * Flag indicating if a logical AND operator is used to join the 
    * following filter criteria (true) or a logical OR (false).
    * 
    * @param logicalAnd boolean flag
    */
   public void setLogicalAnd(boolean logicalAnd)
   {
      m_logicalAnd = logicalAnd;
   }

   /**
    * Retrieves the "logical and" flag.
    * 
    * @return boolean flag
    */
   public boolean getLogicalAnd()
   {
      return (m_logicalAnd);
   }

   /**
    * This method retrieves the given item of prompt text.
    * 
    * @param index text index, either 0 or 1
    * @return prompt text
    */
   public String getPromptText(int index)
   {
      return (m_promptText[index]);
   }

   /**
    * Sets an item of prompt text. Note that calling this method
    * sets a flag indicating that the criteria has prompt text associated
    * with it.
    * 
    * @param index text index, either 0 or 1
    * @param text prompt text
    */
   public void setPromptText(int index, String text)
   {
      m_promptText[index] = text;
      m_promptTextSet = true;
   }

   /**
    * Retrieves a flag indicating if prompt text has been set for 
    * this criteria. This saves having to test both items of
    * prompt text individually.
    * 
    * @return boolean flag
    */
   public boolean getPromptTextSet()
   {
      return (m_promptTextSet);
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("[FilterCriteria");
      sb.append(" logicalAnd=");
      sb.append(m_logicalAnd);

      if (m_promptTextSet)
      {
         sb.append(" promptText=[");
         sb.append(m_promptText[0]);
         sb.append(",");
         sb.append(m_promptText[1]);
         sb.append("]");
      }

      sb.append(" criteria=");
      sb.append(super.toString());
      sb.append("]");
      return (sb.toString());
   }

   private boolean m_logicalAnd;
   private String[] m_promptText = new String[2];
   private boolean m_promptTextSet;
}
