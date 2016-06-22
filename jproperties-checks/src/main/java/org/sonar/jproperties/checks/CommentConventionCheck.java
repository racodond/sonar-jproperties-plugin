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
package org.sonar.jproperties.checks;

import com.google.common.annotations.VisibleForTesting;
import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;

import java.util.Iterator;
import java.util.regex.Pattern;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "comment-convention",
  name = "All comments should be formatted consistently",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("1min")
public class CommentConventionCheck extends JavaPropertiesCheck implements AstAndTokenVisitor {

  private static final String DEFAULT_FORMAT = "#";
  private Pattern pattern;

  @RuleProperty(
    key = "startingCommentToken",
    description = "Allowed values: '#', '!'",
    defaultValue = "" + DEFAULT_FORMAT)
  private String startingCommentToken = DEFAULT_FORMAT;

  @Override
  public void init() {
    validateStartingCommentTokenParameter();
    String startingTokenToDisallow = "#".equals(startingCommentToken) ? "!" : "#";
    pattern = Pattern.compile("^\\" + startingTokenToDisallow + ".*");
  }

  @Override
  public void visitToken(Token token) {
    Iterator iterator = token.getTrivia().iterator();
    while (iterator.hasNext()) {
      Trivia trivia = (Trivia) iterator.next();
      if (trivia.isComment() && pattern.matcher(trivia.getToken().getOriginalValue()).matches()) {
        addIssue(trivia.getToken().getLine(), "Use starting comment token '" + startingCommentToken + "' instead.");
      }
    }
  }

  @VisibleForTesting
  public void setStartingCommentToken(String startingCommentToken) {
    this.startingCommentToken = startingCommentToken;
  }

  private void validateStartingCommentTokenParameter() {
    if (!"#".equals(startingCommentToken) && !"!".equals(startingCommentToken)) {
      throw new IllegalStateException("Check jproperties:comment-convention: startingCommentToken parameter is not valid.\n" +
        "Actual: '" + startingCommentToken + "'\n" + "Expected: '#' or '!'");
    }
  }

}
