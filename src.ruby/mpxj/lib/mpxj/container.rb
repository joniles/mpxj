require 'duration'
require 'time'

module MPXJ
  # Base class from which all project entities are derived
  class Container
    attr_reader :parent_project
    def initialize(parent_project, attribute_types, attribute_values)
      @parent_project = parent_project
      @attribute_types = attribute_types
      @attribute_values = attribute_values
    end

    def method_missing(name, *args, &block)
      # We can probably do this more efficiently with dynamic methods... but let's get some feedback first!
      attribute_name = name.to_s
      attribute_type = @attribute_types[attribute_name]
      attribute_value = @attribute_values[attribute_name]

      if attribute_type.nil? && attribute_value.nil?
        super
      else
        if attribute_type.nil?
          attribute_type = 1
        end
        get_attribute_value(attribute_type, attribute_value)
      end
    end

    protected

    attr_reader :attribute_values

    private

    def get_attribute_value(attribute_type, attribute_value)
      case attribute_type.to_i
      when 12, 17, 19
        get_integer_value(attribute_value)
      when 8, 3, 5, 7
        get_float_value(attribute_value)
      when 2
        get_date_value(attribute_value)
      when 6, 16
        get_duration_value(attribute_value)
      when 4
        get_boolean_value(attribute_value)
      else
        attribute_value
      end
    end

    def get_duration_value(attribute_value)
      if attribute_value.nil?
        Duration.new(0)
      else
        Duration.new(attribute_value.to_i)
      end
    end

    def get_date_value(attribute_value)
      if attribute_value.nil?
        nil
      else
        @parent_project.zone.parse(attribute_value)
      end
    end

    def get_float_value(attribute_value)
      if attribute_value.nil?
        0.0
      else
        attribute_value.to_f
      end
    end

    def get_integer_value(attribute_value)
      if attribute_value.nil?
        0
      else
        attribute_value.to_i
      end
    end

    def get_boolean_value(attribute_value)
      attribute_value == true
    end
  end
end
