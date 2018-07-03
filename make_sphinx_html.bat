@echo off

REM Sphinxのドキュメントをビルドし、taregetのsiteフォルダに配置する。

echo "Building sphinx documentation for version %1"

%~d0
cd %~p0

pushd .\src\site\sphinx

rmdir /q /s build
call make html PACKAGE_VERSION=%1

popd

rmdir /q /s .\target\site\sphinx
mkdir .\target\site\sphinx
xcopy /y /e .\src\site\sphinx\build\html .\target\site\sphinx

REM github-pagesのsphinx対応
echo "" > .\target\site\.nojekyll

