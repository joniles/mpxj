require 'spec_helper'

describe MPXJ::Task do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/task.mpp")
  end

  describe "#parent_task" do
    it 'returns nil for root task' do
      expect(@project.get_task_by_id(0).parent_task).to eq(nil)
    end

    it 'returns root task for Task1' do
      expect(@project.get_task_by_id(1).parent_task.id).to eq(0)
    end

    it 'returns root Task2 for Task3' do
      expect(@project.get_task_by_id(3).parent_task.id).to eq(2)
    end
  end

  describe "#assignments" do
    it 'returns Resource1 for Task1' do
      expect(@project.get_task_by_id(1).assignments.size).to eq(1)
      expect(@project.get_task_by_id(1).assignments[0].resource_unique_id).to eq(1)
    end

    it 'returns no assignments for Task2' do
      expect(@project.get_task_by_id(2).assignments.size).to eq(0)
    end
  end

  describe "#predecessors" do
    it 'returns no predecessors for Task1' do
      expect(@project.get_task_by_id(1).predecessors.size).to eq(0)
    end

    it 'returns Task4 for Task5' do
      expect(@project.get_task_by_id(5).predecessors.size).to eq(1)
      expect(@project.get_task_by_id(5).predecessors[0].task_unique_id).to eq(4)
    end
  end

  describe "#successors" do
    it 'returns no successors for Task1' do
      expect(@project.get_task_by_id(1).successors.size).to eq(0)
    end

    it 'returns Task5 for Task5' do
      expect(@project.get_task_by_id(4).successors.size).to eq(1)
      expect(@project.get_task_by_id(4).successors[0].task_unique_id).to eq(5)
    end
  end

  describe '#child_tasks' do
    it 'returns no child tasks for Task1' do
      expect(@project.get_task_by_id(1).child_tasks.size).to eq(0)
    end

    it 'returns Task3 for Task2' do
      expect(@project.get_task_by_id(2).child_tasks.size).to eq(1)
      expect(@project.get_task_by_id(2).child_tasks[0].unique_id).to eq(3)
    end        
  end
end
