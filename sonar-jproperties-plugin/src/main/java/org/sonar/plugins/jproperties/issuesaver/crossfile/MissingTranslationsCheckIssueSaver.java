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
package org.sonar.plugins.jproperties.issuesaver.crossfile;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.sonar.jproperties.checks.LocaleUtils;
import org.sonar.jproperties.checks.generic.MissingTranslationsCheck;
import org.sonar.plugins.jproperties.api.visitors.issue.FileIssue;
import org.sonar.plugins.jproperties.issuesaver.IssueSaver;

public class MissingTranslationsCheckIssueSaver extends CrossFileCheckIssueSaver {

  public MissingTranslationsCheckIssueSaver(IssueSaver issueSaver) {
    super(issueSaver);
  }

  @Override
  public void saveIssues() {
    Optional<MissingTranslationsCheck> check = getIssueSaver().getCheck(MissingTranslationsCheck.class);

    if (check.isPresent()) {
      for (Map.Entry<File, Set<String>> entry : check.get().getFileKeys().entrySet()) {
        Set<String> missingTranslations = missingTranslations(entry, check.get().getFileKeys());
        if (!missingTranslations.isEmpty()) {
          saveIssue(check.get(), entry.getKey(), missingTranslations);
        }
      }
    }
  }

  private void saveIssue(MissingTranslationsCheck check, File file, Set<String> missingTranslations) {
    getIssueSaver().saveIssue(
      new FileIssue(
        check,
        file,
        buildIssueMessage(missingTranslations)));
  }

  private String buildIssueMessage(Set<String> missingTranslations) {
    return "Add the following missing translation keys: "
      + missingTranslations
        .stream()
        .sorted()
        .collect(Collectors.joining(", "));
  }

  private Set<String> missingTranslations(Map.Entry<File, Set<String>> currentFileKeys, Map<File, Set<String>> allFileKeys) {
    Set<String> missingTranslations = new HashSet<>();
    String defaultLocaleFilePath = LocaleUtils.getDefaultLocaleFilePath(currentFileKeys.getKey());

    for (Map.Entry<File, Set<String>> entry : allFileKeys.entrySet()) {
      if (entry.getKey().getAbsolutePath().equals(defaultLocaleFilePath)) {
        missingTranslations = entry.getValue().stream().collect(Collectors.toSet());
        missingTranslations.removeAll(currentFileKeys.getValue());
        break;
      }
    }

    return missingTranslations;
  }

}
