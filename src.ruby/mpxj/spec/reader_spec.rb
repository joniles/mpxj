require 'spec_helper'

describe MPXJ::Reader do
  describe "#read" do
    it 'will read a valid file without raising an exception' do
      project = MPXJ::Reader.read("#{File.dirname(__FILE__)}/reader.mpp")

      expect(project.all_tasks.size).to eq(2)
      expect(project.get_task_by_id(0).name).to eq("reader")
      expect(project.get_task_by_id(1).name).to eq("Task 1")

      expect(project.all_resources.size).to eq(2)
      expect(project.get_resource_by_id(1).name).to eq("Resource 1")
    end

    it 'will raise an exception when an MPXJ error occurs' do
      expect { MPXJ::Reader.read("idontexist.mpp") }.to raise_error(MPXJ::RuntimeError)
    end

    it 'will extract the error message from the Java output' do
      begin
        MPXJ::Reader.read("idontexist.mpp")
      rescue Exception => e
        expect(e.class).to eq(MPXJ::RuntimeError)
        expect(e.message.split(/\n/).first).to eq("org.mpxj.MPXJException: Invalid file format")
      end
    end

    it 'will raise a specific exception when the file type is not supported' do
      begin
        MPXJ::Reader.read("#{File.dirname(__FILE__)}/reader_spec.rb")
      rescue Exception => e
        expect(e.class).to eq(MPXJ::ArgumentError)
        expect(e.message.split(/\n/).first).to eq("java.lang.IllegalArgumentException: Unsupported file type")
      end
    end
  end
end
