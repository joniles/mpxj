
package net.sf.mpxj.openplan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.AutoCloseableHelper;
import net.sf.mpxj.reader.AbstractProjectStreamReader;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/*
   ACT - Activity
   ASG - Resource Assignment
   BSA - Baseline Activity
   BSU - Baseline Usage
   CST - Resource Cost
   PRJ - Project (OPP_PRJ)
   REL - Relationship
   RSK - Risk Detail
   SUB - Subproject
   USE - Resource Usage
   AVL - Resource Availability
   PSU - Project Summary
   RES - Resource
   RSL - Resource Escalation
   CDR - Code Data
*/

public final class OpenPlanReader extends AbstractProjectStreamReader
{
   @Override public ProjectFile read(InputStream is) throws MPXJException
   {
      try
      {
         return read(new POIFSFileSystem(is));
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   @Override public List<ProjectFile> readAll(InputStream inputStream) throws MPXJException
   {
      return Collections.singletonList(read(inputStream));
   }

   @Override public ProjectFile read(File file) throws MPXJException
   {
      List<ProjectFile> projects = readAll(file);
      return projects.isEmpty() ? null : projects.get(0);
   }

   @Override public List<ProjectFile> readAll(File file) throws MPXJException
   {
      POIFSFileSystem fs = null;

      try
      {
         // Note we provide this version of the read method rather than using
         // the AbstractProjectStreamReader version as we can work with the File
         // instance directly for reduced memory consumption and the ability
         // to open larger MPP files.
         fs = new POIFSFileSystem(file);
         return readAll(fs);
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(fs);
      }
   }

   public ProjectFile read(POIFSFileSystem fs) throws MPXJException
   {
      List<ProjectFile> projects = readAll(fs);
      return projects.isEmpty() ? null : projects.get(0);
   }

   public List<ProjectFile> readAll(POIFSFileSystem fs) throws MPXJException
   {
      try
      {
         return processProjects(fs.getRoot());
      }

      catch (OpenPlanException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   private List<ProjectFile> processProjects(DirectoryEntry root)
   {
      return root.getEntryNames().stream().filter(s -> s.toUpperCase().endsWith("_PRJ")).map(s -> processProject(root, s)).collect(Collectors.toList());
   }

   private ProjectFile processProject(DirectoryEntry root, String name)
   {
      return new ProjectDirectoryReader(root).read(name);
   }
}
