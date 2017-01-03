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
import org.sonar.plugins.jproperties.api.tree.PropertiesTree;
import org.sonar.plugins.jproperties.api.tree.PropertyTree;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "no-properties",
  name = "Files not defining any properties should be removed",
  priority = Priority.MINOR,
  tags = {Tags.UNUSED})
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class NoPropertiesCheck extends DoubleDispatchVisitorCheck {

  private int counter;

  @Override
  public void visitProperties(PropertiesTree tree) {
    counter = 0;
    super.visitProperties(tree);
    if (counter == 0) {
      addFileIssue("Remove this file that does not define any properties.");
    }
  }

  @Override
  public void visitProperty(PropertyTree tree) {
    counter++;
    super.visitProperty(tree);
  }

}
