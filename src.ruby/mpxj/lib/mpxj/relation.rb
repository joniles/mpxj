module MPXJ
  # Represents a relationship between two tasks in a project plan
  class Relation < Container
    # Retrieve the Task Unique ID value
    #
    # @return Task Unique ID value
 		def task_unique_id
 			get_integer_value(attribute_values['task_unique_id'])
  	end

    # Retrieve the Lag value
    #
    # @return Lag value
  	def lag
  		get_duration_value(attribute_values['lag'])
  	end

    # Retrieve the Type value
    #
    # @return Type value
  	def type
  		attribute_values['type']
  	end
  end
end
