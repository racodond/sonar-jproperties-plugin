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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;

import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = DuplicatedKeysAcrossFilesCheck.RULE_KEY,
  name = "Duplicated keys across files should be removed",
  priority = Priority.CRITICAL,
  tags = {Tags.BUG})
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class DuplicatedKeysAcrossFilesCheck extends JavaPropertiesCheck {

  public static final String RULE_KEY = "duplicated-keys-across-files";

  private static final Set<String> LOCALES = Arrays.asList(DateFormat.getAvailableLocales())
    .stream()
    .map(Object::toString)
    .collect(Collectors.toSet());

  private boolean fileToCheck = false;
  private Map<String, List<FileNode>> keys = new HashMap<>();

  @Override
  public void init() {
    subscribeTo(JavaPropertiesGrammar.KEY);
  }

  @Override
  public void visitFile(@Nullable AstNode astNode) {
    String fileName = getContext().getFile().getName();
    fileToCheck = LOCALES.stream().noneMatch(l -> fileName.endsWith("_" + l + ".properties"));
  }

  @Override
  public void visitNode(AstNode node) {
    if (fileToCheck) {
      if (keys.containsKey(node.getTokenValue())) {
        keys.get(node.getTokenValue()).add(new FileNode(getContext().getFile(), node));
      } else {
        keys.put(node.getTokenValue(), Lists.newArrayList(new FileNode(getContext().getFile(), node)));
      }
    }
  }

  public Map<String, List<FileNode>> getKeys() {
    return keys;
  }

  @VisibleForTesting
  public void setKeys(Map<String, List<FileNode>> keys) {
    this.keys = keys;
  }

}
