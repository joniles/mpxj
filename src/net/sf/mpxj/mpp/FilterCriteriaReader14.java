/*
 * file:       FilterCriteriaReader14.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       2010-05-06
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

import net.sf.mpxj.FieldType;
import net.sf.mpxj.common.FieldTypeHelper;

/**
 * This class allows filter criteria definitions to be read from an MPP file.
 */
public final class FilterCriteriaReader14 extends CriteriaReader
{
   /**
    * {@inheritDoc}
    */
   @Override protected int getCriteriaBlockSize()
   {
      return 36;
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
   @Override protected byte[] getChildBlock(byte[] block)
   {
      int offset = MPPUtility.getShort(block, 32);
      return m_criteriaBlockMap.get(Integer.valueOf(offset));
   }

   /**
    * {@inheritDoc}
    */
   @Override protected byte[] getListNextBlock(byte[] block)
   {
      int offset = MPPUtility.getShort(block, 34);
      return m_criteriaBlockMap.get(Integer.valueOf(offset));
   }

   /**
    * {@inheritDoc}
    */
   @Override protected FieldType getFieldType(byte[] block)
   {
      int fieldIndex = MPPUtility.getInt(block, 8);
      return FieldTypeHelper.mapTextFields(FieldTypeHelper.getInstance14(fieldIndex));
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getTextOffset(byte[] block)
   {
      return MPPUtility.getShort(block, 26);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getPromptOffset(byte[] block)
   {
      return MPPUtility.getShort(block, 30);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getValueOffset()
   {
      return 14;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getTimeUnitsOffset()
   {
      return 24;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getCriteriaTextStartOffset()
   {
      return 16;
   }
}
