require 'tempfile'
require 'active_support'
require 'active_support/core_ext/time/calculations'
require 'active_support/core_ext/object/blank'

module MPXJ
  # Used to read a project plan from a file
  class Reader
    @@max_memory_size = nil

    # Reads a project plan from a file, and returns a Project instance
    # which provides access to the structure and attributes of the project data.
    # Note that an optional timezone can be supplied to ensue that all date-time
    # values returned are in the specified timezone.
    #
    # @param file_name [String] the name of the file to read
    # @param zone [ActiveSupport::TimeZone] an optional timezone
    # @param time_units [Symbol] optional, specify the units for expressing durations. By default durations are in seconds, can pass :minutes, :hours, :days: weeks:, :months or :years
    # @return [Project] new Project instance
    def self.read(file_name, zone = nil, time_units = :seconds)
      project = nil
      json_file = Tempfile.new([File.basename(file_name, ".*"), '.json'])
      tz = zone || Time.zone || ActiveSupport::TimeZone["UTC"]

      begin
        classpath = "#{File.dirname(__FILE__)}/*"
        java_output = `java -cp \"#{classpath}\" #{jvm_args} org.mpxj.ruby.GenerateJson \"#{file_name}\" \"#{json_file.path}\" \"#{time_units}\"`
        if $?.exitstatus != 0
          report_error(java_output)
        end
        project = Project.new(json_file, tz)
      ensure
        json_file.close
        json_file.unlink
      end
      project
    end

    # Allows the caller to set the maximum memory size used by the JVM when processing a schedule.
    # This is useful when handling large schedules which cause out of memory failures if the JVM's
    # default maximum memory size is used. The value is either a plain integer number of bytes,
    # or an integer followed by K, M, or G, e.g. `MPXJ::Reader.max_memory_size="500M"`
    #
    # @param value new maximum memory size
    def self.max_memory_size=(value)
      @@max_memory_size = value
    end

    # @private
    def self.jvm_args
      args = []
      args << "-Dlog4j2.loggerContextFactory=org.apache.logging.log4j.simple.SimpleLoggerContextFactory"
      args << "-Xmx#{@@max_memory_size}" if @@max_memory_size.present?
      args.join(' ')
    end

     # @private
    def self.report_error(java_output)
      if java_output.include?('Conversion Error: ')
        message = java_output.split('Conversion Error: ')[1]
        if message.include?('Unsupported file type')
          raise MPXJ::ArgumentError, message
        elsif message.include?('password protected')
          raise MPXJ::PasswordProtected, message
        else
          raise MPXJ::RuntimeError, message
        end
      else
        raise MPXJ::UnknownError, "Failed to read file: #{java_output}"
      end
    end
  end
end
