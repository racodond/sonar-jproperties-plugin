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
import org.sonar.plugins.jproperties.api.tree.KeyTree;

import static org.fest.assertions.Assertions.assertThat;

public class KeyTreeTest {

  @Test
  public void key() throws Exception {
    checkParsed("abc", "abc");
    checkParsed("abc ", "abc");
    checkParsed("abc  ", "abc");
    checkParsed("a\\=bc  ", "a\\=bc");
    checkParsed("a\\:bc  ", "a\\:bc");
    checkParsed("a\\b", "a\\b");
    checkParsed("abc\\=\\ \\:def", "abc\\=\\ \\:def");
  }

  @Test
  public void notKey() throws Exception {
    checkNotParsed("a=bc");
    checkNotParsed("a:bc");
  }

  private KeyTree parse(String toParse) {
    return (KeyTree) JavaPropertiesParserBuilder
      .createTestParser(Charsets.ISO_8859_1, JavaPropertiesLexicalGrammar.KEY)
      .parse(toParse);
  }

  private void checkParsed(String toParse, String key) {
    KeyTree tree = parse(toParse);
    assertThat(tree.text()).isEqualTo(key);
  }

  private void checkNotParsed(String toParse) {
    KeyTree tree = parse(toParse);
    assertThat(tree.text()).isNotEqualTo(toParse);
  }

}
