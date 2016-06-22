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
package org.sonar.jproperties;

import org.junit.Assert;
import org.junit.Test;
import org.sonar.squidbridge.SquidAstVisitorContextImpl;
import org.sonar.squidbridge.api.CheckMessage;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.api.SourceProject;

public class JavaPropertiesCheckTest {

  @Test
  public void add_issue_at_line() {
    JavaPropertiesCheck check = new JavaPropertiesCheck();
    check.setContext(new SquidAstVisitorContextImpl(new SourceProject("testProject")));
    check.getContext().addSourceCode(new SourceFile("abc", "src/abc.properties"));
    check.addIssue(10, "Remove xxx.");

    Assert.assertNotNull(check.getContext().peekSourceCode().getCheckMessages());
    Assert.assertEquals(check.getContext().peekSourceCode().getCheckMessages().size(), 1);

    for (CheckMessage checkMessage : check.getContext().peekSourceCode().getCheckMessages()) {
      Assert.assertEquals(10, checkMessage.getLine().intValue());
      Assert.assertEquals("Remove xxx.", checkMessage.getDefaultMessage());
      Assert.assertNull(checkMessage.getCost());
      Assert.assertNotNull(checkMessage.getSourceCode());
      Assert.assertEquals("src/abc.properties", checkMessage.getSourceCode().getName());
    }
  }

  @Test
  public void add_issue_on_file() {
    JavaPropertiesCheck check = new JavaPropertiesCheck();
    check.setContext(new SquidAstVisitorContextImpl(new SourceProject("testProject")));
    check.getContext().addSourceCode(new SourceFile("abc", "src/abc.properties"));
    check.addIssueOnFile("Remove xxx.");

    Assert.assertNotNull(check.getContext().peekSourceCode().getCheckMessages());
    Assert.assertEquals(check.getContext().peekSourceCode().getCheckMessages().size(), 1);

    for (CheckMessage checkMessage : check.getContext().peekSourceCode().getCheckMessages()) {
      Assert.assertNull(checkMessage.getLine());
      Assert.assertEquals("Remove xxx.", checkMessage.getDefaultMessage());
      Assert.assertNull(checkMessage.getCost());
      Assert.assertNotNull(checkMessage.getSourceCode());
      Assert.assertEquals("src/abc.properties", checkMessage.getSourceCode().getName());
    }
  }

  @Test
  public void add_issue_at_line_with_cost() {
    JavaPropertiesCheck check = new JavaPropertiesCheck();
    check.setContext(new SquidAstVisitorContextImpl(new SourceProject("testProject")));
    check.getContext().addSourceCode(new SourceFile("abc", "src/abc.properties"));
    check.addIssue(2, "Remove xxx.", 10.0);

    Assert.assertNotNull(check.getContext().peekSourceCode().getCheckMessages());
    Assert.assertEquals(check.getContext().peekSourceCode().getCheckMessages().size(), 1);

    for (CheckMessage checkMessage : check.getContext().peekSourceCode().getCheckMessages()) {
      Assert.assertEquals(2, checkMessage.getLine().intValue());
      Assert.assertEquals("Remove xxx.", checkMessage.getDefaultMessage());
      Assert.assertEquals(10.0, checkMessage.getCost().doubleValue(), 0.5);
      Assert.assertNotNull(checkMessage.getSourceCode());
      Assert.assertEquals("src/abc.properties", checkMessage.getSourceCode().getName());
    }
  }

  @Test(expected = IllegalStateException.class)
  public void add_issue_on_non_existing_source_code() {
    JavaPropertiesCheck check = new JavaPropertiesCheck();
    check.setContext(new SquidAstVisitorContextImpl(new SourceProject("testProject")));
    check.addIssue(2, "Remove xxx.");
  }

}
