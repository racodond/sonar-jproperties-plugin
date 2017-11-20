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
import org.sonar.jproperties.checks.generic.DuplicatedValuesCheck;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

import static org.fest.assertions.Assertions.assertThat;

public class DuplicatedValuesCheckTest {

  private DuplicatedValuesCheck check = new DuplicatedValuesCheck();

  @Test
  public void should_find_some_duplicated_values_and_raise_issues() {
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("duplicatedValues.properties"));
  }

  @Test
  public void should_find_some_duplicated_values_and_raise_issues_with_void_regular_expression_of_values_to_ignore() {
    check.setValuesToIgnore("");
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("duplicatedValuesCustom1.properties"));
  }

  @Test
  public void should_find_some_duplicated_values_and_raise_issues_with_custom_regular_expression_of_values_to_ignore() {
    check.setValuesToIgnore("(?i)(BLABLA|abc|true|false)");
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("duplicatedValuesCustom2.properties"));
  }

  @Test
  public void should_throw_an_illegal_state_exception_as_the_values_to_ignore_regular_expression_is_not_valid() {
    try {
      check.setValuesToIgnore("(");
      JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("duplicatedValuesCustom2.properties"));
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("Check jproperties:duplicated-values (Different keys having the same value should be merged): "
        + "valuesToIgnore parameter \"(\" is not a valid regular expression.");
    }
  }

}
