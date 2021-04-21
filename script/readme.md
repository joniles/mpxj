# Scripting MPXJ
MPXJ is primarily intended to be used as library and directly integrated with
other code. However, there is actually sample code shipped with the
distribution which could be useful when called from a script.

The `run.bat` and `run.sh` files in this folder is are launchers indended to
make it easier to do just this. They handle setting up the classpath required by
MPXJ, allowing you just to pass the name of the class you want to invoke, and
any arguments required by that class.

The example below illustrates working with one of the more useful samples
included with MPXJ: a class which allows you to convert between schedule
formats. In this case the example shows a schedule file being read and
re-written as a JSON file ready to be consumed by another  application.

```bat
path\to\mpxj\script\run.bat net.sf.mpxj.sample.MpxjConvert myfile.mpp myfile.json
```

The same example again, but using the shell script:

```sh
path/to/mpxj/script/run.sh net.sf.mpxj.sample.MpxjConvert myfile.mpp myfile.json
```
