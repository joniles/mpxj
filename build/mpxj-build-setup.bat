@echo off

rem Run this script as administrator for an unattended install

rem
rem Configure Install Dir
rem
set INSTALL_DIR=C:\Java
mkdir %INSTALL_DIR%
cd %INSTALL_DIR%

rem
rem Ant
rem
set ANT_VERSION=1.9.16
curl -O https://archive.apache.org/dist/ant/binaries/apache-ant-%ANT_VERSION%-bin.zip
tar -xf apache-ant-%ANT_VERSION%-bin.zip
set ANT_HOME=%INSTALL_DIR%\apache-ant-%ANT_VERSION%
setx ANT_HOME %ANT_HOME%

rem
rem Maven
rem
set MAVEN_VERSION=3.9.6
curl -O https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip
tar -xf apache-maven-%MAVEN_VERSION%-bin.zip
set MAVEN_HOME=%INSTALL_DIR%\apache-maven-%MAVEN_VERSION%
setx MAVEN_HOME %MAVEN_HOME%

rem
rem JAXB
rem
curl -O https://repo1.maven.org/maven2/com/sun/xml/bind/jaxb-ri/3.0.2/jaxb-ri-3.0.2.zip
tar xf jaxb-ri-3.0.2.zip
ren jaxb-ri jaxb-ri-3.0.2

rem
rem Eclipse
rem
curl -L -o eclipse.zip "https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/2024-03/R/eclipse-jee-2024-03-R-win32-x86_64.zip&mirror_id=1"
tar -xf eclipse.zip

rem
rem WinGet Installs
rem
winget install --accept-source-agreements Microsoft.WindowsTerminal
winget install Microsoft.PowerShell
winget install Git.Git
winget install GitHub.cli
winget install EclipseAdoptium.Temurin.8.JDK
winget install RubyInstallerTeam.RubyWithDevKit.3.1
winget install Python.Python.3.11
winget install SublimeHQ.SublimeText.4
winget install JetBrains.IntelliJIDEA.Community
winget install Microsoft.VisualStudio.2022.Community
winget install dotnet-sdk-3_1

rem provides sn.exe
winget install Microsoft.WindowsSDK.10.0.20348

rem required for Maven deploy
winget install GnuPG.Gpg4win

rem
rem Nuget
rem
mkdir nuget
curl -o nuget\nuget.exe https://dist.nuget.org/win-x86-commandline/latest/nuget.exe
set NUGET_HOME=%INSTALL_DIR%\nuget
setx NUGET_HOME %NUGET_HOME%

rem
rem Python packages
rem
py -m pip install mkdocs-material build twine

rem
rem Quick download of MPXJ for testing - replace with git clone
rem
curl -L -o mpxj.zip https://github.com/joniles/mpxj/archive/refs/heads/master.zip
tar -xf mpxj.zip
ren mpxj-master mpxj

rem
rem Cleanup downloaded files
rem
del *.zip
del *.exe


echo Manual Steps
echo Add the following entries to the PATH
echo    %%ANT_HOME%%\bin
echo    %%MAVEN_HOME%%\bin
echo    %%NUGET_HOME%%
echo To configure GitHub access, run:
echo    gh auth login
echo To enable deploys, add the following environment variables
echo    NUGET_API_KEY (NuGet API key)
echo    TWINE_USERNAME (__token__)
echo    TWINE_PASSWORD (PyPi API Key)
echo    GEM_HOST_API_KEY (Rubygems API key)
echo To enable Maven deploys
echo    create gpg key and publish to server
echo    configure Maven Central credentials in .m2\settings.xml

pause


