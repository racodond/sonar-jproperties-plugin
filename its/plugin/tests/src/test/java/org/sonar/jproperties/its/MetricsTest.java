/*
 * SonarQube Java Properties Analyzer
 * Copyright (C) 2015-2017 David RACODON
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
package org.sonar.jproperties.its;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.sonar.jproperties.its.Tests.getMeasureAsDouble;

public class MetricsTest {

  @ClassRule
  public static Orchestrator orchestrator = Tests.ORCHESTRATOR;

  private static final String PROJECT_KEY = "metrics";

  @BeforeClass
  public static void init() {
    orchestrator.resetData();

    SonarScanner build = Tests.createSonarScannerBuild()
      .setProjectDir(new File("../projects/metrics/"))
      .setProjectKey(PROJECT_KEY)
      .setProjectName(PROJECT_KEY);

    orchestrator.getServer().provisionProject(PROJECT_KEY, PROJECT_KEY);
    Tests.setProfile("empty-profile", PROJECT_KEY);
    orchestrator.executeBuild(build);
  }

  @Test
  public void project_measures() {
    assertThat(getProjectMeasure("lines")).isEqualTo(25);
    assertThat(getProjectMeasure("ncloc")).isEqualTo(8);
    assertThat(getProjectMeasure("classes")).isNull();
    assertThat(getProjectMeasure("functions")).isNull();
    assertThat(getProjectMeasure("statements")).isEqualTo(8);
    assertThat(getProjectMeasure("files")).isEqualTo(2);
    assertThat(getProjectMeasure("directories")).isEqualTo(2);

    assertThat(getProjectMeasure("comment_lines")).isEqualTo(6);
    assertThat(getProjectMeasure("comment_lines_density")).isEqualTo(42.9);

    assertThat(getProjectMeasure("complexity")).isNull();
    assertThat(getProjectMeasure("complexity_in_functions")).isNull();
    assertThat(getProjectMeasure("function_complexity")).isNull();
    assertThat(getProjectMeasure("function_complexity_distribution")).isNull();
    assertThat(getProjectMeasure("file_complexity")).isNull();

    assertThat(getProjectMeasure("duplicated_lines")).isEqualTo(0);
    assertThat(getProjectMeasure("duplicated_files")).isEqualTo(0);
    assertThat(getProjectMeasure("duplicated_blocks")).isEqualTo(0);
    assertThat(getProjectMeasure("duplicated_lines_density")).isEqualTo(0.0);

    assertThat(getProjectMeasure("violations")).isEqualTo(0);
  }

  @Test
  public void dir_measures() {
    assertThat(getDirMeasure("lines")).isEqualTo(9);
    assertThat(getDirMeasure("ncloc")).isEqualTo(4);
    assertThat(getDirMeasure("classes")).isNull();
    assertThat(getDirMeasure("functions")).isNull();
    assertThat(getDirMeasure("statements")).isEqualTo(4);
    assertThat(getDirMeasure("files")).isEqualTo(1);
    assertThat(getDirMeasure("directories")).isEqualTo(1);

    assertThat(getDirMeasure("comment_lines")).isEqualTo(2);
    assertThat(getDirMeasure("comment_lines_density")).isEqualTo(33.3);

    assertThat(getDirMeasure("complexity")).isNull();
    assertThat(getDirMeasure("complexity_in_functions")).isNull();
    assertThat(getDirMeasure("function_complexity")).isNull();
    assertThat(getDirMeasure("function_complexity_distribution")).isNull();
    assertThat(getDirMeasure("file_complexity")).isNull();

    assertThat(getDirMeasure("duplicated_lines")).isEqualTo(0);
    assertThat(getDirMeasure("duplicated_files")).isEqualTo(0);
    assertThat(getDirMeasure("duplicated_blocks")).isEqualTo(0);
    assertThat(getDirMeasure("duplicated_lines_density")).isEqualTo(0.0);

    assertThat(getDirMeasure("violations")).isEqualTo(0);
  }

  @Test
  public void file1_measures() {
    assertThat(getFile1Measure("lines")).isEqualTo(16);
    assertThat(getFile1Measure("ncloc")).isEqualTo(4);
    assertThat(getFile1Measure("classes")).isNull();
    assertThat(getFile1Measure("functions")).isNull();
    assertThat(getFile1Measure("statements")).isEqualTo(4);

    assertThat(getFile1Measure("comment_lines")).isEqualTo(4);
    assertThat(getFile1Measure("comment_lines_density")).isEqualTo(50.0);

    assertThat(getFile1Measure("complexity")).isNull();
    assertThat(getFile1Measure("complexity_in_functions")).isNull();
    assertThat(getFile1Measure("function_complexity")).isNull();
    assertThat(getFile1Measure("function_complexity_distribution")).isNull();
    assertThat(getFile1Measure("file_complexity")).isNull();

    assertThat(getFile1Measure("duplicated_lines")).isEqualTo(0);
    assertThat(getFile1Measure("duplicated_blocks")).isEqualTo(0);
    assertThat(getFile1Measure("duplicated_lines_density")).isEqualTo(0);

    assertThat(getFile1Measure("violations")).isEqualTo(0);
  }

  @Test
  public void file2_measures() {
    assertThat(getFile2Measure("lines")).isEqualTo(9);
    assertThat(getFile2Measure("ncloc")).isEqualTo(4);
    assertThat(getFile2Measure("classes")).isNull();
    assertThat(getFile2Measure("functions")).isNull();
    assertThat(getFile2Measure("statements")).isEqualTo(4);

    assertThat(getFile2Measure("comment_lines")).isEqualTo(2);
    assertThat(getFile2Measure("comment_lines_density")).isEqualTo(33.3);

    assertThat(getFile2Measure("complexity")).isNull();
    assertThat(getFile2Measure("complexity_in_functions")).isNull();
    assertThat(getFile2Measure("function_complexity")).isNull();
    assertThat(getFile2Measure("function_complexity_distribution")).isNull();
    assertThat(getFile2Measure("file_complexity")).isNull();

    assertThat(getFile2Measure("duplicated_lines")).isEqualTo(0);
    assertThat(getFile2Measure("duplicated_blocks")).isEqualTo(0);
    assertThat(getFile2Measure("duplicated_lines_density")).isEqualTo(0);

    assertThat(getFile2Measure("violations")).isEqualTo(0);
  }

  private Double getProjectMeasure(String metricKey) {
    return getMeasureAsDouble(PROJECT_KEY, metricKey);
  }

  private Double getDirMeasure(String metricKey) {
    return getMeasureAsDouble(PROJECT_KEY + ":src/dir", metricKey);
  }

  private Double getFile1Measure(String metricKey) {
    return getMeasureAsDouble(PROJECT_KEY + ":src/file1.properties", metricKey);
  }

  private Double getFile2Measure(String metricKey) {
    return getMeasureAsDouble(PROJECT_KEY + ":src/dir/file2.properties", metricKey);
  }

}
