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
package org.sonar.plugins.jproperties;

import org.junit.Test;
import org.sonar.api.config.Settings;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class JavaPropertiesTest {

  @Test
  public void language_key_and_name() {
    JavaProperties javaProperties = new JavaProperties(mock(Settings.class));
    assertThat(javaProperties.getKey()).isEqualTo("jproperties");
    assertThat(javaProperties.getName()).isEqualTo("Java Properties");
  }

  @Test
  public void default_suffixes() {
    JavaProperties css = new JavaProperties(mock(Settings.class));
    assertThat(css.getFileSuffixes()).containsOnly(".properties");
  }

  @Test
  public void custom_suffixes() {
    Settings settings = new Settings();
    settings.setProperty("sonar.javaProperties.file.suffixes", ".foo,bar");

    JavaProperties css = new JavaProperties(settings);
    assertThat(css.getFileSuffixes()).containsOnly(".foo", "bar");
  }

}
