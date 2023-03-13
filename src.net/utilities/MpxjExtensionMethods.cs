using System;
using System.Collections;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;

namespace net.sf.mpxj.MpxjUtilities
{
    /// <summary>
    /// This class contains extension methods used to make it easier to work with MPXJ
    /// in .Net. In particular these methods provide a mapping between Java data types
    /// and .Net data types.
    /// </summary>
    public static class MpxjExtensionMethods
    {
        //
        // Conversion from Java to .Net
        //

        /// <summary>
        /// Convert a Java Date instance to a .Net DateTime instance.
        /// </summary>
        /// <param name="javaDate">Java Date</param>
        /// <returns>DateTime instance</returns>
        public static DateTime ToDateTime(this java.util.Date javaDate)
        {
            if (javaDate == null)
            {
                throw new ArgumentNullException();
            }

            var calendar = DateHelper.popCalendar();

            calendar.setTime(javaDate);

            var result = new DateTime(
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH) + 1,
                calendar.get(java.util.Calendar.DAY_OF_MONTH),
                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                calendar.get(java.util.Calendar.MINUTE),
                calendar.get(java.util.Calendar.SECOND),
                calendar.get(java.util.Calendar.MILLISECOND)
            );

            DateHelper.pushCalendar(calendar);

            return result;
        }

        /// <summary>
        /// Convert a Java Date instance to a nullable .Net DateTime instance.
        /// </summary>
        /// <param name="javaDate">Java Date</param>
        /// <returns>DateTime instance</returns>
        public static DateTime? ToNullableDateTime(this java.util.Date javaDate)
        {
            return javaDate == null ? null : javaDate.ToDateTime();
        }

        /// <summary>
        /// Convert a Java Number instance to a .Net int.
        /// </summary>
        /// <param name="n">Java Number</param>
        /// <returns>int value</returns>
        public static int ToInt(this java.lang.Number n)
        {
            return n == null ? 0 : n.intValue();
        }

        /// <summary>
        /// Converts a Java Number instance to a nullable int.
        /// </summary>
        /// <param name="n">Java Number instance</param>
        /// <returns>Nullable int</returns>
        public static int? ToNullableInt(this java.lang.Number n)
        {
            return n == null ? (int?)null : n.intValue();
        }

        /// <summary>
        /// Convert a Java Number instance to a .Net long.
        /// </summary>
        /// <param name="n">Java Number</param>
        /// <returns>long value</returns>
        public static long ToLong(this java.lang.Number n)
        {
            return n == null ? 0 : n.longValue();
        }

        /// <summary>
        /// Convert a Java Number instance to a .Net nullable long.
        /// </summary>
        /// <param name="n">Java Number</param>
        /// <returns>nullable long value</returns>
        public static long? ToNullableLong(this java.lang.Number n)
        {
            return n == null ? null : n.longValue();
        }


        /// <summary>
        /// Convert a Java Number instance to a .Net decimal.
        /// </summary>
        /// <param name="n">Java Number</param>
        /// <returns>decimal value</returns>
        public static decimal ToDecimal(this java.lang.Number n)
        {
            return n == null ? 0.0m : (decimal)n.doubleValue();
        }

        /// <summary>
        /// Converts a Java Number instance to a nullable decimal.
        /// </summary>
        /// <param name="n">Java Number instance</param>
        /// <returns>Nullable decimal</returns>
        public static decimal? ToNullableDecimal(this java.lang.Number n)
        {
            return n == null ? (decimal?)null : Convert.ToDecimal(n.doubleValue());
        }

        /// <summary>
        /// Converts a Java Number to a double.
        /// </summary>
        /// <param name="n">Java Number instance</param>
        /// <returns>Nullable double</returns>
        public static double ToDouble(this java.lang.Number n)
        {
            return n == null ? 0.0 : n.doubleValue();
        }

        /// <summary>
        /// Converts a Java Number to a nullable double.
        /// </summary>
        /// <param name="n">Java Number instance</param>
        /// <returns>Nullable double</returns>
        public static double? ToNullableDouble(this java.lang.Number n)
        {
            return n == null ? (double?)null : n.doubleValue();
        }

        /// <summary>
        /// Convert a Java UUID instance to a .Net Guid.
        /// </summary>
        /// <param name="u">Java UUID</param>
        /// <returns>Guid instance</returns>

        public static Guid ToGuid(this java.util.UUID u)
        {
            return u == null ? Guid.Empty : new(u.toString());
        }

        /// <summary>
        /// Convert a Java Color instance to a .Net Color.
        /// </summary>
        /// <param name="color">Java Color</param>
        /// <returns>Color instance</returns>
        public static Color ToColor(this java.awt.Color color)
        {
            return Color.FromArgb(color.getRGB());
        }

        //
        // Conversion from .Net to Java
        //

        /// <summary>
        /// Convert a DateTime instance to a Java Date instance.
        /// </summary>
        /// <param name="d">DateTime instance</param>
        /// <returns>Java Date instance</returns>
        public static java.util.Date ToJavaDate(this DateTime d)
        {
            var calendar = DateHelper.popCalendar();
            calendar.set(java.util.Calendar.YEAR, d.Year);
            calendar.set(java.util.Calendar.MONTH, d.Month - 1);
            calendar.set(java.util.Calendar.DAY_OF_MONTH, d.Day);
            calendar.set(java.util.Calendar.HOUR_OF_DAY, d.Hour);
            calendar.set(java.util.Calendar.MINUTE, d.Minute);
            calendar.set(java.util.Calendar.SECOND, d.Second);
            calendar.set(java.util.Calendar.MILLISECOND, d.Millisecond);
            var result = calendar.getTime();
            DateHelper.pushCalendar(calendar);
            return result;
        }

