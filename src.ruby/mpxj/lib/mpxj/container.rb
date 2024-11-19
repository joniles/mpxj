require 'time'

module MPXJ
  # Base class from which all project entities are derived
  class Container
    attr_reader :parent_project
    def initialize(parent_project, attribute_values)
      @parent_project = parent_project
      @attribute_values = attribute_values
    end

    attr_reader :attribute_values

    private

    def get_duration_value(attribute_value)
      if attribute_value.nil?
        0.0
      else
        attribute_value.to_f
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

    def get_nillable_integer_value(attribute_value)
      if attribute_value.nil?
        nil
      else
        attribute_value.to_i
      end
    end

    def get_boolean_value(attribute_value)
      attribute_value == true
    end
  end
end
