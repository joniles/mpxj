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

package org.mpxj.primavera.p3;

import java.io.File;
import java.io.IOException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.mpxj.primavera.common.ByteColumn;
import org.mpxj.primavera.common.ColumnDefinition;
import org.mpxj.primavera.common.IntColumn;
import org.mpxj.primavera.common.RowValidator;
import org.mpxj.primavera.common.ShortColumn;
import org.mpxj.primavera.common.StringColumn;
import org.mpxj.primavera.common.Table;
import org.mpxj.primavera.common.TableDefinition;

/**
 * Reads a directory containing a P3 Btrieve database and returns a map
 * of table names and the data they contain.
 */
class DatabaseReader
{
   /**
    * Main entry point. Reads a directory containing a P3 Btrieve database files
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

            int typeIndex = name.lastIndexOf('.') - 3;
            String type = name.substring(typeIndex, typeIndex + 3);
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

   private static final ColumnDefinition[] AC2_COLUMNS =
   {
      new StringColumn("UNKNOWN_1", 2, 4),
      new StringColumn("UNKNOWN_2", 6, 8),
      new ShortColumn("UNKNOWN_3", 14),
      new ShortColumn("UNKNOWN_4", 16),
      new StringColumn("UNKNOWN_5", 18, 4),
      new StringColumn("UNKNOWN_6", 26, 8)
   };

   private static final ColumnDefinition[] ACC_COLUMNS =
   {
      new StringColumn("COST_ACCOUNT_NUMBER", 2, 12),
      new StringColumn("UNDEFINED_1", 14, 4),
      new StringColumn("ACC_TITLE", 18, 40)
   };

   private static final ColumnDefinition[] ACT_COLUMNS =
   {
      new StringColumn("ACTIVITY_ID", 2, 10),
      new StringColumn("UNDEFINED_1", 12, 2),
      new DurationColumn("FREE_FLOAT", 14),
      new ShortColumn("CALENDAR_ID", 16),
      new IntColumn("DURATION_CALC_CODE", 18),
      new DurationColumn("ORIGINAL_DURATION", 22),
      new DurationColumn("REMAINING_DURATION", 24),
      new ShortColumn("ACTUAL_START_OR_CONSTRAINT_FLAG", 26),
      new ShortColumn("ACTUAL_FINISH_OR_CONSTRAINT_FLAG", 28),
      new PercentColumn("PERCENT_COMPLETE", 30),
      new DateColumn("EARLY_START_INTERNAL", 34),
      new DateColumn("LATE_START_INTERNAL", 38),
      new DateColumn("AS_OR_ED_CONSTRAINT", 42),
      new DateColumn("AF_OR_LD_CONSTRAINT", 46),
      new DateColumn("EF_INTERNAL", 50),
      new DateColumn("LF_INTERNAL", 54),
      new DurationColumn("TOTAL_FLOAT", 58),
      new StringColumn("MILESTONE", 60, 1),
      new StringColumn("CRITICAL_FLAG", 61, 1),
      new StringColumn("UNDEFINED_4", 62, 8),
      new ByteColumn("ST_ACTIVITY_TYPE", 70),
      new ByteColumn("LEVELING_TYPE", 71),
      new StringColumn("UNDEFINED_5B", 72, 2),
      new StringColumn("DEPT", 74, 3),
      new StringColumn("RESP", 77, 5),
      new StringColumn("PHAS", 82, 5),
      new StringColumn("STEP", 87, 5),
      new StringColumn("ITEM", 92, 5),
      new StringColumn("UNDEFINED_6", 97, 41),
      new StringColumn("ACTIVITY_TITLE", 138, 48),
      new IntColumn("SUSPEND_DATE", 186),
      new IntColumn("RESUME_DATE", 190),
      new IntColumn("UNDEFINED_8A", 194),
      new IntColumn("UNDEFINED_8B", 198),
      new IntColumn("UNDEFINED_8C", 202),
      new IntColumn("UNDEFINED_8D", 206),
      new IntColumn("UNDEFINED_8E", 210),
      new BtrieveDateColumn("EARLY_START", 214),
      new BtrieveDateColumn("LATE_START", 218),
      new BtrieveDateColumn("EARLY_FINISH", 222),
      new BtrieveDateColumn("LATE_FINISH", 226),
      new ByteColumn("EARLY_START_HOUR", 230),
      new ByteColumn("LATE_START_HOUR", 231),
      new ByteColumn("EARLY_FINISH_HOUR", 232),
      new ByteColumn("LATE_FINISH_HOUR", 233),
      new StringColumn("ACTUAL_START_FLAG", 234, 1),
      new StringColumn("ACTUAL_FINISH_FLAG", 235, 1),
      new StringColumn("UNDEFINED_10", 236, 10)
   };

   private static final ColumnDefinition[] DIR_COLUMNS =
   {
      new StringColumn("SUB_PROJECT_NAME", 2, 4),
      new IntColumn("SEQUENCE__NUMBER", 6),
      new IntColumn("PRODUCT_CODE", 10),
      new DateColumn("PROJECT_START_DATE", 14),
      new IntColumn("HOLIDAY_CONVENTION", 18),
      new StringColumn("SUB_PROJECT_ID", 22, 2),
      new StringColumn("UNDEFINED_1", 24, 2),
      new DateColumn("PROJECT_FINISH_DATE", 26),
      new IntColumn("REPORT_COUNTER", 30),
      new StringColumn("ACT_CODE_1_TO_4_SIZE", 34, 4),
      new StringColumn("ACT_CODE_5_TO_8_SIZE", 38, 4),
      new StringColumn("ACT_CODE_9_TO_12_SIZE", 42, 4),
      new StringColumn("ACT_CODE_13_TO_16_SIZE", 46, 4),
      new StringColumn("ACT_CODE_17_TO_20_SIZE", 50, 4),
      new StringColumn("ACT_ID_CODE_1_TO_4_SIZE", 54, 4),
      new IntColumn("PROJECT_TYPE", 58),
      new DateColumn("CURRENT_DATA_DATE", 62),
      new DateColumn("CALENDAR_START_DATE", 66),
      new StringColumn("UNDEFINED_2", 70, 4),
      new StringColumn("COMPANY_TITLE", 74, 36),
      new StringColumn("PROJECT_TITLE", 110, 36),
      new StringColumn("REPORT_TITLE", 146, 48),
      new StringColumn("PROJECT_VERSION", 194, 16),
      new StringColumn("UNDEFINED_3", 210, 32),
      new StringColumn("AUTO_COST_SET", 242, 4),
      new DateColumn("AUTO_COST_DATE", 246),
      new StringColumn("AUTO_COST_RULES", 250, 14),
      new StringColumn("UNDEFINED_4", 264, 14),
      new IntColumn("SCHEDULE_LOGIC", 278),
      new IntColumn("INTERRUPTIBLE_FLAG", 282),
      new DateColumn("LATEST_EARLY_FINISH", 286),
      new StringColumn("TARGET_1_NAME", 290, 4),
      new StringColumn("UNDEFINED_5", 294, 4),
      new StringColumn("TARGET_2_NAME", 298, 4),
      new StringColumn("UNDEFINED_6", 302, 4),
      new ShortColumn("LEVELED_SWITCH", 306), // LOGICAL
      new ShortColumn("TOTAL_FLOAT_TYPE", 308),
      new StringColumn("UNDEFINED_7", 310, 4),
      new ShortColumn("START_DAY_OF_WEEK", 314),
      new StringColumn("UNDEFINED_8", 316, 2),
      new ShortColumn("MASTER_CALENDAR_TYPE", 318),
      new ShortColumn("MASTER_CALENDAR_TYPE_AUX", 320),
      new StringColumn("GRAPHIC_SUMMARY_PROJECT", 322, 1),
      new StringColumn("SCHED_MAST_SUB_BOTH", 323, 1),
      new StringColumn("DECIMAL_PLACES", 324, 1),
      new StringColumn("UPDATE_SUB_DATA_DATE", 325, 1),
      new StringColumn("SUMMARY_CAL_ID", 326, 1),
      new StringColumn("END_DATE_FROM_MS", 327, 1),
      new StringColumn("SS_LAG_FROM_ASES", 328, 1),
      new StringColumn("UNDEFINED_9", 329, 1),
      new ByteColumn("WBSW_01", 330),
      new StringColumn("WBSS_01", 331, 1),
      new ByteColumn("WBSW_02", 332),
      new StringColumn("WBSS_02", 333, 1),
      new ByteColumn("WBSW_03", 334),
      new StringColumn("WBSS_03", 335, 1),
      new ByteColumn("WBSW_04", 336),
      new StringColumn("WBSS_04", 337, 1),
      new ByteColumn("WBSW_05", 338),
      new StringColumn("WBSS_05", 339, 1),
      new ByteColumn("WBSW_06", 340),
      new StringColumn("WBSS_06", 341, 1),
      new ByteColumn("WBSW_07", 342),
      new StringColumn("WBSS_07", 343, 1),
      new ByteColumn("WBSW_08", 344),
      new StringColumn("WBSS_08", 345, 1),
      new ByteColumn("WBSW_09", 346),
      new StringColumn("WBSS_09", 347, 1),
      new ByteColumn("WBSW_10", 348),
      new StringColumn("WBSS_10", 349, 1),
      new ByteColumn("WBSW_11", 350),
      new StringColumn("WBSS_11", 351, 1),
      new ByteColumn("WBSW_12", 352),
      new StringColumn("WBSS_12", 353, 1),
      new ByteColumn("WBSW_13", 354),
      new StringColumn("WBSS_13", 355, 1),
      new ByteColumn("WBSW_14", 356),
      new StringColumn("WBSS_14", 357, 1),
      new ByteColumn("WBSW_15", 358),
      new StringColumn("WBSS_15", 359, 1),
      new ByteColumn("WBSW_16", 360),
      new StringColumn("WBSS_16", 361, 1),
      new ByteColumn("WBSW_17", 362),
      new StringColumn("WBSS_17", 363, 1),
      new ByteColumn("WBSW_18", 364),
      new StringColumn("WBSS_18", 365, 1),
      new ByteColumn("WBSW_19", 366),
      new StringColumn("WBSS_19", 367, 1),
      new ByteColumn("WBSW_20", 368),
      new StringColumn("WBSS_20", 369, 1),
      new IntColumn("INTR_PRO_INDEX", 370),
      new IntColumn("INTR_PROJ_LAST_SCED_DATE", 374),
      new ShortColumn("LEVEL_NUM_SPLITS", 378),
      new ShortColumn("LEVEL_SPLIT_NON_WORK", 380),
      new ShortColumn("LEVEL_CONTIG_WORK", 382),
      new ShortColumn("LEVEL_MIN_PCT_UPT", 384),
      new ShortColumn("LEVEL_MAX_PCT_UPT", 386),
      new StringColumn("PROJECT_CODE_01", 388, 10),
      new StringColumn("PROJECT_CODE_02", 398, 10),
      new StringColumn("PROJECT_CODE_03", 408, 10),
      new StringColumn("PROJECT_CODE_04", 418, 10),
      new StringColumn("PROJECT_CODE_05", 428, 10),
      new StringColumn("PROJECT_CODE_06", 438, 10),
      new StringColumn("PROJECT_CODE_07", 448, 10),
      new StringColumn("PROJECT_CODE_08", 458, 10),
      new StringColumn("PROJECT_CODE_09", 468, 10),
      new StringColumn("PROJECT_CODE_10", 478, 10),
      new StringColumn("UNDEFINED_10", 488, 18)
   };

   private static final ColumnDefinition[] AIT_COLUMNS =
   {
      new StringColumn("ACT_ID", 2, 10),
      new StringColumn("ACTID_EXT", 12, 2),
      new StringColumn("RES", 14, 8),
      new StringColumn("COST_ACCOUNT_NUMBER", 22, 12),
      new StringColumn("RESOURCE_ID", 34, 1),
      new StringColumn("UNDEFINED_1", 35, 3),
      new DateColumn("PLANNED_START", 38),
      new DateColumn("PLANNED_FINISH", 42),
      new IntColumn("APPROVED_CHANGES", 46)
   };

   private static final ColumnDefinition[] DTL_COLUMNS =
   {
      new StringColumn("CODE_NAME", 2, 4),
      new StringColumn("CODE_VALUE", 6, 10),
      new StringColumn("DESCRIPTION", 16, 48)
   };

   private static final ColumnDefinition[] HOL_COLUMNS =
   {
      new ShortColumn("CAL_ID", 2),
      new DateColumn("START_OF_HOLIDAY", 4),
      new DateColumn("END_OF_HOLIDAY", 8)
   };

   private static final ColumnDefinition[] LOG_COLUMNS =
   {
      new StringColumn("ACT_ID", 2, 10),
      new StringColumn("ACT_ID_EXT", 12, 2),
      new ShortColumn("LOG_SEQ_NUMBER", 14),
      new StringColumn("LOG_MASK", 16, 2),
      new StringColumn("LOG_RECORD_INFO", 18, 48),
   };

   private static final ColumnDefinition[] REL_COLUMNS =
   {
      new StringColumn("PREDECESSOR_ACTIVITY_ID", 2, 10),
      new StringColumn("PREDECESSOR_ACTIVITY_EXT", 12, 2),
      new StringColumn("SUCCESSOR_ACTIVITY_ID", 14, 10),
      new StringColumn("SUCCESSOR_ACTIVITY_EXT", 24, 2),
      new RelationTypeColumn("LAG_TYPE", 26),
      new DurationColumn("LAG_VALUE", 28),
      new StringColumn("DRIVING_REL", 30, 1),
   };

   private static final ColumnDefinition[] RES_COLUMNS =
   {
      new StringColumn("ACTIVITY_ID", 2, 10),
      new StringColumn("UNDEFINED_1", 12, 2),
      new StringColumn("RESOURCE_ID", 14, 8),
      new StringColumn("COST_ACCOUNT_NUMBER", 22, 12),
      new ShortColumn("PERCENT_COMPLETE", 34),
      new ShortColumn("LAG", 36),
      new DurationColumn("REMAINING_DURATION", 38),
      new StringColumn("RES_DESIGNATOR", 40, 1),
      new StringColumn("DRIVING_RESOURCE", 41, 1),
      new IntColumn("BUDGET_QUANTITY", 42),
      new IntColumn("QUANTITY_THIS_PERIOD", 46),
      new IntColumn("QUANTITY_TO_DATE", 50),
      new IntColumn("QUANTITY_AT_COMPLETE", 54),
      new DateColumn("ST_RES_EARLY_START", 58),
      new DateColumn("ST_RES_EARLY_FINISH", 62),
      new IntColumn("UNDEFINED_2", 66),
      new IntColumn("BUDGET_COST", 70),
      new IntColumn("COST_THIS_PERIOD", 74),
      new IntColumn("COST_TO_DATE", 78),
      new IntColumn("COST_AT_COMPLETION", 82),
      new DateColumn("ST_RES_LATE_START", 86),
      new DateColumn("ST_RES_LATE_FINISH", 90),
      new IntColumn("UNDEFINED_3", 94)
   };

   private static final ColumnDefinition[] RIT_COLUMNS =
   {
      new StringColumn("ACTID", 2, 10),
      new StringColumn("ACTID_EXT", 12, 2),
      new StringColumn("RES", 13, 8),
      new StringColumn("COST_ACCOUNT_NUMBER", 21, 12),
      new StringColumn("RESOURCE_ID", 33, 1),
      new StringColumn("UNDEFINED_1", 34, 3),
      new IntColumn("COMMITMENT_AMOUNT", 37),
      new IntColumn("ORIGINAL_BUDGET", 41),
   };

   private static final ColumnDefinition[] RLB_COLUMNS =
   {
      new StringColumn("RES_ID", 2, 8),
      new StringColumn("UNIT_OF_MEASURE", 10, 4),
      new StringColumn("RES_TITLE", 14, 40),
      new IntColumn("ESCALATION_VAL_1", 54),
      new DateColumn("ESCALATION_DATE_1", 58),
      new IntColumn("ESCALATION_VAL_2", 62),
      new DateColumn("ESCALATION_DATE_2", 66),
      new IntColumn("ESCALATION_VAL_3", 70),
      new DateColumn("ESCALATION_DATE_3", 7),
      new IntColumn("ESCALATION_VAL_4", 78),
      new DateColumn("ESCALATION_DATE_4", 8),
      new IntColumn("ESCALATION_VAL_5", 86),
      new DateColumn("ESCALATION_DATE_5", 9),
      new IntColumn("ESCALATION_VAL_6", 94),
      new DateColumn("ESCALATION_DATE_6", 9),
      new IntColumn("NORM_LIM_VAL_1", 102),
      new IntColumn("MAX_LIM_VAL_1", 106),
      new DateColumn("LIM_TO_DATE_1", 110),
      new IntColumn("NORM_LIM_VAL_2", 114),
      new IntColumn("MAX_LIM_VAL_2", 118),
      new DateColumn("LIM_TO_DATE_2", 122),
      new IntColumn("NORM_LIM_VAL_3", 126),
      new IntColumn("MAX_LIM_VAL_3", 130),
      new DateColumn("LIM_TO_DATE_3", 134),
      new IntColumn("NORM_LIM_VAL_4", 138),
      new IntColumn("MAX_LIM_VAL_4", 142),
      new DateColumn("LIM_TO_DATE_4", 146),
      new IntColumn("NORM_LIM_VAL_5", 150),
      new IntColumn("MAX_LIM_VAL_5", 154),
      new DateColumn("LIM_TO_DATE_5", 158),
      new IntColumn("NORM_LIM_VAL_6", 162),
      new IntColumn("MAX_LIM_VAL_6", 166),
      new DateColumn("LIM_TO_DATE_6", 170),
      new ShortColumn("SHIFT_NUMB", 174),
      new ShortColumn("SHIFT_LIMIT_TABLE", 176),
      new ShortColumn("DRIVING_RESOURCE", 178),
      new ShortColumn("UNDEFINED_1", 180)
   };

   private static final ColumnDefinition[] SRT_COLUMNS =
   {
      new IntColumn("SEQ_NUMBER", 2),
      new StringColumn("ACT_ID", 2, 10),
      new StringColumn("UNDEFINED_1", 2, 16)
   };

   private static final ColumnDefinition[] STR_COLUMNS =
   {
      new StringColumn("INDICATOR", 2, 1),
      new StringColumn("INDICATOR_EXT", 3, 1),
      new ShortColumn("LEVEL_NUMBER", 4),
      new StringColumn("UNDEFINED_2", 6, 4),
      new StringColumn("CODE_VALUE", 10, 48),
      new StringColumn("CODE_TITLE", 58, 48)
   };

   private static final ColumnDefinition[] TTL_COLUMNS =
   {
      new IntColumn("CODE_NAME", 2),
      new StringColumn("CODE_VALUE", 6, 12),
      new StringColumn("DESCRIPTION", 18, 48),
      new ByteColumn("SORT_ORDER", 66)
   };

   private static final ColumnDefinition[] WBS_COLUMNS =
   {
      new StringColumn("ACTIVITY_ID", 2, 10),
      new StringColumn("ACTIVITY_ID_EXT", 12, 2),
      new StringColumn("CODE_VALUE", 14, 48),
      new StringColumn("INDICATOR", 62, 1)
   };

   // Budget Summary
   private static final ColumnDefinition[] ITM_COLUMNS =
   {
      new StringColumn("ACTIVITY_ID", 2, 12),
      new StringColumn("RESOURCE", 14, 8),
      new StringColumn("COST_ACCOUNT", 22, 11),
      new StringColumn("CATEGORY", 33, 5),
      new ShortColumn("UNKNOWN_3", 38),
      new ShortColumn("UNKNOWN_4", 40)
   };

   private static final ColumnDefinition[] PPA_COLUMNS =
   {
      new StringColumn("UNKNOWN_1", 2, 10),
      new StringColumn("UNKNOWN_2", 12, 19),
      new StringColumn("UNKNOWN_3", 31, 2)
            // additional unknown fields
   };

   private static final ColumnDefinition[] SPR_COLUMNS =
   {
      new ShortColumn("UNKNOWN_1", 4)
            // additional unknown fields
   };

   private static final ColumnDefinition[] STW_COLUMNS =
   {
            // unknown fields
   };

   private static final ColumnDefinition[] TIM_COLUMNS =
   {
            // unknown fields
   };

   private static final ColumnDefinition[] AUD_COLUMNS =
   {
            // unknown fields
   };

   private static final ColumnDefinition[] REP_COLUMNS =
   {
            // unknown fields
   };

   private static final ColumnDefinition[] LAY_COLUMNS =
   {
            // unknown fields
   };

   private static final ColumnDefinition[] PLT_COLUMNS =
   {
            // unknown fields
   };

   /**
    * Used to determine if a row from the DIR Table is valid, based on whether
    * the project start date falls after the date epoch.
    */
   private static final RowValidator DIR_ROW_VALIDATOR = new RowValidator()
   {
      @Override public boolean validRow(Map<String, Object> row)
      {
         LocalDateTime date = (LocalDateTime) row.get("PROJECT_START_DATE");
         return date != null && date.isAfter(EPOCH);
      }
   };

