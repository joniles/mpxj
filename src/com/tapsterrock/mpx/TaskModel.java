/*
 * file:       TaskModel.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       15/08/2002
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

package com.tapsterrock.mpx;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashMap;

/**
 * This class represents the task table definition record in an MPX file.
 * This record defines which fields are present in a task record.
 * This record has two forms, one textual and one numeric. Both
 * variants are handled by this class.
 */
class TaskModel extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   TaskModel (MPXFile file)
   {
      super(file);
   }

   /**
    * This method is used to retrieve a linked list of field identifiers
    * indicating the fields present in a task record.
    *
    * @return list of field names
    */
   public LinkedList getModel ()
   {
      return (m_list);
   }

   /**
    * This method populates the task model from data read from an MPX file.
    *
    * @param record data read from an MPX file
    * @param isText flag indicating whether the tetxual or numeric data is being supplied
    */
   public void update (Record record, boolean isText)
   {
      int length = record.getLength();

      for  (int i = 0 ; i < length ; i++ )
      {
         if (isText == true)
         {
            add (new Integer (getTaskCode (record.getString (i))));
         }
         else
         {
            add (record.getInteger (i));
         }
      }
   }

   /**
    * This method retrieves an iterator, allowing the list of field
    * names to be traversed.
    *
    * @return an iterator
    */
   public Iterator iterator()
   {
      return (m_list.iterator());
   }

   /**
    * Retrieves a flag indicating whether this model has been written
    * to a file.
    *
    * @return written flag
    */
   public boolean getWritten ()
   {
      return (m_written);
   }

   /**
    * This method is used to set the written flag to indicate whether this
    * model has been written to a file.
    *
    * @param written flag indicating whether the model has been written
    */
   public void setWritten (boolean written)
   {
      m_written = written;
   }


   /**
    * This method is called from the task class each time an attribute
    * is added, ensuring that all of the attributes present in each task
    * record are present in the resource model.
    *
    * @param attrib field identifier
    */
   public void add (Integer attrib)
   {
      if (m_list.contains(attrib) == false)
      {
         m_list.add (attrib);
      }
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record. Both the textual and numeric record
    * types are written by this method.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString()
   {
      Integer number;
      char delimiter = getParentFile().getDelimiter();

      StringBuffer textual = new StringBuffer();
      StringBuffer numeric = new StringBuffer();

      textual.append (RECORD_NUMBER_TEXT);
      numeric.append (RECORD_NUMBER_NUMERIC);

      Iterator iter = m_list.iterator();

      while (iter.hasNext() == true)
      {
         number = (Integer)iter.next();

         textual.append (delimiter);
         numeric.append (delimiter);

         textual.append(getTaskField(number));
         numeric.append (number);
      }

      textual.append (MPXFile.EOL);
      numeric.append (MPXFile.EOL);

      textual.append (numeric.toString());

      return (textual.toString());
   }


   /**
    * Returns Task field name of supplied code no.
    *
    * @param key - the code no of required Task field
    * @return - field name
    */
   public static String getTaskField (Integer key)
   {
      return ((String)TASK_NAME.get(key.toString()));
   }

   /**
    * Returns code number of Task field supplied.
    *
    * @param field - name
    * @return - code no
    */
   public static String getTaskCode (String field)
   {
      return ((String)TASK_NUMBER.get(field));
   }

   /**
    * Indicating whether or not model has already been written to a file.
    */
   private boolean m_written = false;

   /**
    * List of ordered fields for task definition.
    */
   private LinkedList m_list = new LinkedList();

   /**
    * Array of task field numbers corresponding to the task field names.
    */
   private static final String[] TASK_KEYS =
   {
      "44", "25", "32", "42", "59", "58", "22", "31", "41", "57",
      "56", "21", "86", "85", "135", "68", "91", "15", "30", "36",
      "37", "38", "34", "125", "82", "88", "92", "40", "46", "47",
      "48", "45", "53", "52", "51", "61", "63", "65", "127", "129",
      "67", "80", "35", "110", "111", "112", "113", "114", "115", "116",
      "117", "118", "119", "93", "123", "90", "55", "54", "122", "83",
      "81", "1", "14", "140", "141", "142", "143", "144", "121", "3",
      "99", "70", "95", "97", "33", "43", "23", "16", "73", "72",
      "152", "151", "84", "50", "60", "62", "64", "126", "128", "66",
      "150", "96", "71", "120", "87", "4", "5", "6", "7", "8",
      "9", "10", "11", "12", "13", "94", "98", "74", "75", "136",
      "2", "20", "24"
   };

   /**
    * Array of task field names corresponding to the task field numbers.
    */
   private static final String[] TASK_NAMES =
   {
      "% Complete", "% Work Complete", "Actual Cost", "Actual Duration",
      "Actual Finish", "Actual Start", "Actual Work", "Baseline Cost",
      "Baseline Duration", "Baseline Finish", "Baseline Start", "Baseline Work",
      "BCWP", "BCWS", "Confirmed", "Constraint Date", "Constraint Type",
      "Contact", "Cost", "Cost1", "Cost2", "Cost3", "Cost Variance", "Created",
      "Critical", "CV", "Delay", "Duration", "Duration1", "Duration2",
      "Duration3", "Duration Variance", "Early Finish", "Early Start", "Finish",
      "Finish1", "Finish2", "Finish3", "Finish4", "Finish5", "Finish Variance",
      "Fixed", "Fixed Cost", "Flag1", "Flag2", "Flag3", "Flag4", "Flag5",
      "Flag6", "Flag7", "Flag8", "Flag9", "Flag10", "Free Slack", "Hide Bar",
      "ID", "Late Finish", "Late Start", "Linked Fields", "Marked", "Milestone",
      "Name", "Notes", "Number1", "Number2", "Number3", "Number4", "Number5",
      "Objects", "Outline Level", "Outline Number", "Predecessors", "Priority",
      "Project", "Remaining Cost", "Remaining Duration", "Remaining Work ",
      "Resource Group", "Resource Initials", "Resource Names", "Resume",
      "Resume No Earlier Than", "Rollup", "Start", "Start1", "Start2", "Start3",
      "Start4", "Start5", "Start Variance", "Stop", "Subproject File",
      "Successors", "Summary", "SV", "Text1", "Text2", "Text3", "Text4",
      "Text5", "Text6", "Text7", "Text8", "Text9", "Text10", "Total Slack",
      "Unique ID", "Unique ID Predecessors", "Unique ID Successors",
      "Update Needed", "WBS", "Work", "Work Variance"
   };

   /**
    * Map used to store task field numbers.
    */
   private static final HashMap TASK_NUMBER = new HashMap();

   /**
    * Map to store task field names.
    */
   private static final HashMap TASK_NAME = new HashMap();

   {
      for (int i=0; i < TASK_KEYS.length; i++)
      {
         TASK_NAME.put (TASK_KEYS[i], TASK_NAMES[i]);
         TASK_NUMBER.put (TASK_NAMES[i], TASK_KEYS[i]);
      }
   }


   /**
    * Constant value representing Text Task Model class.
    */
   public static final int RECORD_NUMBER_TEXT = 60;

   /**
    * Constant value representing Numeric Task Model class.
    */
   public static final int RECORD_NUMBER_NUMERIC = 61;
}