        /// <summary>
        /// Converts a nullable DateTime instance to a Java Date instance.
        /// </summary>
        /// <param name="d">Nullable DateTime instance</param>
        /// <returns>Java Date instance</returns>
        public static java.util.Date? ToJavaDate(this DateTime? d)
        {
            return d == null ? null : ToJavaDate(d.Value);
        }

        /// <summary>
        /// Converts a Guid instance to a nullable Java UUID instance.
        /// </summary>
        /// <param name="g">Guid instance</param>
        /// <returns>Java UUID instance</returns>
        public static java.util.UUID? ToJavaUUID(this Guid g)
        {
            return g == Guid.Empty ? null : java.util.UUID.fromString(g.ToString());
        }

        /// <summary>
        /// Converts a Color instance to a Java Color instance.
        /// </summary>
        /// <param name="c">Color instance</param>
        /// <returns>Java Color instance</returns>
        public static java.awt.Color ToJavaColor(this Color c)
        {
            return new java.awt.Color(c.R, c.G, c.B);
        }

        /// <summary>
        /// Converts an int to a Java Integer instance.
        /// </summary>
        /// <param name="i">int value</param>
        /// <returns>Java Integer instance</returns>
        public static java.lang.Integer ToJavaInteger(this int i)
        {
            return java.lang.Integer.valueOf(i);
        }

        /// <summary>
        /// Converts a nullable int to a Java Integer instance.
        /// </summary>
        /// <param name="i">nullable int value</param>
        /// <returns>Java Integer instance</returns>
        public static java.lang.Integer? ToJavaInteger(this int? i)
        {
            return i == null ? null : java.lang.Integer.valueOf(i.Value);
        }

        /// <summary>
        /// Converts a long to a Java Integer instance.
        /// </summary>
        /// <param name="l">long value</param>
        /// <returns>Java Integer instance</returns>
        public static java.lang.Integer ToJavaInteger(this long l)
        {
            return java.lang.Integer.valueOf((int)l);
        }

        /// <summary>
        /// Converts a nullable long to a Java Integer instance.
        /// </summary>
        /// <param name="l">nullable long value</param>
        /// <returns>Java Integer instance</returns>
        public static java.lang.Integer? ToJavaInteger(this long? l)
        {
            return l == null ? null : java.lang.Integer.valueOf((int)l);
        }

        /// <summary>
        /// Converts a float to a Java Double instance.
        /// </summary>
        /// <param name="f">float value</param>
        /// <returns>Java Double instance</returns>
        public static java.lang.Double ToJavaDouble(this float f)
        {
            return java.lang.Double.valueOf(f);
        }

        /// <summary>
        /// Converts a nullable float to a Java Double instance.
        /// </summary>
        /// <param name="f">nullable float value</param>
        /// <returns>Java Double instance</returns>
        public static java.lang.Double? ToJavaDouble(this float? f)
        {
            return f == null ? null : java.lang.Double.valueOf(f.Value);
        }

        /// <summary>
        /// Converts a decimal to a Java Double instance.
        /// </summary>
        /// <param name="d">decimal value</param>
        /// <returns>Java Double instance</returns>
        public static java.lang.Double ToJavaDouble(this decimal d)
        {
            return java.lang.Double.valueOf((double)d);
        }

        /// <summary>
        /// Converts a nullable decimal to a Java Double instance.
        /// </summary>
        /// <param name="d">nullable decimal value</param>
        /// <returns>Java Double instance</returns>
        public static java.lang.Double? ToJavaDouble(this decimal? d)
        {
            return d == null ? null : java.lang.Double.valueOf((double)d.Value);
        }

        /// <summary>
        /// Converts a double to a Java Double instance.
        /// </summary>
        /// <param name="d">double value</param>
        /// <returns>Java Double instance</returns>
        public static java.lang.Double ToJavaDouble(this double d)
        {
            return java.lang.Double.valueOf(d);
        }

        /// <summary>
        /// Converts a nullable double to a Java Double instance.
        /// </summary>
        /// <param name="d">nullable double value</param>
        /// <returns>Java Double instance</returns>
        public static java.lang.Double? ToJavaDouble(this double? d)
        {
            return d == null ? null : java.lang.Double.valueOf(d.Value);
        }

        /// <summary>
        /// Retrieve an IEnumerable interface to a Java collection.
        /// </summary>
        /// <param name="list">Java collection</param>
        /// <returns></returns>
        public static IEnumerable ToIEnumerable(this java.lang.Iterable list)
        {
            if (list != null)
            {
                java.util.Iterator itr = list.iterator();
                while (itr.hasNext())
                {
                    yield return itr.next();
                }
            }
        }

        /// <summary>
        /// Retrieve a type-safe IEnumerable interface to a Java collection.
        /// </summary>
        /// <param name="list">Java collection</param>
        /// <returns></returns>
        public static IEnumerable<T> ToIEnumerable<T>(this java.lang.Iterable list)
        {
            if (list != null)
            {
                java.util.Iterator itr = list.iterator();
                while (itr.hasNext())
                {
                    yield return (T)itr.next();
                }
            }
        }
    }
}
