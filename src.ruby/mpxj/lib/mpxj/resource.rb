module MPXJ
  # Represents a resource in a project plan
  class Resource < Container
  	include MPXJ::ResourceMethods

    attr_reader :assignments
    def initialize(parent_project, attribute_values)
      super(parent_project, attribute_values)
      @assignments = []
    end

    # Retrieve the calendar used by this resource
    #
    # @return [Calendar] resource calendar
    # @return [nil] if this resource does not have a calendar
    def calendar
      parent_project.get_calendar_by_unique_id(attribute_values['calendar_unique_id']&.to_i)
    end
  end
end
