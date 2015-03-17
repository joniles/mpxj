require 'spec_helper'

describe MPXJ::Relation do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/relation.mpp")
  end

  describe "#task_unique_id" do
    it 'returns correct task unique id' do
      successors = @project.get_task_by_id(1).successors
      expect(successors.size).to eq(1)
      relation = successors[0]
      expect(relation.task_unique_id).to eq(2)
    end
  end
end
