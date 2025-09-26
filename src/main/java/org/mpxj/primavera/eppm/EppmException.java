package org.mpxj.primavera.eppm;

public class EppmException extends RuntimeException
{
   /**
    * Constructor.
    */
   public EppmException()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param ex cause
    */
   public EppmException(Exception ex)
   {
      super(ex);
   }

   /**
    * Constructor.
    *
    * @param message message
    */
   public EppmException(String message)
   {
      super(message);
   }
}
