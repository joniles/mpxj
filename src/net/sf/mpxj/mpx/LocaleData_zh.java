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

package net.sf.mpxj.mpx;

import java.util.HashMap;
import java.util.ListResourceBundle;

import net.sf.mpxj.CodePage;
import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.DateOrder;
import net.sf.mpxj.ProjectDateFormat;
import net.sf.mpxj.ProjectTimeFormat;

/**
 * This class defines the Chinese translation of resource required by MPX files.
 */
public final class LocaleData_zh extends ListResourceBundle
{
   /**
    * {@inheritDoc}
    */
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
   private static final HashMap<String, Integer> TIME_UNITS_MAP_DATA = new HashMap<String, Integer>();

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
      "开始", // "Start",
      "结束", // "End",
      "按比例" // "Prorated"
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
      "越早越好", //   "As Soon As Possible",
      "越晚越好", //   "As Late As Possible",
      "必须开始于", //   "Must Start On",
      "必须完成于", //   "Must Finish On",
      "不得早于...开始", //   "Start No Earlier Than",
      "不得晚于...开始", //   "Start No Later Than",
      "不得早于...完成", //   "Finish No Earlier Than",
      "不得晚于...完成" //   "Finish No Later Than"
   };

   private static final String[] TASK_NAMES_DATA =
   {
      null, //
      "名称", //   "Name",
      "WBS", //   "WBS",
      "大纲级别", //   "Outline Level",
      "文本1", //   "Text1",
      "文本2", //   "Text2",
      "文本3", //   "Text3",
      "文本4", //   "Text4",
      "文本5", //   "Text5",
      "文本6", //   "Text6",
      "文本7", //   "Text7",
      "文本8", //   "Text8",
      "文本9", //   "Text9",
      "文本10", //   "Text10",
      "备注", //  "Notes",
      "联系人", //  "Contact",
      "资源组", //   "Resource Group",
      null, //
      null, //
      null, //
      "工时", //   "Work",
      "比较基准工时", //   "Baseline Work",
      "实际工时", //   "Actual Work",
      "剩余工时", //   "Remaining Work",
      "工时差异", //   "Work Variance",
      "工时完成百分比", //   "% Work Complete",
      null, //
      null, //
      null, //
      null, //
      "成本", //   "Cost",
      "比较基准成本", //   "Baseline Cost",
      "实际成本", //   "Actual Cost",
      "剩余成本", //   "Remaining Cost",
      "成本差异", //   "Cost Variance",
      "固定成本", //   "Fixed Cost",
      "成本1", //   "Cost1",
      "成本2", //   "Cost2",
      "成本3", //   "Cost3",
      null, //
      "工期", //   "Duration",
      "比较基准工期", //   "Baseline Duration",
      "实际工期", //   "Actual Duration",
      "剩余工期", //   "Remaining Duration",
      "完成百分比", //   "% Complete",
      "工期差异", //   "Duration Variance",
      "工期1", //   "Duration1",
      "工期2", //   "Duration2",
      "工期3", //   "Duration3",
      null, //
      "开始时间", //   "Start",
      "完成时间", //   "Finish",
      "最早开始时间", //   "Early Start",
      "最早完成时间", //   "Early Finish",
      "最晚开始时间", //   "Late Start",
      "最晚完成时间", //   "Late Finish",
      "比较基准开始时间", //   "Baseline Start",
      "比较基准完成时间", //   "Baseline Finish",
      "实际开始时间", //   "Actual Start",
      "实际完成时间", //   "Actual Finish",
      "开始时间1", //   "Start1",
      "完成时间1", //   "Finish1",
      "开始时间2", //   "Start2",
      "完成时间2", //   "Finish2",
      "开始时间3", //   "Start3",
      "完成时间3", //   "Finish3",
      "开时间差异", //   "Start Variance",
      "完成时间差异", //   "Finish Variance",
      "限制日期", //   "Constraint Date",
      null, //
      "前置任务", //   "Predecessors",
      "后续任务", //   "Successors",
      "资源名称", //   "Resource Names",
      "资源缩写", //   "Resource Initials",
      "唯一标识号前置任务", //   "Unique ID Predecessors",
      "唯一标识号后续任务", //   "Unique ID Successors",
      null, //
      null, //
      null, //
      null, //
      "固定", //No such field named "Fixed" in project 2003
      "里程碑", //   "Milestone",
      "关键", //   "Critical",
      "已标记", //   "Marked",
      "总成型任务", //   "Rollup",
      "BCWS", //   "BCWS",
      "BCWP", //   "BCWP",
      "SV", //   "SV",
      "CV", //   "CV",
      null, //
      "标识号", //   "ID",
      "限制类型", //   "Constraint Type",
      "延迟", //No such field named   "Delay" in project 2003
      "可用时差", //   "Free Slack",
      "总时差", //   "Total Slack",
      "优先级'", //   "Priority",
      "子项目文件", //   "Subproject File",
      "项目", //   "Project",
      "唯一标识号", //   "Unique ID",
      "大纲数字", //   "Outline Number",
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
      "标志1", //   "Flag1",
      "标志2", //   "Flag2",
      "标志3", //   "Flag3",
      "标志4", //   "Flag4",
      "标志5", //   "Flag5",
      "标志6", //   "Flag6",
      "标志7", //   "Flag7",
      "标志8", //   "Flag8",
      "标志9", //   "Flag9",
      "标志10", //   "Flag10",
      "摘要", //   "Summary",
      "对象数目", //   "Objects",
      "链接域", //   "Linked Fields",
      "隐藏条形图", //   "Hide Bar",
      null, //
      "创建日期", //   "Created",
      "开始时间4", //   "Start4",
      "完成时间4", //   "Finish4",
      "开始时间5", //   "Start5",
      "完成时间5", //   "Finish5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "已确认", //   "Confirmed",
      "需要更新", //   "Update Needed",
      null, //
      null, //
      null, //
      "数字1", //   "Number1",
      "数字2", //   "Number2",
      "数字3", //   "Number3",
      "数字4", //   "Number4",
      "数字5", //   "Number5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "停止", //   "Stop",
      "不早于...重新开始", //   "Resume No Earlier Than",
      "重新开始" //   "Resume"
   };

   private static final String[] RESOURCE_NAMES_DATA =
   {
      null, //
      "名称", //   "Name",
      "缩写", //   "Initials",
      "组", //   "Group",
      "代码", //   "Code",
      "文本1", //   "Text1",
      "文本2", //   "Text2",
      "文本3", //   "Text3",
      "文本4", //   "Text4",
      "文本5", //   "Text5",
      "备注", //   "Notes",
      "电子邮件地址", //   "Email Address",
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      "工时", //   "Work",
      "比较基准工时", //   "Baseline Work",
      "剩余工时", //   "Actual Work",
      "加班工时", //   "Remaining Work",
      "工时完成百分比", //   "Overtime Work",
      "工时差异", //   "Work Variance",
      "工时完成百分比", //   "% Work Complete",
      null, //
      null, //
      null, //
      "成本", //   "Cost",
      "比较基准成本", //   "Baseline Cost",
      "实际成本", //   "Actual Cost",
      "剩余成本", //   "Remaining Cost",
      "成本差异", //   "Cost Variance",
      null, //
      null, //
      null, //
      null, //
      null, //
      "标识号", //   "ID",
      "最大单位", //   "Max Units",
      "标准费率", //   "Standard Rate",
      "加班费率", //   "Overtime Rate",
      "每次使用成本", //   "Cost Per Use",
      "成本累算", //   "Accrue At",
      "过度分配", //   "Overallocated",
      "最大使用量", //   "Peak",
      "基准日历", //   "Base Calendar",
      "唯一标识号", //   "Unique ID",
      "对象数目", //   "Objects",
      "链接域", //   "Linked Fields",
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
         "是"
      },
      {
         LocaleData.NO,
         "否"
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
