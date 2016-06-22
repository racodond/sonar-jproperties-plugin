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

import com.google.common.collect.ImmutableList;

import java.io.File;

import org.junit.Test;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;
import org.sonar.jproperties.checks.verifier.TestIssue;

public class CommentedOutCodeCheckTest {

  private static final String MESSAGE = "Remove this commented out code.";

  @Test
  public void should_find_commented_out_code_and_raise_issues() {
    JavaPropertiesCheckVerifier.verify(
      new CommentedOutCodeCheck(),
      new File("src/test/resources/checks/commentedOutCode.properties"),
      ImmutableList.of(
        new TestIssue(3).message(MESSAGE),
        new TestIssue(5).message(MESSAGE),
        new TestIssue(7).message(MESSAGE),
        new TestIssue(9).message(MESSAGE),
        new TestIssue(11).message(MESSAGE),
        new TestIssue(13).message(MESSAGE),
        new TestIssue(15).message(MESSAGE),
        new TestIssue(17).message(MESSAGE),
        new TestIssue(19).message(MESSAGE),
        new TestIssue(21).message(MESSAGE),
        new TestIssue(23).message(MESSAGE),
        new TestIssue(25).message(MESSAGE),
        new TestIssue(27).message(MESSAGE),
        new TestIssue(29).message(MESSAGE),
        new TestIssue(31).message(MESSAGE),
        new TestIssue(33).message(MESSAGE),
        new TestIssue(35).message(MESSAGE),
        new TestIssue(37).message(MESSAGE),
        new TestIssue(39).message(MESSAGE),
        new TestIssue(41).message(MESSAGE),
        new TestIssue(43).message(MESSAGE),
        new TestIssue(45).message(MESSAGE),
        new TestIssue(57).message(MESSAGE),
        new TestIssue(60).message(MESSAGE),
        new TestIssue(68).message(MESSAGE)));
  }

}
