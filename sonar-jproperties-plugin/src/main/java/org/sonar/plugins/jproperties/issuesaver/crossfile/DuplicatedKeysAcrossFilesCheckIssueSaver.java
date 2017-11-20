/*
 * SonarQube Java Properties Analyzer
 * Copyright (C) 2015-2017 David RACODON
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
package org.sonar.plugins.jproperties.issuesaver.crossfile;

import org.sonar.jproperties.checks.generic.DuplicatedKeysAcrossFilesCheck;
import org.sonar.jproperties.checks.generic.FileKeyTree;
import org.sonar.plugins.jproperties.api.visitors.issue.IssueLocation;
import org.sonar.plugins.jproperties.api.visitors.issue.PreciseIssue;
import org.sonar.plugins.jproperties.issuesaver.IssueSaver;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DuplicatedKeysAcrossFilesCheckIssueSaver extends CrossFileCheckIssueSaver {

  public DuplicatedKeysAcrossFilesCheckIssueSaver(IssueSaver issueSaver) {
    super(issueSaver);
  }

  @Override
  public void saveIssues() {
    Optional<DuplicatedKeysAcrossFilesCheck> check = getIssueSaver().getCheck(DuplicatedKeysAcrossFilesCheck.class);

    if (check.isPresent()) {
      check.get().getKeys().entrySet()
        .stream()
        .filter(entry -> isKeyDuplicatedAcrossFiles(entry.getValue()))
        .forEach(entry -> saveIssue(check.get(), entry));
    }
  }

  private void saveIssue(DuplicatedKeysAcrossFilesCheck check, Map.Entry<String, List<FileKeyTree>> entry) {
    getIssueSaver().saveIssue(
      new PreciseIssue(
        check,
        new IssueLocation(entry.getValue().get(0).getFile(),
          entry.getValue().get(0).getKey(),
          buildIssueMessage(entry))));
  }

  private String buildIssueMessage(Map.Entry<String, List<FileKeyTree>> duplicatedKey) {
    return "Remove this cross-file duplicated key."
      + " \""
      + duplicatedKey.getKey()
      + "\" is defined in: "
      + duplicatedKey.getValue()
        .stream()
        .map(o -> o.getFile().getName())
        .distinct()
        .sorted()
        .collect(Collectors.joining(", "));
  }

  private boolean isKeyDuplicatedAcrossFiles(List<FileKeyTree> fileKeyTrees) {
    return fileKeyTrees.stream()
      .map(f -> f.getFile().getAbsolutePath())
      .distinct()
      .count() > 1;
  }

}
