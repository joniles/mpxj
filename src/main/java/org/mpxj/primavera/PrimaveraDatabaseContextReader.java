package org.mpxj.primavera;

import java.sql.SQLException;
import java.util.List;

import org.mpxj.MPXJException;
import org.mpxj.ProjectContext;
import org.mpxj.WorkContour;
import org.mpxj.WorkContourContainer;

class PrimaveraDatabaseContextReader extends PrimaveraContextReader
{
   public PrimaveraDatabaseContextReader(PrimaveraDatabaseConnection database, String schema, boolean ignoreErrors, Integer projectID)
   {
      m_database = database;
      m_schema = schema;
      m_ignoreErrors = ignoreErrors;
      m_projectID = projectID;
   }

   public ProjectContext read() throws MPXJException
   {
      try
      {
         configure();
         processCurrencies();
         processLocations();
         processShifts();
         processUnitsOfMeasure();
         processExpenseCategories();
         processCostAccounts();
         processWorkContours();
         processNotebookTopics();
         processUdfDefinitions();
         processProjectCodeDefinitions();
         processResourceCodeDefinitions();
         processRoleCodeDefinitions();
         processResourceAssignmentCodeDefinitions();
         processActivityCodeDefinitions();
         return m_context;
      }

      catch (SQLException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         m_database.close();
      }
   }

   /**
    * Select the currencies from the database.
    */
   private void processCurrencies() throws SQLException
   {
      if (m_database.hasTable("CURRTYPE"))
      {
         processCurrencies(m_database.getRows("select * from " + m_schema + "currtype"));
      }
   }

   /**
    * Select the locations from the database.
    */
   private void processLocations() throws SQLException
   {
      if (m_database.hasTable("LOCATION"))
      {
         processLocations(m_database.getRows("select * from " + m_schema + "location"));
      }
   }

   /**
    * Select shifts from the database.
    */
   private void processShifts() throws SQLException
   {
      if (m_database.hasTable("SHIFT") && m_database.hasTable("SHIFTPER"))
      {
         processShifts(m_database.getRows("select * from " + m_schema + "shift"), m_database.getRows("select * from " + m_schema + "shiftper"));
      }
   }

   /**
    * Process units of measure.
    */
   private void processUnitsOfMeasure() throws SQLException
   {
      processUnitsOfMeasure(m_database.getRows("select * from " + m_schema + "umeasure"));
   }

   /**
    * Select the expense categories from the database.
    */
   private void processExpenseCategories() throws SQLException
   {
      processExpenseCategories(m_database.getRows("select * from " + m_schema + "costtype"));
   }

   /**
    * Select the cost accounts from the database.
    */
   private void processCostAccounts() throws SQLException
   {
      processCostAccounts(m_database.getRows("select * from " + m_schema + "account"));
   }

   /**
    * Process resource curves.
    */
   private void processWorkContours() throws SQLException
   {
      WorkContourContainer contours = m_context.getWorkContours();
      List<Row> rows = m_database.getRows("select * from " + m_schema + "rsrccurv");
      for (Row row : rows)
      {
         try
         {
            Integer id = row.getInteger("curv_id");
            if (contours.getByUniqueID(id) != null)
            {
               continue;
            }
            double[] values = new StructuredTextParser().parse(row.getString("curv_data")).getChildren().stream().mapToDouble(r -> Double.parseDouble(r.getAttribute("PctUsage"))).toArray();
            contours.add(new WorkContour(id, row.getString("curv_name"), row.getBoolean("default_flag"), values));
         }

         catch (Exception ex)
         {
            if (m_ignoreErrors)
            {
               // Skip any curves we can't read
               m_context.addIgnoredError(ex);
            }
            else
            {
               throw ex;
            }
         }
      }
   }

   /**
    * Process notebook topics.
    */
   private void processNotebookTopics() throws SQLException
   {
      processNotebookTopics(m_database.getRows("select * from " + m_schema + "memotype"));
   }

   /**
    * Process user defined field definitions.
    */
   private void processUdfDefinitions() throws SQLException
   {
      processUdfDefinitions(m_database.getRows("select * from " + m_schema + "udftype"));
   }

   /**
    * Process project code definitions.
    */
   private void processProjectCodeDefinitions() throws SQLException
   {
      if (m_database.hasTable("PCATTYPE") && m_database.hasTable("PCATVAL"))
      {
         List<Row> types;
         List<Row> typeValues;

         if (m_projectID == null)
         {
            types = m_database.getRows("select * from " + m_schema + "pcattype");
            typeValues = m_database.getRows("select * from " + m_schema + "pcatval");
         }
         else
         {
            types = m_database.getRows("select * from " + m_schema + "pcattype where proj_catg_type_id in (select distinct proj_catg_type_id from projpcat where proj_id=?)", m_projectID);
            typeValues = m_database.getRows("select * from " + m_schema + "pcatval where proj_catg_id in (select distinct proj_catg_id from projpcat where proj_id=?)", m_projectID);
         }

         processProjectCodeDefinitions(types, typeValues);
      }
   }

