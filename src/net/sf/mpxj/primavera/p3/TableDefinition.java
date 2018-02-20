
package net.sf.mpxj.primavera.p3;

public class TableDefinition
{
   public TableDefinition(int pageSize, int recordSize, ColumnDefinition... columns)
   {
      this(pageSize, recordSize, null, null, columns);
   }

   public TableDefinition(int pageSize, int recordSize, String primaryKeyColumnName, RowValidator rowValidator, ColumnDefinition... columns)
   {
      m_pageSize = pageSize;
      m_recordSize = recordSize;
      m_primaryKeyColumnName = primaryKeyColumnName;
      m_rowValidator = rowValidator;
      m_columns = columns;
   }

   public int getPageSize()
   {
      return m_pageSize;
   }

   public int getRecordSize()
   {
      return m_recordSize;
   }

   public String getPrimaryKeyColumnName()
   {
      return m_primaryKeyColumnName;
   }

   public RowValidator getRowValidator()
   {
      return m_rowValidator;
   }

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
