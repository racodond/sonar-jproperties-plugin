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

import java.io.File;
import java.util.Collection;

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.jproperties.JavaPropertiesAstScanner;
import org.sonar.jproperties.JavaPropertiesConfiguration;
import org.sonar.jproperties.api.JavaPropertiesMetric;
import org.sonar.jproperties.ast.visitors.SonarComponents;
import org.sonar.jproperties.checks.CheckList;
import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.api.CheckMessage;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.indexer.QueryByType;
import org.sonar.sslr.parser.LexerlessGrammar;

public class JavaPropertiesSquidSensor implements Sensor {

  private final CheckFactory checkFactory;

  private SensorContext context;
  private AstScanner<LexerlessGrammar> scanner;
  private final SonarComponents sonarComponents;
  private final FileSystem fs;
  private final RulesProfile rulesProfile;
  private final ResourcePerspectives resourcePerspectives;
  private Project project;

  public JavaPropertiesSquidSensor(SonarComponents sonarComponents, FileSystem fs, CheckFactory checkFactory, RulesProfile rulesProfile, ResourcePerspectives resourcePerspectives) {
    this.checkFactory = checkFactory;
    this.sonarComponents = sonarComponents;
    this.fs = fs;
    this.rulesProfile = rulesProfile;
    this.resourcePerspectives = resourcePerspectives;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return filesToAnalyze().iterator().hasNext();
  }

  @Override
  public void analyse(Project project, SensorContext context) {
    this.project = project;
    this.context = context;
    Checks<SquidAstVisitor> checks = checkFactory.<SquidAstVisitor>create(JavaProperties.KEY).addAnnotatedChecks(CheckList.getChecks());
    Collection<SquidAstVisitor> checkList = checks.all();
    this.scanner = JavaPropertiesAstScanner.create(new JavaPropertiesConfiguration(), sonarComponents, checkList.toArray(new SquidAstVisitor[checkList.size()]));
    scanner.scanFiles(Lists.newArrayList(filesToAnalyze()));

    Collection<SourceCode> squidSourceFiles = scanner.getIndex().search(new QueryByType(SourceFile.class));
    save(squidSourceFiles, checks);
  }

  private Iterable<File> filesToAnalyze() {
    return fs.files(fs.predicates().and(fs.predicates().hasLanguage(JavaProperties.KEY), fs.predicates().hasType(Type.MAIN)));
  }

  private void save(Collection<SourceCode> squidSourceFiles, Checks<SquidAstVisitor> checks) {
    for (SourceCode squidSourceFile : squidSourceFiles) {
      SourceFile squidFile = (SourceFile) squidSourceFile;
      InputFile sonarFile = fs.inputFile(fs.predicates().hasAbsolutePath(squidFile.getKey()));
      saveMeasures(sonarFile, squidFile);
      saveIssues(sonarFile, squidFile, checks);
    }
    ProjectChecks projectChecks = new ProjectChecks(project, rulesProfile, checks, resourcePerspectives);
    projectChecks.reportProjectIssues();
  }

  private void saveMeasures(InputFile sonarFile, SourceFile squidFile) {
    context.saveMeasure(sonarFile, CoreMetrics.LINES, squidFile.getDouble(JavaPropertiesMetric.LINES));
    context.saveMeasure(sonarFile, CoreMetrics.NCLOC, squidFile.getDouble(JavaPropertiesMetric.LINES_OF_CODE));
    context.saveMeasure(sonarFile, CoreMetrics.STATEMENTS, squidFile.getDouble(JavaPropertiesMetric.STATEMENTS));
    context.saveMeasure(sonarFile, CoreMetrics.COMMENT_LINES, squidFile.getDouble(JavaPropertiesMetric.COMMENT_LINES));
  }

  private void saveIssues(InputFile sonarFile, SourceFile squidFile, Checks<SquidAstVisitor> checks) {
    Collection<CheckMessage> messages = squidFile.getCheckMessages();
    if (messages != null) {
      for (CheckMessage message : messages) {
        RuleKey activeRule = checks.ruleKey((SquidAstVisitor) message.getCheck());
        Issuable issuable = sonarComponents.getResourcePerspectives().as(Issuable.class, sonarFile);
        if (issuable != null && activeRule != null) {
          Issue issue = issuable.newIssueBuilder()
            .ruleKey(RuleKey.of(activeRule.repository(), activeRule.rule()))
            .line(message.getLine())
            .message(message.formatDefaultMessage())
            .effortToFix(message.getCost())
            .build();
          issuable.addIssue(issue);
        }
      }
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
