module MPXJ
  # Represents a calendar exception
  class CalendarException < Container
    attr_reader :hours

    def initialize(parent_project, attribute_values)
      super(parent_project, attribute_values.slice('name', 'from', 'to', 'type'))
      process_hours(attribute_values)
    end

    # Retrieve the exception name
    #
    # @return [String] the exception name
    def name
      attribute_values['name']
    end

    # Retrieve the date on which this exception starts
    #
    # @return [Time] the exception from date
    def from
      get_date_value(attribute_values['from'])
    end

    # Retrieve the date on which this exception ends
    #
    # @return [Time] the exception to date
    def to
      get_date_value(attribute_values['to'])
    end

    # Retrieve the exception type
    #
    # @return [String] the exception type
    def type
      attribute_values['type']
    end

    private

    def process_hours(attribute_values)
      @hours = (attribute_values['hours'] || {}).map do |hours|
        CalendarHours.new(parent_project, hours)
      end
    end
  end
end
