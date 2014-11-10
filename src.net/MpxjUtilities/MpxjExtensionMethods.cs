using System;
using System.Collections;
using System.Collections.Generic;
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
        /// <summary>
        /// Retrieve an IEnumerable interface to a Java collection.
        /// </summary>
        /// <param name="list">Java collection</param>
        /// <returns></returns>
        public static IEnumerable ToIEnumerable(this java.util.Collection list)
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
        public static IEnumerable<T> ToIEnumerable<T>(this java.util.Collection list)
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
            return new DateTime(DATE_EPOCH_TICKS + (javaDate.getTime() * 10000));
        }

        /// <summary>
        /// Convert a Java Date instance to a nullable .Net DateTime instance.
        /// </summary>
        /// <param name="javaDate">Java Date</param>
        /// <returns>DateTime instance</returns>
        public static DateTime? ToNullableDateTime(this java.util.Date javaDate)
        {
            return javaDate == null ? (DateTime?)null : new DateTime(DATE_EPOCH_TICKS + (javaDate.getTime() * 10000));
        }

        /// <summary>
        /// Convert a DateTime instance to a Java Date instance.
        /// </summary>
        /// <param name="d">DateTime instance</param>
        /// <returns>Java Date instance</returns>
        public static java.util.Date ToJavaDate(this DateTime d)
        {
            TimeSpan ts = d - new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc).ToLocalTime();
            return new java.util.Date((long)ts.TotalMilliseconds);
        }

        /// <summary>
        /// Converts a nullable DateTime instance to a Java Date instance.
        /// </summary>
        /// <param name="d">Nullable DateTime instance</param>
        /// <returns>Java Date instance</returns>
        public static java.util.Date ToJavaDate(this DateTime? d)
        {
            return d == null ? null : ToJavaDate(d.Value);
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
        /// Converts a Java Number instance to a nullable int.
        /// </summary>
        /// <param name="n">Java Number instance</param>
        /// <returns>Nullable int</returns>
        public static int? ToNullableInt(this java.lang.Number n)
        {
            return n == null ? (int?)null : n.intValue();
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

        private const long DATE_EPOCH_TICKS = 621355968000000000;
    }
}
