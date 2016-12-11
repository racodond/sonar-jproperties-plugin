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
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

import static org.fest.assertions.Assertions.assertThat;

public class HardCodedCredentialsCheckTest {

  @Test
  public void should_find_some_hard_coded_credentials_and_raise_some_issues() {
    JavaPropertiesCheckVerifier.verify(new HardCodedCredentialsCheck(), CheckTestUtils.getTestFile("hard-coded-credentials/hardCodedCredentials.properties"));
  }

  @Test
  public void should_not_find_some_hard_coded_credentials_because_values_to_ignore_are_set() {
    HardCodedCredentialsCheck check = new HardCodedCredentialsCheck();
    check.setEncryptedCredentialsToIgnore("^(ENC\\(|OBF:).+$");
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("hard-coded-credentials/hardCodedCredentialsValuesToIgnore.properties"));
  }

  @Test
  public void should_throw_an_illegal_state_exception_as_the_encryptedCredentialsToIgnore_parameter_is_not_a_valid_regular_expression() {
    try {
      HardCodedCredentialsCheck check = new HardCodedCredentialsCheck();
      check.setEncryptedCredentialsToIgnore("(");

      JavaPropertiesCheckVerifier.issues(check, CheckTestUtils.getTestFile("hard-coded-credentials/hardCodedCredentials.properties")).noMore();

    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("Check jproperties:S2068 (Credentials should not be hard-coded): "
        + "encryptedCredentialsToIgnore parameter \"(\" is not a valid regular expression.");
    }
  }


}
