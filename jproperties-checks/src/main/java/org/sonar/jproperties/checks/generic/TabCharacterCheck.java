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

import com.google.common.io.Files;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.checks.Tags;
import org.sonar.jproperties.visitors.CharsetAwareVisitor;
import org.sonar.plugins.jproperties.api.tree.PropertiesTree;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "tab-character",
  name = "Tabulation characters should not be used",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
@SqaleConstantRemediation("2min")
@ActivatedByDefault
public class TabCharacterCheck extends DoubleDispatchVisitorCheck implements CharsetAwareVisitor {

  private Charset charset;

  @Override
  public void visitProperties(PropertiesTree tree) {
    List<String> lines;
    try {
      lines = Files.readLines(getContext().getFile(), charset);
    } catch (IOException e) {
      throw new IllegalStateException("Check jproperties:" + this.getClass().getAnnotation(Rule.class).key()
        + ": Error while reading " + getContext().getFile().getName(), e);
    }
    for (String line : lines) {
      if (line.contains("\t")) {
        addFileIssue("Replace all tab characters in this file by sequences of whitespaces.");
        break;
      }
    }
    super.visitProperties(tree);
  }

  @Override
  public void setCharset(Charset charset) {
    this.charset = charset;
  }

}
