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
import java.util.Locale;


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
      super(file, 0);
      setLocale (file.getLocale());
   }

   /**
    * This method is calkled when the locale of the parent file is updated.
    * It resets the locale specific currency attributes to the default values
    * for the new locale.
    *
    * @param locale new locale
    */
   void setLocale (Locale locale)
   {
      m_update = false;
      setDateOrder((DateOrder)LocaleData.getObject(locale, LocaleData.DATE_ORDER));
      setTimeFormat((TimeFormat)LocaleData.getObject(locale, LocaleData.TIME_FORMAT));
      setIntegerDefaultStartTime (LocaleData.getInteger(locale, LocaleData.DEFAULT_START_TIME));
      setDateSeparator(LocaleData.getChar(locale, LocaleData.DATE_SEPARATOR));
      setTimeSeparator(LocaleData.getChar(locale, LocaleData.TIME_SEPARATOR));
      setAMText(LocaleData.getString(locale, LocaleData.AM_TEXT));
      setPMText(LocaleData.getString(locale, LocaleData.PM_TEXT));
      setDateFormat((DateFormat)LocaleData.getObject(locale, LocaleData.DATE_FORMAT));
      setBarTextDateFormat((DateFormat)LocaleData.getObject(locale, LocaleData.DATE_FORMAT));
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
      setDateOrder(record.getDateOrder(0));
      setTimeFormat(record.getTimeFormat(1));
      setIntegerDefaultStartTime(record.getInteger(2));
      setDateSeparator(record.getCharacter(3));
      setTimeSeparator(record.getCharacter(4));
      setAMText(record.getString(5));
      setPMText(record.getString(6));
      setDateFormat(record.getDateFormat(7));
      setBarTextDateFormat(record.getDateFormat(8));
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
         int dateOrderValue = getDateOrder().getValue();
         
         switch (getDateFormat().getValue())
         {
            case DateFormat.DD_MM_YY_HH_MM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="dd"+datesep+"MM"+datesep+"yy "+getTimeElement();
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="MM"+datesep+"dd"+datesep+"yy "+getTimeElement();
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     pattern="yy"+datesep+"MM"+datesep+"dd "+getTimeElement();
                     break;
                  }
               }
               break;
            }

            case DateFormat.DD_MM_YY_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="dd"+datesep+"MM"+datesep+"yy";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="MM"+datesep+"dd"+datesep+"yy";
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     pattern="yy"+datesep+"MM"+datesep+"dd";
                     break;

                  }
               }
               break;
            }

            case DateFormat.DD_MMMMM_YYYY_HH_MM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="dd MMMMM yyyy "+getTimeElement();
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="MMMMM dd yyyy "+getTimeElement();
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     pattern="yyyy MMMMM dd "+getTimeElement();
                     break;
                  }
               }
               break;
            }

            case DateFormat.DD_MMMMM_YYYY_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="dd MMMMM yyyy";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="MMMMM dd yyyy";
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     pattern="yyyy MMMMM dd";
                     break;
                  }
               }
               break;
            }

            case DateFormat.DD_MMM_HH_MM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="dd MMM "+getTimeElement();
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern=" MMM dd "+getTimeElement();
                     break;
                  }
               }
               break;
            }

            case DateFormat.DD_MMM_YY_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="dd MMM ''yy";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="MMM dd ''yy";
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     pattern="''yy MMM dd";
                     break;
                  }
               }
               break;
            }

            case DateFormat.DD_MMMMM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="dd MMMMM";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="MMMMM dd";
                     break;
                  }
               }
               break;
            }

            case DateFormat.DD_MMM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="dd MMM";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="MMM dd";
                     break;
                  }
               }
               break;
            }

            case DateFormat.EEE_DD_MM_YY_HH_MM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="EEE "+"dd"+datesep+"MM"+datesep+"yy "+getTimeElement();
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="EEE "+"MM"+datesep+"dd"+datesep+"yy "+getTimeElement();
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     pattern="EEE "+"yy"+datesep+"MM"+datesep+"dd "+getTimeElement();
                     break;
                  }
               }
               break;
            }

            case DateFormat.EEE_DD_MM_YY_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="EEE dd"+datesep+"MM"+datesep+"yy";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="EEE MM"+datesep+"dd"+datesep+"yy";
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     pattern="EEE yy"+datesep+"MM"+datesep+"dd";
                     break;
                  }
               }
               break;
            }

            case DateFormat.EEE_DD_MMM_YY_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="EEE dd MMM ''yy";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="EEE MM dd ''yy";
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     pattern="EEE ''yy MMM dd";
                     break;
                  }
               }
               break;
            }

            case DateFormat.EEE_HH_MM_VALUE:
            {
               pattern="EEE "+getTimeElement();
               break;
            }

            case DateFormat.DD_MM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="dd"+datesep+"MM";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="MM"+datesep+"dd";
                     break;
                  }
               }
               break;
            }

            case DateFormat.DD_VALUE:
            {
               pattern="dd";
               break;
            }

            case DateFormat.HH_MM_VALUE:
            {
               pattern = getTimeElement();
               break;
            }

            case DateFormat.EEE_DD_MMM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="EEE dd MMM";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="EEE MMM dd";
                     break;
                  }
               }
               break;
            }

            case DateFormat.EEE_DD_MM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="EEE dd"+datesep+"MM";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="EEE MM"+datesep+"dd";
                     break;
                  }
               }
               break;
            }

            case DateFormat.EEE_DD_VALUE:
            {
               pattern="EEE dd";
               break;
            }

            case DateFormat.DD_WWW_VALUE:
            {
               pattern="F"+datesep+"'W'ww";
               break;
            }

            case DateFormat.DD_WWW_YY_HH_MM_VALUE:
            {
               pattern="F"+datesep+"'W'ww"+datesep+"yy "+getTimeElement();
               break;
            }

            case DateFormat.DD_MM_YYYY_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     pattern="dd"+datesep+"MM"+datesep+"yyyy";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     pattern="MM"+datesep+"dd"+datesep+"yyyy";
                     break;
                  }

                  case DateOrder.YMD_VALUE:
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
      TimeFormat format = getTimeFormat();
      
      if (format == null || format.getValue() == TimeFormat.TWELVE_HOUR_VALUE)
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
    * Gets constant representing set Date order eg DMY, MDY
    *
    * @return constant value for date order
    */
   public DateOrder getDateOrder ()
   {
      return (m_dateOrder);
   }

   /**
    * Sets constant representing set Date order eg DMY, MDY
    *
    * @param dateOrder date order value
    */
   public void setDateOrder (DateOrder dateOrder)
   {
      m_dateOrder = dateOrder;
      updateFormats();
   }

   /**
    * Gets constant representing the Time Format
    *
    * @return time format constant
    */
   public TimeFormat getTimeFormat ()
   {
      return (m_timeFormat);
   }

   /**
    * Sets constant representing the time format
    *
    * @param timeFormat constant value
    */
   public void setTimeFormat (TimeFormat timeFormat)
   {
      m_timeFormat = timeFormat;
      updateFormats();
   }

   /**
    * This internal method is used to convert from an integer representing
    * minutes past midnight into a Date instance whose time component
    * represents the start time.
    * 
    * @param time integer representing the start time in minutes past midnight
    */
   private void setIntegerDefaultStartTime (Integer time)
   {
      if (time != null)
      {
         int minutes = time.intValue();
         int hours = minutes / 60;
         minutes -= (hours * 60);
         
         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.MILLISECOND, 0);
         cal.set(Calendar.SECOND, 0);         
         cal.set(Calendar.MINUTE, minutes);
         cal.set(Calendar.HOUR_OF_DAY, hours);
         
         Date date = cal.getTime();
         setDefaultStartTime(date);
      }
   }

   /**
    * This internal method is used to convert from a Date instance to an
    * integer representing the number of minutes past midnight.
    * 
    * @return minutes past midnight as an integer
    */
   private Integer getIntegerDefaultStartTime ()
   {
      Integer result = null;
      Date date = getDefaultStartTime();
      if (date != null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         int time = cal.get(Calendar.HOUR_OF_DAY) * 60;
         time += cal.get(Calendar.MINUTE);
         result = new Integer (time);
      }      
      return (result);
   }
   
   /**
    * Retrieve the default start time, specified using the Java Date type.
    * Note that this assumes that the value returned from
    * the getTime method starts at zero... i.e. the date part
    * of the date/time value has not been set.
    *
    * @return default start time
    */
   public Date getDefaultStartTime ()
   {
      return (m_defaultStartTime);
   }
   
   /**
    * Set the default start time, specified using the Java Date type.
    * Note that this assumes that the value returned from
    * the getTime method starts at zero... i.e. the date part
    * of the date/time value has not been set.
    *
    * @param defaultStartTime default time
    */
   public void setDefaultStartTime (Date defaultStartTime)
   {
      m_defaultStartTime = defaultStartTime;
   }

   /**
    * Gets the date separator.
    *
    * @return date separator as set.
    */
   public char getDateSeparator ()
   {
      return (m_dateSeparator);
   }

   /**
    * Sets the date separator.
    *
    * @param dateSeparator date separator as set.
    */
   public void setDateSeparator (char dateSeparator)
   {
      m_dateSeparator = dateSeparator;
      updateFormats();
   }

   /**
    * Sets the date separator.
    *
    * @param dateSeparator date separator as set.
    */
   private void setDateSeparator (Character dateSeparator)
   {
      setDateSeparator ((dateSeparator==null?DEFAULT_DATE_SEPARATOR:dateSeparator.charValue()));
   }

   /**
    * Gets the time separator.
    *
    * @return time separator as set.
    */
   public char getTimeSeparator ()
   {
      return (m_timeSeparator);
   }

   /**
    * Sets the time separator.
    *
    * @param timeSeparator time separator
    */
   public void setTimeSeparator (char timeSeparator)
   {
      m_timeSeparator = timeSeparator;
      updateFormats();
   }

   /**
    * Sets the time separator.
    *
    * @param timeSeparator time separator
    */
   public void setTimeSeparator (Character timeSeparator)
   {
      setTimeSeparator ((timeSeparator==null?DEFAULT_TIME_SEPARATOR:timeSeparator.charValue()));
   }

   /**
    * Gets the AM text.
    *
    * @return AM Text as set.
    */
   public String getAMText ()
   {
      return (m_amText);
   }

   /**
    * Sets the AM text.
    *
    * @param amText AM Text as set.
    */
   public void setAMText (String amText)
   {
      m_amText = amText;
      updateFormats();
   }

   /**
    * Gets the PM text.
    *
    * @return PM Text as set.
    */
   public String getPMText ()
   {
      return (m_pmText);
   }

   /**
    * Sets the PM text.
    *
    * @param pmText PM Text as set.
    */
   public void setPMText (String pmText)
   {
      m_pmText = pmText;
      updateFormats();
   }

   /**
    * Gets the set Date Format.
    *
    * @return int representing Date Format
    */
   public DateFormat getDateFormat ()
   {
      return (m_dateFormat);
   }

   /**
    * Sets the set Date Format.
    *
    * @param dateFormat int representing Date Format
    */
   public void setDateFormat (DateFormat dateFormat)
   {
      m_dateFormat = dateFormat;
      updateFormats();
   }

   /**
    * Gets Bar Text Date Format
    *
    * @return int value
    */
   public DateFormat getBarTextDateFormat ()
   {
      return (m_barTextDateFormat);
   }

   /**
    * Sets Bar Text Date Format.
    *
    * @param dateFormat value to be set
    */
   public void setBarTextDateFormat (DateFormat dateFormat)
   {
      m_barTextDateFormat = dateFormat;
   }

   /**
    * Retrieves the default end time.
    *
    * @return End time
    */
   public Date getDefaultEndTime ()
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
      buffer.append(format(getDateOrder()));
      buffer.append (delimiter);
      buffer.append(format(getTimeFormat()));
      buffer.append (delimiter);
      buffer.append(format(getIntegerDefaultStartTime()));
      buffer.append (delimiter);
      buffer.append(getDateSeparator());
      buffer.append (delimiter);
      buffer.append(getTimeSeparator());
      buffer.append (delimiter);
      buffer.append(format(getAMText()));
      buffer.append (delimiter);
      buffer.append(format(getPMText()));
      buffer.append (delimiter);
      buffer.append(format(getDateFormat()));
      buffer.append (delimiter);
      buffer.append(format(getBarTextDateFormat()));
      
      stripTrailingDelimiters(buffer, delimiter);
      buffer.append (MPXFile.EOL);
            
      return (buffer.toString());      
   }
   
   private DateOrder m_dateOrder;  
   private TimeFormat m_timeFormat;
   private Date m_defaultStartTime;
   private char m_dateSeparator;
   private char m_timeSeparator;
   private String m_amText;
   private String m_pmText;
   private DateFormat m_dateFormat;
   private DateFormat m_barTextDateFormat;
      
   
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
    * Flag used to indicate whether the formats can be automatically updated.
    * The default value for this flag is false.
    */
   private boolean m_update;

   /**
    * Default date separator character.
    */
   private static final char DEFAULT_DATE_SEPARATOR = '/';

   /**
    * Default time separator character.
    */
   private static final char DEFAULT_TIME_SEPARATOR = ':';
   
   /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 12;
}
