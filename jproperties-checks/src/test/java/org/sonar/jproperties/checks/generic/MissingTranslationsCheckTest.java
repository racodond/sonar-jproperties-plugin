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
package org.sonar.jproperties.checks.generic;

import com.google.common.base.Charsets;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.sonar.jproperties.checks.CheckTestUtils;
import org.sonar.jproperties.parser.JavaPropertiesParserBuilder;
import org.sonar.jproperties.visitors.JavaPropertiesVisitorContext;
import org.sonar.plugins.jproperties.api.JavaPropertiesCheck;
import org.sonar.plugins.jproperties.api.tree.PropertiesTree;

public class MissingTranslationsCheckTest {

  @Test
  public void test() {
    MissingTranslationsCheck check = new MissingTranslationsCheck();
    File file = CheckTestUtils.getTestFile("missing-translations/message.properties");
    scanFile(check, file);

    Assert.assertNotNull(check.getFileKeys());
    Assert.assertEquals(1, check.getFileKeys().size());
    Assert.assertEquals(3, check.getFileKeys().get(file).size());
    Assert.assertTrue(check.getFileKeys().get(file).contains("myproperty0"));
    Assert.assertTrue(check.getFileKeys().get(file).contains("myproperty1"));
    Assert.assertTrue(check.getFileKeys().get(file).contains("myproperty2"));
  }

  private void scanFile(JavaPropertiesCheck check, File file) {
    PropertiesTree propertiesTree = (PropertiesTree) JavaPropertiesParserBuilder
      .createParser(Charsets.ISO_8859_1)
      .parse(file);

    JavaPropertiesVisitorContext context = new JavaPropertiesVisitorContext(propertiesTree, file);

    check.scanFile(context);
  }

}
