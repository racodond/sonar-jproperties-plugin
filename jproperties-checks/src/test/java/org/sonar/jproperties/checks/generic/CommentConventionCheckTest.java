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
import org.sonar.jproperties.checks.generic.CommentConventionCheck;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

import static org.fest.assertions.Assertions.assertThat;

public class CommentConventionCheckTest {

  private CommentConventionCheck check = new CommentConventionCheck();

  @Test
  public void should_only_contain_comments_starting_with_hash_token_and_not_raise_issues() {
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("comment-convention/commentsHashOnly.properties"));
  }

  @Test
  public void should_only_contain_comments_starting_with_exclamation_mark_token_and_not_raise_issues() {
    check.setStartingCommentToken("!");
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("comment-convention/commentsExclamationMarkOnly.properties"));
  }

  @Test
  public void should_contain_comments_starting_with_exclamation_mark_token_and_raise_issues() {
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("comment-convention/commentsHashAndExclamationMark.properties"));
  }

  @Test
  public void should_contain_comments_starting_with_hash_token_and_raise_issues() {
    check.setStartingCommentToken("!");
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("comment-convention/commentsExclamationMarkAndHash.properties"));
  }

  @Test
  public void should_contain_comments_with_missing_whitespace_and_raise_some_issues_hash_token() {
    check.setStartingCommentToken("#");
    JavaPropertiesCheckVerifier.issues(check, CheckTestUtils.getTestFile("comment-convention/commentsHashMissingWhitespace.properties"))
      .next().atLine(2).withMessage("Add a whitespace after the starting comment token.")
      .next().atLine(3).withMessage("Add a whitespace after the starting comment token.")
      .noMore();
  }

  @Test
  public void should_contain_comments_with_missing_whitespace_and_raise_some_issues_exclamation_mark_token() {
    check.setStartingCommentToken("!");
    JavaPropertiesCheckVerifier.issues(check, CheckTestUtils.getTestFile("comment-convention/commentsExclamationMarkMissingWhitespace.properties"))
      .next().atLine(2).withMessage("Add a whitespace after the starting comment token.")
      .next().atLine(3).withMessage("Add a whitespace after the starting comment token.")
      .noMore();
  }

  @Test
  public void should_throw_an_illegal_state_exception_as_the_starting_comment_token_parameter_is_not_valid() {
    try {
      check.setStartingCommentToken("abc");
      JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("comment-convention/commentsHashOnly.properties"));
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("Check jproperties:comment-convention (All comments should be formatted consistently): "
        + "startingCommentToken parameter is not valid.\nActual: 'abc'\nExpected: '#' or '!'");
    }

  }

}
