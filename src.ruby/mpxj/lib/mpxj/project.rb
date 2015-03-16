require 'json'

module MPXJ
  class Project
    attr_reader :all_resources
    attr_reader :all_tasks
    attr_reader :child_tasks
    attr_reader :all_assignments
    def initialize(file_name)
      @resources_by_unique_id = {}
      @tasks_by_unique_id = {}

      @resources_by_id = {}
      @tasks_by_id = {}

      @all_resources = []
      @all_tasks = []
      @all_assignments = []
      @child_tasks = []

      file = File.read(file_name)
      json_data = JSON.parse(file)
      process_resources(json_data)
      process_tasks(json_data)
      process_assignments(json_data)
    end

    def get_resource_by_unique_id(unique_id)
      @resources_by_unique_id[unique_id]
    end

    def get_task_by_unique_id(unique_id)
      @tasks_by_unique_id[unique_id]
    end

    def get_resource_by_id(id)
      @resources_by_id[id]
    end

    def get_task_by_id(id)
      @tasks_by_unique_id[id]
    end

    private

    def process_resources(json_data)
      attribute_types = json_data["resource_types"]
      resources = json_data["resources"]
      resources.each do |attribute_values|
        resource = Resource.new(self, attribute_types, attribute_values)
        @all_resources << resource
        @resources_by_unique_id[resource.unique_id] = resource
        @resources_by_id[resource.id] = resource
      end
    end

    def process_tasks(json_data)
      attribute_types = json_data["task_types"]
      tasks = json_data["tasks"]
      tasks.each do |attribute_values|
        task = Task.new(self, attribute_types, attribute_values)
        @all_tasks << task
        @tasks_by_unique_id[task.unique_id] = task
        @tasks_by_id[task.id] = task
      end
    end

    def process_assignments(json_data)
      attribute_types = json_data["assignment_types"]
      assignments = json_data["assignments"]
      assignments.each do |attribute_values|
        assignment = Assignment.new(self, attribute_types, attribute_values)
        @all_assignments << assignment
        if assignment.task
          assignment.task.assignments << assignment
        end
        if assignment.resource
          assignment.resource.assignments << assignment
        end
      end
    end
  end
end
