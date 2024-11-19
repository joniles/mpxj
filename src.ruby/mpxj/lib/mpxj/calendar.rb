module MPXJ
  # Represents a calendar
  class Calendar < Container
    attr_reader :days
    attr_reader :weeks
    attr_reader :exceptions

    def initialize(parent_project, attribute_values)
      super(parent_project, attribute_values.slice('unique_id', 'guid', 'parent_unique_id', 'name', 'type', 'personal', 'minutes_per_day', 'minutes_per_week', 'minutes_per_month', 'minutes_per_year'))
      process_days(attribute_values)
      process_weeks(attribute_values)
      process_exceptions(attribute_values)
    end

    # Retrieve the calendar unique ID
    #
    # @return [Integer] the calendar unique ID
    def unique_id
      get_integer_value(attribute_values['unique_id'])
    end

    # Retrieve the calendar GUID
    #
    # @return [String] the calendar GUID
    def guid
      attribute_values['guid']
    end

    # Retrieve the parent calendar unique ID
    #
    # @return [Integer] the parent calendar unique ID
    # @return [nil] if the calendar does not have a parent
    def parent_unique_id
      get_nillable_integer_value(attribute_values['parent_unique_id'])
    end

    # Retrieve the parent calendar of this calendar
    #
    # @return [Calendar] if this calendar is the child of another calendar
    # @return [nil] if this is a base calendar
    def parent_calendar
      parent_project.get_calendar_by_unique_id(attribute_values['parent_unique_id']&.to_i)
    end

    # Retrieve the calendar name
    #
    # @return [String] the calendar name
    def name
      attribute_values['name']
    end

    # Retrieve the calendar type
    #
    # @return [String] the calendar type
    def type
      attribute_values['type']
    end

    # Retrieve the personal flag
    #
    # @return [Boolean] true if this is a personal calendar
    def personal
      get_boolean_value(attribute_values['personal'])
    end

    # Retrieve the number of minutes per day
    #
    # @return [Integer] the number of minutes per day
    # @return [nil] if this calendar does not provide a value for minutes per day
    def minutes_per_day
      get_nillable_integer_value(attribute_values['minutes_per_day'])
    end

    # Retrieve the number of minutes per week
    #
    # @return [Integer] the number of minutes per week
    # @return [nil] if this calendar does not provide a value for minutes per week
    def minutes_per_week
      get_nillable_integer_value(attribute_values['minutes_per_week'])
    end

    # Retrieve the number of minutes per month
    #
    # @return [Integer] the number of minutes per month
    # @return [nil] if this calendar does not provide a value for minutes per month
    def minutes_per_month
      get_nillable_integer_value(attribute_values['minutes_per_month'])
    end

    # Retrieve the number of minutes per year
    #
    # @return [Integer] the number of minutes per year
    # @return [nil] if this calendar does not provide a value for minutes per year
    def minutes_per_year
      get_nillable_integer_value(attribute_values['minutes_per_year'])
    end

  private

    def process_days(attribute_values)
      @days = attribute_values.slice('sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday').map {|name, day| [name, CalendarDay.new(parent_project, day)]}.to_h
    end

    def process_weeks(attribute_values)
      @weeks = (attribute_values['working_weeks'] || []).map {|week| CalendarWeek.new(parent_project, week)}
    end

    def process_exceptions(attribute_values)
      @exceptions = (attribute_values['exceptions'] || []).map {|exception| CalendarException.new(parent_project, exception)}
    end
  end
end
