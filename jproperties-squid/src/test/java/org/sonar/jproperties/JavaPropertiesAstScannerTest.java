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
package org.sonar.jproperties;

import java.io.File;

import org.junit.Test;
import org.sonar.jproperties.api.JavaPropertiesMetric;
import org.sonar.squidbridge.api.SourceFile;

import static org.fest.assertions.Assertions.assertThat;

public class JavaPropertiesAstScannerTest {

  private final String path = "src/test/resources/myProperties.properties";

  @Test
  public void lines() {
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File(path));
    assertThat(file.getInt(JavaPropertiesMetric.LINES)).isEqualTo(12);
  }

  @Test
  public void lines_of_code() {
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File(path));
    assertThat(file.getInt(JavaPropertiesMetric.LINES_OF_CODE)).isEqualTo(6);
  }

  @Test
  public void statements() {
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File(path));
    assertThat(file.getInt(JavaPropertiesMetric.STATEMENTS)).isEqualTo(6);
  }

  @Test
  public void comments() {
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File(path));
    assertThat(file.getInt(JavaPropertiesMetric.COMMENT_LINES)).isEqualTo(4);
  }

  @Test(expected = IllegalArgumentException.class)
  public void file_not_found() {
    JavaPropertiesAstScanner.scanSingleFile(new File("blabla"));
  }

}
