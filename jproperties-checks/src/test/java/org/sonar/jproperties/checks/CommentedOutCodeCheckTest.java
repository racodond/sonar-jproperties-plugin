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

public class CommentedOutCodeCheckTest {

  private static final String MESSAGE = "Remove this commented out code.";

  @Test
  public void should_find_commented_out_code_and_raise_issues() {

    JavaPropertiesCheckVerifier.issues(new CommentedOutCodeCheck(), TestUtils.getTestFile("commentedOutCode.properties"))
      .next().atLine(3).withMessage(MESSAGE)
      .next().atLine(5).withMessage(MESSAGE)
      .next().atLine(7).withMessage(MESSAGE)
      .next().atLine(9).withMessage(MESSAGE)
      .next().atLine(11).withMessage(MESSAGE)
      .next().atLine(13).withMessage(MESSAGE)
      .next().atLine(15).withMessage(MESSAGE)
      .next().atLine(17).withMessage(MESSAGE)
      .next().atLine(19).withMessage(MESSAGE)
      .next().atLine(21).withMessage(MESSAGE)
      .next().atLine(23).withMessage(MESSAGE)
      .next().atLine(25).withMessage(MESSAGE)
      .next().atLine(27).withMessage(MESSAGE)
      .next().atLine(29).withMessage(MESSAGE)
      .next().atLine(31).withMessage(MESSAGE)
      .next().atLine(33).withMessage(MESSAGE)
      .next().atLine(35).withMessage(MESSAGE)
      .next().atLine(37).withMessage(MESSAGE)
      .next().atLine(39).withMessage(MESSAGE)
      .next().atLine(41).withMessage(MESSAGE)
      .next().atLine(43).withMessage(MESSAGE)
      .next().atLine(45).withMessage(MESSAGE)
      .next().atLine(57).withMessage(MESSAGE)
      .next().atLine(60).withMessage(MESSAGE)
      .next().atLine(68).withMessage(MESSAGE)
      .noMore();
  }

}
