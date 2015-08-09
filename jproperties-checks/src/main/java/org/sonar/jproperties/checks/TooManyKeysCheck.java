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
  key = "maximum-number-keys",
  name = "The number of keys should be reduced",
  priority = Priority.MAJOR,
  tags = {Tags.BRAIN_OVERLOAD})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.ARCHITECTURE_CHANGEABILITY)
@SqaleConstantRemediation("30min")
@ActivatedByDefault
public class TooManyKeysCheck extends SquidCheck<LexerlessGrammar> {

  private static final int DEFAULT_THRESHOLD = 200;

  @RuleProperty(
    key = "numberKeys",
    description = "The maximum allowed number of keys per file",
    defaultValue = "" + DEFAULT_THRESHOLD)
  private int numberKeys = DEFAULT_THRESHOLD;

  private int currentKey;

  @Override
  public void init() {
    subscribeTo(JavaPropertiesGrammar.KEY);
  }

  @Override
  public void visitFile(AstNode astNode) {
    currentKey = 0;
  }

  @Override
  public void visitNode(AstNode astNode) {
    currentKey++;
  }

  @Override
  public void leaveFile(AstNode astNode) {
    if (currentKey > numberKeys) {
      getContext().createFileViolation(this, "Reduce the number of keys. The number of "
        + "keys is {0} greater than {1} authorized.", currentKey, numberKeys);
    }
  }

  @VisibleForTesting
  public void setNumberKeys(int numberKeys) {
    this.numberKeys = numberKeys;
  }

}
