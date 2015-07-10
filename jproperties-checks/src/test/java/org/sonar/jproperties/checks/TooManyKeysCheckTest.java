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

public class TooManyKeysCheckTest {

  private final String PATH = "src/test/resources/checks/tooManyKeys.properties";
  private TooManyKeysCheck check = new TooManyKeysCheck();

  @Test
  public void should_contain_more_than_30_keys_and_raise_an_issue() {
    check.setNumberKeys(30);
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File(PATH), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).next()
      .withMessage("Reduce the number of keys. The number of keys is 50 greater than 30 authorized.")
      .noMore();
  }

  @Test
  public void should_contain_exactly_50_keys_and_not_raise_an_issue() {
    check.setNumberKeys(50);
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File(PATH), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).noMore();
  }

  @Test
  public void should_contain_fewer_than_200_keys_and_not_raise_an_issue() {
    SourceFile file = JavaPropertiesAstScanner.scanSingleFile(new File(PATH), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).noMore();
  }

}
