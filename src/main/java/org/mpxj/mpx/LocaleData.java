/*
 * file:       LocaleData.java
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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.mpxj.CodePage;
import org.mpxj.CurrencySymbolPosition;
import org.mpxj.DateOrder;
import org.mpxj.ProjectDateFormat;
import org.mpxj.ProjectTimeFormat;
import org.mpxj.TimeUnit;

/**
 * This class defines utility routines for handling resources, and also
 * the default set of English resources used in MPX files.
 */
public final class LocaleData extends ListResourceBundle
{
   @Override public Object[][] getContents()
   {
      return (RESOURCE_DATA);
   }

   /**
    * Convenience method for retrieving a String resource.
    *
    * @param locale locale identifier
    * @param key resource key
    * @return resource value
    */
   public static final String getString(Locale locale, String key)
   {
      ResourceBundle bundle = ResourceBundle.getBundle(LocaleData.class.getName(), locale);
      return (bundle.getString(key));
   }

   /**
    * Convenience method for retrieving a String[] resource.
    *
    * @param locale locale identifier
    * @param key resource key
    * @return resource value
    */
   public static final String[] getStringArray(Locale locale, String key)
   {
      ResourceBundle bundle = ResourceBundle.getBundle(LocaleData.class.getName(), locale);
      return (bundle.getStringArray(key));
   }

   /**
    * Convenience method for retrieving a String[][] resource.
    *
    * @param locale locale identifier
    * @param key resource key
    * @return resource value
    */
   public static final String[][] getStringArrays(Locale locale, String key)
   {
      ResourceBundle bundle = ResourceBundle.getBundle(LocaleData.class.getName(), locale);
      return ((String[][]) bundle.getObject(key));
   }

   /**
    * Convenience method for retrieving an Object resource.
    *
    * @param locale locale identifier
    * @param key resource key
    * @return resource value
    */
   public static final Object getObject(Locale locale, String key)
   {
      ResourceBundle bundle = ResourceBundle.getBundle(LocaleData.class.getName(), locale);
      return (bundle.getObject(key));
   }

   /**
    * Convenience method for retrieving a Map resource.
    *
    * @param locale locale identifier
    * @param key resource key
    * @return resource value
    */
   @SuppressWarnings("rawtypes") public static final Map getMap(Locale locale, String key)
   {
      ResourceBundle bundle = ResourceBundle.getBundle(LocaleData.class.getName(), locale);
      return ((Map) bundle.getObject(key));
   }

   /**
    * Convenience method for retrieving an Integer resource.
    *
    * @param locale locale identifier
    * @param key resource key
    * @return resource value
    */
   public static final Integer getInteger(Locale locale, String key)
   {
      ResourceBundle bundle = ResourceBundle.getBundle(LocaleData.class.getName(), locale);
      return ((Integer) bundle.getObject(key));
   }

   /**
    * Convenience method for retrieving a char resource.
    *
    * @param locale locale identifier
    * @param key resource key
    * @return resource value
    */
   public static final char getChar(Locale locale, String key)
   {
      ResourceBundle bundle = ResourceBundle.getBundle(LocaleData.class.getName(), locale);
      return (bundle.getString(key).charAt(0));
   }

   public static final String FILE_DELIMITER = "FILE_DELIMITER";
   public static final String PROGRAM_NAME = "PROGRAM_NAME";
   public static final String FILE_VERSION = "FILE_VERSION";
   public static final String CODE_PAGE = "CODE_PAGE";

   public static final String YES = "YES";
   public static final String NO = "NO";

   public static final String CURRENCY_SYMBOL = "CURRENCY_SYMBOL";
   public static final String CURRENCY_SYMBOL_POSITION = "CURRENCY_SYMBOL_POSITION";
   public static final String CURRENCY_DIGITS = "CURRENCY_DIGITS";
   public static final String CURRENCY_THOUSANDS_SEPARATOR = "CURRENCY_THOUSANDS_SEPARATOR";
   public static final String CURRENCY_DECIMAL_SEPARATOR = "CURRENCY_DECIMAL_SEPARATOR";

