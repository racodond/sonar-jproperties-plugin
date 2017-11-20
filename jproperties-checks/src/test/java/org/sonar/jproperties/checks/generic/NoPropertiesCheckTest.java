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
package org.sonar.jproperties.checks.generic;

import org.junit.Test;
import org.sonar.jproperties.checks.CheckTestUtils;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

public class NoPropertiesCheckTest {

  private NoPropertiesCheck check = new NoPropertiesCheck();

  @Test
  public void should_contain_some_properties_and_not_raise_any_issues() {
    JavaPropertiesCheckVerifier.issues(check, CheckTestUtils.getTestFile("no-properties/someProperties.properties"))
      .noMore();
  }

  @Test
  public void should_not_contain_any_properties_and_raise_an_issues() {
    JavaPropertiesCheckVerifier.issues(check, CheckTestUtils.getTestFile("no-properties/noProperties.properties"))
      .next().withMessage("Remove this file that does not define any properties.")
      .noMore();
  }

}
