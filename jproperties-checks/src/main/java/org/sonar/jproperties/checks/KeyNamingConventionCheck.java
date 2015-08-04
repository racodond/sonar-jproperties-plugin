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
import com.sonar.sslr.api.AstNode;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
  key = "key-naming-convention",
  name = "Keys should follow a naming convention",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class KeyNamingConventionCheck extends SquidCheck<LexerlessGrammar> {

  private static final String DEFAULT_FORMAT = "^[.a-zA-Z0-9]+$";
  @RuleProperty(
    key = "Format",
    description = "Regular expression used to check the key against",
    defaultValue = "" + DEFAULT_FORMAT)
  private String format = DEFAULT_FORMAT;

  @Override
  public void init() {
    subscribeTo(JavaPropertiesGrammar.KEY);
  }

  @Override
  public void leaveNode(AstNode astNode) {
    if (!astNode.getTokenValue().matches(format)) {
      getContext().createLineViolation(this, "Rename key \"{0}\" to match the regular expression: {1}", astNode,
        astNode.getTokenValue(), format);
    }
  }

  @VisibleForTesting
  public void setFormat(String format) {
    this.format = format;
  }

}
