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
package org.sonar.jproperties.checks.verifier;

import com.google.common.base.Splitter;
import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.JavaPropertiesSquidContext;
import org.sonar.jproperties.issue.Issue;

public class TestIssueCheck extends JavaPropertiesCheck implements AstAndTokenVisitor {

  @Override
  public void visitToken(Token token) {
    for (Trivia trivia : token.getTrivia()) {
      String comment = trivia.getToken().getOriginalValue();
      String text = comment.substring(2).trim();
      String marker = "Noncompliant";

      if (text.startsWith(marker)) {
        TestIssue issue = new TestIssue(trivia.getToken().getLine());
        String paramsAndMessage = text.substring(marker.length()).trim();

        if (paramsAndMessage.startsWith("[")) {
          int endIndex = paramsAndMessage.indexOf("]");
          addParams(issue, paramsAndMessage.substring(1, endIndex));
          paramsAndMessage = paramsAndMessage.substring(endIndex + 1).trim();
        }

        if (paramsAndMessage.startsWith("{")) {
          int endIndex = paramsAndMessage.indexOf("}");
          String message = paramsAndMessage.substring(1, endIndex);
          issue.message(message);
        }

        addIssue(issue);
      }
    }
  }

  public Set<TestIssue> getTestIssues() {
    Set<Issue> issues = ((JavaPropertiesSquidContext) getContext()).getIssues();
    Set<TestIssue> testIssues = new HashSet<>();
    for (Issue issue : issues) {
      testIssues.add((TestIssue) issue);
    }
    return testIssues;
  }

  private static void addParams(TestIssue issue, String params) {
    for (String param : Splitter.on(';').split(params)) {
      int equalIndex = param.indexOf('=');
      if (equalIndex == -1) {
        throw new IllegalStateException("Invalid param at line 1: " + param);
      }
      String name = param.substring(0, equalIndex);
      String value = param.substring(equalIndex + 1);

      if ("effortToFix".equalsIgnoreCase(name)) {
        issue.effortToFix(Integer.valueOf(value));

      } else if ("sc".equalsIgnoreCase(name)) {
        issue.startColumn(Integer.valueOf(value));

      } else if ("ec".equalsIgnoreCase(name)) {
        issue.endColumn(Integer.valueOf(value));

      } else if ("el".equalsIgnoreCase(name)) {
        issue.endLine(lineValue(issue.line(), value));

      } else if ("secondary".equalsIgnoreCase(name)) {
        addSecondaryLines(issue, value);

      } else if ("sl".equalsIgnoreCase(name)) {
        issue.line(lineValue(issue.line(), value));

      } else {
        throw new IllegalStateException("Invalid param at line 1: " + name);
      }
    }
  }

  private static int lineValue(int baseLine, String shift) {
    if (shift.startsWith("+")) {
      return baseLine + Integer.valueOf(shift.substring(1));
    }
    if (shift.startsWith("-")) {
      return baseLine - Integer.valueOf(shift.substring(1));
    }
    return Integer.valueOf(shift);
  }

  private static void addSecondaryLines(TestIssue issue, String value) {
    List<Integer> secondaryLines = new ArrayList<>();
    if (!"".equals(value)) {
      for (String secondary : Splitter.on(',').split(value)) {
        secondaryLines.add(lineValue(issue.line(), secondary));
      }
    }
    issue.secondary(secondaryLines);
  }

}
