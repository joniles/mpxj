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

package org.mpxj.mpp;

import org.mpxj.FieldType;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.FieldTypeHelper;

/**
 * This class allows filter criteria definitions to be read from an MPP file.
 */
public final class FilterCriteriaReader14 extends CriteriaReader
{
   @Override protected int getCriteriaBlockSize()
   {
      return 36;
   }

   @Override protected int getCriteriaStartOffset()
   {
      return 20;
   }

   @Override protected byte[] getChildBlock(byte[] block)
   {
      int offset = ByteArrayHelper.getShort(block, 32);
      return m_criteriaBlockMap.get(Integer.valueOf(offset));
   }

   @Override protected byte[] getListNextBlock(byte[] block)
   {
      int offset = ByteArrayHelper.getShort(block, 34);
      return m_criteriaBlockMap.get(Integer.valueOf(offset));
   }

   @Override protected FieldType getFieldType(byte[] block)
   {
      int fieldIndex = ByteArrayHelper.getInt(block, 8);
      return FieldTypeHelper.mapTextFields(FieldTypeHelper.getInstance(m_file, fieldIndex));
   }

   @Override protected int getTextOffset(byte[] block)
   {
      return ByteArrayHelper.getShort(block, 26);
   }

   @Override protected int getPromptOffset(byte[] block)
   {
      return ByteArrayHelper.getShort(block, 30);
   }

   @Override protected int getValueOffset()
   {
      return 14;
   }

   @Override protected int getTimeUnitsOffset()
   {
      return 24;
   }

   @Override protected int getCriteriaTextStartOffset()
   {
      return 16;
   }
}
