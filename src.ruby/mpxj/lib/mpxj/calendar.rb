module MPXJ
  # Represents a task in a project plan
  class Calendar
   
   attr_reader :parent_project, :unique_id, :name, :base_calendar_name, :resource_name, :days,  :exceptions

    def initialize(parent_project, calendar_hash)
      @parent_project = parent_project
      @unique_id = calendar_hash['id']
      @name = calendar_hash['name']
      @base_calendar_name = calendar_hash['base_calendar_name']
      @resource_name = calendar_hash['resource']
      set_days(calendar_hash['days'])
      @exceptions = calendar_hash['exceptions']
    end

    def set_days(days_hash)
      @days = {}
      days_hash.each do |day_hash|
        @days[day_hash['name']] = {'type' => day_hash['type'], 'hours' => day_hash['hours']}
      end
    end
  end
end
