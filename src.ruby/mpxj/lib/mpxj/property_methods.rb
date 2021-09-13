module MPXJ
  module PropertyMethods
    def self.included(base)
      base.extend(PropertyClassMethods)
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

    # Retrieve the Baseline Work value
    #
    # @return Baseline Work value
    def baseline_work
      get_duration_value(attribute_values['baseline_work'])
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
      get_integer_value(attribute_values['critical_slack_limit'])
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

    # Retrieve the Default Calendar Name value
    #
    # @return Default Calendar Name value
    def default_calendar_name
      attribute_values['default_calendar_name']
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
      get_date_value(attribute_values['default_end_time'])
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
      get_date_value(attribute_values['default_start_time'])
    end

    # Retrieve the Default Tssk Earned Value Method value
    #
    # @return Default Tssk Earned Value Method value
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

    # Retrieve the Enterprise Custom Field 1 value
    #
    # @return Enterprise Custom Field 1 value
    def enterprise_custom_field1
      attribute_values['enterprise_custom_field1']
    end

    # Retrieve the Enterprise Custom Field 10 value
    #
    # @return Enterprise Custom Field 10 value
    def enterprise_custom_field10
      attribute_values['enterprise_custom_field10']
    end

    # Retrieve the Enterprise Custom Field 100 value
    #
    # @return Enterprise Custom Field 100 value
    def enterprise_custom_field100
      attribute_values['enterprise_custom_field100']
    end

    # Retrieve the Enterprise Custom Field 101 value
    #
    # @return Enterprise Custom Field 101 value
    def enterprise_custom_field101
      attribute_values['enterprise_custom_field101']
    end

    # Retrieve the Enterprise Custom Field 102 value
    #
    # @return Enterprise Custom Field 102 value
    def enterprise_custom_field102
      attribute_values['enterprise_custom_field102']
    end

    # Retrieve the Enterprise Custom Field 103 value
    #
    # @return Enterprise Custom Field 103 value
    def enterprise_custom_field103
      attribute_values['enterprise_custom_field103']
    end

    # Retrieve the Enterprise Custom Field 104 value
    #
    # @return Enterprise Custom Field 104 value
    def enterprise_custom_field104
      attribute_values['enterprise_custom_field104']
    end

    # Retrieve the Enterprise Custom Field 105 value
    #
    # @return Enterprise Custom Field 105 value
    def enterprise_custom_field105
      attribute_values['enterprise_custom_field105']
    end

    # Retrieve the Enterprise Custom Field 106 value
    #
    # @return Enterprise Custom Field 106 value
    def enterprise_custom_field106
      attribute_values['enterprise_custom_field106']
    end

    # Retrieve the Enterprise Custom Field 107 value
    #
    # @return Enterprise Custom Field 107 value
    def enterprise_custom_field107
      attribute_values['enterprise_custom_field107']
    end

    # Retrieve the Enterprise Custom Field 108 value
    #
    # @return Enterprise Custom Field 108 value
    def enterprise_custom_field108
      attribute_values['enterprise_custom_field108']
    end

    # Retrieve the Enterprise Custom Field 109 value
    #
    # @return Enterprise Custom Field 109 value
    def enterprise_custom_field109
      attribute_values['enterprise_custom_field109']
    end

    # Retrieve the Enterprise Custom Field 11 value
    #
    # @return Enterprise Custom Field 11 value
    def enterprise_custom_field11
      attribute_values['enterprise_custom_field11']
    end

    # Retrieve the Enterprise Custom Field 110 value
    #
    # @return Enterprise Custom Field 110 value
    def enterprise_custom_field110
      attribute_values['enterprise_custom_field110']
    end

    # Retrieve the Enterprise Custom Field 111 value
    #
    # @return Enterprise Custom Field 111 value
    def enterprise_custom_field111
      attribute_values['enterprise_custom_field111']
    end

    # Retrieve the Enterprise Custom Field 112 value
    #
    # @return Enterprise Custom Field 112 value
    def enterprise_custom_field112
      attribute_values['enterprise_custom_field112']
    end

    # Retrieve the Enterprise Custom Field 113 value
    #
    # @return Enterprise Custom Field 113 value
    def enterprise_custom_field113
      attribute_values['enterprise_custom_field113']
    end

    # Retrieve the Enterprise Custom Field 114 value
    #
    # @return Enterprise Custom Field 114 value
    def enterprise_custom_field114
      attribute_values['enterprise_custom_field114']
    end

    # Retrieve the Enterprise Custom Field 115 value
    #
    # @return Enterprise Custom Field 115 value
    def enterprise_custom_field115
      attribute_values['enterprise_custom_field115']
    end

    # Retrieve the Enterprise Custom Field 116 value
    #
    # @return Enterprise Custom Field 116 value
    def enterprise_custom_field116
      attribute_values['enterprise_custom_field116']
    end

    # Retrieve the Enterprise Custom Field 117 value
    #
    # @return Enterprise Custom Field 117 value
    def enterprise_custom_field117
      attribute_values['enterprise_custom_field117']
    end

    # Retrieve the Enterprise Custom Field 118 value
    #
    # @return Enterprise Custom Field 118 value
    def enterprise_custom_field118
      attribute_values['enterprise_custom_field118']
    end

    # Retrieve the Enterprise Custom Field 119 value
    #
    # @return Enterprise Custom Field 119 value
    def enterprise_custom_field119
      attribute_values['enterprise_custom_field119']
    end

    # Retrieve the Enterprise Custom Field 12 value
    #
    # @return Enterprise Custom Field 12 value
    def enterprise_custom_field12
      attribute_values['enterprise_custom_field12']
    end

    # Retrieve the Enterprise Custom Field 120 value
    #
    # @return Enterprise Custom Field 120 value
    def enterprise_custom_field120
      attribute_values['enterprise_custom_field120']
    end

    # Retrieve the Enterprise Custom Field 121 value
    #
    # @return Enterprise Custom Field 121 value
    def enterprise_custom_field121
      attribute_values['enterprise_custom_field121']
    end

    # Retrieve the Enterprise Custom Field 122 value
    #
    # @return Enterprise Custom Field 122 value
    def enterprise_custom_field122
      attribute_values['enterprise_custom_field122']
    end

    # Retrieve the Enterprise Custom Field 123 value
    #
    # @return Enterprise Custom Field 123 value
    def enterprise_custom_field123
      attribute_values['enterprise_custom_field123']
    end

    # Retrieve the Enterprise Custom Field 124 value
    #
    # @return Enterprise Custom Field 124 value
    def enterprise_custom_field124
      attribute_values['enterprise_custom_field124']
    end

    # Retrieve the Enterprise Custom Field 125 value
    #
    # @return Enterprise Custom Field 125 value
    def enterprise_custom_field125
      attribute_values['enterprise_custom_field125']
    end

    # Retrieve the Enterprise Custom Field 126 value
    #
    # @return Enterprise Custom Field 126 value
    def enterprise_custom_field126
      attribute_values['enterprise_custom_field126']
    end

    # Retrieve the Enterprise Custom Field 127 value
    #
    # @return Enterprise Custom Field 127 value
    def enterprise_custom_field127
      attribute_values['enterprise_custom_field127']
    end

    # Retrieve the Enterprise Custom Field 128 value
    #
    # @return Enterprise Custom Field 128 value
    def enterprise_custom_field128
      attribute_values['enterprise_custom_field128']
    end

    # Retrieve the Enterprise Custom Field 129 value
    #
    # @return Enterprise Custom Field 129 value
    def enterprise_custom_field129
      attribute_values['enterprise_custom_field129']
    end

    # Retrieve the Enterprise Custom Field 13 value
    #
    # @return Enterprise Custom Field 13 value
    def enterprise_custom_field13
      attribute_values['enterprise_custom_field13']
    end

    # Retrieve the Enterprise Custom Field 130 value
    #
    # @return Enterprise Custom Field 130 value
    def enterprise_custom_field130
      attribute_values['enterprise_custom_field130']
    end

    # Retrieve the Enterprise Custom Field 131 value
    #
    # @return Enterprise Custom Field 131 value
    def enterprise_custom_field131
      attribute_values['enterprise_custom_field131']
    end

    # Retrieve the Enterprise Custom Field 132 value
    #
    # @return Enterprise Custom Field 132 value
    def enterprise_custom_field132
      attribute_values['enterprise_custom_field132']
    end

    # Retrieve the Enterprise Custom Field 133 value
    #
    # @return Enterprise Custom Field 133 value
    def enterprise_custom_field133
      attribute_values['enterprise_custom_field133']
    end

    # Retrieve the Enterprise Custom Field 134 value
    #
    # @return Enterprise Custom Field 134 value
    def enterprise_custom_field134
      attribute_values['enterprise_custom_field134']
    end

    # Retrieve the Enterprise Custom Field 135 value
    #
    # @return Enterprise Custom Field 135 value
    def enterprise_custom_field135
      attribute_values['enterprise_custom_field135']
    end

    # Retrieve the Enterprise Custom Field 136 value
    #
    # @return Enterprise Custom Field 136 value
    def enterprise_custom_field136
      attribute_values['enterprise_custom_field136']
    end

    # Retrieve the Enterprise Custom Field 137 value
    #
    # @return Enterprise Custom Field 137 value
    def enterprise_custom_field137
      attribute_values['enterprise_custom_field137']
    end

    # Retrieve the Enterprise Custom Field 138 value
    #
    # @return Enterprise Custom Field 138 value
    def enterprise_custom_field138
      attribute_values['enterprise_custom_field138']
    end

    # Retrieve the Enterprise Custom Field 139 value
    #
    # @return Enterprise Custom Field 139 value
    def enterprise_custom_field139
      attribute_values['enterprise_custom_field139']
    end

    # Retrieve the Enterprise Custom Field 14 value
    #
    # @return Enterprise Custom Field 14 value
    def enterprise_custom_field14
      attribute_values['enterprise_custom_field14']
    end

    # Retrieve the Enterprise Custom Field 140 value
    #
    # @return Enterprise Custom Field 140 value
    def enterprise_custom_field140
      attribute_values['enterprise_custom_field140']
    end

    # Retrieve the Enterprise Custom Field 141 value
    #
    # @return Enterprise Custom Field 141 value
    def enterprise_custom_field141
      attribute_values['enterprise_custom_field141']
    end

    # Retrieve the Enterprise Custom Field 142 value
    #
    # @return Enterprise Custom Field 142 value
    def enterprise_custom_field142
      attribute_values['enterprise_custom_field142']
    end

    # Retrieve the Enterprise Custom Field 143 value
    #
    # @return Enterprise Custom Field 143 value
    def enterprise_custom_field143
      attribute_values['enterprise_custom_field143']
    end

    # Retrieve the Enterprise Custom Field 144 value
    #
    # @return Enterprise Custom Field 144 value
    def enterprise_custom_field144
      attribute_values['enterprise_custom_field144']
    end

    # Retrieve the Enterprise Custom Field 145 value
    #
    # @return Enterprise Custom Field 145 value
    def enterprise_custom_field145
      attribute_values['enterprise_custom_field145']
    end

    # Retrieve the Enterprise Custom Field 146 value
    #
    # @return Enterprise Custom Field 146 value
    def enterprise_custom_field146
      attribute_values['enterprise_custom_field146']
    end

    # Retrieve the Enterprise Custom Field 147 value
    #
    # @return Enterprise Custom Field 147 value
    def enterprise_custom_field147
      attribute_values['enterprise_custom_field147']
    end

    # Retrieve the Enterprise Custom Field 148 value
    #
    # @return Enterprise Custom Field 148 value
    def enterprise_custom_field148
      attribute_values['enterprise_custom_field148']
    end

    # Retrieve the Enterprise Custom Field 149 value
    #
    # @return Enterprise Custom Field 149 value
    def enterprise_custom_field149
      attribute_values['enterprise_custom_field149']
    end

    # Retrieve the Enterprise Custom Field 15 value
    #
    # @return Enterprise Custom Field 15 value
    def enterprise_custom_field15
      attribute_values['enterprise_custom_field15']
    end

    # Retrieve the Enterprise Custom Field 150 value
    #
    # @return Enterprise Custom Field 150 value
    def enterprise_custom_field150
      attribute_values['enterprise_custom_field150']
    end

    # Retrieve the Enterprise Custom Field 151 value
    #
    # @return Enterprise Custom Field 151 value
    def enterprise_custom_field151
      attribute_values['enterprise_custom_field151']
    end

    # Retrieve the Enterprise Custom Field 152 value
    #
    # @return Enterprise Custom Field 152 value
    def enterprise_custom_field152
      attribute_values['enterprise_custom_field152']
    end

    # Retrieve the Enterprise Custom Field 153 value
    #
    # @return Enterprise Custom Field 153 value
    def enterprise_custom_field153
      attribute_values['enterprise_custom_field153']
    end

    # Retrieve the Enterprise Custom Field 154 value
    #
    # @return Enterprise Custom Field 154 value
    def enterprise_custom_field154
      attribute_values['enterprise_custom_field154']
    end

    # Retrieve the Enterprise Custom Field 155 value
    #
    # @return Enterprise Custom Field 155 value
    def enterprise_custom_field155
      attribute_values['enterprise_custom_field155']
    end

    # Retrieve the Enterprise Custom Field 156 value
    #
    # @return Enterprise Custom Field 156 value
    def enterprise_custom_field156
      attribute_values['enterprise_custom_field156']
    end

    # Retrieve the Enterprise Custom Field 157 value
    #
    # @return Enterprise Custom Field 157 value
    def enterprise_custom_field157
      attribute_values['enterprise_custom_field157']
    end

    # Retrieve the Enterprise Custom Field 158 value
    #
    # @return Enterprise Custom Field 158 value
    def enterprise_custom_field158
      attribute_values['enterprise_custom_field158']
    end

    # Retrieve the Enterprise Custom Field 159 value
    #
    # @return Enterprise Custom Field 159 value
    def enterprise_custom_field159
      attribute_values['enterprise_custom_field159']
    end

    # Retrieve the Enterprise Custom Field 16 value
    #
    # @return Enterprise Custom Field 16 value
    def enterprise_custom_field16
      attribute_values['enterprise_custom_field16']
    end

    # Retrieve the Enterprise Custom Field 160 value
    #
    # @return Enterprise Custom Field 160 value
    def enterprise_custom_field160
      attribute_values['enterprise_custom_field160']
    end

    # Retrieve the Enterprise Custom Field 161 value
    #
    # @return Enterprise Custom Field 161 value
    def enterprise_custom_field161
      attribute_values['enterprise_custom_field161']
    end

    # Retrieve the Enterprise Custom Field 162 value
    #
    # @return Enterprise Custom Field 162 value
    def enterprise_custom_field162
      attribute_values['enterprise_custom_field162']
    end

    # Retrieve the Enterprise Custom Field 163 value
    #
    # @return Enterprise Custom Field 163 value
    def enterprise_custom_field163
      attribute_values['enterprise_custom_field163']
    end

    # Retrieve the Enterprise Custom Field 164 value
    #
    # @return Enterprise Custom Field 164 value
    def enterprise_custom_field164
      attribute_values['enterprise_custom_field164']
    end

    # Retrieve the Enterprise Custom Field 165 value
    #
    # @return Enterprise Custom Field 165 value
    def enterprise_custom_field165
      attribute_values['enterprise_custom_field165']
    end

    # Retrieve the Enterprise Custom Field 166 value
    #
    # @return Enterprise Custom Field 166 value
    def enterprise_custom_field166
      attribute_values['enterprise_custom_field166']
    end

    # Retrieve the Enterprise Custom Field 167 value
    #
    # @return Enterprise Custom Field 167 value
    def enterprise_custom_field167
      attribute_values['enterprise_custom_field167']
    end

    # Retrieve the Enterprise Custom Field 168 value
    #
    # @return Enterprise Custom Field 168 value
    def enterprise_custom_field168
      attribute_values['enterprise_custom_field168']
    end

    # Retrieve the Enterprise Custom Field 169 value
    #
    # @return Enterprise Custom Field 169 value
    def enterprise_custom_field169
      attribute_values['enterprise_custom_field169']
    end

    # Retrieve the Enterprise Custom Field 17 value
    #
    # @return Enterprise Custom Field 17 value
    def enterprise_custom_field17
      attribute_values['enterprise_custom_field17']
    end

    # Retrieve the Enterprise Custom Field 170 value
    #
    # @return Enterprise Custom Field 170 value
    def enterprise_custom_field170
      attribute_values['enterprise_custom_field170']
    end

    # Retrieve the Enterprise Custom Field 171 value
    #
    # @return Enterprise Custom Field 171 value
    def enterprise_custom_field171
      attribute_values['enterprise_custom_field171']
    end

    # Retrieve the Enterprise Custom Field 172 value
    #
    # @return Enterprise Custom Field 172 value
    def enterprise_custom_field172
      attribute_values['enterprise_custom_field172']
    end

    # Retrieve the Enterprise Custom Field 173 value
    #
    # @return Enterprise Custom Field 173 value
    def enterprise_custom_field173
      attribute_values['enterprise_custom_field173']
    end

    # Retrieve the Enterprise Custom Field 174 value
    #
    # @return Enterprise Custom Field 174 value
    def enterprise_custom_field174
      attribute_values['enterprise_custom_field174']
    end

    # Retrieve the Enterprise Custom Field 175 value
    #
    # @return Enterprise Custom Field 175 value
    def enterprise_custom_field175
      attribute_values['enterprise_custom_field175']
    end

    # Retrieve the Enterprise Custom Field 176 value
    #
    # @return Enterprise Custom Field 176 value
    def enterprise_custom_field176
      attribute_values['enterprise_custom_field176']
    end

    # Retrieve the Enterprise Custom Field 177 value
    #
    # @return Enterprise Custom Field 177 value
    def enterprise_custom_field177
      attribute_values['enterprise_custom_field177']
    end

    # Retrieve the Enterprise Custom Field 178 value
    #
    # @return Enterprise Custom Field 178 value
    def enterprise_custom_field178
      attribute_values['enterprise_custom_field178']
    end

    # Retrieve the Enterprise Custom Field 179 value
    #
    # @return Enterprise Custom Field 179 value
    def enterprise_custom_field179
      attribute_values['enterprise_custom_field179']
    end

    # Retrieve the Enterprise Custom Field 18 value
    #
    # @return Enterprise Custom Field 18 value
    def enterprise_custom_field18
      attribute_values['enterprise_custom_field18']
    end

    # Retrieve the Enterprise Custom Field 180 value
    #
    # @return Enterprise Custom Field 180 value
    def enterprise_custom_field180
      attribute_values['enterprise_custom_field180']
    end

    # Retrieve the Enterprise Custom Field 181 value
    #
    # @return Enterprise Custom Field 181 value
    def enterprise_custom_field181
      attribute_values['enterprise_custom_field181']
    end

    # Retrieve the Enterprise Custom Field 182 value
    #
    # @return Enterprise Custom Field 182 value
    def enterprise_custom_field182
      attribute_values['enterprise_custom_field182']
    end

    # Retrieve the Enterprise Custom Field 183 value
    #
    # @return Enterprise Custom Field 183 value
    def enterprise_custom_field183
      attribute_values['enterprise_custom_field183']
    end

    # Retrieve the Enterprise Custom Field 184 value
    #
    # @return Enterprise Custom Field 184 value
    def enterprise_custom_field184
      attribute_values['enterprise_custom_field184']
    end

    # Retrieve the Enterprise Custom Field 185 value
    #
    # @return Enterprise Custom Field 185 value
    def enterprise_custom_field185
      attribute_values['enterprise_custom_field185']
    end

    # Retrieve the Enterprise Custom Field 186 value
    #
    # @return Enterprise Custom Field 186 value
    def enterprise_custom_field186
      attribute_values['enterprise_custom_field186']
    end

    # Retrieve the Enterprise Custom Field 187 value
    #
    # @return Enterprise Custom Field 187 value
    def enterprise_custom_field187
      attribute_values['enterprise_custom_field187']
    end

    # Retrieve the Enterprise Custom Field 188 value
    #
    # @return Enterprise Custom Field 188 value
    def enterprise_custom_field188
      attribute_values['enterprise_custom_field188']
    end

    # Retrieve the Enterprise Custom Field 189 value
    #
    # @return Enterprise Custom Field 189 value
    def enterprise_custom_field189
      attribute_values['enterprise_custom_field189']
    end

    # Retrieve the Enterprise Custom Field 19 value
    #
    # @return Enterprise Custom Field 19 value
    def enterprise_custom_field19
      attribute_values['enterprise_custom_field19']
    end

    # Retrieve the Enterprise Custom Field 190 value
    #
    # @return Enterprise Custom Field 190 value
    def enterprise_custom_field190
      attribute_values['enterprise_custom_field190']
    end

    # Retrieve the Enterprise Custom Field 191 value
    #
    # @return Enterprise Custom Field 191 value
    def enterprise_custom_field191
      attribute_values['enterprise_custom_field191']
    end

    # Retrieve the Enterprise Custom Field 192 value
    #
    # @return Enterprise Custom Field 192 value
    def enterprise_custom_field192
      attribute_values['enterprise_custom_field192']
    end

    # Retrieve the Enterprise Custom Field 193 value
    #
    # @return Enterprise Custom Field 193 value
    def enterprise_custom_field193
      attribute_values['enterprise_custom_field193']
    end

    # Retrieve the Enterprise Custom Field 194 value
    #
    # @return Enterprise Custom Field 194 value
    def enterprise_custom_field194
      attribute_values['enterprise_custom_field194']
    end

    # Retrieve the Enterprise Custom Field 195 value
    #
    # @return Enterprise Custom Field 195 value
    def enterprise_custom_field195
      attribute_values['enterprise_custom_field195']
    end

    # Retrieve the Enterprise Custom Field 196 value
    #
    # @return Enterprise Custom Field 196 value
    def enterprise_custom_field196
      attribute_values['enterprise_custom_field196']
    end

    # Retrieve the Enterprise Custom Field 197 value
    #
    # @return Enterprise Custom Field 197 value
    def enterprise_custom_field197
      attribute_values['enterprise_custom_field197']
    end

    # Retrieve the Enterprise Custom Field 198 value
    #
    # @return Enterprise Custom Field 198 value
    def enterprise_custom_field198
      attribute_values['enterprise_custom_field198']
    end

    # Retrieve the Enterprise Custom Field 199 value
    #
    # @return Enterprise Custom Field 199 value
    def enterprise_custom_field199
      attribute_values['enterprise_custom_field199']
    end

    # Retrieve the Enterprise Custom Field 2 value
    #
    # @return Enterprise Custom Field 2 value
    def enterprise_custom_field2
      attribute_values['enterprise_custom_field2']
    end

    # Retrieve the Enterprise Custom Field 20 value
    #
    # @return Enterprise Custom Field 20 value
    def enterprise_custom_field20
      attribute_values['enterprise_custom_field20']
    end

    # Retrieve the Enterprise Custom Field 200 value
    #
    # @return Enterprise Custom Field 200 value
    def enterprise_custom_field200
      attribute_values['enterprise_custom_field200']
    end

    # Retrieve the Enterprise Custom Field 21 value
    #
    # @return Enterprise Custom Field 21 value
    def enterprise_custom_field21
      attribute_values['enterprise_custom_field21']
    end

    # Retrieve the Enterprise Custom Field 22 value
    #
    # @return Enterprise Custom Field 22 value
    def enterprise_custom_field22
      attribute_values['enterprise_custom_field22']
    end

    # Retrieve the Enterprise Custom Field 23 value
    #
    # @return Enterprise Custom Field 23 value
    def enterprise_custom_field23
      attribute_values['enterprise_custom_field23']
    end

    # Retrieve the Enterprise Custom Field 24 value
    #
    # @return Enterprise Custom Field 24 value
    def enterprise_custom_field24
      attribute_values['enterprise_custom_field24']
    end

    # Retrieve the Enterprise Custom Field 25 value
    #
    # @return Enterprise Custom Field 25 value
    def enterprise_custom_field25
      attribute_values['enterprise_custom_field25']
    end

    # Retrieve the Enterprise Custom Field 26 value
    #
    # @return Enterprise Custom Field 26 value
    def enterprise_custom_field26
      attribute_values['enterprise_custom_field26']
    end

    # Retrieve the Enterprise Custom Field 27 value
    #
    # @return Enterprise Custom Field 27 value
    def enterprise_custom_field27
      attribute_values['enterprise_custom_field27']
    end

    # Retrieve the Enterprise Custom Field 28 value
    #
    # @return Enterprise Custom Field 28 value
    def enterprise_custom_field28
      attribute_values['enterprise_custom_field28']
    end

    # Retrieve the Enterprise Custom Field 29 value
    #
    # @return Enterprise Custom Field 29 value
    def enterprise_custom_field29
      attribute_values['enterprise_custom_field29']
    end

    # Retrieve the Enterprise Custom Field 3 value
    #
    # @return Enterprise Custom Field 3 value
    def enterprise_custom_field3
      attribute_values['enterprise_custom_field3']
    end

    # Retrieve the Enterprise Custom Field 30 value
    #
    # @return Enterprise Custom Field 30 value
    def enterprise_custom_field30
      attribute_values['enterprise_custom_field30']
    end

    # Retrieve the Enterprise Custom Field 31 value
    #
    # @return Enterprise Custom Field 31 value
    def enterprise_custom_field31
      attribute_values['enterprise_custom_field31']
    end

    # Retrieve the Enterprise Custom Field 32 value
    #
    # @return Enterprise Custom Field 32 value
    def enterprise_custom_field32
      attribute_values['enterprise_custom_field32']
    end

    # Retrieve the Enterprise Custom Field 33 value
    #
    # @return Enterprise Custom Field 33 value
    def enterprise_custom_field33
      attribute_values['enterprise_custom_field33']
    end

    # Retrieve the Enterprise Custom Field 34 value
    #
    # @return Enterprise Custom Field 34 value
    def enterprise_custom_field34
      attribute_values['enterprise_custom_field34']
    end

    # Retrieve the Enterprise Custom Field 35 value
    #
    # @return Enterprise Custom Field 35 value
    def enterprise_custom_field35
      attribute_values['enterprise_custom_field35']
    end

    # Retrieve the Enterprise Custom Field 36 value
    #
    # @return Enterprise Custom Field 36 value
    def enterprise_custom_field36
      attribute_values['enterprise_custom_field36']
    end

    # Retrieve the Enterprise Custom Field 37 value
    #
    # @return Enterprise Custom Field 37 value
    def enterprise_custom_field37
      attribute_values['enterprise_custom_field37']
    end

    # Retrieve the Enterprise Custom Field 38 value
    #
    # @return Enterprise Custom Field 38 value
    def enterprise_custom_field38
      attribute_values['enterprise_custom_field38']
    end

    # Retrieve the Enterprise Custom Field 39 value
    #
    # @return Enterprise Custom Field 39 value
    def enterprise_custom_field39
      attribute_values['enterprise_custom_field39']
    end

    # Retrieve the Enterprise Custom Field 4 value
    #
    # @return Enterprise Custom Field 4 value
    def enterprise_custom_field4
      attribute_values['enterprise_custom_field4']
    end

    # Retrieve the Enterprise Custom Field 40 value
    #
    # @return Enterprise Custom Field 40 value
    def enterprise_custom_field40
      attribute_values['enterprise_custom_field40']
    end

    # Retrieve the Enterprise Custom Field 41 value
    #
    # @return Enterprise Custom Field 41 value
    def enterprise_custom_field41
      attribute_values['enterprise_custom_field41']
    end

    # Retrieve the Enterprise Custom Field 42 value
    #
    # @return Enterprise Custom Field 42 value
    def enterprise_custom_field42
      attribute_values['enterprise_custom_field42']
    end

    # Retrieve the Enterprise Custom Field 43 value
    #
    # @return Enterprise Custom Field 43 value
    def enterprise_custom_field43
      attribute_values['enterprise_custom_field43']
    end

    # Retrieve the Enterprise Custom Field 44 value
    #
    # @return Enterprise Custom Field 44 value
    def enterprise_custom_field44
      attribute_values['enterprise_custom_field44']
    end

    # Retrieve the Enterprise Custom Field 45 value
    #
    # @return Enterprise Custom Field 45 value
    def enterprise_custom_field45
      attribute_values['enterprise_custom_field45']
    end

    # Retrieve the Enterprise Custom Field 46 value
    #
    # @return Enterprise Custom Field 46 value
    def enterprise_custom_field46
      attribute_values['enterprise_custom_field46']
    end

    # Retrieve the Enterprise Custom Field 47 value
    #
    # @return Enterprise Custom Field 47 value
    def enterprise_custom_field47
      attribute_values['enterprise_custom_field47']
    end

    # Retrieve the Enterprise Custom Field 48 value
    #
    # @return Enterprise Custom Field 48 value
    def enterprise_custom_field48
      attribute_values['enterprise_custom_field48']
    end

    # Retrieve the Enterprise Custom Field 49 value
    #
    # @return Enterprise Custom Field 49 value
    def enterprise_custom_field49
      attribute_values['enterprise_custom_field49']
    end

    # Retrieve the Enterprise Custom Field 5 value
    #
    # @return Enterprise Custom Field 5 value
    def enterprise_custom_field5
      attribute_values['enterprise_custom_field5']
    end

    # Retrieve the Enterprise Custom Field 50 value
    #
    # @return Enterprise Custom Field 50 value
    def enterprise_custom_field50
      attribute_values['enterprise_custom_field50']
    end

    # Retrieve the Enterprise Custom Field 51 value
    #
    # @return Enterprise Custom Field 51 value
    def enterprise_custom_field51
      attribute_values['enterprise_custom_field51']
    end

    # Retrieve the Enterprise Custom Field 52 value
    #
    # @return Enterprise Custom Field 52 value
    def enterprise_custom_field52
      attribute_values['enterprise_custom_field52']
    end

    # Retrieve the Enterprise Custom Field 53 value
    #
    # @return Enterprise Custom Field 53 value
    def enterprise_custom_field53
      attribute_values['enterprise_custom_field53']
    end

    # Retrieve the Enterprise Custom Field 54 value
    #
    # @return Enterprise Custom Field 54 value
    def enterprise_custom_field54
      attribute_values['enterprise_custom_field54']
    end

    # Retrieve the Enterprise Custom Field 55 value
    #
    # @return Enterprise Custom Field 55 value
    def enterprise_custom_field55
      attribute_values['enterprise_custom_field55']
    end

    # Retrieve the Enterprise Custom Field 56 value
    #
    # @return Enterprise Custom Field 56 value
    def enterprise_custom_field56
      attribute_values['enterprise_custom_field56']
    end

    # Retrieve the Enterprise Custom Field 57 value
    #
    # @return Enterprise Custom Field 57 value
    def enterprise_custom_field57
      attribute_values['enterprise_custom_field57']
    end

    # Retrieve the Enterprise Custom Field 58 value
    #
    # @return Enterprise Custom Field 58 value
    def enterprise_custom_field58
      attribute_values['enterprise_custom_field58']
    end

    # Retrieve the Enterprise Custom Field 59 value
    #
    # @return Enterprise Custom Field 59 value
    def enterprise_custom_field59
      attribute_values['enterprise_custom_field59']
    end

    # Retrieve the Enterprise Custom Field 6 value
    #
    # @return Enterprise Custom Field 6 value
    def enterprise_custom_field6
      attribute_values['enterprise_custom_field6']
    end

    # Retrieve the Enterprise Custom Field 60 value
    #
    # @return Enterprise Custom Field 60 value
    def enterprise_custom_field60
      attribute_values['enterprise_custom_field60']
    end

    # Retrieve the Enterprise Custom Field 61 value
    #
    # @return Enterprise Custom Field 61 value
    def enterprise_custom_field61
      attribute_values['enterprise_custom_field61']
    end

    # Retrieve the Enterprise Custom Field 62 value
    #
    # @return Enterprise Custom Field 62 value
    def enterprise_custom_field62
      attribute_values['enterprise_custom_field62']
    end

    # Retrieve the Enterprise Custom Field 63 value
    #
    # @return Enterprise Custom Field 63 value
    def enterprise_custom_field63
      attribute_values['enterprise_custom_field63']
    end

    # Retrieve the Enterprise Custom Field 64 value
    #
    # @return Enterprise Custom Field 64 value
    def enterprise_custom_field64
      attribute_values['enterprise_custom_field64']
    end

    # Retrieve the Enterprise Custom Field 65 value
    #
    # @return Enterprise Custom Field 65 value
    def enterprise_custom_field65
      attribute_values['enterprise_custom_field65']
    end

    # Retrieve the Enterprise Custom Field 66 value
    #
    # @return Enterprise Custom Field 66 value
    def enterprise_custom_field66
      attribute_values['enterprise_custom_field66']
    end

    # Retrieve the Enterprise Custom Field 67 value
    #
    # @return Enterprise Custom Field 67 value
    def enterprise_custom_field67
      attribute_values['enterprise_custom_field67']
    end

    # Retrieve the Enterprise Custom Field 68 value
    #
    # @return Enterprise Custom Field 68 value
    def enterprise_custom_field68
      attribute_values['enterprise_custom_field68']
    end

    # Retrieve the Enterprise Custom Field 69 value
    #
    # @return Enterprise Custom Field 69 value
    def enterprise_custom_field69
      attribute_values['enterprise_custom_field69']
    end

    # Retrieve the Enterprise Custom Field 7 value
    #
    # @return Enterprise Custom Field 7 value
    def enterprise_custom_field7
      attribute_values['enterprise_custom_field7']
    end

    # Retrieve the Enterprise Custom Field 70 value
    #
    # @return Enterprise Custom Field 70 value
    def enterprise_custom_field70
      attribute_values['enterprise_custom_field70']
    end

    # Retrieve the Enterprise Custom Field 71 value
    #
    # @return Enterprise Custom Field 71 value
    def enterprise_custom_field71
      attribute_values['enterprise_custom_field71']
    end

    # Retrieve the Enterprise Custom Field 72 value
    #
    # @return Enterprise Custom Field 72 value
    def enterprise_custom_field72
      attribute_values['enterprise_custom_field72']
    end

    # Retrieve the Enterprise Custom Field 73 value
    #
    # @return Enterprise Custom Field 73 value
    def enterprise_custom_field73
      attribute_values['enterprise_custom_field73']
    end

    # Retrieve the Enterprise Custom Field 74 value
    #
    # @return Enterprise Custom Field 74 value
    def enterprise_custom_field74
      attribute_values['enterprise_custom_field74']
    end

    # Retrieve the Enterprise Custom Field 75 value
    #
    # @return Enterprise Custom Field 75 value
    def enterprise_custom_field75
      attribute_values['enterprise_custom_field75']
    end

    # Retrieve the Enterprise Custom Field 76 value
    #
    # @return Enterprise Custom Field 76 value
    def enterprise_custom_field76
      attribute_values['enterprise_custom_field76']
    end

    # Retrieve the Enterprise Custom Field 77 value
    #
    # @return Enterprise Custom Field 77 value
    def enterprise_custom_field77
      attribute_values['enterprise_custom_field77']
    end

    # Retrieve the Enterprise Custom Field 78 value
    #
    # @return Enterprise Custom Field 78 value
    def enterprise_custom_field78
      attribute_values['enterprise_custom_field78']
    end

    # Retrieve the Enterprise Custom Field 79 value
    #
    # @return Enterprise Custom Field 79 value
    def enterprise_custom_field79
      attribute_values['enterprise_custom_field79']
    end

    # Retrieve the Enterprise Custom Field 8 value
    #
    # @return Enterprise Custom Field 8 value
    def enterprise_custom_field8
      attribute_values['enterprise_custom_field8']
    end

    # Retrieve the Enterprise Custom Field 80 value
    #
    # @return Enterprise Custom Field 80 value
    def enterprise_custom_field80
      attribute_values['enterprise_custom_field80']
    end

    # Retrieve the Enterprise Custom Field 81 value
    #
    # @return Enterprise Custom Field 81 value
    def enterprise_custom_field81
      attribute_values['enterprise_custom_field81']
    end

    # Retrieve the Enterprise Custom Field 82 value
    #
    # @return Enterprise Custom Field 82 value
    def enterprise_custom_field82
      attribute_values['enterprise_custom_field82']
    end

    # Retrieve the Enterprise Custom Field 83 value
    #
    # @return Enterprise Custom Field 83 value
    def enterprise_custom_field83
      attribute_values['enterprise_custom_field83']
    end

    # Retrieve the Enterprise Custom Field 84 value
    #
    # @return Enterprise Custom Field 84 value
    def enterprise_custom_field84
      attribute_values['enterprise_custom_field84']
    end

    # Retrieve the Enterprise Custom Field 85 value
    #
    # @return Enterprise Custom Field 85 value
    def enterprise_custom_field85
      attribute_values['enterprise_custom_field85']
    end

    # Retrieve the Enterprise Custom Field 86 value
    #
    # @return Enterprise Custom Field 86 value
    def enterprise_custom_field86
      attribute_values['enterprise_custom_field86']
    end

    # Retrieve the Enterprise Custom Field 87 value
    #
    # @return Enterprise Custom Field 87 value
    def enterprise_custom_field87
      attribute_values['enterprise_custom_field87']
    end

    # Retrieve the Enterprise Custom Field 88 value
    #
    # @return Enterprise Custom Field 88 value
    def enterprise_custom_field88
      attribute_values['enterprise_custom_field88']
    end

    # Retrieve the Enterprise Custom Field 89 value
    #
    # @return Enterprise Custom Field 89 value
    def enterprise_custom_field89
      attribute_values['enterprise_custom_field89']
    end

    # Retrieve the Enterprise Custom Field 9 value
    #
    # @return Enterprise Custom Field 9 value
    def enterprise_custom_field9
      attribute_values['enterprise_custom_field9']
    end

    # Retrieve the Enterprise Custom Field 90 value
    #
    # @return Enterprise Custom Field 90 value
    def enterprise_custom_field90
      attribute_values['enterprise_custom_field90']
    end

    # Retrieve the Enterprise Custom Field 91 value
    #
    # @return Enterprise Custom Field 91 value
    def enterprise_custom_field91
      attribute_values['enterprise_custom_field91']
    end

    # Retrieve the Enterprise Custom Field 92 value
    #
    # @return Enterprise Custom Field 92 value
    def enterprise_custom_field92
      attribute_values['enterprise_custom_field92']
    end

    # Retrieve the Enterprise Custom Field 93 value
    #
    # @return Enterprise Custom Field 93 value
    def enterprise_custom_field93
      attribute_values['enterprise_custom_field93']
    end

    # Retrieve the Enterprise Custom Field 94 value
    #
    # @return Enterprise Custom Field 94 value
    def enterprise_custom_field94
      attribute_values['enterprise_custom_field94']
    end

    # Retrieve the Enterprise Custom Field 95 value
    #
    # @return Enterprise Custom Field 95 value
    def enterprise_custom_field95
      attribute_values['enterprise_custom_field95']
    end

    # Retrieve the Enterprise Custom Field 96 value
    #
    # @return Enterprise Custom Field 96 value
    def enterprise_custom_field96
      attribute_values['enterprise_custom_field96']
    end

    # Retrieve the Enterprise Custom Field 97 value
    #
    # @return Enterprise Custom Field 97 value
    def enterprise_custom_field97
      attribute_values['enterprise_custom_field97']
    end

    # Retrieve the Enterprise Custom Field 98 value
    #
    # @return Enterprise Custom Field 98 value
    def enterprise_custom_field98
      attribute_values['enterprise_custom_field98']
    end

    # Retrieve the Enterprise Custom Field 99 value
    #
    # @return Enterprise Custom Field 99 value
    def enterprise_custom_field99
      attribute_values['enterprise_custom_field99']
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

    # Retrieve the Last Saved value
    #
    # @return Last Saved value
    def last_saved
      get_date_value(attribute_values['last_saved'])
    end

    # Retrieve the Manager value
    #
    # @return Manager value
    def manager
      attribute_values['manager']
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

    # Retrieve the Minutes per Week value
    #
    # @return Minutes per Week value
    def minutes_per_week
      get_integer_value(attribute_values['minutes_per_week'])
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

    # Retrieve the New Tasj Start Is Project Start value
    #
    # @return New Tasj Start Is Project Start value
    def new_task_start_is_project_start
      get_boolean_value(attribute_values['new_task_start_is_project_start'])
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

    # Retrieve the Project Title value
    #
    # @return Project Title value
    def project_title
      attribute_values['project_title']
    end

    # Retrieve the Remove File Properties value
    #
    # @return Remove File Properties value
    def remove_file_properties
      get_boolean_value(attribute_values['remove_file_properties'])
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
      'baseline_cost' => :currency,
      'baseline_date' => :date,
      'baseline_duration' => :duration,
      'baseline_finish' => :date,
      'baseline_for_earned_value' => :integer,
      'baseline_project_unique_id' => :integer,
      'baseline_start' => :date,
      'baseline_work' => :work,
      'category' => :string,
      'comments' => :string,
      'company' => :string,
      'content_status' => :string,
      'content_type' => :string,
      'cost' => :currency,
      'creation_date' => :date,
      'critical_activity_type' => :critical_activity_type,
      'critical_slack_limit' => :integer,
      'currency_code' => :string,
      'currency_digits' => :integer,
      'currency_symbol' => :string,
      'currency_symbol_position' => :currency_symbol_position,
      'current_date' => :date,
      'custom_properties' => :map,
      'date_format' => :project_date_format,
      'date_order' => :date_order,
      'date_separator' => :char,
      'days_per_month' => :integer,
      'decimal_separator' => :char,
      'default_calendar_name' => :string,
      'default_duration_is_fixed' => :boolean,
      'default_duration_units' => :time_units,
      'default_end_time' => :date,
      'default_fixed_cost_accrual' => :accrue,
      'default_overtime_rate' => :rate,
      'default_standard_rate' => :rate,
      'default_start_time' => :date,
      'default_task_earned_value_method' => :earned_value_method,
      'default_task_type' => :task_type,
      'default_work_units' => :time_units,
      'document_version' => :string,
      'duration' => :duration,
      'earned_value_method' => :earned_value_method,
      'editable_actual_costs' => :boolean,
      'editing_time' => :integer,
      'enterprise_custom_field1' => :binary,
      'enterprise_custom_field10' => :binary,
      'enterprise_custom_field100' => :binary,
      'enterprise_custom_field101' => :binary,
      'enterprise_custom_field102' => :binary,
      'enterprise_custom_field103' => :binary,
      'enterprise_custom_field104' => :binary,
      'enterprise_custom_field105' => :binary,
      'enterprise_custom_field106' => :binary,
      'enterprise_custom_field107' => :binary,
      'enterprise_custom_field108' => :binary,
      'enterprise_custom_field109' => :binary,
      'enterprise_custom_field11' => :binary,
      'enterprise_custom_field110' => :binary,
      'enterprise_custom_field111' => :binary,
      'enterprise_custom_field112' => :binary,
      'enterprise_custom_field113' => :binary,
      'enterprise_custom_field114' => :binary,
      'enterprise_custom_field115' => :binary,
      'enterprise_custom_field116' => :binary,
      'enterprise_custom_field117' => :binary,
      'enterprise_custom_field118' => :binary,
      'enterprise_custom_field119' => :binary,
      'enterprise_custom_field12' => :binary,
      'enterprise_custom_field120' => :binary,
      'enterprise_custom_field121' => :binary,
      'enterprise_custom_field122' => :binary,
      'enterprise_custom_field123' => :binary,
      'enterprise_custom_field124' => :binary,
      'enterprise_custom_field125' => :binary,
      'enterprise_custom_field126' => :binary,
      'enterprise_custom_field127' => :binary,
      'enterprise_custom_field128' => :binary,
      'enterprise_custom_field129' => :binary,
      'enterprise_custom_field13' => :binary,
      'enterprise_custom_field130' => :binary,
      'enterprise_custom_field131' => :binary,
      'enterprise_custom_field132' => :binary,
      'enterprise_custom_field133' => :binary,
      'enterprise_custom_field134' => :binary,
      'enterprise_custom_field135' => :binary,
      'enterprise_custom_field136' => :binary,
      'enterprise_custom_field137' => :binary,
      'enterprise_custom_field138' => :binary,
      'enterprise_custom_field139' => :binary,
      'enterprise_custom_field14' => :binary,
      'enterprise_custom_field140' => :binary,
      'enterprise_custom_field141' => :binary,
      'enterprise_custom_field142' => :binary,
      'enterprise_custom_field143' => :binary,
      'enterprise_custom_field144' => :binary,
      'enterprise_custom_field145' => :binary,
      'enterprise_custom_field146' => :binary,
      'enterprise_custom_field147' => :binary,
      'enterprise_custom_field148' => :binary,
      'enterprise_custom_field149' => :binary,
      'enterprise_custom_field15' => :binary,
      'enterprise_custom_field150' => :binary,
      'enterprise_custom_field151' => :binary,
      'enterprise_custom_field152' => :binary,
      'enterprise_custom_field153' => :binary,
      'enterprise_custom_field154' => :binary,
      'enterprise_custom_field155' => :binary,
      'enterprise_custom_field156' => :binary,
      'enterprise_custom_field157' => :binary,
      'enterprise_custom_field158' => :binary,
      'enterprise_custom_field159' => :binary,
      'enterprise_custom_field16' => :binary,
      'enterprise_custom_field160' => :binary,
      'enterprise_custom_field161' => :binary,
      'enterprise_custom_field162' => :binary,
      'enterprise_custom_field163' => :binary,
      'enterprise_custom_field164' => :binary,
      'enterprise_custom_field165' => :binary,
      'enterprise_custom_field166' => :binary,
      'enterprise_custom_field167' => :binary,
      'enterprise_custom_field168' => :binary,
      'enterprise_custom_field169' => :binary,
      'enterprise_custom_field17' => :binary,
      'enterprise_custom_field170' => :binary,
      'enterprise_custom_field171' => :binary,
      'enterprise_custom_field172' => :binary,
      'enterprise_custom_field173' => :binary,
      'enterprise_custom_field174' => :binary,
      'enterprise_custom_field175' => :binary,
      'enterprise_custom_field176' => :binary,
      'enterprise_custom_field177' => :binary,
      'enterprise_custom_field178' => :binary,
      'enterprise_custom_field179' => :binary,
      'enterprise_custom_field18' => :binary,
      'enterprise_custom_field180' => :binary,
      'enterprise_custom_field181' => :binary,
      'enterprise_custom_field182' => :binary,
      'enterprise_custom_field183' => :binary,
      'enterprise_custom_field184' => :binary,
      'enterprise_custom_field185' => :binary,
      'enterprise_custom_field186' => :binary,
      'enterprise_custom_field187' => :binary,
      'enterprise_custom_field188' => :binary,
      'enterprise_custom_field189' => :binary,
      'enterprise_custom_field19' => :binary,
      'enterprise_custom_field190' => :binary,
      'enterprise_custom_field191' => :binary,
      'enterprise_custom_field192' => :binary,
      'enterprise_custom_field193' => :binary,
      'enterprise_custom_field194' => :binary,
      'enterprise_custom_field195' => :binary,
      'enterprise_custom_field196' => :binary,
      'enterprise_custom_field197' => :binary,
      'enterprise_custom_field198' => :binary,
      'enterprise_custom_field199' => :binary,
      'enterprise_custom_field2' => :binary,
      'enterprise_custom_field20' => :binary,
      'enterprise_custom_field200' => :binary,
      'enterprise_custom_field21' => :binary,
      'enterprise_custom_field22' => :binary,
      'enterprise_custom_field23' => :binary,
      'enterprise_custom_field24' => :binary,
      'enterprise_custom_field25' => :binary,
      'enterprise_custom_field26' => :binary,
      'enterprise_custom_field27' => :binary,
      'enterprise_custom_field28' => :binary,
      'enterprise_custom_field29' => :binary,
      'enterprise_custom_field3' => :binary,
      'enterprise_custom_field30' => :binary,
      'enterprise_custom_field31' => :binary,
      'enterprise_custom_field32' => :binary,
      'enterprise_custom_field33' => :binary,
      'enterprise_custom_field34' => :binary,
      'enterprise_custom_field35' => :binary,
      'enterprise_custom_field36' => :binary,
      'enterprise_custom_field37' => :binary,
      'enterprise_custom_field38' => :binary,
      'enterprise_custom_field39' => :binary,
      'enterprise_custom_field4' => :binary,
      'enterprise_custom_field40' => :binary,
      'enterprise_custom_field41' => :binary,
      'enterprise_custom_field42' => :binary,
      'enterprise_custom_field43' => :binary,
      'enterprise_custom_field44' => :binary,
      'enterprise_custom_field45' => :binary,
      'enterprise_custom_field46' => :binary,
      'enterprise_custom_field47' => :binary,
      'enterprise_custom_field48' => :binary,
      'enterprise_custom_field49' => :binary,
      'enterprise_custom_field5' => :binary,
      'enterprise_custom_field50' => :binary,
      'enterprise_custom_field51' => :binary,
      'enterprise_custom_field52' => :binary,
      'enterprise_custom_field53' => :binary,
      'enterprise_custom_field54' => :binary,
      'enterprise_custom_field55' => :binary,
      'enterprise_custom_field56' => :binary,
      'enterprise_custom_field57' => :binary,
      'enterprise_custom_field58' => :binary,
      'enterprise_custom_field59' => :binary,
      'enterprise_custom_field6' => :binary,
      'enterprise_custom_field60' => :binary,
      'enterprise_custom_field61' => :binary,
      'enterprise_custom_field62' => :binary,
      'enterprise_custom_field63' => :binary,
      'enterprise_custom_field64' => :binary,
      'enterprise_custom_field65' => :binary,
      'enterprise_custom_field66' => :binary,
      'enterprise_custom_field67' => :binary,
      'enterprise_custom_field68' => :binary,
      'enterprise_custom_field69' => :binary,
      'enterprise_custom_field7' => :binary,
      'enterprise_custom_field70' => :binary,
      'enterprise_custom_field71' => :binary,
      'enterprise_custom_field72' => :binary,
      'enterprise_custom_field73' => :binary,
      'enterprise_custom_field74' => :binary,
      'enterprise_custom_field75' => :binary,
      'enterprise_custom_field76' => :binary,
      'enterprise_custom_field77' => :binary,
      'enterprise_custom_field78' => :binary,
      'enterprise_custom_field79' => :binary,
      'enterprise_custom_field8' => :binary,
      'enterprise_custom_field80' => :binary,
      'enterprise_custom_field81' => :binary,
      'enterprise_custom_field82' => :binary,
      'enterprise_custom_field83' => :binary,
      'enterprise_custom_field84' => :binary,
      'enterprise_custom_field85' => :binary,
      'enterprise_custom_field86' => :binary,
      'enterprise_custom_field87' => :binary,
      'enterprise_custom_field88' => :binary,
      'enterprise_custom_field89' => :binary,
      'enterprise_custom_field9' => :binary,
      'enterprise_custom_field90' => :binary,
      'enterprise_custom_field91' => :binary,
      'enterprise_custom_field92' => :binary,
      'enterprise_custom_field93' => :binary,
      'enterprise_custom_field94' => :binary,
      'enterprise_custom_field95' => :binary,
      'enterprise_custom_field96' => :binary,
      'enterprise_custom_field97' => :binary,
      'enterprise_custom_field98' => :binary,
      'enterprise_custom_field99' => :binary,
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
      'inserted_projects_like_summary' => :boolean,
      'keywords' => :string,
      'language' => :string,
      'lastprinted' => :date,
      'last_author' => :string,
      'last_saved' => :date,
      'manager' => :string,
      'microsoft_project_server_url' => :boolean,
      'minutes_per_day' => :integer,
      'minutes_per_week' => :integer,
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
      'percentage_complete' => :percentage,
      'planned_start' => :date,
      'pm_text' => :string,
      'presentation_format' => :string,
      'project_externally_edited' => :boolean,
      'project_file_path' => :string,
      'project_id' => :string,
      'project_title' => :string,
      'remove_file_properties' => :boolean,
      'revision' => :integer,
      'scheduled_finish' => :date,
      'schedule_from' => :schedule_from,
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
      'unique_id' => :integer,
      'updating_task_status_updates_resource_status' => :boolean,
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
