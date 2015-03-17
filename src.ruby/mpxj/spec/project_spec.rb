require 'spec_helper'

describe MPXJ::Project do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/project.mpp")
  end

  describe "#all_resources" do
    it 'returns the number of resources' do
      expect(@project.all_resources.size).to eq(4)
    end
  end
  
  describe "#all_tasks" do
    it 'returns the number of tasks' do
      expect(@project.all_tasks.size).to eq(6)
    end
  end
  
  describe "#child_tasks" do
    it 'returns the number of child tasks' do
      expect(@project.child_tasks.size).to eq(1)
    end
  end  

  describe "#all_assignments" do
    it 'returns the number of assignments' do
      expect(@project.all_assignments.size).to eq(4)
    end
  end  

  describe "#get_resource_by_id" do
    it 'returns the correct resource' do
      expect(@project.get_resource_by_id(1).id).to eq(1)
    end
  end  

  describe "#get_resource_by_unique_id" do
    it 'returns the correct resource' do
      expect(@project.get_resource_by_unique_id(1).id).to eq(1)
    end
  end  
  
  describe "#get_task_by_id" do
    it 'returns the correct task' do
      expect(@project.get_task_by_id(1).id).to eq(1)
    end
  end  

  describe "#get_task_by_unique_id" do
    it 'returns the correct task' do
      expect(@project.get_task_by_unique_id(1).id).to eq(1)
    end
  end      
end