   public static final String DATE_ORDER = "DATE_ORDER";
   public static final String TIME_FORMAT = "TIME_FORMAT";
   public static final String DEFAULT_START_TIME = "DEFAULT_START_TIME";
   public static final String DATE_SEPARATOR = "DATE_SEPARATOR";
   public static final String TIME_SEPARATOR = "TIME_SEPARATOR";
   public static final String AM_TEXT = "AM_TEXT";
   public static final String PM_TEXT = "PM_TEXT";
   public static final String DATE_FORMAT = "DATE_FORMAT";
   public static final String BAR_TEXT_DATE_FORMAT = "BAR_TEXT_DATE_FORMAT";
   public static final String NA = "NA";

   public static final String TIME_UNITS_ARRAY = "TIME_UNITS_ARRAY";
   public static final String TIME_UNITS_MAP = "TIME_UNITS_MAP";

   public static final String ACCRUE_TYPES = "ACCRUE_TYPES";
   public static final String RELATION_TYPES = "RELATION_TYPES";
   public static final String PRIORITY_TYPES = "PRIORITY_TYPES";
   public static final String CONSTRAINT_TYPES = "CONSTRAINT_TYPES";

   public static final String TASK_NAMES = "TASK_NAMES";
   public static final String RESOURCE_NAMES = "RESOURCE_NAMES";

