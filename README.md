SonarQube Java Properties Plugin
====================

[![Build Status](https://api.travis-ci.org/racodond/sonar-jproperties-plugin.svg)](https://travis-ci.org/racodond/sonar-jproperties-plugin)

## Description
This plugin enables code QA analysis of [Java Properties files](https://en.wikipedia.org/wiki/.properties) within [SonarQube](http://www.sonarqube.org):

 * Computes metrics: lines of code, comments lines, etc.
 * Performs more than 15 checks such as: Duplicated keys should be removed, Property with empty value should be removed, Separators should follow a convention, etc.


## Useful Links

* [Homepage](https://github.com/racodond/sonar-jproperties-plugin)
* [Issue tracking](https://github.com/racodond/sonar-jproperties-plugin/issues)
* [Download](https://github.com/racodond/sonar-jproperties-plugin/releases/)

## Usage
1. [Download and install](http://docs.sonarqube.org/display/SONAR/Setup+and+Upgrade) SonarQube
2. [Download](https://github.com/racodond/sonar-jproperties-plugin/releases) the Java Properties plugin.
3. [Manually install](http://docs.sonarqube.org/display/SONAR/Installing+a+Plugin) the Java Properties plugin.
4. [Install your favorite analyzer](http://docs.sonarqube.org/display/SONAR/Analyzing+Source+Code#AnalyzingSourceCode-RunningAnalysis) (SonarQube Runner, Maven, etc.) and analyze your code.
