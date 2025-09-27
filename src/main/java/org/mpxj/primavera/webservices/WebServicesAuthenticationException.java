package org.mpxj.primavera.webservices;

public class WebServicesAuthenticationException extends WebServicesException
{
   /**
    * Constructor.
    *
    * @param ex cause
    */
   public WebServicesAuthenticationException(Exception ex)
   {
      super(ex);
   }

   /**
    * Constructor.
    *
    * @param message message
    */
   public WebServicesAuthenticationException(String message)
   {
      super(message);
   }
}
