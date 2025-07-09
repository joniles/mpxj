package org.mpxj.opc;

public class OpcAuthenticationException extends OpcException
{
   public OpcAuthenticationException(Exception ex)
   {
      super(ex);
   }

   public OpcAuthenticationException(String message)
   {
      super(message);
   }
}
