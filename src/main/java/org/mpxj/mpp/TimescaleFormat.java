/*
 * file:       TimescaleFormat.java
 * author:     Jari Niskala
 * copyright:  (c) Packwood Software 2009
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

package org.mpxj.mpp;

import org.mpxj.MpxjEnum;
import org.mpxj.common.EnumHelper;
import org.mpxj.common.NumberHelper;

/**
 * Enumeration representing the formats which may be shown on a Gantt chart timescale.
 */
public enum TimescaleFormat implements MpxjEnum
{
   NONE(35, "None"),

   //
   // Years
   //
   YEAR_YYYY(0, "2002, 2003, ..."),
   YEAR_XYY(1, "'02, '03, ..."),
   YEAR_YY(78, "02, 03, ..."),
   YEAR_YEAR_START(48, "Year 1, Year 2 ... (From Start)"),
   YEAR_Y_START(73, "Y1, Y2, Y3, ... (From Start)"),
   YEAR_1_START(74, "1, 2, 3, 4, ...(From Start)"),
   YEAR_YEAR_END(49, "Year 2, Year 1 ... (From End)"),
   YEAR_Y_END(71, "Y3, Y2, Y1, ... (From End)"),
   YEAR_1_END(72, "4, 3, 2, 1, ... (From End)"),

   //
   // Half years
   //
   HALFYEAR_1_HALF(123, "1st Half, 2nd Half, ..."),
   HALFYEAR_HALF_1_YYYY(124, "Half 1, 2002, Half2, 2002, ..."),
   HALFYEAR_HALF_1(125, "Half 1, Half2, ..."),
   HALFYEAR_H1_XYY(126, "H1 '02, H2 '02, ..."),
   HALFYEAR_H1(127, "H1, H2, ..."),
   HALFYEAR_1(128, "1, 2, ..."),
   HALFYEAR_1HYY(129, "1H02, 2H02, ..."),
   HALFYEAR_HALF_1_START(130, "Half 1, Half 2, Half 3, ... (From Start)"),
   HALFYEAR_H1_START(131, "H1, H2, H3, ... (From Start)"),
   HALFYEAR_1_START(132, "1, 2, 3, ... (From Start)"),
   HALFYEAR_HALF_1_END(133, "Half 3, Half 2, Half 1, ... (From End)"),
   HALFYEAR_H1_END(134, "H3, H2, H1, ... (From End)"),
   HALFYEAR_1_END(135, "3, 2, 1, ... (From End)"),

   //
   // Quarters
   //
   QUARTER_1_QUARTER(2, "1st Quarter"),
   QUARTER_QTR_1_YYYY(3, "Qtr 1, 2002"),
   QUARTER_QTR_1(5, "Qtr 1, Qtr2, ..."),
   QUARTER_Q1_XYY(4, "Q1 '02, Q2 '02, ..."),
   QUARTER_Q1(6, "Q1, Q2, ..."),
   QUARTER_1(62, "1, 2, ..."),
   QUARTER_1QYY(51, "1Q02, 2Q02, ..."),
   QUARTER_QUARTER_1_START(46, "Quarter 1, Quarter 2, ...(From Start)"),
   QUARTER_Q1_START(65, "Q1, Q2, Q3, Q4, ... (From Start)"),
   QUARTER_1_START(66, "1, 2, 3, 4, ... (From Start)"),
   QUARTER_QUARTER_1_END(47, "Quarter 2 Quarter 1, ... (From End)"),
   QUARTER_Q1_END(63, "Q4, Q3, Q2, Q1, ... (From End)"),
   QUARTER_1_END(64, "4, 3, 2, 1, (From End)"),

   //
   // Months
   //
   MONTHS_MMMM_YYYY(7, "January 2002"),
   MONTHS_MMM_XYY(8, "Jan '02"),
   MONTHS_MMMM(9, "January"),
   MONTHS_MMM(10, "Jan, Feb, ..."),
   MONTHS_M(11, "J, F, ..."),
   MONTHS_1(57, "1, 2, ..."),
   MONTHS_1_XYY(85, "1 '02"),
   MONTHS_1SYY(86, "1/02"),
   MONTHS_MONTH_1_START(44, "Month 1, Month 2, ... (From Start)"),
   MONTHS_M1_START(60, "M1, M2, M3, ... (From Start)"),
   MONTHS_1_START(61, "1, 2, 3, 4, ... (From Start)"),
   MONTHS_MONTH_1_END(45, "Month 2 Month 1, ... (From End)"),
   MONTHS_M1_END(58, "M3, M2, M1, ... (From End)"),
   MONTHS_1_END(59, "4, 3, 2, 1, ... (From End)"),

