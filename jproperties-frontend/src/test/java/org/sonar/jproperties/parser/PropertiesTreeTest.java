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
package org.sonar.jproperties.parser;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;

import org.junit.Test;
import org.sonar.jproperties.parser.JavaPropertiesLexicalGrammar;
import org.sonar.jproperties.parser.JavaPropertiesParserBuilder;
import org.sonar.plugins.jproperties.api.tree.PropertiesTree;

import static org.fest.assertions.Assertions.assertThat;

public class PropertiesTreeTest {

  @Test
  public void bom() throws Exception {
    PropertiesTree propertiesTree;

    propertiesTree = parse("\uFEFF");
    assertThat(propertiesTree.hasByteOrderMark()).isTrue();

    propertiesTree = parse("\uFEFFabc:def");
    assertThat(propertiesTree.hasByteOrderMark()).isTrue();

    propertiesTree = parse("abc:def");
    assertThat(propertiesTree.hasByteOrderMark()).isFalse();
  }

  @Test
  public void multiple_properties() throws Exception {
    PropertiesTree propertiesTree;

    propertiesTree = parse("");
    assertThat(propertiesTree.properties().size()).isEqualTo(0);

    propertiesTree = parse(" ");
    assertThat(propertiesTree.properties().size()).isEqualTo(0);

    propertiesTree = parse("abc:def\ndef=dfdsf");
    assertThat(propertiesTree.properties().size()).isEqualTo(2);

    propertiesTree = parse(" abc:def\ndef=dfdsf\n aaa : bbb ");
    assertThat(propertiesTree.properties().size()).isEqualTo(3);

    propertiesTree = parse(" abc=\ndef=\n ghi:");
    assertThat(propertiesTree.properties().size()).isEqualTo(3);
  }

  @Test
  public void comments() throws Exception {
    parse("!blabla");
    parse("! blabla");
    parse("!  blabla");
    parse("#blabla");
    parse("# blabla");
    parse("#  blabla");
    parse("!blabla");
    parse(" ! blabla");
    parse("  !  blabla");
    parse("   #blabla");
    parse(" # blabla");
    parse("  #  blabla");
    parse("   # blabla\n! blabla");
    parse(" # blabla \n ! blabla\n");
  }

  @Test
  public void complex_file() throws Exception {
    PropertiesTree propertiesTree = parse(Files.toString(new File("src/test/resources/parser/complex.properties"), Charsets.ISO_8859_1));
    assertThat(propertiesTree.properties().size()).isEqualTo(13);
  }

  private PropertiesTree parse(String toParse) {
    return (PropertiesTree) JavaPropertiesParserBuilder
      .createTestParser(Charsets.ISO_8859_1, JavaPropertiesLexicalGrammar.PROPERTIES)
      .parse(toParse);
  }

}
