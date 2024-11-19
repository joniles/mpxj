require 'spec_helper'

describe MPXJ::Resource do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/resource.mpp")
  end

  describe "#assignments" do
    it 'returns empty array for Resource2' do
      expect(@project.get_resource_by_id(2).assignments.size).to eq(0)
    end

    it 'returns Task1 for Resource1' do
      expect(@project.get_resource_by_id(1).assignments.size).to eq(1)
      expect(@project.get_resource_by_id(1).assignments[0].task_unique_id).to eq(1)
    end
  end

  describe "#calendar" do
    it 'returns correct value' do
      resource = @project.get_resource_by_id(1)
      expect(resource.name).to eq('Resource1')
      expect(resource.calendar.unique_id).to eq(3)
    end
  end
end
