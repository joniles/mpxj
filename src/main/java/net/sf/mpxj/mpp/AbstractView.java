/*
 * file:       AbstractView.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2003
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

package net.sf.mpxj.mpp;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Table;
import net.sf.mpxj.TableContainer;
import net.sf.mpxj.View;
import net.sf.mpxj.ViewType;

/**
 * This abstract class implements functionality common to all views.
 */
public abstract class AbstractView implements View
{
   /**
    * Constructor.
    *
    * @param parent parent file
    */
   public AbstractView(ProjectFile parent)
   {
      m_properties = parent.getProjectProperties();
      m_tables = parent.getTables();
   }

   /**
    * {@inheritDoc}
    */
   @Override public Integer getID()
   {
      return (m_id);
   }

   /**
    * {@inheritDoc}
    */
   @Override public String getName()
   {
      return (m_name);
   }

   /**
    * {@inheritDoc}
    */
   @Override public ViewType getType()
   {
      return (m_type);
   }

   /**
    * Retrieve the name of the table part of the view.
    *
    * @return table name
    */
   public String getTableName()
   {
      return (m_tableName);
   }

   /**
    * Retrieve an instance of the Table class representing the
    * table part of this view.
    *
    * @return table instance
    */
   public Table getTable()
   {
      return (m_tables.getTaskTableByName(m_tableName));
   }

   /**
    * This method dumps the contents of this View as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this view
    */
   @Override public String toString()
   {
      return ("[View id=" + m_id + " type=" + m_type + " name=" + m_name + (m_tableName == null ? "" : " table=" + m_tableName) + "]");
   }

   protected ProjectProperties m_properties;
   protected TableContainer m_tables;
   protected Integer m_id;
   protected String m_name;
   protected ViewType m_type;
   protected String m_tableName;
}
