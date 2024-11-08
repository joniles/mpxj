module MPXJ
  # Represents a range of hours
  class CalendarWeek < Container
    attr_reader :days

    def initialize(parent_project, attribute_values)
      super(parent_project, attribute_values.slice('name', 'effective_from', 'effective_to'))
      process_days(attribute_values)
    end

    def name
      attribute_values['name']
    end

    def effective_from
      get_date_value(attribute_values['effective_from'])
    end

    def effective_to
      get_date_value(attribute_values['effective_to'])
    end

    private

    def process_days(attribute_values)
      @days = attribute_values.slice('sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday').map {|name, day| [name, CalendarDay.new(parent_project, day)]}.to_h
    end
  end
end
