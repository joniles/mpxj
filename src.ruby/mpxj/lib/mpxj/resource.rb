module MPXJ
  # Represents a resource in a project plan
  class Resource < Container
    attr_reader :assignments
    def initialize(parent_project, attribute_types, attribute_values)
      super(parent_project, attribute_types, attribute_values)
      @assignments = []
    end
  end
end
