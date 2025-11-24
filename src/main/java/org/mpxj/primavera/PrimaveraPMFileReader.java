/*
 * file:       PrimaveraPMFileReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       08/08/2011
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj.primavera;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.mpxj.BaselineStrategy;
import org.mpxj.common.InputStreamHelper;
import org.apache.poi.util.ReplacingInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.Task;
import org.mpxj.common.UnmarshalHelper;
import org.mpxj.primavera.schema.APIBusinessObjects;
import org.mpxj.primavera.schema.ProjectType;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading a Primavera PM file.
 */
public final class PrimaveraPMFileReader extends AbstractProjectStreamReader
{
   /**
    * Set the ID of the project to be read.
    *
    * @param projectID project ID
    */
   public void setProjectID(int projectID)
   {
      m_projectID = Integer.valueOf(projectID);
   }

   /**
    * Retrieve a flag indicating if, when using `realAll` to retrieve all
    * projects from a file, cross project relations should be linked together.
    *
    * @return true if cross project relations should be linked
    */
   public boolean getLinkCrossProjectRelations()
   {
      return m_linkCrossProjectRelations;
   }

   /**
    * Sets a flag indicating if, when using `realAll` to retrieve all
    * projects from a file, cross project relations should be linked together.
    *
    * @param linkCrossProjectRelations true if cross project relations should be linked
    */
   public void setLinkCrossProjectRelations(boolean linkCrossProjectRelations)
   {
      m_linkCrossProjectRelations = linkCrossProjectRelations;
   }

   /**
    * Set the strategy to use when populating baseline fields.
    * The default is the planned dates strategy.
    *
    * @param strategy baseline strategy
    */
   public void setBaselineStrategy(BaselineStrategy strategy)
   {
      m_baselineStrategy = strategy;
   }

   /**
    * Retrieve the strategy to use when populating baseline fields.
    *
    * @return baseline strategy
    */
   public BaselineStrategy getBaselineStrategy()
   {
      return m_baselineStrategy;
   }

   /**
    * Populates a Map instance representing the IDs and names of
    * projects available in the current file.
    *
    * @param is input stream used to read XER file
    * @return Map instance containing ID and name pairs
    */
   public Map<Integer, String> listProjects(InputStream is) throws MPXJException
   {
      APIBusinessObjects apibo = processFile(is);
      List<ProjectType> projects = apibo.getProject();

      Map<Integer, String> result = new HashMap<>();
      projects.forEach(p -> result.put(p.getObjectId(), p.getName()));
      return result;
   }

   @Override public ProjectFile read(InputStream is) throws MPXJException
   {
      ProjectFile project = null;
      // Using readAll ensures that cross project relations can be included if required
      List<ProjectFile> projects = readAll(is);
      if (!projects.isEmpty())
      {
         if (m_projectID == null)
         {
            project = projects.get(0);
         }
         else
         {
            project = projects.stream().filter(p -> m_projectID.equals(p.getProjectProperties().getUniqueID())).findFirst().orElse(null);
         }
      }

      return project;
   }

   /**
    * This is a convenience method which allows all projects in a
    * PMXML file to be read in a single pass. External relationships
    * are not linked.
    *
    * @param is input stream
    * @return list of ProjectFile instances
    */
   @Override public List<ProjectFile> readAll(InputStream is) throws MPXJException
   {
      APIBusinessObjects apibo = processFile(is);
      XmlReaderState state = new XmlReaderState(apibo);

      addListenersToContext(state.getContext());
      new XmlContextReader(state).read();
      state.getContext().getProjectConfig().setBaselineStrategy(m_baselineStrategy);

      XmlProjectReader reader = new XmlProjectReader(state);
      List<ProjectFile> projects = apibo.getProject().stream().map(reader::read).collect(Collectors.toList());
      Map<Integer, List<ProjectFile>> baselines = new HashMap<>();
      apibo.getBaselineProject().forEach(b -> baselines.computeIfAbsent(b.getOriginalProjectObjectId(), k -> new ArrayList<>()).add(reader.read(b)));

      // Sort to ensure exported project is first
      projects.sort((o1, o2) -> Boolean.compare(o2.getProjectProperties().getExportFlag(), o1.getProjectProperties().getExportFlag()));

      linkCrossProjectRelations(projects, state.getExternalRelations());

      projects.forEach(p -> populateBaselines(p, baselines.getOrDefault(p.getProjectProperties().getUniqueID(), Collections.emptyList())));

      return projects;
   }

