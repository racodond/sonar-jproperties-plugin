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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.jproperties.api.tree.KeyTree;
import org.sonar.plugins.jproperties.api.tree.PropertiesTree;
import org.sonar.plugins.jproperties.api.tree.PropertyTree;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.plugins.jproperties.api.visitors.issue.PreciseIssue;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "duplicated-values",
  name = "Different keys having the same value should be merged",
  priority = Priority.MAJOR,
  tags = {Tags.PITFALL})
@SqaleConstantRemediation("30min")
@ActivatedByDefault
public class DuplicatedValuesCheck extends DoubleDispatchVisitorCheck {

  private static final String DEFAULT_VALUES_TO_IGNORE = "(?i)(true|false|yes|no|0|1|-1)";
  private final Map<String, List<KeyTree>> valuesMap = new HashMap<>();

  @RuleProperty(
    key = "valuesToIgnore",
    description = "Regular expression of values to ignore. See http://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html for detailed regular expression syntax.",
    defaultValue = DEFAULT_VALUES_TO_IGNORE)
  private String valuesToIgnore = DEFAULT_VALUES_TO_IGNORE;

  @VisibleForTesting
  public void setValuesToIgnore(String valuesToIgnore) {
    this.valuesToIgnore = valuesToIgnore;
  }

  @Override
  public void visitProperties(PropertiesTree tree) {
    valuesMap.clear();
    super.visitProperties(tree);
    for (Map.Entry<String, List<KeyTree>> entry : valuesMap.entrySet()) {
      if (entry.getValue().size() > 1) {
        PreciseIssue issue = addPreciseIssue(
          entry.getValue().get(0),
          "Merge keys \"" + getCommaSeparatedListOfDuplicatedKeys(entry.getValue()) + "\" that have the same value \"" + getFiftyCharacterValue(entry.getKey()) + "\".");
        for (int i = 1; i < entry.getValue().size(); i++) {
          issue.secondary(entry.getValue().get(i), "Duplicated value");
        }
      }
    }
  }

  @Override
  public void visitProperty(PropertyTree tree) {
    if (tree.value() != null) {
      String value = getValueWithoutLineBreak(tree.value().text());
      if (!value.matches(valuesToIgnore)) {
        if (valuesMap.containsKey(value)) {
          valuesMap.get(value).add(tree.key());
        } else {
          valuesMap.put(value, Lists.newArrayList(tree.key()));
        }
      }
    }
  }

  @Override
  public void validateParameters() {
    try {
      Pattern.compile(valuesToIgnore);
    } catch (PatternSyntaxException exception) {
      throw new IllegalStateException(
        "Check jproperties:" + this.getClass().getAnnotation(Rule.class).key() + " ("
          + this.getClass().getAnnotation(Rule.class).name() + "): valuesToIgnore parameter \""
          + valuesToIgnore + "\" is not a valid regular expression.",
        exception);
    }
  }

  private static String getCommaSeparatedListOfDuplicatedKeys(List<KeyTree> keyTrees) {
    return keyTrees
      .stream()
      .map(KeyTree::text)
      .sorted()
      .collect(Collectors.joining(", "));
  }

  private static String getFiftyCharacterValue(String value) {
    return value.length() > 50 ? value.substring(0, 50) + "..." : value;
  }

  private static String getValueWithoutLineBreak(String value) {
    return value.replaceAll("\\\\\\n\\s*", "");
  }

}
