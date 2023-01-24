package net.sf.mpxj;
public class Step
{
   public Step(Task task)
   {
      m_task = task;
   }

   public Task getTask()
   {
      return m_task;
   }

   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   public void setUniqueID(Integer uniqueID)
   {
      m_uniqueID = uniqueID;
   }

   public String getName()
   {
      return m_name;
   }

   public void setName(String name)
   {
      m_name = name;
   }

   public Double getPercentComplete()
   {
      return m_percentComplete;
   }

   public void setPercentComplete(Double percentComplete)
   {
      m_percentComplete = percentComplete;
   }

   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   public void setSequenceNumber(Integer sequenceNumber)
   {
      m_sequenceNumber = m_sequenceNumber;
   }

   public Double getWeight()
   {
      return m_weight;
   }

   public void setWeight(Double weight)
   {
      m_weight = weight;
   }

   private final Task m_task;
   private Integer m_uniqueID;
   private String m_name;
   private Double m_percentComplete;
   private Integer m_sequenceNumber;
   private Double m_weight;
}
