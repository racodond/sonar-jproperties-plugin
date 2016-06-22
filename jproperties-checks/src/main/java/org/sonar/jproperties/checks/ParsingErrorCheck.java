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

import com.sonar.sslr.api.RecognitionException;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.squidbridge.AstScannerExceptionHandler;
import org.sonar.squidbridge.annotations.ActivatedByDefault;

@Rule(
  key = "S2260",
  name = "Java Properties parser failure",
  priority = Priority.CRITICAL,
  tags = {Tags.BUG})
@ActivatedByDefault
public class ParsingErrorCheck extends JavaPropertiesCheck implements AstScannerExceptionHandler {

  @Override
  public void processException(Exception e) {
    StringWriter exception = new StringWriter();
    e.printStackTrace(new PrintWriter(exception));  // NOSONAR(squid:S1148): printStackTrace intentionally used
    addIssueOnFile(exception.toString());
  }

  @Override
  public void processRecognitionException(RecognitionException e) {
    addIssue(e.getLine(), e.getMessage());
  }

}
