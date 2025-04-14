require 'spec_helper'

describe MPXJ::Relation do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/relation.mpp")
  end

  describe "#successor_task_unique_id" do
    it 'returns correct task unique id' do
      successors = @project.get_task_by_id(1).successors
      expect(successors.size).to eq(1)
      relation = successors[0]
      expect(relation.successor_task_unique_id).to eq(2)

      predecessors = @project.get_task_by_id(2).predecessors
      expect(predecessors.size).to eq(1)
      relation = predecessors[0]
      expect(relation.successor_task_unique_id).to eq(2)
    end
  end

  describe "#predecessor_task_unique_id" do
    it 'returns correct task unique id' do
      successors = @project.get_task_by_id(1).successors
      expect(successors.size).to eq(1)
      relation = successors[0]
      expect(relation.predecessor_task_unique_id).to eq(1)

      predecessors = @project.get_task_by_id(2).predecessors
      expect(predecessors.size).to eq(1)
      relation = predecessors[0]
      expect(relation.predecessor_task_unique_id).to eq(1)
    end
  end
end
