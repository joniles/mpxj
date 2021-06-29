require 'spec_helper'

describe MPXJ::Container do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/container.mpp")
  end

  describe "#parent_project" do
    it 'returns parent project' do
      expect(@project.get_task_by_id(1).parent_project).to eq(@project)
    end
  end
end
