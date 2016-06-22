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
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.sonar.sslr.api.AstNode;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.jproperties.JavaPropertiesCheck;

@Rule(
  key = "end-line-characters",
  name = "End-line characters should be consistent",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
public class EndLineCharactersCheck extends JavaPropertiesCheck {

  private static final String DEFAULT_FORMAT = "LF";

  @RuleProperty(
    key = "End-line character",
    description = "Allowed values: 'CR', 'CRLF', 'LF'",
    defaultValue = "" + DEFAULT_FORMAT)
  private String endLineCharacters = DEFAULT_FORMAT;

  @Override
  public void init() {
    validateEndLineCharactersParameter();
  }

  @Override
  public void visitFile(@Nullable AstNode astNode) {
    if (fileContainsIllegalEndLineCharacters()) {
      addFileIssue(this, "Set all end-line characters to '" + endLineCharacters + "' in this file.");
    }
  }

  @VisibleForTesting
  public void setEndLineCharacters(String endLineCharacters) {
    this.endLineCharacters = endLineCharacters;
  }

  private boolean fileContainsIllegalEndLineCharacters() {
    try {
      String fileContent = Files.toString(getContext().getFile(), Charsets.ISO_8859_1);
      return "CR".equals(endLineCharacters) && Pattern.compile("(?s)\n").matcher(fileContent).find()
        || "LF".equals(endLineCharacters) && Pattern.compile("(?s)\r").matcher(fileContent).find()
        || "CRLF".equals(endLineCharacters) && Pattern.compile("(?s)(\r(?!\n)|(?<!\r)\n)").matcher(fileContent).find();
    } catch (IOException e) {
      throw new IllegalStateException("Check jproperties:end-line-characters - File cannot be read.", e);
    }
  }

  private void validateEndLineCharactersParameter() {
    if (!"CRLF".equals(endLineCharacters) && !"CR".equals(endLineCharacters) && !"LF".equals(endLineCharacters)) {
      throw new IllegalStateException("Check jproperties:end-line-characters - End-line character parameter is not valid.\nActual: '"
        + endLineCharacters + "'\nExpected: 'CR' or 'CRLF' or 'LF'");
    }
  }

}
