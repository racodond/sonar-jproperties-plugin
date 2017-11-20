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
package org.sonar.jproperties.checks;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocaleUtils {

  public static final ImmutableSet<String> LOCALES = ImmutableSet.copyOf(
    Arrays.stream(DateFormat.getAvailableLocales())
      .filter(l -> !l.toString().isEmpty())
      .map(Object::toString)
      .collect(Collectors.toSet()));

  private LocaleUtils() {
  }

  public static boolean isLocaleFile(File file) {
    return getFileLocale(file).isPresent();
  }

  public static String getDefaultLocaleFilePath(File localeFile) {
    String locale = getFileLocale(localeFile).orElse("");

    return localeFile
      .getAbsolutePath()
      .substring(0, localeFile.getAbsolutePath().length() - CheckUtils.JAVA_PROPERTIES_FILE_EXTENSION.length() - locale.length() - 1)
      + CheckUtils.JAVA_PROPERTIES_FILE_EXTENSION;
  }

  private static Optional<String> getFileLocale(File file) {
    return LOCALES
      .stream()
      .filter(l -> file.getName().endsWith(l + CheckUtils.JAVA_PROPERTIES_FILE_EXTENSION))
      .findFirst();
  }

}
