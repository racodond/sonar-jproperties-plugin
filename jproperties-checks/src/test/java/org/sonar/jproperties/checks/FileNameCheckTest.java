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

import org.junit.Test;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

public class FileNameCheckTest {

  private FileNameCheck check = new FileNameCheck();

  @Test
  public void should_follow_the_default_naming_convention_and_not_raise_an_issue() {
    JavaPropertiesCheckVerifier.issues(check, TestUtils.getTestFile("file-name/fileNameOK.properties"))
      .noMore();
  }

  @Test
  public void should_not_follow_the_default_naming_convention_and_raise_an_issue() {
    JavaPropertiesCheckVerifier.issues(check, TestUtils.getTestFile("file-name/file_name.ko.properties"))
      .next().withMessage("Rename this file to match the regular expression: ^[A-Za-z][-_A-Za-z0-9]*\\.properties$")
      .noMore();
  }

  @Test
  public void should_follow_a_custom_naming_convention_and_not_raise_an_issue() {
    check.setFormat("^[a-z][._a-z]+\\.properties$");
    JavaPropertiesCheckVerifier.issues(check, TestUtils.getTestFile("file-name/file_name.ok.properties"))
      .noMore();
  }

  @Test
  public void should_not_follow_a_custom_naming_convention_and_raise_an_issue() {
    check.setFormat("^[a-z]+\\.properties$");
    JavaPropertiesCheckVerifier.issues(check, TestUtils.getTestFile("file-name/file_name.kocustom.properties"))
      .next().withMessage("Rename this file to match the regular expression: ^[a-z]+\\.properties$")
      .noMore();
  }

  @Test(expected = IllegalStateException.class)
  public void should_throw_an_illegal_state_exception_as_the_format_parameter_regular_expression_is_not_valid() {
    check.setFormat("(");
    JavaPropertiesCheckVerifier.issues(check, TestUtils.getTestFile("file-name/file_name.ok.properties")).noMore();
  }

}
