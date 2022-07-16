@echo off

%~d0
cd %~p0

call env.bat
call mvn -version

mkdir target
call mvn javadoc:javadoc > target/javadoc.log 2>&1 

start target/javadoc.log

