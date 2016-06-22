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
package org.sonar.plugins.jproperties;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sonar.api.batch.rule.Checks;
import org.sonar.api.rule.RuleKey;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.checks.DuplicatedKeysAcrossFilesCheck;
import org.sonar.jproperties.checks.FileNode;
import org.sonar.jproperties.issue.Issue;
import org.sonar.jproperties.issue.PreciseIssue;
import org.sonar.squidbridge.checks.SquidCheck;

public class ProjectChecks {

  private final Checks<SquidCheck> checks;
  private final Set<Issue> issues;

  public ProjectChecks(Checks<SquidCheck> checks, Set<Issue> issues) {
    this.issues = issues;
    this.checks = checks;
  }

  public void checkProjectIssues() {
    checkDuplicatedKeysAcrossFiles();
  }

  private void checkDuplicatedKeysAcrossFiles() {
    DuplicatedKeysAcrossFilesCheck check = (DuplicatedKeysAcrossFilesCheck) checks.of(RuleKey.of(JavaPropertiesLanguage.KEY, DuplicatedKeysAcrossFilesCheck.RULE_KEY));
    if (check != null) {
      for (Map.Entry<String, List<FileNode>> entry : check.getKeys().entrySet())
        if (entry.getValue().size() > 1) {
          addIssue(check, "Remove this cross-file duplicated key.", entry.getValue().get(0));
        }
    }
  }

  private void addIssue(JavaPropertiesCheck check, String message, FileNode fileNode) {
    issues.add(new PreciseIssue(check, fileNode.getFile(), message, fileNode.getNode(), check.getCharset()));
  }

}
