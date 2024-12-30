#!/bin/bash

echo "Building sphinx documentation for version $1"

SCRIPT_DIR=$(cd $(dirname $0); pwd)
cd $SCRIPT_DIR

if [ -e ./build ]; then
  echo "step - remove old build directory."
  sudo /bin/rm -rf ./build/*
fi

echo "step - make html by sphinx."
make html PACKAGE_VERSION=$1

TARGET_DIR=../target
echo "step - remove target sphinx directory."
sudo /bin/rm -rf ${TARGET_DIR}/site/sphinx
/bin/mkdir -p ${TARGET_DIR}/site/sphinx

echo "step - copy build html to target directory."
/bin/cp -vr ./build/html/* ${TARGET_DIR}/site/sphinx/

## github-pagesのsphinx対応
echo "step - create file or .nojekyll."
touch ${TARGET_DIR}/site/.nojekyll
