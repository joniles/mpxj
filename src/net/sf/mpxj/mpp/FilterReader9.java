/*
 * file:       FilterReader9.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       Oct 31, 2006
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

package net.sf.mpxj.mpp;

import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.TaskField;

/**
 * This class allows filter definitions to be read from an MPP9 file.
 */
public final class FilterReader9 extends FilterReader
{
   /**
    * {@inheritDoc}
    */
   @Override protected int getCriteriaBlockSize()
   {
      return 80;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getCriteriaStartOffset()
   {
      return 20;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected Integer getVarDataType()
   {
      return (FILTER_DATA);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected byte[] getChildBlock(byte[] block)
   {
      int offset = MPPUtility.getShort(block, 74);
      return m_criteriaBlockMap.get(Integer.valueOf(offset));
   }

   /**
    * {@inheritDoc}
    */
   @Override protected byte[] getListNextBlock(byte[] block)
   {
      int offset = MPPUtility.getShort(block, 76);
      return m_criteriaBlockMap.get(Integer.valueOf(offset));
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getFieldIndex(byte[] block)
   {
      return MPPUtility.getInt(block, 40);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getTextOffset(byte[] block)
   {
      return MPPUtility.getShort(block, 68);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getPromptOffset(byte[] block)
   {
      return MPPUtility.getShort(block, 72);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getValueOffset()
   {
      return 32;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getTimeUnitsOffset()
   {
      return 42;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected TaskField getTaskField(int index)
   {
      return MPPTaskField.getInstance(index);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected ResourceField getResourceField(int index)
   {
      return MPPResourceField.getInstance(index);
   }

   private static final Integer FILTER_DATA = Integer.valueOf(1);
}
