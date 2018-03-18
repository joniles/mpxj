// TODO: can we move classes into a common package?

package net.sf.mpxj.primavera.suretrak;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.primavera.p3.ByteColumn;
import net.sf.mpxj.primavera.p3.ColumnDefinition;
import net.sf.mpxj.primavera.p3.IntColumn;
import net.sf.mpxj.primavera.p3.ShortColumn;
import net.sf.mpxj.primavera.p3.StringColumn;
import net.sf.mpxj.primavera.p3.Table;
import net.sf.mpxj.primavera.p3.TableDefinition;

public class DatabaseReader
{
   public Map<String, Table> process(File directory, String prefix) throws IOException
   {
      Map<String, Table> tables = new HashMap<String, Table>();
      File[] files = directory.listFiles();
      if (files != null)
      {
         for (File file : files)
         {
            String name = file.getName().toUpperCase();
            if (!name.startsWith(prefix))
            {
               continue;
            }

            int typeIndex = name.lastIndexOf('.');
            String type = name.substring(typeIndex + 1, name.length());
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

   private static final ColumnDefinition[] ACT_COLUMNS = {};

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
      new DateColumn("DATE", 3),
      new AnnualColumn("ANNUAL", 3)
   };

   private static final ColumnDefinition[] REL_COLUMNS = {};

   private static final ColumnDefinition[] RES_COLUMNS = {};

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

   private static final Map<String, TableDefinition> TABLE_DEFINITIONS = new HashMap<String, TableDefinition>();
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