   //
   // Thirds of months
   //
   TRIMONTHS_1(136, "1, 11, 21, ..."),
   TRIMONTHS_B(137, "B, M, E, ..."),
   TRIMONTHS_BEGINNING(138, "Beginning, Middle, End, ..."),
   TRIMONTHS_MS1(139, "1/1, 1/11, 1/21, ..."),
   TRIMONTHS_MSB(140, "1/B, 1/M, 1/E, ..."),
   TRIMONTHS_MMMM_BEGINNING(141, "January Beginning, January Middle, ..."),
   TRIMONTHS_MMM_1(142, "Jan 1, Jan 11, Jan 21, ..."),
   TRIMONTHS_MMM_B(143, "Jan B, Jan M, Jan E, ..."),
   TRIMONTHS_MMMM_1(144, "January 1, January 11, January 21, ..."),
   TRIMONTHS_MS1SYY(145, "1/1/02, 1/11/02, 1/21/02, ..."),
   TRIMONTHS_MSBSYY(146, "1/B/02, 1/M/02, 1/E/02, ..."),
   TRIMONTHS_MMM_1_X02(147, "Jan 1/02, Jan 11/02, Jan 21/02, ..."),
   TRIMONTHS_MMM_B_X02(148, "Jan B/02, Jan M/02, Jan E/02, ..."),
   TRIMONTHS_MMMM_1_YYYY(149, "January 1, 2002, January 11, 2002, ..."),
   TRIMONTHS_MMMM_BEGINNING_YYYY(150, "January Beginning, ..."),

   //
   // Weeks
   //
   WEEKS_MMMM_DD_YYYY(12, "January 27, 2007"),
   WEEKS_MMM_DD_XYY(13, "January 27, '02"),
   WEEKS_MMMM_DD(14, "January 27"),
   WEEKS_MMM_DD(15, "Jan 27, Feb 3, ..."),
   WEEKS_MDD(89, "J 27, F 3, ..."),
   WEEKS_MSDDSYY(16, "1/27/02, 2/3/02, ..."),
   WEEKS_MSDD(17, "1/27, 2/3, ..."),
   WEEKS_DD(87, "27, 3, ..."),
   WEEKS_DDD_DD(88, "Sun 27"),
   WEEKS_DDD_MSDDSYY(100, "Sun 1/27/02"),
   WEEKS_DDD_MMMM_DD_XYY(102, "Sun January 27, '02"),
   WEEKS_DDD_MMM_DD_XYY(101, "Sun Jan 27, '02"),
   WEEKS_DDD_MMMM_DD(96, "Sun January 27"),
   WEEKS_DDD_MMM_DD(93, "Su Jan 27"),
   WEEKS_MMM_W(94, "S Jan 27"),
   WEEKS_D_MMM_DD(95, "Sun J 27"),
   WEEKS_DDD_M_DD(97, "Su J 27"),
   WEEKS_DD_M_DD(98, "S J 27"),
   WEEKS_D_M_DD(99, "Sun 1/27"),
   WEEKS_DDD_MSDD(90, "Su 1/27"),
   WEEKS_DD_MSDD(91, "S 1/27"),
   WEEKS_D_MSDD(92, "1, 2, ...52, 1, 2"),
   WEEKS_W(50, "Sun 1, ..., Sun 52, Sun 1,..."),
   WEEKS_DDD_W(103, "1 1, ..., 7 1, 1 2, ..., 7 52"),
   WEEKS_D_W(104, "Week 1, Week 2, ... (From Start)"),
   WEEKS_WEEK_1_START(42, "W1, W2, ... (From start)"),
   WEEKS_W1_START(69, "1, 2, 3, 4, ... (From start)"),
   WEEKS_1_START(70, "Week 2, Week 1, ... (From end)"),
   WEEKS_WEEK_1_END(43, "W4, W3, W2, W1, ... (From end)"),
   WEEKS_W1_END(67, "4, 3, 2, 1, ... (From end)"),
   WEEKS_1_END(68, "Mon Jan 28, '02"),

