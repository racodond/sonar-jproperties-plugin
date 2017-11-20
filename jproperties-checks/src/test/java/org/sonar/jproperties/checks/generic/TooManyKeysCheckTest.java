/*
 * SonarQube Java Properties Analyzer
 * Copyright (C) 2015-2017 David RACODON
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
import org.sonar.jproperties.checks.verifier.JavaPropertiesCheckVerifier;

public class TooManyKeysCheckTest {

  private TooManyKeysCheck check = new TooManyKeysCheck();

  @Test
  public void should_contain_more_than_30_keys_and_raise_an_issue() {
    check.setNumberKeys(30);
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("tooManyKeys30.properties"));
  }

  @Test
  public void should_contain_exactly_50_keys_and_not_raise_an_issue() {
    check.setNumberKeys(50);
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("tooManyKeys50.properties"));
  }

  @Test
  public void should_contain_fewer_than_200_keys_and_not_raise_an_issue() {
    JavaPropertiesCheckVerifier.verify(check, CheckTestUtils.getTestFile("tooManyKeys50.properties"));
  }

}
