/*
 * file:       LocaleData_ru.java
 * author:     Roman Bilous
 *             Jon Iles
 * copyright:  (c) Packwood Software 2004
 * date:       11/05/2010
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

import org.mpxj.CodePage;
import org.mpxj.CurrencySymbolPosition;
import org.mpxj.DateOrder;
import org.mpxj.ProjectDateFormat;
import org.mpxj.ProjectTimeFormat;

/**
 * This class defines the Russian translation of resource required by MPX files.
 */
public final class LocaleData_ru extends ListResourceBundle
{
   @Override public Object[][] getContents()
   {
      return (RESOURCE_DATA);
   }

   private static final String[][] TIME_UNITS_ARRAY_DATA =
   {
      {
         "\u043C\u0438\u043D\u0443\u0442\u0430"
      },
      {
         "\u0447\u0430\u0441"
      },
      {
         "\u0434\u0435\u043D\u044C"
      },
      {
         "\u043D\u0435\u0434\u0435\u043B\u044F"
      },
      {
         "\u043C\u0435\u0441\u044F\u0446"
      },
      {
         "\u0433\u043E\u0434"
      },
      {
         "%"
      },
      {
         "\u043A\u0430\u0436\u0434\u0443\u044E \u043C\u0438\u043D\u0443\u0442\u0443"
      },
      {
         "\u043A\u0430\u0436\u0434\u044B\u0439 \u0447\u0430\u0441"
      },
      {
         "\u043A\u0430\u0436\u0434\u044B\u0439 \u0434\u0435\u043D\u044C"
      },
      {
         "\u043A\u0430\u0436\u0434\u0443\u044E \u043D\u0435\u0434\u0435\u043B\u044E"
      },
      {
         "\u043A\u0430\u0436\u0434\u044B\u0439 \u043C\u0435\u0441\u044F\u0446"
      },
      {
         "\u043A\u0430\u0436\u0434\u044B\u0439 \u0433\u043E\u0434"
      },
      {
         "\u043A\u0430\u0436\u0434\u044B\u0439 %"
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

   private static final String[] ACCRUE_TYPES_DATA =
   {
      "\u041D\u0430\u0447\u0430\u043B\u043E", //   "Start",
      "\u041A\u043E\u043D\u0435\u0446", //   "End",
      "\u041F\u043E\u0432\u0441\u0435\u0434\u043D\u0435\u0432\u043D\u044B\u0439" //   "Prorated"
   };

   private static final String[] RELATION_TYPES_DATA =
   {
      "\u041A\u041A", //   "FF",
      "\u041A\u041D", //   "FS",
      "\u041D\u041A", //   "SF",
      "\u041D\u041D" //   "SS"
   };

   private static final String[] PRIORITY_TYPES_DATA =
   {
      "\u0421\u0430\u043C\u044B\u0439 \u043D\u0438\u0437\u043A\u0438\u0439", //   "Lowest",
      "\u041E\u0447\u0435\u043D\u044C \u043D\u0438\u0437\u043A\u0438\u0439", //   "Very Low",
      "\u041D\u0438\u0437\u043A\u0438\u0439", //   "Lower",
      "\u041D\u0438\u0436\u0435 \u0441\u0440\u0435\u0434\u043D\u0435\u0433\u043E", //   "Low",
      "\u0421\u0440\u0435\u0434\u043D\u0438\u0439", //   "Medium",
      "\u0412\u044B\u0448\u0435 \u0441\u0440\u0435\u0434\u043D\u0435\u0433\u043E", //   "High",
      "\u0412\u044B\u0441\u043E\u043A\u0438\u0439", //   "Higher",
      "\u041E\u0447\u0435\u043D\u044C \u0432\u044B\u0441\u043E\u043A\u0438\u0439", //   "Very High",
      "\u041D\u0430\u0438\u0432\u044B\u0441\u0448\u0438\u0439", //   "Highest",
      "\u0411\u0435\u0437 \u043F\u0440\u0438\u043E\u0440\u0438\u0442\u0435\u0442\u0430" //   "Do Not Level"
   };

   private static final String[] CONSTRAINT_TYPES_DATA =
   {
      "\u041A\u0430\u043A \u043C\u043E\u0436\u043D\u043E \u0440\u0430\u043D\u044C\u0448\u0435", //   "As Soon As Possible",
      "\u041A\u0430\u043A \u043C\u043E\u0436\u043D\u043E \u043F\u043E\u0437\u0436\u0435", //   "As Late As Possible",
      "\u0414\u043E\u043B\u0436\u0435\u043D \u043D\u0430\u0447\u0430\u0442\u044C\u0441\u044F", //   "Must Start On",
      "\u0414\u043E\u043B\u0436\u0435\u043D \u0437\u0430\u043A\u043E\u043D\u0447\u0438\u0442\u044C\u0441\u044F", //   "Must Finish On",
      "\u041D\u0430\u0447\u0430\u0442\u044C\u0441\u044F \u043D\u0435 \u0440\u0430\u043D\u044C\u0448\u0435", //   "Start No Earlier Than",
      "\u041D\u0430\u0447\u0430\u0442\u044C\u0441\u044F \u043D\u0435 \u043F\u043E\u0437\u0436\u0435", //   "Start No Later Than",
      "\u0417\u0430\u043A\u043E\u043D\u0447\u0438\u0442\u044C\u0441\u044F \u043D\u0435 \u0440\u0430\u043D\u044C\u0448\u0435", //   "Finish No Earlier Than",
      "\u0417\u0430\u043A\u043E\u043D\u0447\u0438\u0442\u044C\u0441\u044F \u043D\u0435 \u043F\u043E\u0437\u0436\u0435" //   "Finish No Later Than"
   };

   private static final String[] TASK_NAMES_DATA =
   {
      null, //
      "\u041D\u0430\u0437\u0432\u0430\u043D\u0438\u0435", //   "Name",
      "WBS", //   "WBS",
      "\u0412\u043D\u0435\u0448\u043D\u0438\u0439 \u0443\u0440\u043E\u0432\u0435\u043D\u044C", //   "Outline Level",
      "\u0422\u0435\u043A\u0441\u04421", //   "Text1",
      "\u0422\u0435\u043A\u0441\u04422", //   "Text2",
      "\u0422\u0435\u043A\u0441\u04423", //   "Text3",
      "\u0422\u0435\u043A\u0441\u04424", //   "Text4",
      "\u0422\u0435\u043A\u0441\u04425", //   "Text5",
      "\u0422\u0435\u043A\u0441\u04426", //   "Text6",
      "\u0422\u0435\u043A\u0441\u04427", //   "Text7",
      "\u0422\u0435\u043A\u0441\u04428", //   "Text8",
      "\u0422\u0435\u043A\u0441\u04429", //   "Text9",
      "\u0422\u0435\u043A\u0441\u044210", //   "Text10",
      "\u041F\u0440\u0438\u043C\u0435\u0447\u0430\u043D\u0438\u0435", //   "Notes",
      "\u041A\u043E\u043D\u0442\u0430\u043A\u0442", //   "Contact",
      "\u0413\u0440\u0443\u043F\u043F\u0430 \u0440\u0435\u0441\u0443\u0440\u0441\u043E\u0432", //   "Resource Group",
      null, //
      null, //
      null, //
      "\u0420\u0430\u0431\u043E\u0442\u0430", //   "Work",
      "\u0417\u0430\u043F\u043B\u0430\u043D\u0438\u0440\u043E\u0432\u0430\u043D\u043E \u0440\u0430\u0431\u043E\u0442", //   "Baseline Work",
      "\u0412\u044B\u043F\u043E\u043B\u043D\u0435\u043D\u043E \u0440\u0430\u0431\u043E\u0442", //   "Actual Work",
      "\u041E\u0441\u0442\u0430\u043B\u043E\u0441\u044C \u0440\u0430\u0431\u043E\u0442", //   "Remaining Work",
      "\u0420\u0430\u0437\u0431\u0435\u0436\u043D\u043E\u0441\u0442\u044C \u0440\u0430\u0431\u043E\u0442", //   "Work Variance",
      "% \u0440\u0430\u0431\u043E\u0442 \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043D\u043E", //   "% Work Complete",
      null, //
      null, //
      null, //
      null, //
      "\u0421\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C", //   "Cost",
      "\u0417\u0430\u043F\u0430\u043B\u043D\u0438\u0440\u043E\u0432\u0430\u043D\u043D\u0430\u044F \u0441\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C", //   "Baseline Cost",
      "\u0420\u0435\u0430\u043B\u044C\u043D\u0430\u044F \u0441\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C", //   "Actual Cost",
      "\u041E\u0436\u0438\u0434\u0430\u0435\u043C\u0430\u044F \u0441\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C", //   "Remaining Cost",
      "\u0420\u0430\u0437\u0431\u0435\u0436\u043D\u043D\u043E\u0441\u0442\u044C \u0441\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u0438", //   "Cost Variance",
      "\u0424\u0438\u043A\u0441\u0438\u0440\u043E\u0432\u0430\u043D\u043D\u0430\u044F \u0441\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C", //   "Fixed Cost",
      "\u0421\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C1", //   "Cost1",
      "\u0421\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C2", //   "Cost2",
      "\u0421\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C3", //   "Cost3",
      null, //
      "\u0414\u043B\u0438\u0442\u0435\u043B\u044C\u043D\u043E\u0441\u0442\u044C", //   "Duration",
      "\u0417\u0430\u043F\u0430\u043B\u043D\u0438\u0440\u043E\u0432\u0430\u043D\u043D\u0430\u044F \u0434\u043B\u0438\u0442\u0435\u043B\u044C\u043D\u043E\u0441\u0442\u044C", //   "Baseline Duration",
      "\u0420\u0435\u0430\u043B\u044C\u043D\u0430\u044F \u0434\u043B\u0438\u0442\u0435\u043B\u044C\u043D\u043E\u0441\u0442\u044C", //   "Actual Duration",
      "\u041E\u0436\u0438\u0434\u0430\u0435\u043C\u0430\u044F \u0434\u043B\u0438\u0442\u0435\u043B\u044C\u043D\u043E\u0441\u0442\u044C", //   "Remaining Duration",
      "% \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043D\u043E", //   "% Complete",
      "\u0420\u0430\u0437\u0431\u0435\u0436\u043D\u043D\u043E\u0441\u0442\u044C \u0434\u043B\u0438\u0442\u0435\u043B\u044C\u043D\u043E\u0441\u0442\u0438", //   "Duration Variance",
      "\u0414\u043B\u0438\u0442\u0435\u043B\u044C\u043D\u043E\u0441\u0442\u044C1", //   "Duration1",
      "\u0414\u043B\u0438\u0442\u0435\u043B\u044C\u043D\u043E\u0441\u0442\u044C2", //   "Duration2",
      "\u0414\u043B\u0438\u0442\u0435\u043B\u044C\u043D\u043E\u0441\u0442\u044C3", //   "Duration3",
      null, //
      "\u041D\u0430\u0447\u0430\u043B\u043E", //   "Start",
      "\u041E\u043A\u043E\u043D\u0447\u0430\u043D\u0438\u0435", //   "Finish",
      "\u041F\u0440\u0435\u0434\u044B\u0434\u0443\u0449\u0435\u0435 \u043D\u0430\u0447\u0430\u043B\u043E", //   "Early Start",
      "\u041F\u0440\u0435\u0434\u044B\u0434\u0443\u0449\u0435\u0435 \u043E\u043A\u043E\u043D\u0447\u0430\u043D\u0438\u0435", //   "Early Finish",
      "\u0421\u043B\u0435\u0434\u0443\u044E\u0449\u0435\u0435 \u043D\u0430\u0447\u0430\u043B\u043E", //   "Late Start",
      "\u0421\u043B\u0435\u0434\u0443\u044E\u0449\u0435\u0435 \u043E\u043A\u043E\u043D\u0447\u0430\u043D\u0438\u0435", //   "Late Finish",
      "\u0417\u0430\u043F\u0430\u043B\u043D\u0438\u0440\u043E\u0430\u043D\u043D\u043E\u0435 \u043D\u0430\u0447\u0430\u043B\u043E", //   "Baseline Start",
      "\u0417\u0430\u043F\u043B\u0430\u043D\u0438\u0440\u043E\u0432\u0430\u043D\u043D\u043E\u0435 \u043E\u043A\u043E\u043D\u0447\u0430\u043D\u0438\u0435", //   "Baseline Finish",
      "\u0420\u0435\u0430\u043B\u044C\u043D\u043E\u0435 \u043D\u0430\u0447\u0430\u043B\u043E", //   "Actual Start",
      "\u0420\u0435\u0430\u043B\u044C\u043D\u043E\u0435 \u043E\u043A\u043E\u043D\u0447\u0430\u043D\u0438\u0435", //   "Actual Finish",
      "\u041D\u0430\u0447\u0430\u043B\u043E1", //   "Start1",
      "\u041E\u043A\u043E\u043D\u0447\u0430\u043D\u0438\u0435", //   "Finish1",
      "\u041D\u0430\u0447\u0430\u043B\u043E2", //   "Start2",
      "\u041E\u043A\u043E\u043D\u0447\u0430\u043D\u0438\u04352", //   "Finish2",
      "\u041D\u0430\u0447\u0430\u043B\u043E3", //   "Start3",
      "\u041E\u043A\u043E\u043D\u0447\u0430\u043D\u0438\u04353", //   "Finish3",
      "\u0421\u043C\u0435\u0448\u0435\u043D\u0438\u0435 \u043D\u0430\u0447\u0430\u043B\u0430", //   "Start Variance",
      "\u0421\u043C\u0435\u0449\u0435\u043D\u0438\u0435 \u043E\u043A\u043E\u043D\u0447\u0430\u043D\u0438\u044F", //   "Finish Variance",
      "\u041F\u0440\u0438\u043D\u0443\u0434\u0438\u0442\u0435\u043B\u044C\u043D\u0430\u044F \u0434\u0430\u0442\u0430", //   "Constraint Date",
      null, //
      "\u041F\u0440\u0435\u0434\u0448\u0435\u0441\u0442\u0432\u0435\u043D\u043D\u0438\u043A\u0438", //   "Predecessors",
      "\u041D\u0430\u0441\u043B\u0435\u0434\u043D\u0438\u043A\u0438", //   "Successors",
      "\u0418\u043C\u044F \u0440\u0435\u0441\u0443\u0440\u0441\u0430", //   "Resource Names",
      "\u0418\u043D\u0438\u0446\u0438\u0430\u043B\u044B \u0440\u0435\u0441\u0443\u0440\u0441\u0430", //   "Resource Initials",
      "\u0423\u043D\u0438\u043A\u0430\u043B\u044C\u043D\u044B\u0439 ID \u043F\u0440\u0435\u0434\u0448\u0435\u0441\u0442\u0432\u0435\u043D\u043D\u0438\u043A\u0430", //   "Unique ID Predecessors",
      "\u0423\u043D\u0438\u043A\u0430\u043B\u044C\u043D\u044B\u0439 ID \u043D\u0430\u0441\u043B\u0435\u0434\u043D\u0438\u043A\u0430", //   "Unique ID Successors",
      null, //
      null, //
      null, //
      null, //
      "\u0424\u0438\u043A\u0441\u0438\u0440\u043E\u0432\u0430\u043D\u043D\u044B\u0439", //   "Fixed",
      "\u0412\u0435\u0445\u0430", //   "Milestone",
      "\u041A\u0440\u0438\u0442\u0438\u0447\u0435\u0441\u043A\u0430\u044F", //   "Critical",
      "\u041F\u043E\u043C\u0435\u0447\u0435\u043D\u043D\u0430\u044F", //   "Marked",
      "\u041F\u043E\u0432\u044B\u0448\u0435\u043D\u043D\u0430\u044F", //   "Rollup",
      "\u0421\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C \u0437\u0430\u043F\u043B\u0430\u043D\u0438\u0440\u043E\u0432\u0430\u043D\u043D\u044B\u0445 \u0440\u0430\u0431\u043E\u0442", //   "BCWS",
      "\u0421\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C \u0432\u044B\u043F\u043E\u043B\u043D\u0435\u043D\u043D\u044B\u0445 \u0440\u0430\u0431\u043E\u0442", //   "BCWP",
      "\u0421\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C \u043E\u0441\u0442\u0430\u0442\u043E\u0447\u043D\u044B\u0445 \u0440\u0430\u0431\u043E\u0442", //   "SV",
      "\u0420\u0430\u0437\u0431\u0435\u0436\u043D\u043E\u0441\u0442\u044C \u0441\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u0435\u0439", //   "CV",
      null, //
      "ID", //   "ID",
      "\u041F\u0440\u0438\u043D\u0443\u0434\u0438\u0442\u0435\u043B\u044C\u043D\u044B\u0439 \u0422\u0438\u043F", //   "Constraint Type",
      "\u0417\u0430\u0434\u0435\u0440\u0436\u043A\u0430", //   "Delay",
      "\u0421\u0432\u043E\u0431\u043E\u0434\u043D\u043E", //   "Free Slack",
      "\u0412\u0441\u0435\u0433\u043E \u0441\u0432\u043E\u0431\u043E\u0434\u043D\u043E", //   "Total Slack",
      "\u041F\u0440\u0438\u043E\u0440\u0438\u0442\u0435\u0442", //   "Priority",
      "\u0424\u0430\u0439\u043B \u043F\u043E\u0434\u043F\u0440\u043E\u0435\u043A\u0442\u0430", //   "Subproject File",
      "\u041F\u0440\u043E\u0435\u043A\u0442", //   "Project",
      "\u0423\u043D\u0438\u043A\u0430\u043B\u044C\u043D\u044B\u0439 ID", //   "Unique ID",
      "\u0412\u043D\u0435\u0448\u043D\u0438\u0439 \u041D\u043E\u043C\u0435\u0440", //   "Outline Number",
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      "\u0424\u043B\u0430\u04331", //   "Flag1",
      "\u0424\u043B\u0430\u04332", //   "Flag2",
      "\u0424\u043B\u0430\u04333", //   "Flag3",
      "\u0424\u043B\u0430\u04334", //   "Flag4",
      "\u0424\u043B\u0430\u04335", //   "Flag5",
      "\u0424\u043B\u0430\u04336", //   "Flag6",
      "\u0424\u043B\u0430\u04337", //   "Flag7",
      "\u0424\u043B\u0430\u04338", //   "Flag8",
      "\u0424\u043B\u0430\u04339", //   "Flag9",
      "\u0424\u043B\u0430\u043310", //   "Flag10",
      "\u0412\u0441\u0435\u0433\u043E", //   "Summary",
      "\u041E\u0431\u044A\u0435\u043A\u0442\u044B", //   "Objects",
      "\u041E\u0442\u043C\u0435\u0447\u0435\u043D\u043E \u043F\u043E\u043B\u0435\u0439", //   "Linked Fields",
      "\u0421\u043A\u0440\u044B\u0442\u043E\u0435 \u043F\u043E\u043B\u0435 ", //   "Hide Bar",
      null, //
      "\u0421\u043E\u0437\u0434\u0430\u043D\u043E", //   "Created",
      "\u041D\u0430\u0447\u0430\u043B\u043E4", //   "Start4",
      "\u041E\u043A\u043E\u043D\u0447\u0430\u043D\u0438\u04354", //   "Finish4",
      "\u041D\u0430\u0447\u0430\u043B\u043E5", //   "Start5",
      "\u041E\u043A\u043E\u043D\u0447\u0430\u043D\u0438\u04355", //   "Finish5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "\u0423\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043D\u043D\u044B\u0439", //   "Confirmed",
      "\u041D\u0443\u0436\u0434\u0430\u0435\u0442\u0441\u044F \u0432 \u043E\u0431\u043D\u043E\u0432\u043B\u0435\u043D\u0438\u0438", //   "Update Needed",
      null, //
      null, //
      null, //
      "\u041D\u043E\u043C\u0435\u04401", //   "Number1",
      "\u041D\u043E\u043C\u0435\u04402", //   "Number2",
      "\u041D\u043E\u043C\u0435\u04403", //   "Number3",
      "\u041D\u043E\u043C\u0435\u04404", //   "Number4",
      "\u041D\u043E\u043C\u0435\u04405", //   "Number5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "\u0421\u0442\u043E\u043F", //   "Stop",
      "\u041D\u0430\u0447\u0430\u0442\u044C \u043D\u0435 \u0440\u0430\u043D\u044C\u0448\u0435 \u0447\u0435\u043C", //   "Resume No Earlier Than",
      "\u041D\u0430\u0447\u0430\u0442\u044C" //   "Resume"
   };

   private static final String[] RESOURCE_NAMES_DATA =
   {
      null, //
      "\u0418\u043C\u044F", //   "Name",
      "\u0418\u043D\u0438\u0446\u0438\u0430\u043B\u044B", //   "Initials",
      "\u0413\u0440\u0443\u043F\u043F\u0430", //   "Group",
      "\u041A\u043E\u0434", //   "Code",
      "\u0422\u0435\u043A\u0441\u04421", //   "Text1",
      "\u0422\u0435\u043A\u0441\u04422", //   "Text2",
      "\u0422\u0435\u043A\u0441\u04423", //   "Text3",
      "\u0422\u0435\u043A\u0441\u04424", //   "Text4",
      "\u0422\u0435\u043A\u0441\u04425", //   "Text5",
      "\u041F\u0440\u0438\u043C\u0435\u0447\u0430\u043D\u0438\u044F", //   "Notes",
      "Email", //   "Email Address",
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      "\u0420\u0430\u0431\u043E\u0442\u0430", //   "Work",
      "\u0417\u0430\u043F\u043B\u0430\u043D\u0438\u0440\u043E\u0432\u0430\u043D\u043E \u0440\u0430\u0431\u043E\u0442", //   "Baseline Work",
      "\u0412\u044B\u043F\u043E\u043B\u043D\u0435\u043D\u043E \u0440\u0430\u0431\u043E\u0442", //   "Actual Work",
      "\u041E\u0441\u0442\u0430\u043B\u043E\u0441\u044C \u0440\u0430\u0431\u043E\u0442", //   "Remaining Work",
      "\u0420\u0430\u0437\u0431\u0435\u0436\u043D\u043E\u0441\u0442\u044C \u0440\u0430\u0431\u043E\u0442", //   "Work Variance",
      "% \u0440\u0430\u0431\u043E\u0442 \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043D\u043E", //   "% Work Complete",
      null, //
      null, //
      null, //
      "\u0421\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C", //   "Cost",
      "\u0417\u0430\u043F\u0430\u043B\u043D\u0438\u0440\u043E\u0432\u0430\u043D\u043D\u0430\u044F \u0441\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C", //   "Baseline Cost",
      "\u0420\u0435\u0430\u043B\u044C\u043D\u0430\u044F \u0441\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C", //   "Actual Cost",
      "\u041E\u0436\u0438\u0434\u0430\u0435\u043C\u0430\u044F \u0441\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C", //   "Remaining Cost",
      "\u0420\u0430\u0437\u0431\u0435\u0436\u043D\u043D\u043E\u0441\u0442\u044C \u0441\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u0438", //   "Cost Variance",
      null, //
      null, //
      null, //
      null, //
      null, //
      "ID", //   "ID",
      "\u041C\u0430\u043A\u0441\u0438\u043C\u0430\u043B\u044C\u043D\u043E\u0435 \u0437\u043D\u0430\u0447\u0435\u043D\u0438\u0435", //   "Max Units",
      "\u0421\u0442\u0430\u043D\u0434\u0430\u0440\u0442\u043D\u043E\u0435 \u0437\u043D\u0430\u0447\u0435\u043D\u0438\u0435", //   "Standard Rate",
      "\u041F\u0440\u043E\u0441\u0440\u043E\u0447\u0435\u043D\u043D\u043E\u0435 \u0437\u043D\u0430\u0447\u0435\u043D\u0438\u0435", //   "Overtime Rate",
      "\u0421\u0442\u043E\u0438\u043C\u043E\u0441\u0442\u044C \u0438\u0441\u043F\u043E\u043B\u044C\u0437\u043E\u0432\u0430\u043D\u0438\u044F", //   "Cost Per Use",
      "\u041F\u0440\u043E\u0438\u0437\u043E\u0448\u043B\u043E", //   "Accrue At",
      "\u041F\u0435\u0440\u0435\u043D\u0430\u0437\u043D\u0430\u0447\u0435\u043D\u043E", //   "Overallocated",
      "\u041F\u0438\u043A", //   "Peak",
      "\u041E\u0441\u043D\u043E\u0432\u043D\u043E\u0439 \u043A\u0430\u043B\u0435\u043D\u0434\u0430\u0440\u044C", //   "Base Calendar",
      "\u0423\u043D\u0438\u043A\u0430\u043B\u044C\u043D\u044B\u0439 ID", //   "Unique ID",
      "\u041E\u0431\u044A\u0435\u043A\u0442", //   "Objects",
      "\u0421\u0432\u044F\u0437\u0430\u043D\u043D\u044B\u0435 \u043F\u043E\u043B\u044F", //   "Linked Fields",
   };

   private static final Object[][] RESOURCE_DATA =
   {
      {
         LocaleData.FILE_DELIMITER,
         ";"
      },
      {
         LocaleData.CODE_PAGE,
         CodePage.RU
      },
      {
         LocaleData.CURRENCY_SYMBOL,
         ""
      },
      {
         LocaleData.CURRENCY_SYMBOL_POSITION,
         CurrencySymbolPosition.BEFORE
      },
      {
         LocaleData.CURRENCY_DIGITS,
         Integer.valueOf(2)
      },
      {
         LocaleData.CURRENCY_THOUSANDS_SEPARATOR,
         "."
      },
      {
         LocaleData.CURRENCY_DECIMAL_SEPARATOR,
         ","
      },

      {
         LocaleData.DATE_ORDER,
         DateOrder.DMY
      },
      {
         LocaleData.TIME_FORMAT,
         ProjectTimeFormat.TWENTY_FOUR_HOUR
      },
      {
         LocaleData.DATE_SEPARATOR,
         "/"
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
         ProjectDateFormat.DD_MM_YYYY
      },
      {
         LocaleData.BAR_TEXT_DATE_FORMAT,
         Integer.valueOf(0)
      },
      {
         LocaleData.NA,
         "\u043D\u0435\u0434\u043E\u0441\u0442\u0443\u043F\u043D\u043E"
      },

      {
         LocaleData.YES,
         "\u0414\u0430"
      },
      {
         LocaleData.NO,
         "\u041D\u0435\u0442"
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
         LocaleData.ACCRUE_TYPES,
         ACCRUE_TYPES_DATA
      },
      {
         LocaleData.RELATION_TYPES,
         RELATION_TYPES_DATA
      },
      {
         LocaleData.PRIORITY_TYPES,
         PRIORITY_TYPES_DATA
      },
      {
         LocaleData.CONSTRAINT_TYPES,
         CONSTRAINT_TYPES_DATA
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
