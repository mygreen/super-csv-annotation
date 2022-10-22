@echo off

%~d0
cd %~p0

call env.bat

call mvn -version

call mvn clean -Dmaven.test.skip=true source:jar javadoc:jar deploy

pause
