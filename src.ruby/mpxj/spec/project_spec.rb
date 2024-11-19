require 'spec_helper'

describe MPXJ::Project do
  before :each do
    @project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/project.mpp")
  end

  describe "#all_calendars" do
    it 'returns the number of calendars' do
      expect(@project.all_calendars.size).to eq(5)
    end
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

  describe "#get_calendar_by_unique_id" do
    it 'returns the correct calendar' do
      expect(@project.get_calendar_by_unique_id(1).unique_id).to eq(1)
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

  describe "#get_field_by_alias" do
    it 'returns the expected aliases' do
      expect(@project.get_field_by_alias('task', 'TaskText1Alias')).to eq('text1')
      expect(@project.get_field_by_alias('task', 'TaskText2Alias')).to eq('text2')
      expect(@project.get_field_by_alias('task', 'NonExistantTaskAlias')).to eq(nil)
      expect(@project.get_field_by_alias('resource', 'ResourceText1Alias')).to eq('text1')
      expect(@project.get_field_by_alias('resource', 'ResourceText2Alias')).to eq('text2')
      expect(@project.get_field_by_alias('resource', 'NonExistantResourceAlias')).to eq(nil)
    end
  end

  describe "#get_alias_by_field" do
    it 'returns the expected fields' do
      expect(@project.get_alias_by_field('task', 'text1')).to eq('TaskText1Alias')
      expect(@project.get_alias_by_field('task', 'text2')).to eq('TaskText2Alias')
      expect(@project.get_alias_by_field('task', 'text3')).to eq(nil)
      expect(@project.get_alias_by_field('resource', 'text1')).to eq('ResourceText1Alias')
      expect(@project.get_alias_by_field('resource', 'text2')).to eq('ResourceText2Alias')
      expect(@project.get_alias_by_field('resource', 'text3')).to eq(nil)
    end
  end
end
