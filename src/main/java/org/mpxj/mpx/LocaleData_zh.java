/*
 * file:       LocaleData_zh.java
 * author:     Felix Tian
 *             Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       15/11/2005
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
 * This class defines the Chinese translation of resource required by MPX files.
 */
public final class LocaleData_zh extends ListResourceBundle
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
         "h"
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

   private static final String[] ACCRUE_TYPES_DATA =
   {
      "\u5F00\u59CB", // "Start",
      "\u7ED3\u675F", // "End",
      "\u6309\u6BD4\u4F8B" // "Prorated"
   };

   private static final String[] RELATION_TYPES_DATA =
   {
      "FF", //   "FF",
      "FS", //   "FS",
      "SF", //   "SF",
      "SS" //   "SS"
   };

   private static final String[] PRIORITY_TYPES_DATA =
   {
      "Lowest",
      "Very Low",
      "Lower",
      "Low",
      "Medium",
      "High",
      "Higher",
      "Very High",
      "Highest",
      "Do Not Level"
   };

   private static final String[] CONSTRAINT_TYPES_DATA =
   {
      "\u8D8A\u65E9\u8D8A\u597D", //   "As Soon As Possible",
      "\u8D8A\u665A\u8D8A\u597D", //   "As Late As Possible",
      "\u5FC5\u987B\u5F00\u59CB\u4E8E", //   "Must Start On",
      "\u5FC5\u987B\u5B8C\u6210\u4E8E", //   "Must Finish On",
      "\u4E0D\u5F97\u65E9\u4E8E...\u5F00\u59CB", //   "Start No Earlier Than",
      "\u4E0D\u5F97\u665A\u4E8E...\u5F00\u59CB", //   "Start No Later Than",
      "\u4E0D\u5F97\u65E9\u4E8E...\u5B8C\u6210", //   "Finish No Earlier Than",
      "\u4E0D\u5F97\u665A\u4E8E...\u5B8C\u6210" //   "Finish No Later Than"
   };

   private static final String[] TASK_NAMES_DATA =
   {
      null, //
      "\u540D\u79F0", //   "Name",
      "WBS", //   "WBS",
      "\u5927\u7EB2\u7EA7\u522B", //   "Outline Level",
      "\u6587\u672C1", //   "Text1",
      "\u6587\u672C2", //   "Text2",
      "\u6587\u672C3", //   "Text3",
      "\u6587\u672C4", //   "Text4",
      "\u6587\u672C5", //   "Text5",
      "\u6587\u672C6", //   "Text6",
      "\u6587\u672C7", //   "Text7",
      "\u6587\u672C8", //   "Text8",
      "\u6587\u672C9", //   "Text9",
      "\u6587\u672C10", //   "Text10",
      "\u5907\u6CE8", //  "Notes",
      "\u8054\u7CFB\u4EBA", //  "Contact",
      "\u8D44\u6E90\u7EC4", //   "Resource Group",
      null, //
      null, //
      null, //
      "\u5DE5\u65F6", //   "Work",
      "\u6BD4\u8F83\u57FA\u51C6\u5DE5\u65F6", //   "Baseline Work",
      "\u5B9E\u9645\u5DE5\u65F6", //   "Actual Work",
      "\u5269\u4F59\u5DE5\u65F6", //   "Remaining Work",
      "\u5DE5\u65F6\u5DEE\u5F02", //   "Work Variance",
      "\u5DE5\u65F6\u5B8C\u6210\u767E\u5206\u6BD4", //   "% Work Complete",
      null, //
      null, //
      null, //
      null, //
      "\u6210\u672C", //   "Cost",
      "\u6BD4\u8F83\u57FA\u51C6\u6210\u672C", //   "Baseline Cost",
      "\u5B9E\u9645\u6210\u672C", //   "Actual Cost",
      "\u5269\u4F59\u6210\u672C", //   "Remaining Cost",
      "\u6210\u672C\u5DEE\u5F02", //   "Cost Variance",
      "\u56FA\u5B9A\u6210\u672C", //   "Fixed Cost",
      "\u6210\u672C1", //   "Cost1",
      "\u6210\u672C2", //   "Cost2",
      "\u6210\u672C3", //   "Cost3",
      null, //
      "\u5DE5\u671F", //   "Duration",
      "\u6BD4\u8F83\u57FA\u51C6\u5DE5\u671F", //   "Baseline Duration",
      "\u5B9E\u9645\u5DE5\u671F", //   "Actual Duration",
      "\u5269\u4F59\u5DE5\u671F", //   "Remaining Duration",
      "\u5B8C\u6210\u767E\u5206\u6BD4", //   "% Complete",
      "\u5DE5\u671F\u5DEE\u5F02", //   "Duration Variance",
      "\u5DE5\u671F1", //   "Duration1",
      "\u5DE5\u671F2", //   "Duration2",
      "\u5DE5\u671F3", //   "Duration3",
      null, //
      "\u5F00\u59CB\u65F6\u95F4", //   "Start",
      "\u5B8C\u6210\u65F6\u95F4", //   "Finish",
      "\u6700\u65E9\u5F00\u59CB\u65F6\u95F4", //   "Early Start",
      "\u6700\u65E9\u5B8C\u6210\u65F6\u95F4", //   "Early Finish",
      "\u6700\u665A\u5F00\u59CB\u65F6\u95F4", //   "Late Start",
      "\u6700\u665A\u5B8C\u6210\u65F6\u95F4", //   "Late Finish",
      "\u6BD4\u8F83\u57FA\u51C6\u5F00\u59CB\u65F6\u95F4", //   "Baseline Start",
      "\u6BD4\u8F83\u57FA\u51C6\u5B8C\u6210\u65F6\u95F4", //   "Baseline Finish",
      "\u5B9E\u9645\u5F00\u59CB\u65F6\u95F4", //   "Actual Start",
      "\u5B9E\u9645\u5B8C\u6210\u65F6\u95F4", //   "Actual Finish",
      "\u5F00\u59CB\u65F6\u95F41", //   "Start1",
      "\u5B8C\u6210\u65F6\u95F41", //   "Finish1",
      "\u5F00\u59CB\u65F6\u95F42", //   "Start2",
      "\u5B8C\u6210\u65F6\u95F42", //   "Finish2",
      "\u5F00\u59CB\u65F6\u95F43", //   "Start3",
      "\u5B8C\u6210\u65F6\u95F43", //   "Finish3",
      "\u5F00\u65F6\u95F4\u5DEE\u5F02", //   "Start Variance",
      "\u5B8C\u6210\u65F6\u95F4\u5DEE\u5F02", //   "Finish Variance",
      "\u9650\u5236\u65E5\u671F", //   "Constraint Date",
      null, //
      "\u524D\u7F6E\u4EFB\u52A1", //   "Predecessors",
      "\u540E\u7EED\u4EFB\u52A1", //   "Successors",
      "\u8D44\u6E90\u540D\u79F0", //   "Resource Names",
      "\u8D44\u6E90\u7F29\u5199", //   "Resource Initials",
      "\u552F\u4E00\u6807\u8BC6\u53F7\u524D\u7F6E\u4EFB\u52A1", //   "Unique ID Predecessors",
      "\u552F\u4E00\u6807\u8BC6\u53F7\u540E\u7EED\u4EFB\u52A1", //   "Unique ID Successors",
      null, //
      null, //
      null, //
      null, //
      "\u56FA\u5B9A", //No such field named "Fixed" in project 2003
      "\u91CC\u7A0B\u7891", //   "Milestone",
      "\u5173\u952E", //   "Critical",
      "\u5DF2\u6807\u8BB0", //   "Marked",
      "\u603B\u6210\u578B\u4EFB\u52A1", //   "Rollup",
      "BCWS", //   "BCWS",
      "BCWP", //   "BCWP",
      "SV", //   "SV",
      "CV", //   "CV",
      null, //
      "\u6807\u8BC6\u53F7", //   "ID",
      "\u9650\u5236\u7C7B\u578B", //   "Constraint Type",
      "\u5EF6\u8FDF", //No such field named   "Delay" in project 2003
      "\u53EF\u7528\u65F6\u5DEE", //   "Free Slack",
      "\u603B\u65F6\u5DEE", //   "Total Slack",
      "\u4F18\u5148\u7EA7'", //   "Priority",
      "\u5B50\u9879\u76EE\u6587\u4EF6", //   "Subproject File",
      "\u9879\u76EE", //   "Project",
      "\u552F\u4E00\u6807\u8BC6\u53F7", //   "Unique ID",
      "\u5927\u7EB2\u6570\u5B57", //   "Outline Number",
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
      "\u6807\u5FD71", //   "Flag1",
      "\u6807\u5FD72", //   "Flag2",
      "\u6807\u5FD73", //   "Flag3",
      "\u6807\u5FD74", //   "Flag4",
      "\u6807\u5FD75", //   "Flag5",
      "\u6807\u5FD76", //   "Flag6",
      "\u6807\u5FD77", //   "Flag7",
      "\u6807\u5FD78", //   "Flag8",
      "\u6807\u5FD79", //   "Flag9",
      "\u6807\u5FD710", //   "Flag10",
      "\u6458\u8981", //   "Summary",
      "\u5BF9\u8C61\u6570\u76EE", //   "Objects",
      "\u94FE\u63A5\u57DF", //   "Linked Fields",
      "\u9690\u85CF\u6761\u5F62\u56FE", //   "Hide Bar",
      null, //
      "\u521B\u5EFA\u65E5\u671F", //   "Created",
      "\u5F00\u59CB\u65F6\u95F44", //   "Start4",
      "\u5B8C\u6210\u65F6\u95F44", //   "Finish4",
      "\u5F00\u59CB\u65F6\u95F45", //   "Start5",
      "\u5B8C\u6210\u65F6\u95F45", //   "Finish5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "\u5DF2\u786E\u8BA4", //   "Confirmed",
      "\u9700\u8981\u66F4\u65B0", //   "Update Needed",
      null, //
      null, //
      null, //
      "\u6570\u5B571", //   "Number1",
      "\u6570\u5B572", //   "Number2",
      "\u6570\u5B573", //   "Number3",
      "\u6570\u5B574", //   "Number4",
      "\u6570\u5B575", //   "Number5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "\u505C\u6B62", //   "Stop",
      "\u4E0D\u65E9\u4E8E...\u91CD\u65B0\u5F00\u59CB", //   "Resume No Earlier Than",
      "\u91CD\u65B0\u5F00\u59CB" //   "Resume"
   };

   private static final String[] RESOURCE_NAMES_DATA =
   {
      null, //
      "\u540D\u79F0", //   "Name",
      "\u7F29\u5199", //   "Initials",
      "\u7EC4", //   "Group",
      "\u4EE3\u7801", //   "Code",
      "\u6587\u672C1", //   "Text1",
      "\u6587\u672C2", //   "Text2",
      "\u6587\u672C3", //   "Text3",
      "\u6587\u672C4", //   "Text4",
      "\u6587\u672C5", //   "Text5",
      "\u5907\u6CE8", //   "Notes",
      "\u7535\u5B50\u90AE\u4EF6\u5730\u5740", //   "Email Address",
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      "\u5DE5\u65F6", //   "Work",
      "\u6BD4\u8F83\u57FA\u51C6\u5DE5\u65F6", //   "Baseline Work",
      "\u5269\u4F59\u5DE5\u65F6", //   "Actual Work",
      "\u52A0\u73ED\u5DE5\u65F6", //   "Remaining Work",
      "\u5DE5\u65F6\u5B8C\u6210\u767E\u5206\u6BD4", //   "Overtime Work",
      "\u5DE5\u65F6\u5DEE\u5F02", //   "Work Variance",
      "\u5DE5\u65F6\u5B8C\u6210\u767E\u5206\u6BD4", //   "% Work Complete",
      null, //
      null, //
      null, //
      "\u6210\u672C", //   "Cost",
      "\u6BD4\u8F83\u57FA\u51C6\u6210\u672C", //   "Baseline Cost",
      "\u5B9E\u9645\u6210\u672C", //   "Actual Cost",
      "\u5269\u4F59\u6210\u672C", //   "Remaining Cost",
      "\u6210\u672C\u5DEE\u5F02", //   "Cost Variance",
      null, //
      null, //
      null, //
      null, //
      null, //
      "\u6807\u8BC6\u53F7", //   "ID",
      "\u6700\u5927\u5355\u4F4D", //   "Max Units",
      "\u6807\u51C6\u8D39\u7387", //   "Standard Rate",
      "\u52A0\u73ED\u8D39\u7387", //   "Overtime Rate",
      "\u6BCF\u6B21\u4F7F\u7528\u6210\u672C", //   "Cost Per Use",
      "\u6210\u672C\u7D2F\u7B97", //   "Accrue At",
      "\u8FC7\u5EA6\u5206\u914D", //   "Overallocated",
      "\u6700\u5927\u4F7F\u7528\u91CF", //   "Peak",
      "\u57FA\u51C6\u65E5\u5386", //   "Base Calendar",
      "\u552F\u4E00\u6807\u8BC6\u53F7", //   "Unique ID",
      "\u5BF9\u8C61\u6570\u76EE", //   "Objects",
      "\u94FE\u63A5\u57DF", //   "Linked Fields",
   };

   private static final Object[][] RESOURCE_DATA =
   {
      {
         LocaleData.FILE_DELIMITER,
         ";"
      },
      {
         LocaleData.PROGRAM_NAME,
         "Microsoft Project for Windows"
      },
      {
         LocaleData.FILE_VERSION,
         "4.0"
      },
      {
         LocaleData.CODE_PAGE,
         CodePage.ZH
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
         "NA"
      },

      {
         LocaleData.YES,
         "\u662F"
      },
      {
         LocaleData.NO,
         "\u5426"
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
