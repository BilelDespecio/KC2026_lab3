@echo off
cd /d "%~dp0"
chcp 65001 >nul
if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot\bin\java.exe" set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot"
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"
call ".\.runtime\apache-maven-3.9.9\bin\mvn.cmd" clean package
if errorlevel 1 (
 echo BUILD FAILED
 pause
 exit /b 1
)
echo BUILD OK
pause
