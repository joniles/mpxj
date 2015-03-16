module MPXJ
  class Resource < Container
    attr_reader :assignments
    def initialize(parent_project, attribute_types, attribute_values)
      super(parent_project, attribute_types, attribute_values)
      @assignments = []
    end
  end
end
