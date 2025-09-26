package org.mpxj.primavera.eppm;

public class EppmAuthenticationException extends EppmException
{
   /**
    * Constructor.
    *
    * @param ex cause
    */
   public EppmAuthenticationException(Exception ex)
   {
      super(ex);
   }

   /**
    * Constructor.
    *
    * @param message message
    */
   public EppmAuthenticationException(String message)
   {
      super(message);
   }
}
