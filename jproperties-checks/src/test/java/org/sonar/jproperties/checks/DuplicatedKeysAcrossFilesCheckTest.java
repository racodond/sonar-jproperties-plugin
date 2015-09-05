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

import com.google.common.collect.Lists;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sonar.jproperties.JavaPropertiesAstScanner;

public class DuplicatedKeysAcrossFilesCheckTest {

  @Test
  public void analyze_one_single_file() {
    DuplicatedKeysAcrossFilesCheck check = new DuplicatedKeysAcrossFilesCheck();
    JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/duplicatedKeysAcrossFiles.properties"), check);
    Assert.assertNotNull(check.getKeys());
    Assert.assertEquals(check.getKeys().size(), 2);
    Assert.assertTrue(check.getKeys().containsKey("myproperty1"));
    Assert.assertNotNull(check.getKeys().get("myproperty1"));
    Assert.assertEquals(check.getKeys().get("myproperty1").size(), 1);
    Assert.assertEquals(check.getKeys().get("myproperty1").get(0).getFile().getPath(), "src/test/resources/checks/duplicatedKeysAcrossFiles.properties");
    Assert.assertEquals(check.getKeys().get("myproperty1").get(0).getLine(), 1);
    Assert.assertTrue(check.getKeys().containsKey("myproperty2"));
    Assert.assertEquals(check.getKeys().get("myproperty2").size(), 1);
    Assert.assertEquals(check.getKeys().get("myproperty2").get(0).getFile().getPath(), "src/test/resources/checks/duplicatedKeysAcrossFiles.properties");
    Assert.assertEquals(check.getKeys().get("myproperty2").get(0).getLine(), 2);
  }

  @Test
  public void analyze_several_files() {
    DuplicatedKeysAcrossFilesCheck check = new DuplicatedKeysAcrossFilesCheck();
    Map<String, List<FileLine>> keys = new HashMap<>();
    keys.put("myproperty1", Lists.newArrayList(new FileLine(new File("src/test/resources/checks/keys.properties"), 2)));
    keys.put("myproperty3", Lists.newArrayList(new FileLine(new File("src/test/resources/checks/keys.properties"), 4)));
    check.setKeys(keys);
    JavaPropertiesAstScanner.scanSingleFile(new File("src/test/resources/checks/duplicatedKeysAcrossFiles.properties"), check);
    Assert.assertNotNull(check.getKeys());
    Assert.assertEquals(check.getKeys().size(), 3);
    Assert.assertTrue(check.getKeys().containsKey("myproperty1"));
    Assert.assertNotNull(check.getKeys().get("myproperty1"));
    Assert.assertEquals(check.getKeys().get("myproperty1").size(), 2);
    Assert.assertEquals(check.getKeys().get("myproperty1").get(0).getFile().getPath(), "src/test/resources/checks/keys.properties");
    Assert.assertEquals(check.getKeys().get("myproperty1").get(0).getLine(), 2);
    Assert.assertEquals(check.getKeys().get("myproperty1").get(1).getFile().getPath(), "src/test/resources/checks/duplicatedKeysAcrossFiles.properties");
    Assert.assertEquals(check.getKeys().get("myproperty1").get(1).getLine(), 1);
    Assert.assertTrue(check.getKeys().containsKey("myproperty3"));
    Assert.assertEquals(check.getKeys().get("myproperty3").size(), 1);
    Assert.assertEquals(check.getKeys().get("myproperty3").get(0).getFile().getPath(), "src/test/resources/checks/keys.properties");
    Assert.assertEquals(check.getKeys().get("myproperty3").get(0).getLine(), 4);
    Assert.assertTrue(check.getKeys().containsKey("myproperty2"));
    Assert.assertEquals(check.getKeys().get("myproperty2").size(), 1);
    Assert.assertEquals(check.getKeys().get("myproperty2").get(0).getFile().getPath(), "src/test/resources/checks/duplicatedKeysAcrossFiles.properties");
    Assert.assertEquals(check.getKeys().get("myproperty2").get(0).getLine(), 2);
  }
}
