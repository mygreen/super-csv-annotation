@echo off

%~d0
cd %~p0

mvn javadoc:javadoc -Dencoding=UTF-8 -Dcharset=UTF-8 -Ddecoding=UTF-8

pause
