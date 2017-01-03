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
package org.sonar.jproperties.parser;

import com.google.common.base.Charsets;
import org.junit.Test;
import org.sonar.jproperties.parser.JavaPropertiesLexicalGrammar;
import org.sonar.jproperties.parser.JavaPropertiesParserBuilder;
import org.sonar.plugins.jproperties.api.tree.ValueTree;

import static org.fest.assertions.Assertions.assertThat;

public class ValueTreeTest {

  @Test
  public void value() throws Exception {
    checkParsed("abc", "abc");
    checkParsed("abc ", "abc ");
    checkParsed("abc  ", "abc  ");
    checkParsed("a=bc", "a=bc");
    checkParsed("a:bc", "a:bc");
    checkParsed("abc\\ndef", "abc\\ndef");
    checkParsed("\\u8ACB", "\\u8ACB");
    checkParsed("ab\\u8ACBcd", "ab\\u8ACBcd");
    checkParsed("\\u8ACB\\u7A0D", "\\u8ACB\\u7A0D");
    checkParsed("abc\\ndef \\n...", "abc\\ndef \\n...");
  }

  private void checkParsed(String toParse, String value) {
    ValueTree tree = (ValueTree) JavaPropertiesParserBuilder
      .createTestParser(Charsets.ISO_8859_1, JavaPropertiesLexicalGrammar.VALUE)
      .parse(toParse);

    assertValueTree(tree, value);
  }

  private void assertValueTree(ValueTree tree, String value) {
    assertThat(tree.value().text()).isEqualTo(value);
  }

}
