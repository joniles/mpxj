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


/**
 * This class represents the record in an MPX file containing details of
 * how date and time values are formatted.
 */
public class DateTimeSettings extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   DateTimeSettings (MPXFile file)
   {
      super(file);

      m_update = false;
      setDateOrder(DATE_ORDER_DMY);
      setTimeFormat(TIME_FORMAT_12HR);
      setDefaultTime (new Integer (480));
      setDateSeparator(new Character('/'));
      setTimeSeparator(new Character(':'));
      setAMText("am");
      setPMText("pm");
      setDateFormat(DATE_TIME_FORMAT_DD_MM_YYYY);
      setBarTextDateFormat(null);

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
      throws MPXException
   {
      m_update = false;
      setDateOrder(record.getByteValue(0));
      setTimeFormat(record.getByteValue(1));
      setDefaultTime(record.getInteger(2));
      setDateSeparator(record.getCharacter(3));
      setTimeSeparator(record.getCharacter(4));
      setAMText(record.getString(5));
      setPMText(record.getString(6));
      setDateFormat(record.getByteValue(7));
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

         switch (getDateFormat())
         {
            case DATE_TIME_FORMAT_DD_MM_YY_HH_MM:
            {
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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
               switch (getDateOrder())
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

      if (getTimeFormat() == TIME_FORMAT_12HR)
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
    * @param val new value
    */
   protected void put (Integer key, Object value)
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
   public byte getDateOrder ()
   {
      return (getByteValue (DATE_ORDER));
   }

   /**
    * Sets constant representing set Date order eg DMY, MDY
    *
    * @param date see CONSTANTS
    */
   public void setDateOrder (byte date)
   {
      put (DATE_ORDER, new Byte(date));
   }

   /**
    * Gets constant representing the Time Format
    *
    * @return time format constant
    */
   public byte getTimeFormat ()
   {
      return (getByteValue (TIME_FORMAT));
   }

   /**
    * Sets constant representing the time format
    *
    * @param time constant value
    */
   public void setTimeFormat (byte time)
   {
      put (TIME_FORMAT, new Byte(time));
   }

   /**
    * Retrieve the default time specified as after midnight.
    *
    * @return string
    */
   public int getDefaultTime ()
   {
      return (getIntValue (DEFAULT_TIME));
   }

   /**
    * Set the default time, specified as minutes after midnight.
    *
    * @param time time in minutes after midnight
    */
   public void setDefaultTime (Integer time)
   {
      put (DEFAULT_TIME, time);
   }

   /**
    * Gets the date separator. e.g. '/'
    *
    * @return date separator as set.
    */
   public char getDateSeparator ()
   {
      return (((Character)get(DATE_SEPARATOR)).charValue());
   }

   /**
    * Sets the date separator. e.g. '/'
    *
    * @param sep date separator as set.
    */
   public void setDateSeparator (Character sep)
   {
      put (DATE_SEPARATOR, sep);
   }

   /**
    * Gets the time separator. e.g. ':'
    *
    * @return time separator as set.
    */
   public char getTimeSeparator ()
   {
      return (((Character)get(TIME_SEPARATOR)).charValue());
   }

   /**
    * Sets the time separator. ie ':'
    *
    * @param sep time separator
    */
   public void setTimeSeparator (Character sep)
   {
      put (TIME_SEPARATOR, sep);
   }

   /**
    * Gets the AM text. ie 'AM'
    *
    * @return AM Text as set.
    */
   public String getAMText ()
   {
      return ((String)get(AMTEXT));
   }

   /**
    * Sets the AM text. ie 'AM'
    *
    * @param am AM Text as set.
    */
   public void setAMText (String am)
   {
      put (AMTEXT, am);
   }

   /**
    * Gets the PM text. ie 'PM'
    *
    * @return PM Text as set.
    */
   public String getPMText ()
   {
      return ((String)get(PMTEXT));
   }

   /**
    * Sets the PM text. ie 'PM'
    *
    * @param pm PM Text as set.
    */
   public void setPMText (String pm)
   {
      put (PMTEXT, pm);
   }

   /**
    * Gets the set Date Format. see CONSTANTS
    *
    * @return byte representing Date Format
    */
   public byte getDateFormat ()
   {
      return (getByteValue (DATE_FORMAT));
   }

   /**
    * Sets the set Date Format. see CONSTANTS
    *
    * @param df byte representing Date Format
    */
   public void setDateFormat (byte df)
   {
      put (DATE_FORMAT, new Byte(df));
   }

   /**
    * Gets Bar Text Date Format
    *
    * @return int value
    */
   public int getBarTextDateFormat()
   {
      return (getIntValue (BAR_TEXT_DATE_FORMAT));
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
    * All the following formats are as per Project '98.
    */

   /**
    * This format represents dates in the form eg. 25/12/98 12:56
    */
   public static final byte DATE_TIME_FORMAT_DD_MM_YY_HH_MM = 0;

   /**
    * This format represents dates in the form eg. 25/05/98
    */
   public static final byte DATE_TIME_FORMAT_DD_MM_YY = 1;

   /**
    * This format represents dates in the form eg. 13 December 2002 12:56
    */
   public static final byte DATE_TIME_FORMAT_DD_MMMMM_YYYY_HH_MM = 2;

   /**
    * This format represents dates in the form eg. 13 December 2002
    */
   public static final byte DATE_TIME_FORMAT_DD_MMMMM_YYYY = 3;

   /**
    * This format represents dates in the form eg. 24 Nov 12:56
    */
   public static final byte DATE_TIME_FORMAT_DD_MMM_HH_MM = 4;

   /**
    * This format represents dates in the form eg. 25 Aug '98
    */
   public static final byte DATE_TIME_FORMAT_DD_MMM_YY = 5;

   /**
    * This format represents dates in the form eg. 25 September
    */
   public static final byte DATE_TIME_FORMAT_DD_MMMMM = 6;

   /**
    * This format represents dates in the form eg. 25 Aug
    */
   public static final byte DATE_TIME_FORMAT_DD_MMM = 7;

   /**
    * This format represents dates in the form eg. Thu 25/05/98 12:56
    */
   public static final byte DATE_TIME_FORMAT_EEE_DD_MM_YY_HH_MM = 8;

   /**
    * This format represents dates in the form eg. Wed 25/05/98
    */
   public static final byte DATE_TIME_FORMAT_EEE_DD_MM_YY = 9;

   /**
    * This format represents dates in the form eg. Wed 25 Mar '98
    */
   public static final byte DATE_TIME_FORMAT_EEE_DD_MMM_YY = 10;

   /**
    * This format represents dates in the form eg. Wed 12:56
    */
   public static final byte DATE_TIME_FORMAT_EEE_HH_MM = 11;

   /**
    * This format represents dates in the form eg. 25/5
    */
   public static final byte DATE_TIME_FORMAT_DD_MM = 12;

   /**
    * This format represents dates in the form eg. 23
    */
   public static final byte DATE_TIME_FORMAT_DD = 13;

   /**
    * This format represents dates in the form eg. 12:56
    */
   public static final byte DATE_TIME_FORMAT_HH_MM = 14;

   /**
    * This format represents dates in the form eg. Wed 23 Mar
    */
   public static final byte DATE_TIME_FORMAT_EEE_DD_MMM = 15;

   /**
    * This format represents dates in the form eg. Wed 25/5
    */
   public static final byte DATE_TIME_FORMAT_EEE_DD_MM = 16;

   /**
    * This format represents dates in the form eg. Wed 05
    */
   public static final byte DATE_TIME_FORMAT_EEE_DD = 17;

   /**
    * This format represents dates in the form eg. 5/W25
    */
   public static final byte DATE_TIME_FORMAT_DD_WWW = 18;

   /**
    * This format represents dates in the form eg. 5/W25/98 12:56
    */
   public static final byte DATE_TIME_FORMAT_DD_WWW_YY_HH_MM = 19;

   /**
    * This format represents dates in the form eg. 25/05/1998
    */
   public static final byte DATE_TIME_FORMAT_DD_MM_YYYY = 20;

   /**
    * This format represents dates ordered month-day-year e.g. 12/25/99
    */
   public static final byte DATE_ORDER_MDY = 0;

   /**
    * This format represents dates ordered day-month-year e.g. 25/12/99
    */
   public static final byte DATE_ORDER_DMY = 1;

   /**
    * This format represents dates ordered year-month-day e.g. 99/12/25
    */
   public static final byte DATE_ORDER_YMD = 2;

   /**
    *  12 hour clock time format e.g. 11:59
    */
   public static final byte TIME_FORMAT_12HR = 0;

   /**
    * 24 hour clock time format e.g. 23:59
    */
   public static final byte TIME_FORMAT_24HR = 1;


   /**
    * Flag used to indicate whether the formats can be automatically updated.
    */
   private boolean m_update = false;

   /**
    * Constant value representing Date Order field
    */
   private static final Integer DATE_ORDER = new Integer(0);

   /**
    * Constant value representing Time Format field
    */
   private static final Integer TIME_FORMAT = new Integer(1);

   /**
    * Constant value representing Default Time field
    */
   private static final Integer DEFAULT_TIME = new Integer(2);

   /**
    * Constant value representing Date Separator field
    */
   private static final Integer DATE_SEPARATOR = new Integer(3);

   /**
    * Constant value representing Time Separator field
    */
   private static final Integer TIME_SEPARATOR = new Integer(4);

   /**
    * Constant value representing AMText field
    */
   private static final Integer AMTEXT = new Integer(5);

   /**
    * Constant value representing PMText field
    */
   private static final Integer PMTEXT = new Integer(6);

   /**
    * Constant value representing Date Format field
    */
   private static final Integer DATE_FORMAT = new Integer(7);

   /**
    * Constant value representing Bar text Date Format field
    */
   private static final Integer BAR_TEXT_DATE_FORMAT = new Integer(8);

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 12;
}