   private void linkCrossProjectRelations(List<ProjectFile> projects, List<ExternalRelation> externalRelations)
   {
      if (m_linkCrossProjectRelations)
      {
         for (ExternalRelation externalRelation : externalRelations)
         {
            Task externalTask = findTaskInProjects(projects, externalRelation.externalTaskUniqueID());
            if (externalTask != null)
            {
               Task successor;
               Task predecessor;

               if (externalRelation.getPredecessor())
               {
                  successor = externalRelation.getTargetTask();
                  predecessor = externalTask;
               }
               else
               {
                  successor = externalTask;
                  predecessor = externalRelation.getTargetTask();
               }

               // We need to ensure that the relation is present in both
               // projects so that predecessors and successors are populated
               // in both projects.

               ProjectFile successorProject = successor.getParentFile();
               successorProject.getRelations().addPredecessor(new Relation.Builder()
                  .predecessorTask(predecessor)
                  .successorTask(successor)
                  .type(externalRelation.getType())
                  .lag(externalRelation.getLag())
                  .uniqueID(externalRelation.getUniqueID())
                  .notes(externalRelation.getNotes()));

               ProjectFile predecessorProject = predecessor.getParentFile();
               predecessorProject.getRelations().addPredecessor(new Relation.Builder()
                  .predecessorTask(predecessor)
                  .successorTask(successor)
                  .type(externalRelation.getType())
                  .lag(externalRelation.getLag())
                  .uniqueID(externalRelation.getUniqueID())
                  .notes(externalRelation.getNotes()));
            }
         }
      }
   }

   private void populateBaselines(ProjectFile project, List<ProjectFile> baselines)
   {
      if (baselines.isEmpty())
      {
         return;
      }

      int baselineIndex = 0;
      Integer baselineProjectUniqueID = project.getProjectProperties().getBaselineProjectUniqueID();
      if (baselineProjectUniqueID != null)
      {
         ProjectFile baseline = baselines.stream().filter(b -> baselineProjectUniqueID.equals(b.getProjectProperties().getUniqueID())).findFirst().orElse(null);
         if (baseline != null)
         {
            project.setBaseline(baseline, baselineIndex++);
         }
      }

      for (ProjectFile baseline : baselines)
      {
         if (baselineProjectUniqueID != null && baselineProjectUniqueID.equals(baseline.getProjectProperties().getUniqueID()))
         {
            continue;
         }
         project.setBaseline(baseline, baselineIndex++);
      }
   }

   /**
    * Find a task by unique ID across multiple projects.
    *
    * @param projects list of projects
    * @param uniqueID unique ID to find
    * @return requested task, or null if the task can't be found
    */
   private Task findTaskInProjects(List<ProjectFile> projects, Integer uniqueID)
   {
      Task result = null;

      // we could aggregate the project task id maps but that's likely more work than just looping through the projects
      for (ProjectFile proj : projects)
      {
         result = proj.getTaskByUniqueID(uniqueID);
         if (result != null)
         {
            break;
         }
      }
      return result;
   }

   /**
    * Parse the PMXML file.
    *
    * @param stream PMXML file
    * @return APIBusinessObjects instance
    */
   private APIBusinessObjects processFile(InputStream stream) throws MPXJException
   {
      try
      {
         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         return (APIBusinessObjects) UnmarshalHelper.unmarshal(CONTEXT, configureInputSource(stream), new NamespaceFilter(), false);
      }

      catch (ParserConfigurationException | IOException | SAXException | JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }
   }

   /**
    * Normally we'd just create an InputSource instance directly from
    * the input stream. Unfortunately P6 doesn't seem to filter out
    * characters which are invalid for XML or not encoded correctly
    * when it writes PMXML files. This method tries to identify the
    * encoding claimed in the XML header and use this to a
    * PrimaveraInputStreamReader which can ignore these invalid characters.
    *
    * @param stream InputStream instance
    * @return InputSource instance
    */
   private InputSource configureInputSource(InputStream stream) throws IOException
   {
      int bufferSize = 512;
      BufferedInputStream bis = new BufferedInputStream(stream);
      bis.mark(bufferSize);
      byte[] buffer = InputStreamHelper.read(bis, bufferSize);
      bis.reset();

      // Handle trailing nul character following HTML content expressed as &#0;
      InputStream ris = new ReplacingInputStream(bis, "&lt;/HTML&gt;&#0;", "&lt;/HTML&gt;");

      InputSource result;
      Matcher matcher = ENCODING_PATTERN.matcher(new String(buffer));
      if (matcher.find())
      {
         result = new InputSource(new PrimaveraInputStreamReader(ris, matcher.group(1)));
      }
      else
      {
         result = new InputSource(ris);
      }

      return result;
   }

   /**
    * Cached context to minimise construction cost.
    */
   private static JAXBContext CONTEXT;

   /**
    * Note any error occurring during context construction.
    */
   private static JAXBException CONTEXT_EXCEPTION;

   static
   {
      try
      {
         //
         // JAXB RI property to speed up construction
         //
         System.setProperty("com.sun.xml.bind.v2.runtime.JAXBContextImpl.fastBoot", "true");

         //
         // Construct the context
         //
         CONTEXT = JAXBContext.newInstance("org.mpxj.primavera.schema", PrimaveraPMFileReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }

   private Integer m_projectID;
   private boolean m_linkCrossProjectRelations;
   private BaselineStrategy m_baselineStrategy = PrimaveraBaselineStrategy.PLANNED_ATTRIBUTES;

   private static final Pattern ENCODING_PATTERN = Pattern.compile(".*<\\?xml.*encoding=\"([^\"]+)\".*\\?>.*", Pattern.DOTALL);
}
