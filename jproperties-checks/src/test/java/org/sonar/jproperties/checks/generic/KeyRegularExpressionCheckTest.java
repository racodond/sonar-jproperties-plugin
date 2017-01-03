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
package org.sonar.jproperties.checks.generic;

import org.junit.Test;
import org.sonar.jproperties.checks.CheckTestUtils;
import org.sonar.jproperties.checks.generic.KeyRegularExpressionCheck;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

import static org.fest.assertions.Assertions.assertThat;

public class KeyRegularExpressionCheckTest {

  private KeyRegularExpressionCheck check = new KeyRegularExpressionCheck();

  @Test
  public void should_match_some_keys_and_raise_issues() {
    check.regularExpression = "^mykey.*";
    check.message = "Find out keys starting with 'mykey'";
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("keyRegularExpression.properties"));
  }

  @Test
  public void should_not_match_any_keys_and_not_raise_issues() {
    check.regularExpression = "^blabla.*";
    check.message = "Find out keys starting with 'blabla'";
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("keyRegularExpressionNoMatch.properties"));
  }

  @Test
  public void should_throw_an_illegal_state_exception_as_the_regular_expression_parameter_is_not_valid() {
    try {
      check.regularExpression = "(";
      check.message = "blabla";
      JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("keyRegularExpressionNoMatch.properties"));
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("Check jproperties:key-regular-expression (Regular expression on key): "
        + "regularExpression parameter \"(\" is not a valid regular expression.");
    }
  }

}
