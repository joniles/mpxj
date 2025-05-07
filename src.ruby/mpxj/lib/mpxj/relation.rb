module MPXJ
  # Represents a relationship between two tasks in a project plan
  class Relation < Container
    # Retrieve the Predecessor Task Unique ID value
    #
    # @return Predecessor Task Unique ID value
    def predecessor_task_unique_id
      get_integer_value(attribute_values['predecessor_task_unique_id'])
    end

    # Retrieve the Successor Task Unique ID value
    #
    # @return Successor Task Unique ID value
    def successor_task_unique_id
      get_integer_value(attribute_values['successor_task_unique_id'])
    end

    # Retrieve the Lag value
    #
    # @return Lag value
  	def lag
  		get_duration_value(attribute_values['lag'])
  	end

    # Retrieve the Lag Units value
    #
    # @return Lag Units value
    def lag_units
      attribute_values['lag_units']
    end

    # Retrieve the Type value
    #
    # @return Type value
  	def type
  		attribute_values['type']
  	end
  end
end
