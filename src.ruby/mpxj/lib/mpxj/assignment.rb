module MPXJ
  class Assignment < Container
    def task
      parent_project.get_task_by_unique_id(task_unique_id)
    end

    def resource
      parent_project.get_resource_by_unique_id(resource_unique_id)
    end
  end
end
