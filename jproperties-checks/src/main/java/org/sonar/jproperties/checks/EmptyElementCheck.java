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
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;

@Rule(
  key = "empty-element",
  name = "Property with empty value should be removed",
  priority = Priority.CRITICAL,
  tags = {Tags.PITFALL})
@ActivatedByDefault
public class EmptyElementCheck extends JavaPropertiesCheck {

  @Override
  public void init() {
    subscribeTo(JavaPropertiesGrammar.PROPERTY);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (astNode.getFirstChild(JavaPropertiesGrammar.ELEMENT) == null) {
      addIssue(this, "Remove this property whose value is empty.", astNode.getFirstChild(JavaPropertiesGrammar.KEY));
    }
  }

}
