#!/bin/bash

echo "========= Setup permission =========="
sudo chown -R vscode:vscode node_modules

echo "========= Setup alias =========="
cat << EOF >> ~/.bashrc 
alias ll='ls -l'
alias la='ls -A'
alias l='ls -CF'
EOF

echo "========= Setup Sphinx =========="
pip3 install -r .devcontainer/requirements.txt

echo "========= Setup Textlint =========="
# reStructuredText用をASTに変換するPythonモジュールのインストール「docutils-ast-writer」
# ・「textlint-plugin-rst」で使用する。
# ・Sphinxに合わせた docutils に対応したフォークしたモジュール。
mkdir ~/python_modules && cd ~/python_modules
pip3 install -e git+https://github.com/mygreen/docutils-ast-writer@mygreen#egg=docutils-ast-writer
cd ${CONTAINER_WORKSPACE_FOLDER}

source $NVM_DIR/nvm.sh && nvm install ${NODE_VERSION}

## npm ciを post create で実行すると、textlintのインストール前にVScodeが開かれtextlintのサーバ起動に失敗するため、
## on create で実行する。
npm ci

echo "=========================="
echo "Finish ! on create"
echo "=========================="

