package org.mpxj.msplanner;

/**
 * General exception thrown when Microsoft Planner API calls do not work as expected.
 */
public class MsPlannerException extends RuntimeException
{
   /**
    * Constructor.
    *
    * @param ex cause
    */
   public MsPlannerException(Exception ex)
   {
      super(ex);
   }

   /**
    * Constructor.
    *
    * @param message message
    */
   public MsPlannerException(String message)
   {
      super(message);
   }
}
