/*
 * file:       DateFormat.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       04/01/2005
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
 * Instances of this class represent enumerated date format values.
 */
public final class DateFormat
{
   /**
    * Private constructor.
    * 
    * @param value date format value
    */
   private DateFormat (int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the date format.
    * 
    * @return date format value
    */
   public int getValue ()
   {
      return (m_value);
   }
   
   /**
    * Retrieve a DateFormat instance representing the supplied value.
    * 
    * @param value date format value
    * @return DateFormat instance
    */
   public static DateFormat getInstance (int value)
   {
      DateFormat result;
      
      switch (value)
      {
         case DD_MM_YY_HH_MM_VALUE:
         {
            result = DD_MM_YY_HH_MM;
            break;
         }

         case DD_MM_YY_VALUE:
         {
            result = DD_MM_YY;
            break;
         }

         case DD_MMMMM_YYYY_HH_MM_VALUE:
         {
            result = DD_MMMMM_YYYY_HH_MM;
            break;
         }

         case DD_MMMMM_YYYY_VALUE:
         {
            result = DD_MMMMM_YYYY;
            break;
         }

         case DD_MMM_HH_MM_VALUE:
         {
            result = DD_MMM_HH_MM;
            break;
         }

         case DD_MMM_YY_VALUE:
         {
            result = DD_MMM_YY;
            break;
         }

         case DD_MMMMM_VALUE:
         {
            result = DD_MMMMM;
            break;
         }

         case DD_MMM_VALUE:
         {
            result = DD_MMM;
            break;
         }

         case EEE_DD_MM_YY_HH_MM_VALUE:
         {
            result = EEE_DD_MM_YY_HH_MM;
            break;
         }

         case EEE_DD_MM_YY_VALUE:
         {
            result = EEE_DD_MM_YY;
            break;
         }

         case EEE_DD_MMM_YY_VALUE:
         {
            result = EEE_DD_MMM_YY;
            break;
         }

         case EEE_HH_MM_VALUE:
         {
            result = EEE_HH_MM;
            break;
         }

         case DD_MM_VALUE:
         {
            result = DD_MM;
            break;
         }

         case DD_VALUE:
         {
            result = DD;
            break;
         }

         case HH_MM_VALUE:
         {
            result = HH_MM;
            break;
         }

         case EEE_DD_MMM_VALUE:
         {
            result = EEE_DD_MMM;
            break;
         }

         case EEE_DD_MM_VALUE:
         {
            result = EEE_DD_MM;
            break;
         }

         case EEE_DD_VALUE:
         {
            result = EEE_DD;
            break;
         }

         case DD_WWW_VALUE:
         {
            result = DD_WWW;
            break;
         }

         case DD_WWW_YY_HH_MM_VALUE:
         {
            result = DD_WWW_YY_HH_MM;
            break;
         }

         default:
         case DD_MM_YYYY_VALUE:
         {
            result = DD_MM_YYYY;
            break;
         }         
      }
      
      return (result);
   }
   
   /**
    * Returns a string representation of the date format type
    * to be used as part of an MPX file.
    * 
    * @return string representation
    */
   public String toString ()
   {
      return (Integer.toString(m_value));
   }

   
   private int m_value;
   
   /**
    * This format represents dates in the form 25/12/98 12:56
    */
   public static final int DD_MM_YY_HH_MM_VALUE = 0;

   /**
    * This format represents dates in the form 25/05/98
    */
   public static final int DD_MM_YY_VALUE = 1;

   /**
    * This format represents dates in the form 13 December 2002 12:56
    */
   public static final int DD_MMMMM_YYYY_HH_MM_VALUE = 2;

   /**
    * This format represents dates in the form 13 December 2002
    */
   public static final int DD_MMMMM_YYYY_VALUE = 3;

   /**
    * This format represents dates in the form 24 Nov 12:56
    */
   public static final int DD_MMM_HH_MM_VALUE = 4;

   /**
    * This format represents dates in the form 25 Aug '98
    */
   public static final int DD_MMM_YY_VALUE = 5;

   /**
    * This format represents dates in the form 25 September
    */
   public static final int DD_MMMMM_VALUE = 6;

   /**
    * This format represents dates in the form 25 Aug
    */
   public static final int DD_MMM_VALUE = 7;

   /**
    * This format represents dates in the form Thu 25/05/98 12:56
    */
   public static final int EEE_DD_MM_YY_HH_MM_VALUE = 8;

   /**
    * This format represents dates in the form Wed 25/05/98
    */
   public static final int EEE_DD_MM_YY_VALUE = 9;

   /**
    * This format represents dates in the form Wed 25 Mar '98
    */
   public static final int EEE_DD_MMM_YY_VALUE = 10;

   /**
    * This format represents dates in the form Wed 12:56
    */
   public static final int EEE_HH_MM_VALUE = 11;

   /**
    * This format represents dates in the form 25/5
    */
   public static final int DD_MM_VALUE = 12;

   /**
    * This format represents dates in the form 23
    */
   public static final int DD_VALUE = 13;

   /**
    * This format represents dates in the form 12:56
    */
   public static final int HH_MM_VALUE = 14;

   /**
    * This format represents dates in the form Wed 23 Mar
    */
   public static final int EEE_DD_MMM_VALUE = 15;

   /**
    * This format represents dates in the form Wed 25/5
    */
   public static final int EEE_DD_MM_VALUE = 16;

   /**
    * This format represents dates in the form Wed 05
    */
   public static final int EEE_DD_VALUE = 17;

   /**
    * This format represents dates in the form 5/W25
    */
   public static final int DD_WWW_VALUE = 18;

   /**
    * This format represents dates in the form 5/W25/98 12:56
    */
   public static final int DD_WWW_YY_HH_MM_VALUE = 19;

   /**
    * This format represents dates in the form 25/05/1998
    */
   public static final int DD_MM_YYYY_VALUE = 20;
   
   /**
    * This format represents dates in the form 25/12/98 12:56
    */
   public static final DateFormat DD_MM_YY_HH_MM = new DateFormat(DD_MM_YY_HH_MM_VALUE);

   /**
    * This format represents dates in the form 25/05/98
    */
   public static final DateFormat DD_MM_YY = new DateFormat(DD_MM_YY_VALUE);

   /**
    * This format represents dates in the form 13 December 2002 12:56
    */
   public static final DateFormat DD_MMMMM_YYYY_HH_MM = new DateFormat(DD_MMMMM_YYYY_HH_MM_VALUE);

   /**
    * This format represents dates in the form 13 December 2002
    */
   public static final DateFormat DD_MMMMM_YYYY = new DateFormat(DD_MMMMM_YYYY_VALUE);

   /**
    * This format represents dates in the form 24 Nov 12:56
    */
   public static final DateFormat DD_MMM_HH_MM = new DateFormat(DD_MMM_HH_MM_VALUE);

   /**
    * This format represents dates in the form 25 Aug '98
    */
   public static final DateFormat DD_MMM_YY = new DateFormat(DD_MMM_YY_VALUE);

   /**
    * This format represents dates in the form 25 September
    */
   public static final DateFormat DD_MMMMM = new DateFormat(DD_MMMMM_VALUE);

   /**
    * This format represents dates in the form 25 Aug
    */
   public static final DateFormat DD_MMM = new DateFormat(DD_MMM_VALUE);

   /**
    * This format represents dates in the form Thu 25/05/98 12:56
    */
   public static final DateFormat EEE_DD_MM_YY_HH_MM = new DateFormat(EEE_DD_MM_YY_HH_MM_VALUE);

   /**
    * This format represents dates in the form Wed 25/05/98
    */
   public static final DateFormat EEE_DD_MM_YY = new DateFormat(EEE_DD_MM_YY_VALUE);

   /**
    * This format represents dates in the form Wed 25 Mar '98
    */
   public static final DateFormat EEE_DD_MMM_YY = new DateFormat(EEE_DD_MMM_YY_VALUE);

   /**
    * This format represents dates in the form Wed 12:56
    */
   public static final DateFormat EEE_HH_MM = new DateFormat(EEE_HH_MM_VALUE);

   /**
    * This format represents dates in the form 25/5
    */
   public static final DateFormat DD_MM = new DateFormat(DD_MM_VALUE);

   /**
    * This format represents dates in the form 23
    */
   public static final DateFormat DD = new DateFormat(DD_VALUE);

   /**
    * This format represents dates in the form 12:56
    */
   public static final DateFormat HH_MM = new DateFormat(HH_MM_VALUE);

   /**
    * This format represents dates in the form Wed 23 Mar
    */
   public static final DateFormat EEE_DD_MMM = new DateFormat(EEE_DD_MMM_VALUE);

   /**
    * This format represents dates in the form Wed 25/5
    */
   public static final DateFormat EEE_DD_MM = new DateFormat(EEE_DD_MM_VALUE);

   /**
    * This format represents dates in the form Wed 05
    */
   public static final DateFormat EEE_DD = new DateFormat(EEE_DD_VALUE);

   /**
    * This format represents dates in the form 5/W25
    */
   public static final DateFormat DD_WWW = new DateFormat(DD_WWW_VALUE);

   /**
    * This format represents dates in the form 5/W25/98 12:56
    */
   public static final DateFormat DD_WWW_YY_HH_MM = new DateFormat(DD_WWW_YY_HH_MM_VALUE);

   /**
    * This format represents dates in the form 25/05/1998
    */
   public static final DateFormat DD_MM_YYYY = new DateFormat(DD_MM_YYYY_VALUE);
   
}
