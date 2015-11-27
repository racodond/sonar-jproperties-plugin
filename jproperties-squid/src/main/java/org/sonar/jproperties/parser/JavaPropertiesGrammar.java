/*
 * SonarQube Java Properties Plugin
 * Copyright (C) 2015 David RACODON
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.jproperties.parser;

import com.sonar.sslr.api.GenericTokenType;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;
import org.sonar.sslr.parser.LexerlessGrammar;

public enum JavaPropertiesGrammar implements GrammarRuleKey {

  PROPERTIES,
  PROPERTY,
  KEY,
  ELEMENT,
  SEPARATOR,
  COLON_SEPARATOR,
  EQUALS_SEPARATOR,
  WHITESPACES_WITHOUT_LINE_BREAK,
  WHITESPACES_AND_COMMENTS,
  EOF;

  public static LexerlessGrammar createGrammar() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    syntax(b);
    b.setRootRule(PROPERTIES);
    return b.build();
  }

  private static void syntax(LexerlessGrammarBuilder b) {
    b.rule(PROPERTIES).is(WHITESPACES_AND_COMMENTS, b.zeroOrMore(PROPERTY), WHITESPACES_AND_COMMENTS, EOF);
    b.rule(PROPERTY).is(WHITESPACES_AND_COMMENTS, KEY, SEPARATOR, b.optional(ELEMENT));
    b.rule(SEPARATOR).is(b.firstOf(COLON_SEPARATOR, EQUALS_SEPARATOR));
    b.rule(EOF).is(b.token(GenericTokenType.EOF, b.endOfInput())).skip();
    b.rule(WHITESPACES_WITHOUT_LINE_BREAK).is(b.skippedTrivia(b.regexp("(?<!\\\\)[ \\t\\x0B\\f]+"))).skip();
    b.rule(WHITESPACES_AND_COMMENTS).is(b.zeroOrMore(
      b.firstOf(
        b.skippedTrivia(b.regexp("(?<!\\\\)[\\s]+")),
        b.commentTrivia(b.regexp("#[^\\n\\r]*+|(?<!\\\\)![^\\n\\r]*+"))))).skip();
    b.rule(COLON_SEPARATOR).is(b.optional(WHITESPACES_WITHOUT_LINE_BREAK), b.nextNot("\\"), ":");
    b.rule(EQUALS_SEPARATOR).is(b.optional(WHITESPACES_WITHOUT_LINE_BREAK), b.nextNot("\\"), "=");
    b.rule(KEY).is(b.optional(WHITESPACES_AND_COMMENTS), b.regexp("([^=:\\s]|(?<=\\\\)\\ |(?<=\\\\)\\=|(?<=\\\\)\\:)+"));
    b.rule(ELEMENT).is(b.optional(WHITESPACES_WITHOUT_LINE_BREAK), b.regexp("[^\\n\\r]+((?<=\\\\)\\r?\\n[^\\n\\r]*)*"));
  }

}
