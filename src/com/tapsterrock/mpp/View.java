/*
 * file:       View.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2003
 * date:       27/10/2003
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


/**
 * This class represents a view of a set of project data that has been 
 * instantiated within an MS Project file. View data is instantiated when a user
 * first looks at a view in MS Project. Each "screen" in MS Project, for example
 * the Gantt Chart, the Resource Sheet and so on are views. If a user has not 
 * looked at a view (for example the Resource Usage view), information about 
 * that view will not be present in the MPP file.
 */
public final class View
{
   /**
    * This method is used to retrieve the unique view identifier. This
    * value identifies the view within the file. It does not identify
    * the type of view represented by an instance of this class.
    * 
    * @return view identifier
    */
   public int getID ()
   {
      return (m_id);   
   }

   /**
    * This method is used to to set the unique identifier associated with
    * this view.
    * 
    * @param id unique view identifier
    */
   public void setID (int id)
   {
      m_id = id;   
   }
   
   /**
    * This method is used to retrieve the view name. Note that internally
    * in MS Project the view name will contain an ampersand (&) used to
    * flag the letter that can be used as a shortcut for this view. The
    * ampersand is stripped out by MPXJ.
    * 
    * @return view name
    */
   public String getName ()
   {
      return (m_name);   
   }
   
   /**
    * This method is used to set the name associated with this view.
    * 
    * @param name view name
    */
   public void setName (String name)
   {
      m_name = name;         
   }
   
   /**
    * This method dumps the contents of this View as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this view
    */
   public String toString ()
   {
      return ("[VIEW id=" + m_id + " name=" + m_name +"]");
   }
         
   private int m_id;
   private String m_name;   
}
