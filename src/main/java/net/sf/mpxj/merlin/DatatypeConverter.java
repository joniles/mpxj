

package net.sf.mpxj.merlin;

import java.util.UUID;

/**
 * This class contains methods used to perform the datatype conversions
 * required to read and write Phoenix files.
 */
public final class DatatypeConverter
{
   /**
    * Convert the Phoenix representation of a UUID into a Java UUID instance.
    *
    * @param value Phoenix UUID
    * @return Java UUID instance
    */
   public static final UUID parseUUID(String value)
   {
      value = value.replace("-", "+").replace("_", "/");

      byte[] data = javax.xml.bind.DatatypeConverter.parseBase64Binary(value + "==");
      long msb = 0;
      long lsb = 0;

      for (int i = 0; i < 8; i++)
      {
         msb = (msb << 8) | (data[i] & 0xff);
      }

      for (int i = 8; i < 16; i++)
      {
         lsb = (lsb << 8) | (data[i] & 0xff);
      }

      return new UUID(msb, lsb);
   }
}