   //
   // Days
   //
   DAYS_DDD_MMM_DD_XYY(22, "Mon January 28"),
   DAYS_DDD_MMMM_DD(111, "Mon Jan 28"),
   DAYS_DDD_MMM_DD(23, "Mon J 28"),
   DAYS_DDD_M_DD(112, "Mo J 28"),
   DAYS_DD_M_DD(113, "M J 28"),
   DAYS_D_M_DD(114, "Mon 1/28"),
   DAYS_DDD_MSDD(108, "Mo 1/28"),
   DAYS_DD_MSDD(109, "M 1/28"),
   DAYS_D_MSDD(110, "Mon 28"),
   DAYS_DDD_DD(105, "Mo 28"),
   DAYS_DD_DD(106, "M 28"),
   DAYS_D_DD(107, "M28"),
   DAYS_DXDD(121, "Jan 28, '02"),
   DAYS_DD_MMM(25, "28 Jan"),
   DAYS_DDD_DD_XYY(24, "Jan 28, Jan 29, ... "),
   DAYS_M_DD(115, "J28, J29, ... "),
   DAYS_DDDD(18, "Sunday, Monday, ..."),
   DAYS_DDD(19, "Sun, Mon, Tue, ..."),
   DAYS_DD(119, "Su, Mo, Tu, ..."),
   DAYS_D(20, "S, M, T, ..."),
   DAYS_MSDDSYY(26, "1/28/02, 1/29/02, ... "),
   DAYS_DDD_MSDDSYY(52, "Mon 1/28/02, Tue 1/29/02, ... "),
   DAYS_MSDD(27, "1/28, 1/29, ... "),
   DAYS_1(21, "1, 2, ..."),
   DAYS_128_YYYY(117, "128 2002 (Day of Year)"),
   DAYS_128_XYY(116, "128 '02 (Day of Year)"),
   DAYS_128(118, "128 (Day of Year)"),
   DAYS_DAY_1_START(40, "Day 1, Day 2, ...  (From Start)"),
   DAYS_D1_START(55, "D1, D2, D3, ...  (From start)"),
   DAYS_1_START(56, "1, 2, 3, 4, ...  (From start)"),
   DAYS_DAY_1_END(41, "Day 2, Day 1, ...  (From end)"),
   DAYS_D1_END(53, "D3, D2, D1, ...  (From end)"),
   DAYS_1_END(54, "4, 3, 2, 1, ...  (From start)"),

   //
   // Hours
   //
   HOURS_DDD_MMM_DD_HH_AM(28, "Mon Jan 28, 11 AM"),
   HOURS_MMM_DD_HH_AM(29, "Jan 28, 11 AM"),
   HOURS_MSDD_HH_AM(120, "1/28, 11 AM"),
   HOURS_HHMM_AM(30, "11:00 AM, 12:00 PM, ... "),
   HOURS_HH_AM(31, "11 AM, 12 PM, ... "),
   HOURS_HH(32, "11, 12, ... "),
   HOURS_HOUR_1_START(38, "Hour 1, Hour 2, ...  (From Start)"),
   HOURS_H1_START(78, "H1, H2, H3, ...  (From start)"),
   HOURS_1_START(79, "1, 2, 3, 4, ...  (From start)"),
   HOURS_HOUR_1_END(39, "Hour 2, Hour 1, ...  (From end)"),
   HOURS_H1_END(76, "H3, H2, H1, ...  (From end)"),
   HOURS_1_END(77, "4, 3, 2, 1, ...  (From start)"),

   //
   // Minutes
   //
   MINUTES_HHMM_AM(33, "1:45 PM, 1:46 PM, ... "),
   MINUTES_MM(34, "45, 46, 47, ... "),
   MINUTES_MINUTE_1_START(36, "Minute 1, Minute 2, ...  (From Start)"),
   MINUTES_M1_START(82, "M1, M2, M3, ...  (From start)"),
   MINUTES_1_START(83, "1, 2, 3, 4, ...  (From start)"),
   MINUTES_MINUTE_1_END(37, "Minute 2, Minute 1, ...  (From end)"),
   MINUTES_M1_END(80, "M3, M2, M1, ...  (From end)"),
   MINUTES_1_END(81, "4, 3, 2, 1, ...  (From start)");

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name enum name
    */
   TimescaleFormat(int type, String name)
   {
      m_value = type;
      m_name = name;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static TimescaleFormat getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = NONE.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static TimescaleFormat getInstance(Number type)
   {
      int value;
      if (type == null)
      {
         value = -1;
      }
      else
      {
         value = NumberHelper.getInt(type);
      }
      return (getInstance(value));
   }

   /**
    * Accessor method used to retrieve the numeric representation of the enum.
    *
    * @return int representation of the enum
    */
   @Override public int getValue()
   {
      return (m_value);
   }

   /**
    * Retrieve the name of this alignment. Note that this is not
    * localised.
    *
    * @return name of this alignment type
    */
   public String getName()
   {
      return (m_name);
   }

   /**
    * Generate a string representation of this instance.
    *
    * @return string representation of this instance
    */
   @Override public String toString()
   {
      return (getName());
   }

   /**
    * Array mapping int types to enums.
    */
   private static final TimescaleFormat[] TYPE_VALUES = EnumHelper.createTypeArray(TimescaleFormat.class, 3);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
   private final String m_name;
}
