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

package net.sf.mpxj;

import java.util.stream.DoubleStream;

/**
 * Instances of this class represent enumerated work contour values.
 */
public final class WorkContour
{
   /**
    * Constructor.
    * 
    * @param name work contour name
    * @param values curve values, 20 values representing 5% duration intervals, total of values is 100%
    */
   public WorkContour(String name, double... values)
   {
      this(name, -1, values);
   }

   /**
    * Private constructor.
    *
    * @param name work contour name
    * @param type int version of the enum
    * @param values curve values, 20 values representing 5% duration intervals, total of values is 100% 
    */
   private WorkContour(String name, int type, double... values)
   {
      m_name = name;
      m_value = type;
      m_curveValues = values;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    * TODO: move this to MS Project specific code
    *
    * @param type int type
    * @return enum instance
    */
   public static WorkContour getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = FLAT.m_value;
      }
      return TYPE_VALUES[type];
   }

   /**
    * Retrieve the values which define the curve.
    * The method returns an array of 20 doubles, each representing 5% of the duration.
    * The total of the values in the array will be 100%. Note that the CONTOURED
    * enum will return null as the values for the work or cost per time period have
    * been hand crafted and do not use a curve.
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
   public boolean isContoured()
   {
      return m_curveValues == null;
   }

   /**
    * Returns true if this contour is flat.
    * 
    * @return true if flat
    */
   public boolean isFlat()
   {
      return m_curveValues != null && DoubleStream.of(m_curveValues).distinct().count() == 1;
   }

   @Override public String toString()
   {
      return m_name;
   }

   private final String m_name;

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;

   /**
    * Curve representation - one value per 5% of duration - 20 values, total of values is 100%.
    */
   private final double[] m_curveValues;

   public static final WorkContour FLAT = new WorkContour("FLAT", 0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0);
   public static final WorkContour BACK_LOADED = new WorkContour("BACK_LOADED", 1, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5);
   public static final WorkContour FRONT_LOADED = new WorkContour("FRONT_LOADED", 2, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 6.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5);
   public static final WorkContour DOUBLE_PEAK = new WorkContour("DOUBLE_PEAK", 3, 1.3, 2.5, 3.8, 5.1, 7.6, 10.1, 7.6, 5.1, 3.8, 2.5, 2.5, 2.5, 3.8, 5.1, 7.6, 10.1, 7.6, 5.1, 3.8, 2.5);
   public static final WorkContour EARLY_PEAK = new WorkContour("EARLY_PEAK", 4, 1.2, 2.5, 3.8, 5, 7.5, 10.1, 10.1, 10.1, 8.8, 7.5, 6.3, 5.0, 5.0, 5.0, 3.8, 2.5, 2.0, 1.5, 1.3, 1.0);
   public static final WorkContour LATE_PEAK = new WorkContour("LATE_PEAK", 5, 1.0, 1.3, 1.5, 2.0, 2.5, 3.8, 5.0, 5.0, 5.0, 6.3, 7.5, 8.8, 10.1, 10.1, 10.1, 7.5, 5, 3.8, 2.5, 1.2);
   public static final WorkContour BELL = new WorkContour("BELL", 6, 0.5, 0.5, 1.5, 1.5, 4.0, 4.0, 7.5, 7.5, 11.5, 11.5, 11.5, 11.5, 7.5, 7.5, 4, 4, 1.5, 1.5, 0.5, 0.5);
   public static final WorkContour TURTLE = new WorkContour("TURTLE", 7, 1.0, 1.0, 3.5, 3.5, 5.5, 5.5, 7.5, 7.5, 7.5, 7.5, 7.5, 7.5, 7.5, 7.5, 5.5, 5.5, 3.5, 3.5, 1.0, 1.0);
   public static final WorkContour CONTOURED = new WorkContour("CONTOURED", 8, null);

   /**
    * Array mapping int types to WorkContour instances.
    */
   private static final WorkContour[] TYPE_VALUES =
   {
      FLAT,
      BACK_LOADED,
      FRONT_LOADED,
      DOUBLE_PEAK,
      EARLY_PEAK,
      LATE_PEAK,
      BELL,
      TURTLE,
      CONTOURED
   };
}
