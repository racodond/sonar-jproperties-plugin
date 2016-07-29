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
package org.sonar.jproperties.checks.sonarscanner;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.checks.Tags;
import org.sonar.plugins.jproperties.api.tree.KeyTree;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "sonar-scanner-deprecated-properties",
  name = "Deprecated SonarQube properties should not be used",
  priority = Priority.MAJOR,
  tags = {Tags.SONAR_SCANNER, Tags.OBSOLETE})
@SqaleConstantRemediation("10min")
public class SonarScannerDeprecatedPropertiesCheck extends DoubleDispatchVisitorCheck {

  private static final Map<String, String> DEPRECATED_PROPERTIES = ImmutableMap.of(
    "sonar.profile", "Remove this property.",
    "sonar.skipDesign", "Remove this property.",
    "sonar.showProfiling", "Use \"sonar.log.level\" instead.",
    "sonar.binaries", "Use \"sonar.java.binaries\" instead.",
    "sonar.libraries", "Use \"sonar.java.libraries\" instead.");

  @Override
  public void visitKey(KeyTree key) {
    if (DEPRECATED_PROPERTIES.containsKey(key.text())) {
      addPreciseIssue(key, DEPRECATED_PROPERTIES.get(key.text()));
    }
    super.visitKey(key);
  }

}
