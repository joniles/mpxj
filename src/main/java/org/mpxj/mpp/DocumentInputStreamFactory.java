/*
 * file:       DocumentInputStreamFactory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       21/03/2015
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

import java.io.IOException;
import java.io.InputStream;

import org.mpxj.cfb.CfbFile;

/**
 * Factory used to create document input streams. Can switch between normal and encrypted streams as required.
 */
class DocumentInputStreamFactory
{
   /**
    * Constructor.
    *
    * @param props document properties
    */
   public DocumentInputStreamFactory(CfbFile cfb, Props props)
   {
      m_cfb = cfb;
      m_encrypted = props.getByte(PropsKey.PASSWORD_FLAG) != 0;
      byte code = props.getByte(PropsKey.ENCRYPTION_CODE);
      m_encryptionCode = (byte) (code == 0x00 ? 0x00 : (0xFF - code));
   }

   /**
    * Method used to instantiate the appropriate input stream reader,
    * a standard one, or one which can deal with "encrypted" data.
    *
    * @param path file path
    * @return new input stream
    */
   public InputStream getInstance(String path) throws IOException
   {
      // TODO add CFB to constructor
      InputStream stream = m_cfb.getInputStream(path);
      if (m_encrypted)
      {
         stream = new EncryptedDocumentInputStream(stream, m_encryptionCode);
      }
      return stream;
   }

   /**
    * Retrieve the encryption code.
    *
    * @return encryption code.
    */
   public byte getEncryptionCode()
   {
      return m_encryptionCode;
   }

   private final CfbFile m_cfb;
   private final boolean m_encrypted;
   private final byte m_encryptionCode;
}
