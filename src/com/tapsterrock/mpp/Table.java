/*
 * file:       Table.java
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * This class represents the definition of a table of data from an MPP file.
 * Much of the important information held in MS Project is represented
 * in a tabular format. This class represents the attributes associated with
 * these tables. For example, the atributes of the table of data that appears 
 * as the left hand part of the standard Gantt Chart view in MS Project will 
 * be defined here.
 */
public final class Table
{
   /**
    * This method is used to retrieve the unique table identifier. This
    * value identifies the table within the file. It does not identify
    * the type of table represented by an instance of this class.
    * 
    * @return table identifier
    */
   public int getID ()
   {
      return (m_id);   
   }

   /**
    * This method is used to to set the unique identifier associated with
    * this table.
    * 
    * @param id unique table identifier
    */
   public void setID (int id)
   {
      m_id = id;   
   }

   /**
    * This method is used to retrieve the table name. Note that internally
    * in MS Project the table name will contain an ampersand (&) used to
    * flag the letter that can be used as a shortcut for this table. The
    * ampersand is stripped out by MPXJ.
    * 
    * @return view name
    */
   public String getName ()
   {
      return (m_name);   
   }
   
   /**
    * This method is used to set the name associated with this table.
    * 
    * @param name table name
    */
   public void setName (String name)
   {
      m_name = name;         
   }

   /**
    * This method retrieves the flag attribute of the table. For MPP9 files
    * this attribute appears to represent whether the table refers to
    * task data (false) or resource data (true).
    * 
    * @return boolean flag
    */
   public boolean getFlag ()
   {
      return (m_flag);   
   }
        
   /**
    * This method sets the flag attribute of the table. For MPP9 files
    * this attribute appears to represent whether the table refers to
    * task data (false) or resource data (true).
    * 
    * @param flag boolean flag
    */
   public void setFlag (boolean flag)
   {
      m_flag = flag;
   }
   
   /**
    * Adds a column definition to this table
    * 
    * @param column column definition
    */
   public void addColumn (Column column)
   {
      m_columns.add(column);
   }
   
   /**
    * Retrieves the list of columns that make up this table.
    * 
    * @return list of columns
    */
   public ArrayList getColumns ()
   {
      return (m_columns);   
   }
   
   /**
    * This method dumps the contents of this table as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this table
    */
   public String toString ()
   {
      StringWriter sw = new StringWriter ();
      PrintWriter pw = new PrintWriter (sw);
   
      pw.print ("[TABLE id=");
      pw.print (m_id);
      pw.print (" name=");
      pw.print (m_name);
      pw.print (" flag=");
      pw.println (m_flag);
      
      Iterator iter = m_columns.iterator();
      while (iter.hasNext() == true)
      {
         pw.print ("   ");
         pw.print (iter.next());
      }
         
      pw.println ("]");   
      pw.close();
      
      return (sw.toString());      
   }
         
   private int m_id;
   private String m_name;   
   private boolean m_flag;
   private ArrayList m_columns = new ArrayList ();
}
