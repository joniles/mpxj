# IKVM for MPXJ

## .Net Framework
The version of IKVM used to build MPXJ for .Net Framework is 8.1.5717.0
(See [IKVM.NET 8.1 Release Candidate 0](https://web.archive.org/web/20210816060013/http://weblog.ikvm.net/#ab36dc873-097a-445d-b61a-3501eca38f5e)).

## .Net Core
The version of IKVM used to build MPXJ for .Net Core is from
[this branch](https://github.com/ikvm-revived/ikvm/pull/38) of
[ikvm-revived/ikvm](https://github.com/ikvm-revived/ikvm).

Build instructions:
1. Clone the repo and ensure the correct branch is checked out
2. Ensure [.Net Core 3.1 SDK](https://dotnet.microsoft.com/download/dotnet/3.1) is installed
3. Ensure JDK 8 tools (javac etc) are on the path
4. Download [openjdk-8u45-b14-stripped.zip](https://web.archive.org/web/20210816060013/http://www.frijters.net/openjdk-8u45-b14-stripped.zip) and unzip in the same directory as your ikvm directory.
5. In your ikvm directory run `build.core.cmd` 

Note that the native DLLs aren't built by the instructions above.
The Windows DLLS from IKVM 8.1 distribution will work fine.
I've manually built the Linux and OSX versions of these libraries,
which you'll find in the MPXJ distribution.