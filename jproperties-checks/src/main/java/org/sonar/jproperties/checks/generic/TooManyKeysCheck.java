/*
 * SonarQube Java Properties Analyzer
 * Copyright (C) 2015-2017 David RACODON
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

import com.google.common.annotations.VisibleForTesting;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.jproperties.checks.Tags;
import org.sonar.plugins.jproperties.api.tree.KeyTree;
import org.sonar.plugins.jproperties.api.tree.PropertiesTree;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.plugins.jproperties.api.visitors.issue.FileIssue;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Rule(
  key = "maximum-number-keys",
  name = "Number of keys should be reduced",
  priority = Priority.MAJOR,
  tags = {Tags.BRAIN_OVERLOAD})
@SqaleConstantRemediation("30min")
@ActivatedByDefault
public class TooManyKeysCheck extends DoubleDispatchVisitorCheck {

  private static final int DEFAULT_THRESHOLD = 200;

  @RuleProperty(
    key = "numberKeys",
    description = "The maximum allowed number of keys per file",
    defaultValue = "" + DEFAULT_THRESHOLD)
  private int numberKeys = DEFAULT_THRESHOLD;

  private final List<KeyTree> keyTrees = new ArrayList<>();

  @Override
  public void visitKey(KeyTree tree) {
    keyTrees.add(tree);
    super.visitKey(tree);
  }

  @Override
  public void visitProperties(PropertiesTree tree) {
    keyTrees.clear();
    super.visitProperties(tree);
    if (keyTrees.size() > numberKeys) {
      FileIssue issue = addFileIssue(
        MessageFormat.format(
          "Reduce the number of keys. The number of keys is {0}, greater than {1} authorized.",
          keyTrees.size(),
          numberKeys));
      keyTrees.subList(numberKeys, keyTrees.size()).stream().forEach(n -> issue.secondary(n, "+1"));
    }
  }

  @VisibleForTesting
  void setNumberKeys(int numberKeys) {
    this.numberKeys = numberKeys;
  }

}
