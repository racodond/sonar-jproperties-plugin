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

import com.sonar.sslr.api.typed.GrammarBuilder;
import org.sonar.jproperties.tree.impl.InternalSyntaxToken;
import org.sonar.plugins.jproperties.api.tree.*;

public class JavaPropertiesGrammar {

  private final GrammarBuilder<InternalSyntaxToken> b;
  private final TreeFactory f;

  public JavaPropertiesGrammar(GrammarBuilder<InternalSyntaxToken> b, TreeFactory f) {
    this.b = b;
    this.f = f;
  }

  public PropertiesTree PROPERTIES() {
    return b.<PropertiesTree>nonterminal(JavaPropertiesLexicalGrammar.PROPERTIES).is(
      f.properties(
        b.optional(b.token(JavaPropertiesLexicalGrammar.BOM)),
        b.zeroOrMore(PROPERTY()),
        b.token(JavaPropertiesLexicalGrammar.EOF)));
  }

  public PropertyTree PROPERTY() {
    return b.<PropertyTree>nonterminal(JavaPropertiesLexicalGrammar.PROPERTY).is(
      f.property(
        KEY(),
        SEPARATOR(),
        b.optional(VALUE())));
  }

  public KeyTree KEY() {
    return b.<KeyTree>nonterminal(JavaPropertiesLexicalGrammar.KEY).is(
      f.key(b.token(JavaPropertiesLexicalGrammar.KEY_LITERAL)));
  }

  public SeparatorTree SEPARATOR() {
    return b.<SeparatorTree>nonterminal(JavaPropertiesLexicalGrammar.SEPARATOR).is(
      f.separator(
        b.firstOf(
          b.token(JavaPropertiesLexicalGrammar.EQUALS_SEPARATOR),
          b.token(JavaPropertiesLexicalGrammar.COLON_SEPARATOR))));
  }

  public ValueTree VALUE() {
    return b.<ValueTree>nonterminal(JavaPropertiesLexicalGrammar.VALUE).is(
      f.value(b.token(JavaPropertiesLexicalGrammar.VALUE_LITERAL)));
  }

}
