package net.sf.mpxj.openplan;
class OpenPlanException extends RuntimeException
{
   public OpenPlanException(Exception cause)
   {
      super(cause);
   }

   public OpenPlanException(String message)
   {
      super(message);
   }
}
