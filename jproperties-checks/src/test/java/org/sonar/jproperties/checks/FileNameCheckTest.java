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

public class FileNameCheckTest {

  private FileNameCheck check = new FileNameCheck();

  @Test
  public void should_follow_the_default_naming_convention_and_not_raise_an_issue() {
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/fileNameOK.properties"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).noMore();
  }

  @Test
  public void should_not_follow_the_default_naming_convention_and_raise_an_issue() {
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/file_name.ko.properties"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).next()
            .withMessage("Rename this file to match this regular expression: \"^[A-Za-z][_A-Za-z0-9]*\\.properties$\"")
            .noMore();
  }

  @Test
  public void should_follow_a_custom_naming_convention_and_not_raise_an_issue() {
    check.setFormat("^[a-z][._a-z]+\\.properties$");
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/file_name.ko.properties"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).noMore();
  }

  @Test
  public void should_not_follow_a_custom_naming_convention_and_raise_an_issue() {
    check.setFormat("^[a-z]+\\.properties$");
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/file_name.ko.properties"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).next()
            .withMessage("Rename this file to match this regular expression: \"^[a-z]+\\.properties$\"")
            .noMore();
  }

}
