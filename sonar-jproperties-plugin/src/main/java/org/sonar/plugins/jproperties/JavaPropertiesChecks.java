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
package org.sonar.plugins.jproperties;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.jproperties.api.CustomJavaPropertiesRulesDefinition;
import org.sonar.plugins.jproperties.api.JavaPropertiesCheck;
import org.sonar.plugins.jproperties.api.visitors.TreeVisitor;

public class JavaPropertiesChecks {

  private final CheckFactory checkFactory;
  private Set<Checks<JavaPropertiesCheck>> checksByRepository = Sets.newHashSet();

  private JavaPropertiesChecks(CheckFactory checkFactory) {
    this.checkFactory = checkFactory;
  }

  public static JavaPropertiesChecks createJavaPropertiesChecks(CheckFactory checkFactory) {
    return new JavaPropertiesChecks(checkFactory);
  }

  public JavaPropertiesChecks addChecks(String repositoryKey, Iterable<Class> checkClass) {
    checksByRepository.add(checkFactory
      .<JavaPropertiesCheck>create(repositoryKey)
      .addAnnotatedChecks(checkClass));

    return this;
  }

  public JavaPropertiesChecks addCustomChecks(@Nullable CustomJavaPropertiesRulesDefinition[] customRulesDefinitions) {
    if (customRulesDefinitions != null) {

      for (CustomJavaPropertiesRulesDefinition rulesDefinition : customRulesDefinitions) {
        addChecks(rulesDefinition.repositoryKey(), Lists.newArrayList(rulesDefinition.checkClasses()));
      }
    }
    return this;
  }

  public List<JavaPropertiesCheck> all() {
    List<JavaPropertiesCheck> allVisitors = Lists.newArrayList();

    for (Checks<JavaPropertiesCheck> checks : checksByRepository) {
      allVisitors.addAll(checks.all());
    }

    return allVisitors;
  }

  public List<TreeVisitor> visitorChecks() {
    List<TreeVisitor> checks = new ArrayList<>();
    for (JavaPropertiesCheck check : all()) {
      if (check instanceof TreeVisitor) {
        checks.add((TreeVisitor) check);
      }
    }

    return checks;
  }

  @Nullable
  public RuleKey ruleKeyFor(JavaPropertiesCheck check) {
    RuleKey ruleKey;

    for (Checks<JavaPropertiesCheck> checks : checksByRepository) {
      ruleKey = checks.ruleKey(check);

      if (ruleKey != null) {
        return ruleKey;
      }
    }
    return null;
  }

}
