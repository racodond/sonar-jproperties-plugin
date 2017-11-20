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

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.sonar.jproperties.checks.LocaleUtils;
import org.sonar.jproperties.checks.generic.MissingTranslationsInDefaultCheck;
import org.sonar.plugins.jproperties.api.visitors.issue.FileIssue;
import org.sonar.plugins.jproperties.issuesaver.IssueSaver;

public class MissingTranslationsInDefaultCheckIssueSaver extends CrossFileCheckIssueSaver {

  public MissingTranslationsInDefaultCheckIssueSaver(IssueSaver issueSaver) {
    super(issueSaver);
  }

  @Override
  public void saveIssues() {
    Optional<MissingTranslationsInDefaultCheck> check = getIssueSaver().getCheck(MissingTranslationsInDefaultCheck.class);

    if (check.isPresent()) {
      check.get().getFileKeys().entrySet()
        .stream()
        .filter(entry -> LocaleUtils.isLocaleFile(entry.getKey()))
        .forEach(entry -> {
          Optional<Map.Entry<File, Set<String>>> defaultLocaleFileKeys = getDefaultLocaleFileKeys(entry, check.get().getFileKeys());
          if (defaultLocaleFileKeys.isPresent()) {
            Set<String> missingTranslations = missingTranslations(entry, defaultLocaleFileKeys.get());
            if (!missingTranslations.isEmpty()) {
              saveIssue(check.get(), defaultLocaleFileKeys.get().getKey(), entry.getKey(), missingTranslations);
            }
          }
        });
    }
  }

  private void saveIssue(MissingTranslationsInDefaultCheck check, File defaultLocale, File locale, Set<String> missingTranslations) {
    getIssueSaver().saveIssue(
      new FileIssue(
        check,
        defaultLocale,
        buildIssueMessage(missingTranslations, locale)));
  }

  private String buildIssueMessage(Set<String> missingTranslations, File locale) {
    return "Add the following missing default translation keys defined in "
      + locale.getName()
      + ": "
      + missingTranslations
        .stream()
        .sorted()
        .collect(Collectors.joining(", "));
  }

  private Set<String> missingTranslations(Map.Entry<File, Set<String>> locale, Map.Entry<File, Set<String>> defaultLocale) {
    Set<String> missingTranslations = locale.getValue().stream().collect(Collectors.toSet());
    missingTranslations.removeAll(defaultLocale.getValue());
    return missingTranslations;
  }

  private Optional<Map.Entry<File, Set<String>>> getDefaultLocaleFileKeys(
    Map.Entry<File, Set<String>> currentFileKeys,
    Map<File, Set<String>> allFileKeys) {

    return allFileKeys.entrySet()
      .stream()
      .filter(e -> e.getKey().getAbsolutePath().equals(LocaleUtils.getDefaultLocaleFilePath(currentFileKeys.getKey())))
      .findFirst();
  }

}
