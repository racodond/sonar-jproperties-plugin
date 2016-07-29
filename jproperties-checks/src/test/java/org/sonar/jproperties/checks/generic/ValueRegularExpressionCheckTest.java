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
package org.sonar.jproperties.checks.generic;

import org.junit.Test;
import org.sonar.jproperties.checks.CheckTestUtils;
import org.sonar.jproperties.checks.generic.ValueRegularExpressionCheck;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

import static org.fest.assertions.Assertions.assertThat;

public class ValueRegularExpressionCheckTest {

  private ValueRegularExpressionCheck check = new ValueRegularExpressionCheck();

  @Test
  public void should_match_some_values_and_raise_issues() {
    check.regularExpression = "^(?s)myvalue.*";
    check.message = "Find out values starting with 'myvalue'";
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("valueRegularExpression.properties"));
  }

  @Test
  public void should_not_match_any_keys_and_not_raise_issues() {
    check.regularExpression = "^blabla.*";
    check.message = "Find out values starting with 'blabla'";
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("valueRegularExpressionNoMatch.properties"));
  }

  @Test
  public void should_throw_an_illegal_state_exception_as_the_regular_expression_parameter_is_not_valid() {
    try {
      check.regularExpression = "(";
      check.message = "blabla";
      JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("valueRegularExpressionNoMatch.properties"));
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("Check jproperties:value-regular-expression (Regular expression on value): "
        + "regularExpression parameter \"(\" is not a valid regular expression.");
    }
  }

}