   /**
    * Epoch for date calculations. Represents 31/12/1983.
    */
   static final LocalDateTime EPOCH = LocalDateTime.of(1983, 12, 31, 0, 0);

   private static final Map<String, TableDefinition> TABLE_DEFINITIONS = new HashMap<>();
   static
   {
      TABLE_DEFINITIONS.put("AC2", new TableDefinition(512, 34, AC2_COLUMNS));
      TABLE_DEFINITIONS.put("ACC", new TableDefinition(512, 58, ACC_COLUMNS));
      TABLE_DEFINITIONS.put("ACT", new TableDefinition(1024, 250, "ACTIVITY_ID", null, ACT_COLUMNS));
      TABLE_DEFINITIONS.put("AIT", new TableDefinition(1024, 214, AIT_COLUMNS));
      TABLE_DEFINITIONS.put("AUD", new TableDefinition(1024, 143, AUD_COLUMNS));
      // CAL - not a Btrieve file
      TABLE_DEFINITIONS.put("DIR", new TableDefinition(512, 506, "SUB_PROJECT_NAME", DIR_ROW_VALIDATOR, DIR_COLUMNS));
      // DST - not a Btrieve file
      TABLE_DEFINITIONS.put("DTL", new TableDefinition(1024, 64, DTL_COLUMNS));
      TABLE_DEFINITIONS.put("HOL", new TableDefinition(512, 12, HOL_COLUMNS));
      TABLE_DEFINITIONS.put("ITM", new TableDefinition(1024, 42, ITM_COLUMNS));
      TABLE_DEFINITIONS.put("LAY", new TableDefinition(512, 14, LAY_COLUMNS));
      TABLE_DEFINITIONS.put("LOG", new TableDefinition(1024, 66, LOG_COLUMNS));
      TABLE_DEFINITIONS.put("PLT", new TableDefinition(512, 21, PLT_COLUMNS));
      TABLE_DEFINITIONS.put("PPA", new TableDefinition(1024, 46, PPA_COLUMNS));
      TABLE_DEFINITIONS.put("REL", new TableDefinition(512, 31, REL_COLUMNS));
      TABLE_DEFINITIONS.put("REP", new TableDefinition(512, 21, REP_COLUMNS));
      TABLE_DEFINITIONS.put("RES", new TableDefinition(1024, 114, RES_COLUMNS));
      TABLE_DEFINITIONS.put("RIT", new TableDefinition(1024, 214, RIT_COLUMNS));
      TABLE_DEFINITIONS.put("RLB", new TableDefinition(1024, 182, "RES_ID", null, RLB_COLUMNS));
      TABLE_DEFINITIONS.put("SPR", new TableDefinition(1024, 37, SPR_COLUMNS));
      TABLE_DEFINITIONS.put("SRT", new TableDefinition(4096, 16, SRT_COLUMNS));
      TABLE_DEFINITIONS.put("STR", new TableDefinition(512, 122, "CODE_VALUE", null, STR_COLUMNS));
      TABLE_DEFINITIONS.put("STW", new TableDefinition(1024, 58, STW_COLUMNS));
      TABLE_DEFINITIONS.put("TIM", new TableDefinition(1024, 153, TIM_COLUMNS));
      TABLE_DEFINITIONS.put("TTL", new TableDefinition(1024, 67, TTL_COLUMNS));
      TABLE_DEFINITIONS.put("WBS", new TableDefinition(1024, 63, "ACTIVITY_ID", null, WBS_COLUMNS));
   }
}
