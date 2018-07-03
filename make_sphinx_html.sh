#!/bin/sh -e

echo "Building sphinx documentation for version $1"

SCRIPT_DIR=$(cd $(dirname $0); pwd)
cd $SCRIPT_DIR

if [ -e ./src/site/sphinx/build ]; then
  echo "step - remove old build directory."
  sudo /bin/rm -rf ./src/site/sphinx/build
fi

cd ./src/site/sphinx

echo "step - make html by sphinx."
make html PACKAGE_VERSION=$1

echo "step - change owner for build directry with jenkins."
sudo /usr/bin/chown -R jenkins:jenkins ./build

## copy html dir
cd $SCRIPT_DIR

echo "step - remove target sphinx directory."
sudo /bin/rm -rf ./target/site/sphinx
/bin/mkdir -p ./target/site/sphinx

echo "step - copy build html to target directory."
/bin/cp -vr ./src/site/sphinx/build/html/* ./target/site/sphinx/

## github-pagesのsphinx対応
echo "step - create file or .nojekyll."
touch ./target/site/.nojekyll

