SonarQube Java Properties Plugin
====================

[![Build Status](https://api.travis-ci.org/racodond/sonar-jproperties-plugin.svg)](https://travis-ci.org/racodond/sonar-jproperties-plugin)
[![Quality Gate](https://nemo.sonarqube.org/api/badges/gate?key=org.codehaus.sonar-plugins.jproperties:jproperties)](https://nemo.sonarqube.org/overview?id=org.codehaus.sonar-plugins.jproperties%3Ajproperties)

Plugin versions and compatibility with SonarQube versions: [http://docs.sonarqube.org/display/PLUG/Plugin+Version+Matrix](http://docs.sonarqube.org/display/PLUG/Plugin+Version+Matrix)

## Description
This plugin enables code QA analysis of [Java Properties files](https://en.wikipedia.org/wiki/.properties) within [SonarQube](http://www.sonarqube.org):

 * Computes metrics: lines of code, comments lines, etc.
 * Performs more than 20 checks such as: Duplicated keys should be removed, Property with empty value should be removed, Separators should follow a convention, etc. [Browse the full list of checks](https://nemo.sonarqube.org/coding_rules#languages=jproperties).

## Usage
1. [Download and install](http://docs.sonarqube.org/display/SONAR/Setup+and+Upgrade) SonarQube
2. Install the Java Properties plugin either by a [direct download](https://github.com/racodond/sonar-jproperties-plugin/releases) or through the [Update Center](http://docs.sonarqube.org/display/SONAR/Update+Center).
3. [Install your favorite analyzer](http://docs.sonarqube.org/display/SONAR/Analyzing+Source+Code#AnalyzingSourceCode-RunningAnalysis) (SonarQube Scanner, Maven, Ant, etc.) and analyze your code. Note that starting at version 2.0, Java 8 is required to run an analysis.

## Notes

 * The following kind of error may indicate that you did not properly set the `sonar.sourceEncoding` property. Prior to version 2.0, only ISO-8859-1 was supported.
 
        Caused by: java.lang.IllegalArgumentException: Unable to highlight file \[moduleKey=xxx, relative=xxx, basedir=xxx\] from offset 808 to offset 876
         at org.sonar.api.batch.sensor.highlighting.internal.DefaultHighlighting.highlight(DefaultHighlighting.java:85)
         at org.sonar.batch.source.DefaultHighlightable$DefaultHighlightingBuilder.highlight(DefaultHighlightable.java:79)
         at org.sonar.jproperties.ast.visitors.SyntaxHighlighterVisitor.visitNode(SyntaxHighlighterVisitor.java:93)
         at com.sonar.sslr.impl.ast.AstWalker.visitNode(AstWalker.java:114)
         ...
        

 
