/*
 * SonarQube Java Properties Plugin
 * Copyright (C) 2016 David RACODON
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

public class DuplicatedKeysCheckTest {

  private final static String MESSAGE = "Remove the duplicated key ";
  private DuplicatedKeysCheck check = new DuplicatedKeysCheck();

  @Test
  public void should_find_some_duplicated_keys_and_raise_issues() {
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/duplicatedKeys.properties"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).next()
      .atLine(2).withMessage(MESSAGE + "\"myproperty\".").next()
      .atLine(4).withMessage(MESSAGE + "\"myproperty2\".").next()
      .atLine(5).withMessage(MESSAGE + "\"myproperty\".").next()
      .atLine(7).withMessage(MESSAGE + "\"my\\=property\".").noMore();
  }

}
