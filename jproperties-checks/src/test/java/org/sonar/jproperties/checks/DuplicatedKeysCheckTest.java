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

import java.io.File;

import org.junit.Test;
import org.sonar.jproperties.JavaPropertiesConfiguration;
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

public class DuplicatedKeysCheckTest {

  @Test
  public void should_find_some_duplicated_keys_and_raise_issues() {
    JavaPropertiesCheckVerifier.verify(new DuplicatedKeysCheck(), new File("src/test/resources/checks/duplicatedKeys.properties"));
  }

  @Test
  public void should_find_some_duplicated_keys_and_raise_issues_on_UTF8_with_BOM_file() {
    JavaPropertiesCheckVerifier.verify(
      new DuplicatedKeysCheck(),
      new File("src/test/resources/checks/duplicatedKeysUTF8WithBOM.properties"),
      new JavaPropertiesConfiguration(Charsets.UTF_8));
  }

}
