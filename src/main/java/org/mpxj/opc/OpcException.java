package org.mpxj.opc;

public class OpcException extends RuntimeException
{
   public OpcException()
   {
      super();
   }

   public OpcException(Exception ex)
   {
      super(ex);
   }

   public OpcException(String message)
   {
      super(message);
   }
}
