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

package net.sf.mpxj.mpx;

import java.util.HashMap;
import java.util.ListResourceBundle;

import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.DateOrder;
import net.sf.mpxj.ProjectDateFormat;
import net.sf.mpxj.ProjectTimeFormat;

/**
 * This class defines the Russian translation of resource required by MPX files.
 */
public final class LocaleData_ru extends ListResourceBundle
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
         "минута"
      },
      {
         "час"
      },
      {
         "день"
      },
      {
         "неделя"
      },
      {
         "месяц"
      },
      {
         "год"
      },
      {
         "%"
      },
      {
         "каждую минуту"
      },
      {
         "каждый час"
      },
      {
         "каждый день"
      },
      {
         "каждую неделю"
      },
      {
         "каждый месяц"
      },
      {
         "каждый год"
      },
      {
         "каждый %"
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
      "Начало", //   "Start",
      "Конец", //   "End",
      "Повседневный" //   "Prorated"
   };

   private static final String[] RELATION_TYPES_DATA =
   {
      "КК", //   "FF",
      "КН", //   "FS",
      "НК", //   "SF",
      "НН" //   "SS"
   };

   private static final String[] PRIORITY_TYPES_DATA =
   {
      "Самый низкий", //   "Lowest",
      "Очень низкий", //   "Very Low",
      "Низкий", //   "Lower",
      "Ниже среднего", //   "Low",
      "Средний", //   "Medium",
      "Выше среднего", //   "High",
      "Высокий", //   "Higher",
      "Очень высокий", //   "Very High",
      "Наивысший", //   "Highest",
      "Без приоритета" //   "Do Not Level"
   };

   private static final String[] CONSTRAINT_TYPES_DATA =
   {
      "Как можно раньше", //   "As Soon As Possible",
      "Как можно позже", //   "As Late As Possible",
      "Должен начаться", //   "Must Start On",
      "Должен закончиться", //   "Must Finish On",
      "Начаться не раньше", //   "Start No Earlier Than",
      "Начаться не позже", //   "Start No Later Than",
      "Закончиться не раньше", //   "Finish No Earlier Than",
      "Закончиться не позже" //   "Finish No Later Than"
   };

   private static final String[] TASK_NAMES_DATA =
   {
      null, //
      "Название", //   "Name",
      "WBS", //   "WBS",
      "Внешний уровень", //   "Outline Level",
      "Текст1", //   "Text1",
      "Текст2", //   "Text2",
      "Текст3", //   "Text3",
      "Текст4", //   "Text4",
      "Текст5", //   "Text5",
      "Текст6", //   "Text6",
      "Текст7", //   "Text7",
      "Текст8", //   "Text8",
      "Текст9", //   "Text9",
      "Текст10", //   "Text10",
      "Примечание", //   "Notes",
      "Контакт", //   "Contact",
      "Группа ресурсов", //   "Resource Group",
      null, //
      null, //
      null, //
      "Работа", //   "Work",
      "Запланировано работ", //   "Baseline Work",
      "Выполнено работ", //   "Actual Work",
      "Осталось работ", //   "Remaining Work",
      "Разбежность работ", //   "Work Variance",
      "% работ завершено", //   "% Work Complete",
      null, //
      null, //
      null, //
      null, //
      "Стоимость", //   "Cost",
      "Запалнированная стоимость", //   "Baseline Cost",
      "Реальная стоимость", //   "Actual Cost",
      "Ожидаемая стоимость", //   "Remaining Cost",
      "Разбежнность стоимости", //   "Cost Variance",
      "Фиксированная стоимость", //   "Fixed Cost",
      "Стоимость1", //   "Cost1",
      "Стоимость2", //   "Cost2",
      "Стоимость3", //   "Cost3",
      null, //
      "Длительность", //   "Duration",
      "Запалнированная длительность", //   "Baseline Duration",
      "Реальная длительность", //   "Actual Duration",
      "Ожидаемая длительность", //   "Remaining Duration",
      "% завершено", //   "% Complete",
      "Разбежнность длительности", //   "Duration Variance",
      "Длительность1", //   "Duration1",
      "Длительность2", //   "Duration2",
      "Длительность3", //   "Duration3",
      null, //
      "Начало", //   "Start",
      "Окончание", //   "Finish",
      "Предыдущее начало", //   "Early Start",
      "Предыдущее окончание", //   "Early Finish",
      "Следующее начало", //   "Late Start",
      "Следующее окончание", //   "Late Finish",
      "Запалнироанное начало", //   "Baseline Start",
      "Запланированное окончание", //   "Baseline Finish",
      "Реальное начало", //   "Actual Start",
      "Реальное окончание", //   "Actual Finish",
      "Начало1", //   "Start1",
      "Окончание", //   "Finish1",
      "Начало2", //   "Start2",
      "Окончание2", //   "Finish2",
      "Начало3", //   "Start3",
      "Окончание3", //   "Finish3",
      "Смешение начала", //   "Start Variance",
      "Смещение окончания", //   "Finish Variance",
      "Принудительная дата", //   "Constraint Date",
      null, //
      "Предшественники", //   "Predecessors",
      "Наследники", //   "Successors",
      "Имя ресурса", //   "Resource Names",
      "Инициалы ресурса", //   "Resource Initials",
      "Уникальный ID предшественника",//   "Unique ID Predecessors",
      "Уникальный ID наследника", //   "Unique ID Successors",
      null, //
      null, //
      null, //
      null, //
      "Фиксированный", //   "Fixed",
      "Веха", //   "Milestone",
      "Критическая", //   "Critical",
      "Помеченная", //   "Marked",
      "Повышенная", //   "Rollup",
      "Стоимость запланированных работ", //   "BCWS",
      "Стоимость выполненных работ", //   "BCWP",
      "Стоимость остаточных работ", //   "SV",
      "Разбежность стоимостей", //   "CV",
      null, //
      "ID", //   "ID",
      "Принудительный Тип", //   "Constraint Type",
      "Задержка", //   "Delay",
      "Свободно", //   "Free Slack",
      "Всего свободно", //   "Total Slack",
      "Приоритет", //   "Priority",
      "Файл подпроекта", //   "Subproject File",
      "Проект", //   "Project",
      "Уникальный ID", //   "Unique ID",
      "Внешний Номер", //   "Outline Number",
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
      "Флаг1", //   "Flag1",
      "Флаг2", //   "Flag2",
      "Флаг3", //   "Flag3",
      "Флаг4", //   "Flag4",
      "Флаг5", //   "Flag5",
      "Флаг6", //   "Flag6",
      "Флаг7", //   "Flag7",
      "Флаг8", //   "Flag8",
      "Флаг9", //   "Flag9",
      "Флаг10", //   "Flag10",
      "Всего", //   "Summary",
      "Объекты", //   "Objects",
      "Отмечено полей", //   "Linked Fields",
      "Скрытое поле ", //   "Hide Bar",
      null, //
      "Создано", //   "Created",
      "Начало4", //   "Start4",
      "Окончание4", //   "Finish4",
      "Начало5", //   "Start5",
      "Окончание5", //   "Finish5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "Утвержденный", //   "Confirmed",
      "Нуждается в обновлении", //   "Update Needed",
      null, //
      null, //
      null, //
      "Номер1", //   "Number1",
      "Номер2", //   "Number2",
      "Номер3", //   "Number3",
      "Номер4", //   "Number4",
      "Номер5", //   "Number5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "Стоп", //   "Stop",
      "Начать не раньше чем", //   "Resume No Earlier Than",
      "Начать" //   "Resume"
   };

   private static final String[] RESOURCE_NAMES_DATA =
   {
      null, //
      "Имя", //   "Name",
      "Инициалы", //   "Initials",
      "Группа", //   "Group",
      "Код", //   "Code",
      "Текст1", //   "Text1",
      "Текст2", //   "Text2",
      "Текст3", //   "Text3",
      "Текст4", //   "Text4",
      "Текст5", //   "Text5",
      "Примечания", //   "Notes",
      "Email", //   "Email Address",
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      "Работа", //   "Work",
      "Запланировано работ", //   "Baseline Work",
      "Выполнено работ", //   "Actual Work",
      "Осталось работ", //   "Remaining Work",
      "Разбежность работ", //   "Work Variance",
      "% работ завершено", //   "% Work Complete",
      null, //
      null, //
      null, //
      "Стоимость", //   "Cost",
      "Запалнированная стоимость", //   "Baseline Cost",
      "Реальная стоимость", //   "Actual Cost",
      "Ожидаемая стоимость", //   "Remaining Cost",
      "Разбежнность стоимости", //   "Cost Variance",
      null, //
      null, //
      null, //
      null, //
      null, //
      "ID", //   "ID",
      "Максимальное значение", //   "Max Units",
      "Стандартное значение", //   "Standard Rate",
      "Просроченное значение", //   "Overtime Rate",
      "Стоимость использования", //   "Cost Per Use",
      "Произошло", //   "Accrue At",
      "Переназначено", //   "Overallocated",
      "Пик", //   "Peak",
      "Основной календарь", //   "Base Calendar",
      "Уникальный ID", //   "Unique ID",
      "Объект", //   "Objects",
      "Связанные поля", //   "Linked Fields",
   };

   private static final Object[][] RESOURCE_DATA =
   {
      {
         LocaleData.FILE_DELIMITER,
         ";"
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
         "недоступно"
      },

      {
         LocaleData.YES,
         "Да"
      },
      {
         LocaleData.NO,
         "Нет"
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
