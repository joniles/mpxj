/*
 * file:       DateTimeSettings.java
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

import java.util.Calendar;
import java.util.Date;


/**
 * This class represents the record in an MPX file containing details of
 * how date and time values are formatted.
 */
public final class DateTimeSettings extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   DateTimeSettings (MPXFile file)
   {
      super(file, MAX_FIELDS);

      m_update = false;
      setDateOrder(DATE_ORDER_DMY);
      setTimeFormat(TIME_FORMAT_12HR);
      setDefaultStartTime (480);
      setDateSeparator('/');
      setTimeSeparator(':');
      setAMText("am");
      setPMText("pm");
      setDateFormat(DATE_TIME_FORMAT_DD_MM_YYYY);
      setBarTextDateFormat(0);

      m_update = true;

      updateFormats ();
   }

   /**
    * This method is used to update a date time settings instance with
    * data from an MPX file.
    *
    * @param record record containing the data for  this object.
    */
   void update (Record record)
   {
      m_update = false;
      setDateOrder(record.getInteger(0));
      setTimeFormat(record.getInteger(1));
      setDefaultStartTime(record.getInteger(2));
      setDateSeparator(record.getCharacter(3));
      setTimeSeparator(record.getCharacter(4));
      setAMText(record.getString(5));
      setPMText(record.getString(6));
      setDateFormat(record.getInteger(7));
      setBarTextDateFormat(record.getInteger(8));
      m_update = true;

      updateFormats ();
   }

   /**
    * This method updates the formatters used to control time and date
    * formatting.
    */
   private void updateFormats ()
   {
      if (m_update == true)
      {
         String pattern= "";
         char datesep = getDateSeparator();

         switch (getDateFormatValue())
         {
            case DATE_TIME_FORMAT_DD_MM_YY_HH_MM:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="dd"+datesep+"MM"+datesep+"yy "+getTimeElement();
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="MM"+datesep+"dd"+datesep+"yy "+getTimeElement();
                     break;
                  }

                  case DATE_ORDER_YMD:
                  {
                     pattern="yy"+datesep+"MM"+datesep+"dd "+getTimeElement();
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_DD_MM_YY:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="dd"+datesep+"MM"+datesep+"yy";
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="MM"+datesep+"dd"+datesep+"yy";
                     break;
                  }

                  case DATE_ORDER_YMD:
                  {
                     pattern="yy"+datesep+"MM"+datesep+"dd";
                     break;

                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_DD_MMMMM_YYYY_HH_MM:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="dd MMMMM yyyy "+getTimeElement();
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="MMMMM dd yyyy "+getTimeElement();
                     break;
                  }

                  case DATE_ORDER_YMD:
                  {
                     pattern="yyyy MMMMM dd "+getTimeElement();
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_DD_MMMMM_YYYY:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="dd MMMMM yyyy";
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="MMMMM dd yyyy";
                     break;
                  }

                  case DATE_ORDER_YMD:
                  {
                     pattern="yyyy MMMMM dd";
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_DD_MMM_HH_MM:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="dd MMM "+getTimeElement();
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern=" MMM dd "+getTimeElement();
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_DD_MMM_YY:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="dd MMM ''yy";
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="MMM dd ''yy";
                     break;
                  }

                  case DATE_ORDER_YMD:
                  {
                     pattern="''yy MMM dd";
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_DD_MMMMM:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="dd MMMMM";
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="MMMMM dd";
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_DD_MMM:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="dd MMM";
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="MMM dd";
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_EEE_DD_MM_YY_HH_MM:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="EEE "+"dd"+datesep+"MM"+datesep+"yy "+getTimeElement();
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="EEE "+"MM"+datesep+"dd"+datesep+"yy "+getTimeElement();
                     break;
                  }

                  case DATE_ORDER_YMD:
                  {
                     pattern="EEE "+"yy"+datesep+"MM"+datesep+"dd "+getTimeElement();
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_EEE_DD_MM_YY:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="EEE dd"+datesep+"MM"+datesep+"yy";
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="EEE MM"+datesep+"dd"+datesep+"yy";
                     break;
                  }

                  case DATE_ORDER_YMD:
                  {
                     pattern="EEE yy"+datesep+"MM"+datesep+"dd";
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_EEE_DD_MMM_YY:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="EEE dd MMM ''yy";
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="EEE MM dd ''yy";
                     break;
                  }

                  case DATE_ORDER_YMD:
                  {
                     pattern="EEE ''yy MMM dd";
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_EEE_HH_MM:
            {
               pattern="EEE "+getTimeElement();
               break;
            }

            case DATE_TIME_FORMAT_DD_MM:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="dd"+datesep+"MM";
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="MM"+datesep+"dd";
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_DD:
            {
               pattern="dd";
               break;
            }

            case DATE_TIME_FORMAT_HH_MM:
            {
               pattern = getTimeElement();
               break;
            }

            case DATE_TIME_FORMAT_EEE_DD_MMM:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="EEE dd MMM";
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="EEE MMM dd";
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_EEE_DD_MM:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="EEE dd"+datesep+"MM";
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="EEE MM"+datesep+"dd";
                     break;
                  }
               }
               break;
            }

            case DATE_TIME_FORMAT_EEE_DD:
            {
               pattern="EEE dd";
               break;
            }

            case DATE_TIME_FORMAT_DD_WWW:
            {
               pattern="F"+datesep+"'W'ww";
               break;
            }

            case DATE_TIME_FORMAT_DD_WWW_YY_HH_MM:
            {
               pattern="F"+datesep+"'W'ww"+datesep+"yy "+getTimeElement();
               break;
            }

            case DATE_TIME_FORMAT_DD_MM_YYYY:
            {
               switch (getDateOrderValue())
               {
                  case DATE_ORDER_DMY:
                  {
                     pattern="dd"+datesep+"MM"+datesep+"yyyy";
                     break;
                  }

                  case DATE_ORDER_MDY:
                  {
                     pattern="MM"+datesep+"dd"+datesep+"yyyy";
                     break;
                  }

                  case DATE_ORDER_YMD:
                  {
                     pattern="yyyy"+datesep+"MM"+datesep+"dd";
                     break;
                  }
               }
               break;
            }
         }

         MPXFile parent = getParentFile();
         parent.getDateFormat().applyPattern(pattern);
         parent.getTimeFormat().applyPattern(getTimeElement());
      }
   }

   /**
    * Returns time elements considering 12/24 hour formatting.
    *
    * @return time formatting String
    */
   private String getTimeElement()
   {
      String time;
      char timesep = getTimeSeparator();

      if (getTimeFormatValue() == TIME_FORMAT_12HR)
      {
         time = "hh"+timesep+"mm a";
      }
      else
      {
         time = "HH"+timesep+"mm";
      }

      return (time);
   }

   /**
    * This method overrides the put method defined in MPXRecord allowing
    * the date and time formats to be updated whenever one of the attributes
    * controlling these formats is changed.
    *
    * @param key identifier of the value being changed
    * @param value new value
    */
   protected void put (int key, Object value)
   {
      super.put (key, value);
      updateFormats ();
   }

   /**
    * This method overrides the put method defined in MPXRecord allowing
    * the date and time formats to be updated whenever one of the attributes
    * controlling these formats is changed.
    *
    * @param key identifier of the value being changed
    * @param value new value
    */
   protected void put (int key, int value)
   {
      super.put (key, value);
      updateFormats ();
   }

   /**
    * This method overrides the put method defined in MPXRecord allowing
    * the date and time formats to be updated whenever one of the attributes
    * controlling these formats is changed.
    *
    * @param key identifier of the value being changed
    * @param value new value
    */
   protected void putChar (int key, char value)
   {
      super.put (key, value);
      updateFormats ();
   }

   /**
    * Gets constant representing set Date order eg DMY, MDY
    *
    * @return constant value for date order
    * @see #DATE_ORDER_MDY for Date order constants
    */
   public int getDateOrderValue ()
   {
      return (getIntValue (DATE_ORDER));
   }

   /**
    * Gets constant representing set Date order eg DMY, MDY
    *
    * @return constant value for date order
    * @see #DATE_ORDER_MDY for Date order constants
    */
   public Integer getDateOrder ()
   {
      return ((Integer)get (DATE_ORDER));
   }

   /**
    * Sets constant representing set Date order eg DMY, MDY
    *
    * @param date see CONSTANTS
    */
   public void setDateOrder (int date)
   {
      put (DATE_ORDER, new Integer(date));
   }

   /**
    * Sets constant representing set Date order eg DMY, MDY
    *
    * @param date see CONSTANTS
    */
   public void setDateOrder (Integer date)
   {
      put (DATE_ORDER, date);
   }

   /**
    * Gets constant representing the Time Format
    *
    * @return time format constant
    */
   public int getTimeFormatValue ()
   {
      return (getIntValue (TIME_FORMAT));
   }

   /**
    * Gets constant representing the Time Format
    *
    * @return time format constant
    */
   public Integer getTimeFormat ()
   {
      return ((Integer)get (TIME_FORMAT));
   }

   /**
    * Sets constant representing the time format
    *
    * @param time constant value
    */
   public void setTimeFormat (int time)
   {
      put (TIME_FORMAT, time);
   }

   /**
    * Sets constant representing the time format
    *
    * @param time constant value
    */
   public void setTimeFormat (Integer time)
   {
      put (TIME_FORMAT, time);
   }

   /**
    * Retrieve the default time specified as minutes after midnight.
    *
    * @return string
    */
   public int getDefaultStartTimeValue ()
   {
      return (getIntValue (DEFAULT_TIME));
   }

   /**
    * Retrieve the default time specified as minutes after midnight.
    *
    * @return string
    */
   public Integer getDefaultStartTime ()
   {
      return ((Integer)get (DEFAULT_TIME));
   }

   /**
    * Retrieve the default time as a Date object
    *
    * @return string
    */
   public Date getDefaultStartTimeAsDate ()
   {
      Date result = null;

      Integer minutes = (Integer)get (DEFAULT_TIME);
      if (minutes != null)
      {
         Calendar cal = Calendar.getInstance();
         int hval = minutes.intValue() / 60;
         int mval = minutes.intValue() - (hval * 60);
         cal.set(Calendar.HOUR_OF_DAY, hval);
         cal.set(Calendar.MINUTE, mval);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         result = cal.getTime();
      }

      return (result);
   }

   /**
    * Set the default time, specified as minutes after midnight.
    *
    * @param time time in minutes after midnight
    */
   public void setDefaultStartTime (int time)
   {
      put (DEFAULT_TIME, time);
   }

   /**
    * Set the default time, specified as minutes after midnight.
    *
    * @param time time in minutes after midnight
    */
   public void setDefaultStartTime (Integer time)
   {
      put (DEFAULT_TIME, time);
   }

   /**
    * Set the default time, specified using the Java Date type.
    * Note that this assumes that the value returned from
    * the getTime method starts at zero... i.e. the date part
    * of the date/time value has not been set.
    *
    * @param time default time
    */
   public void setDefaultStartTime (Date time)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(time);
      int result = cal.get(Calendar.HOUR_OF_DAY) * 60;
      result += cal.get(Calendar.MINUTE);
      put (DEFAULT_TIME, result);
   }

   /**
    * Gets the date separator.
    *
    * @return date separator as set.
    */
   public char getDateSeparator ()
   {
      return (getCharValue(DATE_SEPARATOR));
   }

   /**
    * Sets the date separator.
    *
    * @param sep date separator as set.
    */
   public void setDateSeparator (char sep)
   {
      putChar (DATE_SEPARATOR, sep);
   }

   /**
    * Sets the date separator.
    *
    * @param sep date separator as set.
    */
   public void setDateSeparator (Character sep)
   {
      put (DATE_SEPARATOR, sep);
   }

   /**
    * Gets the time separator.
    *
    * @return time separator as set.
    */
   public char getTimeSeparator ()
   {
      return (getCharValue(TIME_SEPARATOR));
   }

   /**
    * Sets the time separator.
    *
    * @param sep time separator
    */
   public void setTimeSeparator (char sep)
   {
      putChar (TIME_SEPARATOR, sep);
   }

   /**
    * Sets the time separator.
    *
    * @param sep time separator
    */
   public void setTimeSeparator (Character sep)
   {
      put (TIME_SEPARATOR, sep);
   }

   /**
    * Gets the AM text.
    *
    * @return AM Text as set.
    */
   public String getAMText ()
   {
      return ((String)get(AMTEXT));
   }

   /**
    * Sets the AM text.
    *
    * @param am AM Text as set.
    */
   public void setAMText (String am)
   {
      put (AMTEXT, am);
   }

   /**
    * Gets the PM text.
    *
    * @return PM Text as set.
    */
   public String getPMText ()
   {
      return ((String)get(PMTEXT));
   }

   /**
    * Sets the PM text.
    *
    * @param pm PM Text as set.
    */
   public void setPMText (String pm)
   {
      put (PMTEXT, pm);
   }

   /**
    * Gets the set Date Format.
    *
    * @return int representing Date Format
    */
   public int getDateFormatValue ()
   {
      return (getIntValue (DATE_FORMAT));
   }

   /**
    * Gets the set Date Format.
    *
    * @return int representing Date Format
    */
   public Integer getDateFormat ()
   {
      return ((Integer)get (DATE_FORMAT));
   }

   /**
    * Sets the set Date Format.
    *
    * @param df int representing Date Format
    */
   public void setDateFormat (int df)
   {
      put (DATE_FORMAT, new Integer(df));
   }

   /**
    * Sets the set Date Format.
    *
    * @param df int representing Date Format
    */
   public void setDateFormat (Integer df)
   {
      put (DATE_FORMAT, df);
   }

   /**
    * Gets Bar Text Date Format
    *
    * @return int value
    */
   public int getBarTextDateFormatValue ()
   {
      return (getIntValue (BAR_TEXT_DATE_FORMAT));
   }

   /**
    * Gets Bar Text Date Format
    *
    * @return int value
    */
   public Integer getBarTextDateFormat ()
   {
      return ((Integer)get (BAR_TEXT_DATE_FORMAT));
   }

   /**
    * Sets Bar Text Date Format.
    *
    * @param dateFormat value to be set
    */
   public void setBarTextDateFormat (int dateFormat)
   {
      put (BAR_TEXT_DATE_FORMAT, dateFormat);
   }

   /**
    * Sets Bar Text Date Format.
    *
    * @param dateFormat value to be set
    */
   public void setBarTextDateFormat (Integer dateFormat)
   {
      put (BAR_TEXT_DATE_FORMAT, dateFormat);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString()
   {
      return (toString(RECORD_NUMBER));
   }


   /**
    * Retrieve the default time specified as minutes after midnight.
    *
    * @return string
    */
   public Integer getDefaultEndTime ()
   {
      return (new Integer (getDefaultEndTimeValue()));
   }

   /**
    * Retrieve the default time specified as minutes after midnight.
    *
    * @return string
    */
   public int getDefaultEndTimeValue ()
   {
      int result = 0;

      if (m_defaultEndTime != null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(m_defaultEndTime);
         result = cal.get(Calendar.HOUR_OF_DAY) * 60;
         result += cal.get(Calendar.MINUTE);
      }

      return (result);
   }

   /**
    * Retrieves the default end time.
    *
    * @return End time
    */
   public Date getDefaultEndTimeAsDate ()
   {
      return (m_defaultEndTime);
   }

   /**
    * Sets the default end time.
    *
    * @param date End time
    */
   public void setDefaultEndTime (Date date)
   {
      m_defaultEndTime = date;
   }

   /**
    * The following member variables are extended attributes. They are
    * do not form part of the MPX file format definition, and are neither
    * loaded from an MPX file, or saved to an MPX file. Their purpose
    * is to provide storage for attributes which are defined by later versions
    * of Microsoft Project. This allows these attributes to be manipulated
    * when they have been retrieved from file formats other than MPX.
    */
   private Date m_defaultEndTime;

   /**
    * All the following formats are as per Project '98.
    */

   /**
    * This format represents dates in the form 25/12/98 12:56
    */
   public static final int DATE_TIME_FORMAT_DD_MM_YY_HH_MM = 0;

   /**
    * This format represents dates in the form 25/05/98
    */
   public static final int DATE_TIME_FORMAT_DD_MM_YY = 1;

   /**
    * This format represents dates in the form 13 December 2002 12:56
    */
   public static final int DATE_TIME_FORMAT_DD_MMMMM_YYYY_HH_MM = 2;

   /**
    * This format represents dates in the form 13 December 2002
    */
   public static final int DATE_TIME_FORMAT_DD_MMMMM_YYYY = 3;

   /**
    * This format represents dates in the form 24 Nov 12:56
    */
   public static final int DATE_TIME_FORMAT_DD_MMM_HH_MM = 4;

   /**
    * This format represents dates in the form 25 Aug '98
    */
   public static final int DATE_TIME_FORMAT_DD_MMM_YY = 5;

   /**
    * This format represents dates in the form 25 September
    */
   public static final int DATE_TIME_FORMAT_DD_MMMMM = 6;

   /**
    * This format represents dates in the form 25 Aug
    */
   public static final int DATE_TIME_FORMAT_DD_MMM = 7;

   /**
    * This format represents dates in the form Thu 25/05/98 12:56
    */
   public static final int DATE_TIME_FORMAT_EEE_DD_MM_YY_HH_MM = 8;

   /**
    * This format represents dates in the form Wed 25/05/98
    */
   public static final int DATE_TIME_FORMAT_EEE_DD_MM_YY = 9;

   /**
    * This format represents dates in the form Wed 25 Mar '98
    */
   public static final int DATE_TIME_FORMAT_EEE_DD_MMM_YY = 10;

   /**
    * This format represents dates in the form Wed 12:56
    */
   public static final int DATE_TIME_FORMAT_EEE_HH_MM = 11;

   /**
    * This format represents dates in the form 25/5
    */
   public static final int DATE_TIME_FORMAT_DD_MM = 12;

   /**
    * This format represents dates in the form 23
    */
   public static final int DATE_TIME_FORMAT_DD = 13;

   /**
    * This format represents dates in the form 12:56
    */
   public static final int DATE_TIME_FORMAT_HH_MM = 14;

   /**
    * This format represents dates in the form Wed 23 Mar
    */
   public static final int DATE_TIME_FORMAT_EEE_DD_MMM = 15;

   /**
    * This format represents dates in the form Wed 25/5
    */
   public static final int DATE_TIME_FORMAT_EEE_DD_MM = 16;

   /**
    * This format represents dates in the form Wed 05
    */
   public static final int DATE_TIME_FORMAT_EEE_DD = 17;

   /**
    * This format represents dates in the form 5/W25
    */
   public static final int DATE_TIME_FORMAT_DD_WWW = 18;

   /**
    * This format represents dates in the form 5/W25/98 12:56
    */
   public static final int DATE_TIME_FORMAT_DD_WWW_YY_HH_MM = 19;

   /**
    * This format represents dates in the form 25/05/1998
    */
   public static final int DATE_TIME_FORMAT_DD_MM_YYYY = 20;

   /**
    * This format represents dates ordered month-day-year for example, 12/25/99
    */
   public static final int DATE_ORDER_MDY = 0;

   /**
    * This format represents dates ordered day-month-year for example,  25/12/99
    */
   public static final int DATE_ORDER_DMY = 1;

   /**
    * This format represents dates ordered year-month-day for example,  99/12/25
    */
   public static final int DATE_ORDER_YMD = 2;

   /**
    *  12 hour clock time format, for example, 11:59
    */
   public static final int TIME_FORMAT_12HR = 0;

   /**
    * 24 hour clock time format, for example, 23:59
    */
   public static final int TIME_FORMAT_24HR = 1;


   /**
    * Flag used to indicate whether the formats can be automatically updated.
    */
   private boolean m_update = false;

   /**
    * Constant value representing Date Order field
    */
   private static final int DATE_ORDER = 0;

   /**
    * Constant value representing Time Format field
    */
   private static final int TIME_FORMAT = 1;

   /**
    * Constant value representing Default Time field
    */
   private static final int DEFAULT_TIME = 2;

   /**
    * Constant value representing Date Separator field
    */
   private static final int DATE_SEPARATOR = 3;

   /**
    * Constant value representing Time Separator field
    */
   private static final int TIME_SEPARATOR = 4;

   /**
    * Constant value representing AMText field
    */
   private static final int AMTEXT = 5;

   /**
    * Constant value representing PMText field
    */
   private static final int PMTEXT = 6;

   /**
    * Constant value representing Date Format field
    */
   private static final int DATE_FORMAT = 7;

   /**
    * Constant value representing Bar text Date Format field
    */
   private static final int BAR_TEXT_DATE_FORMAT = 8;

   /**
    * Maximum number of fields in this record.
    */
   private static final int MAX_FIELDS = 9;

   /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 12;
}
