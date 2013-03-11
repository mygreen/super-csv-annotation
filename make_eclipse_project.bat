@echo off

%~d0
cd %~p0

REM mvn clean
mvn eclipse:eclipse -DdownloadSources=true

pause
