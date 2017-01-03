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
package org.sonar.jproperties.visitors.metrics;

import com.google.common.base.Charsets;

import java.io.File;

import org.junit.Test;
import org.sonar.jproperties.parser.JavaPropertiesLexicalGrammar;
import org.sonar.jproperties.parser.JavaPropertiesParserBuilder;
import org.sonar.plugins.jproperties.api.tree.Tree;

import static org.fest.assertions.Assertions.assertThat;

public class MetricsTest {

  @Test
  public void metrics_ISO_8859_1_file() {
    String path = "src/test/resources/metrics/metrics.properties";
    Tree tree = JavaPropertiesParserBuilder.createTestParser(Charsets.ISO_8859_1, JavaPropertiesLexicalGrammar.PROPERTIES).parse(new File(path));
    assertMetrics(tree);
  }

  @Test
  public void metrics_UTF8_file_with_BOM() {
    String path = "src/test/resources/metrics/metricsUtf8WithBom.properties";
    Tree tree = JavaPropertiesParserBuilder.createTestParser(Charsets.UTF_8, JavaPropertiesLexicalGrammar.PROPERTIES).parse(new File(path));
    assertMetrics(tree);
  }

  private void assertMetrics(Tree tree) {
    assertThat(new LinesOfCodeVisitor(tree).getNumberOfLinesOfCode()).isEqualTo(4);
    assertThat(new StatementsVisitor(tree).getNumberOfStatements()).isEqualTo(4);
    assertThat(new CommentLinesVisitor(tree).getNumberOfCommentLines()).isEqualTo(4);
  }

}
