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
package org.sonar.jproperties.visitors;

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.jproperties.tree.impl.InternalSyntaxToken;
import org.sonar.plugins.jproperties.api.tree.*;
import org.sonar.plugins.jproperties.api.visitors.SubscriptionVisitor;

public class SyntaxHighlighterVisitor extends SubscriptionVisitor {

  private final SensorContext sensorContext;
  private final FileSystem fileSystem;
  private NewHighlighting highlighting;

  public SyntaxHighlighterVisitor(SensorContext sensorContext) {
    this.sensorContext = sensorContext;
    fileSystem = sensorContext.fileSystem();
  }

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return ImmutableList.<Tree.Kind>builder()
      .add(Tree.Kind.PROPERTIES)
      .add(Tree.Kind.KEY)
      .add(Tree.Kind.VALUE)
      .add(Tree.Kind.TOKEN)
      .build();
  }

  @Override
  public void visitFile(Tree tree) {
    highlighting = sensorContext.newHighlighting().onFile(fileSystem.inputFile(fileSystem.predicates().is(getContext().getFile())));
  }

  @Override
  public void leaveFile(Tree scriptTree) {
    highlighting.save();
  }

  @Override
  public void visitNode(Tree tree) {
    SyntaxToken token = null;
    TypeOfText code = null;

    if (tree.is(Tree.Kind.KEY)) {
      token = ((KeyTree) tree).value();
      code = TypeOfText.KEYWORD;

    } else if (tree.is(Tree.Kind.VALUE)) {
      token = ((ValueTree) tree).value();
      code = TypeOfText.STRING;

    } else if (tree.is(Tree.Kind.TOKEN)) {
      highlightComments((InternalSyntaxToken) tree);
    }

    if (token != null) {
      highlight(token, code);
    }
  }

  private void highlightComments(InternalSyntaxToken token) {
    for (SyntaxTrivia trivia : token.trivias()) {
      highlight(trivia, TypeOfText.COMMENT);
    }
  }

  private void highlight(SyntaxToken token, TypeOfText type) {
    highlighting.highlight(token.line(), token.column(), token.endLine(), token.endColumn(), type);
  }

}
