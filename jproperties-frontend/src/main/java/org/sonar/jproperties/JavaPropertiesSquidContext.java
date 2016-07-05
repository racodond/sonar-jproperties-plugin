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
package org.sonar.jproperties;

import java.util.Set;

import org.sonar.jproperties.issue.Issue;
import org.sonar.squidbridge.SquidAstVisitorContextImpl;
import org.sonar.squidbridge.api.SourceProject;
import org.sonar.sslr.parser.LexerlessGrammar;

public class JavaPropertiesSquidContext extends SquidAstVisitorContextImpl<LexerlessGrammar> {

  private final Set<Issue> issues;

  public JavaPropertiesSquidContext(SourceProject project, Set<Issue> issues) {
    super(project);
    this.issues = issues;
  }

  public Set<Issue> getIssues() {
    return issues;
  }

}
