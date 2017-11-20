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

import static org.fest.assertions.Assertions.assertThat;

public class SeparatorConventionCheckTest {

  private SeparatorConventionCheck check = new SeparatorConventionCheck();

  @Test
  public void should_find_separators_not_following_equals_convention_and_raise_issues() {
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("separatorConventionEquals.properties"));
  }

  @Test
  public void should_find_separators_not_following_colon_convention_and_raise_issues() {
    check.setSeparator(":");
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("separatorConventionColon.properties"));
  }

  @Test
  public void should_throw_an_illegal_state_exception_as_the_separator_parameter_is_not_valid() {
    try {
      check.setSeparator("abc");
      JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("separatorConventionColon.properties"));
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("Check jproperties:separator-convention (Separators should follow a convention): "
        + "separator parameter is not valid.\nActual: \"abc\"\nExpected: '=' or ':'");
    }
  }

}
