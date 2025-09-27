package org.mpxj.primavera.webservices;

public class WebServicesException extends RuntimeException
{
   /**
    * Constructor.
    */
   public WebServicesException()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param ex cause
    */
   public WebServicesException(Exception ex)
   {
      super(ex);
   }

   /**
    * Constructor.
    *
    * @param message message
    */
   public WebServicesException(String message)
   {
      super(message);
   }
}
