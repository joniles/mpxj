package net.sf.mpxj.openplan;

import java.io.FileNotFoundException;
import java.util.List;

import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

class ProjectReader
{
   public ProjectReader(DirectoryEntry root)
   {
      m_root = root;
      m_file = new ProjectFile();
   }

   public ProjectFile read(String name)
   {
//     ProjectConfig config = m_file.getProjectConfig();
//      config.setAutoTaskID(false);
//      config.setAutoTaskUniqueID(false);
//      config.setAutoResourceID(false);
//      config.setAutoResourceUniqueID(false);
//      config.setAutoOutlineLevel(false);
//      config.setAutoOutlineNumber(false);
//      config.setAutoWBS(false);
//      config.setAutoCalendarUniqueID(false);
//      config.setAutoAssignmentUniqueID(false);
//      config.setAutoRelationUniqueID(false);
//
//      addListenersToProject(projectFile);

      DirectoryEntry dir = getDirectoryEntry(m_root, name);
      List<Row> rows = new TableReader(dir, "PRJ").read();
      if (rows.size() != 1)
      {
         throw new OpenPlanException("Expecting 1 project row, found " + rows.size());
      }

      Row row = rows.get(0);
//      System.out.println(row);

      ProjectProperties props = m_file.getProjectProperties();
      props.setFileApplication("Deltek OpenPlan");
      props.setFileType("BK3");
      props.setMinutesPerDay(row.getInteger("MNPERDAY"));
      // CLD_UID, CLD_ID - calendar
      props.setStatusDate(row.getDate("STATDATE"));
      props.setName(row.getString("DESCRIPTION"));
      props.setBaselineStart(row.getDate("BSDATE"));
      props.setBaselineFinish(row.getDate("BFDATE"));
      props.setLastSaved(row.getDate("LASTUPDATE"));
      props.setManager(row.getString("OPMANAGER"));
      props.setCompany(row.getString("OPCOMPANY"));
      props.setStartDate(row.getDate("STARTDATE"));
      props.setFinishDate(row.getDate("SFDATE"));

      DependenciesReader dependencies = new DependenciesReader(dir).read();

      ResourceReader resourceReader = new ResourceReader(m_root, m_file);
      dependencies.getResources().forEach(r -> resourceReader.read(r));

      ActivityReader activityReader = new ActivityReader(dir, m_file);
      activityReader.read("ACT");

      m_file.readComplete();

      return m_file;
   }

   private DirectoryEntry getDirectoryEntry(DirectoryEntry root, String name)
   {
      try
      {
         return (DirectoryEntry) root.getEntry(name);
      }

      catch (FileNotFoundException e)
      {
         throw new OpenPlanException(e);
      }
   }

   private final DirectoryEntry m_root;
   private final ProjectFile m_file;
}
