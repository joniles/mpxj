module MPXJ
  # Represents a relationship between two tasks in a project plan
  class Relation < Container
    # Retrieve the Task Unique ID value
    # <b>DEPRECATED:</b> Please use <tt>predecessor_task_unique_id</tt> or <tt>successor_task_unique_id</tt>instead.
    # 
    # @return Task Unique ID value
 		def task_unique_id
 			get_integer_value(attribute_values['task_unique_id'])
  	end

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

    # Retrieve the Type value
    #
    # @return Type value
  	def type
  		attribute_values['type']
  	end
  end
end
