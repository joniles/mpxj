# MPXJ

This gem allows a Ruby developer to work with a read-only view of project plans
saved by a number of popular project planning applications. The work required to
read data from these files is actually carried out by a
[Java library](https://mpxj.org), hence you will need Java installed and
available on your path in order to work with this gem. Once the project data has
been read from a file, a set of Ruby objects provides access to the structure of
the project plan and its attributes.

This gem only came about through the interest and support of Procore, who would
love to [hear from you](https://www.procore.com/jobs/) if you're excited about
working with Ruby and Rails.

## Installation

Add this line to your application's Gemfile:

	gem 'mpxj'

And then execute:

    $ bundle

Or install it yourself as:

    $ gem install mpxj

## Changelog
You'll find details of what has changed in this version
[here](http://mpxj.sourceforge.net/changes-report.html).

## Supported File Types

Full details of the supported file formats can be found [here](https://www.mpxj.org/supported-formats/).

## Example Code

The following is a trivial example showing some basic task and resource details
being queried from a project:

	project = MPXJ::Reader.read("project1.mpp")

	puts "There are #{project.all_tasks.size} tasks in this project"
	puts "There are #{project.all_resources.size} resources in this project"

	puts "The resources are:"
	project.all_resources.each do |resource|
	  puts resource.name
	end

	puts "The tasks are:"
	project.all_tasks.each do |task|
	  puts "#{task.name}: starts on #{task.start}, finishes on #{task.finish}, it's duration is #{task.duration}"
	end

## Entities

The gem represents the project plan using the following classes, all of which reside in the MPXJ module.

* Project
* Resource
* Task
* Assignment
* Relation

A **Project** contains **Resource**s and **Task**s. Each **Resource** can be
**Assigned** to one or more **Task**s. **Task**s can have dependencies between
them which are represented as **Relation**s.

## Acknowledgements
Acknowledgements for this library can be found [here](https://www.mpxj.org/#acknowledgements).
