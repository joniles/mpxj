require 'spec_helper'

describe MPXJ::Calendar do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/calendar.mpp")
  end

  describe "#unique_id" do
    it 'returns correct unique id' do
      calendar = @project.get_calendar_by_unique_id(1)
      expect(calendar.unique_id).to eq(1)
    end
  end

  describe "#guid" do
    it 'returns correct guid' do
      calendar = @project.get_calendar_by_unique_id(1)
      expect(calendar.guid).to eq('073aaf7a-ed5e-4fbe-ad97-f2cf43cea217')
    end
  end

  describe "#parent_unique_id" do
    it 'returns correct parent_unique_id' do
      calendar = @project.get_calendar_by_unique_id(1)
      expect(calendar.parent_unique_id).to eq(nil)

      calendar = @project.get_calendar_by_unique_id(2)
      expect(calendar.parent_unique_id).to eq(1)
    end
  end

  describe "#parent_calendar" do
    it 'returns correct calendar instance' do
      calendar = @project.get_calendar_by_unique_id(1)
      expect(calendar.parent_calendar).to eq(nil)

      calendar = @project.get_calendar_by_unique_id(2)
      expect(calendar.parent_calendar.unique_id).to eq(1)
    end
  end

  describe "#name" do
    it 'returns correct name' do
      calendar = @project.get_calendar_by_unique_id(1)
      expect(calendar.name).to eq('Standard')
    end
  end

  describe "#type" do
    it 'returns correct type' do
      calendar = @project.get_calendar_by_unique_id(1)
      expect(calendar.type).to eq('GLOBAL')
    end
  end

  describe "#personal" do
    it 'returns correct personal flag' do
      calendar = @project.get_calendar_by_unique_id(1)
      expect(calendar.personal).to eq(false)
    end
  end

  describe "#minutes_per_day" do
    it 'returns correct minutes_per_day' do
      calendar = @project.get_calendar_by_unique_id(1)
      expect(calendar.minutes_per_day).to eq(nil)
    end
  end

  describe "#minutes_per_week" do
    it 'returns correct minutes_per_week' do
      calendar = @project.get_calendar_by_unique_id(1)
      expect(calendar.minutes_per_week).to eq(nil)
    end
  end

  describe "#minutes_per_month" do
    it 'returns correct minutes_per_month' do
      calendar = @project.get_calendar_by_unique_id(1)
      expect(calendar.minutes_per_month).to eq(nil)
    end
  end


  describe "#minutes_per_year" do
    it 'returns correct minutes_per_year' do
      calendar = @project.get_calendar_by_unique_id(1)
      expect(calendar.minutes_per_year).to eq(nil)
    end
  end

  describe "#days" do
    it 'returns correct number of days' do
      calendar = @project.get_calendar_by_unique_id(1)
      expect(calendar.days.count).to eq(7)
    end
  end

  describe "#weeks" do
    it 'returns correct number of weeks' do
      calendar = @project.get_calendar_by_unique_id(3)
      expect(calendar.weeks.count).to eq(2)
    end
  end

  describe "#exceptions" do
    it 'returns correct number of exceptions' do
      calendar = @project.get_calendar_by_unique_id(3)
      expect(calendar.exceptions.count).to eq(2)
    end
  end
end
