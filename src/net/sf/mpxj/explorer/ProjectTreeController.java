/*
 * file:       ProjectTreeController.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       06/07/2014
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

package net.sf.mpxj.explorer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.Group;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.json.JsonWriter;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.planner.PlannerWriter;
import net.sf.mpxj.primavera.PrimaveraPMFileWriter;
import net.sf.mpxj.reader.UniversalProjectReader;
import net.sf.mpxj.sdef.SDEFWriter;
import net.sf.mpxj.writer.ProjectWriter;

/**
 * Implements the controller component of the ProjectTree MVC.
 */
public class ProjectTreeController
{
   private static final Map<String, Class<? extends ProjectWriter>> WRITER_MAP = new HashMap<String, Class<? extends ProjectWriter>>();
   static
   {
      WRITER_MAP.put("MPX", MPXWriter.class);
      WRITER_MAP.put("MSPDI", MSPDIWriter.class);
      WRITER_MAP.put("PMXML", PrimaveraPMFileWriter.class);
      WRITER_MAP.put("PLANNER", PlannerWriter.class);
      WRITER_MAP.put("JSON", JsonWriter.class);
      WRITER_MAP.put("SDEF", SDEFWriter.class);
   }

   private final ProjectTreeModel m_model;
   private ProjectFile m_projectFile;

   /**
    * Constructor.
    *
    * @param model PoiTree model
    */
   public ProjectTreeController(ProjectTreeModel model)
   {
      m_model = model;
   }

   /**
    * Command to load a file.
    *
    * @param file file to load
    */
   public void loadFile(File file)
   {
      try
      {
         m_projectFile = new UniversalProjectReader().read(file);
         if (m_projectFile == null)
         {
            throw new IllegalArgumentException("Unsupported file type");
         }
      }

      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }

      DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(m_projectFile);

      DefaultMutableTreeNode propertiesNode = new DefaultMutableTreeNode(m_projectFile.getProjectProperties())
      {
         @Override public String toString()
         {
            return "Properties";
         }
      };

      projectNode.add(propertiesNode);

      DefaultMutableTreeNode tasksFolder = new DefaultMutableTreeNode("Tasks");
      projectNode.add(tasksFolder);
      addTasks(tasksFolder, m_projectFile);

      DefaultMutableTreeNode resourcesFolder = new DefaultMutableTreeNode("Resources");
      projectNode.add(resourcesFolder);
      addResources(resourcesFolder, m_projectFile);

      DefaultMutableTreeNode assignmentsFolder = new DefaultMutableTreeNode("Assignments");
      projectNode.add(assignmentsFolder);
      addAssignments(assignmentsFolder, m_projectFile);

      DefaultMutableTreeNode calendarsFolder = new DefaultMutableTreeNode("Calendars");
      projectNode.add(calendarsFolder);
      addCalendars(calendarsFolder, m_projectFile);

      DefaultMutableTreeNode groupsFolder = new DefaultMutableTreeNode("Groups");
      projectNode.add(groupsFolder);
      addGroups(groupsFolder, m_projectFile);

      m_model.setRoot(projectNode);
   }

   /**
    * Add tasks to the tree.
    *
    * @param parentNode parent tree node
    * @param parent parent task container
    */
   private void addTasks(DefaultMutableTreeNode parentNode, ChildTaskContainer parent)
   {
      for (Task task : parent.getChildTasks())
      {
         final Task t = task;
         DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(task)
         {
            @Override public String toString()
            {
               return t.getName();
            }
         };
         parentNode.add(childNode);
         addTasks(childNode, task);
      }
   }

   /**
    * Add resources to the tree.
    *
    * @param parentNode parent tree node
    * @param file resource container
    */
   private void addResources(DefaultMutableTreeNode parentNode, ProjectFile file)
   {
      for (Resource resource : file.getAllResources())
      {
         final Resource r = resource;
         DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(resource)
         {
            @Override public String toString()
            {
               return r.getName();
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add calendars to the tree.
    *
    * @param parentNode parent tree node
    * @param file calendar container
    */
   private void addCalendars(DefaultMutableTreeNode parentNode, ProjectFile file)
   {
      for (ProjectCalendar calendar : file.getCalendars())
      {
         final ProjectCalendar c = calendar;
         DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(calendar)
         {
            @Override public String toString()
            {
               return c.getName();
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add groups to the tree.
    *
    * @param parentNode parent tree node
    * @param file group container
    */
   private void addGroups(DefaultMutableTreeNode parentNode, ProjectFile file)
   {
      for (Group group : file.getGroups())
      {
         final Group g = group;
         DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(group)
         {
            @Override public String toString()
            {
               return g.getName();
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add assignments to the tree.
    *
    * @param parentNode parent tree node
    * @param file assignments container
    */
   private void addAssignments(DefaultMutableTreeNode parentNode, ProjectFile file)
   {
      for (ResourceAssignment assignment : file.getAllResourceAssignments())
      {
         final ResourceAssignment a = assignment;
         DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(a)
         {
            @Override public String toString()
            {
               Resource resource = a.getResource();
               String resourceName = resource == null ? "(unknown resource)" : resource.getName();
               Task task = a.getTask();
               String taskName = task == null ? "(unknown task)" : task.getName();
               return resourceName + "->" + taskName;
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Save the current file as the given type.
    *
    * @param file target file
    * @param type file type
    */
   public void saveFile(File file, String type)
   {
      try
      {
         Class<? extends ProjectWriter> fileClass = WRITER_MAP.get(type);
         if (fileClass == null)
         {
            throw new IllegalArgumentException("Cannot write files of type: " + type);
         }

         ProjectWriter writer = fileClass.newInstance();
         writer.write(m_projectFile, file);
      }

      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

}
