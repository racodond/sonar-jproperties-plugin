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

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

import static org.fest.assertions.Assertions.assertThat;

public class EndLineCharactersCheckTest {

  private EndLineCharactersCheck check = new EndLineCharactersCheck();

  @Test
  public void should_find_only_crlf_and_not_raise_any_issues() throws Exception {
    check.setEndLineCharacters("CRLF");
    JavaPropertiesCheckVerifier.issues(check, getTestFileWithProperEndLineCharacters("\r\n"))
      .noMore();
  }

  @Test
  public void should_find_only_cr_and_not_raise_any_issues() throws Exception {
    check.setEndLineCharacters("CR");
    JavaPropertiesCheckVerifier.issues(check, getTestFileWithProperEndLineCharacters("\r"))
      .noMore();
  }

  @Test
  public void should_find_only_lf_and_not_raise_any_issues() throws Exception {
    check.setEndLineCharacters("LF");
    JavaPropertiesCheckVerifier.issues(check, getTestFileWithProperEndLineCharacters("\n"))
      .noMore();
  }

  @Test
  public void crlf_should_find_lf_and_raise_issues() throws Exception {
    check.setEndLineCharacters("CRLF");
    JavaPropertiesCheckVerifier.issues(check, getTestFileWithProperEndLineCharacters("\n"))
      .next().withMessage("Set all end-line characters to 'CRLF' in this file.")
      .noMore();
  }

  @Test
  public void crlf_should_find_cr_and_raise_issues() throws Exception {
    check.setEndLineCharacters("CRLF");
    JavaPropertiesCheckVerifier.issues(check, getTestFileWithProperEndLineCharacters("\r"))
      .next().withMessage("Set all end-line characters to 'CRLF' in this file.")
      .noMore();
  }

  @Test
  public void cr_should_find_crlf_and_raise_issues() throws Exception {
    check.setEndLineCharacters("CR");
    JavaPropertiesCheckVerifier.issues(check, getTestFileWithProperEndLineCharacters("\r\n"))
      .next().withMessage("Set all end-line characters to 'CR' in this file.")
      .noMore();
  }

  @Test
  public void cr_should_find_lf_and_raise_issues() throws Exception {
    check.setEndLineCharacters("CR");
    JavaPropertiesCheckVerifier.issues(check, getTestFileWithProperEndLineCharacters("\n"))
      .next().withMessage("Set all end-line characters to 'CR' in this file.")
      .noMore();
  }

  @Test
  public void lf_should_find_crlf_and_raise_issues() throws Exception {
    check.setEndLineCharacters("LF");
    JavaPropertiesCheckVerifier.issues(check, getTestFileWithProperEndLineCharacters("\r\n"))
      .next().withMessage("Set all end-line characters to 'LF' in this file.")
      .noMore();
  }

  @Test
  public void lf_should_find_cr_and_raise_issues() throws Exception {
    check.setEndLineCharacters("LF");
    JavaPropertiesCheckVerifier.issues(check, getTestFileWithProperEndLineCharacters("\r"))
      .next().withMessage("Set all end-line characters to 'LF' in this file.")
      .noMore();
  }

  @Test
  public void should_throw_an_illegal_state_exception_as_the_endLineCharacters_parameter_is_not_valid() {
    try {
      check.setEndLineCharacters("abc");
      JavaPropertiesCheckVerifier.issues(check, TestUtils.getTestFile("endLineCharacters.properties")).noMore();
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("Check jproperties:end-line-characters (End-line characters should be consistent): "
        + "endLineCharacters parameter is not valid.\nActual: 'abc'\nExpected: 'CR' or 'CRLF' or 'LF'");
    }
  }

  private File getTestFileWithProperEndLineCharacters(String endLineCharacter) throws Exception {
    TemporaryFolder temporaryFolder = new TemporaryFolder();
    File testFile = temporaryFolder.newFile();
    Files.write(
      Files.toString(TestUtils.getTestFile("endLineCharacters.properties"), Charsets.ISO_8859_1)
        .replaceAll("\\r\\n", "\n")
        .replaceAll("\\r", "\n")
        .replaceAll("\\n", endLineCharacter),
      testFile, Charsets.ISO_8859_1);
    return testFile;
  }

}
