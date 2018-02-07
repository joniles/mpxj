package net.sf.mpxj.primavera.p3;

public class TableDefinition
{
   public TableDefinition (int pageSize, int recordSize)
   {
      this(pageSize, recordSize, EMPTY_ARRAY);
   }
   
   public TableDefinition (int pageSize, int recordSize, ColumnDefinition... columns)
   {
      m_pageSize = pageSize;
      m_recordSize = recordSize;
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

   public ColumnDefinition[] getColumns()
   {
      return m_columns;
   }
   
   private final int m_pageSize;
   private final int m_recordSize;
   private final ColumnDefinition[] m_columns;
   
   private static final ColumnDefinition[] EMPTY_ARRAY = new ColumnDefinition[0];
}
