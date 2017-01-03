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

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.checks.Tags;
import org.sonar.plugins.jproperties.api.tree.KeyTree;
import org.sonar.plugins.jproperties.api.tree.PropertiesTree;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.plugins.jproperties.api.visitors.issue.PreciseIssue;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "duplicated-keys",
  name = "Duplicated keys should be removed",
  priority = Priority.CRITICAL,
  tags = {Tags.BUG})
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class DuplicatedKeysCheck extends DoubleDispatchVisitorCheck {

  private final Map<String, List<KeyTree>> keys = new HashMap<>();

  @Override
  public void visitProperties(PropertiesTree tree) {
    keys.clear();
    super.visitProperties(tree);
    keys.keySet()
      .stream()
      .filter(k -> keys.get(k).size() > 1)
      .forEach(k -> createIssue(keys.get(k)));
  }

  @Override
  public void visitKey(KeyTree tree) {
    if (keys.containsKey(tree.text())) {
      keys.get(tree.text()).add(tree);
    } else {
      keys.put(tree.text(), Lists.newArrayList(tree));
    }
    super.visitKey(tree);
  }

  private void createIssue(List<KeyTree> duplicatedKeys) {
    PreciseIssue issue = addPreciseIssue(duplicatedKeys.get(0), "Remove the duplicated keys \"" + duplicatedKeys.get(0).text() + "\".");
    for (int i = 1; i < duplicatedKeys.size(); i++) {
      issue.secondary(duplicatedKeys.get(i), "Duplicated key");
    }
  }

}
