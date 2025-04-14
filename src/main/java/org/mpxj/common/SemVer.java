/*
 * file:       SemVer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       2020-06-16
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

package org.mpxj.common;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Simple semantic version representation, with comparison methods.
 */
public final class SemVer implements Comparable<SemVer>
{
   /**
    * Constructor.
    *
    * @param version integer version components
    */
   public SemVer(int... version)
   {
      m_version = version;
   }

   /**
    * Constructor.
    *
    * @param version string version with dot separators
    */
   public SemVer(String version)
   {
      this(Arrays.stream(version.split("\\.")).mapToInt(Integer::parseInt).toArray());
   }

   /**
    * Comparator.
    *
    * @param otherVersion version to compare
    * @return true if  this version is greater than or equal to the other version
    */
   public boolean atLeast(SemVer otherVersion)
   {
      return compareTo(otherVersion) >= 0;
   }

   /**
    * Comparator.
    *
    * @param otherVersion version to compare
    * @return true if  this version is less than the other version
    */
   public boolean before(SemVer otherVersion)
   {
      return compareTo(otherVersion) < 0;
   }

   @Override public int compareTo(SemVer o)
   {
      int maxLength = Math.max(m_version.length, o.m_version.length);
      for (int index = 0; index < maxLength; index++)
      {
         int thisValue = m_version.length > index ? m_version[index] : 0;
         int otherValue = o.m_version.length > index ? o.m_version[index] : 0;

         if (thisValue == otherValue)
         {
            continue;
         }

         return thisValue - otherValue;
      }

      return 0;
   }

   @Override public String toString()
   {
      return IntStream.of(m_version).mapToObj(Integer::toString).collect(Collectors.joining("."));
   }

   private final int[] m_version;
}
