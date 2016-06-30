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

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sonar.jproperties.JavaPropertiesAstScanner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DuplicatedKeysAcrossFilesCheckTest {

  private static final String TEST_DIRECTORY = "src/test/resources/checks/duplicated-keys-across-files/";

  @Test
  public void analyze_one_single_file() {
    DuplicatedKeysAcrossFilesCheck check = new DuplicatedKeysAcrossFilesCheck();
    JavaPropertiesAstScanner.scanSingleFile(new File(TEST_DIRECTORY + "keys.properties"), check);

    Assert.assertNotNull(check.getKeys());
    Assert.assertEquals(2, check.getKeys().size());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate1"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate1"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate1").size());
    Assert.assertEquals(TEST_DIRECTORY + "keys.properties", check.getKeys().get("the.key.to.translate1").get(0).getFile().getPath());
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate1").get(0).getNode().getTokenLine());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate2"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate2"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate2").size());
    Assert.assertEquals(TEST_DIRECTORY + "keys.properties", check.getKeys().get("the.key.to.translate2").get(0).getFile().getPath());
    Assert.assertEquals(2, check.getKeys().get("the.key.to.translate2").get(0).getNode().getTokenLine());

    Assert.assertFalse(check.getKeys().containsKey("the.key.to.translate3"));
  }

  @Test
  public void analyze_several_files() {
    DuplicatedKeysAcrossFilesCheck check = new DuplicatedKeysAcrossFilesCheck();
    Map<String, List<FileNode>> keys = new HashMap<>();

    AstNode node1 = mock(AstNode.class);
    when(node1.getTokenLine()).thenReturn(2);

    AstNode node3 = mock(AstNode.class);
    when(node3.getTokenLine()).thenReturn(4);

    keys.put("the.key.to.translate1", Lists.newArrayList(new FileNode(new File(TEST_DIRECTORY + "keys2.properties"), node1)));
    keys.put("the.key.to.translate3", Lists.newArrayList(new FileNode(new File(TEST_DIRECTORY + "keys2.properties"), node3)));
    check.setKeys(keys);

    JavaPropertiesAstScanner.scanSingleFile(new File(TEST_DIRECTORY + "keys.properties"), check);
    Assert.assertNotNull(check.getKeys());
    Assert.assertEquals(3, check.getKeys().size());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate1"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate1"));
    Assert.assertEquals(2, check.getKeys().get("the.key.to.translate1").size());
    Assert.assertEquals(TEST_DIRECTORY + "keys2.properties", check.getKeys().get("the.key.to.translate1").get(0).getFile().getPath());
    Assert.assertEquals(2, check.getKeys().get("the.key.to.translate1").get(0).getNode().getTokenLine());
    Assert.assertEquals(TEST_DIRECTORY + "keys.properties", check.getKeys().get("the.key.to.translate1").get(1).getFile().getPath());
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate1").get(1).getNode().getTokenLine());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate3"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate3"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate3").size());
    Assert.assertEquals(TEST_DIRECTORY + "keys2.properties", check.getKeys().get("the.key.to.translate3").get(0).getFile().getPath());
    Assert.assertEquals(4, check.getKeys().get("the.key.to.translate3").get(0).getNode().getTokenLine());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate2"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate3"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate2").size());
    Assert.assertEquals(TEST_DIRECTORY + "keys.properties", check.getKeys().get("the.key.to.translate2").get(0).getFile().getPath());
    Assert.assertEquals(2, check.getKeys().get("the.key.to.translate2").get(0).getNode().getTokenLine());
  }

  @Test
  public void analyze_i18n_file_language() {
    DuplicatedKeysAcrossFilesCheck check = new DuplicatedKeysAcrossFilesCheck();
    Map<String, List<FileNode>> keys = new HashMap<>();

    AstNode node1 = mock(AstNode.class);
    when(node1.getTokenLine()).thenReturn(2);

    AstNode node3 = mock(AstNode.class);
    when(node3.getTokenLine()).thenReturn(4);

    keys.put("the.key.to.translate1", Lists.newArrayList(new FileNode(new File(TEST_DIRECTORY + "keys.properties"), node1)));
    keys.put("the.key.to.translate3", Lists.newArrayList(new FileNode(new File(TEST_DIRECTORY + "keys.properties"), node3)));
    check.setKeys(keys);

    JavaPropertiesAstScanner.scanSingleFile(new File(TEST_DIRECTORY + "keys_fr.properties"), check);

    Assert.assertNotNull(check.getKeys());
    Assert.assertEquals(2, check.getKeys().size());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate1"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate1"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate1").size());
    Assert.assertEquals(TEST_DIRECTORY + "keys.properties", check.getKeys().get("the.key.to.translate1").get(0).getFile().getPath());
    Assert.assertEquals(2, check.getKeys().get("the.key.to.translate1").get(0).getNode().getTokenLine());

    Assert.assertFalse(check.getKeys().containsKey("the.key.to.translate2"));

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate3"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate3"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate3").size());
    Assert.assertEquals(TEST_DIRECTORY + "keys.properties", check.getKeys().get("the.key.to.translate3").get(0).getFile().getPath());
    Assert.assertEquals(4, check.getKeys().get("the.key.to.translate3").get(0).getNode().getTokenLine());
  }


  @Test
  public void analyze_i18n_file_language_and_country() {
    DuplicatedKeysAcrossFilesCheck check = new DuplicatedKeysAcrossFilesCheck();
    Map<String, List<FileNode>> keys = new HashMap<>();

    AstNode node1 = mock(AstNode.class);
    when(node1.getTokenLine()).thenReturn(2);

    AstNode node3 = mock(AstNode.class);
    when(node3.getTokenLine()).thenReturn(4);

    keys.put("the.key.to.translate1", Lists.newArrayList(new FileNode(new File(TEST_DIRECTORY + "keys.properties"), node1)));
    keys.put("the.key.to.translate3", Lists.newArrayList(new FileNode(new File(TEST_DIRECTORY + "keys.properties"), node3)));
    check.setKeys(keys);

    JavaPropertiesAstScanner.scanSingleFile(new File(TEST_DIRECTORY + "keys_fr_FR.properties"), check);

    Assert.assertNotNull(check.getKeys());
    Assert.assertEquals(2, check.getKeys().size());

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate1"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate1"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate1").size());
    Assert.assertEquals(TEST_DIRECTORY + "keys.properties", check.getKeys().get("the.key.to.translate1").get(0).getFile().getPath());
    Assert.assertEquals(2, check.getKeys().get("the.key.to.translate1").get(0).getNode().getTokenLine());

    Assert.assertFalse(check.getKeys().containsKey("the.key.to.translate2"));

    Assert.assertTrue(check.getKeys().containsKey("the.key.to.translate3"));
    Assert.assertNotNull(check.getKeys().get("the.key.to.translate3"));
    Assert.assertEquals(1, check.getKeys().get("the.key.to.translate3").size());
    Assert.assertEquals(TEST_DIRECTORY + "keys.properties", check.getKeys().get("the.key.to.translate3").get(0).getFile().getPath());
    Assert.assertEquals(4, check.getKeys().get("the.key.to.translate3").get(0).getNode().getTokenLine());
  }

}
