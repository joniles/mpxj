using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading;

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
        /// Convert a Java LocalDateTime instance to a .Net DateTime instance.
        /// </summary>
        /// <param name="javaValue">Java LocalDateTime</param>
        /// <returns>DateTime instance</returns>
        public static DateTime ToDateTime(this java.time.LocalDateTime javaValue)
        {
            if (javaValue == null)
            {
                throw new ArgumentNullException();
            }

            return new DateTime(
                javaValue.getYear(),
                javaValue.getMonthValue(),
                javaValue.getDayOfMonth(),
                javaValue.getHour(),
                javaValue.getMinute(),
                javaValue.getSecond()
            );
        }

        /// <summary>
        /// Convert a Java LocalDateTime instance to a nullable .Net DateTime instance.
        /// </summary>
        /// <param name="javaValue">Java LocalDateTime</param>
        /// <returns>DateTime instance</returns>
        public static DateTime? ToNullableDateTime(this java.time.LocalDateTime javaValue)
        {
            return javaValue?.ToDateTime();
        }

        /// <summary>
        /// Convert a Java LocalDate instance to a .Net DateTime instance.
        /// </summary>
        /// <param name="javaValue">Java LocalDate</param>
        /// <returns>DateTime instance</returns>
        public static DateTime ToDateTime(this java.time.LocalDate javaValue)
        {
            if (javaValue == null)
            {
                throw new ArgumentNullException();
            }

            return new DateTime(
                javaValue.getYear(),
                javaValue.getMonthValue(),
                javaValue.getDayOfMonth()
            );
        }

        /// <summary>
        /// Convert a Java LocalDate instance to a nullable .Net DateTime instance.
        /// </summary>
        /// <param name="javaValue">Java LocalDate</param>
        /// <returns>DateTime instance</returns>
        public static DateTime? ToNullableDateTime(this java.time.LocalDate javaValue)
        {
            return javaValue?.ToDateTime();
        }

        /// <summary>
        /// Convert a Java LocalTime instance to a .Net DateTime instance.
        /// </summary>
        /// <param name="javaValue">Java LocalTime</param>
        /// <returns>DateTime instance</returns>
        public static DateTime ToDateTime(this java.time.LocalTime javaValue)
        {
            if (javaValue == null)
            {
                throw new ArgumentNullException();
            }

            return new DateTime(
                1,
                1,
                1,
                javaValue.getHour(),
                javaValue.getMinute(),
                javaValue.getSecond()
            );
        }

        /// <summary>
        /// Convert a Java LocalTime instance to a nullable .Net DateTime instance.
        /// </summary>
        /// <param name="javaValue">Java LocalTime</param>
        /// <returns>DateTime instance</returns>
        public static DateTime? ToNullableDateTime(this java.time.LocalTime javaValue)
        {
            return javaValue?.ToDateTime();
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
            return n?.intValue();
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
            return n?.longValue();
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
            return n?.doubleValue();
        }

        /// <summary>
        /// Convert a Java UUID instance to a .Net Guid.
        /// </summary>
        /// <param name="u">Java UUID</param>
        /// <returns>Guid instance</returns>

        public static Guid ToGuid(this java.util.UUID u)
        {
            return u == null ? Guid.Empty : new Guid(u.toString());
        }

        //
        // Conversion from .Net to Java
        //

        /// <summary>
        /// Convert a DateTime instance to a Java LocalDateTime instance.
        /// </summary>
        /// <param name="d">DateTime instance</param>
        /// <returns>Java LocalDateTime instance</returns>
        public static java.time.LocalDateTime ToJavaLocalDateTime(this DateTime d)
        {
            return java.time.LocalDateTime.of(
                d.Year,
                d.Month,
                d.Day,
                d.Hour,
                d.Minute,
                d.Second
            );
        }

        /// <summary>
        /// Converts a nullable DateTime instance to a Java LocalDateTime instance.
        /// </summary>
        /// <param name="d">Nullable DateTime instance</param>
        /// <returns>Java LocalDateTime instance</returns>
        public static java.time.LocalDateTime ToJavaLocalDateTime(this DateTime? d)
        {
            return d == null ? null : ToJavaLocalDateTime(d.Value);
        }

        /// <summary>
        /// Convert a DateTime instance to a Java LocalDate instance.
        /// </summary>
        /// <param name="d">DateTime instance</param>
        /// <returns>Java LocalDate instance</returns>
        public static java.time.LocalDate ToJavaLocalDate(this DateTime d)
        {
            return java.time.LocalDate.of(
                d.Year,
                d.Month,
                d.Day
            );
        }

        /// <summary>
        /// Converts a nullable DateTime instance to a Java LocalDate instance.
        /// </summary>
        /// <param name="d">Nullable DateTime instance</param>
        /// <returns>Java LocalDate instance</returns>
        public static java.time.LocalDate ToJavaLocalDate(this DateTime? d)
        {
            return d == null ? null : ToJavaLocalDate(d.Value);
        }

        /// <summary>
        /// Convert a DateTime instance to a Java LocalTime instance.
        /// </summary>
        /// <param name="d">DateTime instance</param>
        /// <returns>Java LocalTime instance</returns>
        public static java.time.LocalTime ToJavaLocalTime(this DateTime d)
        {
            return java.time.LocalTime.of(
                d.Hour,
                d.Minute,
                d.Second
            );
        }

        /// <summary>
        /// Converts a nullable DateTime instance to a Java LocalTime instance.
        /// </summary>
        /// <param name="d">Nullable DateTime instance</param>
        /// <returns>Java LocalTime instance</returns>
        public static java.time.LocalTime ToJavaLocalTime(this DateTime? d)
        {
            return d == null ? null : ToJavaLocalTime(d.Value);
        }

        /// <summary>
        /// Converts a Guid instance to a nullable Java UUID instance.
        /// </summary>
        /// <param name="g">Guid instance</param>
        /// <returns>Java UUID instance</returns>
        public static java.util.UUID ToJavaUUID(this Guid g)
        {
            return g == Guid.Empty ? null : java.util.UUID.fromString(g.ToString());
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
        public static java.lang.Integer ToJavaInteger(this int? i)
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
        public static java.lang.Integer ToJavaInteger(this long? l)
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
        public static java.lang.Double ToJavaDouble(this float? f)
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
        public static java.lang.Double ToJavaDouble(this decimal? d)
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
        public static java.lang.Double ToJavaDouble(this double? d)
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
