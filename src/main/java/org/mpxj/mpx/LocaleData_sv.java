/*
 * file:       LocaleData_sv.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2004
 * date:       24/03/2004
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj.mpx;

import java.util.HashMap;
import java.util.ListResourceBundle;

import org.mpxj.CurrencySymbolPosition;
import org.mpxj.DateOrder;
import org.mpxj.ProjectDateFormat;
import org.mpxj.ProjectTimeFormat;

/**
 * This class defines the Swedish translation of resource required by MPX files.
 */
public final class LocaleData_sv extends ListResourceBundle
{
   @Override public Object[][] getContents()
   {
      return (RESOURCE_DATA);
   }

   private static final String[][] TIME_UNITS_ARRAY_DATA =
   {
      {
         "m"
      },
      {
         "t"
      },
      {
         "d"
      },
      {
         "w"
      },
      {
         "mon"
      },
      {
         "y"
      },
      {
         "%"
      },
      {
         "em"
      },
      {
         "eh"
      },
      {
         "ed"
      },
      {
         "ew"
      },
      {
         "emon"
      },
      {
         "ey"
      },
      {
         "e%"
      }
   };
   private static final HashMap<String, Integer> TIME_UNITS_MAP_DATA = new HashMap<>();

   static
   {
      for (int loop = 0; loop < TIME_UNITS_ARRAY_DATA.length; loop++)
      {
         Integer value = Integer.valueOf(loop);
         for (String name : TIME_UNITS_ARRAY_DATA[loop])
         {
            TIME_UNITS_MAP_DATA.put(name, value);
         }
      }
   }

   private static final String[] TASK_NAMES_DATA =
   {
      null,
      "Namn",
      "WBS",
      "Dispositionsniv\u00E5",
      "Text1",
      "Text2",
      "Text3",
      "Text4",
      "Text5",
      "Text6",
      "Text7",
      "Text8",
      "Text9",
      "Text10",
      "Notes", // Translate
      "Contact", // Translate
      "Resource", // Translate
      null,
      null,
      null,
      "Arbete",
      "Originalarbete",
      "Verkligt arbete",
      "Remaining Work", // Translate
      "Work Variance", // Translate
      "% Work Complete", // Translate
      null,
      null,
      null,
      null,
      "Kostnad",
      "Originalkostnad",
      "Verklig kostnad",
      "\u00C5terst\u00E5ende kostnad",
      "Cost Variance", // Translate
      "Fast kostnad",
      "Kostnad1",
      "Kostnad2",
      "Kostnad3",
      null,
      "Varaktighet",
      "Originalvaraktighet",
      "Actual Duration", // Translate
      "Remaining Duration", // Translate
      "% f\u00E4rdigt",
      "Duration Variance", // Translate
      "Varaktighet1",
      "Varaktighet2",
      "Varaktighet3",
      null,
      "Start",
      "Slut",
      "Tidig start",
      "Tidigt slut",
      "Sen start",
      "Sent slut",
      "Originalstart",
      "Originalslut",
      "Verklig start",
      "Verkligt slut",
      "Start1", // Translate
      "Finish1", // Translate
      "Start2", // Translate
      "Finish2", // Translate
      "Start3", // Translate
      "Finish3", // Translate
      "Start Variance", // Translate
      "Finish Variance", // Translate
      "Villkorsdatum",
      null,
      "F\u00F6reg\u00E5ende aktiviteter",
      "Successors", // Translate
      "Resource Names", // Translate
      "Resource Initials", // Translate
      "Unique ID Predecessors", // Translate
      "Unique ID Successors", // Translate
      null,
      null,
      null,
      null,
      "Fast",
      "Milstolpe",
      "Critical", // Translate
      "M\u00E4rkt",
      "Upplyft",
      "BCWS", // Translate
      "BCWP", // Translate
      "SV", // Translate
      "CV", // Translate
      null,
      "ID",
      "Villkorstyp",
      "F\u00F6rskjutning",
      "Fritt slack",
      "Totalt slack",
      "Prioritet",
      "Underprojektsfil",
      "Project", // Translate
      "Eget ID",
      "Outline Number", // Translate
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "Flagga1",
      "Flagga2",
      "Flagga3",
      "Flagga4",
      "Flagga5",
      "Flagga6",
      "Flagga7",
      "Flagga8",
      "Flagga9",
      "Flagga10",
      "Sammanfattning",
      "Objects", // Translate
      "Linked Fields", // Translate
      "Hide Bar", // Translate
      null,
      "Skapad",
      "Start4", // Translate
      "Finish4", // Translate
      "Start5", // Translate
      "Finish5", // Translate
      null,
      null,
      null,
      null,
      null,
      "Confirmed", // Translate
      "Update Needed", // Translate
      null,
      null,
      null,
      "Tal1",
      "Tal2",
      "Tal3",
      "Tal4",
      "Tal5",
      null,
      null,
      null,
      null,
      null,
      "Stopp",
      "Forts\u00E4tt tidigast",
      "Resume", // Translate
   };

