/*
 * file:       GanttBarStyle.java
 * author:     Jon Iles
 *             Tom Ollar
 * copyright:  (c) Packwood Software 2005-2009
 * date:       13/04/2005
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

package net.sf.mpxj.mpp;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;

import net.sf.mpxj.TaskField;

/**
 * This class represents the default style for a Gantt chart bar.
 */
public final class GanttBarStyle extends GanttBarCommonStyle
{
   /**
    * Retrieve the field used to determine the start date of this bar.
    *
    * @return from field
    */
   public TaskField getFromField()
   {
      return (m_fromField);
   }

   /**
    * Set the field used to determine the start date of this bar.
    *
    * @param field from field
    */
   public void setFromField(TaskField field)
   {
      m_fromField = field;
   }

   /**
    * Retrieve the name of this style.
    *
    * @return style name
    */
   public String getName()
   {
      return (m_name);
   }

   /**
    * Sets the name of this style.
    *
    * @param name style name
    */
   public void setName(String name)
   {
      m_name = name;
   }

   /**
    * Retrieve the row number of this bar.
    *
    * @return row number
    */
   public int getRow()
   {
      return (m_row);
   }

   /**
    * Sets the row number of this style.
    *
    * @param row row number
    */
   public void setRow(int row)
   {
      m_row = row;
   }

   /**
    * Retrieve the field used to determine the end date of this bar.
    *
    * @return to field
    */
   public TaskField getToField()
   {
      return (m_toField);
   }

   /**
    * Sets the field used to determine the end date of this bar.
    *
    * @param field to field
    */
   public void setToField(TaskField field)
   {
      m_toField = field;
   }

   /**
    * Retrieve set of Show For criteria for this style.
    *
    * @return show for criteria
    */
   public Set<GanttBarShowForTasks> getShowForTasks()
   {
      return (m_showForTasks);
   }

   /**
    * Adds a Show For criteria entry for this style.
    *
    * @param tasks Show For entry criteria
    */
   public void addShowForTasks(GanttBarShowForTasks tasks)
   {
      m_showForTasks.add(tasks);
   }

   /**
    * Generate a string representation of this instance.
    *
    * @return string representation of this instance
    */
   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("   [GanttBarStyle");
      pw.println("      Name=" + m_name);
      pw.println("      FromField=" + m_fromField);
      pw.println("      ToField=" + m_toField);
      pw.println("      Row=" + m_row);
      pw.println("      ShowForTasks=" + m_showForTasks);
      pw.println(super.toString());
      pw.println("   ]");
      pw.flush();
      return (os.toString());
   }

   private String m_name;
   private TaskField m_fromField;
   private TaskField m_toField;
   private int m_row;
   private Set<GanttBarShowForTasks> m_showForTasks = new TreeSet<GanttBarShowForTasks>();
}
