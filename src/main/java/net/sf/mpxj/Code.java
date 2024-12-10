package net.sf.mpxj;

public interface Code extends ProjectEntityWithUniqueID
{
   /**
    * Retrieve the project code unique ID.
    *
    * @return unique ID
    */
   @Override Integer getUniqueID();

   /**
    * Retrieve the sequence number of this project code.
    *
    * @return sequence number
    */
   Integer getSequenceNumber();

   /**
    * Retrieve the project code name.
    *
    * @return name
    */
   String getName();

   /**
    * Retrieve the secure flag.
    *
    * @return secure flag
    */
   boolean getSecure();

   /**
    * Retrieve the max length.
    *
    * @return max length
    */
   Integer getMaxLength();
}
