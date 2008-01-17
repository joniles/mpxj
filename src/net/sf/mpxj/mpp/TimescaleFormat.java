/*
 * file:       TimescaleFormat.java
 * author:     Jari Niskala
 * copyright:  (c) Packwood Software Limited 2009
 * date:       17/01/2008
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

/**
 * Class representing the formats which may be shown on a Gantt chart timescale.
 */
public final class TimescaleFormat 
{
	   /**
	    * Private constructor.
	    *
	    * @param value units value from an MS Project file
	    */
	   private TimescaleFormat (int value)
	   {
	      m_value = value;
	   }

	   /**
	    * Retrieve an instance of this class based on the data read from an
	    * MS Project file.
	    *
	    * @param value value from an MS Project file
	    * @return instance of this class
	    */
	   public static TimescaleFormat getInstance (int value)
	   {		   		  
		  return FORMATS_ARRAY[getIndex(value)];
	   }
	   
	   /**
	    * Retrieve the index in the format array for this value.
	    *
	    * @param value value from an MS Project file
	    * @return index in to the FORMATS_ARRAY
	    */
	   private static int getIndex(int value)
	   {
		  for (int i = 0; i < FORMATS_ARRAY.length; i++)
		  {
			  if (FORMATS_ARRAY[i].m_value == value)
			  {
				  return i;
			  }
		  }
		  return 0;
	   }


	   /**
	    * Retrieve the name of this time format. Note that this is not
	    * localised.
	    *
	    * @return name of this timescale format
	    */
	   public String getName ()
	   {
	      return FORMATS_NAMES[getIndex(m_value)];
	   }

	   /**
        * {@inheritDoc}
	    */
	   @Override
	   public String toString ()
	   {
	      return (getName());
	   }

	   /**
	    * Retrieve the value associated with this instance.
	    *
	    * @return int value
	    */
	   public int getValue ()
	   {
	      return (m_value);
	   }
	   
