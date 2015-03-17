require 'spec_helper'

describe MPXJ::Assignment do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/assignment.mpp")
  end

  describe "#task" do
    it 'returns correct task' do
      expect(@project.get_task_by_id(1).assignments.size).to eq(1)
      expect(@project.get_task_by_id(1).assignments[0].task.id).to eq(1)
    end
  end

  describe "#resource" do
    it 'returns correct resource' do
      expect(@project.get_task_by_id(1).assignments.size).to eq(1)
      expect(@project.get_task_by_id(1).assignments[0].resource.id).to eq(1)
    end
  end
end
