require 'spec_helper'

describe MPXJ::Properties do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/properties.mpp")
  end

  describe "#author" do
    it 'returns correct value' do
      expect(@project.properties.author).to eq("Project User")
    end
  end

  describe "#start_date" do
    it 'returns correct value' do
      expect(@project.properties.start_date).to eq(Time.parse("2015-03-14T08:00:00.0"))
    end
  end

  describe "#custom_properties" do
    it 'returns correct value' do
      properties = @project.properties.custom_properties
      expect(properties["Scheduled Finish"]).to eq("2015-04-03T18:00:00.0")
      expect(properties["Scheduled Start"]).to eq("2015-03-16T08:00:00.0")
      expect(properties["% Work Complete"]).to eq("0%")
      expect(properties["Work"]).to eq("120h")
      expect(properties["Scheduled Duration"]).to eq("15d")
      expect(properties["% Complete"]).to eq("0%")
    end
  end

  describe "#default_calendar" do
    it 'returns correct value' do
      expect(@project.properties.default_calendar.unique_id).to eq(1)
    end
  end
end
