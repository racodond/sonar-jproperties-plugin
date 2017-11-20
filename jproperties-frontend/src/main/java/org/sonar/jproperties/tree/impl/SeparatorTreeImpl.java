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
package org.sonar.jproperties.tree.impl;

import com.google.common.collect.Iterators;
import org.sonar.plugins.jproperties.api.tree.SeparatorTree;
import org.sonar.plugins.jproperties.api.tree.SyntaxToken;
import org.sonar.plugins.jproperties.api.tree.Tree;
import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitor;

import java.util.Iterator;

public class SeparatorTreeImpl extends JavaPropertiesTree implements SeparatorTree {

  private final SyntaxToken separator;
  private final Separator separatorEnum;

  public SeparatorTreeImpl(SyntaxToken separator) {
    this.separator = separator;
    if (separator.text().contains("=")) {
      this.separatorEnum = Separator.EQUALS;
    } else {
      this.separatorEnum = Separator.COLON;
    }
  }

  @Override
  public Kind getKind() {
    return Kind.SEPARATOR;
  }

  @Override
  public Iterator<Tree> childrenIterator() {
    return Iterators.singletonIterator(separator);
  }

  @Override
  public Separator separator() {
    return separatorEnum;
  }

  @Override
  public SyntaxToken separatorToken() {
    return separator;
  }

  @Override
  public String text() {
    return separatorEnum.getValue();
  }

  @Override
  public void accept(DoubleDispatchVisitor visitor) {
    visitor.visitSeparator(this);
  }

}
