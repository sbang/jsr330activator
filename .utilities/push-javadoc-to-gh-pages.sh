#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "sbang/jsr330activator" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing javadoc...\n"

  ls -al jsr330activator.implementation/target/apidocs
  cp -R jsr330activator.implementation/target/apidocs $HOME/javadoc-latest
  ls -al $HOME/javadoc-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/sbang/jsr330activator gh-pages

  cd gh-pages
  git rm -rf ./javadoc
  cp -Rf $HOME/javadoc-latest ./javadoc
  git add -f .
  git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages

  echo -e "Published Javadoc to gh-pages.\n"
  
fi
