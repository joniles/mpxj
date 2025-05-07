/*
 * file:       View.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Jan 27, 2006
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

package org.mpxj;

/**
 * This interface represents a view of a set of project data that has been
 * instantiated within an MS Project file. View data is instantiated when a user
 * first looks at a view in MS Project. Each "screen" in MS Project, for example
 * the Gantt Chart, the Resource Sheet and so on are views. If a user has not
 * looked at a view (for example the Resource Usage view), information about
 * that view will not be present in the MPP file.
 */
public interface View
{
   /**
    * This method is used to retrieve the unique view identifier. This
    * value identifies the view within the file. It does not identify
    * the type of view represented by an instance of this class.
    *
    * @return view identifier
    */
   Integer getID();

   /**
    * This method is used to retrieve the view name. Note that internally
    * in MS Project the view name will contain an ampersand (&amp;) used to
    * flag the letter that can be used as a shortcut for this view. The
    * ampersand is stripped out by MPXJ.
    *
    * @return view name
    */
   String getName();

   /**
    * Retrieves the view type.
    *
    * @return view type
    */
   ViewType getType();

   /**
    * Retrieve the name of the table part of the view.
    *
    * @return table name
    */
   String getTableName();

   /**
    * Retrieve an instance of the Table class representing the
    * table part of this view.
    *
    * @return table instance
    */
   Table getTable();
}