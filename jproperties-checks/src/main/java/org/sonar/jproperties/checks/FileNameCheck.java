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
import com.sonar.sslr.api.AstNode;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.Nullable;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.squidbridge.annotations.ActivatedByDefault;

@Rule(
  key = "S1578",
  name = "File names should comply with a naming convention",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
@ActivatedByDefault
public class FileNameCheck extends JavaPropertiesCheck {

  public static final String DEFAULT = "^[A-Za-z][-_A-Za-z0-9]*\\.properties$";

  @RuleProperty(
    key = "format",
    defaultValue = DEFAULT,
    description = "Regular expression that file names should match")
  private String format = DEFAULT;

  @Override
  public void init() {
    validateFormatParameter();
  }

  @Override
  public void visitFile(@Nullable AstNode astNode) {
    if (!Pattern.compile(format).matcher(getContext().getFile().getName()).matches()) {
      addIssueOnFile("Rename this file to match this regular expression: " + format);
    }
  }

  @VisibleForTesting
  public void setFormat(String format) {
    this.format = format;
  }

  private void validateFormatParameter() {
    try {
      Pattern.compile(format);
    } catch (PatternSyntaxException exception) {
      throw new IllegalStateException("Check jproperties:S1578 - format parameter \""
        + format + "\" is not a valid regular expression.", exception);
    }
  }

}
