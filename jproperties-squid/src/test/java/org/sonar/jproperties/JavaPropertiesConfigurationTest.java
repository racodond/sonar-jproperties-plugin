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
package org.sonar.jproperties;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class JavaPropertiesConfigurationTest {

  @Test
  public void ignoreHeaderComments() {
    JavaPropertiesConfiguration conf = new JavaPropertiesConfiguration();

    assertThat(conf.ignoreHeaderComments()).isFalse();

    conf.ignoreHeaderComments(true);
    assertThat(conf.ignoreHeaderComments()).isTrue();

    conf.ignoreHeaderComments(false);
    assertThat(conf.ignoreHeaderComments()).isFalse();
  }

}
