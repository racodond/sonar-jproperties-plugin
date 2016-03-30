SonarQube Java Properties Plugin
====================

[![Build Status](https://api.travis-ci.org/racodond/sonar-jproperties-plugin.svg)](https://travis-ci.org/racodond/sonar-jproperties-plugin)
[![Quality Gate](https://nemo.sonarqube.org/api/badges/gate?key=org.codehaus.sonar-plugins.jproperties:jproperties)](https://nemo.sonarqube.org/overview?id=org.codehaus.sonar-plugins.jproperties%3Ajproperties)

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
2. Install the Java Properties plugin either by a [direct download](https://github.com/racodond/sonar-jproperties-plugin/releases) or through the [Update Center](http://docs.sonarqube.org/display/SONAR/Update+Center).
3. [Install your favorite analyzer](http://docs.sonarqube.org/display/SONAR/Analyzing+Source+Code#AnalyzingSourceCode-RunningAnalysis) (SonarQube Runner, Maven, etc.) and analyze your code.

## Notes

 * Java properties files are expected to be encoded in ISO-8859-1 as stated in Java specifications. During analysis, the following error may indicate that the file is not encoded in ISO-8859-1:
 
        Caused by: java.lang.IllegalArgumentException: Unable to highlight file \[moduleKey=xxx, relative=xxx, basedir=xxx\] from offset 808 to offset 876
         at org.sonar.api.batch.sensor.highlighting.internal.DefaultHighlighting.highlight(DefaultHighlighting.java:85)
         at org.sonar.batch.source.DefaultHighlightable$DefaultHighlightingBuilder.highlight(DefaultHighlightable.java:79)
         at org.sonar.jproperties.ast.visitors.SyntaxHighlighterVisitor.visitNode(SyntaxHighlighterVisitor.java:93)
         at com.sonar.sslr.impl.ast.AstWalker.visitNode(AstWalker.java:114)
         ...
        

 
