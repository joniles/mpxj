
package net.sf.mpxj.primavera.common;

/**
 * Represents the structure of a Btrieve table.
 */
public class TableDefinition
{
   /**
    * Constructor.
    *
    * @param pageSize page size in bytes
    * @param recordSize record size in bytes
    * @param columns list of column definitions
    */
   public TableDefinition(int pageSize, int recordSize, ColumnDefinition... columns)
   {
      this(pageSize, recordSize, null, null, columns);
   }

   /**
    * Constructor.
    *
    * @param pageSize page size in bytes
    * @param recordSize record size in bytes
    * @param primaryKeyColumnName optional primary key column name
    * @param rowValidator optional row validation
    * @param columns list of column definitions
    */
   public TableDefinition(int pageSize, int recordSize, String primaryKeyColumnName, RowValidator rowValidator, ColumnDefinition... columns)
   {
      m_pageSize = pageSize;
      m_recordSize = recordSize;
      m_primaryKeyColumnName = primaryKeyColumnName;
      m_rowValidator = rowValidator;
      m_columns = columns;
   }

   /**
    * Retrieve the page size.
    *
    * @return page sze in bytes
    */
   public int getPageSize()
   {
      return m_pageSize;
   }

   /**
    * Retrieve the record size.
    *
    * @return record size in bytes
    */
   public int getRecordSize()
   {
      return m_recordSize;
   }

   /**
    * Retrieve the optional primary key column name.
    *
    * @return primary key column name or null
    */
   public String getPrimaryKeyColumnName()
   {
      return m_primaryKeyColumnName;
   }

   /**
    * Retrieve the optional row validator.
    *
    * @return RowValidator instance or null
    */
   public RowValidator getRowValidator()
   {
      return m_rowValidator;
   }

   /**
    * Retrieve the column definitions.
    *
    * @return array of column definitions
    */
   public ColumnDefinition[] getColumns()
   {
      return m_columns;
   }

   private final int m_pageSize;
   private final int m_recordSize;
   private final String m_primaryKeyColumnName;
   private final RowValidator m_rowValidator;
   private final ColumnDefinition[] m_columns;
}
