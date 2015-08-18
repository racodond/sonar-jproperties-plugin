/*
 * SonarQube Java Properties Plugin
 * Copyright (C) 2015 David RACODON
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.jproperties;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sonar.api.batch.rule.Checks;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.ActiveRule;
import org.sonar.jproperties.checks.DuplicatedKeysAcrossFilesCheck;
import org.sonar.jproperties.checks.FileLine;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.api.CodeVisitor;

public class ProjectChecks {

  private final Project project;
  private final RulesProfile rulesProfile;
  private final Checks<SquidAstVisitor> checks;
  private final ResourcePerspectives resourcePerspectives;

  public ProjectChecks(Project project, RulesProfile rulesProfile, Checks<SquidAstVisitor> checks,
    ResourcePerspectives resourcePerspectives) {
    this.project = project;
    this.rulesProfile = rulesProfile;
    this.checks = checks;
    this.resourcePerspectives = resourcePerspectives;
  }

  public void reportProjectIssues() {
    checkDuplicatedKeysAcrossFiles();
  }

  private void checkDuplicatedKeysAcrossFiles() {
    ActiveRule activeRule = rulesProfile.getActiveRule(JavaProperties.KEY, DuplicatedKeysAcrossFilesCheck.RULE_KEY);
    if (activeRule != null) {
      CodeVisitor check = checks.of(activeRule.getRule().ruleKey());
      if (check != null) {
        Map<String, List<FileLine>> keys = ((DuplicatedKeysAcrossFilesCheck) check).getKeys();
        Iterator it = keys.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry pair = (Map.Entry) it.next();
          if (((List<FileLine>) pair.getValue()).size() > 1) {
            addIssue(DuplicatedKeysAcrossFilesCheck.RULE_KEY, "Remove this cross-file duplicated key", ((List<FileLine>) pair.getValue()).get(0));
          }
        }
      }
    }
  }

  private void addIssue(String ruleKey, String message, FileLine fileLine) {
    ActiveRule activeRule = rulesProfile.getActiveRule(JavaProperties.KEY, ruleKey);
    if (activeRule != null) {
      CodeVisitor check = checks.of(activeRule.getRule().ruleKey());
      if (check != null) {
        Issuable issuable = resourcePerspectives.as(Issuable.class, File.fromIOFile(fileLine.getFile(), project));
        if (issuable != null) {
          Issue issue = issuable.newIssueBuilder().ruleKey(RuleKey.of(JavaProperties.KEY, ruleKey)).message(message).line(fileLine.getLine()).build();
          issuable.addIssue(issue);
        }
      }
    }
  }

}
