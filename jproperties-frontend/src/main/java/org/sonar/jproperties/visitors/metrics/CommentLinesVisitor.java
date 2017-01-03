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

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.sonar.plugins.jproperties.api.tree.SyntaxToken;
import org.sonar.plugins.jproperties.api.tree.SyntaxTrivia;
import org.sonar.plugins.jproperties.api.tree.Tree;
import org.sonar.plugins.jproperties.api.visitors.SubscriptionVisitor;

public class CommentLinesVisitor extends SubscriptionVisitor {

  private int commentLines;
  private JavaPropertiesCommentAnalyser commentAnalyser = new JavaPropertiesCommentAnalyser();

  public CommentLinesVisitor(Tree tree) {
    commentLines = 0;
    scanTree(tree);
  }

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return ImmutableList.of(Tree.Kind.TOKEN);
  }

  @Override
  public void visitNode(Tree tree) {
    for (SyntaxTrivia trivia : ((SyntaxToken) tree).trivias()) {
      if (!commentAnalyser.isBlank(commentAnalyser.getContents(trivia.text()))) {
        commentLines++;
      }
    }
  }

  public int getNumberOfCommentLines() {
    return commentLines;
  }

}
