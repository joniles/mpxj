# coding: utf-8
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)
require 'mpxj/version'

Gem::Specification.new do |spec|
  spec.name          = "mpxj"
  spec.version       = MPXJ::VERSION
  spec.authors       = ["Jon Iles"]
  spec.email         = ["jon@timephased.com"]

  spec.summary       = "The MPXJ gem allows Ruby applications to work with schedule data from project management applications including Microsoft Project, Primavera, Asta Powerproject and Gnome Planner amongst others. The gem provides a Ruby wrapper around the MPXJ Java JAR."
  spec.homepage      = "https://mpxj.org"
  spec.licenses      = ['LGPL-2.1-or-later']

  spec.files         = Dir.glob("{bin,lib,docs,legal}/**/*").concat(["README.md"])
  spec.bindir        = "exe"
  spec.executables   = spec.files.grep(%r{^exe/}) { |f| File.basename(f) }
  spec.require_paths = ["lib"]

  spec.add_development_dependency "bundler"
  spec.add_development_dependency "rake"
  spec.add_development_dependency "rspec"

  spec.add_dependency "json"
  spec.add_dependency "activesupport"
  spec.add_dependency "tzinfo-data"
end
