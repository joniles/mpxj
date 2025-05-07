/*
 * file:       DatabaseReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       01/03/2018
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

package org.mpxj.primavera.suretrak;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mpxj.primavera.common.ByteColumn;
import org.mpxj.primavera.common.ColumnDefinition;
import org.mpxj.primavera.common.IntColumn;
import org.mpxj.primavera.common.ShortColumn;
import org.mpxj.primavera.common.StringColumn;
import org.mpxj.primavera.common.Table;
import org.mpxj.primavera.common.TableDefinition;

/**
 * Reads a directory containing a SureTrak database and returns a map
 * of table names and the data they contain.
 */
class DatabaseReader
{
   /**
    * Main entry point. Reads a directory containing a SureTrak database files
    * and returns a map of table names and table content.
    *
    * @param directory directory containing the database
    * @param prefix file name prefix used to identify files from the same database
    * @return Map of table names to table data
    */
   public Map<String, Table> process(File directory, String prefix) throws IOException
   {
      String filePrefix = prefix.toUpperCase();
      Map<String, Table> tables = new HashMap<>();
      File[] files = directory.listFiles();
      if (files != null)
      {
         for (File file : files)
         {
            String name = file.getName().toUpperCase();
            if (!name.startsWith(filePrefix))
            {
               continue;
            }

            int typeIndex = name.lastIndexOf('.');
            String type = name.substring(typeIndex + 1);
            TableDefinition definition = TABLE_DEFINITIONS.get(type);
            if (definition != null)
            {
               Table table = new Table();
               TableReader reader = new TableReader(definition);
               reader.read(file, table);
               tables.put(type, table);
               //dumpCSV(type, definition, table);
            }
         }
      }
      return tables;
   }

   //   private void dumpCSV(String type, TableDefinition definition, Table table) throws IOException
   //   {
   //      PrintWriter pw = new PrintWriter(new File("c:/temp/" + type + ".csv"));
   //      pw.print("ROW_NUMBER,ROW_VERSION,");
   //
   //      for (ColumnDefinition column : definition.getColumns())
   //      {
   //         pw.print(column.getName());
   //         pw.print(',');
   //      }
   //      pw.println();
   //
   //      for (MapRow row : table)
   //      {
   //         pw.print(row.getObject("ROW_NUMBER"));
   //         pw.print(',');
   //         pw.print(row.getObject("ROW_VERSION"));
   //         pw.print(',');
   //
   //         for (ColumnDefinition column : definition.getColumns())
   //         {
   //            pw.print(row.getObject(column.getName()));
   //            pw.print(',');
   //         }
   //         pw.println();
   //      }
   //
   //      pw.close();
   //   }

   private static final ColumnDefinition[] ACT_COLUMNS =
   {
      new StringColumn("ACTIVITY_ID", 1, 10),
      new StringColumn("NAME", 11, 48),
      new StringColumn("DEPARTMENT", 59, 5),
      new StringColumn("MANAGER", 64, 8),
      new StringColumn("SECTION", 72, 4),
      new StringColumn("MAIL", 76, 8),
      new StringColumn("WBS", 123, 48),
      new PercentColumn("PERCENT_COMPLETE", 192),
      new DurationColumn("ORIGINAL_DURATION", 198),
      new DurationColumn("REMAINING_DURATION", 200),
      new DateInHoursColumn("EARLY_START", 202),
      new DateInHoursColumn("EARLY_FINISH", 206),
      new DateInHoursColumn("LATE_START", 210),
      new DateInHoursColumn("LATE_FINISH", 214),
      //new DateInHoursColumn("UNKNOWN_DATE1", 218),
      //new DateInHoursColumn("UNKNOWN_DATE2", 222),
      new DateInHoursColumn("ACTUAL_START", 234),
      new DateInHoursColumn("ACTUAL_FINISH", 238),
      new DateInHoursColumn("TARGET_START", 242),
      new DateInHoursColumn("TARGET_FINISH,", 246)
   };

   private static final ColumnDefinition[] CAL_COLUMNS =
   {
      new ShortColumn("CALENDAR_ID", 1),
      new StringColumn("NAME", 3, 16),
      new IntColumn("SUNDAY_HOURS", 19),
      new IntColumn("MONDAY_HOURS", 23),
      new IntColumn("TUESDAY_HOURS", 27),
      new IntColumn("WEDNESDAY_HOURS", 31),
      new IntColumn("THURSDAY_HOURS", 35),
      new IntColumn("FRIDAY_HOURS", 39),
      new IntColumn("SATURDAY_HOURS", 43),
   };

   private static final ColumnDefinition[] DIR_COLUMNS = {};

   private static final ColumnDefinition[] FLT_COLUMNS = {};

   private static final ColumnDefinition[] HOL_COLUMNS =
   {
      new ShortColumn("CALENDAR_ID", 1),
      new DateInDaysColumn("DATE", 3),
      new AnnualColumn("ANNUAL", 3)
   };

   private static final ColumnDefinition[] REL_COLUMNS =
   {
      new StringColumn("PREDECESSOR_ACTIVITY_ID", 1, 10),
      new StringColumn("SUCCESSOR_ACTIVITY_ID", 11, 10),
      new RelationTypeColumn("TYPE", 21),
      new DurationColumn("LAG", 22),
   };

   private static final ColumnDefinition[] RES_COLUMNS =
   {
      new StringColumn("ACTIVITY_ID", 1, 10),
      new StringColumn("RESOURCE_ID", 11, 8),
   };

   private static final ColumnDefinition[] RLB_COLUMNS =
   {
      new StringColumn("CODE", 1, 8),
      new StringColumn("NAME", 9, 40),
      new ShortColumn("BASE_CALENDAR_ID", 99),
      new ShortColumn("CALENDAR_ID", 101),
   };

   private static final ColumnDefinition[] TTL_COLUMNS =
   {
      new RawColumn("DATA", 0, 100),
      new StringColumn("TEXT1", 1, 48),
      new StringColumn("TEXT2", 49, 48),
      new ByteColumn("DEFINITION_ID", 97),
      new ShortColumn("ORDER", 98),
   };

   private static final Map<String, TableDefinition> TABLE_DEFINITIONS = new HashMap<>();
   static
   {
      TABLE_DEFINITIONS.put("ACT", new TableDefinition(0, 298, ACT_COLUMNS));
      TABLE_DEFINITIONS.put("CAL", new TableDefinition(0, 47, "CALENDAR_ID", null, CAL_COLUMNS));
      TABLE_DEFINITIONS.put("DIR", new TableDefinition(0, 565, DIR_COLUMNS));
      TABLE_DEFINITIONS.put("FLT", new TableDefinition(0, 137, FLT_COLUMNS));
      TABLE_DEFINITIONS.put("HOL", new TableDefinition(0, 11, HOL_COLUMNS));
      //TABLE_DEFINITIONS.put("LAY", new TableDefinition(0, 0, LAY_COLUMNS));
      //TABLE_DEFINITIONS.put("LOG", new TableDefinition(0, 0, LOG_COLUMNS));
      TABLE_DEFINITIONS.put("REL", new TableDefinition(0, 26, REL_COLUMNS));
      //TABLE_DEFINITIONS.put("REP", new TableDefinition(0, 0, REP_COLUMNS));
      TABLE_DEFINITIONS.put("RES", new TableDefinition(0, 118, RES_COLUMNS));
      TABLE_DEFINITIONS.put("RLB", new TableDefinition(0, 111, RLB_COLUMNS));
      TABLE_DEFINITIONS.put("TTL", new TableDefinition(0, 100, TTL_COLUMNS));
   }
}
