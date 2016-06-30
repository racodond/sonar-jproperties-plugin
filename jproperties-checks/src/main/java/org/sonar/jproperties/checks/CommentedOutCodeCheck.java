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

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.squidbridge.annotations.ActivatedByDefault;

@Rule(
  key = "commented-out-code",
  name = "Sections of code should not be commented out",
  priority = Priority.MAJOR,
  tags = {Tags.UNUSED})
@ActivatedByDefault
public class CommentedOutCodeCheck extends JavaPropertiesCheck implements AstAndTokenVisitor {

  private static final Pattern commentedOutCodePattern = Pattern.compile("^(#|!){1}[ \\t\\x0B\\f]*(?!(?i)todo)(?!(?i)fixme)([^=:\\s]|(?<=\\\\)\\ |(?<=\\\\)\\=|(?<=\\\\)\\:)+[ \\t\\x0B\\f]*(:|=){1}.*$");
  private List<Integer> commentedOutLines;

  @Override
  public void visitFile(AstNode astNode) {
    commentedOutLines = new ArrayList<>();
  }

  @Override
  public void visitToken(Token token) {
    for (Trivia trivia : token.getTrivia()) {
      String comment = trivia.getToken().getOriginalValue();
      if (commentedOutCodePattern.matcher(comment).matches()) {
        commentedOutLines.add(trivia.getToken().getLine());
      }
    }
  }

  @Override
  public void leaveFile(AstNode astNode) {
    int lastLineIssue = Integer.MIN_VALUE;
    for (Integer line : commentedOutLines) {
      if (line != lastLineIssue + 1) {
        addLineIssue(this, "Remove this commented out code.", line);
      }
      lastLineIssue = line;
    }
  }

}
