@echo off

%~d0
cd %~p0

if NOT "%JAVA_HOME_8%" == "" (
    set JAVA_HOME="%JAVA_HOME_8%"
)

call mvn clean site -Dgpg.skip=true > target/site.log 2>&1 

start target/site.log

