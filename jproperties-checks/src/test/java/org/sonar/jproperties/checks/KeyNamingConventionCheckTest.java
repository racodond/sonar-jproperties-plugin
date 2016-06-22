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
package org.sonar.jproperties.checks;

import java.io.File;

import org.junit.Test;
import org.sonar.jproperties.JavaPropertiesAstScanner;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

public class KeyNamingConventionCheckTest {

  private KeyNamingConventionCheck check = new KeyNamingConventionCheck();

  @Test
  public void should_find_some_keys_that_do_not_follow_the_default_naming_convention_and_raise_issues() {
    JavaPropertiesCheckVerifier.verify(check, new File("src/test/resources/checks/keyNamingConvention.properties"));
  }

  @Test
  public void should_find_some_keys_that_do_not_follow_a_custom_naming_convention_and_raise_issues() {
    check.setFormat("a.*");
    JavaPropertiesCheckVerifier.verify(check, new File("src/test/resources/checks/keyNamingConventionCustom.properties"));
  }

  @Test(expected = IllegalStateException.class)
  public void should_throw_an_illegal_state_exception_as_the_format_parameter_regular_expression_is_not_valid() {
    check.setFormat("(");
    JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/keyNamingConvention.properties"), check);
  }

}
