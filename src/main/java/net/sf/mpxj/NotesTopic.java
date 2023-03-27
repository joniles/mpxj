package net.sf.mpxj;
public class NotesTopic implements ProjectEntityWithUniqueID
{
   public NotesTopic(Integer uniqueID, Integer sequenceNumber, String name, boolean eps, boolean project, boolean wbs, boolean activity)
   {
      m_uniqueID = uniqueID;
      m_sequenceNumber = sequenceNumber;
      m_name = name;
      m_eps = eps;
      m_project = project;
      m_wbs = wbs;
      m_activity = activity;
   }

   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   @Override public void setUniqueID(Integer id)
   {
      throw new UnsupportedOperationException();
   }

   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   public String getName()
   {
      return m_name;
   }

   public boolean getEps()
   {
      return m_eps;
   }

   public boolean getProject()
   {
      return m_project;
   }

   public boolean getWbs()
   {
      return m_wbs;
   }

   public boolean getActivity()
   {
      return m_activity;
   }

   private final Integer m_uniqueID;
   private final Integer m_sequenceNumber;
   private final String m_name;
   private final boolean m_eps;
   private final boolean m_project;
   private final boolean m_wbs;
   private final boolean m_activity;

   public static final NotesTopic DEFAULT = new NotesTopic(Integer.valueOf(1), Integer.valueOf(1), "Notes", true, true, true, true);
}
