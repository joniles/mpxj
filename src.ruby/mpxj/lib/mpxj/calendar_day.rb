module MPXJ
  # Represents a calendar day
  class CalendarDay < Container
    attr_reader :hours

    def initialize(parent_project, attribute_values)
      super(parent_project, attribute_values.slice('type'))
      process_hours(attribute_values)
    end

    # Retrieve the day type
    #
    # @return [String] the calendar day type
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
