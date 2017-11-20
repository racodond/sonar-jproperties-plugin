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

import com.sonar.sslr.api.GenericTokenType;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

public enum JavaPropertiesLexicalGrammar implements GrammarRuleKey {

  PROPERTIES,
  PROPERTY,
  KEY,
  VALUE,
  KEY_LITERAL,
  VALUE_LITERAL,
  SEPARATOR,
  COLON_SEPARATOR,
  EQUALS_SEPARATOR,
  SPACING,
  WHITESPACES_WITHOUT_LINE_BREAK,
  BOM,
  EOF;

  public static LexerlessGrammarBuilder createGrammar() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    syntax(b);
    b.setRootRule(PROPERTIES);
    return b;
  }

  private static void syntax(LexerlessGrammarBuilder b) {
    b.rule(EOF).is(SPACING, b.token(GenericTokenType.EOF, b.endOfInput()));
    b.rule(BOM).is("\ufeff");

    b.rule(WHITESPACES_WITHOUT_LINE_BREAK).is(b.skippedTrivia(b.regexp("(?<!\\\\)[ \\t\\x0B\\f]*+")));
    b.rule(SPACING).is(
      b.skippedTrivia(b.regexp("(?<!\\\\)[\\s]*+")),
      b.zeroOrMore(
        b.commentTrivia(b.regexp("(#|!)+.*")),
        b.skippedTrivia(b.regexp("(?<!\\\\)[\\s]*+"))));

    b.rule(KEY_LITERAL).is(SPACING, b.regexp("([^=:\\s]|(?<=\\\\)\\ |(?<=\\\\)\\=|(?<=\\\\)\\:)+"));
    b.rule(COLON_SEPARATOR).is(WHITESPACES_WITHOUT_LINE_BREAK, b.nextNot("\\"), ":");
    b.rule(EQUALS_SEPARATOR).is(WHITESPACES_WITHOUT_LINE_BREAK, b.nextNot("\\"), "=");
    b.rule(VALUE_LITERAL).is(WHITESPACES_WITHOUT_LINE_BREAK, b.regexp("[^\\n\\r]+((?<=\\\\)\\r?\\n[^\\n\\r]*)*"));
  }

}
