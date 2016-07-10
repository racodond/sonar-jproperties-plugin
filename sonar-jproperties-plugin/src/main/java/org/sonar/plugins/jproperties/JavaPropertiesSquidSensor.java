/*
 * SonarQube Java Properties Plugin
 * Copyright (C) 2015-2016 David RACODON
 * david.racodon@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.jproperties;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.typed.ActionParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.jproperties.checks.CheckList;
import org.sonar.jproperties.checks.ParsingErrorCheck;
import org.sonar.jproperties.parser.JavaPropertiesParserBuilder;
import org.sonar.jproperties.visitors.CharsetAwareVisitor;
import org.sonar.jproperties.visitors.JavaPropertiesVisitorContext;
import org.sonar.jproperties.visitors.SyntaxHighlighterVisitor;
import org.sonar.jproperties.visitors.metrics.MetricsVisitor;
import org.sonar.plugins.jproperties.api.JavaPropertiesCheck;
import org.sonar.plugins.jproperties.api.tree.PropertiesTree;
import org.sonar.plugins.jproperties.api.tree.Tree;
import org.sonar.plugins.jproperties.api.visitors.TreeVisitor;
import org.sonar.plugins.jproperties.api.visitors.issue.Issue;
import org.sonar.squidbridge.api.AnalysisException;

public class JavaPropertiesSquidSensor implements Sensor {

  private static final Logger LOG = Loggers.get(JavaPropertiesSquidSensor.class);

  private final FileSystem fileSystem;
  private final JavaPropertiesChecks checks;
  private final ActionParser<Tree> parser;
  private final FilePredicate mainFilePredicate;
  private IssueSaver issueSaver;
  private RuleKey parsingErrorRuleKey = null;

  public JavaPropertiesSquidSensor(FileSystem fileSystem, CheckFactory checkFactory) {
    this.fileSystem = fileSystem;

    this.mainFilePredicate = fileSystem.predicates().and(
      fileSystem.predicates().hasType(InputFile.Type.MAIN),
      fileSystem.predicates().hasLanguage(JavaPropertiesLanguage.KEY));

    this.parser = JavaPropertiesParserBuilder.createParser(fileSystem.encoding());

    this.checks = JavaPropertiesChecks.createJavaPropertiestCheck(checkFactory)
      .addChecks(CheckList.REPOSITORY_KEY, CheckList.getChecks());
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor
      .onlyOnLanguage(JavaPropertiesLanguage.KEY)
      .name("Java Properties Squid Sensor")
      .onlyOnFileType(Type.MAIN);
  }

  @Override
  public void execute(SensorContext sensorContext) {
    List<TreeVisitor> treeVisitors = Lists.newArrayList();
    treeVisitors.addAll(checks.visitorChecks());
    treeVisitors.add(new SyntaxHighlighterVisitor(sensorContext));
    treeVisitors.add(new MetricsVisitor(sensorContext));

    setParsingErrorCheckIfActivated(treeVisitors);

    issueSaver = new IssueSaver(sensorContext, checks);
    List<Issue> issues = new ArrayList<>();
    for (InputFile inputFile : fileSystem.inputFiles(mainFilePredicate)) {
      issues.addAll(analyzeFile(sensorContext, inputFile, treeVisitors));
    }
    saveSingleFileIssues(issues);
    saveCrossFileIssues();
  }

  private List<Issue> analyzeFile(SensorContext sensorContext, InputFile inputFile, List<TreeVisitor> visitors) {
    try {
      PropertiesTree propertiesTree = (PropertiesTree) parser.parse(new File(inputFile.absolutePath()));
      return scanFile(inputFile, propertiesTree, visitors);
    } catch (RecognitionException e) {
      LOG.error("Unable to parse file: " + inputFile.absolutePath());
      LOG.error(e.getMessage());
      processRecognitionException(e, sensorContext, inputFile);
    } catch (Exception e) {
      throw new AnalysisException("Unable to analyse file: " + inputFile.absolutePath(), e);
    }
    return new ArrayList<>();
  }

  private List<Issue> scanFile(InputFile inputFile, PropertiesTree propertiesTree, List<TreeVisitor> visitors) {
    JavaPropertiesVisitorContext context = new JavaPropertiesVisitorContext(propertiesTree, inputFile.file());
    List<Issue> issues = new ArrayList<>();
    for (TreeVisitor visitor : visitors) {
      if (visitor instanceof CharsetAwareVisitor) {
        ((CharsetAwareVisitor) visitor).setCharset(fileSystem.encoding());
      }
      if (visitor instanceof JavaPropertiesCheck) {
        issues.addAll(((JavaPropertiesCheck) visitor).scanFile(context));
      } else {
        visitor.scanTree(context);
      }
    }
    return issues;
  }

  private void saveSingleFileIssues(List<Issue> issues) {
    for (Issue issue : issues) {
      issueSaver.saveIssue(issue);
    }
  }

  private void saveCrossFileIssues() {
    new CrossFileChecks(issueSaver).saveCrossFileIssues();
  }

  private void processRecognitionException(RecognitionException e, SensorContext sensorContext, InputFile inputFile) {
    if (parsingErrorRuleKey != null) {
      NewIssue newIssue = sensorContext.newIssue();

      NewIssueLocation primaryLocation = newIssue.newLocation()
        .message(e.getMessage())
        .on(inputFile)
        .at(inputFile.selectLine(e.getLine()));

      newIssue
        .forRule(parsingErrorRuleKey)
        .at(primaryLocation)
        .save();
    }
  }

  private void setParsingErrorCheckIfActivated(List<TreeVisitor> treeVisitors) {
    for (TreeVisitor check : treeVisitors) {
      if (check instanceof ParsingErrorCheck) {
        parsingErrorRuleKey = checks.ruleKeyFor((JavaPropertiesCheck) check);
        break;
      }
    }
  }

}
