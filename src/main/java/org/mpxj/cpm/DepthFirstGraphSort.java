/*
 * file:       DepthFirstGraphSort.java
 * author:     Jon Iles
 * date:       2025-04-02
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

package org.mpxj.cpm;

import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.Task;

/**
 * Sort tasks based on successor relationship ready for CPM forward pass.
 */
class DepthFirstGraphSort
{
   /**
    * Constructor.
    *
    * @param file parent project file
    * @param filter filter to apply to tasks
    */
   public DepthFirstGraphSort(ProjectFile file, Function<Task, Boolean> filter)
   {
      m_file = file;
      m_includeTask = filter;
   }

   /**
    * Perform the sort.
    * Note that if a cycle is encountered a CycleException will be thrown.
    *
    * @return sorted tasks
    */
   public List<Task> sort() throws CycleException
   {
      try
      {
         for (Task task : m_file.getTasks())
         {
            if (m_includeTask.apply(task).booleanValue())
            {
               visit(task);
            }
         }

         return new ArrayList<>(m_tasks);
      }

      finally
      {
         m_tasks.clear();
         m_temporaryMark.clear();
         m_permanentMark.clear();
      }
   }

   /**
    * Retrieve the successors for the given task.
    * Note this method is intended to be overridden to augment the original functionality.
    *
    * @param task task from which to generate list of successors
    * @return list of successors
    */
   public List<Relation> getSuccessors(Task task)
   {
      return task.getSuccessors();
   }

   /**
    * Recursively build the sorted list of tasks.
    *
    * @param task current task
    */
   private void visit(Task task) throws CycleException
   {
      if (m_permanentMark.contains(task))
      {
         return;
      }

      if (m_temporaryMark.contains(task))
      {
         // TODO: Can we break a cycle by ignoring relation where cycle detected?
         throw new CycleException();
      }

      m_temporaryMark.add(task);

      for (Relation relation : getSuccessors(task))
      {
         Task successorTask = relation.getSuccessorTask();
         if (m_includeTask.apply(successorTask).booleanValue())
         {
            visit(successorTask);
         }
      }

      m_temporaryMark.remove(task);
      m_permanentMark.add(task);
      m_tasks.push(task);
   }

   private final ProjectFile m_file;
   private final Function<Task, Boolean> m_includeTask;
   private final Deque<Task> m_tasks = new ArrayDeque<>();
   private final Set<Task> m_temporaryMark = new HashSet<>();
   private final Set<Task> m_permanentMark = new HashSet<>();
}
