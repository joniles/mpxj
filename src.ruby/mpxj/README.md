# MPXJ

This gem allows a Ruby developer to work with a read-only view of project plans saved by a number of popular project planning applications.
The work required to read data from these files is actually carried out by a [Java library](http://mpxj.sf.net), hence you will need Java installed
in order to work with this gem. Once the project data has been read from a file, a set of Ruby objects provides access to the
structure of the project plan and its attributes. 

## Installation

Add this line to your application's Gemfile:

	gem 'mpxj'

And then execute:

    $ bundle

Or install it yourself as:

    $ gem install mpxj

## Supported File Types

This gem uses the file name extension to determine what kind of project data it is reading. The list below shows the supported file extensions:

* **MPP** - Microsoft Project MPP file
* **MPT** - Microsoft Project template file
* **MPX** - Microsoft Project MPX file
* **XML** - Microsoft Project MSPDI (XML) file
* **MPD** - Microsoft Project database (only when the gem is used on Microsoft Windows)
* **PLANNER** - Gnome Planner
* **XER** - Primavera XER file
* **PMXML** - Primavera PMXML file
* **PP** - Asta Powerproject file
 
## Example Code
 
The following is a trivial example showing some basic task and resource details being queried from a project:


	project = MPXJ::Reader.read("project1.mpp")
	
	puts "There are #{project.all_tasks.size} tasks in this project"
	puts "There are #{project.all_resources.size} resources in this project"
	
	puts "The resources are:"
	project.all_resources.each do |resource|
	  puts resource.name
	end
	
	puts "The tasks are:"
	project.all_tasks.each do |task|
	  puts "#{task.name}: starts on #{task.start}, finishes on #{task.finish}, it's duration is #{task.duration}"
	end 

## Entities

The gem represents the project plan using the following classes, all of which reside in the MPXJ module.

* Project
* Resource
* Task
* Assignment
* Relation

A **Project** contains **Resource**s and **Task**s. Each **Resource** can be **Assigned** to one ore more **Task**s. 
**Task**s can have dependencies between them which are represented as **Relation**s.

 
## Methods, Attributes and Data Types

There are very few explicit methods implemented by the classes noted above. Access to the attributes of each class is provided via a `method_missing` handler which checks to see if the requested method name matches a known attribute name. If it does match, the attribute value is returned, otherwise the normal method missing exception is raised.

The methods defined explicitly by these classes are:

	Project#all_resources
	Project#all_tasks
	Project#child_tasks
	Project#all_assignments
	Project#get_resource_by_unique_id(unique_id)
	Project#get_task_by_unique_id(unique_id)
	Project#get_resource_by_id(id)
	Project#get_task_by_id(id)
	
	Resource#parent_project
	Resource#assignments
	
	Task#parent_project
	Task#assignments
	Task#predecessors
	Task#successors
	Task#child_tasks
	Task#parent_task
	
	Assignment#parent_project
	Assignment#task
	Assignment#resource

Each attribute supported by these classes is represented by appropriate data types:

* String
* Duration [https://rubygems.org/gems/duration](https://rubygems.org/gems/duration)
* Time 
* Integer
* Float

### Resource Attributes

	Resource#accrue_at
	Resource#active
	Resource#actual_cost
	Resource#actual_finish
	Resource#actual_overtime_cost
	Resource#actual_overtime_work
	Resource#actual_overtime_work_protected
	Resource#actual_start
	Resource#actual_work
	Resource#actual_work_protected
	Resource#acwp
	Resource#assignment
	Resource#assignment_delay
	Resource#assignment_owner
	Resource#assignment_units
	Resource#availability_data
	Resource#available_from
	Resource#available_to
	Resource#base_calendar
	Resource#baseline1_budget_cost
	Resource#baseline1_budget_work
	Resource#baseline1_cost
	Resource#baseline1_finish
	Resource#baseline1_start
	Resource#baseline1_work
	...	
	Resource#baseline10_budget_cost
	Resource#baseline10_budget_work
	Resource#baseline10_cost
	Resource#baseline10_finish
	Resource#baseline10_start
	Resource#baseline10_work	
	Resource#baseline_budget_cost
	Resource#baseline_budget_work
	Resource#baseline_cost
	Resource#baseline_finish
	Resource#baseline_start
	Resource#baseline_work
	Resource#bcwp
	Resource#bcws
	Resource#booking_type
	Resource#budget
	Resource#budget_cost
	Resource#budget_work
	Resource#calendar_guid
	Resource#can_level
	Resource#code
	Resource#confirmed
	Resource#cost
	Resource#cost1
	...
	Resource#cost10
	Resource#cost_center
	Resource#cost_per_use
	Resource#cost_rate_a
	Resource#cost_rate_b
	Resource#cost_rate_c
	Resource#cost_rate_d
	Resource#cost_rate_e
	Resource#cost_rate_table
	Resource#cost_variance
	Resource#created
	Resource#cv
	Resource#date1
	...
	Resource#date10
	Resource#default_assignment_owner
	Resource#duration1
	...
	Resource#duration10
	Resource#duration1_units
	...
	Resource#duration10_units
	Resource#email_address
	Resource#enterprise
	Resource#enterprise_base_calendar
	Resource#enterprise_checked_out_by
	Resource#enterprise_cost1
	...
	Resource#enterprise_cost10
	Resource#enterprise_custom_field1
	...
	Resource#enterprise_custom_field50
	Resource#enterprise_data
	Resource#enterprise_date1
	...
	Resource#enterprise_date30
	Resource#enterprise_duration1
	...
	Resource#enterprise_duration10	
	Resource#enterprise_duration1_units
	...
	Resource#enterprise_duration10_units
	Resource#enterprise_flag1
	...
	Resource#enterprise_flag20
	Resource#enterprise_is_checked_out
	Resource#enterprise_last_modified_date
	Resource#enterprise_multi_value20
	...
	Resource#enterprise_multi_value29
	Resource#enterprise_name_used
	Resource#enterprise_number1
	...
	Resource#enterprise_number40
	Resource#enterprise_outline_code1
	...
	Resource#enterprise_outline_code29
	Resource#enterprise_rbs
	Resource#enterprise_required_values
	Resource#enterprise_team_member
	Resource#enterprise_text1
	...
	Resource#enterprise_text40
	Resource#enterprise_unique_id
	Resource#error_message
	Resource#finish
	Resource#finish1
	...
	Resource#finish10
	Resource#flag1
	...
	Resource#flag20
	Resource#generic
	Resource#group
	Resource#group_by_summary
	Resource#guid
	Resource#hyperlink
	Resource#hyperlink_address
	Resource#hyperlink_data
	Resource#hyperlink_href
	Resource#hyperlink_screen_tip
	Resource#hyperlink_subaddress
	Resource#id
	Resource#import
	Resource#inactive
	Resource#index
	Resource#indicators
	Resource#initials
	Resource#leveling_delay
	Resource#linked_fields
	Resource#material_label
	Resource#max_units
	Resource#name
	Resource#notes
	Resource#number1
	...
	Resource#number20
	Resource#objects
	Resource#outline_code1
	...
	Resource#outline_code10	
	Resource#outline_code1_index
	...
	Resource#outline_code10_index
	Resource#overallocated
	Resource#overtime_cost
	Resource#overtime_rate
	Resource#overtime_rate_units
	Resource#overtime_work
	Resource#peak
	Resource#percent_work_complete
	Resource#phonetics
	Resource#project
	Resource#regular_work
	Resource#remaining_cost
	Resource#remaining_overtime_cost
	Resource#remaining_overtime_work
	Resource#remaining_work
	Resource#request_demand
	Resource#response_pending
	Resource#standard_rate
	Resource#standard_rate_units
	Resource#start
	Resource#start1
	...
	Resource#start10
	Resource#subproject_resource_unique_id
	Resource#summary
	Resource#sv
	Resource#task_outline_number
	Resource#task_summary_name
	Resource#team_assignment_pool
	Resource#teamstatus_pending
	Resource#text1
	...
	Resource#text30
	Resource#type
	Resource#unavailable
	Resource#unique_id
	Resource#update_needed
	Resource#vac
	Resource#wbs
	Resource#windows_user_account
	Resource#work
	Resource#work_contour
	Resource#work_variance
	Resource#workgroup

### Task Attributes

	Task#active
	Task#actual_cost
	Task#actual_duration
	Task#actual_duration_units
	Task#actual_finish
	Task#actual_overtime_cost
	Task#actual_overtime_work
	Task#actual_overtime_work_protected
	Task#actual_start
	Task#actual_work
	Task#actual_work_protected
	Task#acwp
	Task#assignment
	Task#assignment_delay
	Task#assignment_owner
	Task#assignment_units
	Task#baseline1_budget_cost
	Task#baseline1_budget_work
	Task#baseline1_cost
	Task#baseline1_deliverable_finish
	Task#baseline1_deliverable_start
	Task#baseline1_duration
	Task#baseline1_duration_estimated
	Task#baseline1_duration_units
	Task#baseline1_estimated_duration
	Task#baseline1_estimated_finish
	Task#baseline1_estimated_start
	Task#baseline1_finish
	Task#baseline1_fixed_cost
	Task#baseline1_fixed_cost_accrual
	Task#baseline1_start
	Task#baseline1_work
	...		
	Task#baseline10_budget_cost
	Task#baseline10_budget_work
	Task#baseline10_cost
	Task#baseline10_deliverable_finish
	Task#baseline10_deliverable_start
	Task#baseline10_duration
	Task#baseline10_duration_estimated
	Task#baseline10_duration_units
	Task#baseline10_estimated_duration
	Task#baseline10_estimated_finish
	Task#baseline10_estimated_start
	Task#baseline10_finish
	Task#baseline10_fixed_cost
	Task#baseline10_fixed_cost_accrual
	Task#baseline10_start
	Task#baseline10_work
	Task#baseline_budget_cost
	Task#baseline_budget_work
	Task#baseline_cost
	Task#baseline_deliverable_finish
	Task#baseline_deliverable_start
	Task#baseline_duration
	Task#baseline_duration_estimated
	Task#baseline_duration_units
	Task#baseline_estimated_duration
	Task#baseline_estimated_finish
	Task#baseline_estimated_start
	Task#baseline_finish
	Task#baseline_fixed_cost
	Task#baseline_fixed_cost_accrual
	Task#baseline_start
	Task#baseline_work
	Task#bcwp
	Task#bcws
	Task#budget_cost
	Task#budget_work
	Task#calendar
	Task#calendar_unique_id
	Task#complete_through
	Task#confirmed
	Task#constraint_date
	Task#constraint_type
	Task#contact
	Task#cost
	Task#cost1
	...
	Task#cost10
	Task#cost_rate_table
	Task#cost_variance
	Task#cpi
	Task#created
	Task#critical
	Task#cv
	Task#cvpercent
	Task#date1
	...
	Task#date10
	Task#deadline
	Task#deliverable_finish
	Task#deliverable_guid
	Task#deliverable_name
	Task#deliverable_start
	Task#deliverable_type
	Task#duration
	Task#duration1
	...
	Task#duration10
	Task#duration1_estimated
	...
	Task#duration10_estimated
	Task#duration1_units
	...
	Task#duration10_units
	Task#duration_text
	Task#duration_units
	Task#duration_variance
	Task#eac
	Task#early_finish
	Task#early_start
	Task#earned_value_method
	Task#effort_driven
	Task#enterprise_cost1
	...
	Task#enterprise_cost10
	Task#enterprise_custom_field1
	...
	Task#enterprise_custom_field50
	Task#enterprise_data
	Task#enterprise_date1
	...
	Task#enterprise_date30
	Task#enterprise_duration1
	...
	Task#enterprise_duration10
	Task#enterprise_duration1_units
	...
	Task#enterprise_duration10_units
	Task#enterprise_flag1
	...
	Task#enterprise_flag20
	Task#enterprise_number1
	...
	Task#enterprise_number40
	Task#enterprise_outline_code1
	...
	Task#enterprise_outline_code30
	Task#enterprise_project_cost1
	...
	Task#enterprise_project_cost10
	Task#enterprise_project_date1
	...
	Task#enterprise_project_date30
	Task#enterprise_project_duration1
	...
	Task#enterprise_project_duration10
	Task#enterprise_project_flag1
	...
	Task#enterprise_project_flag20
	Task#enterprise_project_number1
	...
	Task#enterprise_project_number40
	Task#enterprise_project_outline_code1
	...
	Task#enterprise_project_outline_code30
	Task#enterprise_project_text1
	...
	Task#enterprise_project_text40
	Task#enterprise_text1
	...
	Task#enterprise_text40
	Task#error_message
	Task#estimated
	Task#external_task
	Task#finish
	Task#finish1
	...
	Task#finish10
	Task#finish_slack
	Task#finish_text
	Task#finish_variance
	Task#fixed_cost
	Task#fixed_cost_accrual
	Task#fixed_duration
	Task#flag1
	...
	Task#flag20
	Task#free_slack
	Task#group_by_summary
	Task#guid
	Task#hide_bar
	Task#hyperlink
	Task#hyperlink_address
	Task#hyperlink_data
	Task#hyperlink_href
	Task#hyperlink_screen_tip
	Task#hyperlink_subaddress
	Task#id
	Task#ignore_resource_calendar
	Task#ignore_warnings
	Task#index
	Task#indicators
	Task#is_duration_valid
	Task#is_finish_valid
	Task#is_start_valid
	Task#late_finish
	Task#late_start
	Task#level_assignments
	Task#leveling_can_split
	Task#leveling_delay
	Task#leveling_delay_units
	Task#linked_fields
	Task#manual_duration
	Task#manual_duration_units
	Task#marked
	Task#milestone
	Task#name
	Task#notes
	Task#number1
	...
	Task#number20
	Task#outline_code1
	...
	Task#outline_code10
	Task#outline_code1_index
	...
	Task#outline_code10_index
	Task#outline_level
	Task#outline_number
	Task#overallocated
	Task#overtime_cost
	Task#overtime_work
	Task#parent_task
	Task#parent_task_unique_id
	Task#path_driven_successor
	Task#path_driving_predecessor
	Task#path_predecessor
	Task#path_successor
	Task#peak
	Task#percent_complete
	Task#percent_work_complete
	Task#physical_percent_complete
	Task#placeholder
	Task#predecessors
	Task#preleveled_finish
	Task#preleveled_start
	Task#priority
	Task#project
	Task#publish
	Task#recalc_outline_codes
	Task#recurring
	Task#recurring_data
	Task#regular_work
	Task#remaining_cost
	Task#remaining_duration
	Task#remaining_overtime_cost
	Task#remaining_overtime_work
	Task#remaining_work
	Task#request_demand
	Task#resource_enterprise_multi_value_code20
	...
	Task#resource_enterprise_multi_value_code29
	Task#resource_enterprise_outline_code1
	...
	Task#resource_enterprise_outline_code29
	Task#resource_enterprise_rbs
	Task#resource_group
	Task#resource_initials
	Task#resource_names
	Task#resource_phonetics
	Task#resource_type
	Task#response_pending
	Task#resume
	Task#resume_no_earlier_than
	Task#rollup
	Task#scheduled_duration
	Task#scheduled_finish
	Task#scheduled_start
	Task#spi
	Task#start
	Task#start1
	...
	Task#start10
	Task#start_slack
	Task#start_text
	Task#start_variance
	Task#status
	Task#status_indicator
	Task#status_manager
	Task#stop
	Task#subproject_file
	Task#subproject_read_only
	Task#subproject_task_id
	Task#subproject_tasks_uniqueid_offset
	Task#subproject_unique_task_id
	Task#successors
	Task#summary
	Task#summary_progress
	Task#sv
	Task#svpercent
	Task#task_calendar
	Task#task_calendar_guid
	Task#task_mode
	Task#tcpi
	Task#teamstatus_pending
	Task#text1
	...
	Task#text30
	Task#total_slack
	Task#type
	Task#unavailable
	Task#unique_id
	Task#unique_id_predecessors
	Task#unique_id_successors
	Task#update_needed
	Task#vac
	Task#warning
	Task#wbs
	Task#wbs_predecessors
	Task#wbs_successors
	Task#work
	Task#work_contour
	Task#work_variance
		
### Assignment Attributes

	Assignment#actual_cost
	Assignment#actual_finish
	Assignment#actual_overtime_cost
	Assignment#actual_overtime_work
	Assignment#actual_overtime_work_protected
	Assignment#actual_start
	Assignment#actual_work
	Assignment#actual_work_protected
	Assignment#acwp
	Assignment#assignment_delay
	Assignment#assignment_resource_guid
	Assignment#assignment_task_guid
	Assignment#assignment_units
	Assignment#baseline1_budget_cost
	Assignment#baseline1_budget_work
	Assignment#baseline1_cost
	Assignment#baseline1_finish
	Assignment#baseline1_start
	Assignment#baseline1_work
	...	
	Assignment#baseline10_budget_cost
	Assignment#baseline10_budget_work
	Assignment#baseline10_cost
	Assignment#baseline10_finish
	Assignment#baseline10_start
	Assignment#baseline10_work
	Assignment#baseline_budget_cost
	Assignment#baseline_budget_work
	Assignment#baseline_cost
	Assignment#baseline_finish
	Assignment#baseline_start
	Assignment#baseline_work
	Assignment#bcwp
	Assignment#bcws
	Assignment#budget_cost
	Assignment#budget_work
	Assignment#confirmed
	Assignment#cost
	Assignment#cost1
	...
	Assignment#cost10
	Assignment#cost_rate_table
	Assignment#cost_variance
	Assignment#created
	Assignment#cv
	Assignment#date1
	...
	Assignment#date10
	Assignment#duration1
	...
	Assignment#duration10
	Assignment#duration1_units
	...
	Assignment#duration10_units
	Assignment#enterprise_cost1
	...
	Assignment#enterprise_cost10
	Assignment#enterprise_custom_field1
	...
	Assignment#enterprise_custom_field50
	Assignment#enterprise_date1
	...
	Assignment#enterprise_date30
	Assignment#enterprise_duration1
	...
	Assignment#enterprise_duration10
	Assignment#enterprise_flag1
	...
	Assignment#enterprise_flag20
	Assignment#enterprise_number1
	...
	Assignment#enterprise_number40
	Assignment#enterprise_resource_multi_value20
	...
	Assignment#enterprise_resource_multi_value29
	Assignment#enterprise_resource_outline_code1
	...
	Assignment#enterprise_resource_outline_code29
	Assignment#enterprise_resource_rbs
	Assignment#enterprise_team_member
	Assignment#enterprise_text1
	...
	Assignment#enterprise_text40
	Assignment#finish
	Assignment#finish1
	...
	Assignment#finish10
	Assignment#finish_variance
	Assignment#fixed_material_assignment
	Assignment#flag1
	...
	Assignment#flag20
	Assignment#guid
	Assignment#hyperlink
	Assignment#hyperlink_address
	Assignment#hyperlink_data
	Assignment#hyperlink_href
	Assignment#hyperlink_screen_tip
	Assignment#hyperlink_subaddress
	Assignment#index
	Assignment#leveling_delay
	Assignment#leveling_delay_units
	Assignment#linked_fields
	Assignment#notes
	Assignment#number1
	...
	Assignment#number20
	Assignment#overallocated
	Assignment#overtime_cost
	Assignment#overtime_work
	Assignment#owner
	Assignment#peak
	Assignment#percent_work_complete
	Assignment#project
	Assignment#regular_work
	Assignment#remaining_cost
	Assignment#remaining_overtime_cost
	Assignment#remaining_overtime_work
	Assignment#remaining_work
	Assignment#resource_id
	Assignment#resource_name
	Assignment#resource_request_type
	Assignment#resource_type
	Assignment#resource_unique_id
	Assignment#response_pending
	Assignment#start
	Assignment#start1
	...
	Assignment#start10
	Assignment#start_variance
	Assignment#summary
	Assignment#sv
	Assignment#task_id
	Assignment#task_name
	Assignment#task_outline_number
	Assignment#task_summary_name
	Assignment#task_unique_id
	Assignment#team_status_pending
	Assignment#text1
	...
	Assignment#text30
	Assignment#timephased_actual_overtime_work
	Assignment#timephased_actual_work
	Assignment#timephased_baseline1_cost
	...
	Assignment#timephased_baseline10_cost
	Assignment#timephased_baseline1_work
	...
	Assignment#timephased_baseline10_work
	Assignment#timephased_work
	Assignment#unavailable
	Assignment#unique_id
	Assignment#update_needed
	Assignment#vac
	Assignment#variable_rate_units
	Assignment#wbs
	Assignment#work
	Assignment#work_contour
	Assignment#work_variance

### Relation Attributes

	Relation#task_unique_id
	Relation#lag
	Relation#type
