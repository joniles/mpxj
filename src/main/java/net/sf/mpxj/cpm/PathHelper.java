package net.sf.mpxj.cpm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;

public class PathHelper
{
   public PathHelper(ProjectFile file)
   {
      m_file = file;
   }

   public List<List<Task>> getAllPaths(Function<Task, Boolean> ignoreTasks) throws CycleException
   {
      m_paths.clear();

      List<Task> tasks = new DepthFirstGraphSort(m_file, ignoreTasks).sort();
      if (tasks.isEmpty())
      {
         return m_paths;
      }

      for (Task task : tasks)
      {
         if (m_processedTasks.contains(task))
         {
            continue;
         }

         List<Task> path = new ArrayList<>();
         m_paths.add(path);
         processSuccessorPath(path, task);
      }

      return m_paths;
   }

   public List<List<Task>> getPathsFrom(Task task) throws CycleException
   {
      List<Task> path = new ArrayList<>();
      m_paths.clear();
      m_paths.add(path);
      processSuccessorPath(path, task);
      return m_paths;
   }

   public List<List<Task>> getPathsTo(Task task) throws CycleException
   {
      List<Task> path = new ArrayList<>();
      m_paths.clear();
      m_paths.add(path);
      processPredecessorPath(path, task);
      return m_paths;
   }

   private void processSuccessorPath(List<Task> path, Task task)
   {
      Duration d = Duration.getInstance(1, TimeUnit.DAYS);

      path.add(task);
      m_processedTasks.add(task);
      if (task.getSuccessors().isEmpty())
      {
         return;
      }

      List<Task> successors = task.getSuccessors().stream().map(r -> r.getSuccessorTask()).distinct().collect(Collectors.toList());

      if (successors.size() == 1)
      {
         processSuccessorPath(path, successors.get(0));
      }
      else
      {
         List<List<Task>> paths = new ArrayList<>();
         paths.add(path);

         for (int loop=1; loop < successors.size(); loop++)
         {
            List<Task> newPath = new ArrayList<>(path);
            paths.add(newPath);
            m_paths.add(newPath);
         }

         for (int index=0; index < successors.size(); index++)
         {
            processSuccessorPath(paths.get(index), successors.get(index));
         }
      }
   }

   private void processPredecessorPath(List<Task> path, Task task)
   {
      path.add(task);
      m_processedTasks.add(task);
      if (task.getPredecessors().isEmpty())
      {
         return;
      }

      List<Task> predecessors = task.getPredecessors().stream().map(r -> r.getPredecessorTask()).distinct().collect(Collectors.toList());

      if (predecessors.size() == 1)
      {
         processPredecessorPath(path, predecessors.get(0));
      }
      else
      {
         List<List<Task>> paths = new ArrayList<>();
         paths.add(path);

         for (int loop=1; loop < predecessors.size(); loop++)
         {
            List<Task> newPath = new ArrayList<>(path);
            paths.add(newPath);
            m_paths.add(newPath);
         }

         for (int index=0; index < predecessors.size(); index++)
         {
            processPredecessorPath(paths.get(index), predecessors.get(index));
         }
      }
   }

   private final ProjectFile m_file;
   private final Set<Task> m_processedTasks = new HashSet<>();
   private final List<List<Task>> m_paths = new ArrayList<>();
}
