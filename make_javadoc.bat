@echo off

%~d0
cd %~p0

if NOT "%JAVA_HOME_8%" == "" (
    set JAVA_HOME="%JAVA_HOME_8%"
)

del javadoc.txt
call mvn javadoc:javadoc 2>&1 > javadoc.txt

start javadoc.txt


