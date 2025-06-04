module MPXJ
  module PropertyMethods
    def self.included(base)
      base.extend(PropertyClassMethods)
    end

    # Retrieve the Activity ID Increment value
    #
    # @return Activity ID Increment value
    def activity_id_increment
      get_integer_value(attribute_values['activity_id_increment'])
    end

    # Retrieve the Activity ID Increment Based On Selected Activity value
    #
    # @return Activity ID Increment Based On Selected Activity value
    def activity_id_increment_based_on_selected_activity
      get_boolean_value(attribute_values['activity_id_increment_based_on_selected_activity'])
    end

    # Retrieve the Activity ID Prefix value
    #
    # @return Activity ID Prefix value
    def activity_id_prefix
      attribute_values['activity_id_prefix']
    end

    # Retrieve the Activity ID Suffix value
    #
    # @return Activity ID Suffix value
    def activity_id_suffix
      get_integer_value(attribute_values['activity_id_suffix'])
    end

    # Retrieve the Actuals In Sync value
    #
    # @return Actuals In Sync value
    def actuals_in_sync
      get_boolean_value(attribute_values['actuals_in_sync'])
    end

    # Retrieve the Actual Cost value
    #
    # @return Actual Cost value
    def actual_cost
      get_float_value(attribute_values['actual_cost'])
    end

    # Retrieve the Actual Duration value
    #
    # @return Actual Duration value
    def actual_duration
      get_duration_value(attribute_values['actual_duration'])
    end

    # Retrieve the Actual Finish value
    #
    # @return Actual Finish value
    def actual_finish
      get_date_value(attribute_values['actual_finish'])
    end

    # Retrieve the Actual Start value
    #
    # @return Actual Start value
    def actual_start
      get_date_value(attribute_values['actual_start'])
    end

    # Retrieve the Actual Work value
    #
    # @return Actual Work value
    def actual_work
      get_duration_value(attribute_values['actual_work'])
    end

    # Retrieve the Admin Project value
    #
    # @return Admin Project value
    def admin_project
      get_boolean_value(attribute_values['admin_project'])
    end

    # Retrieve the AM Text value
    #
    # @return AM Text value
    def am_text
      attribute_values['am_text']
    end

    # Retrieve the Application Version value
    #
    # @return Application Version value
    def application_version
      get_integer_value(attribute_values['application_version'])
    end

    # Retrieve the Author value
    #
    # @return Author value
    def author
      attribute_values['author']
    end

    # Retrieve the Auto Filter value
    #
    # @return Auto Filter value
    def autofilter
      get_boolean_value(attribute_values['autofilter'])
    end

    # Retrieve the Auto Add New Resources and Tasks value
    #
    # @return Auto Add New Resources and Tasks value
    def auto_add_new_resources_and_tasks
      get_boolean_value(attribute_values['auto_add_new_resources_and_tasks'])
    end

    # Retrieve the Auto Link value
    #
    # @return Auto Link value
    def auto_link
      get_boolean_value(attribute_values['auto_link'])
    end

    # Retrieve the Bar Text Date Format value
    #
    # @return Bar Text Date Format value
    def bar_text_date_format
      attribute_values['bar_text_date_format']
    end

    # Retrieve the Baseline10 Date value
    #
    # @return Baseline10 Date value
    def baseline10_date
      get_date_value(attribute_values['baseline10_date'])
    end

    # Retrieve the Baseline1 Date value
    #
    # @return Baseline1 Date value
    def baseline1_date
      get_date_value(attribute_values['baseline1_date'])
    end

    # Retrieve the Baseline2 Date value
    #
    # @return Baseline2 Date value
    def baseline2_date
      get_date_value(attribute_values['baseline2_date'])
    end

    # Retrieve the Baseline3 Date value
    #
    # @return Baseline3 Date value
    def baseline3_date
      get_date_value(attribute_values['baseline3_date'])
    end

    # Retrieve the Baseline4 Date value
    #
    # @return Baseline4 Date value
    def baseline4_date
      get_date_value(attribute_values['baseline4_date'])
    end

    # Retrieve the Baseline5 Date value
    #
    # @return Baseline5 Date value
    def baseline5_date
      get_date_value(attribute_values['baseline5_date'])
    end

    # Retrieve the Baseline6 Date value
    #
    # @return Baseline6 Date value
    def baseline6_date
      get_date_value(attribute_values['baseline6_date'])
    end

    # Retrieve the Baseline7 Date value
    #
    # @return Baseline7 Date value
    def baseline7_date
      get_date_value(attribute_values['baseline7_date'])
    end

    # Retrieve the Baseline8 Date value
    #
    # @return Baseline8 Date value
    def baseline8_date
      get_date_value(attribute_values['baseline8_date'])
    end

    # Retrieve the Baseline9 Date value
    #
    # @return Baseline9 Date value
    def baseline9_date
      get_date_value(attribute_values['baseline9_date'])
    end

    # Retrieve the Baseline Calendar Name value
    #
    # @return Baseline Calendar Name value
    def baseline_calendar_name
      attribute_values['baseline_calendar_name']
    end

    # Retrieve the Baseline Cost value
    #
    # @return Baseline Cost value
    def baseline_cost
      get_float_value(attribute_values['baseline_cost'])
    end

    # Retrieve the Baseline Date value
    #
    # @return Baseline Date value
    def baseline_date
      get_date_value(attribute_values['baseline_date'])
    end

    # Retrieve the Baseline Duration value
    #
    # @return Baseline Duration value
    def baseline_duration
      get_duration_value(attribute_values['baseline_duration'])
    end

    # Retrieve the Baseline Finish value
    #
    # @return Baseline Finish value
    def baseline_finish
      get_date_value(attribute_values['baseline_finish'])
    end

    # Retrieve the Baseline For Earned Value value
    #
    # @return Baseline For Earned Value value
    def baseline_for_earned_value
      get_integer_value(attribute_values['baseline_for_earned_value'])
    end

    # Retrieve the Baseline Project Unique ID value
    #
    # @return Baseline Project Unique ID value
    def baseline_project_unique_id
      get_integer_value(attribute_values['baseline_project_unique_id'])
    end

    # Retrieve the Baseline Start value
    #
    # @return Baseline Start value
    def baseline_start
      get_date_value(attribute_values['baseline_start'])
    end

    # Retrieve the Baseline Type Name value
    #
    # @return Baseline Type Name value
    def baseline_type_name
      attribute_values['baseline_type_name']
    end

    # Retrieve the Baseline Type Name value
    #
    # @return Baseline Type Name value
    def baseline_type_unique_id
      get_integer_value(attribute_values['baseline_type_unique_id'])
    end

    # Retrieve the Baseline Work value
    #
    # @return Baseline Work value
    def baseline_work
      get_duration_value(attribute_values['baseline_work'])
    end

    # Retrieve the Calculate Float on Finish Date of Each Project value
    #
    # @return Calculate Float on Finish Date of Each Project value
    def calculate_float_based_on_finish_date_of_each_project
      get_boolean_value(attribute_values['calculate_float_based_on_finish_date_of_each_project'])
    end

    # Retrieve the Calculate Multiple Float Paths value
    #
    # @return Calculate Multiple Float Paths value
    def calculate_multiple_float_paths
      get_boolean_value(attribute_values['calculate_multiple_float_paths'])
    end

    # Retrieve the Calculate Multiple Paths Using Total Float value
    #
    # @return Calculate Multiple Paths Using Total Float value
    def calculate_multiple_float_paths_using_total_float
      get_boolean_value(attribute_values['calculate_multiple_float_paths_using_total_float'])
    end

    # Retrieve the Category value
    #
    # @return Category value
    def category
      attribute_values['category']
    end

    # Retrieve the Comments value
    #
    # @return Comments value
    def comments
      attribute_values['comments']
    end

    # Retrieve the Company value
    #
    # @return Company value
    def company
      attribute_values['company']
    end

    # Retrieve the Compute Start to Start Lag From Early Start value
    #
    # @return Compute Start to Start Lag From Early Start value
    def compute_start_to_start_lag_from_early_start
      get_boolean_value(attribute_values['compute_start_to_start_lag_from_early_start'])
    end

    # Retrieve the Consider Assignments In Other Projects value
    #
    # @return Consider Assignments In Other Projects value
    def consider_assignments_in_other_projects
      get_boolean_value(attribute_values['consider_assignments_in_other_projects'])
    end

    # Retrieve the Consider Assignments In Other Project With Priority Equal or Higher Than value
    #
    # @return Consider Assignments In Other Project With Priority Equal or Higher Than value
    def consider_assignments_in_other_projects_with_priority_equal_higher_than
      get_integer_value(attribute_values['consider_assignments_in_other_projects_with_priority_equal_higher_than'])
    end

    # Retrieve the Content Status value
    #
    # @return Content Status value
    def content_status
      attribute_values['content_status']
    end

    # Retrieve the Content Type value
    #
    # @return Content Type value
    def content_type
      attribute_values['content_type']
    end

    # Retrieve the Cost value
    #
    # @return Cost value
    def cost
      get_float_value(attribute_values['cost'])
    end

    # Retrieve the Creation Date value
    #
    # @return Creation Date value
    def creation_date
      get_date_value(attribute_values['creation_date'])
    end

    # Retrieve the Critical Activity Type value
    #
    # @return Critical Activity Type value
    def critical_activity_type
      attribute_values['critical_activity_type']
    end

    # Retrieve the Critical Slack Limit value
    #
    # @return Critical Slack Limit value
    def critical_slack_limit
      get_duration_value(attribute_values['critical_slack_limit'])
    end

    # Retrieve the Currency Code value
    #
    # @return Currency Code value
    def currency_code
      attribute_values['currency_code']
    end

    # Retrieve the Currency Digits value
    #
    # @return Currency Digits value
    def currency_digits
      get_integer_value(attribute_values['currency_digits'])
    end

    # Retrieve the Currency Symbol value
    #
    # @return Currency Symbol value
    def currency_symbol
      attribute_values['currency_symbol']
    end

    # Retrieve the Currency Symbol Position value
    #
    # @return Currency Symbol Position value
    def currency_symbol_position
      attribute_values['currency_symbol_position']
    end

    # Retrieve the Current Date value
    #
    # @return Current Date value
    def current_date
      get_date_value(attribute_values['current_date'])
    end

    # Retrieve the Custom Properties value
    #
    # @return Custom Properties value
    def custom_properties
      attribute_values['custom_properties']
    end

    # Retrieve the Date Date and Planned Start Set To Project Forecast Start value
    #
    # @return Date Date and Planned Start Set To Project Forecast Start value
    def data_date_and_planned_start_set_to_project_forecast_start
      get_boolean_value(attribute_values['data_date_and_planned_start_set_to_project_forecast_start'])
    end

    # Retrieve the Date Format value
    #
    # @return Date Format value
    def date_format
      attribute_values['date_format']
    end

    # Retrieve the Date Order value
    #
    # @return Date Order value
    def date_order
      attribute_values['date_order']
    end

    # Retrieve the Date Separator value
    #
    # @return Date Separator value
    def date_separator
      attribute_values['date_separator']
    end

    # Retrieve the Days per Month value
    #
    # @return Days per Month value
    def days_per_month
      get_integer_value(attribute_values['days_per_month'])
    end

    # Retrieve the Decimal Separator value
    #
    # @return Decimal Separator value
    def decimal_separator
      attribute_values['decimal_separator']
    end

    # Retrieve the Default Calendar Unique ID value
    #
    # @return Default Calendar Unique ID value
    def default_calendar_unique_id
      get_integer_value(attribute_values['default_calendar_unique_id'])
    end

    # Retrieve the Default Duration Is Fixed value
    #
    # @return Default Duration Is Fixed value
    def default_duration_is_fixed
      get_boolean_value(attribute_values['default_duration_is_fixed'])
    end

    # Retrieve the Default Duration Units value
    #
    # @return Default Duration Units value
    def default_duration_units
      attribute_values['default_duration_units']
    end

    # Retrieve the Default End Time value
    #
    # @return Default End Time value
    def default_end_time
      attribute_values['default_end_time']
    end

    # Retrieve the Default Fixed Cost Accrual value
    #
    # @return Default Fixed Cost Accrual value
    def default_fixed_cost_accrual
      attribute_values['default_fixed_cost_accrual']
    end

    # Retrieve the Default Overtime Rate value
    #
    # @return Default Overtime Rate value
    def default_overtime_rate
      attribute_values['default_overtime_rate']
    end

    # Retrieve the Default Standard Rate value
    #
    # @return Default Standard Rate value
    def default_standard_rate
      attribute_values['default_standard_rate']
    end

    # Retrieve the Default Start Time value
    #
    # @return Default Start Time value
    def default_start_time
      attribute_values['default_start_time']
    end

    # Retrieve the Default Task Earned Value Method value
    #
    # @return Default Task Earned Value Method value
    def default_task_earned_value_method
      attribute_values['default_task_earned_value_method']
    end

    # Retrieve the Default Task Type value
    #
    # @return Default Task Type value
    def default_task_type
      attribute_values['default_task_type']
    end

    # Retrieve the Default Work Units value
    #
    # @return Default Work Units value
    def default_work_units
      attribute_values['default_work_units']
    end

    # Retrieve the Calculate Multiple Float Paths Ending With Activity Unique ID value
    #
    # @return Calculate Multiple Float Paths Ending With Activity Unique ID value
    def display_multiple_float_paths_ending_with_activity_unique_id
      get_integer_value(attribute_values['display_multiple_float_paths_ending_with_activity_unique_id'])
    end

    # Retrieve the Document Version value
    #
    # @return Document Version value
    def document_version
      attribute_values['document_version']
    end

    # Retrieve the Duration value
    #
    # @return Duration value
    def duration
      get_duration_value(attribute_values['duration'])
    end

    # Retrieve the Earned Value Method value
    #
    # @return Earned Value Method value
    def earned_value_method
      attribute_values['earned_value_method']
    end

    # Retrieve the Editable Actual Costs value
    #
    # @return Editable Actual Costs value
    def editable_actual_costs
      get_boolean_value(attribute_values['editable_actual_costs'])
    end

    # Retrieve the Editing Time value
    #
    # @return Editing Time value
    def editing_time
      get_integer_value(attribute_values['editing_time'])
    end

    # Retrieve the Enable Publication value
    #
    # @return Enable Publication value
    def enable_publication
      get_boolean_value(attribute_values['enable_publication'])
    end

    # Retrieve the Enable Summarization value
    #
    # @return Enable Summarization value
    def enable_summarization
      get_boolean_value(attribute_values['enable_summarization'])
    end

    # Retrieve the Export Flag value
    #
    # @return Export Flag value
    def export_flag
      get_boolean_value(attribute_values['export_flag'])
    end

    # Retrieve the Extended Creation Date value
    #
    # @return Extended Creation Date value
    def extended_creation_date
      get_date_value(attribute_values['extended_creation_date'])
    end

    # Retrieve the File Application value
    #
    # @return File Application value
    def file_application
      attribute_values['file_application']
    end

    # Retrieve the File Type value
    #
    # @return File Type value
    def file_type
      attribute_values['file_type']
    end

    # Retrieve the Finish Date value
    #
    # @return Finish Date value
    def finish_date
      get_date_value(attribute_values['finish_date'])
    end

    # Retrieve the Finish Variance value
    #
    # @return Finish Variance value
    def finish_variance
      get_duration_value(attribute_values['finish_variance'])
    end

    # Retrieve the Fiscal Year Start value
    #
    # @return Fiscal Year Start value
    def fiscal_year_start
      get_boolean_value(attribute_values['fiscal_year_start'])
    end

    # Retrieve the Fiscal Year Start Month value
    #
    # @return Fiscal Year Start Month value
    def fiscal_year_start_month
      get_integer_value(attribute_values['fiscal_year_start_month'])
    end

    # Retrieve the Full Application Name value
    #
    # @return Full Application Name value
    def full_application_name
      attribute_values['full_application_name']
    end

    # Retrieve the GUID value
    #
    # @return GUID value
    def guid
      attribute_values['guid']
    end

    # Retrieve the Honor Constraints value
    #
    # @return Honor Constraints value
    def honor_constraints
      get_boolean_value(attribute_values['honor_constraints'])
    end

    # Retrieve the Hyperlink Base value
    #
    # @return Hyperlink Base value
    def hyperlink_base
      attribute_values['hyperlink_base']
    end

    # Retrieve the Ignore Relationships To And From Other Projects value
    #
    # @return Ignore Relationships To And From Other Projects value
    def ignore_relationships_to_and_from_other_projects
      get_boolean_value(attribute_values['ignore_relationships_to_and_from_other_projects'])
    end

    # Retrieve the Inserted Projects Like Summary value
    #
    # @return Inserted Projects Like Summary value
    def inserted_projects_like_summary
      get_boolean_value(attribute_values['inserted_projects_like_summary'])
    end

    # Retrieve the Keywords value
    #
    # @return Keywords value
    def keywords
      attribute_values['keywords']
    end

    # Retrieve the Language value
    #
    # @return Language value
    def language
      attribute_values['language']
    end

    # Retrieve the Last Printed value
    #
    # @return Last Printed value
    def lastprinted
      get_date_value(attribute_values['lastprinted'])
    end

    # Retrieve the Last Author value
    #
    # @return Last Author value
    def last_author
      attribute_values['last_author']
    end

    # Retrieve the Last Baseline Update Date value
    #
    # @return Last Baseline Update Date value
    def last_baseline_update_date
      get_date_value(attribute_values['last_baseline_update_date'])
    end

    # Retrieve the Last Saved value
    #
    # @return Last Saved value
    def last_saved
      get_date_value(attribute_values['last_saved'])
    end

    # Retrieve the Leveling Priorities value
    #
    # @return Leveling Priorities value
    def leveling_priorities
      attribute_values['leveling_priorities']
    end

    # Retrieve the Level All Resources value
    #
    # @return Level All Resources value
    def level_all_resources
      get_boolean_value(attribute_values['level_all_resources'])
    end

    # Retrieve the Level Resources Only Within Activity Total Float value
    #
    # @return Level Resources Only Within Activity Total Float value
    def level_resources_only_within_activity_total_float
      get_boolean_value(attribute_values['level_resources_only_within_activity_total_float'])
    end

    # Retrieve the Limit Number of Float Paths to Calculate value
    #
    # @return Limit Number of Float Paths to Calculate value
    def limit_number_of_float_paths_to_calculate
      get_boolean_value(attribute_values['limit_number_of_float_paths_to_calculate'])
    end

    # Retrieve the Location Unique ID value
    #
    # @return Location Unique ID value
    def location_unique_id
      get_integer_value(attribute_values['location_unique_id'])
    end

    # Retrieve the Make Open Ended Activities Critical value
    #
    # @return Make Open Ended Activities Critical value
    def make_open_ended_activities_critical
      get_boolean_value(attribute_values['make_open_ended_activities_critical'])
    end

    # Retrieve the Manager value
    #
    # @return Manager value
    def manager
      attribute_values['manager']
    end

    # Retrieve the Number of Float Paths to Calculate value
    #
    # @return Number of Float Paths to Calculate value
    def maximum_number_of_float_paths_to_calculate
      get_integer_value(attribute_values['maximum_number_of_float_paths_to_calculate'])
    end

    # Retrieve the Maximum Percentage to Overallocate Resources value
    #
    # @return Maximum Percentage to Overallocate Resources value
    def max_percent_to_overallocate_resources
      get_float_value(attribute_values['max_percent_to_overallocate_resources'])
    end

    # Retrieve the Microsoft Project Server URL value
    #
    # @return Microsoft Project Server URL value
    def microsoft_project_server_url
      get_boolean_value(attribute_values['microsoft_project_server_url'])
    end

    # Retrieve the Minutes per Day value
    #
    # @return Minutes per Day value
    def minutes_per_day
      get_integer_value(attribute_values['minutes_per_day'])
    end

    # Retrieve the Minutes per Month value
    #
    # @return Minutes per Month value
    def minutes_per_month
      get_integer_value(attribute_values['minutes_per_month'])
    end

    # Retrieve the Minutes per Week value
    #
    # @return Minutes per Week value
    def minutes_per_week
      get_integer_value(attribute_values['minutes_per_week'])
    end

    # Retrieve the Minutes per Year value
    #
    # @return Minutes per Year value
    def minutes_per_year
      get_integer_value(attribute_values['minutes_per_year'])
    end

    # Retrieve the Move Completed Ends Back value
    #
    # @return Move Completed Ends Back value
    def move_completed_ends_back
      get_boolean_value(attribute_values['move_completed_ends_back'])
    end

    # Retrieve the Move Completed Ends Forward value
    #
    # @return Move Completed Ends Forward value
    def move_completed_ends_forward
      get_boolean_value(attribute_values['move_completed_ends_forward'])
    end

    # Retrieve the Move Remaining Starts Back value
    #
    # @return Move Remaining Starts Back value
    def move_remaining_starts_back
      get_boolean_value(attribute_values['move_remaining_starts_back'])
    end

    # Retrieve the Move Remaining Starts Forward value
    #
    # @return Move Remaining Starts Forward value
    def move_remaining_starts_forward
      get_boolean_value(attribute_values['move_remaining_starts_forward'])
    end

    # Retrieve the MPP File Type value
    #
    # @return MPP File Type value
    def mpp_file_type
      get_integer_value(attribute_values['mpp_file_type'])
    end

    # Retrieve the MPX Code Page value
    #
    # @return MPX Code Page value
    def mpx_code_page
      attribute_values['mpx_code_page']
    end

    # Retrieve the MPX Delimiter value
    #
    # @return MPX Delimiter value
    def mpx_delimiter
      attribute_values['mpx_delimiter']
    end

    # Retrieve the MPX File Version value
    #
    # @return MPX File Version value
    def mpx_file_version
      attribute_values['mpx_file_version']
    end

    # Retrieve the MPX Program Name value
    #
    # @return MPX Program Name value
    def mpx_program_name
      attribute_values['mpx_program_name']
    end

    # Retrieve the Multiple Critical Paths value
    #
    # @return Multiple Critical Paths value
    def multiple_critical_paths
      get_boolean_value(attribute_values['multiple_critical_paths'])
    end

    # Retrieve the Must Finish By value
    #
    # @return Must Finish By value
    def must_finish_by
      get_date_value(attribute_values['must_finish_by'])
    end

    # Retrieve the Name value
    #
    # @return Name value
    def name
      attribute_values['name']
    end

    # Retrieve the New Tasks Are Manual value
    #
    # @return New Tasks Are Manual value
    def new_tasks_are_manual
      get_boolean_value(attribute_values['new_tasks_are_manual'])
    end

    # Retrieve the New Tasks Are Effort Driven value
    #
    # @return New Tasks Are Effort Driven value
    def new_tasks_effort_driven
      get_boolean_value(attribute_values['new_tasks_effort_driven'])
    end

    # Retrieve the New Tasks Estimated value
    #
    # @return New Tasks Estimated value
    def new_tasks_estimated
      get_boolean_value(attribute_values['new_tasks_estimated'])
    end

    # Retrieve the New Task Start Is Project Start value
    #
    # @return New Task Start Is Project Start value
    def new_task_start_is_project_start
      get_boolean_value(attribute_values['new_task_start_is_project_start'])
    end

    # Retrieve the Notes value
    #
    # @return Notes value
    def notes
      attribute_values['notes']
    end

    # Retrieve the Percentage Complete value
    #
    # @return Percentage Complete value
    def percentage_complete
      get_float_value(attribute_values['percentage_complete'])
    end

    # Retrieve the Planned Start value
    #
    # @return Planned Start value
    def planned_start
      get_date_value(attribute_values['planned_start'])
    end

    # Retrieve the PM Text value
    #
    # @return PM Text value
    def pm_text
      attribute_values['pm_text']
    end

    # Retrieve the Presentation Format value
    #
    # @return Presentation Format value
    def presentation_format
      attribute_values['presentation_format']
    end

    # Retrieve the Preserve Minimum Float When Leveling value
    #
    # @return Preserve Minimum Float When Leveling value
    def preserve_minimum_float_when_leveling
      get_duration_value(attribute_values['preserve_minimum_float_when_leveling'])
    end

    # Retrieve the Preserve Scheduled Early and Late Dates value
    #
    # @return Preserve Scheduled Early and Late Dates value
    def preserve_scheduled_early_and_late_dates
      get_boolean_value(attribute_values['preserve_scheduled_early_and_late_dates'])
    end

    # Retrieve the Project Code Values value
    #
    # @return Project Code Values value
    def project_code_values
      attribute_values['project_code_values']
    end

    # Retrieve the Project Externally Edited value
    #
    # @return Project Externally Edited value
    def project_externally_edited
      get_boolean_value(attribute_values['project_externally_edited'])
    end

    # Retrieve the Project File Path value
    #
    # @return Project File Path value
    def project_file_path
      attribute_values['project_file_path']
    end

    # Retrieve the Project ID value
    #
    # @return Project ID value
    def project_id
      attribute_values['project_id']
    end

    # Retrieve the Project Is Baseline value
    #
    # @return Project Is Baseline value
    def project_is_baseline
      get_boolean_value(attribute_values['project_is_baseline'])
    end

    # Retrieve the Project Title value
    #
    # @return Project Title value
    def project_title
      attribute_values['project_title']
    end

    # Retrieve the Project Website URL value
    #
    # @return Project Website URL value
    def project_website_url
      attribute_values['project_website_url']
    end

    # Retrieve the Relationship Lag Calendar value
    #
    # @return Relationship Lag Calendar value
    def relationship_lag_calendar
      attribute_values['relationship_lag_calendar']
    end

    # Retrieve the Remove File Properties value
    #
    # @return Remove File Properties value
    def remove_file_properties
      get_boolean_value(attribute_values['remove_file_properties'])
    end

    # Retrieve the Resource Pool File value
    #
    # @return Resource Pool File value
    def resource_pool_file
      attribute_values['resource_pool_file']
    end

    # Retrieve the Revision value
    #
    # @return Revision value
    def revision
      get_integer_value(attribute_values['revision'])
    end

    # Retrieve the Scheduled Finish value
    #
    # @return Scheduled Finish value
    def scheduled_finish
      get_date_value(attribute_values['scheduled_finish'])
    end

    # Retrieve the Schedule From value
    #
    # @return Schedule From value
    def schedule_from
      attribute_values['schedule_from']
    end

    # Retrieve the When Scheduling Progressed Activities Use value
    #
    # @return When Scheduling Progressed Activities Use value
    def scheduling_progressed_activities
      attribute_values['scheduling_progressed_activities']
    end

    # Retrieve the Short Application Name value
    #
    # @return Short Application Name value
    def short_application_name
      attribute_values['short_application_name']
    end

    # Retrieve the Show Project Summary Task value
    #
    # @return Show Project Summary Task value
    def show_project_summary_task
      get_boolean_value(attribute_values['show_project_summary_task'])
    end

    # Retrieve the Split In Progress Tasks value
    #
    # @return Split In Progress Tasks value
    def split_in_progress_tasks
      get_boolean_value(attribute_values['split_in_progress_tasks'])
    end

    # Retrieve the Spread Actual Cost value
    #
    # @return Spread Actual Cost value
    def spread_actual_cost
      get_boolean_value(attribute_values['spread_actual_cost'])
    end

    # Retrieve the Spread Percent Complete value
    #
    # @return Spread Percent Complete value
    def spread_percent_complete
      get_boolean_value(attribute_values['spread_percent_complete'])
    end

    # Retrieve the Start Date value
    #
    # @return Start Date value
    def start_date
      get_date_value(attribute_values['start_date'])
    end

    # Retrieve the Start Variance value
    #
    # @return Start Variance value
    def start_variance
      get_duration_value(attribute_values['start_variance'])
    end

    # Retrieve the Status Date value
    #
    # @return Status Date value
    def status_date
      get_date_value(attribute_values['status_date'])
    end

    # Retrieve the Subject value
    #
    # @return Subject value
    def subject
      attribute_values['subject']
    end

    # Retrieve the Template value
    #
    # @return Template value
    def template
      attribute_values['template']
    end

    # Retrieve the Thousands Separator value
    #
    # @return Thousands Separator value
    def thousands_separator
      attribute_values['thousands_separator']
    end

    # Retrieve the Time Format value
    #
    # @return Time Format value
    def time_format
      attribute_values['time_format']
    end

    # Retrieve the Time Separator value
    #
    # @return Time Separator value
    def time_separator
      attribute_values['time_separator']
    end

    # Retrieve the Total Slack Calculation Type value
    #
    # @return Total Slack Calculation Type value
    def total_slack_calculation_type
      attribute_values['total_slack_calculation_type']
    end

    # Retrieve the Unique ID value
    #
    # @return Unique ID value
    def unique_id
      get_integer_value(attribute_values['unique_id'])
    end

    # Retrieve the Updating Task Status Updates Resource Status value
    #
    # @return Updating Task Status Updates Resource Status value
    def updating_task_status_updates_resource_status
      get_boolean_value(attribute_values['updating_task_status_updates_resource_status'])
    end

    # Retrieve the Use Expected Finish Dates value
    #
    # @return Use Expected Finish Dates value
    def use_expected_finish_dates
      get_boolean_value(attribute_values['use_expected_finish_dates'])
    end

    # Retrieve the WBS Code Separator value
    #
    # @return WBS Code Separator value
    def wbs_code_separator
      attribute_values['wbs_code_separator']
    end

    # Retrieve the Week Start Day value
    #
    # @return Week Start Day value
    def week_start_day
      attribute_values['week_start_day']
    end

    # Retrieve the Work value
    #
    # @return Work value
    def work
      get_duration_value(attribute_values['work'])
    end

    # Retrieve the Work 2 value
    #
    # @return Work 2 value
    def work2
      get_float_value(attribute_values['work2'])
    end

    ATTRIBUTE_TYPES = {
      'activity_id_increment' => :integer,
      'activity_id_increment_based_on_selected_activity' => :boolean,
      'activity_id_prefix' => :string,
      'activity_id_suffix' => :integer,
      'actuals_in_sync' => :boolean,
      'actual_cost' => :currency,
      'actual_duration' => :duration,
      'actual_finish' => :date,
      'actual_start' => :date,
      'actual_work' => :work,
      'admin_project' => :boolean,
      'am_text' => :string,
      'application_version' => :integer,
      'author' => :string,
      'autofilter' => :boolean,
      'auto_add_new_resources_and_tasks' => :boolean,
      'auto_link' => :boolean,
      'bar_text_date_format' => :project_date_format,
      'baseline10_date' => :date,
      'baseline1_date' => :date,
      'baseline2_date' => :date,
      'baseline3_date' => :date,
      'baseline4_date' => :date,
      'baseline5_date' => :date,
      'baseline6_date' => :date,
      'baseline7_date' => :date,
      'baseline8_date' => :date,
      'baseline9_date' => :date,
      'baseline_calendar_name' => :string,
      'baseline_cost' => :currency,
      'baseline_date' => :date,
      'baseline_duration' => :duration,
      'baseline_finish' => :date,
      'baseline_for_earned_value' => :integer,
      'baseline_project_unique_id' => :integer,
      'baseline_start' => :date,
      'baseline_type_name' => :string,
      'baseline_type_unique_id' => :integer,
      'baseline_work' => :work,
      'calculate_float_based_on_finish_date_of_each_project' => :boolean,
      'calculate_multiple_float_paths' => :boolean,
      'calculate_multiple_float_paths_using_total_float' => :boolean,
      'category' => :string,
      'comments' => :string,
      'company' => :string,
      'compute_start_to_start_lag_from_early_start' => :boolean,
      'consider_assignments_in_other_projects' => :boolean,
      'consider_assignments_in_other_projects_with_priority_equal_higher_than' => :integer,
      'content_status' => :string,
      'content_type' => :string,
      'cost' => :currency,
      'creation_date' => :date,
      'critical_activity_type' => :critical_activity_type,
      'critical_slack_limit' => :duration,
      'currency_code' => :string,
      'currency_digits' => :integer,
      'currency_symbol' => :string,
      'currency_symbol_position' => :currency_symbol_position,
      'current_date' => :date,
      'custom_properties' => :map,
      'data_date_and_planned_start_set_to_project_forecast_start' => :boolean,
      'date_format' => :project_date_format,
      'date_order' => :date_order,
      'date_separator' => :char,
      'days_per_month' => :integer,
      'decimal_separator' => :char,
      'default_calendar_unique_id' => :integer,
      'default_duration_is_fixed' => :boolean,
      'default_duration_units' => :time_units,
      'default_end_time' => :time,
      'default_fixed_cost_accrual' => :accrue,
      'default_overtime_rate' => :rate,
      'default_standard_rate' => :rate,
      'default_start_time' => :time,
      'default_task_earned_value_method' => :earned_value_method,
      'default_task_type' => :task_type,
      'default_work_units' => :time_units,
      'display_multiple_float_paths_ending_with_activity_unique_id' => :integer,
      'document_version' => :string,
      'duration' => :duration,
      'earned_value_method' => :earned_value_method,
      'editable_actual_costs' => :boolean,
      'editing_time' => :integer,
      'enable_publication' => :boolean,
      'enable_summarization' => :boolean,
      'export_flag' => :boolean,
      'extended_creation_date' => :date,
      'file_application' => :string,
      'file_type' => :string,
      'finish_date' => :date,
      'finish_variance' => :duration,
      'fiscal_year_start' => :boolean,
      'fiscal_year_start_month' => :integer,
      'full_application_name' => :string,
      'guid' => :guid,
      'honor_constraints' => :boolean,
      'hyperlink_base' => :string,
      'ignore_relationships_to_and_from_other_projects' => :boolean,
      'inserted_projects_like_summary' => :boolean,
      'keywords' => :string,
      'language' => :string,
      'lastprinted' => :date,
      'last_author' => :string,
      'last_baseline_update_date' => :date,
      'last_saved' => :date,
      'leveling_priorities' => :string,
      'level_all_resources' => :boolean,
      'level_resources_only_within_activity_total_float' => :boolean,
      'limit_number_of_float_paths_to_calculate' => :boolean,
      'location_unique_id' => :integer,
      'make_open_ended_activities_critical' => :boolean,
      'manager' => :string,
      'maximum_number_of_float_paths_to_calculate' => :integer,
      'max_percent_to_overallocate_resources' => :numeric,
      'microsoft_project_server_url' => :boolean,
      'minutes_per_day' => :integer,
      'minutes_per_month' => :integer,
      'minutes_per_week' => :integer,
      'minutes_per_year' => :integer,
      'move_completed_ends_back' => :boolean,
      'move_completed_ends_forward' => :boolean,
      'move_remaining_starts_back' => :boolean,
      'move_remaining_starts_forward' => :boolean,
      'mpp_file_type' => :integer,
      'mpx_code_page' => :mpx_code_page,
      'mpx_delimiter' => :char,
      'mpx_file_version' => :mpx_file_version,
      'mpx_program_name' => :string,
      'multiple_critical_paths' => :boolean,
      'must_finish_by' => :date,
      'name' => :string,
      'new_tasks_are_manual' => :boolean,
      'new_tasks_effort_driven' => :boolean,
      'new_tasks_estimated' => :boolean,
      'new_task_start_is_project_start' => :boolean,
      'notes' => :notes,
      'percentage_complete' => :percentage,
      'planned_start' => :date,
      'pm_text' => :string,
      'presentation_format' => :string,
      'preserve_minimum_float_when_leveling' => :duration,
      'preserve_scheduled_early_and_late_dates' => :boolean,
      'project_code_values' => :code_values,
      'project_externally_edited' => :boolean,
      'project_file_path' => :string,
      'project_id' => :string,
      'project_is_baseline' => :boolean,
      'project_title' => :string,
      'project_website_url' => :string,
      'relationship_lag_calendar' => :relationship_lag_calendar,
      'remove_file_properties' => :boolean,
      'resource_pool_file' => :string,
      'revision' => :integer,
      'scheduled_finish' => :date,
      'schedule_from' => :schedule_from,
      'scheduling_progressed_activities' => :scheduling_progressed_activities,
      'short_application_name' => :string,
      'show_project_summary_task' => :boolean,
      'split_in_progress_tasks' => :boolean,
      'spread_actual_cost' => :boolean,
      'spread_percent_complete' => :boolean,
      'start_date' => :date,
      'start_variance' => :duration,
      'status_date' => :date,
      'subject' => :string,
      'template' => :string,
      'thousands_separator' => :char,
      'time_format' => :project_time_format,
      'time_separator' => :char,
      'total_slack_calculation_type' => :total_slack_type,
      'unique_id' => :integer,
      'updating_task_status_updates_resource_status' => :boolean,
      'use_expected_finish_dates' => :boolean,
      'wbs_code_separator' => :string,
      'week_start_day' => :day,
      'work' => :work,
      'work2' => :numeric,
    }.freeze

    def attribute_types
      ATTRIBUTE_TYPES
    end

    module PropertyClassMethods
      def attribute_types
        ATTRIBUTE_TYPES
      end
    end
  end
end
