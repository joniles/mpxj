/*
 * file:       ProjectHeader.java
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
 * This class represents the ProjectHeader record as found in an MPX
 * file. This record contains details of global settings relevant to the
 * project plan. Note that a number of the fields in this record are
 * calculated by Microsoft Project, and will therefore be ignored on import.
 */
public class ProjectHeader extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ProjectHeader (MPXFile file)
      throws MPXException
   {
      super (file);
   }


   /**
    * This method allows an existing instance of a ProjectHeader object
    * to be updated with data taken from a record in an MPX file.
    *
    * @param record record containing the data for  this object.
    */
   void update (Record record)
      throws MPXException
   {
      setProjectTab(record.getString(0));
      setCompany(record.getString(1));
      setManager(record.getString(2));
      setCalendar(record.getString(3));
      setStartDate(record.getDate(4));
      setFinishDate(record.getDate(5));
      setScheduleFrom(record.getInteger(6));
      setCurrentDate(record.getDate(7));
      setComments(record.getString(8));
      setCost(record.getCurrency(9));
      setBaselineCost(record.getCurrency(10));
      setActualCost(record.getCurrency(11));
      setWork(record.getDuration(12));
      setBaselineWork(record.getDuration(13));
      setActualWork(record.getDuration(14));
      setWork2(record.getPercentage(15));
      setDuration(record.getDuration(16));
      setBaselineDuration(record.getDuration(17));
      setActualDuration(record.getDuration(18));
      setPercentageComplete(record.getPercentage(19));
      setBaselineStart(record.getDate(20));
      setBaselineFinish(record.getDate(21));
      setActualStart(record.getDate(22));
      setActualFinish(record.getDate(23));
      setStartVariance(record.getDuration(24));
      setFinishVariance(record.getDuration(25));
      setSubject(record.getString(26));
      setAuthor(record.getString(27));
      setKeywords(record.getString(28));
   }

   /**
    * Sets the project tab field
    *
    * @param val value
    */
   public void setProjectTab (String val)
   {
      put (PROJECT_TAB, val);
   }

   /**
    * Gets the project tab field
    *
    * @return value
    */
   public String getProjectTab ()
   {
      return ((String)get(PROJECT_TAB));
   }

   /**
    * Sets the Company field
    *
    * @param val value
    */
   public void setCompany (String val)
   {
      put (COMPANY, val);
   }

   /**
    * Gets the Company field
    *
    * @return compant name
    */
   public String getCompany ()
   {
      return ((String)get(COMPANY));
   }

   /**
    * Sets the Manager name
    *
    * @param val value
    */
   public void setManager (String val)
   {
      put (MANAGER, val);
   }

   /**
    * Gets the Manager name
    *
    * @return Manager name
    */
   public String getManager ()
   {
      return ((String)get(MANAGER));
   }

   /**
    * Sets the Calendar used. 'Standard' if no value is set
    *
    * @param val Calendar name
    */
   public void setCalendar (String val)
   {
      if (val == null || val.length() == 0)
      {
         val = "Standard";
      }

      put (CALENDAR, val);
   }

   /**
    * Gets the Calendar used. 'Standard' if no value is set
    *
    * @return Calendar name
    */
   public String getCalendar ()
   {
      return ((String)get(CALENDAR));
   }

   /**
    * Sets the Start Date
    *
    * @param val Start Date
    */
   public void setStartDate (Date val)
   {
      putDate (START_DATE, val);
   }

   /**
    * Gets the Start Date
    *
    * @return Start Date
    */
   public Date getStartDate ()
   {
      return ((Date)get(START_DATE));
   }

   /**
    * Gets the Finish Date
    *
    * @return Finish Date
    */
   public Date getFinishDate ()
   {
      return ((Date)get(FINISH_DATE));
   }

   /**
    * Sets the Finish Date
    *
    * @param val Finish Date
    */
   public void setFinishDate (Date val)
   {
      putDate (FINISH_DATE, val);
   }

   /**
    * To flag whether start date(0) or finish date(1) is included in file.
    * @return int - possible values 0-start, 1-finish
    */
   public int getScheduleFromValue ()
   {
      return (getIntValue (SCHEDULE_FROM));
   }

   /**
    * To flag whether start date(0) or finish date(1) is included in file.
    * @return int - possible values 0-start, 1-finish
    */
   public Integer getScheduleFrom ()
   {
      return ((Integer)get (SCHEDULE_FROM));
   }

   /**
    * To flag whether start date(0) or finish date(1) is included in file.
    *
    * @param val - possible values 0-start,1-finish
    */
   public void setScheduleFrom (int val)
   {
      put (SCHEDULE_FROM, val);
   }

   /**
    * To flag whether start date(0) or finish date(1) is included in file.
    *
    * @param val - possible values 0-start,1-finish
    */
   public void setScheduleFrom (Integer val)
   {
      put (SCHEDULE_FROM, val);
   }

   /**
    * Calculated by MS Project on import, so not required if creating
    * file for import to MS Project
    *
    * @return CurrentDate
    */
   public Date getCurrentDate()
   {
      return ((Date)get(CURRENT_DATE));
   }

   /**
    * Calculated by MS Project on import, so not required if creating
    * file for import to MS Project
    *
    * @param val - Date to set as CurrentDate
    */
   public void setCurrentDate (Date val)
   {
      putDate (CURRENT_DATE, val);
   }

   /**
    * Returns any comments
    * @return comments attached to the Project Header
    */
   public String getComments ()
   {
      return ((String)get(COMMENTS));
   }

   /**
    * Set comments
    *
    * @param val attach comments to the Project Header
    */
   public void setComments (String val)
   {
      put (COMMENTS, val);
   }

   /**
    * Calculated by MS Project on import, so not required if creating
    * file for import to MS Project
    *
    * @return - Cost
    */
   public Number getCost ()
   {
      return ((Number)get(COST));
   }

   /**
    * Calculated by MS Project on import, so not required if creating
    * file for import to MS Project
    *
    * @param val - float value
    */
   public void setCost (Number val)
   {
      putCurrency (COST, val);
   }

   /**
    * Calculated by MS Project on import, so not required if creating
    * file for import to MS Project
    *
    * @param val - BaselineCost
    */
   public void setBaselineCost (Number val)
   {
      putCurrency (BASELINE_COST, val);
   }

   /**
    * Calculated by MS Project on import, so not required if creating
    * file for import to MS Project
    * @return - float BaselineCost
    */
   public Number getBaselineCost ()
   {
      return ((Number)get(BASELINE_COST));
   }

   /**
    * Calculated by MS Project on import, so not required if creating
    * file for import to MS Project
    * @param val - value of actual cost
    */
   public void setActualCost (Number val)
   {
      putCurrency (ACTUAL_COST, val);
   }

   /**
    * Calculated by MS Project on import, so not required if creating
    * file for import to MS Project
    *
    * @return actual cost
    */
   public Number getActualCost ()
   {
      return ((Number)get(ACTUAL_COST));
   }

   /**
    * Set Work duration
    *
    * @param val duration
    */
   public void setWork (MPXDuration val)
   {
      put (WORK, val);
   }

   /**
    * Get Work duration
    *
    * @return duration
    */
   public MPXDuration getWork ()
   {
      return ((MPXDuration)get(WORK));
   }

   /**
    * Set Baseline Work duration
    *
    * @param val duration
    */
   public void setBaselineWork (MPXDuration val)
   {
      put (BASELINE_WORK, val);
   }

   /**
    * Get Baseline Work duration
    *
    * @return duration
    */
   public MPXDuration getBaselineWork ()
   {
      return ((MPXDuration)get(BASELINE_WORK));
   }

   /**
    * Set Actual Work duration
    *
    * @param val duration
    */
   public void setActualWork (MPXDuration val)
   {
      put (ACTUAL_WORK, val);
   }

   /**
    * Get Actual Work duration
    *
    * @return duration
    */
   public MPXDuration getActualWork ()
   {
      return ((MPXDuration)get(ACTUAL_WORK));
   }

   /**
    * Calculated by MS Project on import, so not required if creating
    * file for import to MS Project
    *
    * @return percentage
    */
   public double getWork2Value ()
   {
      return (getDoubleValue (WORK2));
   }

   /**
    * Calculated by MS Project on import, so not required if creating
    * file for import to MS Project
    *
    * @return percentage
    */
   public Number getWork2 ()
   {
      return ((Number)get (WORK2));
   }

   /**
    * Calculated by MS Project on import, so not required if creating
    * file for import to MS Project
    *
    * @param val percentage
    */
   public void setWork2 (double val)
   {
      putPercentage (WORK2, new MPXPercentage (val));
   }

   /**
    * Calculated by MS Project on import, so not required if creating
    * file for import to MS Project
    *
    * @param val percentage
    */
   public void setWork2 (Number val)
   {
      putPercentage (WORK2, val);
   }

   /**
    * Returns Duration value
    *
    * @return duration value
    */
   public MPXDuration getDuration ()
   {
      return ((MPXDuration)get(DURATION));
   }

   /**
    * Sets Duration value
    *
    * @param val duration value
    */
   public void setDuration (MPXDuration val)
   {
      put (DURATION, val);
   }

   /**
    * Returns Baseline Duration value
    *
    * @return duration value
    */
   public MPXDuration getBaselineDuration ()
   {
      return ((MPXDuration)get(BASELINE_DURATION));
   }

   /**
    * Sets Baseline Duration value
    *
    * @param val duration value
    */
   public void setBaselineDuration (MPXDuration val)
   {
      put (BASELINE_DURATION, val);
   }

   /**
    * Returns Actual Duration value
    *
    * @return duration value
    */
   public MPXDuration getActualDuration ()
   {
      return ((MPXDuration)get(ACTUAL_DURATION));
   }

   /**
    * Sets Actual Duration value
    *
    * @param val duration value
    */
   public void setActualDuration (MPXDuration val)
   {
      put (ACTUAL_DURATION, val);
   }

   /**
    * Gets percentage complete
    *
    * @return percentage value
    */
   public double getPercentageCompleteValue ()
   {
      return (getDoubleValue(PERCENTAGE_COMPLETE));
   }

   /**
    * Gets percentage complete
    *
    * @return percentage value
    */
   public Number getPercentageComplete ()
   {
      return ((Number)get(PERCENTAGE_COMPLETE));
   }

   /**
    * Sets percentage complete
    *
    * @param val percentage value
    */
   public void setPercentageComplete (double val)
   {
      putPercentage (PERCENTAGE_COMPLETE, new MPXPercentage (val));
   }

   /**
    * Sets percentage complete
    *
    * @param val percentage value
    */
   public void setPercentageComplete (Number val)
   {
      putPercentage (PERCENTAGE_COMPLETE, val);
   }

   /**
    * Sets the Baseline project start
    *
    * @param val baseline start date
    */
   public void setBaselineStart (Date val)
   {
      putDate (BASELINE_START, val);
   }

   /**
    * Gets the Baseline project start
    *
    * @return baseline start date
    */
   public Date getBaselineStart ()
   {
      return ((Date)get(BASELINE_START));
   }

   /**
    * Sets the Baseline project finish
    *
    * @param val baseline finish date
    */
   public void setBaselineFinish (Date val)
   {
      putDate (BASELINE_FINISH, val);
   }

   /**
    * Gets the Baseline project finish
    *
    * @return baseline finish date
    */
   public Date getBaselineFinish()
   {
      return ((Date)get(BASELINE_FINISH));
   }

   /**
    * Sets the Actual Start
    *
    * @param val actual start date
    */
   public void setActualStart (Date val)
   {
      putDate(ACTUAL_START, val);
   }

   /**
    * Gets the Actual project start
    *
    * @return actual start date
    */
   public Date getActualStart ()
   {
      return ((Date)get(ACTUAL_START));
   }

   /**
    * Sets the Actual Finish
    *
    * @param val actual finish date
    */
   public void setActualFinish (Date val)
   {
      putDate (ACTUAL_FINISH, val);
   }

   /**
    * Gets the Actual project finish
    *
    * @return actual finish date
    */
   public Date getActualFinish ()
   {
      return ((Date)get(ACTUAL_FINISH));
   }

   /**
    * gets the start variance duration
    *
    * @return the start date variance
    */
   public MPXDuration getStartVariance()
   {
      return ((MPXDuration)get(START_VARIANCE));
   }

   /**
    * Sets the start variance duration
    *
    * @param val the start date variance
    */
   public void setStartVariance (MPXDuration val)
   {
      put (START_VARIANCE, val);
   }

   /**
    * gets the finish variance duration
    *
    * @return the finish date variance
    */
   public MPXDuration getFinishVariance ()
   {
      return ((MPXDuration)get(FINISH_VARIANCE));
   }

   /**
    * Sets the finish variance duration
    *
    * @param val the finish date variance
    */
   public void setFinishVariance (MPXDuration val)
   {
      put (FINISH_VARIANCE, val);
   }

   /**
    * Returns the subject field
    *
    * @return string Subject
    */
   public String getSubject ()
   {
      return ((String)get(SUBJECT));
   }

   /**
    * Sets the subject field
    * @param val string Subject
    */
   public void setSubject (String val)
   {
      put (SUBJECT, val);
   }

   /**
    * Returns the Author field
    * @return string Author
    */
   public String getAuthor ()
   {
      return (String)get(AUTHOR);
   }

   /**
    * Sets the Author field
    *
    * @param val string Author
    */
   public void setAuthor (String val)
   {
      put (AUTHOR, val);
   }

   /**
    * Gets the Keywords field
    *
    * @return string Keywords
    */
   public String getKeywords ()
   {
      return ((String)get(KEYWORDS));
   }

   /**
    * Sets the Keywords field
    *
    * @param val string Keywords
    */
   public void setKeywords (String val)
   {
      put (KEYWORDS, val);
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
    * Constant value representing Project Tab field
    */
   private static final Integer PROJECT_TAB = new Integer(0);

   /**
    * Constant value representing Company field
    */
   private static final Integer COMPANY = new Integer(1);

   /**
    * Constant value representing Manager field
    */
   private static final Integer MANAGER = new Integer(2);

   /**
    * Constant value representing Project Tab field
    */
   private static final Integer CALENDAR = new Integer(3);

   /**
    * Constant value representing Start Date field
    */
   private static final Integer START_DATE = new Integer(4);

   /**
    * Constant value representing Finish Date field
    */
   private static final Integer FINISH_DATE = new Integer(5);

   /**
    * Constant value representing Schedule From field
    */
   private static final Integer SCHEDULE_FROM = new Integer(6);

   /**
    * Constant value representing Current Date field
    */
   private static final Integer CURRENT_DATE = new Integer(7);

   /**
    * Constant value representing Comments field
    */
   private static final Integer COMMENTS = new Integer(8);

   /**
    * Constant value representing Cost field
    */
   private static final Integer COST = new Integer(9);

   /**
    * Constant value representing Baseline Cost field
    */
   private static final Integer BASELINE_COST = new Integer(10);

   /**
    * Constant value representing Actual Cost field
    */
   private static final Integer ACTUAL_COST = new Integer(11);

   /**
    * Constant value representing Work field
    */
   private static final Integer WORK = new Integer(12);

   /**
    * Constant value representing Baseline Work field
    */
   private static final Integer BASELINE_WORK = new Integer(13);

   /**
    * Constant value representing Actual Work field
    */
   private static final Integer ACTUAL_WORK = new Integer(14);

   /**
    * Constant value representing Work2 field. This field is calculated by MSP
    */
   private static final Integer WORK2 = new Integer(15);

   /**
    * Constant value representing Duration field
    */
   private static final Integer DURATION = new Integer(16);

   /**
    * Constant value representing Baseline Duration field
    */
   private static final Integer BASELINE_DURATION = new Integer(17);

   /**
    * Constant value representing Actual Duration field
    */
   private static final Integer ACTUAL_DURATION = new Integer(18);

   /**
    * Constant value representing Percentage Complete field
    */
   private static final Integer PERCENTAGE_COMPLETE = new Integer(19);

   /**
    * Constant value representing Baseline Start field
    */
   private static final Integer BASELINE_START = new Integer(20);

   /**
    * Constant value representing Baseline Finish field
    */
   private static final Integer BASELINE_FINISH = new Integer(21);

   /**
    * Constant value representing Actual Start field
    */
   private static final Integer ACTUAL_START = new Integer(22);

   /**
    * Constant value representing Actual Finish field
    */
   private static final Integer ACTUAL_FINISH = new Integer(23);

   /**
    * Constant value representing Start Variance field
    */
   private static final Integer START_VARIANCE = new Integer(24);

   /**
    * Constant value representing Finish Variance field
    */
   private static final Integer FINISH_VARIANCE = new Integer(25);

   /**
    * Constant value representing Subject field
    */
   private static final Integer SUBJECT = new Integer(26);

   /**
    * Constant value representing Author field
    */
   private static final Integer AUTHOR = new Integer(27);

   /**
    * Constant value representing Keywords field
    */
   private static final Integer KEYWORDS = new Integer(28);

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 30;
}
