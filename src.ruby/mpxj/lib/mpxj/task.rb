module MPXJ
  # Represents a task in a project plan
  class Task < Container
    attr_reader :assignments
    attr_reader :predecessors
    attr_reader :successors
    attr_reader :child_tasks

    def initialize(parent_project, attribute_types, attribute_values)
      super(parent_project, attribute_types, attribute_values)
      @assignments = []
      @child_tasks = []
      process_relations
      process_hierarchy
    end

    # Retrieve the parent task of this task
    #
    # @return [Task] if this task is the child of another task
    # @return [nil] if this is the root task
    def parent_task
      parent_project.get_task_by_unique_id(parent_task_unique_id)
    end

    private

    RELATION_ATTRIBUTE_TYPES = {"task_unique_id" => 17, "lag" => 6, "type" => 10}

    def process_relations
      @predecessors = process_relation_list(attribute_values["predecessors"])
      @successors = process_relation_list(attribute_values["successors"])
    end

    def process_relation_list(list)
      result = []
      if list
        list.each do |attribute_values|
          result << Relation.new(self, RELATION_ATTRIBUTE_TYPES, attribute_values)
        end
      end
      result
    end

    def process_hierarchy
      if parent_task
        parent_task.child_tasks << self
      else
        parent_project.child_tasks << self
      end
    end
  end
end
