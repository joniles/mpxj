module MPXJ
  # Represents a resource in a project plan
  class Resource < Container
  	include MPXJ::ResourceMethods

    attr_reader :assignments
    def initialize(parent_project, attribute_values)
      super(parent_project, attribute_values)
      @assignments = []
    end
  end
end
