module MPXJ
  module ResourceMethods
    def self.included(base)
      base.extend(ResourceClassMethods)
    end

    # Retrieve the Accrue At value
    #
    # @return Accrue At value
    def accrue_at
      attribute_values['accrue_at']
    end

    # Retrieve the Active value
    #
    # @return Active value
    def active
      get_boolean_value(attribute_values['active'])
    end

    # Retrieve the Actual Cost value
    #
    # @return Actual Cost value
    def actual_cost
      get_float_value(attribute_values['actual_cost'])
    end

    # Retrieve the Actual Finish value
    #
    # @return Actual Finish value
    def actual_finish
      get_date_value(attribute_values['actual_finish'])
    end

    # Retrieve the Actual Overtime Cost value
    #
    # @return Actual Overtime Cost value
    def actual_overtime_cost
      get_float_value(attribute_values['actual_overtime_cost'])
    end

    # Retrieve the Actual Overtime Work value
    #
    # @return Actual Overtime Work value
    def actual_overtime_work
      get_duration_value(attribute_values['actual_overtime_work'])
    end

    # Retrieve the Actual Overtime Work Protected value
    #
    # @return Actual Overtime Work Protected value
    def actual_overtime_work_protected
      get_duration_value(attribute_values['actual_overtime_work_protected'])
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

    # Retrieve the Actual Work Protected value
    #
    # @return Actual Work Protected value
    def actual_work_protected
      get_duration_value(attribute_values['actual_work_protected'])
    end

    # Retrieve the ACWP value
    #
    # @return ACWP value
    def acwp
      get_float_value(attribute_values['acwp'])
    end

    # Retrieve the Assignment value
    #
    # @return Assignment value
    def assignment
      get_boolean_value(attribute_values['assignment'])
    end

    # Retrieve the Assignment Delay value
    #
    # @return Assignment Delay value
    def assignment_delay
      get_duration_value(attribute_values['assignment_delay'])
    end

    # Retrieve the Assignment Owner value
    #
    # @return Assignment Owner value
    def assignment_owner
      attribute_values['assignment_owner']
    end

    # Retrieve the Assignment Units value
    #
    # @return Assignment Units value
    def assignment_units
      get_float_value(attribute_values['assignment_units'])
    end

    # Retrieve the Availability Data value
    #
    # @return Availability Data value
    def availability_data
      attribute_values['availability_data']
    end

    # Retrieve the Available From value
    #
    # @return Available From value
    def available_from
      get_date_value(attribute_values['available_from'])
    end

    # Retrieve the Available To value
    #
    # @return Available To value
    def available_to
      get_date_value(attribute_values['available_to'])
    end

    # Retrieve the Baseline10 Budget Cost value
    #
    # @return Baseline10 Budget Cost value
    def baseline10_budget_cost
      get_float_value(attribute_values['baseline10_budget_cost'])
    end

    # Retrieve the Baseline10 Budget Work value
    #
    # @return Baseline10 Budget Work value
    def baseline10_budget_work
      get_duration_value(attribute_values['baseline10_budget_work'])
    end

    # Retrieve the Baseline10 Cost value
    #
    # @return Baseline10 Cost value
    def baseline10_cost
      get_float_value(attribute_values['baseline10_cost'])
    end

    # Retrieve the Baseline10 Finish value
    #
    # @return Baseline10 Finish value
    def baseline10_finish
      get_date_value(attribute_values['baseline10_finish'])
    end

    # Retrieve the Baseline10 Start value
    #
    # @return Baseline10 Start value
    def baseline10_start
      get_date_value(attribute_values['baseline10_start'])
    end

    # Retrieve the Baseline10 Work value
    #
    # @return Baseline10 Work value
    def baseline10_work
      get_duration_value(attribute_values['baseline10_work'])
    end

    # Retrieve the Baseline1 Budget Cost value
    #
    # @return Baseline1 Budget Cost value
    def baseline1_budget_cost
      get_float_value(attribute_values['baseline1_budget_cost'])
    end

    # Retrieve the Baseline1 Budget Work value
    #
    # @return Baseline1 Budget Work value
    def baseline1_budget_work
      get_duration_value(attribute_values['baseline1_budget_work'])
    end

    # Retrieve the Baseline1 Cost value
    #
    # @return Baseline1 Cost value
    def baseline1_cost
      get_float_value(attribute_values['baseline1_cost'])
    end

    # Retrieve the Baseline1 Finish value
    #
    # @return Baseline1 Finish value
    def baseline1_finish
      get_date_value(attribute_values['baseline1_finish'])
    end

    # Retrieve the Baseline1 Start value
    #
    # @return Baseline1 Start value
    def baseline1_start
      get_date_value(attribute_values['baseline1_start'])
    end

    # Retrieve the Baseline1 Work value
    #
    # @return Baseline1 Work value
    def baseline1_work
      get_duration_value(attribute_values['baseline1_work'])
    end

    # Retrieve the Baseline2 Budget Cost value
    #
    # @return Baseline2 Budget Cost value
    def baseline2_budget_cost
      get_float_value(attribute_values['baseline2_budget_cost'])
    end

    # Retrieve the Baseline2 Budget Work value
    #
    # @return Baseline2 Budget Work value
    def baseline2_budget_work
      get_duration_value(attribute_values['baseline2_budget_work'])
    end

    # Retrieve the Baseline2 Cost value
    #
    # @return Baseline2 Cost value
    def baseline2_cost
      get_float_value(attribute_values['baseline2_cost'])
    end

    # Retrieve the Baseline2 Finish value
    #
    # @return Baseline2 Finish value
    def baseline2_finish
      get_date_value(attribute_values['baseline2_finish'])
    end

    # Retrieve the Baseline2 Start value
    #
    # @return Baseline2 Start value
    def baseline2_start
      get_date_value(attribute_values['baseline2_start'])
    end

    # Retrieve the Baseline2 Work value
    #
    # @return Baseline2 Work value
    def baseline2_work
      get_duration_value(attribute_values['baseline2_work'])
    end

    # Retrieve the Baseline3 Budget Cost value
    #
    # @return Baseline3 Budget Cost value
    def baseline3_budget_cost
      get_float_value(attribute_values['baseline3_budget_cost'])
    end

    # Retrieve the Baseline3 Budget Work value
    #
    # @return Baseline3 Budget Work value
    def baseline3_budget_work
      get_duration_value(attribute_values['baseline3_budget_work'])
    end

    # Retrieve the Baseline3 Cost value
    #
    # @return Baseline3 Cost value
    def baseline3_cost
      get_float_value(attribute_values['baseline3_cost'])
    end

    # Retrieve the Baseline3 Finish value
    #
    # @return Baseline3 Finish value
    def baseline3_finish
      get_date_value(attribute_values['baseline3_finish'])
    end

    # Retrieve the Baseline3 Start value
    #
    # @return Baseline3 Start value
    def baseline3_start
      get_date_value(attribute_values['baseline3_start'])
    end

    # Retrieve the Baseline3 Work value
    #
    # @return Baseline3 Work value
    def baseline3_work
      get_duration_value(attribute_values['baseline3_work'])
    end

    # Retrieve the Baseline4 Budget Cost value
    #
    # @return Baseline4 Budget Cost value
    def baseline4_budget_cost
      get_float_value(attribute_values['baseline4_budget_cost'])
    end

    # Retrieve the Baseline4 Budget Work value
    #
    # @return Baseline4 Budget Work value
    def baseline4_budget_work
      get_duration_value(attribute_values['baseline4_budget_work'])
    end

    # Retrieve the Baseline4 Cost value
    #
    # @return Baseline4 Cost value
    def baseline4_cost
      get_float_value(attribute_values['baseline4_cost'])
    end

    # Retrieve the Baseline4 Finish value
    #
    # @return Baseline4 Finish value
    def baseline4_finish
      get_date_value(attribute_values['baseline4_finish'])
    end

    # Retrieve the Baseline4 Start value
    #
    # @return Baseline4 Start value
    def baseline4_start
      get_date_value(attribute_values['baseline4_start'])
    end

    # Retrieve the Baseline4 Work value
    #
    # @return Baseline4 Work value
    def baseline4_work
      get_duration_value(attribute_values['baseline4_work'])
    end

    # Retrieve the Baseline5 Budget Cost value
    #
    # @return Baseline5 Budget Cost value
    def baseline5_budget_cost
      get_float_value(attribute_values['baseline5_budget_cost'])
    end

    # Retrieve the Baseline5 Budget Work value
    #
    # @return Baseline5 Budget Work value
    def baseline5_budget_work
      get_duration_value(attribute_values['baseline5_budget_work'])
    end

    # Retrieve the Baseline5 Cost value
    #
    # @return Baseline5 Cost value
    def baseline5_cost
      get_float_value(attribute_values['baseline5_cost'])
    end

    # Retrieve the Baseline5 Finish value
    #
    # @return Baseline5 Finish value
    def baseline5_finish
      get_date_value(attribute_values['baseline5_finish'])
    end

    # Retrieve the Baseline5 Start value
    #
    # @return Baseline5 Start value
    def baseline5_start
      get_date_value(attribute_values['baseline5_start'])
    end

    # Retrieve the Baseline5 Work value
    #
    # @return Baseline5 Work value
    def baseline5_work
      get_duration_value(attribute_values['baseline5_work'])
    end

    # Retrieve the Baseline6 Budget Cost value
    #
    # @return Baseline6 Budget Cost value
    def baseline6_budget_cost
      get_float_value(attribute_values['baseline6_budget_cost'])
    end

    # Retrieve the Baseline6 Budget Work value
    #
    # @return Baseline6 Budget Work value
    def baseline6_budget_work
      get_duration_value(attribute_values['baseline6_budget_work'])
    end

    # Retrieve the Baseline6 Cost value
    #
    # @return Baseline6 Cost value
    def baseline6_cost
      get_float_value(attribute_values['baseline6_cost'])
    end

    # Retrieve the Baseline6 Finish value
    #
    # @return Baseline6 Finish value
    def baseline6_finish
      get_date_value(attribute_values['baseline6_finish'])
    end

    # Retrieve the Baseline6 Start value
    #
    # @return Baseline6 Start value
    def baseline6_start
      get_date_value(attribute_values['baseline6_start'])
    end

    # Retrieve the Baseline6 Work value
    #
    # @return Baseline6 Work value
    def baseline6_work
      get_duration_value(attribute_values['baseline6_work'])
    end

    # Retrieve the Baseline7 Budget Cost value
    #
    # @return Baseline7 Budget Cost value
    def baseline7_budget_cost
      get_float_value(attribute_values['baseline7_budget_cost'])
    end

    # Retrieve the Baseline7 Budget Work value
    #
    # @return Baseline7 Budget Work value
    def baseline7_budget_work
      get_duration_value(attribute_values['baseline7_budget_work'])
    end

    # Retrieve the Baseline7 Cost value
    #
    # @return Baseline7 Cost value
    def baseline7_cost
      get_float_value(attribute_values['baseline7_cost'])
    end

    # Retrieve the Baseline7 Finish value
    #
    # @return Baseline7 Finish value
    def baseline7_finish
      get_date_value(attribute_values['baseline7_finish'])
    end

    # Retrieve the Baseline7 Start value
    #
    # @return Baseline7 Start value
    def baseline7_start
      get_date_value(attribute_values['baseline7_start'])
    end

    # Retrieve the Baseline7 Work value
    #
    # @return Baseline7 Work value
    def baseline7_work
      get_duration_value(attribute_values['baseline7_work'])
    end

    # Retrieve the Baseline8 Budget Cost value
    #
    # @return Baseline8 Budget Cost value
    def baseline8_budget_cost
      get_float_value(attribute_values['baseline8_budget_cost'])
    end

    # Retrieve the Baseline8 Budget Work value
    #
    # @return Baseline8 Budget Work value
    def baseline8_budget_work
      get_duration_value(attribute_values['baseline8_budget_work'])
    end

    # Retrieve the Baseline8 Cost value
    #
    # @return Baseline8 Cost value
    def baseline8_cost
      get_float_value(attribute_values['baseline8_cost'])
    end

    # Retrieve the Baseline8 Finish value
    #
    # @return Baseline8 Finish value
    def baseline8_finish
      get_date_value(attribute_values['baseline8_finish'])
    end

    # Retrieve the Baseline8 Start value
    #
    # @return Baseline8 Start value
    def baseline8_start
      get_date_value(attribute_values['baseline8_start'])
    end

    # Retrieve the Baseline8 Work value
    #
    # @return Baseline8 Work value
    def baseline8_work
      get_duration_value(attribute_values['baseline8_work'])
    end

    # Retrieve the Baseline9 Budget Cost value
    #
    # @return Baseline9 Budget Cost value
    def baseline9_budget_cost
      get_float_value(attribute_values['baseline9_budget_cost'])
    end

    # Retrieve the Baseline9 Budget Work value
    #
    # @return Baseline9 Budget Work value
    def baseline9_budget_work
      get_duration_value(attribute_values['baseline9_budget_work'])
    end

    # Retrieve the Baseline9 Cost value
    #
    # @return Baseline9 Cost value
    def baseline9_cost
      get_float_value(attribute_values['baseline9_cost'])
    end

    # Retrieve the Baseline9 Finish value
    #
    # @return Baseline9 Finish value
    def baseline9_finish
      get_date_value(attribute_values['baseline9_finish'])
    end

    # Retrieve the Baseline9 Start value
    #
    # @return Baseline9 Start value
    def baseline9_start
      get_date_value(attribute_values['baseline9_start'])
    end

    # Retrieve the Baseline9 Work value
    #
    # @return Baseline9 Work value
    def baseline9_work
      get_duration_value(attribute_values['baseline9_work'])
    end

    # Retrieve the Baseline Budget Cost value
    #
    # @return Baseline Budget Cost value
    def baseline_budget_cost
      get_float_value(attribute_values['baseline_budget_cost'])
    end

    # Retrieve the Baseline Budget Work value
    #
    # @return Baseline Budget Work value
    def baseline_budget_work
      get_duration_value(attribute_values['baseline_budget_work'])
    end

    # Retrieve the Baseline Cost value
    #
    # @return Baseline Cost value
    def baseline_cost
      get_float_value(attribute_values['baseline_cost'])
    end

    # Retrieve the Baseline Finish value
    #
    # @return Baseline Finish value
    def baseline_finish
      get_date_value(attribute_values['baseline_finish'])
    end

    # Retrieve the Baseline Start value
    #
    # @return Baseline Start value
    def baseline_start
      get_date_value(attribute_values['baseline_start'])
    end

    # Retrieve the Baseline Work value
    #
    # @return Baseline Work value
    def baseline_work
      get_duration_value(attribute_values['baseline_work'])
    end

    # Retrieve the Base Calendar value
    #
    # @return Base Calendar value
    def base_calendar
      attribute_values['base_calendar']
    end

    # Retrieve the BCWP value
    #
    # @return BCWP value
    def bcwp
      get_float_value(attribute_values['bcwp'])
    end

    # Retrieve the BCWS value
    #
    # @return BCWS value
    def bcws
      get_float_value(attribute_values['bcws'])
    end

    # Retrieve the Booking Type value
    #
    # @return Booking Type value
    def booking_type
      attribute_values['booking_type']
    end

    # Retrieve the Budget value
    #
    # @return Budget value
    def budget
      get_boolean_value(attribute_values['budget'])
    end

    # Retrieve the Budget Cost value
    #
    # @return Budget Cost value
    def budget_cost
      get_float_value(attribute_values['budget_cost'])
    end

    # Retrieve the Budget Work value
    #
    # @return Budget Work value
    def budget_work
      get_duration_value(attribute_values['budget_work'])
    end

    # Retrieve the Calculate Costs From Units value
    #
    # @return Calculate Costs From Units value
    def calculate_costs_from_units
      get_boolean_value(attribute_values['calculate_costs_from_units'])
    end

    # Retrieve the Calendar GUID value
    #
    # @return Calendar GUID value
    def calendar_guid
      attribute_values['calendar_guid']
    end

    # Retrieve the Calendar Unique ID value
    #
    # @return Calendar Unique ID value
    def calendar_unique_id
      get_integer_value(attribute_values['calendar_unique_id'])
    end

    # Retrieve the Can Level value
    #
    # @return Can Level value
    def can_level
      get_boolean_value(attribute_values['can_level'])
    end

    # Retrieve the Code value
    #
    # @return Code value
    def code
      attribute_values['code']
    end

    # Retrieve the Confirmed value
    #
    # @return Confirmed value
    def confirmed
      get_boolean_value(attribute_values['confirmed'])
    end

    # Retrieve the Cost value
    #
    # @return Cost value
    def cost
      get_float_value(attribute_values['cost'])
    end

    # Retrieve the Cost1 value
    #
    # @return Cost1 value
    def cost1
      get_float_value(attribute_values['cost1'])
    end

    # Retrieve the Cost10 value
    #
    # @return Cost10 value
    def cost10
      get_float_value(attribute_values['cost10'])
    end

    # Retrieve the Cost2 value
    #
    # @return Cost2 value
    def cost2
      get_float_value(attribute_values['cost2'])
    end

    # Retrieve the Cost3 value
    #
    # @return Cost3 value
    def cost3
      get_float_value(attribute_values['cost3'])
    end

    # Retrieve the Cost4 value
    #
    # @return Cost4 value
    def cost4
      get_float_value(attribute_values['cost4'])
    end

    # Retrieve the Cost5 value
    #
    # @return Cost5 value
    def cost5
      get_float_value(attribute_values['cost5'])
    end

    # Retrieve the Cost6 value
    #
    # @return Cost6 value
    def cost6
      get_float_value(attribute_values['cost6'])
    end

    # Retrieve the Cost7 value
    #
    # @return Cost7 value
    def cost7
      get_float_value(attribute_values['cost7'])
    end

    # Retrieve the Cost8 value
    #
    # @return Cost8 value
    def cost8
      get_float_value(attribute_values['cost8'])
    end

    # Retrieve the Cost9 value
    #
    # @return Cost9 value
    def cost9
      get_float_value(attribute_values['cost9'])
    end

    # Retrieve the Cost Center value
    #
    # @return Cost Center value
    def cost_center
      attribute_values['cost_center']
    end

    # Retrieve the Cost Per Use value
    #
    # @return Cost Per Use value
    def cost_per_use
      get_float_value(attribute_values['cost_per_use'])
    end

    # Retrieve the Cost Rate A value
    #
    # @return Cost Rate A value
    def cost_rate_a
      attribute_values['cost_rate_a']
    end

    # Retrieve the Cost Rate B value
    #
    # @return Cost Rate B value
    def cost_rate_b
      attribute_values['cost_rate_b']
    end

    # Retrieve the Cost Rate C value
    #
    # @return Cost Rate C value
    def cost_rate_c
      attribute_values['cost_rate_c']
    end

    # Retrieve the Cost Rate D value
    #
    # @return Cost Rate D value
    def cost_rate_d
      attribute_values['cost_rate_d']
    end

    # Retrieve the Cost Rate E value
    #
    # @return Cost Rate E value
    def cost_rate_e
      attribute_values['cost_rate_e']
    end

    # Retrieve the Cost Rate Table value
    #
    # @return Cost Rate Table value
    def cost_rate_table
      get_integer_value(attribute_values['cost_rate_table'])
    end

    # Retrieve the Cost Variance value
    #
    # @return Cost Variance value
    def cost_variance
      get_float_value(attribute_values['cost_variance'])
    end

    # Retrieve the Created value
    #
    # @return Created value
    def created
      get_date_value(attribute_values['created'])
    end

    # Retrieve the Currency Unique ID value
    #
    # @return Currency Unique ID value
    def currency_unique_id
      get_integer_value(attribute_values['currency_unique_id'])
    end

    # Retrieve the CV value
    #
    # @return CV value
    def cv
      get_float_value(attribute_values['cv'])
    end

    # Retrieve the Date1 value
    #
    # @return Date1 value
    def date1
      get_date_value(attribute_values['date1'])
    end

    # Retrieve the Date10 value
    #
    # @return Date10 value
    def date10
      get_date_value(attribute_values['date10'])
    end

    # Retrieve the Date2 value
    #
    # @return Date2 value
    def date2
      get_date_value(attribute_values['date2'])
    end

    # Retrieve the Date3 value
    #
    # @return Date3 value
    def date3
      get_date_value(attribute_values['date3'])
    end

    # Retrieve the Date4 value
    #
    # @return Date4 value
    def date4
      get_date_value(attribute_values['date4'])
    end

    # Retrieve the Date5 value
    #
    # @return Date5 value
    def date5
      get_date_value(attribute_values['date5'])
    end

    # Retrieve the Date6 value
    #
    # @return Date6 value
    def date6
      get_date_value(attribute_values['date6'])
    end

    # Retrieve the Date7 value
    #
    # @return Date7 value
    def date7
      get_date_value(attribute_values['date7'])
    end

    # Retrieve the Date8 value
    #
    # @return Date8 value
    def date8
      get_date_value(attribute_values['date8'])
    end

    # Retrieve the Date9 value
    #
    # @return Date9 value
    def date9
      get_date_value(attribute_values['date9'])
    end

    # Retrieve the Default Assignment Owner value
    #
    # @return Default Assignment Owner value
    def default_assignment_owner
      attribute_values['default_assignment_owner']
    end

    # Retrieve the Default Units value
    #
    # @return Default Units value
    def default_units
      get_float_value(attribute_values['default_units'])
    end

    # Retrieve the Description value
    #
    # @return Description value
    def description
      attribute_values['description']
    end

    # Retrieve the Duration1 value
    #
    # @return Duration1 value
    def duration1
      get_duration_value(attribute_values['duration1'])
    end

    # Retrieve the Duration10 value
    #
    # @return Duration10 value
    def duration10
      get_duration_value(attribute_values['duration10'])
    end

    # Retrieve the Duration10 Units value
    #
    # @return Duration10 Units value
    def duration10_units
      attribute_values['duration10_units']
    end

    # Retrieve the Duration1 Units value
    #
    # @return Duration1 Units value
    def duration1_units
      attribute_values['duration1_units']
    end

    # Retrieve the Duration2 value
    #
    # @return Duration2 value
    def duration2
      get_duration_value(attribute_values['duration2'])
    end

    # Retrieve the Duration2 Units value
    #
    # @return Duration2 Units value
    def duration2_units
      attribute_values['duration2_units']
    end

    # Retrieve the Duration3 value
    #
    # @return Duration3 value
    def duration3
      get_duration_value(attribute_values['duration3'])
    end

    # Retrieve the Duration3 Units value
    #
    # @return Duration3 Units value
    def duration3_units
      attribute_values['duration3_units']
    end

    # Retrieve the Duration4 value
    #
    # @return Duration4 value
    def duration4
      get_duration_value(attribute_values['duration4'])
    end

    # Retrieve the Duration4 Units value
    #
    # @return Duration4 Units value
    def duration4_units
      attribute_values['duration4_units']
    end

    # Retrieve the Duration5 value
    #
    # @return Duration5 value
    def duration5
      get_duration_value(attribute_values['duration5'])
    end

    # Retrieve the Duration5 Units value
    #
    # @return Duration5 Units value
    def duration5_units
      attribute_values['duration5_units']
    end

    # Retrieve the Duration6 value
    #
    # @return Duration6 value
    def duration6
      get_duration_value(attribute_values['duration6'])
    end

    # Retrieve the Duration6 Units value
    #
    # @return Duration6 Units value
    def duration6_units
      attribute_values['duration6_units']
    end

    # Retrieve the Duration7 value
    #
    # @return Duration7 value
    def duration7
      get_duration_value(attribute_values['duration7'])
    end

    # Retrieve the Duration7 Units value
    #
    # @return Duration7 Units value
    def duration7_units
      attribute_values['duration7_units']
    end

    # Retrieve the Duration8 value
    #
    # @return Duration8 value
    def duration8
      get_duration_value(attribute_values['duration8'])
    end

    # Retrieve the Duration8 Units value
    #
    # @return Duration8 Units value
    def duration8_units
      attribute_values['duration8_units']
    end

    # Retrieve the Duration9 value
    #
    # @return Duration9 value
    def duration9
      get_duration_value(attribute_values['duration9'])
    end

    # Retrieve the Duration9 Units value
    #
    # @return Duration9 Units value
    def duration9_units
      attribute_values['duration9_units']
    end

    # Retrieve the Email Address value
    #
    # @return Email Address value
    def email_address
      attribute_values['email_address']
    end

    # Retrieve the Engagement Status value
    #
    # @return Engagement Status value
    def engagement_status
      attribute_values['engagement_status']
    end

    # Retrieve the Enterprise value
    #
    # @return Enterprise value
    def enterprise
      get_boolean_value(attribute_values['enterprise'])
    end

    # Retrieve the Enterprise Base Calendar value
    #
    # @return Enterprise Base Calendar value
    def enterprise_base_calendar
      get_boolean_value(attribute_values['enterprise_base_calendar'])
    end

    # Retrieve the Enterprise Checked Out By value
    #
    # @return Enterprise Checked Out By value
    def enterprise_checked_out_by
      attribute_values['enterprise_checked_out_by']
    end

    # Retrieve the Enterprise Cost1 value
    #
    # @return Enterprise Cost1 value
    def enterprise_cost1
      get_float_value(attribute_values['enterprise_cost1'])
    end

    # Retrieve the Enterprise Cost10 value
    #
    # @return Enterprise Cost10 value
    def enterprise_cost10
      get_float_value(attribute_values['enterprise_cost10'])
    end

    # Retrieve the Enterprise Cost2 value
    #
    # @return Enterprise Cost2 value
    def enterprise_cost2
      get_float_value(attribute_values['enterprise_cost2'])
    end

    # Retrieve the Enterprise Cost3 value
    #
    # @return Enterprise Cost3 value
    def enterprise_cost3
      get_float_value(attribute_values['enterprise_cost3'])
    end

    # Retrieve the Enterprise Cost4 value
    #
    # @return Enterprise Cost4 value
    def enterprise_cost4
      get_float_value(attribute_values['enterprise_cost4'])
    end

    # Retrieve the Enterprise Cost5 value
    #
    # @return Enterprise Cost5 value
    def enterprise_cost5
      get_float_value(attribute_values['enterprise_cost5'])
    end

    # Retrieve the Enterprise Cost6 value
    #
    # @return Enterprise Cost6 value
    def enterprise_cost6
      get_float_value(attribute_values['enterprise_cost6'])
    end

    # Retrieve the Enterprise Cost7 value
    #
    # @return Enterprise Cost7 value
    def enterprise_cost7
      get_float_value(attribute_values['enterprise_cost7'])
    end

    # Retrieve the Enterprise Cost8 value
    #
    # @return Enterprise Cost8 value
    def enterprise_cost8
      get_float_value(attribute_values['enterprise_cost8'])
    end

    # Retrieve the Enterprise Cost9 value
    #
    # @return Enterprise Cost9 value
    def enterprise_cost9
      get_float_value(attribute_values['enterprise_cost9'])
    end

    # Retrieve the Enterprise Data value
    #
    # @return Enterprise Data value
    def enterprise_data
      attribute_values['enterprise_data']
    end

    # Retrieve the Enterprise Date1 value
    #
    # @return Enterprise Date1 value
    def enterprise_date1
      get_date_value(attribute_values['enterprise_date1'])
    end

    # Retrieve the Enterprise Date10 value
    #
    # @return Enterprise Date10 value
    def enterprise_date10
      get_date_value(attribute_values['enterprise_date10'])
    end

    # Retrieve the Enterprise Date11 value
    #
    # @return Enterprise Date11 value
    def enterprise_date11
      get_date_value(attribute_values['enterprise_date11'])
    end

    # Retrieve the Enterprise Date12 value
    #
    # @return Enterprise Date12 value
    def enterprise_date12
      get_date_value(attribute_values['enterprise_date12'])
    end

    # Retrieve the Enterprise Date13 value
    #
    # @return Enterprise Date13 value
    def enterprise_date13
      get_date_value(attribute_values['enterprise_date13'])
    end

    # Retrieve the Enterprise Date14 value
    #
    # @return Enterprise Date14 value
    def enterprise_date14
      get_date_value(attribute_values['enterprise_date14'])
    end

    # Retrieve the Enterprise Date15 value
    #
    # @return Enterprise Date15 value
    def enterprise_date15
      get_date_value(attribute_values['enterprise_date15'])
    end

    # Retrieve the Enterprise Date16 value
    #
    # @return Enterprise Date16 value
    def enterprise_date16
      get_date_value(attribute_values['enterprise_date16'])
    end

    # Retrieve the Enterprise Date17 value
    #
    # @return Enterprise Date17 value
    def enterprise_date17
      get_date_value(attribute_values['enterprise_date17'])
    end

    # Retrieve the Enterprise Date18 value
    #
    # @return Enterprise Date18 value
    def enterprise_date18
      get_date_value(attribute_values['enterprise_date18'])
    end

    # Retrieve the Enterprise Date19 value
    #
    # @return Enterprise Date19 value
    def enterprise_date19
      get_date_value(attribute_values['enterprise_date19'])
    end

    # Retrieve the Enterprise Date2 value
    #
    # @return Enterprise Date2 value
    def enterprise_date2
      get_date_value(attribute_values['enterprise_date2'])
    end

    # Retrieve the Enterprise Date20 value
    #
    # @return Enterprise Date20 value
    def enterprise_date20
      get_date_value(attribute_values['enterprise_date20'])
    end

    # Retrieve the Enterprise Date21 value
    #
    # @return Enterprise Date21 value
    def enterprise_date21
      get_date_value(attribute_values['enterprise_date21'])
    end

    # Retrieve the Enterprise Date22 value
    #
    # @return Enterprise Date22 value
    def enterprise_date22
      get_date_value(attribute_values['enterprise_date22'])
    end

    # Retrieve the Enterprise Date23 value
    #
    # @return Enterprise Date23 value
    def enterprise_date23
      get_date_value(attribute_values['enterprise_date23'])
    end

    # Retrieve the Enterprise Date24 value
    #
    # @return Enterprise Date24 value
    def enterprise_date24
      get_date_value(attribute_values['enterprise_date24'])
    end

    # Retrieve the Enterprise Date25 value
    #
    # @return Enterprise Date25 value
    def enterprise_date25
      get_date_value(attribute_values['enterprise_date25'])
    end

    # Retrieve the Enterprise Date26 value
    #
    # @return Enterprise Date26 value
    def enterprise_date26
      get_date_value(attribute_values['enterprise_date26'])
    end

    # Retrieve the Enterprise Date27 value
    #
    # @return Enterprise Date27 value
    def enterprise_date27
      get_date_value(attribute_values['enterprise_date27'])
    end

    # Retrieve the Enterprise Date28 value
    #
    # @return Enterprise Date28 value
    def enterprise_date28
      get_date_value(attribute_values['enterprise_date28'])
    end

    # Retrieve the Enterprise Date29 value
    #
    # @return Enterprise Date29 value
    def enterprise_date29
      get_date_value(attribute_values['enterprise_date29'])
    end

    # Retrieve the Enterprise Date3 value
    #
    # @return Enterprise Date3 value
    def enterprise_date3
      get_date_value(attribute_values['enterprise_date3'])
    end

    # Retrieve the Enterprise Date30 value
    #
    # @return Enterprise Date30 value
    def enterprise_date30
      get_date_value(attribute_values['enterprise_date30'])
    end

    # Retrieve the Enterprise Date4 value
    #
    # @return Enterprise Date4 value
    def enterprise_date4
      get_date_value(attribute_values['enterprise_date4'])
    end

    # Retrieve the Enterprise Date5 value
    #
    # @return Enterprise Date5 value
    def enterprise_date5
      get_date_value(attribute_values['enterprise_date5'])
    end

    # Retrieve the Enterprise Date6 value
    #
    # @return Enterprise Date6 value
    def enterprise_date6
      get_date_value(attribute_values['enterprise_date6'])
    end

    # Retrieve the Enterprise Date7 value
    #
    # @return Enterprise Date7 value
    def enterprise_date7
      get_date_value(attribute_values['enterprise_date7'])
    end

    # Retrieve the Enterprise Date8 value
    #
    # @return Enterprise Date8 value
    def enterprise_date8
      get_date_value(attribute_values['enterprise_date8'])
    end

    # Retrieve the Enterprise Date9 value
    #
    # @return Enterprise Date9 value
    def enterprise_date9
      get_date_value(attribute_values['enterprise_date9'])
    end

    # Retrieve the Enterprise Duration1 value
    #
    # @return Enterprise Duration1 value
    def enterprise_duration1
      get_duration_value(attribute_values['enterprise_duration1'])
    end

    # Retrieve the Enterprise Duration10 value
    #
    # @return Enterprise Duration10 value
    def enterprise_duration10
      get_duration_value(attribute_values['enterprise_duration10'])
    end

    # Retrieve the Enterprise Duration10 Units value
    #
    # @return Enterprise Duration10 Units value
    def enterprise_duration10_units
      attribute_values['enterprise_duration10_units']
    end

    # Retrieve the Enterprise Duration1 Units value
    #
    # @return Enterprise Duration1 Units value
    def enterprise_duration1_units
      attribute_values['enterprise_duration1_units']
    end

    # Retrieve the Enterprise Duration2 value
    #
    # @return Enterprise Duration2 value
    def enterprise_duration2
      get_duration_value(attribute_values['enterprise_duration2'])
    end

    # Retrieve the Enterprise Duration2 Units value
    #
    # @return Enterprise Duration2 Units value
    def enterprise_duration2_units
      attribute_values['enterprise_duration2_units']
    end

    # Retrieve the Enterprise Duration3 value
    #
    # @return Enterprise Duration3 value
    def enterprise_duration3
      get_duration_value(attribute_values['enterprise_duration3'])
    end

    # Retrieve the Enterprise Duration3 Units value
    #
    # @return Enterprise Duration3 Units value
    def enterprise_duration3_units
      attribute_values['enterprise_duration3_units']
    end

    # Retrieve the Enterprise Duration4 value
    #
    # @return Enterprise Duration4 value
    def enterprise_duration4
      get_duration_value(attribute_values['enterprise_duration4'])
    end

    # Retrieve the Enterprise Duration4 Units value
    #
    # @return Enterprise Duration4 Units value
    def enterprise_duration4_units
      attribute_values['enterprise_duration4_units']
    end

    # Retrieve the Enterprise Duration5 value
    #
    # @return Enterprise Duration5 value
    def enterprise_duration5
      get_duration_value(attribute_values['enterprise_duration5'])
    end

    # Retrieve the Enterprise Duration5 Units value
    #
    # @return Enterprise Duration5 Units value
    def enterprise_duration5_units
      attribute_values['enterprise_duration5_units']
    end

    # Retrieve the Enterprise Duration6 value
    #
    # @return Enterprise Duration6 value
    def enterprise_duration6
      get_duration_value(attribute_values['enterprise_duration6'])
    end

    # Retrieve the Enterprise Duration6 Units value
    #
    # @return Enterprise Duration6 Units value
    def enterprise_duration6_units
      attribute_values['enterprise_duration6_units']
    end

    # Retrieve the Enterprise Duration7 value
    #
    # @return Enterprise Duration7 value
    def enterprise_duration7
      get_duration_value(attribute_values['enterprise_duration7'])
    end

    # Retrieve the Enterprise Duration7 Units value
    #
    # @return Enterprise Duration7 Units value
    def enterprise_duration7_units
      attribute_values['enterprise_duration7_units']
    end

    # Retrieve the Enterprise Duration8 value
    #
    # @return Enterprise Duration8 value
    def enterprise_duration8
      get_duration_value(attribute_values['enterprise_duration8'])
    end

    # Retrieve the Enterprise Duration8 Units value
    #
    # @return Enterprise Duration8 Units value
    def enterprise_duration8_units
      attribute_values['enterprise_duration8_units']
    end

    # Retrieve the Enterprise Duration9 value
    #
    # @return Enterprise Duration9 value
    def enterprise_duration9
      get_duration_value(attribute_values['enterprise_duration9'])
    end

    # Retrieve the Enterprise Duration9 Units value
    #
    # @return Enterprise Duration9 Units value
    def enterprise_duration9_units
      attribute_values['enterprise_duration9_units']
    end

    # Retrieve the Enterprise Flag1 value
    #
    # @return Enterprise Flag1 value
    def enterprise_flag1
      get_boolean_value(attribute_values['enterprise_flag1'])
    end

    # Retrieve the Enterprise Flag10 value
    #
    # @return Enterprise Flag10 value
    def enterprise_flag10
      get_boolean_value(attribute_values['enterprise_flag10'])
    end

    # Retrieve the Enterprise Flag11 value
    #
    # @return Enterprise Flag11 value
    def enterprise_flag11
      get_boolean_value(attribute_values['enterprise_flag11'])
    end

    # Retrieve the Enterprise Flag12 value
    #
    # @return Enterprise Flag12 value
    def enterprise_flag12
      get_boolean_value(attribute_values['enterprise_flag12'])
    end

    # Retrieve the Enterprise Flag13 value
    #
    # @return Enterprise Flag13 value
    def enterprise_flag13
      get_boolean_value(attribute_values['enterprise_flag13'])
    end

    # Retrieve the Enterprise Flag14 value
    #
    # @return Enterprise Flag14 value
    def enterprise_flag14
      get_boolean_value(attribute_values['enterprise_flag14'])
    end

    # Retrieve the Enterprise Flag15 value
    #
    # @return Enterprise Flag15 value
    def enterprise_flag15
      get_boolean_value(attribute_values['enterprise_flag15'])
    end

    # Retrieve the Enterprise Flag16 value
    #
    # @return Enterprise Flag16 value
    def enterprise_flag16
      get_boolean_value(attribute_values['enterprise_flag16'])
    end

    # Retrieve the Enterprise Flag17 value
    #
    # @return Enterprise Flag17 value
    def enterprise_flag17
      get_boolean_value(attribute_values['enterprise_flag17'])
    end

    # Retrieve the Enterprise Flag18 value
    #
    # @return Enterprise Flag18 value
    def enterprise_flag18
      get_boolean_value(attribute_values['enterprise_flag18'])
    end

    # Retrieve the Enterprise Flag19 value
    #
    # @return Enterprise Flag19 value
    def enterprise_flag19
      get_boolean_value(attribute_values['enterprise_flag19'])
    end

    # Retrieve the Enterprise Flag2 value
    #
    # @return Enterprise Flag2 value
    def enterprise_flag2
      get_boolean_value(attribute_values['enterprise_flag2'])
    end

    # Retrieve the Enterprise Flag20 value
    #
    # @return Enterprise Flag20 value
    def enterprise_flag20
      get_boolean_value(attribute_values['enterprise_flag20'])
    end

    # Retrieve the Enterprise Flag3 value
    #
    # @return Enterprise Flag3 value
    def enterprise_flag3
      get_boolean_value(attribute_values['enterprise_flag3'])
    end

    # Retrieve the Enterprise Flag4 value
    #
    # @return Enterprise Flag4 value
    def enterprise_flag4
      get_boolean_value(attribute_values['enterprise_flag4'])
    end

    # Retrieve the Enterprise Flag5 value
    #
    # @return Enterprise Flag5 value
    def enterprise_flag5
      get_boolean_value(attribute_values['enterprise_flag5'])
    end

    # Retrieve the Enterprise Flag6 value
    #
    # @return Enterprise Flag6 value
    def enterprise_flag6
      get_boolean_value(attribute_values['enterprise_flag6'])
    end

    # Retrieve the Enterprise Flag7 value
    #
    # @return Enterprise Flag7 value
    def enterprise_flag7
      get_boolean_value(attribute_values['enterprise_flag7'])
    end

    # Retrieve the Enterprise Flag8 value
    #
    # @return Enterprise Flag8 value
    def enterprise_flag8
      get_boolean_value(attribute_values['enterprise_flag8'])
    end

    # Retrieve the Enterprise Flag9 value
    #
    # @return Enterprise Flag9 value
    def enterprise_flag9
      get_boolean_value(attribute_values['enterprise_flag9'])
    end

    # Retrieve the Enterprise Is Checked Out value
    #
    # @return Enterprise Is Checked Out value
    def enterprise_is_checked_out
      get_boolean_value(attribute_values['enterprise_is_checked_out'])
    end

    # Retrieve the Enterprise Last Modified Date value
    #
    # @return Enterprise Last Modified Date value
    def enterprise_last_modified_date
      get_date_value(attribute_values['enterprise_last_modified_date'])
    end

    # Retrieve the Enterprise Multi Value20 value
    #
    # @return Enterprise Multi Value20 value
    def enterprise_multi_value20
      attribute_values['enterprise_multi_value20']
    end

    # Retrieve the Enterprise Multi Value21 value
    #
    # @return Enterprise Multi Value21 value
    def enterprise_multi_value21
      attribute_values['enterprise_multi_value21']
    end

    # Retrieve the Enterprise Multi Value22 value
    #
    # @return Enterprise Multi Value22 value
    def enterprise_multi_value22
      attribute_values['enterprise_multi_value22']
    end

    # Retrieve the Enterprise Multi Value23 value
    #
    # @return Enterprise Multi Value23 value
    def enterprise_multi_value23
      attribute_values['enterprise_multi_value23']
    end

    # Retrieve the Enterprise Multi Value24 value
    #
    # @return Enterprise Multi Value24 value
    def enterprise_multi_value24
      attribute_values['enterprise_multi_value24']
    end

    # Retrieve the Enterprise Multi Value25 value
    #
    # @return Enterprise Multi Value25 value
    def enterprise_multi_value25
      attribute_values['enterprise_multi_value25']
    end

    # Retrieve the Enterprise Multi Value26 value
    #
    # @return Enterprise Multi Value26 value
    def enterprise_multi_value26
      attribute_values['enterprise_multi_value26']
    end

    # Retrieve the Enterprise Multi Value27 value
    #
    # @return Enterprise Multi Value27 value
    def enterprise_multi_value27
      attribute_values['enterprise_multi_value27']
    end

    # Retrieve the Enterprise Multi Value28 value
    #
    # @return Enterprise Multi Value28 value
    def enterprise_multi_value28
      attribute_values['enterprise_multi_value28']
    end

    # Retrieve the Enterprise Multi Value29 value
    #
    # @return Enterprise Multi Value29 value
    def enterprise_multi_value29
      attribute_values['enterprise_multi_value29']
    end

    # Retrieve the Enterprise Name Used value
    #
    # @return Enterprise Name Used value
    def enterprise_name_used
      attribute_values['enterprise_name_used']
    end

    # Retrieve the Enterprise Number1 value
    #
    # @return Enterprise Number1 value
    def enterprise_number1
      get_float_value(attribute_values['enterprise_number1'])
    end

    # Retrieve the Enterprise Number10 value
    #
    # @return Enterprise Number10 value
    def enterprise_number10
      get_float_value(attribute_values['enterprise_number10'])
    end

    # Retrieve the Enterprise Number11 value
    #
    # @return Enterprise Number11 value
    def enterprise_number11
      get_float_value(attribute_values['enterprise_number11'])
    end

    # Retrieve the Enterprise Number12 value
    #
    # @return Enterprise Number12 value
    def enterprise_number12
      get_float_value(attribute_values['enterprise_number12'])
    end

    # Retrieve the Enterprise Number13 value
    #
    # @return Enterprise Number13 value
    def enterprise_number13
      get_float_value(attribute_values['enterprise_number13'])
    end

    # Retrieve the Enterprise Number14 value
    #
    # @return Enterprise Number14 value
    def enterprise_number14
      get_float_value(attribute_values['enterprise_number14'])
    end

    # Retrieve the Enterprise Number15 value
    #
    # @return Enterprise Number15 value
    def enterprise_number15
      get_float_value(attribute_values['enterprise_number15'])
    end

    # Retrieve the Enterprise Number16 value
    #
    # @return Enterprise Number16 value
    def enterprise_number16
      get_float_value(attribute_values['enterprise_number16'])
    end

    # Retrieve the Enterprise Number17 value
    #
    # @return Enterprise Number17 value
    def enterprise_number17
      get_float_value(attribute_values['enterprise_number17'])
    end

    # Retrieve the Enterprise Number18 value
    #
    # @return Enterprise Number18 value
    def enterprise_number18
      get_float_value(attribute_values['enterprise_number18'])
    end

    # Retrieve the Enterprise Number19 value
    #
    # @return Enterprise Number19 value
    def enterprise_number19
      get_float_value(attribute_values['enterprise_number19'])
    end

    # Retrieve the Enterprise Number2 value
    #
    # @return Enterprise Number2 value
    def enterprise_number2
      get_float_value(attribute_values['enterprise_number2'])
    end

    # Retrieve the Enterprise Number20 value
    #
    # @return Enterprise Number20 value
    def enterprise_number20
      get_float_value(attribute_values['enterprise_number20'])
    end

    # Retrieve the Enterprise Number21 value
    #
    # @return Enterprise Number21 value
    def enterprise_number21
      get_float_value(attribute_values['enterprise_number21'])
    end

    # Retrieve the Enterprise Number22 value
    #
    # @return Enterprise Number22 value
    def enterprise_number22
      get_float_value(attribute_values['enterprise_number22'])
    end

    # Retrieve the Enterprise Number23 value
    #
    # @return Enterprise Number23 value
    def enterprise_number23
      get_float_value(attribute_values['enterprise_number23'])
    end

    # Retrieve the Enterprise Number24 value
    #
    # @return Enterprise Number24 value
    def enterprise_number24
      get_float_value(attribute_values['enterprise_number24'])
    end

    # Retrieve the Enterprise Number25 value
    #
    # @return Enterprise Number25 value
    def enterprise_number25
      get_float_value(attribute_values['enterprise_number25'])
    end

    # Retrieve the Enterprise Number26 value
    #
    # @return Enterprise Number26 value
    def enterprise_number26
      get_float_value(attribute_values['enterprise_number26'])
    end

    # Retrieve the Enterprise Number27 value
    #
    # @return Enterprise Number27 value
    def enterprise_number27
      get_float_value(attribute_values['enterprise_number27'])
    end

    # Retrieve the Enterprise Number28 value
    #
    # @return Enterprise Number28 value
    def enterprise_number28
      get_float_value(attribute_values['enterprise_number28'])
    end

    # Retrieve the Enterprise Number29 value
    #
    # @return Enterprise Number29 value
    def enterprise_number29
      get_float_value(attribute_values['enterprise_number29'])
    end

    # Retrieve the Enterprise Number3 value
    #
    # @return Enterprise Number3 value
    def enterprise_number3
      get_float_value(attribute_values['enterprise_number3'])
    end

    # Retrieve the Enterprise Number30 value
    #
    # @return Enterprise Number30 value
    def enterprise_number30
      get_float_value(attribute_values['enterprise_number30'])
    end

    # Retrieve the Enterprise Number31 value
    #
    # @return Enterprise Number31 value
    def enterprise_number31
      get_float_value(attribute_values['enterprise_number31'])
    end

    # Retrieve the Enterprise Number32 value
    #
    # @return Enterprise Number32 value
    def enterprise_number32
      get_float_value(attribute_values['enterprise_number32'])
    end

    # Retrieve the Enterprise Number33 value
    #
    # @return Enterprise Number33 value
    def enterprise_number33
      get_float_value(attribute_values['enterprise_number33'])
    end

    # Retrieve the Enterprise Number34 value
    #
    # @return Enterprise Number34 value
    def enterprise_number34
      get_float_value(attribute_values['enterprise_number34'])
    end

    # Retrieve the Enterprise Number35 value
    #
    # @return Enterprise Number35 value
    def enterprise_number35
      get_float_value(attribute_values['enterprise_number35'])
    end

    # Retrieve the Enterprise Number36 value
    #
    # @return Enterprise Number36 value
    def enterprise_number36
      get_float_value(attribute_values['enterprise_number36'])
    end

    # Retrieve the Enterprise Number37 value
    #
    # @return Enterprise Number37 value
    def enterprise_number37
      get_float_value(attribute_values['enterprise_number37'])
    end

    # Retrieve the Enterprise Number38 value
    #
    # @return Enterprise Number38 value
    def enterprise_number38
      get_float_value(attribute_values['enterprise_number38'])
    end

    # Retrieve the Enterprise Number39 value
    #
    # @return Enterprise Number39 value
    def enterprise_number39
      get_float_value(attribute_values['enterprise_number39'])
    end

    # Retrieve the Enterprise Number4 value
    #
    # @return Enterprise Number4 value
    def enterprise_number4
      get_float_value(attribute_values['enterprise_number4'])
    end

    # Retrieve the Enterprise Number40 value
    #
    # @return Enterprise Number40 value
    def enterprise_number40
      get_float_value(attribute_values['enterprise_number40'])
    end

    # Retrieve the Enterprise Number5 value
    #
    # @return Enterprise Number5 value
    def enterprise_number5
      get_float_value(attribute_values['enterprise_number5'])
    end

    # Retrieve the Enterprise Number6 value
    #
    # @return Enterprise Number6 value
    def enterprise_number6
      get_float_value(attribute_values['enterprise_number6'])
    end

    # Retrieve the Enterprise Number7 value
    #
    # @return Enterprise Number7 value
    def enterprise_number7
      get_float_value(attribute_values['enterprise_number7'])
    end

    # Retrieve the Enterprise Number8 value
    #
    # @return Enterprise Number8 value
    def enterprise_number8
      get_float_value(attribute_values['enterprise_number8'])
    end

    # Retrieve the Enterprise Number9 value
    #
    # @return Enterprise Number9 value
    def enterprise_number9
      get_float_value(attribute_values['enterprise_number9'])
    end

    # Retrieve the Enterprise Outline Code1 value
    #
    # @return Enterprise Outline Code1 value
    def enterprise_outline_code1
      attribute_values['enterprise_outline_code1']
    end

    # Retrieve the Enterprise Outline Code10 value
    #
    # @return Enterprise Outline Code10 value
    def enterprise_outline_code10
      attribute_values['enterprise_outline_code10']
    end

    # Retrieve the Enterprise Outline Code11 value
    #
    # @return Enterprise Outline Code11 value
    def enterprise_outline_code11
      attribute_values['enterprise_outline_code11']
    end

    # Retrieve the Enterprise Outline Code12 value
    #
    # @return Enterprise Outline Code12 value
    def enterprise_outline_code12
      attribute_values['enterprise_outline_code12']
    end

    # Retrieve the Enterprise Outline Code13 value
    #
    # @return Enterprise Outline Code13 value
    def enterprise_outline_code13
      attribute_values['enterprise_outline_code13']
    end

    # Retrieve the Enterprise Outline Code14 value
    #
    # @return Enterprise Outline Code14 value
    def enterprise_outline_code14
      attribute_values['enterprise_outline_code14']
    end

    # Retrieve the Enterprise Outline Code15 value
    #
    # @return Enterprise Outline Code15 value
    def enterprise_outline_code15
      attribute_values['enterprise_outline_code15']
    end

    # Retrieve the Enterprise Outline Code16 value
    #
    # @return Enterprise Outline Code16 value
    def enterprise_outline_code16
      attribute_values['enterprise_outline_code16']
    end

    # Retrieve the Enterprise Outline Code17 value
    #
    # @return Enterprise Outline Code17 value
    def enterprise_outline_code17
      attribute_values['enterprise_outline_code17']
    end

    # Retrieve the Enterprise Outline Code18 value
    #
    # @return Enterprise Outline Code18 value
    def enterprise_outline_code18
      attribute_values['enterprise_outline_code18']
    end

    # Retrieve the Enterprise Outline Code19 value
    #
    # @return Enterprise Outline Code19 value
    def enterprise_outline_code19
      attribute_values['enterprise_outline_code19']
    end

    # Retrieve the Enterprise Outline Code2 value
    #
    # @return Enterprise Outline Code2 value
    def enterprise_outline_code2
      attribute_values['enterprise_outline_code2']
    end

    # Retrieve the Enterprise Outline Code20 value
    #
    # @return Enterprise Outline Code20 value
    def enterprise_outline_code20
      attribute_values['enterprise_outline_code20']
    end

    # Retrieve the Enterprise Outline Code21 value
    #
    # @return Enterprise Outline Code21 value
    def enterprise_outline_code21
      attribute_values['enterprise_outline_code21']
    end

    # Retrieve the Enterprise Outline Code22 value
    #
    # @return Enterprise Outline Code22 value
    def enterprise_outline_code22
      attribute_values['enterprise_outline_code22']
    end

    # Retrieve the Enterprise Outline Code23 value
    #
    # @return Enterprise Outline Code23 value
    def enterprise_outline_code23
      attribute_values['enterprise_outline_code23']
    end

    # Retrieve the Enterprise Outline Code24 value
    #
    # @return Enterprise Outline Code24 value
    def enterprise_outline_code24
      attribute_values['enterprise_outline_code24']
    end

    # Retrieve the Enterprise Outline Code25 value
    #
    # @return Enterprise Outline Code25 value
    def enterprise_outline_code25
      attribute_values['enterprise_outline_code25']
    end

    # Retrieve the Enterprise Outline Code26 value
    #
    # @return Enterprise Outline Code26 value
    def enterprise_outline_code26
      attribute_values['enterprise_outline_code26']
    end

    # Retrieve the Enterprise Outline Code27 value
    #
    # @return Enterprise Outline Code27 value
    def enterprise_outline_code27
      attribute_values['enterprise_outline_code27']
    end

    # Retrieve the Enterprise Outline Code28 value
    #
    # @return Enterprise Outline Code28 value
    def enterprise_outline_code28
      attribute_values['enterprise_outline_code28']
    end

    # Retrieve the Enterprise Outline Code29 value
    #
    # @return Enterprise Outline Code29 value
    def enterprise_outline_code29
      attribute_values['enterprise_outline_code29']
    end

    # Retrieve the Enterprise Outline Code3 value
    #
    # @return Enterprise Outline Code3 value
    def enterprise_outline_code3
      attribute_values['enterprise_outline_code3']
    end

    # Retrieve the Enterprise Outline Code4 value
    #
    # @return Enterprise Outline Code4 value
    def enterprise_outline_code4
      attribute_values['enterprise_outline_code4']
    end

    # Retrieve the Enterprise Outline Code5 value
    #
    # @return Enterprise Outline Code5 value
    def enterprise_outline_code5
      attribute_values['enterprise_outline_code5']
    end

    # Retrieve the Enterprise Outline Code6 value
    #
    # @return Enterprise Outline Code6 value
    def enterprise_outline_code6
      attribute_values['enterprise_outline_code6']
    end

    # Retrieve the Enterprise Outline Code7 value
    #
    # @return Enterprise Outline Code7 value
    def enterprise_outline_code7
      attribute_values['enterprise_outline_code7']
    end

    # Retrieve the Enterprise Outline Code8 value
    #
    # @return Enterprise Outline Code8 value
    def enterprise_outline_code8
      attribute_values['enterprise_outline_code8']
    end

    # Retrieve the Enterprise Outline Code9 value
    #
    # @return Enterprise Outline Code9 value
    def enterprise_outline_code9
      attribute_values['enterprise_outline_code9']
    end

    # Retrieve the Enterprise RBS value
    #
    # @return Enterprise RBS value
    def enterprise_rbs
      attribute_values['enterprise_rbs']
    end

    # Retrieve the Enterprise Required Values value
    #
    # @return Enterprise Required Values value
    def enterprise_required_values
      get_boolean_value(attribute_values['enterprise_required_values'])
    end

    # Retrieve the Enterprise Team Member value
    #
    # @return Enterprise Team Member value
    def enterprise_team_member
      get_boolean_value(attribute_values['enterprise_team_member'])
    end

    # Retrieve the Enterprise Text1 value
    #
    # @return Enterprise Text1 value
    def enterprise_text1
      attribute_values['enterprise_text1']
    end

    # Retrieve the Enterprise Text10 value
    #
    # @return Enterprise Text10 value
    def enterprise_text10
      attribute_values['enterprise_text10']
    end

    # Retrieve the Enterprise Text11 value
    #
    # @return Enterprise Text11 value
    def enterprise_text11
      attribute_values['enterprise_text11']
    end

    # Retrieve the Enterprise Text12 value
    #
    # @return Enterprise Text12 value
    def enterprise_text12
      attribute_values['enterprise_text12']
    end

    # Retrieve the Enterprise Text13 value
    #
    # @return Enterprise Text13 value
    def enterprise_text13
      attribute_values['enterprise_text13']
    end

    # Retrieve the Enterprise Text14 value
    #
    # @return Enterprise Text14 value
    def enterprise_text14
      attribute_values['enterprise_text14']
    end

    # Retrieve the Enterprise Text15 value
    #
    # @return Enterprise Text15 value
    def enterprise_text15
      attribute_values['enterprise_text15']
    end

    # Retrieve the Enterprise Text16 value
    #
    # @return Enterprise Text16 value
    def enterprise_text16
      attribute_values['enterprise_text16']
    end

    # Retrieve the Enterprise Text17 value
    #
    # @return Enterprise Text17 value
    def enterprise_text17
      attribute_values['enterprise_text17']
    end

    # Retrieve the Enterprise Text18 value
    #
    # @return Enterprise Text18 value
    def enterprise_text18
      attribute_values['enterprise_text18']
    end

    # Retrieve the Enterprise Text19 value
    #
    # @return Enterprise Text19 value
    def enterprise_text19
      attribute_values['enterprise_text19']
    end

    # Retrieve the Enterprise Text2 value
    #
    # @return Enterprise Text2 value
    def enterprise_text2
      attribute_values['enterprise_text2']
    end

    # Retrieve the Enterprise Text20 value
    #
    # @return Enterprise Text20 value
    def enterprise_text20
      attribute_values['enterprise_text20']
    end

    # Retrieve the Enterprise Text21 value
    #
    # @return Enterprise Text21 value
    def enterprise_text21
      attribute_values['enterprise_text21']
    end

    # Retrieve the Enterprise Text22 value
    #
    # @return Enterprise Text22 value
    def enterprise_text22
      attribute_values['enterprise_text22']
    end

    # Retrieve the Enterprise Text23 value
    #
    # @return Enterprise Text23 value
    def enterprise_text23
      attribute_values['enterprise_text23']
    end

    # Retrieve the Enterprise Text24 value
    #
    # @return Enterprise Text24 value
    def enterprise_text24
      attribute_values['enterprise_text24']
    end

    # Retrieve the Enterprise Text25 value
    #
    # @return Enterprise Text25 value
    def enterprise_text25
      attribute_values['enterprise_text25']
    end

    # Retrieve the Enterprise Text26 value
    #
    # @return Enterprise Text26 value
    def enterprise_text26
      attribute_values['enterprise_text26']
    end

    # Retrieve the Enterprise Text27 value
    #
    # @return Enterprise Text27 value
    def enterprise_text27
      attribute_values['enterprise_text27']
    end

    # Retrieve the Enterprise Text28 value
    #
    # @return Enterprise Text28 value
    def enterprise_text28
      attribute_values['enterprise_text28']
    end

    # Retrieve the Enterprise Text29 value
    #
    # @return Enterprise Text29 value
    def enterprise_text29
      attribute_values['enterprise_text29']
    end

    # Retrieve the Enterprise Text3 value
    #
    # @return Enterprise Text3 value
    def enterprise_text3
      attribute_values['enterprise_text3']
    end

    # Retrieve the Enterprise Text30 value
    #
    # @return Enterprise Text30 value
    def enterprise_text30
      attribute_values['enterprise_text30']
    end

    # Retrieve the Enterprise Text31 value
    #
    # @return Enterprise Text31 value
    def enterprise_text31
      attribute_values['enterprise_text31']
    end

    # Retrieve the Enterprise Text32 value
    #
    # @return Enterprise Text32 value
    def enterprise_text32
      attribute_values['enterprise_text32']
    end

    # Retrieve the Enterprise Text33 value
    #
    # @return Enterprise Text33 value
    def enterprise_text33
      attribute_values['enterprise_text33']
    end

    # Retrieve the Enterprise Text34 value
    #
    # @return Enterprise Text34 value
    def enterprise_text34
      attribute_values['enterprise_text34']
    end

    # Retrieve the Enterprise Text35 value
    #
    # @return Enterprise Text35 value
    def enterprise_text35
      attribute_values['enterprise_text35']
    end

    # Retrieve the Enterprise Text36 value
    #
    # @return Enterprise Text36 value
    def enterprise_text36
      attribute_values['enterprise_text36']
    end

    # Retrieve the Enterprise Text37 value
    #
    # @return Enterprise Text37 value
    def enterprise_text37
      attribute_values['enterprise_text37']
    end

    # Retrieve the Enterprise Text38 value
    #
    # @return Enterprise Text38 value
    def enterprise_text38
      attribute_values['enterprise_text38']
    end

    # Retrieve the Enterprise Text39 value
    #
    # @return Enterprise Text39 value
    def enterprise_text39
      attribute_values['enterprise_text39']
    end

    # Retrieve the Enterprise Text4 value
    #
    # @return Enterprise Text4 value
    def enterprise_text4
      attribute_values['enterprise_text4']
    end

    # Retrieve the Enterprise Text40 value
    #
    # @return Enterprise Text40 value
    def enterprise_text40
      attribute_values['enterprise_text40']
    end

    # Retrieve the Enterprise Text5 value
    #
    # @return Enterprise Text5 value
    def enterprise_text5
      attribute_values['enterprise_text5']
    end

    # Retrieve the Enterprise Text6 value
    #
    # @return Enterprise Text6 value
    def enterprise_text6
      attribute_values['enterprise_text6']
    end

    # Retrieve the Enterprise Text7 value
    #
    # @return Enterprise Text7 value
    def enterprise_text7
      attribute_values['enterprise_text7']
    end

    # Retrieve the Enterprise Text8 value
    #
    # @return Enterprise Text8 value
    def enterprise_text8
      attribute_values['enterprise_text8']
    end

    # Retrieve the Enterprise Text9 value
    #
    # @return Enterprise Text9 value
    def enterprise_text9
      attribute_values['enterprise_text9']
    end

    # Retrieve the Enterprise Unique ID value
    #
    # @return Enterprise Unique ID value
    def enterprise_unique_id
      get_integer_value(attribute_values['enterprise_unique_id'])
    end

    # Retrieve the Error Message value
    #
    # @return Error Message value
    def error_message
      attribute_values['error_message']
    end

    # Retrieve the Expenses Only value
    #
    # @return Expenses Only value
    def expenses_only
      get_boolean_value(attribute_values['expenses_only'])
    end

    # Retrieve the Finish value
    #
    # @return Finish value
    def finish
      get_date_value(attribute_values['finish'])
    end

    # Retrieve the Finish1 value
    #
    # @return Finish1 value
    def finish1
      get_date_value(attribute_values['finish1'])
    end

    # Retrieve the Finish10 value
    #
    # @return Finish10 value
    def finish10
      get_date_value(attribute_values['finish10'])
    end

    # Retrieve the Finish2 value
    #
    # @return Finish2 value
    def finish2
      get_date_value(attribute_values['finish2'])
    end

    # Retrieve the Finish3 value
    #
    # @return Finish3 value
    def finish3
      get_date_value(attribute_values['finish3'])
    end

    # Retrieve the Finish4 value
    #
    # @return Finish4 value
    def finish4
      get_date_value(attribute_values['finish4'])
    end

    # Retrieve the Finish5 value
    #
    # @return Finish5 value
    def finish5
      get_date_value(attribute_values['finish5'])
    end

    # Retrieve the Finish6 value
    #
    # @return Finish6 value
    def finish6
      get_date_value(attribute_values['finish6'])
    end

    # Retrieve the Finish7 value
    #
    # @return Finish7 value
    def finish7
      get_date_value(attribute_values['finish7'])
    end

    # Retrieve the Finish8 value
    #
    # @return Finish8 value
    def finish8
      get_date_value(attribute_values['finish8'])
    end

    # Retrieve the Finish9 value
    #
    # @return Finish9 value
    def finish9
      get_date_value(attribute_values['finish9'])
    end

    # Retrieve the Flag1 value
    #
    # @return Flag1 value
    def flag1
      get_boolean_value(attribute_values['flag1'])
    end

    # Retrieve the Flag10 value
    #
    # @return Flag10 value
    def flag10
      get_boolean_value(attribute_values['flag10'])
    end

    # Retrieve the Flag11 value
    #
    # @return Flag11 value
    def flag11
      get_boolean_value(attribute_values['flag11'])
    end

    # Retrieve the Flag12 value
    #
    # @return Flag12 value
    def flag12
      get_boolean_value(attribute_values['flag12'])
    end

    # Retrieve the Flag13 value
    #
    # @return Flag13 value
    def flag13
      get_boolean_value(attribute_values['flag13'])
    end

    # Retrieve the Flag14 value
    #
    # @return Flag14 value
    def flag14
      get_boolean_value(attribute_values['flag14'])
    end

    # Retrieve the Flag15 value
    #
    # @return Flag15 value
    def flag15
      get_boolean_value(attribute_values['flag15'])
    end

    # Retrieve the Flag16 value
    #
    # @return Flag16 value
    def flag16
      get_boolean_value(attribute_values['flag16'])
    end

    # Retrieve the Flag17 value
    #
    # @return Flag17 value
    def flag17
      get_boolean_value(attribute_values['flag17'])
    end

    # Retrieve the Flag18 value
    #
    # @return Flag18 value
    def flag18
      get_boolean_value(attribute_values['flag18'])
    end

    # Retrieve the Flag19 value
    #
    # @return Flag19 value
    def flag19
      get_boolean_value(attribute_values['flag19'])
    end

    # Retrieve the Flag2 value
    #
    # @return Flag2 value
    def flag2
      get_boolean_value(attribute_values['flag2'])
    end

    # Retrieve the Flag20 value
    #
    # @return Flag20 value
    def flag20
      get_boolean_value(attribute_values['flag20'])
    end

    # Retrieve the Flag3 value
    #
    # @return Flag3 value
    def flag3
      get_boolean_value(attribute_values['flag3'])
    end

    # Retrieve the Flag4 value
    #
    # @return Flag4 value
    def flag4
      get_boolean_value(attribute_values['flag4'])
    end

    # Retrieve the Flag5 value
    #
    # @return Flag5 value
    def flag5
      get_boolean_value(attribute_values['flag5'])
    end

    # Retrieve the Flag6 value
    #
    # @return Flag6 value
    def flag6
      get_boolean_value(attribute_values['flag6'])
    end

    # Retrieve the Flag7 value
    #
    # @return Flag7 value
    def flag7
      get_boolean_value(attribute_values['flag7'])
    end

    # Retrieve the Flag8 value
    #
    # @return Flag8 value
    def flag8
      get_boolean_value(attribute_values['flag8'])
    end

    # Retrieve the Flag9 value
    #
    # @return Flag9 value
    def flag9
      get_boolean_value(attribute_values['flag9'])
    end

    # Retrieve the Generic value
    #
    # @return Generic value
    def generic
      get_boolean_value(attribute_values['generic'])
    end

    # Retrieve the Group value
    #
    # @return Group value
    def group
      attribute_values['group']
    end

    # Retrieve the Group By Summary value
    #
    # @return Group By Summary value
    def group_by_summary
      get_boolean_value(attribute_values['group_by_summary'])
    end

    # Retrieve the GUID value
    #
    # @return GUID value
    def guid
      attribute_values['guid']
    end

    # Retrieve the Hyperlink value
    #
    # @return Hyperlink value
    def hyperlink
      attribute_values['hyperlink']
    end

    # Retrieve the Hyperlink Address value
    #
    # @return Hyperlink Address value
    def hyperlink_address
      attribute_values['hyperlink_address']
    end

    # Retrieve the Hyperlink Data value
    #
    # @return Hyperlink Data value
    def hyperlink_data
      attribute_values['hyperlink_data']
    end

    # Retrieve the Hyperlink Href value
    #
    # @return Hyperlink Href value
    def hyperlink_href
      attribute_values['hyperlink_href']
    end

    # Retrieve the Hyperlink Screen Tip value
    #
    # @return Hyperlink Screen Tip value
    def hyperlink_screen_tip
      attribute_values['hyperlink_screen_tip']
    end

    # Retrieve the Hyperlink SubAddress value
    #
    # @return Hyperlink SubAddress value
    def hyperlink_subaddress
      attribute_values['hyperlink_subaddress']
    end

    # Retrieve the ID value
    #
    # @return ID value
    def id
      get_integer_value(attribute_values['id'])
    end

    # Retrieve the Import value
    #
    # @return Import value
    def import
      get_boolean_value(attribute_values['import'])
    end

    # Retrieve the Inactive value
    #
    # @return Inactive value
    def inactive
      get_boolean_value(attribute_values['inactive'])
    end

    # Retrieve the Index value
    #
    # @return Index value
    def index
      get_integer_value(attribute_values['index'])
    end

    # Retrieve the Indicators value
    #
    # @return Indicators value
    def indicators
      attribute_values['indicators']
    end

    # Retrieve the Initials value
    #
    # @return Initials value
    def initials
      attribute_values['initials']
    end

    # Retrieve the Leveling Delay value
    #
    # @return Leveling Delay value
    def leveling_delay
      get_duration_value(attribute_values['leveling_delay'])
    end

    # Retrieve the Linked Fields value
    #
    # @return Linked Fields value
    def linked_fields
      get_boolean_value(attribute_values['linked_fields'])
    end

    # Retrieve the Location Unique ID value
    #
    # @return Location Unique ID value
    def location_unique_id
      get_integer_value(attribute_values['location_unique_id'])
    end

    # Retrieve the Material Label value
    #
    # @return Material Label value
    def material_label
      attribute_values['material_label']
    end

    # Retrieve the Max Units value
    #
    # @return Max Units value
    def max_units
      get_float_value(attribute_values['max_units'])
    end

    # Retrieve the Modify On Integrate value
    #
    # @return Modify On Integrate value
    def modify_on_integrate
      get_boolean_value(attribute_values['modify_on_integrate'])
    end

    # Retrieve the Name value
    #
    # @return Name value
    def name
      attribute_values['name']
    end

    # Retrieve the Notes value
    #
    # @return Notes value
    def notes
      attribute_values['notes']
    end

    # Retrieve the Number1 value
    #
    # @return Number1 value
    def number1
      get_float_value(attribute_values['number1'])
    end

    # Retrieve the Number10 value
    #
    # @return Number10 value
    def number10
      get_float_value(attribute_values['number10'])
    end

    # Retrieve the Number11 value
    #
    # @return Number11 value
    def number11
      get_float_value(attribute_values['number11'])
    end

    # Retrieve the Number12 value
    #
    # @return Number12 value
    def number12
      get_float_value(attribute_values['number12'])
    end

    # Retrieve the Number13 value
    #
    # @return Number13 value
    def number13
      get_float_value(attribute_values['number13'])
    end

    # Retrieve the Number14 value
    #
    # @return Number14 value
    def number14
      get_float_value(attribute_values['number14'])
    end

    # Retrieve the Number15 value
    #
    # @return Number15 value
    def number15
      get_float_value(attribute_values['number15'])
    end

    # Retrieve the Number16 value
    #
    # @return Number16 value
    def number16
      get_float_value(attribute_values['number16'])
    end

    # Retrieve the Number17 value
    #
    # @return Number17 value
    def number17
      get_float_value(attribute_values['number17'])
    end

    # Retrieve the Number18 value
    #
    # @return Number18 value
    def number18
      get_float_value(attribute_values['number18'])
    end

    # Retrieve the Number19 value
    #
    # @return Number19 value
    def number19
      get_float_value(attribute_values['number19'])
    end

    # Retrieve the Number2 value
    #
    # @return Number2 value
    def number2
      get_float_value(attribute_values['number2'])
    end

    # Retrieve the Number20 value
    #
    # @return Number20 value
    def number20
      get_float_value(attribute_values['number20'])
    end

    # Retrieve the Number3 value
    #
    # @return Number3 value
    def number3
      get_float_value(attribute_values['number3'])
    end

    # Retrieve the Number4 value
    #
    # @return Number4 value
    def number4
      get_float_value(attribute_values['number4'])
    end

    # Retrieve the Number5 value
    #
    # @return Number5 value
    def number5
      get_float_value(attribute_values['number5'])
    end

    # Retrieve the Number6 value
    #
    # @return Number6 value
    def number6
      get_float_value(attribute_values['number6'])
    end

    # Retrieve the Number7 value
    #
    # @return Number7 value
    def number7
      get_float_value(attribute_values['number7'])
    end

    # Retrieve the Number8 value
    #
    # @return Number8 value
    def number8
      get_float_value(attribute_values['number8'])
    end

    # Retrieve the Number9 value
    #
    # @return Number9 value
    def number9
      get_float_value(attribute_values['number9'])
    end

    # Retrieve the Objects value
    #
    # @return Objects value
    def objects
      get_float_value(attribute_values['objects'])
    end

    # Retrieve the Outline Code1 value
    #
    # @return Outline Code1 value
    def outline_code1
      attribute_values['outline_code1']
    end

    # Retrieve the Outline Code10 value
    #
    # @return Outline Code10 value
    def outline_code10
      attribute_values['outline_code10']
    end

    # Retrieve the Outline Code10 Index value
    #
    # @return Outline Code10 Index value
    def outline_code10_index
      get_integer_value(attribute_values['outline_code10_index'])
    end

    # Retrieve the Outline Code1 Index value
    #
    # @return Outline Code1 Index value
    def outline_code1_index
      get_integer_value(attribute_values['outline_code1_index'])
    end

    # Retrieve the Outline Code2 value
    #
    # @return Outline Code2 value
    def outline_code2
      attribute_values['outline_code2']
    end

    # Retrieve the Outline Code2 Index value
    #
    # @return Outline Code2 Index value
    def outline_code2_index
      get_integer_value(attribute_values['outline_code2_index'])
    end

    # Retrieve the Outline Code3 value
    #
    # @return Outline Code3 value
    def outline_code3
      attribute_values['outline_code3']
    end

    # Retrieve the Outline Code3 Index value
    #
    # @return Outline Code3 Index value
    def outline_code3_index
      get_integer_value(attribute_values['outline_code3_index'])
    end

    # Retrieve the Outline Code4 value
    #
    # @return Outline Code4 value
    def outline_code4
      attribute_values['outline_code4']
    end

    # Retrieve the Outline Code4 Index value
    #
    # @return Outline Code4 Index value
    def outline_code4_index
      get_integer_value(attribute_values['outline_code4_index'])
    end

    # Retrieve the Outline Code5 value
    #
    # @return Outline Code5 value
    def outline_code5
      attribute_values['outline_code5']
    end

    # Retrieve the Outline Code5 Index value
    #
    # @return Outline Code5 Index value
    def outline_code5_index
      get_integer_value(attribute_values['outline_code5_index'])
    end

    # Retrieve the Outline Code6 value
    #
    # @return Outline Code6 value
    def outline_code6
      attribute_values['outline_code6']
    end

    # Retrieve the Outline Code6 Index value
    #
    # @return Outline Code6 Index value
    def outline_code6_index
      get_integer_value(attribute_values['outline_code6_index'])
    end

    # Retrieve the Outline Code7 value
    #
    # @return Outline Code7 value
    def outline_code7
      attribute_values['outline_code7']
    end

    # Retrieve the Outline Code7 Index value
    #
    # @return Outline Code7 Index value
    def outline_code7_index
      get_integer_value(attribute_values['outline_code7_index'])
    end

    # Retrieve the Outline Code8 value
    #
    # @return Outline Code8 value
    def outline_code8
      attribute_values['outline_code8']
    end

    # Retrieve the Outline Code8 Index value
    #
    # @return Outline Code8 Index value
    def outline_code8_index
      get_integer_value(attribute_values['outline_code8_index'])
    end

    # Retrieve the Outline Code9 value
    #
    # @return Outline Code9 value
    def outline_code9
      attribute_values['outline_code9']
    end

    # Retrieve the Outline Code9 Index value
    #
    # @return Outline Code9 Index value
    def outline_code9_index
      get_integer_value(attribute_values['outline_code9_index'])
    end

    # Retrieve the Overallocated value
    #
    # @return Overallocated value
    def overallocated
      get_boolean_value(attribute_values['overallocated'])
    end

    # Retrieve the Overtime Cost value
    #
    # @return Overtime Cost value
    def overtime_cost
      get_float_value(attribute_values['overtime_cost'])
    end

    # Retrieve the Overtime Rate value
    #
    # @return Overtime Rate value
    def overtime_rate
      attribute_values['overtime_rate']
    end

    # Retrieve the Overtime Rate Units value
    #
    # @return Overtime Rate Units value
    def overtime_rate_units
      attribute_values['overtime_rate_units']
    end

    # Retrieve the Overtime Work value
    #
    # @return Overtime Work value
    def overtime_work
      get_duration_value(attribute_values['overtime_work'])
    end

    # Retrieve the Parent ID value
    #
    # @return Parent ID value
    def parent_id
      get_integer_value(attribute_values['parent_id'])
    end

    # Retrieve the Peak value
    #
    # @return Peak value
    def peak
      get_float_value(attribute_values['peak'])
    end

    # Retrieve the % Work Complete value
    #
    # @return % Work Complete value
    def percent_work_complete
      get_float_value(attribute_values['percent_work_complete'])
    end

    # Retrieve the Period Dur value
    #
    # @return Period Dur value
    def period_dur
      get_float_value(attribute_values['period_dur'])
    end

    # Retrieve the Per Day value
    #
    # @return Per Day value
    def per_day
      get_float_value(attribute_values['per_day'])
    end

    # Retrieve the Phone value
    #
    # @return Phone value
    def phone
      attribute_values['phone']
    end

    # Retrieve the Phonetics value
    #
    # @return Phonetics value
    def phonetics
      attribute_values['phonetics']
    end

    # Retrieve the Pool value
    #
    # @return Pool value
    def pool
      get_float_value(attribute_values['pool'])
    end

    # Retrieve the Primary Role Unique ID value
    #
    # @return Primary Role Unique ID value
    def primary_role_unique_id
      get_integer_value(attribute_values['primary_role_unique_id'])
    end

    # Retrieve the Priority value
    #
    # @return Priority value
    def priority
      get_float_value(attribute_values['priority'])
    end

    # Retrieve the Project value
    #
    # @return Project value
    def project
      attribute_values['project']
    end

    # Retrieve the Proposed Finish value
    #
    # @return Proposed Finish value
    def proposed_finish
      get_date_value(attribute_values['proposed_finish'])
    end

    # Retrieve the Proposed Max Units value
    #
    # @return Proposed Max Units value
    def proposed_max_units
      get_float_value(attribute_values['proposed_max_units'])
    end

    # Retrieve the Proposed Start value
    #
    # @return Proposed Start value
    def proposed_start
      get_date_value(attribute_values['proposed_start'])
    end

    # Retrieve the Rate value
    #
    # @return Rate value
    def rate
      get_float_value(attribute_values['rate'])
    end

    # Retrieve the Regular Work value
    #
    # @return Regular Work value
    def regular_work
      get_duration_value(attribute_values['regular_work'])
    end

    # Retrieve the Remaining Cost value
    #
    # @return Remaining Cost value
    def remaining_cost
      get_float_value(attribute_values['remaining_cost'])
    end

    # Retrieve the Remaining Overtime Cost value
    #
    # @return Remaining Overtime Cost value
    def remaining_overtime_cost
      get_float_value(attribute_values['remaining_overtime_cost'])
    end

    # Retrieve the Remaining Overtime Work value
    #
    # @return Remaining Overtime Work value
    def remaining_overtime_work
      get_duration_value(attribute_values['remaining_overtime_work'])
    end

    # Retrieve the Remaining Work value
    #
    # @return Remaining Work value
    def remaining_work
      get_duration_value(attribute_values['remaining_work'])
    end

    # Retrieve the Request/Demand value
    #
    # @return Request/Demand value
    def request_demand
      attribute_values['request_demand']
    end

    # Retrieve the Resource Code Values value
    #
    # @return Resource Code Values value
    def resource_code_values
      attribute_values['resource_code_values']
    end

    # Retrieve the Resource ID value
    #
    # @return Resource ID value
    def resource_id
      attribute_values['resource_id']
    end

    # Retrieve the Response Pending value
    #
    # @return Response Pending value
    def response_pending
      get_boolean_value(attribute_values['response_pending'])
    end

    # Retrieve the Role value
    #
    # @return Role value
    def role
      get_boolean_value(attribute_values['role'])
    end

    # Retrieve the Role Code Values value
    #
    # @return Role Code Values value
    def role_code_values
      attribute_values['role_code_values']
    end

    # Retrieve the Sequence Number value
    #
    # @return Sequence Number value
    def sequence_number
      get_integer_value(attribute_values['sequence_number'])
    end

    # Retrieve the Shift Unique ID value
    #
    # @return Shift Unique ID value
    def shift_unique_id
      get_integer_value(attribute_values['shift_unique_id'])
    end

    # Retrieve the Standard Rate value
    #
    # @return Standard Rate value
    def standard_rate
      attribute_values['standard_rate']
    end

    # Retrieve the Standard Rate Units value
    #
    # @return Standard Rate Units value
    def standard_rate_units
      attribute_values['standard_rate_units']
    end

    # Retrieve the Start value
    #
    # @return Start value
    def start
      get_date_value(attribute_values['start'])
    end

    # Retrieve the Start1 value
    #
    # @return Start1 value
    def start1
      get_date_value(attribute_values['start1'])
    end

    # Retrieve the Start10 value
    #
    # @return Start10 value
    def start10
      get_date_value(attribute_values['start10'])
    end

    # Retrieve the Start2 value
    #
    # @return Start2 value
    def start2
      get_date_value(attribute_values['start2'])
    end

    # Retrieve the Start3 value
    #
    # @return Start3 value
    def start3
      get_date_value(attribute_values['start3'])
    end

    # Retrieve the Start4 value
    #
    # @return Start4 value
    def start4
      get_date_value(attribute_values['start4'])
    end

    # Retrieve the Start5 value
    #
    # @return Start5 value
    def start5
      get_date_value(attribute_values['start5'])
    end

    # Retrieve the Start6 value
    #
    # @return Start6 value
    def start6
      get_date_value(attribute_values['start6'])
    end

    # Retrieve the Start7 value
    #
    # @return Start7 value
    def start7
      get_date_value(attribute_values['start7'])
    end

    # Retrieve the Start8 value
    #
    # @return Start8 value
    def start8
      get_date_value(attribute_values['start8'])
    end

    # Retrieve the Start9 value
    #
    # @return Start9 value
    def start9
      get_date_value(attribute_values['start9'])
    end

    # Retrieve the Subproject Unique Resource ID value
    #
    # @return Subproject Unique Resource ID value
    def subproject_resource_unique_id
      get_integer_value(attribute_values['subproject_resource_unique_id'])
    end

    # Retrieve the Summary value
    #
    # @return Summary value
    def summary
      attribute_values['summary']
    end

    # Retrieve the Supply Reference value
    #
    # @return Supply Reference value
    def supply_reference
      attribute_values['supply_reference']
    end

    # Retrieve the SV value
    #
    # @return SV value
    def sv
      get_float_value(attribute_values['sv'])
    end

    # Retrieve the Task Outline Number value
    #
    # @return Task Outline Number value
    def task_outline_number
      attribute_values['task_outline_number']
    end

    # Retrieve the Task Summary Name value
    #
    # @return Task Summary Name value
    def task_summary_name
      attribute_values['task_summary_name']
    end

    # Retrieve the Team Assignment Pool value
    #
    # @return Team Assignment Pool value
    def team_assignment_pool
      get_boolean_value(attribute_values['team_assignment_pool'])
    end

    # Retrieve the TeamStatus Pending value
    #
    # @return TeamStatus Pending value
    def team_status_pending
      get_boolean_value(attribute_values['team_status_pending'])
    end

    # Retrieve the Text1 value
    #
    # @return Text1 value
    def text1
      attribute_values['text1']
    end

    # Retrieve the Text10 value
    #
    # @return Text10 value
    def text10
      attribute_values['text10']
    end

    # Retrieve the Text11 value
    #
    # @return Text11 value
    def text11
      attribute_values['text11']
    end

    # Retrieve the Text12 value
    #
    # @return Text12 value
    def text12
      attribute_values['text12']
    end

    # Retrieve the Text13 value
    #
    # @return Text13 value
    def text13
      attribute_values['text13']
    end

    # Retrieve the Text14 value
    #
    # @return Text14 value
    def text14
      attribute_values['text14']
    end

    # Retrieve the Text15 value
    #
    # @return Text15 value
    def text15
      attribute_values['text15']
    end

    # Retrieve the Text16 value
    #
    # @return Text16 value
    def text16
      attribute_values['text16']
    end

    # Retrieve the Text17 value
    #
    # @return Text17 value
    def text17
      attribute_values['text17']
    end

    # Retrieve the Text18 value
    #
    # @return Text18 value
    def text18
      attribute_values['text18']
    end

    # Retrieve the Text19 value
    #
    # @return Text19 value
    def text19
      attribute_values['text19']
    end

    # Retrieve the Text2 value
    #
    # @return Text2 value
    def text2
      attribute_values['text2']
    end

    # Retrieve the Text20 value
    #
    # @return Text20 value
    def text20
      attribute_values['text20']
    end

    # Retrieve the Text21 value
    #
    # @return Text21 value
    def text21
      attribute_values['text21']
    end

    # Retrieve the Text22 value
    #
    # @return Text22 value
    def text22
      attribute_values['text22']
    end

    # Retrieve the Text23 value
    #
    # @return Text23 value
    def text23
      attribute_values['text23']
    end

    # Retrieve the Text24 value
    #
    # @return Text24 value
    def text24
      attribute_values['text24']
    end

    # Retrieve the Text25 value
    #
    # @return Text25 value
    def text25
      attribute_values['text25']
    end

    # Retrieve the Text26 value
    #
    # @return Text26 value
    def text26
      attribute_values['text26']
    end

    # Retrieve the Text27 value
    #
    # @return Text27 value
    def text27
      attribute_values['text27']
    end

    # Retrieve the Text28 value
    #
    # @return Text28 value
    def text28
      attribute_values['text28']
    end

    # Retrieve the Text29 value
    #
    # @return Text29 value
    def text29
      attribute_values['text29']
    end

    # Retrieve the Text3 value
    #
    # @return Text3 value
    def text3
      attribute_values['text3']
    end

    # Retrieve the Text30 value
    #
    # @return Text30 value
    def text30
      attribute_values['text30']
    end

    # Retrieve the Text4 value
    #
    # @return Text4 value
    def text4
      attribute_values['text4']
    end

    # Retrieve the Text5 value
    #
    # @return Text5 value
    def text5
      attribute_values['text5']
    end

    # Retrieve the Text6 value
    #
    # @return Text6 value
    def text6
      attribute_values['text6']
    end

    # Retrieve the Text7 value
    #
    # @return Text7 value
    def text7
      attribute_values['text7']
    end

    # Retrieve the Text8 value
    #
    # @return Text8 value
    def text8
      attribute_values['text8']
    end

    # Retrieve the Text9 value
    #
    # @return Text9 value
    def text9
      attribute_values['text9']
    end

    # Retrieve the Type value
    #
    # @return Type value
    def type
      attribute_values['type']
    end

    # Retrieve the <Unavailable> value
    #
    # @return <Unavailable> value
    def unavailable
      attribute_values['unavailable']
    end

    # Retrieve the Unique ID value
    #
    # @return Unique ID value
    def unique_id
      get_integer_value(attribute_values['unique_id'])
    end

    # Retrieve the Unit value
    #
    # @return Unit value
    def unit
      attribute_values['unit']
    end

    # Retrieve the Unit of Measure Unique ID value
    #
    # @return Unit of Measure Unique ID value
    def unit_of_measure_unique_id
      get_integer_value(attribute_values['unit_of_measure_unique_id'])
    end

    # Retrieve the Update Needed value
    #
    # @return Update Needed value
    def update_needed
      get_boolean_value(attribute_values['update_needed'])
    end

    # Retrieve the VAC value
    #
    # @return VAC value
    def vac
      get_float_value(attribute_values['vac'])
    end

    # Retrieve the WBS value
    #
    # @return WBS value
    def wbs
      attribute_values['wbs']
    end

    # Retrieve the Windows User Account value
    #
    # @return Windows User Account value
    def windows_user_account
      attribute_values['windows_user_account']
    end

    # Retrieve the Work value
    #
    # @return Work value
    def work
      get_duration_value(attribute_values['work'])
    end

    # Retrieve the Workgroup value
    #
    # @return Workgroup value
    def workgroup
      attribute_values['workgroup']
    end

    # Retrieve the Work Contour value
    #
    # @return Work Contour value
    def work_contour
      attribute_values['work_contour']
    end

    # Retrieve the Work Variance value
    #
    # @return Work Variance value
    def work_variance
      get_duration_value(attribute_values['work_variance'])
    end

    ATTRIBUTE_TYPES = {
      'accrue_at' => :accrue,
      'active' => :boolean,
      'actual_cost' => :currency,
      'actual_finish' => :date,
      'actual_overtime_cost' => :currency,
      'actual_overtime_work' => :work,
      'actual_overtime_work_protected' => :work,
      'actual_start' => :date,
      'actual_work' => :work,
      'actual_work_protected' => :work,
      'acwp' => :currency,
      'assignment' => :boolean,
      'assignment_delay' => :delay,
      'assignment_owner' => :string,
      'assignment_units' => :units,
      'availability_data' => :binary,
      'available_from' => :date,
      'available_to' => :date,
      'baseline10_budget_cost' => :currency,
      'baseline10_budget_work' => :work,
      'baseline10_cost' => :currency,
      'baseline10_finish' => :date,
      'baseline10_start' => :date,
      'baseline10_work' => :work,
      'baseline1_budget_cost' => :currency,
      'baseline1_budget_work' => :work,
      'baseline1_cost' => :currency,
      'baseline1_finish' => :date,
      'baseline1_start' => :date,
      'baseline1_work' => :work,
      'baseline2_budget_cost' => :currency,
      'baseline2_budget_work' => :work,
      'baseline2_cost' => :currency,
      'baseline2_finish' => :date,
      'baseline2_start' => :date,
      'baseline2_work' => :work,
      'baseline3_budget_cost' => :currency,
      'baseline3_budget_work' => :work,
      'baseline3_cost' => :currency,
      'baseline3_finish' => :date,
      'baseline3_start' => :date,
      'baseline3_work' => :work,
      'baseline4_budget_cost' => :currency,
      'baseline4_budget_work' => :work,
      'baseline4_cost' => :currency,
      'baseline4_finish' => :date,
      'baseline4_start' => :date,
      'baseline4_work' => :work,
      'baseline5_budget_cost' => :currency,
      'baseline5_budget_work' => :work,
      'baseline5_cost' => :currency,
      'baseline5_finish' => :date,
      'baseline5_start' => :date,
      'baseline5_work' => :work,
      'baseline6_budget_cost' => :currency,
      'baseline6_budget_work' => :work,
      'baseline6_cost' => :currency,
      'baseline6_finish' => :date,
      'baseline6_start' => :date,
      'baseline6_work' => :work,
      'baseline7_budget_cost' => :currency,
      'baseline7_budget_work' => :work,
      'baseline7_cost' => :currency,
      'baseline7_finish' => :date,
      'baseline7_start' => :date,
      'baseline7_work' => :work,
      'baseline8_budget_cost' => :currency,
      'baseline8_budget_work' => :work,
      'baseline8_cost' => :currency,
      'baseline8_finish' => :date,
      'baseline8_start' => :date,
      'baseline8_work' => :work,
      'baseline9_budget_cost' => :currency,
      'baseline9_budget_work' => :work,
      'baseline9_cost' => :currency,
      'baseline9_finish' => :date,
      'baseline9_start' => :date,
      'baseline9_work' => :work,
      'baseline_budget_cost' => :currency,
      'baseline_budget_work' => :work,
      'baseline_cost' => :currency,
      'baseline_finish' => :date,
      'baseline_start' => :date,
      'baseline_work' => :work,
      'base_calendar' => :string,
      'bcwp' => :currency,
      'bcws' => :currency,
      'booking_type' => :booking_type,
      'budget' => :boolean,
      'budget_cost' => :currency,
      'budget_work' => :work,
      'calculate_costs_from_units' => :boolean,
      'calendar_guid' => :guid,
      'calendar_unique_id' => :integer,
      'can_level' => :boolean,
      'code' => :string,
      'confirmed' => :boolean,
      'cost' => :currency,
      'cost1' => :currency,
      'cost10' => :currency,
      'cost2' => :currency,
      'cost3' => :currency,
      'cost4' => :currency,
      'cost5' => :currency,
      'cost6' => :currency,
      'cost7' => :currency,
      'cost8' => :currency,
      'cost9' => :currency,
      'cost_center' => :string,
      'cost_per_use' => :currency,
      'cost_rate_a' => :binary,
      'cost_rate_b' => :binary,
      'cost_rate_c' => :binary,
      'cost_rate_d' => :binary,
      'cost_rate_e' => :binary,
      'cost_rate_table' => :short,
      'cost_variance' => :currency,
      'created' => :date,
      'currency_unique_id' => :integer,
      'cv' => :currency,
      'date1' => :date,
      'date10' => :date,
      'date2' => :date,
      'date3' => :date,
      'date4' => :date,
      'date5' => :date,
      'date6' => :date,
      'date7' => :date,
      'date8' => :date,
      'date9' => :date,
      'default_assignment_owner' => :string,
      'default_units' => :units,
      'description' => :string,
      'duration1' => :duration,
      'duration10' => :duration,
      'duration10_units' => :time_units,
      'duration1_units' => :time_units,
      'duration2' => :duration,
      'duration2_units' => :time_units,
      'duration3' => :duration,
      'duration3_units' => :time_units,
      'duration4' => :duration,
      'duration4_units' => :time_units,
      'duration5' => :duration,
      'duration5_units' => :time_units,
      'duration6' => :duration,
      'duration6_units' => :time_units,
      'duration7' => :duration,
      'duration7_units' => :time_units,
      'duration8' => :duration,
      'duration8_units' => :time_units,
      'duration9' => :duration,
      'duration9_units' => :time_units,
      'email_address' => :string,
      'engagement_status' => :string,
      'enterprise' => :boolean,
      'enterprise_base_calendar' => :boolean,
      'enterprise_checked_out_by' => :string,
      'enterprise_cost1' => :currency,
      'enterprise_cost10' => :currency,
      'enterprise_cost2' => :currency,
      'enterprise_cost3' => :currency,
      'enterprise_cost4' => :currency,
      'enterprise_cost5' => :currency,
      'enterprise_cost6' => :currency,
      'enterprise_cost7' => :currency,
      'enterprise_cost8' => :currency,
      'enterprise_cost9' => :currency,
      'enterprise_data' => :binary,
      'enterprise_date1' => :date,
      'enterprise_date10' => :date,
      'enterprise_date11' => :date,
      'enterprise_date12' => :date,
      'enterprise_date13' => :date,
      'enterprise_date14' => :date,
      'enterprise_date15' => :date,
      'enterprise_date16' => :date,
      'enterprise_date17' => :date,
      'enterprise_date18' => :date,
      'enterprise_date19' => :date,
      'enterprise_date2' => :date,
      'enterprise_date20' => :date,
      'enterprise_date21' => :date,
      'enterprise_date22' => :date,
      'enterprise_date23' => :date,
      'enterprise_date24' => :date,
      'enterprise_date25' => :date,
      'enterprise_date26' => :date,
      'enterprise_date27' => :date,
      'enterprise_date28' => :date,
      'enterprise_date29' => :date,
      'enterprise_date3' => :date,
      'enterprise_date30' => :date,
      'enterprise_date4' => :date,
      'enterprise_date5' => :date,
      'enterprise_date6' => :date,
      'enterprise_date7' => :date,
      'enterprise_date8' => :date,
      'enterprise_date9' => :date,
      'enterprise_duration1' => :duration,
      'enterprise_duration10' => :duration,
      'enterprise_duration10_units' => :time_units,
      'enterprise_duration1_units' => :time_units,
      'enterprise_duration2' => :duration,
      'enterprise_duration2_units' => :time_units,
      'enterprise_duration3' => :duration,
      'enterprise_duration3_units' => :time_units,
      'enterprise_duration4' => :duration,
      'enterprise_duration4_units' => :time_units,
      'enterprise_duration5' => :duration,
      'enterprise_duration5_units' => :time_units,
      'enterprise_duration6' => :duration,
      'enterprise_duration6_units' => :time_units,
      'enterprise_duration7' => :duration,
      'enterprise_duration7_units' => :time_units,
      'enterprise_duration8' => :duration,
      'enterprise_duration8_units' => :time_units,
      'enterprise_duration9' => :duration,
      'enterprise_duration9_units' => :time_units,
      'enterprise_flag1' => :boolean,
      'enterprise_flag10' => :boolean,
      'enterprise_flag11' => :boolean,
      'enterprise_flag12' => :boolean,
      'enterprise_flag13' => :boolean,
      'enterprise_flag14' => :boolean,
      'enterprise_flag15' => :boolean,
      'enterprise_flag16' => :boolean,
      'enterprise_flag17' => :boolean,
      'enterprise_flag18' => :boolean,
      'enterprise_flag19' => :boolean,
      'enterprise_flag2' => :boolean,
      'enterprise_flag20' => :boolean,
      'enterprise_flag3' => :boolean,
      'enterprise_flag4' => :boolean,
      'enterprise_flag5' => :boolean,
      'enterprise_flag6' => :boolean,
      'enterprise_flag7' => :boolean,
      'enterprise_flag8' => :boolean,
      'enterprise_flag9' => :boolean,
      'enterprise_is_checked_out' => :boolean,
      'enterprise_last_modified_date' => :date,
      'enterprise_multi_value20' => :string,
      'enterprise_multi_value21' => :string,
      'enterprise_multi_value22' => :string,
      'enterprise_multi_value23' => :string,
      'enterprise_multi_value24' => :string,
      'enterprise_multi_value25' => :string,
      'enterprise_multi_value26' => :string,
      'enterprise_multi_value27' => :string,
      'enterprise_multi_value28' => :string,
      'enterprise_multi_value29' => :string,
      'enterprise_name_used' => :string,
      'enterprise_number1' => :numeric,
      'enterprise_number10' => :numeric,
      'enterprise_number11' => :numeric,
      'enterprise_number12' => :numeric,
      'enterprise_number13' => :numeric,
      'enterprise_number14' => :numeric,
      'enterprise_number15' => :numeric,
      'enterprise_number16' => :numeric,
      'enterprise_number17' => :numeric,
      'enterprise_number18' => :numeric,
      'enterprise_number19' => :numeric,
      'enterprise_number2' => :numeric,
      'enterprise_number20' => :numeric,
      'enterprise_number21' => :numeric,
      'enterprise_number22' => :numeric,
      'enterprise_number23' => :numeric,
      'enterprise_number24' => :numeric,
      'enterprise_number25' => :numeric,
      'enterprise_number26' => :numeric,
      'enterprise_number27' => :numeric,
      'enterprise_number28' => :numeric,
      'enterprise_number29' => :numeric,
      'enterprise_number3' => :numeric,
      'enterprise_number30' => :numeric,
      'enterprise_number31' => :numeric,
      'enterprise_number32' => :numeric,
      'enterprise_number33' => :numeric,
      'enterprise_number34' => :numeric,
      'enterprise_number35' => :numeric,
      'enterprise_number36' => :numeric,
      'enterprise_number37' => :numeric,
      'enterprise_number38' => :numeric,
      'enterprise_number39' => :numeric,
      'enterprise_number4' => :numeric,
      'enterprise_number40' => :numeric,
      'enterprise_number5' => :numeric,
      'enterprise_number6' => :numeric,
      'enterprise_number7' => :numeric,
      'enterprise_number8' => :numeric,
      'enterprise_number9' => :numeric,
      'enterprise_outline_code1' => :string,
      'enterprise_outline_code10' => :string,
      'enterprise_outline_code11' => :string,
      'enterprise_outline_code12' => :string,
      'enterprise_outline_code13' => :string,
      'enterprise_outline_code14' => :string,
      'enterprise_outline_code15' => :string,
      'enterprise_outline_code16' => :string,
      'enterprise_outline_code17' => :string,
      'enterprise_outline_code18' => :string,
      'enterprise_outline_code19' => :string,
      'enterprise_outline_code2' => :string,
      'enterprise_outline_code20' => :string,
      'enterprise_outline_code21' => :string,
      'enterprise_outline_code22' => :string,
      'enterprise_outline_code23' => :string,
      'enterprise_outline_code24' => :string,
      'enterprise_outline_code25' => :string,
      'enterprise_outline_code26' => :string,
      'enterprise_outline_code27' => :string,
      'enterprise_outline_code28' => :string,
      'enterprise_outline_code29' => :string,
      'enterprise_outline_code3' => :string,
      'enterprise_outline_code4' => :string,
      'enterprise_outline_code5' => :string,
      'enterprise_outline_code6' => :string,
      'enterprise_outline_code7' => :string,
      'enterprise_outline_code8' => :string,
      'enterprise_outline_code9' => :string,
      'enterprise_rbs' => :string,
      'enterprise_required_values' => :boolean,
      'enterprise_team_member' => :boolean,
      'enterprise_text1' => :string,
      'enterprise_text10' => :string,
      'enterprise_text11' => :string,
      'enterprise_text12' => :string,
      'enterprise_text13' => :string,
      'enterprise_text14' => :string,
      'enterprise_text15' => :string,
      'enterprise_text16' => :string,
      'enterprise_text17' => :string,
      'enterprise_text18' => :string,
      'enterprise_text19' => :string,
      'enterprise_text2' => :string,
      'enterprise_text20' => :string,
      'enterprise_text21' => :string,
      'enterprise_text22' => :string,
      'enterprise_text23' => :string,
      'enterprise_text24' => :string,
      'enterprise_text25' => :string,
      'enterprise_text26' => :string,
      'enterprise_text27' => :string,
      'enterprise_text28' => :string,
      'enterprise_text29' => :string,
      'enterprise_text3' => :string,
      'enterprise_text30' => :string,
      'enterprise_text31' => :string,
      'enterprise_text32' => :string,
      'enterprise_text33' => :string,
      'enterprise_text34' => :string,
      'enterprise_text35' => :string,
      'enterprise_text36' => :string,
      'enterprise_text37' => :string,
      'enterprise_text38' => :string,
      'enterprise_text39' => :string,
      'enterprise_text4' => :string,
      'enterprise_text40' => :string,
      'enterprise_text5' => :string,
      'enterprise_text6' => :string,
      'enterprise_text7' => :string,
      'enterprise_text8' => :string,
      'enterprise_text9' => :string,
      'enterprise_unique_id' => :integer,
      'error_message' => :string,
      'expenses_only' => :boolean,
      'finish' => :date,
      'finish1' => :date,
      'finish10' => :date,
      'finish2' => :date,
      'finish3' => :date,
      'finish4' => :date,
      'finish5' => :date,
      'finish6' => :date,
      'finish7' => :date,
      'finish8' => :date,
      'finish9' => :date,
      'flag1' => :boolean,
      'flag10' => :boolean,
      'flag11' => :boolean,
      'flag12' => :boolean,
      'flag13' => :boolean,
      'flag14' => :boolean,
      'flag15' => :boolean,
      'flag16' => :boolean,
      'flag17' => :boolean,
      'flag18' => :boolean,
      'flag19' => :boolean,
      'flag2' => :boolean,
      'flag20' => :boolean,
      'flag3' => :boolean,
      'flag4' => :boolean,
      'flag5' => :boolean,
      'flag6' => :boolean,
      'flag7' => :boolean,
      'flag8' => :boolean,
      'flag9' => :boolean,
      'generic' => :boolean,
      'group' => :string,
      'group_by_summary' => :boolean,
      'guid' => :guid,
      'hyperlink' => :string,
      'hyperlink_address' => :string,
      'hyperlink_data' => :binary,
      'hyperlink_href' => :string,
      'hyperlink_screen_tip' => :string,
      'hyperlink_subaddress' => :string,
      'id' => :integer,
      'import' => :boolean,
      'inactive' => :boolean,
      'index' => :integer,
      'indicators' => :string,
      'initials' => :string,
      'leveling_delay' => :duration,
      'linked_fields' => :boolean,
      'location_unique_id' => :integer,
      'material_label' => :string,
      'max_units' => :units,
      'modify_on_integrate' => :boolean,
      'name' => :string,
      'notes' => :notes,
      'number1' => :numeric,
      'number10' => :numeric,
      'number11' => :numeric,
      'number12' => :numeric,
      'number13' => :numeric,
      'number14' => :numeric,
      'number15' => :numeric,
      'number16' => :numeric,
      'number17' => :numeric,
      'number18' => :numeric,
      'number19' => :numeric,
      'number2' => :numeric,
      'number20' => :numeric,
      'number3' => :numeric,
      'number4' => :numeric,
      'number5' => :numeric,
      'number6' => :numeric,
      'number7' => :numeric,
      'number8' => :numeric,
      'number9' => :numeric,
      'objects' => :numeric,
      'outline_code1' => :string,
      'outline_code10' => :string,
      'outline_code10_index' => :integer,
      'outline_code1_index' => :integer,
      'outline_code2' => :string,
      'outline_code2_index' => :integer,
      'outline_code3' => :string,
      'outline_code3_index' => :integer,
      'outline_code4' => :string,
      'outline_code4_index' => :integer,
      'outline_code5' => :string,
      'outline_code5_index' => :integer,
      'outline_code6' => :string,
      'outline_code6_index' => :integer,
      'outline_code7' => :string,
      'outline_code7_index' => :integer,
      'outline_code8' => :string,
      'outline_code8_index' => :integer,
      'outline_code9' => :string,
      'outline_code9_index' => :integer,
      'overallocated' => :boolean,
      'overtime_cost' => :currency,
      'overtime_rate' => :rate,
      'overtime_rate_units' => :rate_units,
      'overtime_work' => :work,
      'parent_id' => :integer,
      'peak' => :units,
      'percent_work_complete' => :percentage,
      'period_dur' => :numeric,
      'per_day' => :numeric,
      'phone' => :string,
      'phonetics' => :string,
      'pool' => :numeric,
      'primary_role_unique_id' => :integer,
      'priority' => :numeric,
      'project' => :string,
      'proposed_finish' => :date,
      'proposed_max_units' => :units,
      'proposed_start' => :date,
      'rate' => :numeric,
      'regular_work' => :work,
      'remaining_cost' => :currency,
      'remaining_overtime_cost' => :currency,
      'remaining_overtime_work' => :work,
      'remaining_work' => :work,
      'request_demand' => :string,
      'resource_code_values' => :code_values,
      'resource_id' => :string,
      'response_pending' => :boolean,
      'role' => :boolean,
      'role_code_values' => :code_values,
      'sequence_number' => :integer,
      'shift_unique_id' => :integer,
      'standard_rate' => :rate,
      'standard_rate_units' => :rate_units,
      'start' => :date,
      'start1' => :date,
      'start10' => :date,
      'start2' => :date,
      'start3' => :date,
      'start4' => :date,
      'start5' => :date,
      'start6' => :date,
      'start7' => :date,
      'start8' => :date,
      'start9' => :date,
      'subproject_resource_unique_id' => :integer,
      'summary' => :string,
      'supply_reference' => :string,
      'sv' => :currency,
      'task_outline_number' => :string,
      'task_summary_name' => :string,
      'team_assignment_pool' => :boolean,
      'team_status_pending' => :boolean,
      'text1' => :string,
      'text10' => :string,
      'text11' => :string,
      'text12' => :string,
      'text13' => :string,
      'text14' => :string,
      'text15' => :string,
      'text16' => :string,
      'text17' => :string,
      'text18' => :string,
      'text19' => :string,
      'text2' => :string,
      'text20' => :string,
      'text21' => :string,
      'text22' => :string,
      'text23' => :string,
      'text24' => :string,
      'text25' => :string,
      'text26' => :string,
      'text27' => :string,
      'text28' => :string,
      'text29' => :string,
      'text3' => :string,
      'text30' => :string,
      'text4' => :string,
      'text5' => :string,
      'text6' => :string,
      'text7' => :string,
      'text8' => :string,
      'text9' => :string,
      'type' => :resource_type,
      'unavailable' => :string,
      'unique_id' => :integer,
      'unit' => :string,
      'unit_of_measure_unique_id' => :integer,
      'update_needed' => :boolean,
      'vac' => :currency,
      'wbs' => :string,
      'windows_user_account' => :string,
      'work' => :work,
      'workgroup' => :workgroup,
      'work_contour' => :work_contour,
      'work_variance' => :duration,
    }.freeze

    def attribute_types
      ATTRIBUTE_TYPES
    end

    module ResourceClassMethods
      def attribute_types
        ATTRIBUTE_TYPES
      end
    end
  end
end