	   public static final int NONE_VALUE = 35;
	   // Years
	   public static final int YEAR_YYYY_VALUE = 0;
	   public static final int YEAR_XYY_VALUE = 1;
	   public static final int YEAR_YY_VALUE = 78;
	   public static final int YEAR_YEAR_START_VALUE = 48;
	   public static final int YEAR_Y_START_VALUE = 73;
	   public static final int YEAR_1_START_VALUE = 74;
	   public static final int YEAR_YEAR_END_VALUE = 49;
	   public static final int YEAR_Y_END_VALUE = 71;
	   public static final int YEAR_1_END_VALUE = 72;
	   // Half Years
	   public static final int HALFYEAR_1_HALF_VALUE = 123;
	   public static final int HALFYEAR_HALF_1_YYYY_VALUE = 124;
	   public static final int HALFYEAR_HALF_1_VALUE = 125;
	   public static final int HALFYEAR_H1_XYY_VALUE = 126;
	   public static final int HALFYEAR_H1_VALUE = 127;
	   public static final int HALFYEAR_1_VALUE = -128;
	   public static final int HALFYEAR_1HYY_VALUE = -127;
	   public static final int HALFYEAR_HALF_1_START_VALUE = -126;
	   public static final int HALFYEAR_H1_START_VALUE = -125;
	   public static final int HALFYEAR_1_START_VALUE = -124;
	   public static final int HALFYEAR_HALF_1_END_VALUE = -123;
	   public static final int HALFYEAR_H1_END_VALUE = -122;
	   public static final int HALFYEAR_1_END_VALUE = -121;
	   // Quarters
	   public static final int QUARTER_1_QUARTER_VALUE = 2;
	   public static final int QUARTER_QTR_1_YYYY_VALUE = 3;
	   public static final int QUARTER_QTR_1_VALUE = 5;
	   public static final int QUARTER_Q1_XYY_VALUE = 4;
	   public static final int QUARTER_Q1_VALUE = 6;
	   public static final int QUARTER_1_VALUE = 62;
	   public static final int QUARTER_1QYY_VALUE = 51;
	   public static final int QUARTER_QUARTER_1_START_VALUE = 46;
	   public static final int QUARTER_Q1_START_VALUE = 65;
	   public static final int QUARTER_1_START_VALUE = 66;
	   public static final int QUARTER_QUARTER_1_END_VALUE = 47;
	   public static final int QUARTER_Q1_END_VALUE = 63;
	   public static final int QUARTER_1_END_VALUE = 64;
	   // Months
	   public static final int MONTHS_MMMM_YYYY_VALUE = 35;
	   public static final int MONTHS_MMM_XYY_VALUE = 8;
	   public static final int MONTHS_MMMM_VALUE = 9;
	   public static final int MONTHS_MMM_VALUE = 10;
	   public static final int MONTHS_M_VALUE = 11;
	   public static final int MONTHS_1_VALUE = 57;
	   public static final int MONTHS_1_XYY_VALUE = 85;
	   public static final int MONTHS_1SYY_VALUE = 86;
	   public static final int MONTHS_MONTH_1_START_VALUE = 44;
	   public static final int MONTHS_M1_START_VALUE = 60;
	   public static final int MONTHS_1_START_VALUE = 61;
	   public static final int MONTHS_MONTH_1_END_VALUE = 45;
	   public static final int MONTHS_M1_END_VALUE = 58;
	   public static final int MONTHS_1_END_VALUE = 59;
	   // Thirds of Months
	   public static final int TRIMONTHS_1_VALUE = -120;
	   public static final int TRIMONTHS_B_VALUE = -119;
	   public static final int TRIMONTHS_BEGINNING_VALUE = -118;
	   public static final int TRIMONTHS_MS1_VALUE = -117;
	   public static final int TRIMONTHS_MSB_VALUE = -116;
	   public static final int TRIMONTHS_MMMM_BEGINNING_VALUE = -115;
	   public static final int TRIMONTHS_MMM_1_VALUE = -114;
	   public static final int TRIMONTHS_MMM_B_VALUE = -113;
	   public static final int TRIMONTHS_MMMM_1_VALUE = -112;
	   public static final int TRIMONTHS_MS1SYY_VALUE = -111;
	   public static final int TRIMONTHS_MSBSYY_VALUE = -110;
	   public static final int TRIMONTHS_MMM_1_X02_VALUE = -109;
	   public static final int TRIMONTHS_MMM_B_X02_VALUE = -108;
	   public static final int TRIMONTHS_MMMM_1_YYYY_VALUE = -107;
	   public static final int TRIMONTHS_MMMM_BEGINNING_YYYY_VALUE = -106;
	   // Weeks
	   public static final int WEEKS_MMMM_DD_YYYY_VALUE = 12;
	   public static final int WEEKS_MMM_DD_XYY_VALUE = 13;
	   public static final int WEEKS_MMMM_DD_VALUE = 14;
	   public static final int WEEKS_MMM_DD_VALUE = 15;
	   public static final int WEEKS_MDD_VALUE = 89;
	   public static final int WEEKS_MSDDSYY_VALUE = 16;
	   public static final int WEEKS_MSDD_VALUE = 17;
	   public static final int WEEKS_DD_VALUE = 87;
	   public static final int WEEKS_DDD_DD_VALUE = 88;
	   public static final int WEEKS_DDD_MSDDSYY_VALUE = 100;
	   public static final int WEEKS_DDD_MMMM_DD_XYY_VALUE = 102;
	   public static final int WEEKS_DDD_MMM_DD_XYY_VALUE = 101;
	   public static final int WEEKS_DDD_MMMM_DD_VALUE = 96;
	   public static final int WEEKS_DDD_MMM_DD_VALUE = 93;
	   public static final int WEEKS_MMM_W_VALUE = 94;
	   public static final int WEEKS_D_MMM_DD_VALUE = 95;
	   public static final int WEEKS_DDD_M_DD_VALUE = 97;
	   public static final int WEEKS_DD_M_DD_VALUE = 98;
	   public static final int WEEKS_D_M_DD_VALUE = 99;
	   public static final int WEEKS_DDD_MSDD_VALUE = 90;
	   public static final int WEEKS_DD_MSDD_VALUE = 91;
	   public static final int WEEKS_D_MSDD_VALUE = 92;
	   public static final int WEEKS_W_VALUE = 50;
	   public static final int WEEKS_DDD_W_VALUE = 103;
	   public static final int WEEKS_D_W_VALUE = 104;
	   public static final int WEEKS_WEEK_1_START_VALUE = 42;
	   public static final int WEEKS_W1_START_VALUE = 69;
	   public static final int WEEKS_1_START_VALUE = 70;
	   public static final int WEEKS_WEEK_1_END_VALUE = 43;
	   public static final int WEEKS_W1_END_VALUE = 67;
	   public static final int WEEKS_1_END_VALUE = 68;
	   // Days
	   public static final int DAYS_DDD_MMM_DD_XYY_VALUE = 22;
	   public static final int DAYS_DDD_MMMM_DD_VALUE = 111;
	   public static final int DAYS_DDD_MMM_DD_VALUE = 23;
	   public static final int DAYS_DDD_M_DD_VALUE = 112;
	   public static final int DAYS_DD_M_DD_VALUE = 113;
	   public static final int DAYS_D_M_DD_VALUE = 114;
	   public static final int DAYS_DDD_MSDD_VALUE = 108;
	   public static final int DAYS_DD_MSDD_VALUE = 109;
	   public static final int DAYS_D_MSDD_VALUE = 110;
	   public static final int DAYS_DDD_DD_VALUE = 105;
	   public static final int DAYS_DD_DD_VALUE = 106;
	   public static final int DAYS_D_DD_VALUE = 107;
	   public static final int DAYS_DXDD_VALUE = 121;
	   public static final int DAYS_DDD_DD_XYY_VALUE = 24;
	   public static final int DAYS_M_DD_VALUE = 115;
	   public static final int DAYS_DDDD_VALUE = 18;
	   public static final int DAYS_DDD_VALUE = 19;
	   public static final int DAYS_DD_VALUE = 119;
	   public static final int DAYS_D_VALUE = 20;
	   public static final int DAYS_MSDDSYY_VALUE = 26;
	   public static final int DAYS_DDD_MSDDSYY_VALUE = 52;
	   public static final int DAYS_MSDD_VALUE = 27;
	   public static final int DAYS_1_VALUE = 21;
	   public static final int DAYS_128_YYYY_VALUE = 117;
	   public static final int DAYS_128_XYY_VALUE = 116;
	   public static final int DAYS_128_VALUE = 118;
	   public static final int DAYS_DAY_1_START_VALUE = 40;
	   public static final int DAYS_D1_START_VALUE = 55;
	   public static final int DAYS_1_START_VALUE = 56;
	   public static final int DAYS_DAY_1_END_VALUE = 41;
	   public static final int DAYS_D1_END_VALUE = 53;
	   public static final int DAYS_1_END_VALUE = 54;
	   // Hours
	   public static final int HOURS_DDD_MMM_DD_HH_AM_VALUE = 28;
	   public static final int HOURS_MMM_DD_HH_AM_VALUE = 29;
	   public static final int HOURS_MSDD_HH_AM_VALUE = 120;
	   public static final int HOURS_HHMM_AM_VALUE = 30;
	   public static final int HOURS_HH_AM_VALUE = 31;
	   public static final int HOURS_HH_VALUE = 32;
	   public static final int HOURS_HOUR_1_START_VALUE = 38;
	   public static final int HOURS_H1_START_VALUE = 78;
	   public static final int HOURS_1_START_VALUE = 79;
	   public static final int HOURS_HOUR_1_END_VALUE = 39;
	   public static final int HOURS_H1_END_VALUE = 76;
	   public static final int HOURS_1_END_VALUE = 77;
	   // Minutes
	   public static final int MINUTES_HHMM_AM_VALUE = 33;
	   public static final int MINUTES_MM_VALUE = 34;
	   public static final int MINUTES_MINUTE_1_START_VALUE = 36;
	   public static final int MINUTES_M1_START_VALUE = 82;
	   public static final int MINUTES_1_START_VALUE = 83;
	   public static final int MINUTES_MINUTE_1_END_VALUE = 37;
	   public static final int MINUTES_M1_END_VALUE = 80;
	   public static final int MINUTES_1_END_VALUE = 81;

