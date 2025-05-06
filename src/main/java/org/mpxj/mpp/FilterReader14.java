/*
 * file:       FilterReader14.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       20010-03-03
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

/**
 * This class allows filter definitions to be read from an MPP file.
 */
public final class FilterReader14 extends FilterReader
{
   @Override protected Integer getVarDataType()
   {
      return (FILTER_DATA);
   }

   @Override protected CriteriaReader getCriteriaReader()
   {
      return m_criteraReader;
   }

   private final CriteriaReader m_criteraReader = new FilterCriteriaReader14();

   private static final Integer FILTER_DATA = Integer.valueOf(6);
}
