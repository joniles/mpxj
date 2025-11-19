package org.mpxj.primavera;

import org.mpxj.CurrencySymbolPosition;

class CurrencyHelper
{
   /**
    * Generate a currency format.
    *
    * @param position currency symbol position
    * @return currency format
    */
   public static String getCurrencyFormat(CurrencySymbolPosition position)
   {
      String result;

      switch (position)
      {
         case AFTER:
         {
            result = "1.1#";
            break;
         }

         case AFTER_WITH_SPACE:
         {
            result = "1.1 #";
            break;
         }

         case BEFORE_WITH_SPACE:
         {
            result = "# 1.1";
            break;
         }

         case BEFORE:
         default:
         {
            result = "#1.1";
            break;
         }
      }

      return result;
   }

   public static final Integer DEFAULT_CURRENCY_ID = Integer.valueOf(1);
}
