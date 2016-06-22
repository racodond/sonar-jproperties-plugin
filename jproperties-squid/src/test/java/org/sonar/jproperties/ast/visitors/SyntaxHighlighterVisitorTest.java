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
package org.sonar.jproperties.ast.visitors;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.jproperties.JavaPropertiesAstScanner;
import org.sonar.squidbridge.SquidAstVisitorContext;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sonar.api.batch.sensor.highlighting.TypeOfText.*;

public class SyntaxHighlighterVisitorTest {

  private SensorContextTester sensorContext;
  private File file;
  private DefaultInputFile inputFile;

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  @Before
  public void setUp() throws IOException {
    DefaultFileSystem fileSystem = new DefaultFileSystem(tempFolder.getRoot());
    fileSystem.setEncoding(Charsets.UTF_8);
    file = tempFolder.newFile();
    inputFile = new DefaultInputFile("moduleKey", file.getName())
      .setLanguage("jproperties")
      .setType(InputFile.Type.MAIN);
    fileSystem.add(inputFile);

    sensorContext = SensorContextTester.create(tempFolder.getRoot());
    sensorContext.setFileSystem(fileSystem);

    SquidAstVisitorContext visitorContext = mock(SquidAstVisitorContext.class);
    when(visitorContext.getFile()).thenReturn(file);
  }

  @Test
  public void parse_error() throws Exception {
    highlight("ParseError");
    for (int i = 0; i < 10; i++) {
      assertThat(sensorContext.highlightingTypeAt("moduleKey:" + file.getName(), 1, i)).isEmpty();
    }
  }

  @Test
  public void empty_input() throws Exception {
    highlight("");
    assertThat(sensorContext.highlightingTypeAt("moduleKey:" + file.getName(), 1, 0)).isEmpty();
  }

  @Test
  public void key() throws Exception {
    highlight("abc: abc...abc\n def=def...def");
    assertHighlighting(1, 0, 3, KEYWORD);
    assertHighlighting(2, 1, 3, KEYWORD);
  }

  @Test
  public void value() throws Exception {
    highlight("abc: abc...abc\n def=def...def");
    assertHighlighting(1, 5, 9, PREPROCESS_DIRECTIVE);
    assertHighlighting(2, 5, 9, PREPROCESS_DIRECTIVE);
  }

  @Test
  public void comment_hash() throws Exception {
    highlight("# blabla");
    assertHighlighting(1, 0, 8, COMMENT);
  }

  @Test
  public void comment_hash2() throws Exception {
    highlight("abc: abc...abc\n # blabla");
    assertHighlighting(2, 1, 8, COMMENT);
  }

  @Test
  public void comment_exclamation_mark() throws Exception {
    highlight("! blabla");
    assertHighlighting(1, 0, 8, COMMENT);
  }

  @Test
  public void comment_exclamation_mark2() throws Exception {
    highlight("abc: abc...abc\n ! blabla");
    assertHighlighting(2, 1, 8, COMMENT);
  }

  private void highlight(String string) throws Exception {
    inputFile.initMetadata(string);
    Files.write(string, file, Charsets.UTF_8);
    JavaPropertiesAstScanner.scanSingleFile(file, sensorContext);
  }

  private void assertHighlighting(int line, int column, int length, TypeOfText type) {
    for (int i = column; i < column + length; i++) {
      List<TypeOfText> typeOfTexts = sensorContext.highlightingTypeAt("moduleKey:" + file.getName(), line, i);
      assertThat(typeOfTexts).hasSize(1);
      assertThat(typeOfTexts.get(0)).isEqualTo(type);
    }
  }

}
