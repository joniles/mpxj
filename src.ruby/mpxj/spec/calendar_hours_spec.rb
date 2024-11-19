require 'spec_helper'

describe MPXJ::CalendarHours do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/calendar.mpp")
  end

  describe "#from" do
    it 'returns correct from time' do
      days = @project.get_calendar_by_unique_id(1).days
      expect(days.count).to eq(7)

      hours = days['monday'].hours
      expect(hours.count).to eq(2)

      expect(hours[0].from.strftime('%H:%M')).to eq('08:00')
      expect(hours[1].from.strftime('%H:%M')).to eq('13:00')
    end
  end

  describe "#to" do
    it 'returns correct to time' do
      days = @project.get_calendar_by_unique_id(1).days
      expect(days.count).to eq(7)

      hours = days['monday'].hours
      expect(hours.count).to eq(2)

      expect(hours[0].to.strftime('%H:%M')).to eq('12:00')
      expect(hours[1].to.strftime('%H:%M')).to eq('17:00')
    end
  end
end
