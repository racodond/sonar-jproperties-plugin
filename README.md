[![Release](https://img.shields.io/github/release/racodond/sonar-jproperties-plugin.svg)](https://github.com/racodond/sonar-jproperties-plugin/releases/latest)
[![Build Status](https://api.travis-ci.org/racodond/sonar-jproperties-plugin.svg?branch=master)](https://travis-ci.org/racodond/sonar-jproperties-plugin)
[![AppVeyor Build Status](https://ci.appveyor.com/api/projects/status/v2gdt7d94kq4ngcm/branch/master?svg=true)](https://ci.appveyor.com/project/racodond/sonar-jproperties-plugin/branch/master)

[![Quality Gate status](https://sonarcloud.io/api/project_badges/measure?project=org.codehaus.sonar-plugins.jproperties%3Ajproperties&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.codehaus.sonar-plugins.jproperties%3Ajproperties)
[![Lines of code](https://sonarcloud.io/api/project_badges/measure?project=org.codehaus.sonar-plugins.jproperties%3Ajproperties&metric=ncloc)](https://sonarcloud.io/dashboard?id=org.codehaus.sonar-plugins.jproperties%3Ajproperties)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.codehaus.sonar-plugins.jproperties%3Ajproperties&metric=coverage)](https://sonarcloud.io/dashboard?id=org.codehaus.sonar-plugins.jproperties%3Ajproperties)


# SonarQube Java Properties Analyzer

## Description
This [SonarQube](https://www.sonarqube.org/) plugin analyzes [Java Properties files](https://en.wikipedia.org/wiki/.properties) and:

* Computes metrics: lines of code, comments lines, etc.
* Checks various guidelines to find out potential bugs, vulnerabilities and code smells through more than [30 checks](#available-rules)
* Provides the ability to write your own checks


## Usage
1. [Download and install](http://docs.sonarqube.org/display/SONAR/Setup+and+Upgrade) SonarQube
1. [Download](https://github.com/racodond/sonar-jproperties-plugin/releases) and install the Java Properties plugin. The latest version is compatible with SonarQube 6.7+
1. Install your [favorite scanner](http://docs.sonarqube.org/display/SONAR/Analyzing+Source+Code#AnalyzingSourceCode-RunningAnalysis) (SonarQube Scanner, Maven, Ant, etc.)
1. [Analyze your code](http://docs.sonarqube.org/display/SONAR/Analyzing+Source+Code#AnalyzingSourceCode-RunningAnalysis).


## Custom Checks
You're thinking of new valuable rules? Version 2.1 or greater provides an API to write your own custom checks.
A sample plugin with detailed explanations is available [here](https://github.com/racodond/sonar-jproperties-custom-rules-plugin).
If your custom rules may benefit the community, feel free to create a pull request in order to make the rule available in the Java Properties plugin.

You're thinking of new rules that may benefit the community but don't have the time or the skills to write them? Feel free to create an [issue](https://github.com/racodond/sonar-jproperties-plugin/issues) for your rules to be taken under consideration.


## Available Rules

* "FIXME" tags should be handled
* "TODO" tags should be handled
* All comments should be formatted consistently
* All properties and comments should start at column 1
* Byte Order Mark (BOM) should not be used for UTF-8 files
* Credentials should not be hard-coded
* Different keys having the same value should be merged
* Duplicated keys across files should be removed
* Duplicated keys should be removed
* End-line characters should be consistent
* File names should comply with a naming convention
* Files not defining any properties should be removed
* Files should contain an empty new line at the end
* Keys should follow a naming convention
* Lines should not be too long
* Missing translations should be added to default resource bundle
* Missing translations should be added to locale resource bundles
* Number of keys should be reduced
* Property with empty value should be removed
* Regular expression on comment
* Regular expression on key
* Regular expression on value
* Sections of code should not be commented out
* Separators should follow a convention
* Tabulation characters should not be used
