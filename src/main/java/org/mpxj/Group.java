/*
 * file:       Group.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       17/01/2007
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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the definition of the grouping used
 * to organise data in a view.
 */
public final class Group
{
   /**
    * Constructor.
    *
    * @param id group identifier
    * @param name group name
    * @param showSummaryTasks show summary tasks
    */
   public Group(Integer id, String name, boolean showSummaryTasks)
   {
      m_id = id;
      m_name = name;
      m_showSummaryTasks = showSummaryTasks;
   }

   /**
    * Retrieve group ID.
    *
    * @return group ID
    */
   public Integer getID()
   {
      return m_id;
   }

   /**
    * Retrieve the group name.
    *
    * @return group name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the show summary tasks flag.
    *
    * @return boolean flag
    */
   public boolean getShowSummaryTasks()
   {
      return m_showSummaryTasks;
   }

   /**
    * Adds a clause to the group definition.
    *
    * @param clause group clause
    */
   public void addGroupClause(GroupClause clause)
   {
      m_clauses.add(clause);
   }

   /**
    * Retrieve a list of all clauses which define this group.
    *
    * @return list of clauses
    */
   public List<GroupClause> getGroupClauses()
   {
      return (m_clauses);
   }

   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("[Group ");
      pw.println(" id=" + m_id);
      pw.println(" name=" + m_name);
      pw.println(" showSummaryTasks=" + m_showSummaryTasks);
      pw.println(" [Clauses=");
      for (GroupClause gc : m_clauses)
      {
         pw.println("  " + gc);
      }
      pw.println(" ]");
      pw.println("]");
      pw.flush();
      return (os.toString());

   }

   private final Integer m_id;
   private final String m_name;
   private final boolean m_showSummaryTasks;
   private final List<GroupClause> m_clauses = new ArrayList<>();
}
