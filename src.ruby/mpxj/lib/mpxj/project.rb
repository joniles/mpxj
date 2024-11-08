require 'json'

module MPXJ
  # Represents a project plan
  class Project
    attr_reader :properties
    attr_reader :all_calendars
    attr_reader :all_resources
    attr_reader :all_tasks
    attr_reader :child_tasks
    attr_reader :all_assignments
    attr_reader :zone

    def initialize(file_name, zone)
      @calendars_by_unique_id = {}
      @resources_by_unique_id = {}
      @tasks_by_unique_id = {}

      @resources_by_id = {}      
      @tasks_by_id = {}

      @all_calendars = []
      @all_resources = []
      @all_tasks = []
      @all_assignments = []
      @child_tasks = []

      @zone = zone

      @field_by_alias = {}
      @alias_by_field = {}

      file = File.read(file_name)
      json_data = JSON.parse(file)
      process_calendars(json_data)
      process_custom_fields(json_data)
      process_properties(json_data)
      process_resources(json_data)
      process_tasks(json_data)
      process_assignments(json_data)
    end

    # Retrieves the calendar with the matching unique_id attribute
    #
    # @param unique_id [Integer] calendar unique ID
    # @return [Calendar] if the requested calendar is found
    # @return [nil] if the requested calendar is not found
    def get_calendar_by_unique_id(unique_id)
      @calendars_by_unique_id[unique_id]
    end

    # Retrieves the resource with the matching unique_id attribute
    #
    # @param unique_id [Integer] resource unique ID
    # @return [Resource] if the requested resource is found
    # @return [nil] if the requested resource is not found
    def get_resource_by_unique_id(unique_id)
      @resources_by_unique_id[unique_id]
    end

    # Retrieves the task with the matching unique_id attribute
    #
    # @param unique_id [Integer] task unique ID
    # @return [Task] if the requested task is found
    # @return [nil] if the requested task is not found
    def get_task_by_unique_id(unique_id)
      @tasks_by_unique_id[unique_id]
    end

    # Retrieves the resource with the matching id attribute
    #
    # @param id [Integer] resource ID
    # @return [Resource] if the requested resource is found
    # @return [nil] if the requested resource is not found
    def get_resource_by_id(id)
      @resources_by_id[id]
    end

    # Retrieves the task with the matching id attribute
    #
    # @param id [Integer] task ID
    # @return [Task] if the requested task is found
    # @return [nil] if the requested task is not found
    def get_task_by_id(id)
      @tasks_by_id[id]
    end

    # For a particular entity type (task, resource, and so on), retrieve
    # the field which has the supplied alias. For example this allows the caller to
    # answer the question "which task field is using the alias `Activity ID`"
    #
    # @param field_type_class[String] field type (possible values: task, resource, assignment, constraint, project)
    # @param field_alias[String] the alias we want to look up
    # @return [String] if the alias has been found return the name of the underlying field
    # @return [nil] if the alias is not in use
    def get_field_by_alias(field_type_class, field_alias)
      hash = @field_by_alias[field_type_class]
      if hash
        hash[field_alias]
      end
    end

    # For a particular entity type (task, resource, and so on), retrieve
    # the alias used by the supplied field. For example this allows the caller to
    # answer the question "does the task field Text1 have an alias?"
    #
    # @param field_type_class[String] field type (possible values: task, resource, assignment, constraint, project)
    # @param field_type[String] the field type we want to look up
    # @return [String] if the field has an alias, return the alias
    # @return [nil] if the field does not have an alias
    def get_alias_by_field(field_type_class, field_type)
      hash = @alias_by_field[field_type_class]
      if hash
        hash[field_type]
      end
    end

    private

    def process_calendars(json_data)
      calendars = json_data["calendars"]
      calendars.each do |attribute_values|
        calendar = Calendar.new(self, attribute_values)
        @all_calendars << calendar
        @calendars_by_unique_id[calendar.unique_id] = calendar
      end
    end

    def process_custom_fields(json_data)
      custom_fields = json_data["custom_fields"] || []
      custom_fields.each do |field|
        process_custom_field(field)
      end
    end

    def process_custom_field(field)
      field_type_class = field["field_type_class"]
      field_type = field["field_type"]
      field_alias = field["field_alias"]

      process_custom_field_hash(@field_by_alias, field_type_class, field_alias, field_type)
      process_custom_field_hash(@alias_by_field, field_type_class, field_type, field_alias)
    end

    def process_custom_field_hash(hash, key1, key2, value)
      key1_hash = hash[key1]
      unless key1_hash
        key1_hash = {}
        hash[key1] = key1_hash
      end
      key1_hash[key2] = value
    end

    def process_properties(json_data)
      @properties = Properties.new(self, json_data["property_values"])
    end

    def process_resources(json_data)
      resources = json_data["resources"]
      resources.each do |attribute_values|
        resource = Resource.new(self, attribute_values)
        @all_resources << resource
        @resources_by_unique_id[resource.unique_id] = resource
        @resources_by_id[resource.id] = resource
      end
    end

    def process_tasks(json_data)
      tasks = json_data["tasks"]
      tasks.each do |attribute_values|
        task = Task.new(self, attribute_values)
        @all_tasks << task
        @tasks_by_unique_id[task.unique_id] = task
        @tasks_by_id[task.id] = task
      end
    end

    def process_assignments(json_data)
      assignments = json_data["assignments"]
      assignments.each do |attribute_values|
        assignment = Assignment.new(self, attribute_values)
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
