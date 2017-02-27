/*
 * SonarQube Java Properties Analyzer
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

import com.google.common.base.Charsets;
import org.junit.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.FileMetadata;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rule.RuleKey;
import org.sonar.check.Rule;
import org.sonar.jproperties.checks.generic.CommentConventionCheck;
import org.sonar.jproperties.checks.generic.LineLengthCheck;
import org.sonar.jproperties.checks.generic.MissingNewlineAtEndOfFileCheck;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class JavaPropertiesSquidSensorTest {

  private final File baseDir = new File("src/test/resources");
  private final SensorContextTester context = SensorContextTester.create(baseDir);
  private CheckFactory checkFactory = new CheckFactory(mock(ActiveRules.class));

  private static final String DEFAULT_ENCODING = "ISO-8859-1";
  private static final String UTF8_ENCODING = "UTF-8";

  @Test
  public void should_create_a_valid_sensor_descriptor() {
    DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
    createJavaPropertiesSquidSensor(DEFAULT_ENCODING).describe(descriptor);
    assertThat(descriptor.name()).isEqualTo("Java Properties Squid Sensor");
    assertThat(descriptor.languages()).containsOnly("jproperties");
    assertThat(descriptor.type()).isEqualTo(InputFile.Type.MAIN);
  }

  @Test
  public void should_execute_and_compute_valid_measures_on_ISO_8859_1_file() {
    String relativePath = "myProperties.properties";
    inputFile(relativePath, Charsets.ISO_8859_1);
    createJavaPropertiesSquidSensor(DEFAULT_ENCODING).execute(context);
    assertMeasure("moduleKey:" + relativePath);
  }

  @Test
  public void should_execute_and_compute_valid_measures_on_UTF8_with_BOM_file() {
    String relativePath = "myPropertiesUTF8WithBOM.properties";
    inputFile(relativePath, Charsets.UTF_8);
    createJavaPropertiesSquidSensor(UTF8_ENCODING).execute(context);
    assertMeasure("moduleKey:" + relativePath);
  }

  private void assertMeasure(String key) {
    assertThat(context.measure(key, CoreMetrics.NCLOC).value()).isEqualTo(7);
    assertThat(context.measure(key, CoreMetrics.STATEMENTS).value()).isEqualTo(7);
    assertThat(context.measure(key, CoreMetrics.COMMENT_LINES).value()).isEqualTo(3);
  }

  @Test
  public void should_execute_and_save_issues_on_UTF8_with_BOM_file() {
    inputFile("myPropertiesUTF8WithBOM.properties", Charsets.UTF_8);

    ActiveRules activeRules = (new ActiveRulesBuilder())
      .create(RuleKey.of(GenericJavaPropertiesRulesDefinition.GENERIC_REPOSITORY_KEY, CommentConventionCheck.class.getAnnotation(Rule.class).key()))
      .activate()
      .create(RuleKey.of(GenericJavaPropertiesRulesDefinition.GENERIC_REPOSITORY_KEY, MissingNewlineAtEndOfFileCheck.class.getAnnotation(Rule.class).key()))
      .activate()
      .build();
    checkFactory = new CheckFactory(activeRules);

    createJavaPropertiesSquidSensor(UTF8_ENCODING).execute(context);

    assertThat(context.allIssues()).hasSize(3);
  }

  @Test
  public void should_execute_and_save_issues_on_ISO_8859_1_file() {
    inputFile("myProperties.properties", Charsets.ISO_8859_1);

    ActiveRules activeRules = (new ActiveRulesBuilder())
      .create(RuleKey.of(GenericJavaPropertiesRulesDefinition.GENERIC_REPOSITORY_KEY, CommentConventionCheck.class.getAnnotation(Rule.class).key()))
      .activate()
      .create(RuleKey.of(GenericJavaPropertiesRulesDefinition.GENERIC_REPOSITORY_KEY, MissingNewlineAtEndOfFileCheck.class.getAnnotation(Rule.class).key()))
      .activate()
      .create(RuleKey.of(GenericJavaPropertiesRulesDefinition.GENERIC_REPOSITORY_KEY, LineLengthCheck.class.getAnnotation(Rule.class).key()))
      .activate()
      .build();
    checkFactory = new CheckFactory(activeRules);

    createJavaPropertiesSquidSensor(DEFAULT_ENCODING).execute(context);

    assertThat(context.allIssues()).hasSize(4);
  }

  @Test
  public void should_raise_an_issue_because_the_parsing_error_rule_is_activated() {
    String relativePath = "parsingError.properties";
    inputFile(relativePath, Charsets.ISO_8859_1);

    ActiveRules activeRules = (new ActiveRulesBuilder())
      .create(RuleKey.of(GenericJavaPropertiesRulesDefinition.GENERIC_REPOSITORY_KEY, "S2260"))
      .activate()
      .build();

    checkFactory = new CheckFactory(activeRules);

    context.setActiveRules(activeRules);
    createJavaPropertiesSquidSensor(UTF8_ENCODING).execute(context);
    Collection<Issue> issues = context.allIssues();
    assertThat(issues).hasSize(1);
    Issue issue = issues.iterator().next();
    assertThat(issue.primaryLocation().textRange().start().line()).isEqualTo(2);
  }

  @Test
  public void should_not_raise_any_issue_because_the_parsing_error_rule_is_not_activated() {
    String relativePath = "parsingError.properties";
    inputFile(relativePath, Charsets.ISO_8859_1);

    ActiveRules activeRules = new ActiveRulesBuilder().build();
    checkFactory = new CheckFactory(activeRules);

    context.setActiveRules(activeRules);
    createJavaPropertiesSquidSensor(UTF8_ENCODING).execute(context);
    Collection<Issue> issues = context.allIssues();
    assertThat(issues).hasSize(0);
  }

  private JavaPropertiesSquidSensor createJavaPropertiesSquidSensor(String charset) {
    Settings settings = new Settings();
    settings.setProperty(JavaPropertiesPlugin.JAVA_PROPERTIES_SOURCE_ENCODING_KEY, charset);
    return new JavaPropertiesSquidSensor(context.fileSystem(), checkFactory, settings);
  }

  private void inputFile(String relativePath, Charset charset) {
    DefaultInputFile inputFile = new DefaultInputFile("moduleKey", relativePath)
      .setModuleBaseDir(baseDir.toPath())
      .setType(InputFile.Type.MAIN)
      .setLanguage(JavaPropertiesLanguage.KEY);

    context.fileSystem().setEncoding(charset);
    context.fileSystem().add(inputFile);

    inputFile.initMetadata(new FileMetadata().readMetadata(inputFile.file(), charset));
  }

}
