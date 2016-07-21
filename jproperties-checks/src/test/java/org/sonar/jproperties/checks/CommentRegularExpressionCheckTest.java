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

import static org.fest.assertions.Assertions.assertThat;

public class CommentRegularExpressionCheckTest {

  private final static String TEST_FILE_RELATIVE_PATH = "commentRegularExpression.properties";
  private CommentRegularExpressionCheck check = new CommentRegularExpressionCheck();

  @Test
  public void should_match_some_comments_and_raise_issues() {
    String message = "Stop annotating lines with WTF! Detail what is wrong instead.";
    check.regularExpression = "(?i).*WTF.*";
    check.message = "Stop annotating lines with WTF! Detail what is wrong instead.";

    JavaPropertiesCheckVerifier.issues(check, CheckTestUtils.getTestFile(TEST_FILE_RELATIVE_PATH))
      .next().atLine(1).withMessage(message)
      .next().atLine(2).withMessage(message)
      .next().atLine(3).withMessage(message)
      .noMore();
  }

  @Test
  public void should_not_match_any_comments_and_not_raise_issues() {
    check.regularExpression = "blabla";
    check.message = "blabla";

    JavaPropertiesCheckVerifier.issues(check, CheckTestUtils.getTestFile(TEST_FILE_RELATIVE_PATH))
      .noMore();
  }

  @Test
  public void should_throw_an_illegal_state_exception_as_the_regular_expression_parameter_is_not_valid() {
    try {
      check.regularExpression = "(";
      check.message = "blabla";
      JavaPropertiesCheckVerifier.issues(check, CheckTestUtils.getTestFile(TEST_FILE_RELATIVE_PATH)).noMore();
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("Check jproperties:comment-regular-expression (Regular expression on comment): "
        + "regularExpression parameter \"(\" is not a valid regular expression.");
    }
  }

}