   private static final String[][] TIME_UNITS_ARRAY_DATA = new String[TimeUnit.values().length][];
   static
   {
      TIME_UNITS_ARRAY_DATA[TimeUnit.MINUTES.getValue()] = new String[]
      {
         "m",
         "mins"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.HOURS.getValue()] = new String[]
      {
         "h",
         "hours"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.DAYS.getValue()] = new String[]
      {
         "d",
         "days"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.WEEKS.getValue()] = new String[]
      {
         "w",
         "wk",
         "weeks"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.MONTHS.getValue()] = new String[]
      {
         "mon",
         "months"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.YEARS.getValue()] = new String[]
      {
         "y",
         "years"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.PERCENT.getValue()] = new String[]
      {
         "%"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.ELAPSED_MINUTES.getValue()] = new String[]
      {
         "em"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.ELAPSED_HOURS.getValue()] = new String[]
      {
         "eh"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.ELAPSED_DAYS.getValue()] = new String[]
      {
         "ed"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.ELAPSED_WEEKS.getValue()] = new String[]
      {
         "ew"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.ELAPSED_MONTHS.getValue()] = new String[]
      {
         "emon"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.ELAPSED_YEARS.getValue()] = new String[]
      {
         "ey"
      };

      TIME_UNITS_ARRAY_DATA[TimeUnit.ELAPSED_PERCENT.getValue()] = new String[]
      {
         "e%"
      };
   }

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
      "Start",
      "End",
      "Prorated"
   };

   private static final String[] RELATION_TYPES_DATA =
   {
      "FF",
      "FS",
      "SF",
      "SS"
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
      "As Soon As Possible",
      "As Late As Possible",
      "Must Start On",
      "Must Finish On",
      "Start No Earlier Than",
      "Start No Later Than",
      "Finish No Earlier Than",
      "Finish No Later Than"
   };

   private static final String[] TASK_NAMES_DATA =
   {
      null,
      "Name",
      "WBS",
      "Outline Level",
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
      "Notes",
      "Contact",
      "Resource Group",
      null,
      null,
      null,
      "Work",
      "Baseline Work",
      "Actual Work",
      "Remaining Work",
      "Work Variance",
      "% Work Complete",
      null,
      null,
      null,
      null,
      "Cost",
      "Baseline Cost",
      "Actual Cost",
      "Remaining Cost",
      "Cost Variance",
      "Fixed Cost",
      "Cost1",
      "Cost2",
      "Cost3",
      null,
      "Duration",
      "Baseline Duration",
      "Actual Duration",
      "Remaining Duration",
      "% Complete",
      "Duration Variance",
      "Duration1",
      "Duration2",
      "Duration3",
      null,
      "Start",
      "Finish",
      "Early Start",
      "Early Finish",
      "Late Start",
      "Late Finish",
      "Baseline Start",
      "Baseline Finish",
      "Actual Start",
      "Actual Finish",
      "Start1",
      "Finish1",
      "Start2",
      "Finish2",
      "Start3",
      "Finish3",
      "Start Variance",
      "Finish Variance",
      "Constraint Date",
      null,
      "Predecessors",
      "Successors",
      "Resource Names",
      "Resource Initials",
      "Unique ID Predecessors",
      "Unique ID Successors",
      null,
      null,
      null,
      null,
      "Fixed",
      "Milestone",
      "Critical",
      "Marked",
      "Rollup",
      "BCWS",
      "BCWP",
      "SV",
      "CV",
      null,
      "ID",
      "Constraint Type",
      "Delay",
      "Free Slack",
      "Total Slack",
      "Priority",
      "Subproject File",
      "Project",
      "Unique ID",
      "Outline Number",
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
      "Flag1",
      "Flag2",
      "Flag3",
      "Flag4",
      "Flag5",
      "Flag6",
      "Flag7",
      "Flag8",
      "Flag9",
      "Flag10",
      "Summary",
      "Objects",
      "Linked Fields",
      "Hide Bar",
      null,
      "Created",
      "Start4",
      "Finish4",
      "Start5",
      "Finish5",
      null,
      null,
      null,
      null,
      null,
      "Confirmed",
      "Update Needed",
      null,
      null,
      null,
      "Number1",
      "Number2",
      "Number3",
      "Number4",
      "Number5",
      null,
      null,
      null,
      null,
      null,
      "Stop",
      "Resume No Earlier Than",
      "Resume"
   };

   private static final String[] RESOURCE_NAMES_DATA =
   {
      null,
      "Name",
      "Initials",
      "Group",
      "Code",
      "Text1",
      "Text2",
      "Text3",
      "Text4",
      "Text5",
      "Notes",
      "Email Address",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "Work",
      "Baseline Work",
      "Actual Work",
      "Remaining Work",
      "Overtime Work",
      "Work Variance",
      "% Work Complete",
      null,
      null,
      null,
      "Cost",
      "Baseline Cost",
      "Actual Cost",
      "Remaining Cost",
      "Cost Variance",
      null,
      null,
      null,
      null,
      null,
      "ID",
      "Max Units",
      "Standard Rate",
      "Overtime Rate",
      "Cost Per Use",
      "Accrue At",
      "Overallocated",
      "Peak",
      "Base Calendar",
      "Unique ID",
      "Objects",
      "Linked Fields",
   };

   private static final Object[][] RESOURCE_DATA =
   {
      {
         FILE_DELIMITER,
         ","
      },
      {
         PROGRAM_NAME,
         "Microsoft Project for Windows"
      },
      {
         FILE_VERSION,
         "4.0"
      },
      {
         CODE_PAGE,
         CodePage.ANSI
      },

      {
         CURRENCY_SYMBOL,
         "$"
      },
      {
         CURRENCY_SYMBOL_POSITION,
         CurrencySymbolPosition.BEFORE
      },
      {
         CURRENCY_DIGITS,
         Integer.valueOf(2)
      },
      {
         CURRENCY_THOUSANDS_SEPARATOR,
         ","
      },
      {
         CURRENCY_DECIMAL_SEPARATOR,
         "."
      },

      {
         DATE_ORDER,
         DateOrder.DMY
      },
      {
         TIME_FORMAT,
         ProjectTimeFormat.TWELVE_HOUR
      },
      {
         DEFAULT_START_TIME,
         Integer.valueOf(480)
      },
      {
         DATE_SEPARATOR,
         "/"
      },
      {
         TIME_SEPARATOR,
         ":"
      },
      {
         AM_TEXT,
         "am"
      },
      {
         PM_TEXT,
         "pm"
      },
      {
         DATE_FORMAT,
         ProjectDateFormat.DD_MM_YYYY
      },
      {
         BAR_TEXT_DATE_FORMAT,
         Integer.valueOf(0)
      },
      {
         NA,
         "NA"
      },

      {
         YES,
         "Yes"
      },
      {
         NO,
         "No"
      },

      {
         TIME_UNITS_ARRAY,
         TIME_UNITS_ARRAY_DATA
      },
      {
         TIME_UNITS_MAP,
         TIME_UNITS_MAP_DATA
      },

      {
         ACCRUE_TYPES,
         ACCRUE_TYPES_DATA
      },
      {
         RELATION_TYPES,
         RELATION_TYPES_DATA
      },
      {
         PRIORITY_TYPES,
         PRIORITY_TYPES_DATA
      },
      {
         CONSTRAINT_TYPES,
         CONSTRAINT_TYPES_DATA
      },

      {
         TASK_NAMES,
         TASK_NAMES_DATA
      },
      {
         RESOURCE_NAMES,
         RESOURCE_NAMES_DATA
      }
   };
}
