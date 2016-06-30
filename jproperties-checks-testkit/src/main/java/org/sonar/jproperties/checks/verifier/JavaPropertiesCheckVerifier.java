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

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import org.sonar.jproperties.JavaPropertiesAstScanner;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.JavaPropertiesConfiguration;
import org.sonar.jproperties.issue.Issue;
import org.sonar.jproperties.issue.PreciseIssue;
import org.sonar.jproperties.issue.PreciseIssueLocation;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

public final class JavaPropertiesCheckVerifier {

  private static final String UNSUPPORTED_TYPE_OF_ISSUE = "Unsupported type of issue.";

  private JavaPropertiesCheckVerifier() {
  }

  public static void verify(JavaPropertiesCheck check, File file) {
    verify(check, file, new JavaPropertiesConfiguration(Charsets.ISO_8859_1));
  }

  public static void verify(JavaPropertiesCheck check, File file, JavaPropertiesConfiguration configuration) {
    TestIssueCheck testIssueCheck = new TestIssueCheck();
    JavaPropertiesAstScanner.scanSingleFileWithCustomConfiguration(file, null, configuration, testIssueCheck);
    verify(check, file, configuration, getExpectedIssues(testIssueCheck));
  }

  public static void verify(JavaPropertiesCheck check, File file, List<TestIssue> expectedIssues) {
    verify(check, file, new JavaPropertiesConfiguration(Charsets.ISO_8859_1), expectedIssues);
  }

  private static void verify(JavaPropertiesCheck check, File file, JavaPropertiesConfiguration configuration, List<TestIssue> expectedIssues) {
    JavaPropertiesAstScanner.scanSingleFileWithCustomConfiguration(file, null, configuration, check);
    Iterator<Issue> actualIssues = getActualIssues(check);

    for (TestIssue expected : expectedIssues) {
      if (actualIssues.hasNext()) {
        verifyIssue(expected, actualIssues.next(), file);
      } else {
        throw new AssertionError(fullMessage("Missing issue at line " + expected.line(), file));
      }
    }

    if (actualIssues.hasNext()) {
      Issue issue = actualIssues.next();
      throw new AssertionError(fullMessage("Unexpected issue at line " + line(issue) + ": \"" + message(issue) + "\"", file));
    }
  }

  private static List<TestIssue> getExpectedIssues(TestIssueCheck check) {
    Set<TestIssue> issues = check.getTestIssues();
    return Ordering.natural().onResultOf(new IssueToLine()).sortedCopy(issues);
  }

  private static Iterator<Issue> getActualIssues(JavaPropertiesCheck check) {
    Set<Issue> issues = check.getIssues();
    List<Issue> sortedIssues = Ordering.natural().onResultOf(new IssueToLine()).sortedCopy(issues);
    return sortedIssues.iterator();
  }

  private static void verifyIssue(TestIssue expected, Issue actual, File file) {
    if (line(actual) > expected.line()) {
      fail(fullMessage("Missing issue at line " + expected.line(), file));
    }
    if (line(actual) < expected.line()) {
      fail(fullMessage("Unexpected issue at line " + line(actual) + ": \"" + message(actual) + "\"", file));
    }
    if (expected.message() != null) {
      assertThat(message(actual)).as(fullMessage("Bad message at line " + expected.line(), file)).isEqualTo(expected.message());
    }
    if (expected.effortToFix() != null) {
      assertThat(((PreciseIssue) actual).getEffortToFix()).as(fullMessage("Bad effortToFix at line " + expected.line(), file)).isEqualTo(expected.effortToFix());
    }
    if (expected.startColumn() != null) {
      assertThat(((PreciseIssue) actual).getPrimaryLocation().getStartColumn() + 1).as(fullMessage("Bad start column at line " + expected.line(), file))
        .isEqualTo(expected.startColumn());
    }
    if (expected.endColumn() != null) {
      assertThat(((PreciseIssue) actual).getPrimaryLocation().getEndColumn() + 1).as(fullMessage("Bad end column at line " + expected.line(), file))
        .isEqualTo(expected.endColumn());
    }
    if (expected.endLine() != null) {
      assertThat(((PreciseIssue) actual).getPrimaryLocation().getEndLine()).as(fullMessage("Bad end line at line " + expected.line(), file)).isEqualTo(expected.endLine());
    }
    if (expected.secondaryLines() != null) {
      assertThat(secondary(actual)).as(fullMessage("Bad secondary locations at line " + expected.line(), file)).isEqualTo(expected.secondaryLines());
    }
  }

  private static class IssueToLine implements Function<Issue, Integer> {
    @Override
    public Integer apply(@Nullable Issue issue) {
      return line(issue);
    }
  }

  private static int line(Issue issue) {
    if (issue instanceof TestIssue) {
      return ((TestIssue) issue).line();
    } else if (issue instanceof PreciseIssue) {
      return ((PreciseIssue) issue).getPrimaryLocation().getStartLine();
    } else {
      throw new IllegalStateException(UNSUPPORTED_TYPE_OF_ISSUE);
    }
  }

  private static String message(Issue issue) {
    if (issue instanceof TestIssue) {
      return ((TestIssue) issue).message();
    } else if (issue instanceof PreciseIssue) {
      return ((PreciseIssue) issue).getPrimaryLocation().getMessage();
    } else {
      throw new IllegalStateException(UNSUPPORTED_TYPE_OF_ISSUE);
    }
  }

  private static List<Integer> secondary(Issue issue) {
    List<Integer> result = new ArrayList<>();
    if (issue instanceof PreciseIssue) {
      for (PreciseIssueLocation issueLocation : ((PreciseIssue) issue).getSecondaryLocations()) {
        result.add(issueLocation.getStartLine());
      }
    } else {
      throw new IllegalStateException(UNSUPPORTED_TYPE_OF_ISSUE);
    }
    return Ordering.natural().sortedCopy(result);
  }

  private static String fullMessage(String message, File file) {
    return message + " on file " + file.getPath();
  }

}
