#!/bin/bash

mvn -B clean install

if [ "$TRAVIS_PULL_REQUEST" != "false" ] && [ "$SQ_VERSION" == "LTS" ]; then
  mvn -B sonar:sonar \
      -Dsonar.analysis.mode=issues \
      -Dsonar.github.pullRequest=$TRAVIS_PULL_REQUEST \
      -Dsonar.github.repository=$TRAVIS_REPO_SLUG \
      -Dsonar.github.oauth=$GITHUB_TOKEN \
      -Dsonar.login=$SONAR_TOKEN

elif [ "$TRAVIS_BRANCH" == "master" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$SQ_VERSION" == "LTS" ]; then
  mvn -B sonar:sonar -Dsonar.login=$SONAR_TOKEN
fi

cd its
mvn -B clean install -Dsonar.runtimeVersion=$SQ_VERSION