	   public static final TimescaleFormat NONE = new TimescaleFormat (NONE_VALUE);
	   // Years
	   public static final TimescaleFormat YEAR_YYYY = new TimescaleFormat (YEAR_YYYY_VALUE);
	   public static final TimescaleFormat YEAR_XYY = new TimescaleFormat (YEAR_XYY_VALUE);
	   public static final TimescaleFormat YEAR_YY = new TimescaleFormat (YEAR_YY_VALUE);
	   public static final TimescaleFormat YEAR_YEAR_START = new TimescaleFormat (YEAR_YEAR_START_VALUE);
	   public static final TimescaleFormat YEAR_Y_START = new TimescaleFormat (YEAR_Y_START_VALUE);
	   public static final TimescaleFormat YEAR_1_START = new TimescaleFormat (YEAR_1_START_VALUE);
	   public static final TimescaleFormat YEAR_YEAR_END = new TimescaleFormat (YEAR_YEAR_END_VALUE);
	   public static final TimescaleFormat YEAR_Y_END = new TimescaleFormat (YEAR_Y_END_VALUE);
	   public static final TimescaleFormat YEAR_1_END = new TimescaleFormat (YEAR_1_END_VALUE);
	   // Half Years
	   public static final TimescaleFormat HALFYEAR_1_HALF = new TimescaleFormat (HALFYEAR_1_HALF_VALUE);
	   public static final TimescaleFormat HALFYEAR_HALF_1_YYYY = new TimescaleFormat (HALFYEAR_HALF_1_YYYY_VALUE);
	   public static final TimescaleFormat HALFYEAR_HALF_1 = new TimescaleFormat (HALFYEAR_HALF_1_VALUE);
	   public static final TimescaleFormat HALFYEAR_H1_XYY = new TimescaleFormat (HALFYEAR_H1_XYY_VALUE);
	   public static final TimescaleFormat HALFYEAR_H1 = new TimescaleFormat (HALFYEAR_H1_VALUE);
	   public static final TimescaleFormat HALFYEAR_1 = new TimescaleFormat (HALFYEAR_1_VALUE);
	   public static final TimescaleFormat HALFYEAR_1HYY = new TimescaleFormat (HALFYEAR_1HYY_VALUE);
	   public static final TimescaleFormat HALFYEAR_HALF_1_START = new TimescaleFormat (HALFYEAR_HALF_1_START_VALUE);
	   public static final TimescaleFormat HALFYEAR_H1_START = new TimescaleFormat (HALFYEAR_H1_START_VALUE);
	   public static final TimescaleFormat HALFYEAR_1_START = new TimescaleFormat (HALFYEAR_1_START_VALUE);
	   public static final TimescaleFormat HALFYEAR_HALF_1_END = new TimescaleFormat (HALFYEAR_HALF_1_END_VALUE);
	   public static final TimescaleFormat HALFYEAR_H1_END = new TimescaleFormat (HALFYEAR_H1_END_VALUE);
	   public static final TimescaleFormat HALFYEAR_1_END = new TimescaleFormat (HALFYEAR_1_END_VALUE);
	   // Quarters
	   public static final TimescaleFormat QUARTER_1_QUARTER = new TimescaleFormat (QUARTER_1_QUARTER_VALUE);
	   public static final TimescaleFormat QUARTER_QTR_1_YYYY = new TimescaleFormat (YEAR_YYYY_VALUE);
	   public static final TimescaleFormat QUARTER_QTR_1 = new TimescaleFormat (QUARTER_QTR_1_VALUE);
	   public static final TimescaleFormat QUARTER_Q1_XYY = new TimescaleFormat (QUARTER_Q1_XYY_VALUE);
	   public static final TimescaleFormat QUARTER_Q1 = new TimescaleFormat (QUARTER_Q1_VALUE);
	   public static final TimescaleFormat QUARTER_1 = new TimescaleFormat (QUARTER_1_VALUE);
	   public static final TimescaleFormat QUARTER_1QYY = new TimescaleFormat (QUARTER_1QYY_VALUE);
	   public static final TimescaleFormat QUARTER_QUARTER_1_START = new TimescaleFormat (QUARTER_QUARTER_1_START_VALUE);
	   public static final TimescaleFormat QUARTER_Q1_START = new TimescaleFormat (QUARTER_Q1_START_VALUE);
	   public static final TimescaleFormat QUARTER_1_START = new TimescaleFormat (QUARTER_1_START_VALUE);
	   public static final TimescaleFormat QUARTER_QUARTER_1_END = new TimescaleFormat (QUARTER_QUARTER_1_END_VALUE);
	   public static final TimescaleFormat QUARTER_Q1_END = new TimescaleFormat (QUARTER_Q1_END_VALUE);
	   public static final TimescaleFormat QUARTER_1_END = new TimescaleFormat (QUARTER_1_END_VALUE);
	   // Months
	   public static final TimescaleFormat MONTHS_MMMM_YYYY = new TimescaleFormat (MONTHS_MMMM_YYYY_VALUE);
	   public static final TimescaleFormat MONTHS_MMM_XYY = new TimescaleFormat (MONTHS_MMM_XYY_VALUE);
	   public static final TimescaleFormat MONTHS_MMMM = new TimescaleFormat (MONTHS_MMMM_VALUE);
	   public static final TimescaleFormat MONTHS_MMM = new TimescaleFormat (MONTHS_MMM_VALUE);
	   public static final TimescaleFormat MONTHS_M = new TimescaleFormat (MONTHS_M_VALUE);
	   public static final TimescaleFormat MONTHS_1 = new TimescaleFormat (MONTHS_1_VALUE);
	   public static final TimescaleFormat MONTHS_1_XYY = new TimescaleFormat (MONTHS_1_XYY_VALUE);
	   public static final TimescaleFormat MONTHS_1SYY = new TimescaleFormat (MONTHS_1SYY_VALUE);
	   public static final TimescaleFormat MONTHS_MONTH_1_START = new TimescaleFormat (MONTHS_MONTH_1_START_VALUE);
	   public static final TimescaleFormat MONTHS_M1_START = new TimescaleFormat (MONTHS_M1_START_VALUE);
	   public static final TimescaleFormat MONTHS_1_START = new TimescaleFormat (MONTHS_1_START_VALUE);
	   public static final TimescaleFormat MONTHS_MONTH_1_END = new TimescaleFormat (MONTHS_MONTH_1_END_VALUE);
	   public static final TimescaleFormat MONTHS_M1_END = new TimescaleFormat (MONTHS_M1_END_VALUE);
	   public static final TimescaleFormat MONTHS_1_END = new TimescaleFormat (MONTHS_1_END_VALUE);
	   // Thirds of Months
	   public static final TimescaleFormat TRIMONTHS_1 = new TimescaleFormat (TRIMONTHS_1_VALUE);
	   public static final TimescaleFormat TRIMONTHS_B = new TimescaleFormat (TRIMONTHS_B_VALUE);
	   public static final TimescaleFormat TRIMONTHS_BEGINNING = new TimescaleFormat (TRIMONTHS_BEGINNING_VALUE);
	   public static final TimescaleFormat TRIMONTHS_MS1 = new TimescaleFormat (TRIMONTHS_MS1_VALUE);
	   public static final TimescaleFormat TRIMONTHS_MSB = new TimescaleFormat (TRIMONTHS_MSB_VALUE);
	   public static final TimescaleFormat TRIMONTHS_MMMM_BEGINNING = new TimescaleFormat (TRIMONTHS_MMMM_BEGINNING_VALUE);
	   public static final TimescaleFormat TRIMONTHS_MMM_1 = new TimescaleFormat (TRIMONTHS_MMM_1_VALUE);
	   public static final TimescaleFormat TRIMONTHS_MMM_B = new TimescaleFormat (TRIMONTHS_MMM_B_VALUE);
	   public static final TimescaleFormat TRIMONTHS_MMMM_1 = new TimescaleFormat (TRIMONTHS_MMMM_1_VALUE);
	   public static final TimescaleFormat TRIMONTHS_MS1SYY = new TimescaleFormat (TRIMONTHS_MS1SYY_VALUE);
	   public static final TimescaleFormat TRIMONTHS_MSBSYY = new TimescaleFormat (TRIMONTHS_MSBSYY_VALUE);
	   public static final TimescaleFormat TRIMONTHS_MMM_1_X02 = new TimescaleFormat (TRIMONTHS_MMM_1_X02_VALUE);
	   public static final TimescaleFormat TRIMONTHS_MMM_B_X02 = new TimescaleFormat (TRIMONTHS_MMM_B_X02_VALUE);
	   public static final TimescaleFormat TRIMONTHS_MMMM_1_YYYY = new TimescaleFormat (TRIMONTHS_MMMM_1_YYYY_VALUE);
	   public static final TimescaleFormat TRIMONTHS_MMMM_BEGINNING_YYYY = new TimescaleFormat (TRIMONTHS_MMMM_BEGINNING_YYYY_VALUE);
	   // Weeks
	   public static final TimescaleFormat WEEKS_MMMM_DD_YYYY = new TimescaleFormat (WEEKS_MMMM_DD_YYYY_VALUE);
	   public static final TimescaleFormat WEEKS_MMM_DD_XYY = new TimescaleFormat (WEEKS_MMM_DD_XYY_VALUE);
	   public static final TimescaleFormat WEEKS_MMMM_DD = new TimescaleFormat (WEEKS_MMMM_DD_VALUE);
	   public static final TimescaleFormat WEEKS_MMM_DD = new TimescaleFormat (WEEKS_MMM_DD_VALUE);
	   public static final TimescaleFormat WEEKS_MDD = new TimescaleFormat (WEEKS_MDD_VALUE);
	   public static final TimescaleFormat WEEKS_MSDDSYY = new TimescaleFormat (WEEKS_MSDDSYY_VALUE);
	   public static final TimescaleFormat WEEKS_MSDD = new TimescaleFormat (WEEKS_MSDD_VALUE);
	   public static final TimescaleFormat WEEKS_DD = new TimescaleFormat (WEEKS_DD_VALUE);
	   public static final TimescaleFormat WEEKS_DDD_DD = new TimescaleFormat (WEEKS_DDD_DD_VALUE);
	   public static final TimescaleFormat WEEKS_DDD_MSDDSYY = new TimescaleFormat (WEEKS_DDD_MSDDSYY_VALUE);
	   public static final TimescaleFormat WEEKS_DDD_MMMM_DD_XYY = new TimescaleFormat (WEEKS_DDD_MMMM_DD_XYY_VALUE);
	   public static final TimescaleFormat WEEKS_DDD_MMM_DD_XYY = new TimescaleFormat (WEEKS_DDD_MMM_DD_XYY_VALUE);
	   public static final TimescaleFormat WEEKS_DDD_MMMM_DD = new TimescaleFormat (WEEKS_DDD_MMMM_DD_VALUE);
	   public static final TimescaleFormat WEEKS_DDD_MMM_DD = new TimescaleFormat (WEEKS_DDD_MMM_DD_VALUE);
	   public static final TimescaleFormat WEEKS_MMM_W = new TimescaleFormat (WEEKS_MMM_W_VALUE);
	   public static final TimescaleFormat WEEKS_D_MMM_DD = new TimescaleFormat (WEEKS_D_MMM_DD_VALUE);
	   public static final TimescaleFormat WEEKS_DDD_M_DD = new TimescaleFormat (WEEKS_DDD_M_DD_VALUE);
	   public static final TimescaleFormat WEEKS_DD_M_DD = new TimescaleFormat (WEEKS_DD_M_DD_VALUE);
	   public static final TimescaleFormat WEEKS_D_M_DD = new TimescaleFormat (WEEKS_D_M_DD_VALUE);
	   public static final TimescaleFormat WEEKS_DDD_MSDD = new TimescaleFormat (WEEKS_DDD_MSDD_VALUE);
	   public static final TimescaleFormat WEEKS_DD_MSDD = new TimescaleFormat (WEEKS_DD_MSDD_VALUE);
	   public static final TimescaleFormat WEEKS_D_MSDD = new TimescaleFormat (WEEKS_D_MSDD_VALUE);
	   public static final TimescaleFormat WEEKS_W = new TimescaleFormat (WEEKS_W_VALUE);
	   public static final TimescaleFormat WEEKS_DDD_W = new TimescaleFormat (WEEKS_DDD_W_VALUE);
	   public static final TimescaleFormat WEEKS_D_W = new TimescaleFormat (WEEKS_D_W_VALUE);
	   public static final TimescaleFormat WEEKS_WEEK_1_START = new TimescaleFormat (WEEKS_WEEK_1_START_VALUE);
	   public static final TimescaleFormat WEEKS_W1_START = new TimescaleFormat (WEEKS_W1_START_VALUE);
	   public static final TimescaleFormat WEEKS_1_START = new TimescaleFormat (WEEKS_1_START_VALUE);
	   public static final TimescaleFormat WEEKS_WEEK_1_END = new TimescaleFormat (WEEKS_WEEK_1_END_VALUE);
	   public static final TimescaleFormat WEEKS_W1_END = new TimescaleFormat (WEEKS_W1_END_VALUE);
	   public static final TimescaleFormat WEEKS_1_END = new TimescaleFormat (WEEKS_1_END_VALUE);
	   // Days
	   public static final TimescaleFormat DAYS_DDD_MMM_DD_XYY = new TimescaleFormat (DAYS_DDD_MMM_DD_XYY_VALUE);
	   public static final TimescaleFormat DAYS_DDD_MMMM_DD = new TimescaleFormat (DAYS_DDD_MMMM_DD_VALUE);
	   public static final TimescaleFormat DAYS_DDD_MMM_DD = new TimescaleFormat (DAYS_DDD_MMM_DD_VALUE);
	   public static final TimescaleFormat DAYS_DDD_M_DD = new TimescaleFormat (DAYS_DDD_M_DD_VALUE);
	   public static final TimescaleFormat DAYS_DD_M_DD = new TimescaleFormat (DAYS_DD_M_DD_VALUE);
	   public static final TimescaleFormat DAYS_D_M_DD = new TimescaleFormat (DAYS_D_M_DD_VALUE);
	   public static final TimescaleFormat DAYS_DDD_MSDD = new TimescaleFormat (DAYS_DDD_MSDD_VALUE);
	   public static final TimescaleFormat DAYS_DD_MSDD = new TimescaleFormat (DAYS_DD_MSDD_VALUE);
	   public static final TimescaleFormat DAYS_D_MSDD = new TimescaleFormat (DAYS_D_MSDD_VALUE);
	   public static final TimescaleFormat DAYS_DDD_DD = new TimescaleFormat (DAYS_DDD_DD_VALUE);
	   public static final TimescaleFormat DAYS_DD_DD = new TimescaleFormat (DAYS_DD_DD_VALUE);
	   public static final TimescaleFormat DAYS_D_DD = new TimescaleFormat (DAYS_D_DD_VALUE);
	   public static final TimescaleFormat DAYS_DXDD = new TimescaleFormat (DAYS_DXDD_VALUE);
	   public static final TimescaleFormat DAYS_DDD_DD_XYY = new TimescaleFormat (DAYS_DDD_DD_XYY_VALUE);
	   public static final TimescaleFormat DAYS_M_DD = new TimescaleFormat (DAYS_M_DD_VALUE);
	   public static final TimescaleFormat DAYS_DDDD = new TimescaleFormat (DAYS_DDDD_VALUE);
	   public static final TimescaleFormat DAYS_DDD = new TimescaleFormat (DAYS_DDD_VALUE);
	   public static final TimescaleFormat DAYS_DD = new TimescaleFormat (DAYS_DD_VALUE);
	   public static final TimescaleFormat DAYS_D = new TimescaleFormat (DAYS_D_VALUE);
	   public static final TimescaleFormat DAYS_MSDDSYY = new TimescaleFormat (DAYS_MSDDSYY_VALUE);
	   public static final TimescaleFormat DAYS_DDD_MSDDSYY = new TimescaleFormat (DAYS_DDD_MSDDSYY_VALUE);
	   public static final TimescaleFormat DAYS_MSDD = new TimescaleFormat (DAYS_MSDD_VALUE);
	   public static final TimescaleFormat DAYS_1 = new TimescaleFormat (DAYS_1_VALUE);
	   public static final TimescaleFormat DAYS_128_YYYY = new TimescaleFormat (DAYS_128_YYYY_VALUE);
	   public static final TimescaleFormat DAYS_128_XYY = new TimescaleFormat (DAYS_128_XYY_VALUE);
	   public static final TimescaleFormat DAYS_128 = new TimescaleFormat (DAYS_128_VALUE);
	   public static final TimescaleFormat DAYS_DAY_1_START = new TimescaleFormat (DAYS_DAY_1_START_VALUE);
	   public static final TimescaleFormat DAYS_D1_START = new TimescaleFormat (DAYS_D1_START_VALUE);
	   public static final TimescaleFormat DAYS_1_START = new TimescaleFormat (DAYS_1_START_VALUE);
	   public static final TimescaleFormat DAYS_DAY_1_END = new TimescaleFormat (DAYS_DAY_1_END_VALUE);
	   public static final TimescaleFormat DAYS_D1_END = new TimescaleFormat (DAYS_D1_END_VALUE);
	   public static final TimescaleFormat DAYS_1_END = new TimescaleFormat (DAYS_1_END_VALUE);
	   // Hours
	   public static final TimescaleFormat HOURS_DDD_MMM_DD_HH_AM = new TimescaleFormat (HOURS_DDD_MMM_DD_HH_AM_VALUE);
	   public static final TimescaleFormat HOURS_MMM_DD_HH_AM = new TimescaleFormat (HOURS_MMM_DD_HH_AM_VALUE);
	   public static final TimescaleFormat HOURS_MSDD_HH_AM = new TimescaleFormat (HOURS_MSDD_HH_AM_VALUE);
	   public static final TimescaleFormat HOURS_HHMM_AM = new TimescaleFormat (HOURS_HHMM_AM_VALUE);
	   public static final TimescaleFormat HOURS_HH_AM = new TimescaleFormat (HOURS_HH_AM_VALUE);
	   public static final TimescaleFormat HOURS_HH = new TimescaleFormat (HOURS_HH_VALUE);
	   public static final TimescaleFormat HOURS_HOUR_1_START = new TimescaleFormat (HOURS_HOUR_1_START_VALUE);
	   public static final TimescaleFormat HOURS_H1_START = new TimescaleFormat (HOURS_H1_START_VALUE);
	   public static final TimescaleFormat HOURS_1_START = new TimescaleFormat (HOURS_1_START_VALUE);
	   public static final TimescaleFormat HOURS_HOUR_1_END = new TimescaleFormat (HOURS_HOUR_1_END_VALUE);
	   public static final TimescaleFormat HOURS_H1_END = new TimescaleFormat (HOURS_H1_END_VALUE);
	   public static final TimescaleFormat HOURS_1_END = new TimescaleFormat (HOURS_1_END_VALUE);
	   // Minutes
	   public static final TimescaleFormat MINUTES_HHMM_AM = new TimescaleFormat (MINUTES_HHMM_AM_VALUE);
	   public static final TimescaleFormat MINUTES_MM = new TimescaleFormat (MINUTES_MM_VALUE);
	   public static final TimescaleFormat MINUTES_MINUTE_1_START = new TimescaleFormat (MINUTES_MINUTE_1_START_VALUE);
	   public static final TimescaleFormat MINUTES_M1_START = new TimescaleFormat (MINUTES_M1_START_VALUE);
	   public static final TimescaleFormat MINUTES_1_START = new TimescaleFormat (MINUTES_1_START_VALUE);
	   public static final TimescaleFormat MINUTES_MINUTE_1_END = new TimescaleFormat (MINUTES_MINUTE_1_END_VALUE);
	   public static final TimescaleFormat MINUTES_M1_END = new TimescaleFormat (MINUTES_M1_END_VALUE);
	   public static final TimescaleFormat MINUTES_1_END = new TimescaleFormat (MINUTES_1_END_VALUE);
	   
	   
	   private static final TimescaleFormat[] FORMATS_ARRAY =
	   {
		   NONE,
		   YEAR_YYYY,
		   YEAR_XYY,
		   YEAR_YY,
		   YEAR_YEAR_START,
		   YEAR_Y_START,
		   YEAR_1_START,
		   YEAR_YEAR_END,
		   YEAR_Y_END,
		   YEAR_1_END,
		   HALFYEAR_1_HALF,
		   HALFYEAR_HALF_1_YYYY,
		   HALFYEAR_HALF_1,
		   HALFYEAR_H1_XYY,
		   HALFYEAR_H1,
		   HALFYEAR_1,
		   HALFYEAR_1HYY,
		   HALFYEAR_HALF_1_START,
		   HALFYEAR_H1_START,
		   HALFYEAR_1_START,
		   HALFYEAR_HALF_1_END,
		   HALFYEAR_H1_END,
		   HALFYEAR_1_END,
		   QUARTER_1_QUARTER,
		   QUARTER_QTR_1_YYYY,
		   QUARTER_QTR_1,
		   QUARTER_Q1_XYY,
		   QUARTER_Q1,
		   QUARTER_1,
		   QUARTER_1QYY,
		   QUARTER_QUARTER_1_START,
		   QUARTER_Q1_START,
		   QUARTER_1_START,
		   QUARTER_QUARTER_1_END,
		   QUARTER_Q1_END,
		   QUARTER_1_END,
		   MONTHS_MMMM_YYYY,
		   MONTHS_MMM_XYY,
		   MONTHS_MMMM,
		   MONTHS_MMM,
		   MONTHS_M,
		   MONTHS_1,
		   MONTHS_1_XYY,
		   MONTHS_1SYY,
		   MONTHS_MONTH_1_START,
		   MONTHS_M1_START,
		   MONTHS_1_START,
		   MONTHS_MONTH_1_END,
		   MONTHS_M1_END,
		   MONTHS_1_END,		   
		   TRIMONTHS_1,
		   TRIMONTHS_B,
		   TRIMONTHS_BEGINNING,
		   TRIMONTHS_MS1,
		   TRIMONTHS_MSB,
		   TRIMONTHS_MMMM_BEGINNING,
		   TRIMONTHS_MMM_1,
		   TRIMONTHS_MMM_B,
		   TRIMONTHS_MMMM_1,
		   TRIMONTHS_MS1SYY,
		   TRIMONTHS_MSBSYY,
		   TRIMONTHS_MMM_1_X02,
		   TRIMONTHS_MMM_B_X02,
		   TRIMONTHS_MMMM_1_YYYY,
		   TRIMONTHS_MMMM_BEGINNING_YYYY,
		   WEEKS_MMMM_DD_YYYY,
		   WEEKS_MMM_DD_XYY,
		   WEEKS_MMMM_DD,
		   WEEKS_MMM_DD,
		   WEEKS_MDD,
		   WEEKS_MSDDSYY,
		   WEEKS_MSDD,
		   WEEKS_DD,
		   WEEKS_DDD_DD,
		   WEEKS_DDD_MSDDSYY,
		   WEEKS_DDD_MMMM_DD_XYY,
		   WEEKS_DDD_MMM_DD_XYY,
		   WEEKS_DDD_MMMM_DD,
		   WEEKS_DDD_MMM_DD,
		   WEEKS_MMM_W,
		   WEEKS_D_MMM_DD,
		   WEEKS_DDD_M_DD,
		   WEEKS_DD_M_DD,
		   WEEKS_D_M_DD,
		   WEEKS_DDD_MSDD,
		   WEEKS_DD_MSDD,
		   WEEKS_D_MSDD,
		   WEEKS_W,
		   WEEKS_DDD_W,
		   WEEKS_D_W,
		   WEEKS_WEEK_1_START,
		   WEEKS_W1_START,
		   WEEKS_1_START,
		   WEEKS_WEEK_1_END,
		   WEEKS_W1_END,
		   WEEKS_1_END,
		   DAYS_DDD_MMM_DD_XYY,
		   DAYS_DDD_MMMM_DD,
		   DAYS_DDD_MMM_DD,
		   DAYS_DDD_M_DD,
		   DAYS_DD_M_DD,
		   DAYS_D_M_DD,
		   DAYS_DDD_MSDD,
		   DAYS_DD_MSDD,
		   DAYS_D_MSDD,
		   DAYS_DDD_DD,
		   DAYS_DD_DD,
		   DAYS_D_DD,
		   DAYS_DXDD,
		   DAYS_DDD_DD_XYY,
		   DAYS_M_DD,
		   DAYS_DDDD,
		   DAYS_DDD,
		   DAYS_DD,
		   DAYS_D,
		   DAYS_MSDDSYY,
		   DAYS_DDD_MSDDSYY,
		   DAYS_MSDD,
		   DAYS_1,
		   DAYS_128_YYYY,
		   DAYS_128_XYY,
		   DAYS_128,
		   DAYS_DAY_1_START,
		   DAYS_D1_START,
		   DAYS_1_START,
		   DAYS_DAY_1_END,
		   DAYS_D1_END,
		   DAYS_1_END,
		   HOURS_DDD_MMM_DD_HH_AM,
		   HOURS_MMM_DD_HH_AM,
		   HOURS_MSDD_HH_AM,
		   HOURS_HHMM_AM,
		   HOURS_HH_AM,
		   HOURS_HH,
		   HOURS_HOUR_1_START,
		   HOURS_H1_START,
		   HOURS_1_START,
		   HOURS_HOUR_1_END,
		   HOURS_H1_END,
		   HOURS_1_END,
		   MINUTES_HHMM_AM,
		   MINUTES_MM,
		   MINUTES_MINUTE_1_START,
		   MINUTES_M1_START,
		   MINUTES_1_START,
		   MINUTES_MINUTE_1_END,
		   MINUTES_M1_END,
		   MINUTES_1_END
	   };

