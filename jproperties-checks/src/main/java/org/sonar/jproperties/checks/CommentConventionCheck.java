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
package org.sonar.jproperties.checks;

import com.google.common.annotations.VisibleForTesting;
import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;

import java.util.Iterator;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
  key = "comment-convention",
  name = "All comments should be formatted consistently",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("1min")
public class CommentConventionCheck extends SquidCheck<LexerlessGrammar> implements AstAndTokenVisitor {

  private static final String DEFAULT_FORMAT = "#";
  private static final Logger LOG = LoggerFactory.getLogger(CommentConventionCheck.class);
  private Pattern pattern;
  private boolean runCheck = true;

  @RuleProperty(
    key = "startingCommentToken",
    description = "Allowed values: '#', '!'",
    defaultValue = "" + DEFAULT_FORMAT)
  private String startingCommentToken = DEFAULT_FORMAT;

  @Override
  public void init() {
    if (isStartingCommentTokenParameterValid()) {
      String startingTokenToDisallow = "#".equals(startingCommentToken) ? "!" : "#";
      pattern = Pattern.compile("^\\" + startingTokenToDisallow + ".*");
    } else {
      runCheck = false;
      LOG.error("Rule jproperties:comment-convention: startingCommentToken parameter value is not valid.\nActual: '" + startingCommentToken
        + "'\nExpected: '#' or '!'\nNo check will be performed against this jproperties:comment-convention rule.");
    }
  }

  @Override
  public void visitToken(Token token) {
    if (runCheck) {
      Iterator iterator = token.getTrivia().iterator();
      while (iterator.hasNext()) {
        Trivia trivia = (Trivia) iterator.next();
        if (trivia.isComment() && pattern.matcher(trivia.getToken().getOriginalValue()).matches()) {
          this.getContext().createLineViolation(this, "Use starting comment token '" + startingCommentToken + "' instead.", trivia.getToken());
        }
      }
    }
  }

  @VisibleForTesting
  public void setStartingCommentToken(String startingCommentToken) {
    this.startingCommentToken = startingCommentToken;
  }

  private boolean isStartingCommentTokenParameterValid() {
    return "#".equals(startingCommentToken) || "!".equals(startingCommentToken);
  }

}
