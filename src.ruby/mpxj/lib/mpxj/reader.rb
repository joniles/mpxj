require 'tempfile'

module MPXJ
  class Reader
    def self.read(file_name)
      project = nil
      json_file = Tempfile.new([File.basename(file_name, ".*"), '.json'])
      begin
        classpath = Dir["#{File.dirname(__FILE__)}/*.jar"].join(path_separator)
        java_output = `java -cp \"#{classpath}\" net.sf.mpxj.sample.MpxjConvert \"#{file_name}\" \"#{json_file.path}\"`
        if $?.exitstatus != 0
          raise "Failed to read file: #{java_output}"
        end
        project = Project.new(json_file)
      ensure
        json_file.close
        json_file.unlink
      end
      project
    end

    def self.path_separator
      if windows?
        ";"
      else
        ":"
      end
    end

    def self.windows?
      (/cygwin|mswin|mingw|bccwin|wince|emx/ =~ RUBY_PLATFORM) != nil
    end
  end
end
