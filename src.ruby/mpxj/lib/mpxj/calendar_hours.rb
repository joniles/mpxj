module MPXJ
  # Represents a range of hours
  class CalendarHours < Container

    # Retrieve the the start hour
    #
    # @return [Time] start hour
    def from
      get_date_value(attribute_values['from'])
    end

    # Retrieve the the finish hour
    #
    # @return [Time] finish hour
    def to
      get_date_value(attribute_values['to'])
    end
  end
end
