[![Build Status](https://api.travis-ci.org/racodond/sonar-jproperties-plugin.svg?branch=master)](https://travis-ci.org/racodond/sonar-jproperties-plugin)
[![AppVeyor Build Status](https://ci.appveyor.com/api/projects/status/v2gdt7d94kq4ngcm/branch/master?svg=true)](https://ci.appveyor.com/project/racodond/sonar-jproperties-plugin/branch/master)


# SonarQube Java Properties Analyzer

## Description
This [SonarQube](https://www.sonarqube.org/) plugin analyzes [Java Properties files](https://en.wikipedia.org/wiki/.properties) and:

* Computes metrics: lines of code, comments lines, etc.
* Checks various guidelines to find out potential bugs, vulnerabilities and code smells through more than [30 checks](#available-rules)
* Provides the ability to write your own checks


## Usage
1. [Download and install](http://docs.sonarqube.org/display/SONAR/Setup+and+Upgrade) SonarQube
1. [Download](https://github.com/racodond/sonar-jproperties-plugin/releases) and install the Java Properties plugin. The latest version is compatible with SonarQube 5.6+
1. Install your [favorite scanner](http://docs.sonarqube.org/display/SONAR/Analyzing+Source+Code#AnalyzingSourceCode-RunningAnalysis) (SonarQube Scanner, Maven, Ant, etc.)
1. [Analyze your code](http://docs.sonarqube.org/display/SONAR/Analyzing+Source+Code#AnalyzingSourceCode-RunningAnalysis). By default, files are expected to be encoded in ISO-8859-1. If it is not the case, set the `sonar.jproperties.sourceEncoding` property to the right encoding. See the [full list of supported encodings](http://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html) (Canonical Name for java.nio API column).


## Custom Checks
You're thinking of new valuable rules? Version 2.1 or greater provides an API to write your own custom checks.
A sample plugin with detailed explanations is available [here](https://github.com/racodond/sonar-jproperties-custom-rules-plugin).
If your custom rules may benefit the community, feel free to create a pull request in order to make the rule available in the Java Properties plugin.

You're thinking of new rules that may benefit the community but don't have the time or the skills to write them? Feel free to create an [issue](https://github.com/racodond/sonar-jproperties-plugin/issues) for your rules to be taken under consideration.


## Contributing
Any contribution is more than welcome!
 
You feel like:
* Adding a new check? Just [open an issue](https://github.com/racodond/sonar-jproperties-plugin/issues/new) to discuss the value of your check. Once validated, code, don't forget to add a lot of unit tests and open a PR.
* Fixing some bugs or improving existing checks? Just open a PR.


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
* ISO-8859-1 characters not compatible with UTF-8 should be escaped to keep compatibility with Java 9 default encoding switch
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
