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

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.checks.Tags;
import org.sonar.jproperties.visitors.CharsetAwareVisitor;
import org.sonar.plugins.jproperties.api.tree.KeyTree;
import org.sonar.plugins.jproperties.api.tree.LiteralTree;
import org.sonar.plugins.jproperties.api.tree.ValueTree;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

import java.nio.charset.Charset;

@Rule(
  key = "java9-default-encoding-switch",
  name = "ISO-8859-1 characters not compatible with UTF-8 should be escaped to keep compatibility with Java 9 default encoding switch",
  priority = Priority.CRITICAL,
  tags = {Tags.BUG})
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class Java9DefaultEncodingSwitchCheck extends DoubleDispatchVisitorCheck implements CharsetAwareVisitor {

  private Charset charset;

  @Override
  public void visitKey(KeyTree tree) {
    checkForIssues(tree);
    super.visitKey(tree);
  }

  @Override
  public void visitValue(ValueTree tree) {
    checkForIssues(tree);
    super.visitValue(tree);
  }

  private boolean toBeChecked() {
    return charset.equals(Charset.forName("ISO-8859-1"));
  }

  private void checkForIssues(LiteralTree tree) {
    if (toBeChecked()) {
      for (int i = 0; i < tree.text().toCharArray().length; i++) {
        if (tree.text().toCharArray()[i] > 127) {
          addPreciseIssue(tree.value(), i, i + 1, "Protect this character by using the unicode notation.");
        }
      }
    }
  }

  @Override
  public void setCharset(Charset charset) {
    this.charset = charset;
  }

}
