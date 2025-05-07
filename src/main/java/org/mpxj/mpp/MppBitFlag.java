
package org.mpxj.mpp;

import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.common.ByteArrayHelper;

/**
 * Represents the type and location of a bit flag within a block of data.
 */
public class MppBitFlag
{
   /**
    * Constructor.
    *
    * @param type field type
    * @param offset offset in buffer
    * @param mask bit mask
    * @param zeroValue value to return if expression is zero
    * @param nonZeroValue value to return if expression is non-zero
    */
   public MppBitFlag(FieldType type, int offset, int mask, Object zeroValue, Object nonZeroValue)
   {
      m_type = type;
      m_offset = offset;
      m_mask = mask;
      m_zeroValue = zeroValue;
      m_nonZeroValue = nonZeroValue;
   }

   /**
    * Extracts the value of this bit flag from the supplied byte array
    * and sets the value in the supplied container.
    *
    * @param container container
    * @param data byte array
    */
   public void setValue(FieldContainer container, byte[] data)
   {
      if (data != null)
      {
         container.set(m_type, ((ByteArrayHelper.getInt(data, m_offset) & m_mask) == 0) ? m_zeroValue : m_nonZeroValue);
      }
   }

   private final FieldType m_type;
   private final int m_offset;
   private final int m_mask;
   private final Object m_zeroValue;
   private final Object m_nonZeroValue;
}
