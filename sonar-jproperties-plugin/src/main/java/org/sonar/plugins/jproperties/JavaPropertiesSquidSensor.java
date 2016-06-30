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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.jproperties.JavaPropertiesAstScanner;
import org.sonar.jproperties.JavaPropertiesConfiguration;
import org.sonar.jproperties.api.JavaPropertiesMetric;
import org.sonar.jproperties.checks.CheckList;
import org.sonar.jproperties.issue.Issue;
import org.sonar.jproperties.issue.PreciseIssue;
import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.squidbridge.indexer.QueryByType;
import org.sonar.sslr.parser.LexerlessGrammar;

public class JavaPropertiesSquidSensor implements Sensor {

  private final CheckFactory checkFactory;

  private SensorContext sensorContext;
  private final FileSystem fileSystem;
  private final FilePredicate mainFilePredicate;

  public JavaPropertiesSquidSensor(FileSystem fileSystem, CheckFactory checkFactory) {
    this.checkFactory = checkFactory;
    this.fileSystem = fileSystem;
    this.mainFilePredicate = fileSystem.predicates().and(
      fileSystem.predicates().hasType(InputFile.Type.MAIN),
      fileSystem.predicates().hasLanguage(JavaPropertiesLanguage.KEY));
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
    this.sensorContext = sensorContext;

    Checks<SquidCheck> checks = checkFactory.<SquidCheck>create(JavaPropertiesLanguage.KEY).addAnnotatedChecks((Iterable) CheckList.getChecks());
    Collection<SquidCheck> checkList = checks.all();

    Set<Issue> issues = new HashSet<>();

    AstScanner<LexerlessGrammar> scanner = JavaPropertiesAstScanner.create(
      sensorContext,
      new JavaPropertiesConfiguration(fileSystem.encoding()),
      issues,
      checkList.toArray(new SquidAstVisitor[checkList.size()]));
    scanner.scanFiles(Lists.newArrayList(fileSystem.files(mainFilePredicate)));

    Collection<SourceCode> squidSourceFiles = scanner.getIndex().search(new QueryByType(SourceFile.class));
    save(squidSourceFiles, checks, issues);
  }

  private void save(Collection<SourceCode> squidSourceFiles, Checks<SquidCheck> checks, Set<Issue> issues) {
    for (SourceCode squidSourceFile : squidSourceFiles) {
      SourceFile squidFile = (SourceFile) squidSourceFile;
      InputFile sonarFile = fileSystem.inputFile(fileSystem.predicates().hasAbsolutePath(squidFile.getKey()));
      saveMeasures(sonarFile, squidFile);
    }
    ProjectChecks projectChecks = new ProjectChecks(checks, issues);
    projectChecks.checkProjectIssues();
    saveIssues(checks, issues);
  }

  private void saveMeasures(InputFile sonarFile, SourceFile squidFile) {
    sensorContext.<Integer>newMeasure()
      .on(sonarFile)
      .forMetric(CoreMetrics.NCLOC)
      .withValue(squidFile.getInt(JavaPropertiesMetric.LINES_OF_CODE))
      .save();

    sensorContext.<Integer>newMeasure()
      .on(sonarFile)
      .forMetric(CoreMetrics.STATEMENTS)
      .withValue(squidFile.getInt(JavaPropertiesMetric.STATEMENTS))
      .save();

    sensorContext.<Integer>newMeasure()
      .on(sonarFile)
      .forMetric(CoreMetrics.COMMENT_LINES)
      .withValue(squidFile.getInt(JavaPropertiesMetric.COMMENT_LINES))
      .save();
  }

  private void saveIssues(Checks<SquidCheck> checks, Set<Issue> issues) {
    // TODO: Try and remove TestIssue to avoid this ugly if
    for (Issue issue : issues) {
      if (issue instanceof PreciseIssue) {
        ((PreciseIssue) issue).save(checks, sensorContext);
      } else {
        throw new IllegalStateException("Unsupported type of issue to be saved.");
      }
    }
  }

}
