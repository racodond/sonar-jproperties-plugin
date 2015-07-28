SonarQube Java Properties Plugin
====================

[![Build Status](https://api.travis-ci.org/racodond/sonar-jproperties-plugin.svg)](https://travis-ci.org/racodond/sonar-jproperties-plugin)

## Description
This plugin enables code QA analysis of [Java Properties files](https://en.wikipedia.org/wiki/.properties) within [SonarQube](http://www.sonarqube.org):

 * Computes metrics: lines of code, comments lines, etc.
 * Performs more than 15 checks such as: Duplicated keys should be removed, Property with empty element should be removed, Separators should follow a convention, etc.


## Useful Links

* [Homepage](https://github.com/racodond/sonar-jproperties-plugin)
* [Issue tracking](https://github.com/racodond/sonar-jproperties-plugin/issues)
* [Download](https://github.com/racodond/sonar-jproperties-plugin/releases/)

## Usage
1. [Install](http://docs.sonarqube.org/display/SONAR/Setup+and+Upgrade) SonarQube
2. Install the Java Properties plugin either by a [direct download](https://github.com/SonarCommunity/sonar-css/releases) or through the [update center](http://docs.sonarqube.org/display/SONAR/Update+Center).
3. Install your [favorite analyzer](http://docs.sonarqube.org/display/SONAR/Analyzing+Source+Code#AnalyzingSourceCode-RunningAnalysis) (SonarQube Runner, Maven, etc.) and analyze your code.
