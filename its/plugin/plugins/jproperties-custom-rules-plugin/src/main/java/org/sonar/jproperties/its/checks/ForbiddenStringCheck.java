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
package org.sonar.jproperties.its.checks;

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.jproperties.api.tree.LiteralTree;
import org.sonar.plugins.jproperties.api.tree.Tree;
import org.sonar.plugins.jproperties.api.visitors.SubscriptionVisitorCheck;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "forbidden-string",
  priority = Priority.CRITICAL,
  name = "'WTF' should never appears in keys or values",
  tags = {"convention"})
@SqaleConstantRemediation("5min")
public class ForbiddenStringCheck extends SubscriptionVisitorCheck {

  private static final String FORBIDDEN_STRING = "wtf";

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return ImmutableList.of(Tree.Kind.KEY, Tree.Kind.VALUE);
  }

  @Override
  public void visitNode(Tree tree) {
    if (((LiteralTree) tree).text().toLowerCase().contains(FORBIDDEN_STRING)) {
      addPreciseIssue(tree, "Remove this usage of \"WTF\".");
    }
  }

}
