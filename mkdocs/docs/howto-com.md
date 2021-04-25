# MPXJ and COM

The .Net assemblies provided in the DLLs described [here](howto-dotnet.md) are
accessible from COM. This should allow you to, for example, write VBA code which
utilises MPXJ functionality. To assist with this, type libraries  in the form of
`TLB` files are provided in the `lib.net` directory for each of the DLLs
distributed with MPXJ.  You will also need to register the MPXJ assemblies in
order to use them from COM,  using the `regasm` [assembly registration
tool](http://msdn.microsoft.com/en-us/library/tzat5yw6(v=vs.110).aspx). For your
convenience two batch files have been provided in the lib.net directory:
`mpxj-register-assemblies.bat` and `mpxj-unregister-assemblies.bat`. These batch
files will register and unreigster the MPXJ assemblies respectively. These batch
files assume that `regasm` is available on the path.
