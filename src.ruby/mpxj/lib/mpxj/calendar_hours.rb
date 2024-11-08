module MPXJ
  # Represents a range of hours
  class CalendarHours < Container
    def from
      get_date_value(attribute_values['from'])
    end

    def to
      get_date_value(attribute_values['to'])
    end
  end
end