   private static final String[] RESOURCE_NAMES_DATA =
   {
      null,
      "Namn",
      "Initialer",
      "Grupp",
      "Kod",
      "Text1",
      "Text2",
      "Text3",
      "Text4",
      "Text5",
      "Notes", // translate
      "e-post",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "Arbete",
      "Originalarbete",
      "Verkligt arbete",
      "Remaining Work", // translate
      "\u00D6vertidsarbete",
      "Work Variance", // translate
      "% Work Complete", // translate
      null,
      null,
      null,
      "Kostnad",
      "Originalkostnad",
      "Verklig kostnad",
      "Remaining Cost", // translate
      "Cost Variance", // translate
      null,
      null,
      null,
      null,
      null,
      "ID",
      "Max enheter",
      "Standardkostnad",
      "\u00D6vertidskostnad",
      "Kostnad per tillf\u00E4lle",
      "P\u00E5f\u00F6rs",
      "Overallocated", // translate
      "Peak", // translated
      "Base Calendar", // translate
      "Eget ID",
      "Objects", // translate
      "Linked Fields" // translate
   };

   private static final Object[][] RESOURCE_DATA =
   {
      {
         LocaleData.FILE_DELIMITER,
         ";"
      },
      {
         LocaleData.FILE_VERSION,
         "4,0"
      },

      {
         LocaleData.YES,
         "Ja"
      },
      {
         LocaleData.NO,
         "Nej"
      },

      {
         LocaleData.CURRENCY_SYMBOL,
         "kr"
      },
      {
         LocaleData.CURRENCY_SYMBOL_POSITION,
         CurrencySymbolPosition.AFTER_WITH_SPACE
      },
      {
         LocaleData.CURRENCY_THOUSANDS_SEPARATOR,
         " "
      },
      {
         LocaleData.CURRENCY_DECIMAL_SEPARATOR,
         ","
      },

      {
         LocaleData.DATE_ORDER,
         DateOrder.YMD
      },
      {
         LocaleData.TIME_FORMAT,
         ProjectTimeFormat.TWENTY_FOUR_HOUR
      },

      {
         LocaleData.DATE_SEPARATOR,
         "-"
      },
      {
         LocaleData.TIME_SEPARATOR,
         ":"
      },
      {
         LocaleData.AM_TEXT,
         ""
      },
      {
         LocaleData.PM_TEXT,
         ""
      },
      {
         LocaleData.DATE_FORMAT,
         ProjectDateFormat.EEE_DD_MM_YY
      },

      {
         LocaleData.TIME_UNITS_ARRAY,
         TIME_UNITS_ARRAY_DATA
      },
      {
         LocaleData.TIME_UNITS_MAP,
         TIME_UNITS_MAP_DATA
      },

      {
         LocaleData.TASK_NAMES,
         TASK_NAMES_DATA
      },
      {
         LocaleData.RESOURCE_NAMES,
         RESOURCE_NAMES_DATA
      }
   };
}
