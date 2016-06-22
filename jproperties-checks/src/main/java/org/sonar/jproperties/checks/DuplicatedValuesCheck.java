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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.issue.PreciseIssue;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;

@Rule(
  key = "duplicated-values",
  name = "Different keys having the same value should be merged",
  priority = Priority.MAJOR,
  tags = {Tags.PITFALL})
@ActivatedByDefault
public class DuplicatedValuesCheck extends JavaPropertiesCheck {

  @RuleProperty(
    key = "valuesToIgnore",
    description = "Regular expression of values to ignore. See http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html for detailed regular expression syntax.",
    defaultValue = "" + DEFAULT_VALUES_TO_IGNORE)
  private String valuesToIgnore = DEFAULT_VALUES_TO_IGNORE;
  private static final String DEFAULT_VALUES_TO_IGNORE = "(?i)(true|false|yes|no|0|1|-1)";
  private Map<String, List<AstNode>> elementsMap = new HashMap<>();

  @VisibleForTesting
  public void setValuesToIgnore(String valuesToIgnore) {
    this.valuesToIgnore = valuesToIgnore;
  }

  @Override
  public void init() {
    validateValuesToIgnoreParameter();
    subscribeTo(JavaPropertiesGrammar.PROPERTIES, JavaPropertiesGrammar.PROPERTY);
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.is(JavaPropertiesGrammar.PROPERTIES)) {
      elementsMap.clear();
    } else {
      if (node.getFirstChild(JavaPropertiesGrammar.ELEMENT) != null) {
        String value = getValueWithoutLineBreak(node.getFirstChild(JavaPropertiesGrammar.ELEMENT).getTokenValue());
        if (!value.matches(valuesToIgnore)) {
          if (elementsMap.containsKey(value)) {
            elementsMap.get(value).add(node);
          } else {
            elementsMap.put(value, Lists.newArrayList(node));
          }
        }
      }
    }
  }

  @Override
  public void leaveNode(AstNode node) {
    if (node.is(JavaPropertiesGrammar.PROPERTIES)) {
      for (Map.Entry<String, List<AstNode>> entry : elementsMap.entrySet()) {
        if (entry.getValue().size() > 1) {
          PreciseIssue issue = addIssue(
            this,
            "Merge keys \"" + getCommaSeparatedListOfDuplicatedKeys(entry.getValue()) + "\" that have the same value \"" + getFiftyCharacterValue(entry.getKey()) + "\".",
            entry.getValue().get(0).getFirstChild(JavaPropertiesGrammar.KEY));
          for (int i = 1; i < entry.getValue().size(); i++) {
            issue.addSecondaryLocation("Duplicated value", entry.getValue().get(i).getFirstChild(JavaPropertiesGrammar.KEY));
          }
        }
      }
    }
  }

  private static String getCommaSeparatedListOfDuplicatedKeys(List<AstNode> elementNodes) {
    return elementNodes
      .stream()
      .map(n -> n.getFirstChild(JavaPropertiesGrammar.KEY).getTokenValue())
      .sorted()
      .collect(Collectors.joining(", "));
  }

  private static String getFiftyCharacterValue(String value) {
    return value.length() > 50 ? value.substring(0, 50) + "..." : value;
  }

  private static String getValueWithoutLineBreak(String value) {
    return value.replaceAll("\\\\\\n\\s*", "");
  }

  private void validateValuesToIgnoreParameter() {
    try {
      Pattern.compile(valuesToIgnore);
    } catch (PatternSyntaxException exception) {
      throw new IllegalStateException("Check jproperties:duplicated-values - valuesToIgnore parameter \""
        + valuesToIgnore + "\" is not a valid regular expression.", exception);
    }
  }

}
