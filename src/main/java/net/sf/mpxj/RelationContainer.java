/*
 * file:       RelationContainer.java
 * author:     Jon Iles
 * date:       2023-10-09
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

package net.sf.mpxj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents Relation instances from the current project.
 */
public class RelationContainer extends ProjectEntityContainer<Relation>
{
   /**
    * Constructor.
    *
    * @param projectFile parent project
    */
   public RelationContainer(ProjectFile projectFile)
   {
      super(projectFile);
   }

   /**
    * Retrieve the predecessors for a given task.
    *
    * @param task task
    * @return task predecessors
    */
   public List<Relation> getPredecessors(Task task)
   {
      return m_predecessors.getOrDefault(task, EMPTY_LIST);
   }

   @Override protected void added(Relation element)
   {
      m_predecessors.computeIfAbsent(element.getSourceTask(), t -> new ArrayList<>()).add(element);
      m_successors.computeIfAbsent(element.getTargetTask(), t -> new ArrayList<>()).add(element);
   }

   @Override protected void removed(Relation element)
   {
      m_predecessors.getOrDefault(element.getSourceTask(), EMPTY_LIST).remove(element);
      m_successors.getOrDefault(element.getTargetTask(), EMPTY_LIST).remove(element);
   }

   @Override protected void replaced(Relation oldElement, Relation newElement)
   {
      removed(oldElement);
      added(newElement);
   }

   /**
    * Retrieve the successors of a given task.
    *
    * @param task task
    * @return task successors
    */
   public List<Relation> getSuccessors(Task task)
   {
      return m_successors.getOrDefault(task, EMPTY_LIST).stream().map(r -> new Relation(r.getTargetTask(), r.getSourceTask(), r.getType(), r.getLag())).collect(Collectors.toList());
   }

   /**
    * Add a predecessor for a task.
    *
    * @param sourceTask source task
    * @param targetTask target task (the predecessor)
    * @param type relationship type
    * @param lag relationship lag
    * @return new Relation instance
    */
   public Relation addPredecessor(Task sourceTask, Task targetTask, RelationType type, Duration lag)
   {
      //
      // Ensure that we have a valid lag duration
      //
      if (lag == null)
      {
         lag = Duration.getInstance(0, TimeUnit.DAYS);
      }

      //
      // Retrieve the list of predecessors
      //
      List<Relation> predecessorList = m_predecessors.getOrDefault(sourceTask, EMPTY_LIST);

      //
      // Ensure that there is only one predecessor relationship between
      // these two tasks.
      //
      Relation predecessorRelation = null;
      Iterator<Relation> iter = predecessorList.iterator();
      while (iter.hasNext())
      {
         predecessorRelation = iter.next();
         if (predecessorRelation.getTargetTask() == targetTask)
         {
            if (predecessorRelation.getType() != type || predecessorRelation.getLag().compareTo(lag) != 0)
            {
               predecessorRelation = null;
            }
            break;
         }
         predecessorRelation = null;
      }

      //
      // If necessary, create a new predecessor relationship
      //
      if (predecessorRelation == null)
      {
         predecessorRelation = new Relation(sourceTask, targetTask, type, lag);
         add(predecessorRelation);
      }

      return predecessorRelation;
   }

   /**
    * Remove a matching predecessor relationship from a task.
    *
    * @param sourceTask source task
    * @param targetTask target task (the predecessor to remove)
    * @param type relationship type
    * @param lag relationship lag
    * @return true if a matching predecessor was removed
    */
   public boolean removePredecessor(Task sourceTask, Task targetTask, RelationType type, Duration lag)
   {
      //
      // Retrieve the list of predecessors
      //
      List<Relation> predecessorList = m_predecessors.getOrDefault(sourceTask, EMPTY_LIST);
      if (predecessorList.isEmpty())
      {
         return false;
      }

      //
      // Ensure that we have a valid lag duration
      //
      if (lag == null)
      {
         lag = Duration.getInstance(0, TimeUnit.DAYS);
      }

      boolean matchFound = false;
      for (Relation relation : predecessorList)
      {
         if (relation.getTargetTask() == targetTask)
         {
            if (relation.getType() == type && relation.getLag().compareTo(lag) == 0)
            {
               matchFound = true;
               remove(relation);
               break;
            }
         }
      }
      return matchFound;
   }

   private final Map<Task, List<Relation>> m_predecessors = new HashMap<>();
   private final Map<Task, List<Relation>> m_successors = new HashMap<>();
   private static final List<Relation> EMPTY_LIST = Collections.emptyList();
}
