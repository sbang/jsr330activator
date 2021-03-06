#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "sbang/jsr330activator" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing javadoc...\n"

  cp -R jsr330activator.implementation/target/apidocs $HOME/javadoc-latest
  cp -R jsr330activator.mocks/target/apidocs $HOME/osgi-mocks-javadoc-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/sbang/jsr330activator gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./javadoc
  git rm -rf ./osgi-mocks-javadoc
  cp -Rf $HOME/javadoc-latest ./javadoc
  cp -Rf $HOME/osgi-mocks-javadoc-latest ./osgi-mocks-javadoc
  git add -f .
  git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published Javadoc to gh-pages.\n"
  
fi
