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

import org.junit.Test;
import org.sonar.jproperties.JavaPropertiesAstScanner;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.CheckMessagesVerifier;

public class SeparatorConventionCheckTest {

  private SeparatorConventionCheck check = new SeparatorConventionCheck();

  @Test
  public void should_find_separators_not_following_equals_convention_and_raise_issues() {
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/separatorConventionEquals.properties"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(3).withMessage("Use '=' as separator instead.")
      .next().atLine(4).withMessage("Remove the whitespaces between the key and the separator.")
      .next().atLine(5).withMessage("Remove the whitespaces between the separator and the value.")
      .next().atLine(6).withMessage("Use '=' as separator instead.")
      .noMore();
  }

  @Test
  public void should_find_separators_not_following_colon_convention_and_raise_issues() {
    check.setSeparator(":");
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/separatorConventionColon.properties"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(3).withMessage("Use ':' as separator instead.")
      .next().atLine(4).withMessage("Add a whitespace between the separator and the value.")
      .next().atLine(4).withMessage("Remove the whitespaces between the key and the separator.")
      .next().atLine(5).withMessage("Leave one single whitespace between the separator and the value.")
      .next().atLine(7).withMessage("Use ':' as separator instead.")
      .noMore();
  }

  @Test
  public void should_not_check_anything_if_the_separator_format_parameter_is_not_valid_file_with_colons() {
    check.setSeparator("abc");
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/separatorConventionColon.properties"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).noMore();
  }

  @Test
  public void should_not_check_anything_if_the_separator_format_parameter_is_not_valid_file_with_equals() {
    check.setSeparator("abc");
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/separatorConventionEquals.properties"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).noMore();
  }

}