	   // NOTE: The index here needs to match the corresponding format in the FORMATS_ARRAY
	   private static final String[] FORMATS_NAMES =
	   {
		   "None",
		   // Years
		   "2002, 2003, ...",
		   "'02, '03, ...",
		   "02, 03, ...",
		   "Year 1, Year 2 ... (From Start)",
		   "Y1, Y2, Y3, ... (From Start)",
		   "1, 2, 3, 4, ...(From Start)",
		   "Year 2, Year 1 ... (From End)",
		   "Y3, Y2, Y1, ... (From End)",
		   "4, 3, 2, 1, ... (From End)",
		   // Half Years
		   "1st Half, 2nd Half, ...",
		   "Half 1, 2002, Half2, 2002, ...",
		   "Half 1, Half2, ...",
		   "H1 '02, H2 '02, ...",
		   "H1, H2, ...",
		   "1, 2, ...",
		   "1H02, 2H02, ...",
		   "Half 1, Half 2, Half 3, ... (From Start)",
		   "H1, H2, H3, ... (From Start)",
		   "1, 2, 3, ... (From Start)",
		   "Half 3, Half 2, Half 1, ... (From End)",
		   "H3, H2, H1, ... (From End)",
		   "3, 2, 1, ... (From End)",
		   // Quarters
		   "1st Quarter",
		   "Qtr 1, 2002",
		   "Qtr 1, Qtr2, ...",
		   "Q1 '02, Q2 '02, ...",
		   "Q1, Q2, ...",
		   "1, 2, ...",
		   "1Q02, 2Q02, ...",
		   "Quarter 1, Quarter 2, ...(From Start)",
		   "Q1, Q2, Q3, Q4, ... (From Start)",
		   "1, 2, 3, 4, ... (From Start)",
		   "Quarter 2 Quarter 1, ... (From End)",
		   "Q4, Q3, Q2, Q1, ... (From End)",
		   "4, 3, 2, 1, (From End)",
		   // Months
		   "January 2002",
		   "Jan '02",
		   "January",
		   "Jan, Feb, ...",
		   "J, F, ...",
		   "1, 2, ...",
		   "1 '02",
		   "1/02",
		   "Month 1, Month 2, ... (From Start)",
		   "M1, M2, M3, ... (From Start)",
		   "1, 2, 3, 4, ... (From Start)",
		   "Month 2 Month 1, ... (From End)",
		   "M3, M2, M1, ... (From End)",
		   "4, 3, 2, 1, ... (From End)",
		   // Thirds of Months
		   "1, 11, 21, ...",
		   "B, M, E, ...",
		   "Beginning, Middle, End, ...",
		   "1/1, 1/11, 1/21, ...",
		   "1/B, 1/M, 1/E, ...",
		   "January Beginning, January Middle, ...",
		   "Jan 1, Jan 11, Jan 21, ...",
		   "Jan B, Jan M, Jan E, ...",
		   "January 1, January 11, January 21, ...",
		   "1/1/02, 1/11/02, 1/21/02, ...",
		   "1/B/02, 1/M/02, 1/E/02, ...",
		   "Jan 1/02, Jan 11/02, Jan 21/02, ...",
		   "Jan B/02, Jan M/02, Jan E/02, ...",
		   "January 1, 2002, January 11, 2002, ...",
		   "January Beginning, ...",
		   // Weeks
		   "January 27, 2007",
		   "January 27, '02",
		   "January 27",
		   "Jan 27, Feb 3, ...",
		   "J 27, F 3, ...",
		   "1/27/02, 2/3/02, ...",
		   "1/27, 2/3, ...",
		   "27, 3, ...",
		   "Sun 27",
		   "Sun 1/27/02",
		   "Sun January 27, '02",
		   "Sun Jan 27, '02",
		   "Sun January 27",
		   "Su Jan 27",
		   "S Jan 27",
		   "Sun J 27",
		   "Su J 27",
		   "S J 27",
		   "Sun 1/27",
		   "Su 1/27",
		   "S 1/27",
		   "1, 2, ...52, 1, 2",
		   "Sun 1, ..., Sun 52, Sun 1,...",
		   "1 1, ..., 7 1, 1 2, ..., 7 52",
		   "Week 1, Week 2, ... (From Start)",
		   "W1, W2, ... (From start)",
		   "1, 2, 3, 4, ... (From start)",
		   "Week 2, Week 1, ... (From end)",
		   "W4, W3, W2, W1, ... (From end)",
		   "4, 3, 2, 1, ... (From end)",
		   // Days
		   "Mon Jan 28, '02",
		   "Mon January 28",
		   "Mon Jan 28",
		   "Mon J 28",
		   "Mo J 28",
		   "M J 28",
		   "Mon 1/28",
		   "Mo 1/28",
		   "M 1/28",
		   "Mon 28",
		   "Mo 28",
		   "M 28",
		   "M28",
		   "Jan 28, '02",
		   "Jan 28, Jan 29, ... ",
		   "J28, J29, ... ",
		   "Sunday, Monday, ...",
		   "Sun, Mon, Tue, ...",
		   "Su, Mo, Tu, ...",
		   "S, M, T, ...",
		   "1/28/02, 1/29/02, ... ",
		   "Mon 1/28/02, Tue 1/29/02, ... ",
		   "1/28, 1/29, ... ",
		   "1, 2, ...",
		   "128 2002 (Day of Year)",
		   "128 '02 (Day of Year)",
		   "128 (Day of Year)",
		   "Day 1, Day 2, ...  (From Start)",
		   "D1, D2, D3, ...  (From start)",
		   "1, 2, 3, 4, ...  (From start)",
		   "Day 2, Day 1, ...  (From end)",
		   "D3, D2, D1, ...  (From end)",
		   "4, 3, 2, 1, ...  (From start)",
		   // Hours
		   "Mon Jan 28, 11 AM",		   
		   "Jan 28, 11 AM",
		   "1/28, 11 AM",
		   "11:00 AM, 12:00 PM, ... ",
		   "11 AM, 12 PM, ... ",
		   "11, 12, ... ",
		   "Hour 1, Hour 2, ...  (From Start)",
		   "H1, H2, H3, ...  (From start)",
		   "1, 2, 3, 4, ...  (From start)",
		   "Hour 2, Hour 1, ...  (From end)",
		   "H3, H2, H1, ...  (From end)",
		   "4, 3, 2, 1, ...  (From start)",
		   // Minutes
		   "1:45 PM, 1:46 PM, ... ",
		   "45, 46, 47, ... ",
		   "Minute 1, Minute 2, ...  (From Start)",
		   "M1, M2, M3, ...  (From start)",
		   "1, 2, 3, 4, ...  (From start)",
		   "Minute 2, Minute 1, ...  (From end)",
		   "M3, M2, M1, ...  (From end)",
		   "4, 3, 2, 1, ...  (From start)"
	   };

	   private int m_value;
}
