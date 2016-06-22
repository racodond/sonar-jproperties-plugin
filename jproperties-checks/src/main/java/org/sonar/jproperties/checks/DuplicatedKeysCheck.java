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

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;

import java.util.List;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "duplicated-keys",
  name = "Duplicated keys should be removed",
  priority = Priority.CRITICAL,
  tags = {Tags.BUG})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class DuplicatedKeysCheck extends JavaPropertiesCheck {

  private List<String> keys = Lists.newArrayList();

  @Override
  public void init() {
    subscribeTo(JavaPropertiesGrammar.PROPERTIES, JavaPropertiesGrammar.KEY);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (astNode.getType().equals(JavaPropertiesGrammar.PROPERTIES)) {
      keys.clear();
    } else {
      if (keys.contains(astNode.getTokenValue())) {
        addIssue(astNode, "Remove the duplicated key \"" + astNode.getTokenValue() + "\".");
      } else {
        keys.add(astNode.getTokenValue());
      }
    }
  }
}
