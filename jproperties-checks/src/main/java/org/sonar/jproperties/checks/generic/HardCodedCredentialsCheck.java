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
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.jproperties.checks.CheckUtils;
import org.sonar.jproperties.checks.Tags;
import org.sonar.plugins.jproperties.api.tree.PropertyTree;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Rule(
  key = "S2068",
  name = "Credentials should not be hard-coded",
  priority = Priority.CRITICAL,
  tags = {Tags.SECURITY, Tags.CWE, Tags.OWASP_A2, Tags.SANS_TOP25_POROUS})
@SqaleConstantRemediation("30min")
@ActivatedByDefault
public class HardCodedCredentialsCheck extends DoubleDispatchVisitorCheck {

  private static final String DEFAULT_ENCRYPTED_CREDENTIALS_TO_IGNORE = "";

  private static final Pattern HARD_CODED_USERNAME = Pattern.compile(".*(login|username).*", Pattern.CASE_INSENSITIVE);
  private static final Pattern HARD_CODED_PASSWORD = Pattern.compile(".*(password|passwd|pwd).*", Pattern.CASE_INSENSITIVE);

  @RuleProperty(
    key = "encryptedCredentialsToIgnore",
    description = "Regular expression of encrypted credentials to ignore. "
      + "See " + CheckUtils.LINK_TO_JAVA_REGEX_PATTERN_DOC + " for detailed regular expression syntax. "
      + "For example, to ignore encrypted credentials starting with 'ENC(' and 'OBF:', set the parameter to '^(ENC\\(|OBF:).+$'. "
      + "Leave empty if encrypted credentials should not be ignored.",
    defaultValue = DEFAULT_ENCRYPTED_CREDENTIALS_TO_IGNORE)
  private String encryptedCredentialsToIgnore = DEFAULT_ENCRYPTED_CREDENTIALS_TO_IGNORE;

  @Override
  public void visitProperty(PropertyTree tree) {
    if (tree.value() == null) {
      return;
    }

    if (HARD_CODED_USERNAME.matcher(tree.key().text()).matches()
      && ("".equals(encryptedCredentialsToIgnore) || (!"".equals(encryptedCredentialsToIgnore) && !tree.value().text().matches(encryptedCredentialsToIgnore)))) {
      addPreciseIssue(tree, "Remove this hard-coded username.");

    } else if (HARD_CODED_PASSWORD.matcher(tree.key().text()).matches()
      && ("".equals(encryptedCredentialsToIgnore) || (!"".equals(encryptedCredentialsToIgnore) && !tree.value().text().matches(encryptedCredentialsToIgnore)))) {
      addPreciseIssue(tree, "Remove this hard-coded password.");
    }
    super.visitProperty(tree);

  }

  @Override
  public void validateParameters() {
    try {
      Pattern.compile(encryptedCredentialsToIgnore);
    } catch (PatternSyntaxException exception) {
      throw new IllegalStateException(paramErrorMessage(), exception);
    }
  }

  @VisibleForTesting
  void setEncryptedCredentialsToIgnore(String encryptedCredentialsToIgnore) {
    this.encryptedCredentialsToIgnore = encryptedCredentialsToIgnore;
  }

  private String paramErrorMessage() {
    return CheckUtils.paramErrorMessage(
      this.getClass(),
      "encryptedCredentialsToIgnore parameter \"" + encryptedCredentialsToIgnore + "\" is not a valid regular expression.");
  }

}
