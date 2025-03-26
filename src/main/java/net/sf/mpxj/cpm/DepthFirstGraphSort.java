package net.sf.mpxj.cpm;

import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Task;

public class DepthFirstGraphSort
{
   public DepthFirstGraphSort(ProjectFile file, Function<Task, Boolean> includeTask)
   {
      m_file = file;
      m_includeTask = includeTask;
   }

   public List<Task> sort() throws CycleException
   {
      try
      {
         for (Task task : m_file.getTasks())
         {
            if (m_includeTask.apply(task))
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

   public List<Relation> getSuccessors(Task task)
   {
      return task.getSuccessors();
   }

   private void visit(Task task) throws CycleException
   {
      if (m_permanentMark.contains(task))
      {
         return;
      }

      if (m_temporaryMark.contains(task))
      {
         // TODO: Break cycle by ignoring relation where cycle detected?
         throw new CycleException();
      }

      m_temporaryMark.add(task);

      for (Relation relation : getSuccessors(task))
      {
         Task successorTask = relation.getSuccessorTask();
         if (m_includeTask.apply(successorTask))
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
