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

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.jproperties.checks.sonarscanner.SonarScannerDeprecatedPropertiesCheck;
import org.sonar.squidbridge.annotations.AnnotationBasedRulesDefinition;

public class SonarScannerJavaPropertiesRulesDefinition implements RulesDefinition {

  public static final String SONAR_SCANNER_REPOSITORY_KEY = "sonarscanner";
  private static final String SONAR_SCANNER_REPOSITORY_NAME = "SonarQube Scanner";

  @Override
  public void define(Context context) {
    NewRepository repository = context
      .createRepository(SONAR_SCANNER_REPOSITORY_KEY, JavaPropertiesLanguage.KEY)
      .setName(SONAR_SCANNER_REPOSITORY_NAME);

    new AnnotationBasedRulesDefinition(repository, JavaPropertiesLanguage.KEY).addRuleClasses(false, getChecks());
    repository.done();
  }

  @SuppressWarnings("rawtypes")
  public static Collection<Class> getChecks() {
    return ImmutableList.<Class>of(
      SonarScannerDeprecatedPropertiesCheck.class);
  }

}
