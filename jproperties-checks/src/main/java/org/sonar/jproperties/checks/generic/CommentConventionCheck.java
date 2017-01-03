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
package org.sonar.jproperties.checks.generic;

import com.google.common.annotations.VisibleForTesting;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.jproperties.checks.CheckUtils;
import org.sonar.jproperties.checks.Tags;
import org.sonar.plugins.jproperties.api.tree.PropertiesTree;
import org.sonar.plugins.jproperties.api.tree.SyntaxTrivia;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "comment-convention",
  name = "All comments should be formatted consistently",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
@SqaleConstantRemediation("1min")
@ActivatedByDefault
public class CommentConventionCheck extends DoubleDispatchVisitorCheck {

  private static final String DEFAULT_FORMAT = "#";
  private Pattern pattern;

  @RuleProperty(
    key = "startingCommentToken",
    description = "Allowed values: '#', '!'",
    defaultValue = DEFAULT_FORMAT)
  private String startingCommentToken = DEFAULT_FORMAT;

  @Override
  public void visitProperties(PropertiesTree tree) {
    String startingTokenToDisallow = "#".equals(startingCommentToken) ? "!" : "#";
    pattern = Pattern.compile("^\\" + startingTokenToDisallow + ".*");
    super.visitProperties(tree);
  }

  @Override
  public void visitComment(SyntaxTrivia trivia) {
    if (pattern.matcher(trivia.text()).matches()) {
      addPreciseIssue(trivia, "Use starting comment token '" + startingCommentToken + "' instead.");
    }
    if (trivia.text().length() > 1 && !" ".equals(trivia.text().substring(1, 2))) {
      addPreciseIssue(trivia, "Add a whitespace after the starting comment token.");
    }
    super.visitComment(trivia);
  }

  @Override
  public void validateParameters() {
    if (!Arrays.asList("#", "!").contains(startingCommentToken)) {
      throw new IllegalStateException(paramErrorMessage());
    }
  }

  @VisibleForTesting
  void setStartingCommentToken(String startingCommentToken) {
    this.startingCommentToken = startingCommentToken;
  }

  private String paramErrorMessage() {
    return CheckUtils.paramErrorMessage(
      this.getClass(),
      "startingCommentToken parameter is not valid.\nActual: '" + startingCommentToken + "'\n" + "Expected: '#' or '!'");
  }

}
