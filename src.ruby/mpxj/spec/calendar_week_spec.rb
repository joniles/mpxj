require 'spec_helper'

describe MPXJ::CalendarWeek do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/calendar.mpp")
  end

  describe "#name" do
    it 'returns correct name' do
      weeks = @project.get_calendar_by_unique_id(3).weeks
      expect(weeks.count).to eq(2)

      week = weeks[0]
      expect(week.name).to eq('Work Week 1')

      week = weeks[1]
      expect(week.name).to eq('Work Week 2')
    end
  end

  describe "#effective_from" do
    it 'returns correct date' do
      weeks = @project.get_calendar_by_unique_id(3).weeks
      expect(weeks.count).to eq(2)

      week = weeks[0]
      expect(week.effective_from.strftime('%Y-%m-%d')).to eq('2024-10-01')

      week = weeks[1]
      expect(week.effective_from.strftime('%Y-%m-%d')).to eq('2024-12-01')
    end
  end

  describe "#effective_to" do
    it 'returns correct date' do
      weeks = @project.get_calendar_by_unique_id(3).weeks
      expect(weeks.count).to eq(2)

      week = weeks[0]
      expect(week.effective_to.strftime('%Y-%m-%d')).to eq('2024-10-31')

      week = weeks[1]
      expect(week.effective_to.strftime('%Y-%m-%d')).to eq('2024-12-31')
    end
  end

  describe "#days" do
    it 'returns correct day definitions' do
      weeks = @project.get_calendar_by_unique_id(3).weeks
      expect(weeks.count).to eq(2)

      days = weeks[0].days
      expect(days.count).to eq(7)

      day = days['sunday']
      expect(day.type).to eq('default')
      expect(day.hours.count).to eq(0)

      day = days['monday']
      expect(day.type).to eq('working')
      expect(day.hours.count).to eq(1)
      expect(day.hours[0].from.strftime('%H:%M')).to eq('08:00')
      expect(day.hours[0].to.strftime('%H:%M')).to eq('12:00')


      days = weeks[1].days
      expect(days.count).to eq(7)

      day = days['sunday']
      expect(day.type).to eq('working')
      expect(day.hours.count).to eq(1)
      expect(day.hours[0].from.strftime('%H:%M')).to eq('08:00')
      expect(day.hours[0].to.strftime('%H:%M')).to eq('17:00')

      day = days['monday']
      expect(day.type).to eq('non_working')
      expect(day.hours.count).to eq(0)
    end
  end
end
