@echo off

if NOT "%JAVA_HOME_8%" == "" (
    set JAVA_HOME="%JAVA_HOME_8%"
)

set JAVA_HOME=C:\dev\java\amazon-corretto-8.422.05.1

set PATH=%PATH%;%JAVA_HOME%\bin;%M2_HOME%\bin;


