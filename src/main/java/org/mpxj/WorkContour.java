/*
 * file:       WorkContour.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       12/02/2005
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

package org.mpxj;

import java.util.stream.DoubleStream;

/**
 * Instances of this class represent enumerated work contour values.
 */
public final class WorkContour implements ProjectEntityWithUniqueID
{
   /**
    * Constructor.
    *
    * @param uniqueID unique ID
    * @param name work contour name
    * @param isDefault true if this is a "built in" contour provided by the source application
    * @param curveValues curve values, 21 values representing 5% duration intervals, including 0%, total of values must be 100%
    */
   public WorkContour(Integer uniqueID, String name, boolean isDefault, double... curveValues)
   {
      m_uniqueID = uniqueID;
      m_name = name;
      m_default = isDefault;
      m_curveValues = curveValues;
   }

   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the values which define the curve.
    * The method returns an array of 21 doubles, each representing 5% of the duration.
    * This includes an entry for 0%. The total of the values in the array must be 100%.
    * Note that the CONTOURED enum will return null as the values for the work or cost
    * per time period have been handcrafted and do not use a curve.
    *
    * @return curve values
    */
   public double[] getCurveValues()
   {
      return m_curveValues;
   }

   /**
    * Retrieve the name of this work contour.
    *
    * @return contour name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Returns true if the timephased data has been manually edited.
    *
    * @return true if manually edited
    */
   public boolean isContourManual()
   {
      return m_curveValues == null;
   }

   /**
    * Returns true if this contour is flat.
    *
    * @return true if flat
    */
   public boolean isContourFlat()
   {
      return m_curveValues != null && m_curveValues[0] == 0.0 && DoubleStream.of(m_curveValues).skip(1).distinct().count() == 1;
   }

   /**
    * Determine if this is a "built in" work contour from the source application.
    *
    * @return true if this is a "built in" work contour
    */
   public boolean isContourDefault()
   {
      return m_default;
   }

   @Override public String toString()
   {
      return m_name;
   }

   private final String m_name;

   private final boolean m_default;

   /**
    * Internal representation of the enum int type.
    */
   private final Integer m_uniqueID;

   /**
    * Curve representation - one value per 5% of duration, including 0%, 21 values, total of values needs to be 100%.
    */
   private final double[] m_curveValues;

   public static final WorkContour FLAT = new WorkContour(Integer.valueOf(1), "FLAT", true, 0.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0);
   public static final WorkContour BACK_LOADED = new WorkContour(Integer.valueOf(2), "BACK_LOADED", true, 0.0, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5);
   public static final WorkContour FRONT_LOADED = new WorkContour(Integer.valueOf(3), "FRONT_LOADED", true, 0.0, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5);
   public static final WorkContour DOUBLE_PEAK = new WorkContour(Integer.valueOf(4), "DOUBLE_PEAK", true, 0.0, 1.3, 2.5, 3.8, 5.1, 7.6, 10.1, 7.6, 5.1, 3.8, 2.5, 2.5, 2.5, 3.8, 5.1, 7.6, 10.1, 7.6, 5.1, 3.8, 2.5);
   public static final WorkContour EARLY_PEAK = new WorkContour(Integer.valueOf(5), "EARLY_PEAK", true, 0.0, 1.2, 2.5, 3.8, 5, 7.5, 10.1, 10.1, 10.1, 8.8, 7.5, 6.3, 5.0, 5.0, 5.0, 3.8, 2.5, 2.0, 1.5, 1.3, 1.0);
   public static final WorkContour LATE_PEAK = new WorkContour(Integer.valueOf(6), "LATE_PEAK", true, 0.0, 1.0, 1.3, 1.5, 2.0, 2.5, 3.8, 5.0, 5.0, 5.0, 6.3, 7.5, 8.8, 10.1, 10.1, 10.1, 7.5, 5, 3.8, 2.5, 1.2);
   public static final WorkContour BELL = new WorkContour(Integer.valueOf(7), "BELL", true, 0.0, 0.5, 0.5, 1.5, 1.5, 4.0, 4.0, 7.5, 7.5, 11.5, 11.5, 11.5, 11.5, 7.5, 7.5, 4, 4, 1.5, 1.5, 0.5, 0.5);
   public static final WorkContour TURTLE = new WorkContour(Integer.valueOf(8), "TURTLE", true, 0.0, 1.0, 1.0, 3.5, 3.5, 5.5, 5.5, 7.5, 7.5, 7.5, 7.5, 7.5, 7.5, 7.5, 7.5, 5.5, 5.5, 3.5, 3.5, 1.0, 1.0);
   public static final WorkContour CONTOURED = new WorkContour(Integer.valueOf(9), "CONTOURED", false, (double[]) null);
}
