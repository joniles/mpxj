/*
 * file:       EncryptedDocumentInputStream.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2007
 * date:       20/10/2007
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

import java.io.IOException;

import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

/**
 * This class extends the POI DocumentInputStream class
 * to allow data to be decrypted before passing
 * it back to the caller.
 */
final class EncryptedDocumentInputStream extends DocumentInputStream
{
   /**
    * Constructor.
    * 
    * @param entry file entry
    * @throws IOException
    */
   public EncryptedDocumentInputStream(DocumentEntry entry)
      throws IOException
   {
      super(entry);
   }

   /**
    * {@inheritDoc}
    */
   @Override public int read() throws IOException
   {
      int value = super.read();
      value ^= m_mask;
      return (value);
   }

   /**
    * {@inheritDoc}
    */
   @Override public int read(byte[] b, int off, int len) throws IOException
   {
      int result = super.read(b, off, len);
      for (int loop = 0; loop < len; loop++)
      {
         b[loop + off] ^= m_mask;
      }
      return (result);
   }

   /**
    * Set the mask used to decrypt the stream.
    * 
    * @param mask decryption mask
    */
   public void setMask(int mask)
   {
      m_mask = mask;
   }

   private int m_mask;
}
