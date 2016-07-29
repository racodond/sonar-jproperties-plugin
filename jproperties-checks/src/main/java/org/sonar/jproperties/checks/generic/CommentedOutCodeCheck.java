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
package org.sonar.jproperties.checks.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.checks.Tags;
import org.sonar.plugins.jproperties.api.tree.PropertiesTree;
import org.sonar.plugins.jproperties.api.tree.SyntaxTrivia;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "commented-out-code",
  name = "Sections of code should not be commented out",
  priority = Priority.MAJOR,
  tags = {Tags.UNUSED})
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class CommentedOutCodeCheck extends DoubleDispatchVisitorCheck {

  private static final Pattern commentedOutCodePattern = Pattern
    .compile("^(#|!){1}[ \\t\\x0B\\f]*(?!(?i)todo)(?!(?i)fixme)([^=:\\s]|(?<=\\\\)\\ |(?<=\\\\)\\=|(?<=\\\\)\\:)+[ \\t\\x0B\\f]*(:|=){1}.*$");
  private final List<SyntaxTrivia> commentedOutCode = new ArrayList<>();

  @Override
  public void visitProperties(PropertiesTree tree) {
    commentedOutCode.clear();
    super.visitProperties(tree);
    int lastLineIssue = Integer.MIN_VALUE;
    for (SyntaxTrivia trivia : commentedOutCode) {
      if (trivia.line() != lastLineIssue + 1) {
        addPreciseIssue(trivia, "Remove this commented out code.");
      }
      lastLineIssue = trivia.line();
    }
  }

  @Override
  public void visitComment(SyntaxTrivia trivia) {
    if (commentedOutCodePattern.matcher(trivia.text()).matches()) {
      commentedOutCode.add(trivia);
    }
  }

}
