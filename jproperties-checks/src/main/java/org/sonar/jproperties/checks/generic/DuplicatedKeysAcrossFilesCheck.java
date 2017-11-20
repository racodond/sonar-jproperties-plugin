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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.checks.LocaleUtils;
import org.sonar.jproperties.checks.Tags;
import org.sonar.plugins.jproperties.api.tree.KeyTree;
import org.sonar.plugins.jproperties.api.tree.PropertiesTree;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "duplicated-keys-across-files",
  name = "Duplicated keys across files should be removed",
  priority = Priority.CRITICAL,
  tags = {Tags.BUG})
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class DuplicatedKeysAcrossFilesCheck extends DoubleDispatchVisitorCheck {

  private boolean fileToCheck = false;
  private Map<String, List<FileKeyTree>> keys = new HashMap<>();

  @Override
  public void visitProperties(PropertiesTree tree) {
    String fileName = getContext().getFile().getName();
    fileToCheck = LocaleUtils.LOCALES.stream().noneMatch(l -> fileName.endsWith("_" + l + ".properties"));
    super.visitProperties(tree);
  }

  @Override
  public void visitKey(KeyTree tree) {
    if (fileToCheck) {
      if (keys.containsKey(tree.text())) {
        keys.get(tree.text()).add(new FileKeyTree(getContext().getFile(), tree));
      } else {
        keys.put(tree.text(), Lists.newArrayList(new FileKeyTree(getContext().getFile(), tree)));
      }
    }
    super.visitKey(tree);
  }

  public Map<String, List<FileKeyTree>> getKeys() {
    return keys;
  }

  @VisibleForTesting
  void setKeys(Map<String, List<FileKeyTree>> keys) {
    this.keys = keys;
  }

}
