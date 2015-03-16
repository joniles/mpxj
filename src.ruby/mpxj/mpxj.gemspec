# coding: utf-8
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)
require 'mpxj/version'

Gem::Specification.new do |spec|
  spec.name          = "mpxj"
  spec.version       = MPXJ::VERSION
  spec.authors       = ["Jon Iles"]
  spec.email         = ["jon.iles@bcs.org.uk"]

  spec.summary       = "The MPXJ gem allows Ruby applications to work with schedule data from project management applications including Microsoft Project, Primavera, Asta Powerproject and Gnome Planner amongst others. The gem provides a Ruby wrapper around the MPXJ Java JAR."
  spec.homepage      = "TODO: Put your gem's website or public repo URL here."

  spec.files         = `git ls-files -z`.split("\x0").reject { |f| f.match(%r{^(test|spec|features)/}) }
  spec.bindir        = "exe"
  spec.executables   = spec.files.grep(%r{^exe/}) { |f| File.basename(f) }
  spec.require_paths = ["lib"]

  spec.add_development_dependency "bundler", "~> 1.8"
  spec.add_development_dependency "rake", "~> 10.0"

  spec.add_dependency "json"
  spec.add_dependency "duration"
end
