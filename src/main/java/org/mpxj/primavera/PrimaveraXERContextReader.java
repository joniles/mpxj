package org.mpxj.primavera;

import java.util.List;

import org.mpxj.ProjectContext;
import org.mpxj.WorkContour;
import org.mpxj.WorkContourContainer;
import org.mpxj.common.NumberHelper;

class PrimaveraXERContextReader extends PrimaveraContextReader
{
   public PrimaveraXERContextReader(XerFile file)
   {
      m_file= file;
   }

   public ProjectContext read()
   {
      configure();
      processCurrencies();
      processLocations();
      processShifts();
      processUnitsOfMeasure();
      processExpenseCategories();
      processCostAccounts();
      processWorkContours(); // TODO - use context
      processNotebookTopics();
      processUdfDefinitions();
      processProjectCodeDefinitions();
      processResourceCodeDefinitions();
      processRoleCodeDefinitions();
      processResourceAssignmentCodeDefinitions();
      processActivityCodeDefinitions();

      return m_context;
   }

   /**
    * Process currencies.
    */
   private void processCurrencies()
   {
      processCurrencies(m_file.getRows("currtype", null, null));
   }

   /**
    * Process locations.
    */
   private void processLocations()
   {
      processLocations(m_file.getRows("location", null, null));
   }

   /**
    * Process shifts.
    */
   private void processShifts()
   {
      processShifts(m_file.getRows("shift", null, null), m_file.getRows("shiftper", null, null));
   }

   /**
    * Process expense categories.
    */
   private void processExpenseCategories()
   {
      processExpenseCategories(m_file.getRows("costtype", null, null));
   }

   /**
    * Process cost accounts.
    */
   private void processCostAccounts()
   {
      processCostAccounts(m_file.getRows("account", null, null));
   }

   /**
    * Process resource curves.
    */
   private void processWorkContours()
   {
      WorkContourContainer contours = m_context.getWorkContours();

      List<Row> rows = m_file.getRows("rsrccurvdata", null, null);
      for (Row row : rows)
      {
         Integer id = row.getInteger("curv_id");
         if (contours.getByUniqueID(id) != null)
         {
            continue;
         }

         double[] values =
            {
               NumberHelper.getDouble(row.getDouble("pct_usage_0")),
               NumberHelper.getDouble(row.getDouble("pct_usage_1")),
               NumberHelper.getDouble(row.getDouble("pct_usage_2")),
               NumberHelper.getDouble(row.getDouble("pct_usage_3")),
               NumberHelper.getDouble(row.getDouble("pct_usage_4")),
               NumberHelper.getDouble(row.getDouble("pct_usage_5")),
               NumberHelper.getDouble(row.getDouble("pct_usage_6")),
               NumberHelper.getDouble(row.getDouble("pct_usage_7")),
               NumberHelper.getDouble(row.getDouble("pct_usage_8")),
               NumberHelper.getDouble(row.getDouble("pct_usage_9")),
               NumberHelper.getDouble(row.getDouble("pct_usage_10")),
               NumberHelper.getDouble(row.getDouble("pct_usage_11")),
               NumberHelper.getDouble(row.getDouble("pct_usage_12")),
               NumberHelper.getDouble(row.getDouble("pct_usage_13")),
               NumberHelper.getDouble(row.getDouble("pct_usage_14")),
               NumberHelper.getDouble(row.getDouble("pct_usage_15")),
               NumberHelper.getDouble(row.getDouble("pct_usage_16")),
               NumberHelper.getDouble(row.getDouble("pct_usage_17")),
               NumberHelper.getDouble(row.getDouble("pct_usage_18")),
               NumberHelper.getDouble(row.getDouble("pct_usage_19")),
               NumberHelper.getDouble(row.getDouble("pct_usage_20"))
            };

         contours.add(new WorkContour(id, row.getString("curv_name"), row.getBoolean("default_flag"), values));
      }
   }

   /**
    * Process units of measure.
    */
   private void processUnitsOfMeasure()
   {
      processUnitsOfMeasure(m_file.getRows("umeasure", null, null));
   }

   /**
    * Process notebook topics.
    */
   private void processNotebookTopics()
   {
      processNotebookTopics(m_file.getRows("memotype", null, null));
   }

   /**
    * Process user defined field definitions.
    */
   private void processUdfDefinitions()
   {
      List<Row> fields = m_file.getRows("udftype", null, null);
      processUdfDefinitions(fields);
   }

   /**
    * Process activity code definitions.
    */
   private void processActivityCodeDefinitions()
   {
      List<Row> types = m_file.getRows("actvtype", null, null);
      List<Row> typeValues = m_file.getRows("actvcode", null, null);
      processActivityCodeDefinitions(types, typeValues);
   }

   /**
    * Process project code definitions.
    */
   private void processProjectCodeDefinitions()
   {
      List<Row> types = m_file.getRows("pcattype", null, null);
      List<Row> typeValues = m_file.getRows("pcatval", null, null);
      processProjectCodeDefinitions(types, typeValues);
   }

   /**
    * Process resource code definitions.
    */
   private void processResourceCodeDefinitions()
   {
      List<Row> types = m_file.getRows("rcattype", null, null);
      List<Row> typeValues = m_file.getRows("rcatval", null, null);
      processResourceCodeDefinitions(types, typeValues);
   }

   /**
    * Process role code definitions.
    */
   private void processRoleCodeDefinitions()
   {
      List<Row> types = m_file.getRows("rolecattype", null, null);
      List<Row> typeValues = m_file.getRows("rolecatval", null, null);
      processRoleCodeDefinitions(types, typeValues);
   }

   /**
    * Process resource assignment code definitions.
    */
   private void processResourceAssignmentCodeDefinitions()
   {
      List<Row> types = m_file.getRows("asgnmntcattype", null, null);
      List<Row> typeValues = m_file.getRows("asgnmntcatval", null, null);
      processResourceAssignmentCodeDefinitions(types, typeValues);
   }

   private final XerFile m_file;
}
