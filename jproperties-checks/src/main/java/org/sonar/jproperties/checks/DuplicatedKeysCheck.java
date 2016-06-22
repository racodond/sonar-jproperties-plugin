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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.issue.PreciseIssue;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;

@Rule(
  key = "duplicated-keys",
  name = "Duplicated keys should be removed",
  priority = Priority.CRITICAL,
  tags = {Tags.BUG})
@ActivatedByDefault
public class DuplicatedKeysCheck extends JavaPropertiesCheck {

  private final Map<String, List<AstNode>> keys = new HashMap<>();

  @Override
  public void init() {
    subscribeTo(JavaPropertiesGrammar.PROPERTIES, JavaPropertiesGrammar.KEY);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (astNode.getType().equals(JavaPropertiesGrammar.PROPERTIES)) {
      keys.clear();
    } else {
      if (keys.containsKey(astNode.getTokenValue())) {
        keys.get(astNode.getTokenValue()).add(astNode);
      } else {
        keys.put(astNode.getTokenValue(), Lists.newArrayList(astNode));
      }
    }
  }

  @Override
  public void leaveNode(AstNode astNode) {
    if (astNode.is(JavaPropertiesGrammar.PROPERTIES)) {
      keys.keySet().stream().filter(k -> keys.get(k).size() > 1).forEach(k -> createIssue(keys.get(k)));
    }
  }

  private void createIssue(List<AstNode> duplicatedKeys) {
    PreciseIssue issue = addIssue(this, "Remove the duplicated keys \"" + duplicatedKeys.get(0).getTokenValue() + "\".", duplicatedKeys.get(0));
    for (int i = 1; i < duplicatedKeys.size(); i++) {
      issue.addSecondaryLocation("Duplicated key", duplicatedKeys.get(i));
    }
  }

}
