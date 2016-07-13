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
import java.util.Optional;
import java.util.stream.Collectors;

import org.sonar.jproperties.checks.DuplicatedKeysAcrossFilesCheck;
import org.sonar.jproperties.checks.FileKeyTree;
import org.sonar.plugins.jproperties.api.JavaPropertiesCheck;
import org.sonar.plugins.jproperties.api.visitors.issue.IssueLocation;
import org.sonar.plugins.jproperties.api.visitors.issue.PreciseIssue;

class CrossFileChecks {

  private final IssueSaver issueSaver;

  public CrossFileChecks(IssueSaver issueSaver) {
    this.issueSaver = issueSaver;
  }

  public void saveCrossFileIssues() {
    checkDuplicatedKeysAcrossFiles();
  }

  private void checkDuplicatedKeysAcrossFiles() {
    DuplicatedKeysAcrossFilesCheck check = null;

    Optional<JavaPropertiesCheck> searchedCheck = issueSaver.getChecks().all()
      .stream()
      .filter(r -> r instanceof DuplicatedKeysAcrossFilesCheck)
      .findFirst();

    if (searchedCheck.isPresent()) {
      check = (DuplicatedKeysAcrossFilesCheck) searchedCheck.get();
    }

    if (check != null) {
      saveDuplicatedKeysAcrossFilesIssues(check);
    }
  }

  private void saveDuplicatedKeysAcrossFilesIssues(DuplicatedKeysAcrossFilesCheck check) {
    for (Map.Entry<String, List<FileKeyTree>> entry : check.getKeys().entrySet()) {
      if (entry.getValue().size() > 1) {
        issueSaver.saveIssue(
          new PreciseIssue(
            check,
            new IssueLocation(entry.getValue().get(0).getFile(),
              entry.getValue().get(0).getKey(),
              buildDuplicatedKeysAcrossFilesIssueMessage(entry))));
      }
    }
  }

  private String buildDuplicatedKeysAcrossFilesIssueMessage(Map.Entry<String, List<FileKeyTree>> duplicatedKey) {
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

}
