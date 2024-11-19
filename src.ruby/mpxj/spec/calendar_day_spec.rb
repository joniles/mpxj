require 'spec_helper'

describe MPXJ::CalendarDay do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/calendar.mpp")
  end

  describe "#type" do
    it 'returns correct type' do
      days = @project.get_calendar_by_unique_id(1).days
      expect(days.count).to eq(7)

      day = days['sunday']
      expect(day.type).to eq('non_working')

      day = days['monday']
      expect(day.type).to eq('working')
    end
  end

  describe "#hours" do
    it 'returns correct number of hours' do
      days = @project.get_calendar_by_unique_id(1).days
      expect(days.count).to eq(7)

      day = days['sunday']
      expect(day.hours.count).to eq(0)

      day = days['monday']
      expect(day.hours.count).to eq(2)
    end
  end
end
