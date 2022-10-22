@echo off

%~d0
cd %~p0

call env.bat
call mvn -version

call mvn clean
mkdir target
call mvn site -Dgpg.skip=true -Dfile.encoding=UTF-8 > target/site.log 2>&1

REM github-pagesの対応
echo "" > .\target\site\.nojekyll

start target/site.log

