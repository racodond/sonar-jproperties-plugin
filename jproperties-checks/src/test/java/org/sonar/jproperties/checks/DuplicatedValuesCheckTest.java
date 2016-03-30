/*
 * SonarQube Java Properties Plugin
 * Copyright (C) 2015 David RACODON
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.jproperties.checks;

import java.io.File;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;
import org.sonar.jproperties.JavaPropertiesAstScanner;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.CheckMessagesVerifier;

public class DuplicatedValuesCheckTest {

  @Test
  public void should_find_some_duplicated_values_and_raise_issues() {
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(
      new File("src/test/resources/checks/duplicatedValues.properties"),
      new DuplicatedValuesCheck());
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(3).withMessage("Merge keys \"key1, key2, key3\" that have the same value \"blabla\".")
      .next().atLine(10).withMessage("Merge keys \"long1, long2, cut1, cut2\" that have the same value \"SonarQube is an open platform to manage code quali...\".")
      .noMore();
  }

  @Test
  public void should_find_some_duplicated_values_and_raise_issues_with_void_regular_expression_of_values_to_ignore() {
    DuplicatedValuesCheck check = new DuplicatedValuesCheck();
    check.setValuesToIgnore("");
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/duplicatedValues.properties"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(3).withMessage("Merge keys \"key1, key2, key3\" that have the same value \"blabla\".")
      .next().atLine(10).withMessage("Merge keys \"long1, long2, cut1, cut2\" that have the same value \"SonarQube is an open platform to manage code quali...\".")
      .next().atLine(29).withMessage("Merge keys \"myprop1, myprop2\" that have the same value \"true\".")
      .noMore();
  }

  @Test
  public void should_find_some_duplicated_values_and_raise_issues_with_custom_regular_expression_of_values_to_ignore() {
    DuplicatedValuesCheck check = new DuplicatedValuesCheck();
    check.setValuesToIgnore("(?i)(BLABLA|abc|true|false)");
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/duplicatedValues.properties"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(10).withMessage("Merge keys \"long1, long2, cut1, cut2\" that have the same value \"SonarQube is an open platform to manage code quali...\".")
      .noMore();
  }

  @Test(expected=IllegalStateException.class)
  public void should_throw_an_illegal_state_exception_as_the_values_to_ignore_regular_expression_is_not_valid() {
    DuplicatedValuesCheck check = new DuplicatedValuesCheck();
    check.setValuesToIgnore("(");
    JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/duplicatedValues.properties"), check);
  }

}
