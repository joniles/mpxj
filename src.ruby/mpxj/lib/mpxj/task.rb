module MPXJ
  # Represents a task in a project plan
  class Task < Container
    include MPXJ::TaskMethods

    attr_reader :assignments
    attr_reader :predecessors
    attr_reader :successors
    attr_reader :child_tasks

    def initialize(parent_project, attribute_values)
      super(parent_project, attribute_values)
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
      parent_project.get_task_by_unique_id(attribute_values['parent_task_unique_id']&.to_i)
    end

    # Retrieve the calendar used by this task
    #
    # @return [Calendar] task calendar
    # @return [nil] if this task does not have a calendar assigned
    def calendar
      parent_project.get_calendar_by_unique_id(attribute_values['calendar_unique_id']&.to_i)
    end

    private

    def process_relations
      @predecessors = process_relation_list(attribute_values["predecessors"])
      @successors = process_relation_list(attribute_values["successors"])
    end

    def process_relation_list(list)
      result = []
      if list
        list.each do |attribute_values|
          result << Relation.new(self, attribute_values)
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
