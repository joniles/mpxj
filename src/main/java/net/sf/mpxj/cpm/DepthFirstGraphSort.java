package net.sf.mpxj.cpm;

import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.mpxj.ActivityType;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Task;

public class DepthFirstGraphSort
{
   public DepthFirstGraphSort(ProjectFile file)
   {
      m_file = file;
   }

   public List<Task> sort() throws CycleException
   {
      try
      {
         for (Task task : m_file.getTasks())
         {
            // TODO: consider how MS Project schedules summary tasks with dependencies
            if (task.getSummary())
            {
               continue;
            }

            // Ignore inactive tasks
            if (!task.getActive())
            {
               continue;
            }

            // Ignore null tasks
            if (task.getNull())
            {
               continue;
            }

            // Ignore probably null tasks
            if (task.getName() == null)
            {
               continue;
            }

            // Ignore level of effort tasks - TODO: determine how Early/Late values are calculated from dependent activities
            if (task.getActivityType() == ActivityType.LEVEL_OF_EFFORT)
            {
               continue;
            }

            // Ignore WBS Summary activities
            if (task.getActivityType() == ActivityType.WBS_SUMMARY)
            {
               continue;
            }

            visit(task);
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

   private void visit(Task task) throws CycleException
   {
      if (m_permanentMark.contains(task))
      {
         return;
      }

      if (m_temporaryMark.contains(task))
      {
         // Break cycle by ignoring relation where cycle detected?
         throw new CycleException();
      }

      m_temporaryMark.add(task);

      for (Relation relation : m_file.getRelations().getRawSuccessors(task))
      {
         visit(relation.getSourceTask());
      }

      m_temporaryMark.remove(task);
      m_permanentMark.add(task);
      m_tasks.push(task);
   }

   private final ProjectFile m_file;
   private final Deque<Task> m_tasks = new ArrayDeque<>();
   private final Set<Task> m_temporaryMark = new HashSet<>();
   private final Set<Task> m_permanentMark = new HashSet<>();
}
