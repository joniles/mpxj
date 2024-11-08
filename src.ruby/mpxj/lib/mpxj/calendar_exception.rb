module MPXJ
  # Represents a calendar exception
  class CalendarException < Container
    def initialize(parent_project, attribute_values)
      super(parent_project, attribute_values.slice('name', 'from', 'to', 'type'))
      # process_hours(attribute_values)
    end

    def name
      attribute_values['name']
    end

    def from
      get_date_value(attribute_values['from'])
    end

    def to
      get_date_value(attribute_values['to'])
    end

    def type
      attribute_values['type']
    end
  end
end
