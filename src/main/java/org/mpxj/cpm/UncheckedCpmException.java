package org.mpxj.cpm;

class UncheckedCpmException extends RuntimeException
{
   public UncheckedCpmException(CpmException ex)
   {
      super(ex);
   }

   public UncheckedCpmException(String message)
   {
      super(new CpmException(message));
   }

   @Override public CpmException getCause()
   {
      return (CpmException)super.getCause();
   }
}
