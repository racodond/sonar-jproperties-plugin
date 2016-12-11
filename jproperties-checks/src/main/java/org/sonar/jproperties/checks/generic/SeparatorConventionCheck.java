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
package org.sonar.jproperties.checks.generic;

import com.google.common.annotations.VisibleForTesting;

import java.util.Arrays;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.jproperties.checks.CheckUtils;
import org.sonar.jproperties.checks.Tags;
import org.sonar.plugins.jproperties.api.tree.PropertyTree;
import org.sonar.plugins.jproperties.api.tree.SeparatorTree;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "separator-convention",
  name = "Separators should follow a convention",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
@SqaleConstantRemediation("1min")
@ActivatedByDefault
public class SeparatorConventionCheck extends DoubleDispatchVisitorCheck {

  private static final String DEFAULT_FORMAT = "=";

  @RuleProperty(
    key = "Separator",
    description = "Allowed values: ':', '='",
    defaultValue = DEFAULT_FORMAT)
  private String separator = DEFAULT_FORMAT;

  @Override
  public void visitSeparator(SeparatorTree tree) {
    if (!tree.text().equals(separator)) {
      addPreciseIssue(tree, "Use '" + separator + "' as separator instead.");
    }
    super.visitSeparator(tree);
  }

  @Override
  public void visitProperty(PropertyTree tree) {
    int separatorPosition = tree.separator().separatorToken().column();
    int keyLastCharacter = tree.key().value().endColumn();
    String separatorChar = tree.separator().text();

    if (separatorPosition > keyLastCharacter) {
      addPreciseIssue(tree.separator(), "Remove the whitespaces between the key and the separator.");
    }

    if (tree.value() != null && separator.equals(separatorChar)) {
      int elementFirstCharacter = tree.value().value().column();
      if (SeparatorTree.Separator.EQUALS.getValue().equals(separatorChar)
        && elementFirstCharacter > separatorPosition + 1) {
        addPreciseIssue(tree.separator(), "Remove the whitespaces between the separator and the value.");
      }
      if (SeparatorTree.Separator.COLON.getValue().equals(separatorChar)) {
        if (elementFirstCharacter == separatorPosition + 1) {
          addPreciseIssue(tree.separator(), "Add a whitespace between the separator and the value.");
        } else if (elementFirstCharacter > separatorPosition + 2) {
          addPreciseIssue(tree.separator(), "Leave one single whitespace between the separator and the value.");
        }
      }
    }
    super.visitProperty(tree);
  }

  @Override
  public void validateParameters() {
    if (!Arrays.asList("=", ":").contains(separator)) {
      throw new IllegalStateException(paramErrorMessage());
    }
  }

  @VisibleForTesting
  void setSeparator(String separator) {
    this.separator = separator;
  }

  private String paramErrorMessage() {
    return CheckUtils.paramErrorMessage(
      this.getClass(),
      "separator parameter is not valid.\nActual: \"" + separator + "\"\nExpected: '=' or ':'");
  }

}
