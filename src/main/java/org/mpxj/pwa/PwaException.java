package org.mpxj.pwa;

public class PwaException extends RuntimeException
{
   public PwaException(Exception ex)
   {
      super(ex);
   }

   public PwaException(String message)
   {
      super(message);
   }
}
