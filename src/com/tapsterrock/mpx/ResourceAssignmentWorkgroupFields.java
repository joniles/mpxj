/*
 * file:       ResourceAssignmentWorkgroupFields.java
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

import java.util.Date;


/**
 * This class represents a resorce assignment workgrouo fields record
 * from an MPX file.
 */
public class ResourceAssignmentWorkgroupFields extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ResourceAssignmentWorkgroupFields (MPXFile file)
      throws MPXException
   {
      this (file, Record.EMPTY_RECORD);
   }

   /**
    * Constructor used to create an instance of this class from data
    * taken from an MPXFile record.
    *
    * @param file the MPXFile object to which this record belongs.
    * @param record record containing the data for  this object.
    */
   ResourceAssignmentWorkgroupFields (MPXFile file, Record record)
      throws MPXException
   {
      super(file);

      setMessageUniqueID(record.getString(0));
      setConfirmed(record.getByte(1));
      setResponsePending(record.getByte(2));
      setUpdateStart(record.getDate(3));
      setUpdateFinish(record.getDate(4));
      setScheduleID(record.getString(5));
   }

   /**
    * Sets the Message Unique ID
    *
    * @param val ID
    */
   public void setMessageUniqueID (String val)
   {
      put (MESSAGE_UNIQUE_ID, val);
   }

   /**
    * Gets the Message Unique ID
    *
    * @return ID
    */
   public String getMessageUniqueID ()
   {
      return ((String)get(MESSAGE_UNIQUE_ID));
   }

   /**
    * Gets  confirmed value
    *
    * @return 0-false, 1-true
    */
   public byte getConfirmed ()
   {
      return (getByteValue(CONFIRMED));
   }

   /**
    * Sets to confirmed
    *
    * @param val 0-false, 1-true
    */
   public void setConfirmed (Byte val)
   {
      put (CONFIRMED, val);
   }

   /**
    * Sets to response pending
    *
    * @param val 0-false, 1-true
    */
   public void setResponsePending (Byte val)
   {
      put (RESPONSE_PENDING, val);
   }

   /**
    * Gets  response pending value
    *
    * @return 0-false, 1-true
    */
   public byte getResponsePending ()
   {
      return (getByteValue(RESPONSE_PENDING));
   }

   /**
    * Sets the Update Start Field
    *
    * @param val date to set
    */
   public void setUpdateStart (Date val)
   {
      putDate (UPDATE_START, val);
   }

   /**
    * Gets the Update Start Field value
    *
    * @return update Start Date
    */
   public Date getUpdateStart ()
   {
      return ((Date)get(UPDATE_START));
   }

   /**
    * Sets the Update Finish Field
    * @param val date to set
    */
   public void setUpdateFinish (Date val)
   {
      putDate (UPDATE_FINISH, val);
   }

   /**
    * Gets the Update Finish Field value
    *
    * @return update Finish Date
    */
   public Date getUpdateFinish ()
   {
      return ((Date)get(UPDATE_FINISH));
   }

   /**
    * Set Schedule ID
    *
    * @param val  ID
    */
   public void setScheduleID (String val)
   {
      put (SCHEDULE_ID, val);
   }

   /**
    * Get Schedule ID
    *
    * @return ID
    */
   public String getScheduleID ()
   {
      return ((String)get(SCHEDULE_ID));
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      return (toString(RECORD_NUMBER));
   }

   /**
    * Constant value representing Message Unique ID.
    */
   private static final Integer MESSAGE_UNIQUE_ID = new Integer(0);

   /**
    * Constant value representing Confirmed.
    */
   private static final Integer CONFIRMED = new Integer(1);

   /**
    * Constant value representing Response Pending.
    */
   private static final Integer RESPONSE_PENDING = new Integer(2);

   /**
    * Constant value representing Update Start.
    */
   private static final Integer UPDATE_START = new Integer(3);

   /**
    * Constant value representing Update Finish.
    */
   private static final Integer UPDATE_FINISH = new Integer(4);

   /**
    * Constant value representing Schedule IDmed.
    */
   private static final Integer SCHEDULE_ID = new Integer(5);

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 76;
}
