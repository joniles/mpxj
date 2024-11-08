module MPXJ
  # Represents the properties of a project
  class Properties < Container
  	include MPXJ::PropertyMethods

    # Retrieve the default calendar for this project
    #
    # @return [Calendar] default calendar
    def default_calendar
      parent_project.get_calendar_by_unique_id(attribute_values['default_calendar_unique_id']&.to_i)
    end
  end
end
