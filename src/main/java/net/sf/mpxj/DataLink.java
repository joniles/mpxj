
package net.sf.mpxj;

public final class DataLink
{
   public DataLink(String id)
   {
      m_id = id;
   }

   public String getID()
   {
      return m_id;
   }

   public FieldType getSourceField()
   {
      return m_sourceField;
   }

   public void setSourceField(FieldType sourceField)
   {
      m_sourceField = sourceField;
   }

   public Integer getSourceUniqueID()
   {
      return m_sourceUniqueID;
   }

   public void setSourceUniqueID(Integer sourceUniqueID)
   {
      m_sourceUniqueID = sourceUniqueID;
   }

   public FieldType getSinkField()
   {
      return m_sinkField;
   }

   public void setSinkField(FieldType sinkField)
   {
      m_sinkField = sinkField;
   }

   public Integer getSinkUniqueID()
   {
      return m_sinkUniqueID;
   }

   public void setSinkUniqueID(Integer sinkUniqueID)
   {
      m_sinkUniqueID = sinkUniqueID;
   }

   @Override public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("[DataLink id=");
      sb.append(m_id);
      if (m_sourceField != null)
      {
         sb.append(" sourceField=");
         sb.append(m_sourceField.getFieldTypeClass());
         sb.append('.');
         sb.append(m_sourceField);
         sb.append(" sourceUniqueID=");
         sb.append(m_sourceUniqueID);
      }
      
      if (m_sinkField != null)
      {
         sb.append(" sinkField=");
         sb.append(m_sinkField.getFieldTypeClass());
         sb.append('.');
         sb.append(m_sinkField);
         sb.append(" sinkUniqueID=");
         sb.append(m_sinkUniqueID);
      }
      
      return sb.toString();
   }
   
   private final String m_id;
   private FieldType m_sourceField;
   private Integer m_sourceUniqueID;
   private FieldType m_sinkField;
   private Integer m_sinkUniqueID;
}
