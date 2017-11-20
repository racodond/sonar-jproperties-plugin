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

public class KeyNamingConventionCheckTest {

  private KeyNamingConventionCheck check = new KeyNamingConventionCheck();

  @Test
  public void should_find_some_keys_that_do_not_follow_the_default_naming_convention_and_raise_issues() {
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("keyNamingConvention.properties"));
  }

  @Test
  public void should_find_some_keys_that_do_not_follow_a_custom_naming_convention_and_raise_issues() {
    check.setFormat("a.*");
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("keyNamingConventionCustom.properties"));
  }

  @Test
  public void should_throw_an_illegal_state_exception_as_the_format_parameter_regular_expression_is_not_valid() {
    try {
      check.setFormat("(");
      JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("keyNamingConventionCustom.properties"));
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("Check jproperties:key-naming-convention (Keys should follow a naming convention): "
        + "format parameter \"(\" is not a valid regular expression.");
    }
  }

}
