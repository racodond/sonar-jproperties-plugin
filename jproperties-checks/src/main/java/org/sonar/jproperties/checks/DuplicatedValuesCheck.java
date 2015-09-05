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

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "duplicated-values",
  name = "Different keys having the same value should be merged",
  priority = Priority.CRITICAL,
  tags = {Tags.PITFALL})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("30min")
@ActivatedByDefault
public class DuplicatedValuesCheck extends JavaPropertiesCheck {

  private Map<String, List<AstNode>> elementsMap = new HashMap<>();

  @Override
  public void init() {
    subscribeTo(JavaPropertiesGrammar.PROPERTIES, JavaPropertiesGrammar.PROPERTY);
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.is(JavaPropertiesGrammar.PROPERTIES)) {
      elementsMap.clear();
    } else {
      if (node.getFirstChild(JavaPropertiesGrammar.ELEMENT) != null) {
        String value = getValueWithoutLineBreak(node.getFirstChild(JavaPropertiesGrammar.ELEMENT).getTokenValue());
        if (elementsMap.containsKey(value)) {
          elementsMap.get(value).add(node.getFirstChild(JavaPropertiesGrammar.KEY));
        } else {
          elementsMap.put(value, Lists.newArrayList(node.getFirstChild(JavaPropertiesGrammar.KEY)));
        }
      }
    }
  }

  @Override
  public void leaveNode(AstNode node) {
    if (node.is(JavaPropertiesGrammar.PROPERTIES)) {
      for (Map.Entry<String, List<AstNode>> entry : elementsMap.entrySet()) {
        if (entry.getValue().size() > 1) {
          addIssue(
            entry.getValue().get(0),
            this,
            "Merge keys \"" + getCommaSeparatedListOfDuplicatedKeys(entry.getValue()) + "\" that have the same value \"" + getFiftyCharacterValue(entry.getKey()) + "\".");
        }
      }
    }
  }

  private static String getCommaSeparatedListOfDuplicatedKeys(List<AstNode> keyNodes) {
    StringBuilder keys = new StringBuilder();
    for (AstNode keyNode : keyNodes) {
      if (keys.length() != 0) {
        keys.append(", ");
      }
      keys.append(keyNode.getTokenValue());
    }
    return keys.toString();
  }

  private static String getFiftyCharacterValue(String value) {
    return value.length() > 50 ? value.substring(0, 50) + "..." : value;
  }

  private static String getValueWithoutLineBreak(String value) {
    return value.replaceAll("\\\\\\n\\s*", "");
  }

}
