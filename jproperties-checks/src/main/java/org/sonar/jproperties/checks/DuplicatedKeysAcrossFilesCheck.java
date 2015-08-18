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
import com.sonar.sslr.api.AstNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
  key = DuplicatedKeysAcrossFilesCheck.RULE_KEY,
  name = "Duplicated keys across files should be removed",
  priority = Priority.CRITICAL,
  tags = {Tags.BUG})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("5min")
public class DuplicatedKeysAcrossFilesCheck extends SquidCheck<LexerlessGrammar> {

  public static final String RULE_KEY = "duplicated-keys-across-files";

  private Map<String, List<FileLine>> keys = new HashMap<>();

  @Override
  public void init() {
    subscribeTo(JavaPropertiesGrammar.KEY);
  }

  @Override
  public void visitNode(AstNode node) {
    if (keys.containsKey(node.getTokenValue())) {
      keys.get(node.getTokenValue()).add(new FileLine(getContext().getFile(), node.getTokenLine()));
    } else {
      keys.put(node.getTokenValue(), Lists.newArrayList(new FileLine(getContext().getFile(), node.getTokenLine())));
    }
  }

  public Map<String, List<FileLine>> getKeys() {
    return keys;
  }

}
