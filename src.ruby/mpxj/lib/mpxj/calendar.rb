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

    def unique_id
      get_integer_value(attribute_values['unique_id'])
    end

    def guid
      attribute_values['guid']
    end

    def parent_unique_id
      get_nillable_integer_value(attribute_values['parent_unique_id'])
    end

    def name
      attribute_values['name']
    end

    def type
      attribute_values['type']
    end

    def personal
      get_boolean_value(attribute_values['personal'])
    end

    def minutes_per_day
      get_nillable_integer_value(attribute_values['minutes_per_day'])
    end

    def minutes_per_week
      get_nillable_integer_value(attribute_values['minutes_per_week'])
    end

    def minutes_per_month
      get_nillable_integer_value(attribute_values['minutes_per_month'])
    end

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
