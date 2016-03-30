/*
 * SonarQube Java Properties Plugin
 * Copyright (C) 2016 David RACODON
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.jproperties;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.collections.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.jproperties.ast.visitors.SonarComponents;
import org.sonar.squidbridge.SquidAstVisitor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class JavaPropertiesSquidSensorTest {

  private JavaPropertiesSquidSensor sensor;
  private FileSystem fs;
  private FileLinesContextFactory fileLinesContextFactory;
  private CheckFactory checkFactory;
  private ResourcePerspectives resourcePerspectives;
  private RulesProfile rulesProfile;

  @Before
  public void setUp() {
    fileLinesContextFactory = mock(FileLinesContextFactory.class);
    FileLinesContext fileLinesContext = mock(FileLinesContext.class);
    when(fileLinesContextFactory.createFor(Mockito.any(InputFile.class))).thenReturn(fileLinesContext);

    fs = mock(FileSystem.class);
    when(fs.predicates()).thenReturn(mock(FilePredicates.class));
    when(fs.files(Mockito.any(FilePredicate.class))).thenReturn(
      Arrays.asList(new File("src/test/resources/myProperties.properties")));
    when(fs.encoding()).thenReturn(Charsets.ISO_8859_1);

    Checks<SquidAstVisitor> checks = mock(Checks.class);
    when(checks.addAnnotatedChecks(Mockito.anyCollection())).thenReturn(checks);
    checkFactory = mock(CheckFactory.class);

    resourcePerspectives = mock(ResourcePerspectives.class);
    rulesProfile = mock(RulesProfile.class);

    when(checkFactory.<SquidAstVisitor>create(Mockito.anyString())).thenReturn(checks);

    sensor = new JavaPropertiesSquidSensor(null, fs, checkFactory, rulesProfile, resourcePerspectives);
  }

  @Test
  public void should_execute_on() {
    Project project = new Project("key");
    FileSystem fs = mock(FileSystem.class);
    when(fs.predicates()).thenReturn(mock(FilePredicates.class));
    JavaPropertiesSquidSensor javaPropertiesSensor = new JavaPropertiesSquidSensor(mock(SonarComponents.class), fs, mock(CheckFactory.class), mock(RulesProfile.class),
      mock(ResourcePerspectives.class));

    when(fs.files(Mockito.any(FilePredicate.class))).thenReturn(ListUtils.EMPTY_LIST);
    assertThat(javaPropertiesSensor.shouldExecuteOnProject(project)).isFalse();

    when(fs.files(Mockito.any(FilePredicate.class))).thenReturn(ImmutableList.of(new File("/tmp")));
    assertThat(javaPropertiesSensor.shouldExecuteOnProject(project)).isTrue();
  }

  @Test
  public void should_analyse() {
    Project project = new Project("key");
    SensorContext context = mock(SensorContext.class);

    sensor.analyse(project, context);

    verify(context).saveMeasure(Mockito.any(InputFile.class), Mockito.eq(CoreMetrics.LINES), Mockito.eq(12.0));
    verify(context).saveMeasure(Mockito.any(InputFile.class), Mockito.eq(CoreMetrics.NCLOC), Mockito.eq(6.0));
    verify(context).saveMeasure(Mockito.any(InputFile.class), Mockito.eq(CoreMetrics.STATEMENTS), Mockito.eq(6.0));
    verify(context).saveMeasure(Mockito.any(InputFile.class), Mockito.eq(CoreMetrics.COMMENT_LINES), Mockito.eq(4.0));
  }

}
