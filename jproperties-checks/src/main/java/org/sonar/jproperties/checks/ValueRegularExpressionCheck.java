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

import com.sonar.sslr.api.AstNode;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.annotations.RuleTemplate;

@Rule(
  key = "value-regular-expression",
  name = "Regular expression on value",
  priority = Priority.MAJOR)
@RuleTemplate
public class ValueRegularExpressionCheck extends JavaPropertiesCheck {

  private static final String DEFAULT_REGULAR_EXPRESSION = ".*";
  private static final String DEFAULT_MESSAGE = "The regular expression matches this value.";

  @RuleProperty(
    key = "regularExpression",
    description = "The regular expression",
    defaultValue = DEFAULT_REGULAR_EXPRESSION)
  public String regularExpression = DEFAULT_REGULAR_EXPRESSION;

  @RuleProperty(
    key = "message",
    description = "The issue message",
    defaultValue = DEFAULT_MESSAGE)
  public String message = DEFAULT_MESSAGE;

  @Override
  public void init() {
    validateRegularExpressionParameter();
    subscribeTo(JavaPropertiesGrammar.ELEMENT);
  }

  @Override
  public void leaveNode(AstNode node) {
    if (node.getTokenValue().matches(regularExpression)) {
      addLineIssue(this, message, node.getTokenLine());
    }
  }

  private void validateRegularExpressionParameter() {
    try {
      Pattern.compile(regularExpression);
    } catch (PatternSyntaxException exception) {
      throw new IllegalStateException("Check jproperties:value-regular-expression - regularExpression parameter \""
        + regularExpression + "\" is not a valid regular expression.", exception);
    }
  }

}
