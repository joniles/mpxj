package net.sf.mpxj;

public interface CodeValue
{
   /**
    * Retrieves the unique ID for this value.
    *
    * @return unique ID
    */
   Integer getUniqueID();

   /**
    * Retrieves the sequence number for this value.
    *
    * @return sequence number
    */
   Integer getSequenceNumber();

   /**
    * Retrieves the value name.
    *
    * @return value name
    */
   String getName();

   /**
    * Retrieves the value description.
    *
    * @return value description
    */
   String getDescription();
}