   /**
    * Process resource code definitions.
    */
   private void processResourceCodeDefinitions() throws SQLException
   {
      if (m_database.hasTable("RCATTYPE") && m_database.hasTable("RCATVAL"))
      {
         List<Row> types;
         List<Row> typeValues;

         if (m_projectID == null)
         {
            types = m_database.getRows("select * from " + m_schema + "rcattype");
            typeValues = m_database.getRows("select * from " + m_schema + "rcatval");
         }
         else
         {
            types = m_database.getRows("select * from " + m_schema + "rcattype where rsrc_catg_type_id in (select distinct rsrc_catg_type_id from rsrcrcat where rsrc_id in (select distinct rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null))", m_projectID);
            typeValues = m_database.getRows("select * from " + m_schema + "rcatval where rsrc_catg_id in (select distinct rsrc_catg_id from rsrcrcat where rsrc_id in (select distinct rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null))", m_projectID);
         }

         processResourceCodeDefinitions(types, typeValues);
      }
   }

   /**
    * Process role code definitions.
    */
   private void processRoleCodeDefinitions() throws SQLException
   {
      if (m_database.hasTable("ROLECATTYPE") && m_database.hasTable("ROLECATVAL"))
      {
         List<Row> types;
         List<Row> typeValues;

         if (m_projectID == null)
         {
            types = m_database.getRows("select * from " + m_schema + "rolecattype");
            typeValues = m_database.getRows("select * from " + m_schema + "rolecatval");
         }
         else
         {
            types = m_database.getRows("select * from " + m_schema + "rolecattype where role_catg_type_id in (select distinct role_catg_type_id from rolercat where role_id in (select distinct role_id from " + m_schema + "rsrcrole where delete_date is null and rsrc_id in (select rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null)))", m_projectID);
            typeValues = m_database.getRows("select * from " + m_schema + "rolecatval where role_catg_id in (select distinct role_catg_id from rolercat where role_id in (select distinct role_id from " + m_schema + "rsrcrole where delete_date is null and rsrc_id in (select rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null)))", m_projectID);
         }

         processRoleCodeDefinitions(types, typeValues);
      }
   }

   /**
    * Process resource assignment code definitions.
    */
   private void processResourceAssignmentCodeDefinitions() throws SQLException
   {
      if (m_database.hasTable("ASGNMNTCATTYPE") && m_database.hasTable("ASGNMNTCATVAL"))
      {
         List<Row> types;
         List<Row> typeValues;

         if (m_projectID == null)
         {
            types = m_database.getRows("select * from " + m_schema + "asgnmntcattype");
            typeValues = m_database.getRows("select * from " + m_schema + "asgnmntcatval");
         }
         else
         {
            types = m_database.getRows("select * from " + m_schema + "asgnmntcattype where asgnmnt_catg_type_id in (select distinct asgnmnt_catg_type_id from asgnmntacat where proj_id=?)", m_projectID);
            typeValues = m_database.getRows("select * from " + m_schema + "asgnmntcatval where asgnmnt_catg_id in (select distinct asgnmnt_catg_id from asgnmntacat where proj_id=?)", m_projectID);
         }

         processResourceAssignmentCodeDefinitions(types, typeValues);
      }
   }

   /**
    * Process activity code definitions.
    */
   private void processActivityCodeDefinitions() throws SQLException
   {
      if (m_database.hasTable("ACTVTYPE") && m_database.hasTable("ACTVCODE"))
      {
         List<Row> types;
         List<Row> typeValues;

         if (m_projectID == null)
         {
            types = m_database.getRows("select * from " + m_schema + "actvtype");
            typeValues = m_database.getRows("select * from " + m_schema + "actvcode");
         }
         else
         {
            types = m_database.getRows("select * from " + m_schema + "actvtype where actv_code_type_id in (select distinct actv_code_type_id from taskactv where proj_id=?)", m_projectID);
            typeValues = m_database.getRows("select * from " + m_schema + "actvcode where actv_code_id in (select distinct actv_code_id from taskactv where proj_id=?)", m_projectID);
         }

         processActivityCodeDefinitions(types, typeValues);
      }
   }

   private final PrimaveraDatabaseConnection m_database;
   private final String m_schema;
   private final boolean m_ignoreErrors;
   private final Integer m_projectID;
}
