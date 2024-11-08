require 'spec_helper'

describe MPXJ::CalendarException do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/calendar.mpp")
  end

  describe "#name" do
    it 'returns correct name' do
      exceptions = @project.get_calendar_by_unique_id(3).exceptions
      expect(exceptions.count).to eq(2)

      exception = exceptions[0]
      expect(exception.name).to eq('Working Day')

      exception = exceptions[1]
      expect(exception.name).to eq('Nonworking Day')
    end
  end

  describe "#from" do
    it 'returns correct date' do
      exceptions = @project.get_calendar_by_unique_id(3).exceptions
      expect(exceptions.count).to eq(2)

      exception = exceptions[0]
      expect(exception.from.strftime('%Y-%m-%d')).to eq('2025-01-11')

      exception = exceptions[1]
      expect(exception.from.strftime('%Y-%m-%d')).to eq('2025-01-13')
    end
  end

  describe "#to" do
    it 'returns correct date' do
      exceptions = @project.get_calendar_by_unique_id(3).exceptions
      expect(exceptions.count).to eq(2)

      exception = exceptions[0]
      expect(exception.to.strftime('%Y-%m-%d')).to eq('2025-01-11')

      exception = exceptions[1]
      expect(exception.to.strftime('%Y-%m-%d')).to eq('2025-01-13')
    end
  end

  describe "#type" do
    it 'returns correct type' do
      exceptions = @project.get_calendar_by_unique_id(3).exceptions
      expect(exceptions.count).to eq(2)

      exception = exceptions[0]
      expect(exception.type).to eq('working')

      exception = exceptions[1]
      expect(exception.type).to eq('non_working')
    end
  end

  describe "#hours" do
    it 'returns correct number of hours' do
      exceptions = @project.get_calendar_by_unique_id(3).exceptions
      expect(exceptions.count).to eq(2)

      exception = exceptions[0]
      expect(exception.hours.count).to eq(2)

      exception = exceptions[1]
      expect(exception.hours.count).to eq(0)
    end
  end
end
