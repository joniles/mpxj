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
public final class ProjectHeader extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ProjectHeader (MPXFile file)
   {
      super (file, 0);

      setProjectTitle("Project1");
      setCompany(null);
      setManager(null);
      setCalendarName(DEFAULT_CALENDAR_NAME);
      setStartDate(null);
      setFinishDate(null);
      setScheduleFrom(DEFAULT_SCHEDULE_FROM);
      setCurrentDate(new Date());
      setComments(null);
      setCost(DEFAULT_COST);
      setBaselineCost(DEFAULT_COST);
      setActualCost(DEFAULT_COST);
      setWork(DEFAULT_WORK);
      setBaselineWork(DEFAULT_WORK);
      setActualWork(DEFAULT_WORK);
      setWork2(DEFAULT_WORK2);
      setDuration(DEFAULT_DURATION);
      setBaselineDuration(DEFAULT_DURATION);
      setActualDuration(DEFAULT_DURATION);
      setPercentageComplete(DEFAULT_PERCENT_COMPLETE);
      setBaselineStart(null);
      setBaselineFinish(null);
      setActualStart(null);
      setActualFinish(null);
      setStartVariance(DEFAULT_DURATION);
      setFinishVariance(DEFAULT_DURATION);
      setSubject(null);
      setAuthor(null);
      setKeywords(null);
   }


   /**
    * This method allows an existing instance of a ProjectHeader object
    * to be updated with data taken from a record in an MPX file.
    *
    * @param record record containing the data for  this object.
    * @throws MPXException normally thrown when parsing fails
    */
   void update (Record record)
      throws MPXException
   {
      setProjectTitle(record.getString(0));
      setCompany(record.getString(1));
      setManager(record.getString(2));
      setCalendarName(record.getString(3));
      setStartDate(record.getDate(4));
      setFinishDate(record.getDate(5));
      setScheduleFrom(record.getScheduleFrom(6));
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
    * Sets the project title
    *
    * @param projectTitle project title
    */
   public void setProjectTitle (String projectTitle)
   {
      m_projectTitle = projectTitle;
   }

   /**
    * Gets the project title
    *
    * @return project title
    */
   public String getProjectTitle ()
   {
      return (m_projectTitle);
   }

   /**
    * Sets the company name
    *
    * @param company company name
    */
   public void setCompany (String company)
   {
      m_company = company;
   }

   /**
    * Retrieves the company name
    *
    * @return company name
    */
   public String getCompany ()
   {
      return (m_company);
   }

   /**
    * Sets the manager name
    *
    * @param manager manager name
    */
   public void setManager (String manager)
   {
      m_manager = manager;
   }

   /**
    * Retrieves the manager name
    *
    * @return manager name
    */
   public String getManager ()
   {
      return (m_manager);
   }

   /**
    * Sets the Calendar used. 'Standard' if no value is set
    *
    * @param calendarName Calendar name
    */
   public void setCalendarName (String calendarName)
   {
      if (calendarName == null || calendarName.length() == 0)
      {
         calendarName = DEFAULT_CALENDAR_NAME;
      }

      m_calendarName = calendarName;
   }

   /**
    * Gets the Calendar used. 'Standard' if no value is set
    *
    * @return Calendar name
    */
   public String getCalendarName ()
   {
      return (m_calendarName);
   }

   /**
    * Sets the project start date
    *
    * @param startDate project start date
    */
   public void setStartDate (Date startDate)
   {
      m_startDate = toDate(startDate);
   }

   /**
    * Gets the Start Date
    *
    * @return Start Date
    */
   public Date getStartDate ()
   {
      return (m_startDate);
   }

   /**
    * Gets the Finish Date
    *
    * @return Finish Date
    */
   public Date getFinishDate ()
   {
      return (m_finishDate);
   }

   /**
    * Sets the project finish date
    *
    * @param finishDate project finish date
    */
   public void setFinishDate (Date finishDate)
   {
      m_finishDate = toDate(finishDate);
   }

   /**
    * Retrieves an enumerated value indicating if tasks in this project are
    * scheduled from a start or a finish date.
    * 
    * @return schedule from flag
    */
   public ScheduleFrom getScheduleFrom ()
   {
      return (m_scheduleFrom);
   }

   /**
    * Sets an enumerated value indicating if tasks in this project are
    * scheduled from a start or a finish date.
    *
    * @param scheduleFrom schedule from value
    */
   public void setScheduleFrom (ScheduleFrom scheduleFrom)
   {
      m_scheduleFrom = scheduleFrom;
   }

   /**
    * Retrieves the current date.
    *
    * @return current date
    */
   public Date getCurrentDate()
   {
      return (m_currentDate);
   }

   /**
    * Sets the current date.
    *
    * @param currentDate current date
    */
   public void setCurrentDate (Date currentDate)
   {
      m_currentDate = toDate(currentDate);
   }

   /**
    * Returns any comments.
    * 
    * @return comments attached to the Project Header
    */
   public String getComments ()
   {
      return (m_comments);
   }

   /**
    * Set comment text.
    *
    * @param comments comment text
    */
   public void setComments (String comments)
   {
      m_comments = comments;
   }

   /**
    * Retrieves the project cost.
    *
    * @return project cost
    */
   public Number getCost ()
   {
      return (m_cost);
   }

   /**
    * Sets the project cost.
    *
    * @param cost project cost
    */
   public void setCost (Number cost)
   {
      m_cost = toCurrency(cost);
   }

   /**
    * Sets the baseline project cost.
    *
    * @param baselineCost baseline project cost
    */
   public void setBaselineCost (Number baselineCost)
   {
      m_baselineCost = toCurrency(baselineCost);
   }

   /**
    * Retrieves the baseline project cost.
    * 
    * @return baseline project cost
    */
   public Number getBaselineCost ()
   {
      return (m_baselineCost);
   }

   /**
    * Sets the actual project cost.
    * 
    * @param actualCost actual project cost
    */
   public void setActualCost (Number actualCost)
   {
      m_actualCost = toCurrency(actualCost);
   }

   /**
    * Retrieves the actual project cost.
    *
    * @return actual project cost
    */
   public Number getActualCost ()
   {
      return (m_actualCost);
   }

   /**
    * Sets the project work duration
    *
    * @param work project work duration
    */
   public void setWork (MPXDuration work)
   {
      m_work = work;
   }

   /**
    * Retrieves the project work duration
    *
    * @return project work duration
    */
   public MPXDuration getWork ()
   {
      return (m_work);
   }

   /**
    * Set the baseline project work duration
    *
    * @param baselineWork baseline project work duration
    */
   public void setBaselineWork (MPXDuration baselineWork)
   {
      m_baselineWork = baselineWork;
   }

   /**
    * Retrieves the baseline project work duration
    *
    * @return baseline project work duration
    */
   public MPXDuration getBaselineWork ()
   {
      return (m_baselineWork);
   }

   /**
    * Sets the actual project work duration
    *
    * @param actualWork actual project work duration
    */
   public void setActualWork (MPXDuration actualWork)
   {
      m_actualWork = actualWork;
   }

   /**
    * Retrieves the actual project work duration
    *
    * @return actual project work duration
    */
   public MPXDuration getActualWork ()
   {
      return (m_actualWork);
   }

   /**
    * Retrieves the project's "Work 2" attribute.
    * 
    * @return Work 2 attribute
    */
   public Number getWork2 ()
   {
      return (m_work2);
   }

   /**
    * Sets the project's "Work 2" attribute.
    *
    * @param work2 work2 percentage value
    */
   public void setWork2 (Number work2)
   {
      m_work2 = toPercentage(work2);
   }

   /**
    * Retrieves the project duration
    *
    * @return project duration
    */
   public MPXDuration getDuration ()
   {
      return (m_duration);
   }

   /**
    * Sets the project duration.
    *
    * @param duration project duration
    */
   public void setDuration (MPXDuration duration)
   {
      m_duration = duration;
   }

   /**
    * Retrieves the baseline duration value.
    *
    * @return baseline project duration value
    */
   public MPXDuration getBaselineDuration ()
   {
      return (m_baselineDuration);
   }

   /**
    * Sets the baseline project duration value.
    *
    * @param baselineDuration baseline project duration
    */
   public void setBaselineDuration (MPXDuration baselineDuration)
   {
      m_baselineDuration = baselineDuration;
   }

   /**
    * Retrieves the actual project duration.
    *
    * @return actual project duration
    */
   public MPXDuration getActualDuration ()
   {
      return (m_actualDuration);
   }

   /**
    * Sets the actual project duration.
    *
    * @param actualDuration actual project duration
    */
   public void setActualDuration (MPXDuration actualDuration)
   {
      m_actualDuration = actualDuration;
   }

   /**
    * Retrieves the project percentage complete
    *
    * @return percentage value
    */
   public Number getPercentageComplete ()
   {
      return (m_percentageComplete);
   }

   /**
    * Sets project percentage complete
    *
    * @param percentComplete project percent complete
    */
   public void setPercentageComplete (Number percentComplete)
   {
      m_percentageComplete = toPercentage(percentComplete);
   }

   /**
    * Sets the baseline project start date.
    *
    * @param baselineStartDate baseline project start date
    */
   public void setBaselineStart (Date baselineStartDate)
   {
      m_baselineStart = toDate(baselineStartDate);
   }

   /**
    * Retrieves the baseline project start date.
    *
    * @return baseline project start date
    */
   public Date getBaselineStart ()
   {
      return (m_baselineStart);
   }

   /**
    * Sets the baseline project finish date
    *
    * @param baselineFinishDate baseline project finish date
    */
   public void setBaselineFinish (Date baselineFinishDate)
   {
      m_baselineFinish = toDate(baselineFinishDate);
   }

   /**
    * Retrieves the baseline project finish date.
    *
    * @return baseline project finish date
    */
   public Date getBaselineFinish()
   {
      return (m_baselineFinish);
   }

   /**
    * Sets the actual project start date
    *
    * @param actualStartDate actual project start date
    */
   public void setActualStart (Date actualStartDate)
   {
      m_actualStart = toDate(actualStartDate);
   }

   /**
    * Retrieves the actual project start date.
    *
    * @return actual project start date
    */
   public Date getActualStart ()
   {
      return (m_actualStart);
   }

   /**
    * Sets the actual project finish date.
    *
    * @param actualFinishDate actual project finish date
    */
   public void setActualFinish (Date actualFinishDate)
   {
      m_actualFinish = toDate(actualFinishDate);
   }

   /**
    * Retrieves the actual project finish date.
    *
    * @return actual project finish date
    */
   public Date getActualFinish ()
   {
      return (m_actualFinish);
   }

   /**
    * Retrieves the start variance duration.
    *
    * @return start date variance
    */
   public MPXDuration getStartVariance()
   {
      return (m_startVariance);
   }

   /**
    * Sets the start variance duration.
    *
    * @param startVariance the start date variance
    */
   public void setStartVariance (MPXDuration startVariance)
   {
      m_startVariance = startVariance;
   }

   /**
    * Retrieves the project finish variance duration
    *
    * @return project finish variance duration
    */
   public MPXDuration getFinishVariance ()
   {
      return (m_finishVariance);
   }

   /**
    * Sets the project finish variance duration
    *
    * @param finishVariance project finish variance duration
    */
   public void setFinishVariance (MPXDuration finishVariance)
   {
      m_finishVariance = finishVariance;
   }

   /**
    * Returns the project subject text.
    *
    * @return subject text
    */
   public String getSubject ()
   {
      return (m_subject);
   }

   /**
    * Sets the project subject text.
    * 
    * @param subject subject text
    */
   public void setSubject (String subject)
   {
      m_subject = subject;
   }

   /**
    * Retrieves the project author text.
    * 
    * @return author text
    */
   public String getAuthor ()
   {
      return (m_author);
   }

   /**
    * Sets the project author text
    *
    * @param author project author text
    */
   public void setAuthor (String author)
   {
      m_author = author;
   }

   /**
    * Retrieves the project keyword text.
    *
    * @return project keyword text
    */
   public String getKeywords ()
   {
      return (m_keywords);
   }

   /**
    * Sets the project keyword text
    *
    * @param keywords project keyword text
    */
   public void setKeywords (String keywords)
   {
      m_keywords = keywords;
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      StringBuffer buffer = new StringBuffer ();
      char delimiter = getParentFile().getDelimiter();

      buffer.append (RECORD_NUMBER);
      buffer.append (delimiter);
      buffer.append(format(getProjectTitle()));
      buffer.append (delimiter);
      buffer.append(format(getCompany()));
      buffer.append (delimiter);
      buffer.append(format(getManager()));
      buffer.append (delimiter);
      buffer.append(format(getCalendarName()));
      buffer.append (delimiter);
      buffer.append(format(getStartDate()));
      buffer.append (delimiter);
      buffer.append(format(getFinishDate()));
      buffer.append (delimiter);
      buffer.append(format(getScheduleFrom()));
      buffer.append (delimiter);
      buffer.append(format(getCurrentDate()));
      buffer.append (delimiter);
      buffer.append(format(getComments()));
      buffer.append (delimiter);
      buffer.append(format(getCost()));
      buffer.append (delimiter);
      buffer.append(format(getBaselineCost()));
      buffer.append (delimiter);
      buffer.append(format(getActualCost()));
      buffer.append (delimiter);
      buffer.append(format(getWork()));
      buffer.append (delimiter);
      buffer.append(format(getBaselineWork()));
      buffer.append (delimiter);
      buffer.append(format(getActualWork()));
      buffer.append (delimiter);
      buffer.append(format(getWork2()));
      buffer.append (delimiter);
      buffer.append(format(getDuration()));
      buffer.append (delimiter);
      buffer.append(format(getBaselineDuration()));
      buffer.append (delimiter);
      buffer.append(format(getActualDuration()));
      buffer.append (delimiter);
      buffer.append(format(getPercentageComplete()));
      buffer.append (delimiter);
      buffer.append(format(getBaselineStart()));
      buffer.append (delimiter);
      buffer.append(format(getBaselineFinish()));
      buffer.append (delimiter);
      buffer.append(format(getActualStart()));
      buffer.append (delimiter);
      buffer.append(format(getActualFinish()));
      buffer.append (delimiter);
      buffer.append(format(getStartVariance()));
      buffer.append (delimiter);
      buffer.append(format(getFinishVariance()));
      buffer.append (delimiter);
      buffer.append(format(getSubject()));
      buffer.append (delimiter);
      buffer.append(format(getAuthor()));
      buffer.append (delimiter);
      buffer.append(format(getKeywords()));
      
      stripTrailingDelimiters(buffer, delimiter);
      buffer.append (MPXFile.EOL);
            
      return (buffer.toString());      
   }

   private String m_projectTitle;
   private String m_company;
   private String m_manager;
   private String m_calendarName;
   private MPXDate m_startDate;
   private MPXDate m_finishDate;
   private ScheduleFrom m_scheduleFrom;
   private MPXDate m_currentDate;
   private String m_comments;
   private MPXCurrency m_cost;
   private MPXCurrency m_baselineCost;
   private MPXCurrency m_actualCost;
   private MPXDuration m_work;
   private MPXDuration m_baselineWork;
   private MPXDuration m_actualWork;
   private MPXPercentage m_work2;
   private MPXDuration m_duration;
   private MPXDuration m_baselineDuration;
   private MPXDuration m_actualDuration;
   private MPXPercentage m_percentageComplete;
   private MPXDate m_baselineStart;
   private MPXDate m_baselineFinish;
   private MPXDate m_actualStart;
   private MPXDate m_actualFinish;
   private MPXDuration m_startVariance;
   private MPXDuration m_finishVariance;
   private String m_subject;
   private String m_author;
   private String m_keywords;

   /**
    * Default cost value.
    */
   private static final Double DEFAULT_COST = new Double (0);

   /**
    * Default work value.
    */
   private static final MPXDuration DEFAULT_WORK = new MPXDuration (0, TimeUnit.HOURS);

   /**
    * Default work 2 value.
    */
   private static final MPXPercentage DEFAULT_WORK2 = new MPXPercentage (0);
   
   /**
    * Default duration value.
    */
   private static final MPXDuration DEFAULT_DURATION = new MPXDuration (0, TimeUnit.DAYS);

   /**
    * Default schedule from value.
    */
   private static final ScheduleFrom DEFAULT_SCHEDULE_FROM = ScheduleFrom.START;

   /**
    * Default percent complete value.
    */
   private static final Integer DEFAULT_PERCENT_COMPLETE = new Integer (0);

   /**
    * Default calendar name.
    */
   private static final String DEFAULT_CALENDAR_NAME = "Standard";
   
   /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 30;
}
