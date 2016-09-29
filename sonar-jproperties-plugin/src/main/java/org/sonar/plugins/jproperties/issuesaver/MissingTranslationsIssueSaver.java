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
package org.sonar.plugins.jproperties.issuesaver;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import org.sonar.jproperties.checks.CheckUtils;
import org.sonar.jproperties.checks.generic.MissingTranslationsCheck;
import org.sonar.plugins.jproperties.api.JavaPropertiesCheck;
import org.sonar.plugins.jproperties.api.visitors.issue.FileIssue;

public class MissingTranslationsIssueSaver {

  private static final String JAVA_PROPERTIES_FILE_EXTENSION = ".properties";

  private MissingTranslationsIssueSaver() {
  }

  public static void saveIssues(IssueSaver issueSaver) {
    MissingTranslationsCheck check = null;

    Optional<JavaPropertiesCheck> searchedCheck = issueSaver.getChecks().all()
      .stream()
      .filter(r -> r instanceof MissingTranslationsCheck)
      .findFirst();

    if (searchedCheck.isPresent()) {
      check = (MissingTranslationsCheck) searchedCheck.get();
    }

    if (check != null) {
      for (Map.Entry<File, Set<String>> entry : check.getFileKeys().entrySet()) {
        Set<String> missingTranslations = missingTranslations(entry, check.getFileKeys());
        if (!missingTranslations.isEmpty()) {
          saveIssue(issueSaver, check, entry.getKey(), missingTranslations);
        }
      }
    }
  }

  private static void saveIssue(IssueSaver issueSaver, MissingTranslationsCheck check, File file, Set<String> missingTranslations) {
    issueSaver.saveIssue(
      new FileIssue(
        check,
        file,
        buildIssueMessage(missingTranslations)));
  }

  private static String buildIssueMessage(Set<String> missingTranslations) {
    return "Add the following missing translation keys: "
      + missingTranslations
        .stream()
        .sorted()
        .collect(Collectors.joining(", "));
  }

  private static Set<String> missingTranslations(Map.Entry<File, Set<String>> currentFileKeys, Map<File, Set<String>> allFileKeys) {
    Set<String> missingTranslations = new HashSet<>();

    List<String> locales = CheckUtils.LOCALES
      .stream()
      .filter(l -> currentFileKeys.getKey().getName().endsWith(l + JAVA_PROPERTIES_FILE_EXTENSION))
      .collect(Collectors.toList());

    if (!locales.isEmpty()) {
      String parentFilePath = currentFileKeys.getKey().getAbsolutePath()
        .substring(0, currentFileKeys.getKey().getAbsolutePath().length() - JAVA_PROPERTIES_FILE_EXTENSION.length() - locales.get(0).length() - 1) + JAVA_PROPERTIES_FILE_EXTENSION;

      for (Map.Entry<File, Set<String>> entry : allFileKeys.entrySet()) {
        if (entry.getKey().getAbsolutePath().equals(parentFilePath)) {
          missingTranslations = entry.getValue().stream().collect(Collectors.toSet());
          missingTranslations.removeAll(currentFileKeys.getValue());
          break;
        }
      }
    }

    return missingTranslations;
  }

}
