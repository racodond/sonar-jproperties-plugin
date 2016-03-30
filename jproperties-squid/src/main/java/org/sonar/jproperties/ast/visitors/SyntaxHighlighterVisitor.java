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
package org.sonar.jproperties.ast.visitors;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.sonar.sslr.api.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.source.Highlightable;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.sslr.parser.LexerlessGrammar;

public class SyntaxHighlighterVisitor extends SquidAstVisitor<LexerlessGrammar> implements AstAndTokenVisitor {

  private static final Map<AstNodeType, String> TYPES = ImmutableMap.<AstNodeType, String>builder()
    .put(JavaPropertiesGrammar.KEY, "k")
    .put(JavaPropertiesGrammar.ELEMENT, "p")
    .build();

  private final SonarComponents sonarComponents;

  private Highlightable.HighlightingBuilder highlighting;
  private List<Integer> lineStart;

  public SyntaxHighlighterVisitor(SonarComponents sonarComponents) {
    this.sonarComponents = Preconditions.checkNotNull(sonarComponents);
  }

  @Override
  public void init() {
    for (AstNodeType type : TYPES.keySet()) {
      subscribeTo(type);
    }
  }

  @Override
  public void visitFile(AstNode astNode) {
    if (astNode == null) {
      // parse error
      return;
    }

    InputFile inputFile = sonarComponents.inputFileFor(getContext().getFile());
    Preconditions.checkNotNull(inputFile);
    highlighting = sonarComponents.highlightableFor(inputFile).newHighlighting();

    lineStart = Lists.newArrayList();
    final String content;
    try {
      content = Files.toString(getContext().getFile(), Charsets.ISO_8859_1);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
    lineStart.add(0);
    for (int i = 0; i < content.length(); i++) {
      if (content.charAt(i) == '\n' || (content.charAt(i) == '\r' && i + 1 < content.length() && content.charAt(i + 1) != '\n')) {
        lineStart.add(i + 1);
      }
    }
  }

  @Override
  public void visitNode(AstNode astNode) {
    highlighting.highlight(astNode.getFromIndex(), astNode.getToIndex(), TYPES.get(astNode.getType()));
  }

  @Override
  public void visitToken(Token token) {
    for (Trivia trivia : token.getTrivia()) {
      if (trivia.isComment()) {
        Token triviaToken = trivia.getToken();
        int offset = getOffset(triviaToken.getLine(), triviaToken.getColumn());
        highlighting.highlight(offset, offset + triviaToken.getValue().length(), "cppd");
      }
    }
  }

  /**
   * @param line starts from 1
   * @param column starts from 0
   */
  private int getOffset(int line, int column) {
    return lineStart.get(line - 1) + column;
  }

  @Override
  public void leaveFile(AstNode astNode) {
    if (astNode == null) {
      // parse error
      return;
    }

    highlighting.done();
  }

}
