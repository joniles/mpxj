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
rem .Net Framework 4.5
rem
curl -O https://download.microsoft.com/download/4/3/B/43B61315-B2CE-4F5B-9E32-34CCA07B2F0E/NDP452-KB2901951-x86-x64-DevPack.exe
start /wait NDP452-KB2901951-x86-x64-DevPack.exe /q /norestart

rem
rem IKVM versions
rem
curl -L -o mpxj-legacy-ikvm.zip https://github.com/joniles/mpxj-legacy-ikvm/archive/refs/heads/master.zip
tar -xf mpxj-legacy-ikvm.zip
ren mpxj-legacy-ikvm-master mpxj-legacy-ikvm

rem
rem POIFS
rem
set POIFS_VERSION=5.2.2
mkdir poifs\target
curl -L -o poifs\target\poifs-%POIFS_VERSION%.jar https://github.com/joniles/poifs/releases/download/v%POIFS_VERSION%/poifs-%POIFS_VERSION%.jar

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
rem Install signing key - required for .Net core version of IKVM
rem
curl -O -L https://raw.githubusercontent.com/joniles/mpxj/master/src.net/mpxj.snk
"C:\Program Files (x86)\Microsoft SDKs\Windows\v10.0A\bin\NETFX 4.8 Tools\sn.exe" -m y
"C:\Program Files (x86)\Microsoft SDKs\Windows\v10.0A\bin\NETFX 4.8 Tools\sn.exe" -i mpxj.snk mpxj-key

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
del *.snk
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
echo    configure ossrh credentials in .m2\settings.xml

pause


