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
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

import java.io.File;
import java.util.Collections;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;
import org.sonar.jproperties.checks.verifier.TestIssue;

public class EndLineCharactersCheckTest {

  private EndLineCharactersCheck check = new EndLineCharactersCheck();

  @Test
  public void should_find_only_crlf_and_not_raise_any_issues() throws Exception {
    check.setEndLineCharacters("CRLF");
    JavaPropertiesCheckVerifier.verify(
      check,
      getTestFileWithProperEndLineCharacters("\r\n"),
      Collections.EMPTY_LIST);
  }

  @Test
  public void should_find_only_cr_and_not_raise_any_issues() throws Exception {
    check.setEndLineCharacters("CR");
    JavaPropertiesCheckVerifier.verify(
      check,
      getTestFileWithProperEndLineCharacters("\r"),
      Collections.EMPTY_LIST);
  }

  @Test
  public void should_find_only_lf_and_not_raise_any_issues() throws Exception {
    check.setEndLineCharacters("LF");
    JavaPropertiesCheckVerifier.verify(
      check,
      getTestFileWithProperEndLineCharacters("\n"),
      Collections.EMPTY_LIST);
  }

  @Test
  public void crlf_should_find_lf_and_raise_issues() throws Exception {
    check.setEndLineCharacters("CRLF");
    JavaPropertiesCheckVerifier.verify(
      check,
      getTestFileWithProperEndLineCharacters("\n"),
      ImmutableList.of(new TestIssue(0).message("Set all end-line characters to 'CRLF' in this file.")));
  }

  @Test
  public void crlf_should_find_cr_and_raise_issues() throws Exception {
    check.setEndLineCharacters("CRLF");
    JavaPropertiesCheckVerifier.verify(
      check,
      getTestFileWithProperEndLineCharacters("\r"),
      ImmutableList.of(new TestIssue(0).message("Set all end-line characters to 'CRLF' in this file.")));
  }

  @Test
  public void cr_should_find_crlf_and_raise_issues() throws Exception {
    check.setEndLineCharacters("CR");
    JavaPropertiesCheckVerifier.verify(
      check,
      getTestFileWithProperEndLineCharacters("\r\n"),
      ImmutableList.of(new TestIssue(0).message("Set all end-line characters to 'CR' in this file.")));
  }

  @Test
  public void cr_should_find_lf_and_raise_issues() throws Exception {
    check.setEndLineCharacters("CR");
    JavaPropertiesCheckVerifier.verify(
      check,
      getTestFileWithProperEndLineCharacters("\n"),
      ImmutableList.of(new TestIssue(0).message("Set all end-line characters to 'CR' in this file.")));
  }

  @Test
  public void lf_should_find_crlf_and_raise_issues() throws Exception {
    check.setEndLineCharacters("LF");
    JavaPropertiesCheckVerifier.verify(
      check,
      getTestFileWithProperEndLineCharacters("\r\n"),
      ImmutableList.of(new TestIssue(0).message("Set all end-line characters to 'LF' in this file.")));
  }

  @Test
  public void lf_should_find_cr_and_raise_issues() throws Exception {
    check.setEndLineCharacters("LF");
    JavaPropertiesCheckVerifier.verify(
      check,
      getTestFileWithProperEndLineCharacters("\r"),
      ImmutableList.of(new TestIssue(0).message("Set all end-line characters to 'LF' in this file.")));
  }

  private File getTestFileWithProperEndLineCharacters(String endLineCharacter) throws Exception {
    TemporaryFolder temporaryFolder = new TemporaryFolder();
    File testFile = temporaryFolder.newFile();
    Files.write(
      Files.toString(new File("src/test/resources/checks/endLineCharacters.properties"), Charsets.ISO_8859_1)
        .replaceAll("\\r\\n", "\n")
        .replaceAll("\\r", "\n")
        .replaceAll("\\n", endLineCharacter),
      testFile, Charsets.ISO_8859_1);
    return testFile;
  }

}
