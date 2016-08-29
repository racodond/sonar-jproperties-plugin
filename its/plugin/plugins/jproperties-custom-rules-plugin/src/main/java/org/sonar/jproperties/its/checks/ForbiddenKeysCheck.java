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

import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.jproperties.api.tree.KeyTree;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "forbidden-keys",
  priority = Priority.MAJOR,
  name = "Forbidden keys should not be used",
  tags = {"bug"})
@SqaleConstantRemediation("5min")
public class ForbiddenKeysCheck extends DoubleDispatchVisitorCheck {

  private static final Set<String> FORBIDDEN_KEYS = ImmutableSet.of("foo", "bar");

  @Override
  public void visitKey(KeyTree tree) {
    if (FORBIDDEN_KEYS.contains(tree.text().toLowerCase())) {
      addPreciseIssue(tree, "Remove the usage of this forbidden \"" + tree.text() + "\" key.");
    }
    // super method must be called in order to visit what is under the key node in the syntax tree
    super.visitKey(tree);
  }

}
