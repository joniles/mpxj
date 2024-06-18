# Getting Started with .Net
There are three different ways of adding MPXJ to your .Net project. In each case
tool called [IKVM](https://github.com/ikvmnet/ikvm)
is being used to convert the original Java version of MPXJ into .Net assemblies.

## MPXJ.Net
**This is the recommended approach.**

The [MPXJ.Net NuGet package](https://www.nuget.org/packages/MPXJ.Net)
provides a .Net wrapper around MPXJ's Java API. You will work
with objects which use standard C# naming conventions, and expose native .Net
types and data structures. The original Java nature of MPXJ is completely
hidden from you. This is the preferred way to work with MPXJ. Just add the 
MPXJ.Net NuGet package to your project as a dependency and you can get started.

> Note that your project will take longer than normal to build when first
> built using the MPXJ.Net package. As part of the build process the Java
> version of MPXJ is being dynamically translated into .Net assemblies.
> The results of this translation will be reused, so subsequent build times will
> return to normal. You may also see various transient warning messages as the
> first build completes. These can be ignored and will disappear once your
> project has finished building.

## IKVM.Maven.Sdk
**Documented for completeness, recommend using the MPXJ.Net package rather than
this approach**.

IKVM provides an extension to SDK-style .Net
projects called [IKVM.Maven.SDK](https://www.nuget.org/packages/IKVM.Maven.Sdk)
which allows you to refer to a Java library using Maven (the
most common dependency management solution for Java projects). This means that
your .Net project will be working directly with the original Java version of
the library, which will automatically be translated into .Net assemblies for
you as you build your project.

To include MPXJ in your project using this approach, edit
your project file and include the following lines:

```xml
<ItemGroup>
  <PackageReference Include="IKVM.Maven.Sdk" Version="1.6.9" />
  <MavenReference Include="net.sf.mpxj:mpxj" Version="13.0.0"/>
</ItemGroup>
```

The `<PackageReference>` tag enables IKVM's Maven integration functionality. The
`<MavenReference>` tag uses this integration to request from Maven the version
of MPXJ you'd like to use.

By using this approach you are working with MPXJ's Java API "as is", so you will
need to deal with Java types, data structures, and naming conventions. In most
cases you will find it more productive to work with the MPXJ.Net package
described above. This approach is documented for completeness, but is not
recommended.

> Note that your project will take longer than normal to build when first
> built using `IKVM.Maven.Sdk`. As part of the build process the Java
> version of MPXJ is being dynamically translated into .Net assemblies.
> The results of this translation will be reused, so subsequent build times will
> return to normal. You may also see various transient warning messages as the
> first build completes. These can be ignored and will disappear once your
> project has finished building.


## Legacy IKVM
**Deprecated. Do Not Use. This version will no longer be published once
MPXJ version 14 is released.**

The original .Net version of MPXJ was created using a legacy version of IKVM.
The assemblies for this version are shipped as part of the MPXJ distribution and
are available from NuGet as the following packages:

* [net.sf.mpxj](https://www.nuget.org/packages/net.sf.mpxj) direct translation of MPXJ
* [net.sf.mpxj-for-csharp](https://www.nuget.org/packages/net.sf.mpxj-for-csharp) translation of MPXJ with properties and methods following C# naming conventions
* [net.sf.mpxj-for-vb](https://www.nuget.org/packages/net.sf.mpxj-for-vb) translation of MPXJ with properties and methods following C# naming conventions with some adjustments to accommodate VB's lack of case sensitivity

## Sample Code
There is a repository containing sample .Net
code for MPXJ covering use of the library in more depth. This repository 
can be found [here](https://github.com/joniles/mpxj-dotnet-samples).
