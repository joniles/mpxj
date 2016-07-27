require 'tempfile'
require 'active_support/core_ext/time/calculations'

module MPXJ
  # Used to read a project plan from a file
  class Reader
    # Reads a project plan from a file, and returns a Project instance
    # which provides access to the structure and attributes of the project data.
    # Note that an optional timezone can be supplied to ensue that all date-time
    # values returned are in the specified timezone.
    #
    # @param file_name [String] the name of the file to read
    # @param zone [ActiveSupport::TimeZone] an optional timezone
    # @return [Project] new Project instance
    def self.read(file_name, zone = nil)
      project = nil
      json_file = Dir::Tmpname.make_tmpname([File.basename(file_name, ".*"), '.json'], nil)
      tz = zone || Time.zone || ActiveSupport::TimeZone["UTC"]

      begin
        classpath = Dir["#{File.dirname(__FILE__)}/*.jar"].join(path_separator)
        java_output = `java -cp \"#{classpath}\" net.sf.mpxj.sample.MpxjConvert \"#{file_name}\" \"#{json_file}\"`
        if $?.exitstatus != 0
          raise "Failed to read file: #{java_output}"
        end
        project = Project.new(json_file, tz)
      ensure
        File::delete(json_file)
      end
      project
    end

    # @private
    def self.path_separator
      if windows?
        ";"
      else
        ":"
      end
    end

    # @private
    def self.windows?
      (/cygwin|mswin|mingw|bccwin|wince|emx/ =~ RUBY_PLATFORM) != nil
    end
  end
end
