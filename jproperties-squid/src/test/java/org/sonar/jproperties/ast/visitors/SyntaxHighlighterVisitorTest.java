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
import java.nio.charset.Charset;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.jproperties.JavaPropertiesAstScanner;
import org.sonar.jproperties.JavaPropertiesConfiguration;
import org.sonar.squidbridge.SquidAstVisitorContext;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sonar.api.batch.sensor.highlighting.TypeOfText.*;

public class SyntaxHighlighterVisitorTest {

  private SensorContextTester sensorContext;
  private File file;
  private DefaultInputFile inputFile;
  private Charset charset;

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void parse_error() throws Exception {
    setUp(Charsets.ISO_8859_1);
    highlight("ParseError");
    for (int i = 0; i < 10; i++) {
      assertThat(sensorContext.highlightingTypeAt("moduleKey:" + file.getName(), 1, i)).isEmpty();
    }
  }

  @Test
  public void empty_input() throws Exception {
    setUp(Charsets.ISO_8859_1);
    highlight("");
    assertThat(sensorContext.highlightingTypeAt("moduleKey:" + file.getName(), 1, 0)).isEmpty();
  }

  @Test
  public void key() throws Exception {
    setUp(Charsets.ISO_8859_1);
    highlight("abc: abc...abc\n def=def...def");
    assertHighlighting(1, 0, 3, KEYWORD);
    assertHighlighting(2, 1, 3, KEYWORD);
  }

  @Test
  public void value() throws Exception {
    setUp(Charsets.ISO_8859_1);
    highlight("abc: abc...abc\n def=def...def");
    assertHighlighting(1, 5, 9, PREPROCESS_DIRECTIVE);
    assertHighlighting(2, 5, 9, PREPROCESS_DIRECTIVE);
  }

  @Test
  public void comment_hash() throws Exception {
    setUp(Charsets.ISO_8859_1);
    highlight("# blabla");
    assertHighlighting(1, 0, 8, COMMENT);
  }

  @Test
  public void comment_hash2() throws Exception {
    setUp(Charsets.ISO_8859_1);
    highlight("abc: abc...abc\n # blabla");
    assertHighlighting(2, 1, 8, COMMENT);
  }

  @Test
  public void comment_exclamation_mark() throws Exception {
    setUp(Charsets.ISO_8859_1);
    highlight("! blabla");
    assertHighlighting(1, 0, 8, COMMENT);
  }

  @Test
  public void comment_exclamation_mark2() throws Exception {
    setUp(Charsets.ISO_8859_1);
    highlight("abc: abc...abc\n ! blabla");
    assertHighlighting(2, 1, 8, COMMENT);
  }

  @Test
  public void byte_order_mark() throws Exception {
    setUp(Charsets.UTF_8);
    highlight("\ufeffabc: abc...abc");
    assertHighlighting(1, 0, 3, KEYWORD);
  }

  private void highlight(String string) throws Exception {
    inputFile.initMetadata(string);
    Files.write(string, file, charset);
    JavaPropertiesAstScanner.scanSingleFileWithCustomConfiguration(file, sensorContext, new JavaPropertiesConfiguration(charset));
  }

  private void setUp(Charset charset) throws IOException {
    this.charset = charset;
    DefaultFileSystem fileSystem = new DefaultFileSystem(tempFolder.getRoot());
    fileSystem.setEncoding(charset);
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

  private void assertHighlighting(int line, int column, int length, TypeOfText type) {
    for (int i = column; i < column + length; i++) {
      List<TypeOfText> typeOfTexts = sensorContext.highlightingTypeAt("moduleKey:" + file.getName(), line, i);
      assertThat(typeOfTexts).hasSize(1);
      assertThat(typeOfTexts.get(0)).isEqualTo(type);
    }
  }

}
