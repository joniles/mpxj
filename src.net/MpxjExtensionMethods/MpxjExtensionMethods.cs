using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;


namespace net.sf.mpxj.ExtensionMethods
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
            return new DateTime(621355968000000000 + (javaDate.getTime() * 10000));
        }
    }
}
