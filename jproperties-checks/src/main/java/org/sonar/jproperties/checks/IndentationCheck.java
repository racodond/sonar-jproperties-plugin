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

import com.sonar.sslr.api.AstNode;

import javax.annotation.Nullable;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "indentation",
  name = "All property definitions should start at column 1",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
@SqaleConstantRemediation("1min")
@ActivatedByDefault
public class IndentationCheck extends JavaPropertiesCheck {

  private boolean hasBom = false;

  @Override
  public void init() {
    subscribeTo(JavaPropertiesGrammar.BOM);
    subscribeTo(JavaPropertiesGrammar.KEY);
  }

  @Override
  public void visitFile(@Nullable AstNode astNode) {
    hasBom = false;
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (astNode.is(JavaPropertiesGrammar.BOM)) {
      hasBom = true;
    }
  }

  @Override
  public void leaveNode(AstNode astNode) {
    if (astNode.is(JavaPropertiesGrammar.KEY)) {
      if (astNode.getTokenLine() == 1) {
        if (hasBom && astNode.getToken().getColumn() != 1) {
          createIssue(astNode);
        } else if (!hasBom && astNode.getToken().getColumn() != 0) {
          createIssue(astNode);
        }
      } else {
        if (astNode.getToken().getColumn() != 0) {
          createIssue(astNode);
        }
      }
    }
  }

  private void createIssue(AstNode astNode) {
    addIssue(this, "Remove the whitespaces before the key.", astNode);
  }

}
