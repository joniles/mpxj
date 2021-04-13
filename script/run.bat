@echo off

REM Adapted from runit.bat by Alvin J. Alexander
REM https://alvinalexander.com/blog/post/page-1/thu-mar-9-2006-dynamically-build-environment-variables-in-dos-c/

set JARS=
for %%i in (%0\..\..\lib\*.jar) do call %0\..\cpappend.bat %%i

java -cp %JARS%;%0\..\..\mpxj.jar %1 %2 %3 %4 %5 %6 %7 %8 %9