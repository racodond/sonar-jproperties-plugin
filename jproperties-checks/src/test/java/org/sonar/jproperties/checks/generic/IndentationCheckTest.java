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

import com.google.common.base.Charsets;
import org.junit.Test;
import org.sonar.jproperties.checks.CheckTestUtils;
import org.sonar.jproperties.checks.generic.IndentationCheck;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

public class IndentationCheckTest {

  private IndentationCheck check = new IndentationCheck();

  @Test
  public void should_find_some_keys_that_are_not_properly_indented_and_raise_issues() {
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("indentation/indentation.properties"));
  }

  @Test
  public void should_find_an_issue_on_the_first_line_on_ISO_8859_1_file() {
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("indentation/indentationIssueFirstLine.properties"));
  }

  @Test
  public void should_find_some_keys_that_are_not_properly_indented_and_raise_issues_on_UTF8_file_with_BOM() {
    JavaPropertiesCheckVerifier.verify(
      check,
      CheckTestUtils.getTestFile("indentation/indentationUTF8WithBOM.properties"),
      Charsets.UTF_8);
  }

  @Test
  public void should_find_an_issue_on_the_first_line_on_UTF8_file_with_BOM() {
    JavaPropertiesCheckVerifier.verify(
      check,
      CheckTestUtils.getTestFile("indentation/indentationUTF8WithBOMIssueFirstLine.properties"),
      Charsets.UTF_8);
  }

}
