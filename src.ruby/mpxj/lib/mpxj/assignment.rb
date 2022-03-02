module MPXJ
  # Represents a relationship between a task and a resource in a project plan
  class Assignment < Container
    include MPXJ::AssignmentMethods

    # Retrieve the task associated with this assignment
    #
    # @return [Task] the task associated with this assignment.
    def task
      parent_project.get_task_by_unique_id(task_unique_id)
    end

    # Retrieve the resource associated with this assignment
    #
    # @return [Resource] the resource associated with this assignment.
    def resource
      parent_project.get_resource_by_unique_id(resource_unique_id)
    end
  end
end
