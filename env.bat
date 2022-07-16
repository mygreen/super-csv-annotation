@echo off

if NOT "%JAVA_HOME_8%" == "" (
    set JAVA_HOME="%JAVA_HOME_8%"
)

set PATH=%PATH%;%JAVA_HOME%\bin;%M2_HOME%\bin;


