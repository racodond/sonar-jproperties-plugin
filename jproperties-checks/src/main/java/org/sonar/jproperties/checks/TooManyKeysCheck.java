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

import com.google.common.annotations.VisibleForTesting;
import com.sonar.sslr.api.AstNode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.issue.PreciseIssue;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "maximum-number-keys",
  name = "Number of keys should be reduced",
  priority = Priority.MAJOR,
  tags = {Tags.BRAIN_OVERLOAD})
@SqaleConstantRemediation("30min")
@ActivatedByDefault
public class TooManyKeysCheck extends JavaPropertiesCheck {

  private static final int DEFAULT_THRESHOLD = 200;

  @RuleProperty(
    key = "numberKeys",
    description = "The maximum allowed number of keys per file",
    defaultValue = "" + DEFAULT_THRESHOLD)
  private int numberKeys = DEFAULT_THRESHOLD;

  private final List<AstNode> keyNodes = new ArrayList<>();

  @Override
  public void init() {
    subscribeTo(JavaPropertiesGrammar.KEY);
  }

  @Override
  public void visitFile(@Nullable AstNode astNode) {
    keyNodes.clear();
  }

  @Override
  public void visitNode(AstNode astNode) {
    keyNodes.add(astNode);
  }

  @Override
  public void leaveFile(@Nullable AstNode astNode) {
    if (keyNodes.size() > numberKeys) {
      PreciseIssue issue = addFileIssue(this, MessageFormat.format("Reduce the number of keys. The number of "
        + "keys is {0}, greater than {1} authorized.", keyNodes.size(), numberKeys));
      keyNodes.subList(numberKeys, keyNodes.size()).stream().forEach(n -> issue.addSecondaryLocation("+1", n));
    }
  }

  @VisibleForTesting
  public void setNumberKeys(int numberKeys) {
    this.numberKeys = numberKeys;
  }

}
