/*
 * SonarQube Java Properties Plugin
 * Copyright (C) 2016 David RACODON
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
package org.sonar.jproperties.checks;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

public final class CheckList {

  public static final String REPOSITORY_NAME = "SonarQube";

  private CheckList() {
  }

  @SuppressWarnings("rawtypes")
  public static Collection<Class> getChecks() {
    return ImmutableList.<Class>of(
      CommentConventionCheck.class,
      CommentRegularExpressionCheck.class,
      DuplicatedKeysCheck.class,
      DuplicatedKeysAcrossFilesCheck.class,
      DuplicatedValuesCheck.class,
      EndLineCharactersCheck.class,
      EmptyElementCheck.class,
      FileNameCheck.class,
      FixmeTagPresenceCheck.class,
      HardCodedCredentialsCheck.class,
      IndentationCheck.class,
      KeyNamingConventionCheck.class,
      KeyRegularExpressionCheck.class,
      LineLengthCheck.class,
      MissingNewlineAtEndOfFileCheck.class,
      ParsingErrorCheck.class,
      SeparatorConventionCheck.class,
      TabCharacterCheck.class,
      TodoTagPresenceCheck.class,
      TooManyKeysCheck.class
      );
  }
}
