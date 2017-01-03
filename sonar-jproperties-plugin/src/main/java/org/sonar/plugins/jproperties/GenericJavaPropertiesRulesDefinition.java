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
package org.sonar.plugins.jproperties;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.jproperties.checks.generic.*;
import org.sonar.squidbridge.annotations.AnnotationBasedRulesDefinition;

public class GenericJavaPropertiesRulesDefinition implements RulesDefinition {

  public static final String GENERIC_REPOSITORY_KEY = "jproperties";
  private static final String GENERIC_REPOSITORY_NAME = "SonarQube";

  @Override
  public void define(Context context) {
    NewRepository repository = context
      .createRepository(GENERIC_REPOSITORY_KEY, JavaPropertiesLanguage.KEY)
      .setName(GENERIC_REPOSITORY_NAME);

    new AnnotationBasedRulesDefinition(repository, JavaPropertiesLanguage.KEY).addRuleClasses(false, getChecks());
    repository.done();
  }

  @SuppressWarnings("rawtypes")
  public static Collection<Class> getChecks() {
    return ImmutableList.of(
      BOMCheck.class,
      CommentConventionCheck.class,
      CommentedOutCodeCheck.class,
      CommentRegularExpressionCheck.class,
      DuplicatedKeysAcrossFilesCheck.class,
      DuplicatedKeysCheck.class,
      DuplicatedValuesCheck.class,
      EmptyElementCheck.class,
      EndLineCharactersCheck.class,
      FileNameCheck.class,
      FixmeTagPresenceCheck.class,
      HardCodedCredentialsCheck.class,
      IndentationCheck.class,
      KeyNamingConventionCheck.class,
      KeyRegularExpressionCheck.class,
      LineLengthCheck.class,
      MissingNewlineAtEndOfFileCheck.class,
      MissingTranslationsCheck.class,
      MissingTranslationsInDefaultCheck.class,
      NoPropertiesCheck.class,
      ParsingErrorCheck.class,
      SeparatorConventionCheck.class,
      TabCharacterCheck.class,
      TodoTagPresenceCheck.class,
      TooManyKeysCheck.class,
      ValueRegularExpressionCheck.class);
  }

}
