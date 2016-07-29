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
import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sonar.jproperties.checks.generic.DuplicatedKeysAcrossFilesCheck;
import org.sonar.jproperties.checks.generic.FileKeyTree;
import org.sonar.jproperties.parser.JavaPropertiesParserBuilder;
import org.sonar.jproperties.tree.impl.InternalSyntaxToken;
import org.sonar.jproperties.tree.impl.KeyTreeImpl;
import org.sonar.jproperties.visitors.JavaPropertiesVisitorContext;
import org.sonar.plugins.jproperties.api.JavaPropertiesCheck;
import org.sonar.plugins.jproperties.api.tree.KeyTree;
import org.sonar.plugins.jproperties.api.tree.PropertiesTree;

public class DuplicatedKeysAcrossFilesCheckTest {

  private static final String TEST_DIRECTORY = "duplicated-keys-across-files/";

  @Test
  public void analyze_one_single_file() {
    DuplicatedKeysAcrossFilesCheck check = new DuplicatedKeysAcrossFilesCheck();
    scanFile(check, "keys.properties");

    Assert.assertNotNull(check.getKeys());
    Assert.assertEquals(2, check.getKeys().size());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate1"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate1"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate1").size());
    Assert.assertEquals(getTestFilePath("keys.properties"), check.getKeys().get("the.key.to.translate1").get(0).getFile().getPath());
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate1").get(0).getKey().value().line());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate2"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate2"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate2").size());
    Assert.assertEquals(getTestFilePath("keys.properties"), check.getKeys().get("the.key.to.translate2").get(0).getFile().getPath());
    Assert.assertEquals(2, check.getKeys().get("the.key.to.translate2").get(0).getKey().value().line());

    Assert.assertFalse(check.getKeys().containsKey("the.key.to.translate3"));
  }

  @Test
  public void analyze_several_files() {
    DuplicatedKeysAcrossFilesCheck check = new DuplicatedKeysAcrossFilesCheck();

    Map<String, List<FileKeyTree>> keys = new HashMap<>();

    KeyTree keyTree1 = new KeyTreeImpl(new InternalSyntaxToken(2, 1, "the.key.to.translate3", new ArrayList<>(), false, false));
    KeyTree keyTree3 = new KeyTreeImpl(new InternalSyntaxToken(4, 1, "the.key.to.translate3", new ArrayList<>(), false, false));

    keys.put("the.key.to.translate1", Lists.newArrayList(new FileKeyTree(getTestFile("keys2.properties"), keyTree1)));
    keys.put("the.key.to.translate3", Lists.newArrayList(new FileKeyTree(getTestFile("keys2.properties"), keyTree3)));
    check.setKeys(keys);

    scanFile(check, "keys.properties");

    Assert.assertNotNull(check.getKeys());
    Assert.assertEquals(3, check.getKeys().size());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate1"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate1"));
    Assert.assertEquals(2, check.getKeys().get("the.key.to.translate1").size());
    Assert.assertEquals(getTestFilePath("keys2.properties"), check.getKeys().get("the.key.to.translate1").get(0).getFile().getPath());
    Assert.assertEquals(2, check.getKeys().get("the.key.to.translate1").get(0).getKey().value().line());
    Assert.assertEquals(getTestFilePath("keys.properties"), check.getKeys().get("the.key.to.translate1").get(1).getFile().getPath());
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate1").get(1).getKey().value().line());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate3"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate3"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate3").size());
    Assert.assertEquals(getTestFilePath("keys2.properties"), check.getKeys().get("the.key.to.translate3").get(0).getFile().getPath());
    Assert.assertEquals(4, check.getKeys().get("the.key.to.translate3").get(0).getKey().value().line());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate2"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate3"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate2").size());
    Assert.assertEquals(getTestFilePath("keys.properties"), check.getKeys().get("the.key.to.translate2").get(0).getFile().getPath());
    Assert.assertEquals(2, check.getKeys().get("the.key.to.translate2").get(0).getKey().value().line());
  }

  @Test
  public void analyze_i18n_file_language() {
    DuplicatedKeysAcrossFilesCheck check = new DuplicatedKeysAcrossFilesCheck();
    Map<String, List<FileKeyTree>> keys = new HashMap<>();

    KeyTree keyTree1 = new KeyTreeImpl(new InternalSyntaxToken(2, 1, "the.key.to.translate1", new ArrayList<>(), false, false));
    KeyTree keyTree3 = new KeyTreeImpl(new InternalSyntaxToken(4, 1, "the.key.to.translate3", new ArrayList<>(), false, false));

    keys.put("the.key.to.translate1", Lists.newArrayList(new FileKeyTree(getTestFile("keys.properties"), keyTree1)));
    keys.put("the.key.to.translate3", Lists.newArrayList(new FileKeyTree(getTestFile("keys.properties"), keyTree3)));
    check.setKeys(keys);

    scanFile(check, "keys_fr.properties");

    Assert.assertNotNull(check.getKeys());
    Assert.assertEquals(2, check.getKeys().size());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate1"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate1"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate1").size());
    Assert.assertEquals(getTestFilePath("keys.properties"), check.getKeys().get("the.key.to.translate1").get(0).getFile().getPath());
    Assert.assertEquals(2, check.getKeys().get("the.key.to.translate1").get(0).getKey().value().line());

    Assert.assertFalse(check.getKeys().containsKey("the.key.to.translate2"));

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate3"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate3"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate3").size());
    Assert.assertEquals(getTestFilePath("keys.properties"), check.getKeys().get("the.key.to.translate3").get(0).getFile().getPath());
    Assert.assertEquals(4, check.getKeys().get("the.key.to.translate3").get(0).getKey().value().line());
  }

  @Test
  public void analyze_i18n_file_language_and_country() {
    DuplicatedKeysAcrossFilesCheck check = new DuplicatedKeysAcrossFilesCheck();
    Map<String, List<FileKeyTree>> keys = new HashMap<>();

    KeyTree keyTree1 = new KeyTreeImpl(new InternalSyntaxToken(2, 1, "the.key.to.translate1", new ArrayList<>(), false, false));
    KeyTree keyTree3 = new KeyTreeImpl(new InternalSyntaxToken(4, 1, "the.key.to.translate3", new ArrayList<>(), false, false));

    keys.put("the.key.to.translate1", Lists.newArrayList(new FileKeyTree(getTestFile("keys.properties"), keyTree1)));
    keys.put("the.key.to.translate3", Lists.newArrayList(new FileKeyTree(getTestFile("keys.properties"), keyTree3)));
    check.setKeys(keys);

    scanFile(check, "keys_fr_FR.properties");

    Assert.assertNotNull(check.getKeys());
    Assert.assertEquals(2, check.getKeys().size());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate1"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate1"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate1").size());
    Assert.assertEquals(getTestFilePath("keys.properties"), check.getKeys().get("the.key.to.translate1").get(0).getFile().getPath());
    Assert.assertEquals(2, check.getKeys().get("the.key.to.translate1").get(0).getKey().value().line());

    Assert.assertFalse(check.getKeys().containsKey("the.key.to.translate2"));

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate3"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate3"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate3").size());
    Assert.assertEquals(getTestFilePath("keys.properties"), check.getKeys().get("the.key.to.translate3").get(0).getFile().getPath());
    Assert.assertEquals(4, check.getKeys().get("the.key.to.translate3").get(0).getKey().value().line());
  }

  private void scanFile(JavaPropertiesCheck check, String fileName) {
    PropertiesTree propertiesTree = (PropertiesTree) JavaPropertiesParserBuilder
      .createParser(Charsets.ISO_8859_1)
      .parse(getTestFile(fileName));

    JavaPropertiesVisitorContext context = new JavaPropertiesVisitorContext(propertiesTree, getTestFile(fileName));

    check.scanFile(context);
  }

  private File getTestFile(String fileName) {
    return CheckTestUtils.getTestFile(TEST_DIRECTORY + fileName);
  }

  private String getTestFilePath(String fileName) {
    return CheckTestUtils.getTestFile(TEST_DIRECTORY + fileName).getPath();
  }

}
