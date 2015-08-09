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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  key = "separator-convention",
  name = "Separators should follow a convention",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("1min")
@ActivatedByDefault
public class SeparatorConventionCheck extends SquidCheck<LexerlessGrammar> {

  private static final String DEFAULT_FORMAT = "=";
  private static final Logger LOG = LoggerFactory.getLogger(SeparatorConventionCheck.class);

  @RuleProperty(
    key = "Separator",
    description = "Allowed values: ':', '='",
    defaultValue = "" + DEFAULT_FORMAT)
  private String separator = DEFAULT_FORMAT;

  @Override
  public void init() {
    if (isSeparatorParameterValid()) {
      subscribeTo(JavaPropertiesGrammar.PROPERTY);
    } else {
      LOG.error("Rule jproperties:separator-convention: separator parameter value is not valid.\nActual: '" + separator
        + "'\nExpected: '=' or ':'\nNo check will be performed against this jproperties:separator-convention rule.");
    }
  }

  @Override
  public void leaveNode(AstNode astNode) {
    if (!astNode.getFirstChild(JavaPropertiesGrammar.SEPARATOR).getTokenValue().equals(separator)) {
      getContext().createLineViolation(this, "Use '" + separator + "' as separator instead.", astNode);
      return;
    }

    int separatorPosition = astNode.getFirstChild(JavaPropertiesGrammar.SEPARATOR).getToken().getColumn();
    int keyLastCharacter = astNode.getFirstChild(JavaPropertiesGrammar.KEY).getTokenValue().length() + astNode.getFirstChild(JavaPropertiesGrammar.KEY).getToken().getColumn();
    if (separatorPosition > keyLastCharacter) {
      getContext().createLineViolation(this, "Remove the whitespaces between the key and the separator.", astNode);
    }

    if (astNode.getFirstChild(JavaPropertiesGrammar.ELEMENT) != null) {
      int elementFirstCharacter = astNode.getFirstChild(JavaPropertiesGrammar.ELEMENT).getToken().getColumn();
      if ("=".equals(separator) && elementFirstCharacter > separatorPosition + 1) {
        getContext().createLineViolation(this, "Remove the whitespaces between the separator and the value.", astNode);
      }
      if (":".equals(separator)) {
        if (elementFirstCharacter == separatorPosition + 1) {
          getContext().createLineViolation(this, "Add a whitespace between the separator and the value.", astNode);
        } else if (elementFirstCharacter > separatorPosition + 2) {
          getContext().createLineViolation(this, "Leave one single whitespace between the separator and the value.", astNode);
        }
      }
    }
  }

  @VisibleForTesting
  public void setSeparator(String separator) {
    this.separator = separator;
  }

  private boolean isSeparatorParameterValid() {
    return "=".equals(separator) || ":".equals(separator);
  }

}
