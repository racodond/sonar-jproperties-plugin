/*
 * SonarQube Java Properties Analyzer
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
package org.sonar.jproperties.tree.impl;

import com.google.common.collect.Iterators;

import java.util.Iterator;

import org.sonar.plugins.jproperties.api.visitors.DoubleDispatchVisitor;
import org.sonar.plugins.jproperties.api.tree.*;

import javax.annotation.Nullable;

public class PropertyTreeImpl extends JavaPropertiesTree implements PropertyTree {

  private final KeyTree key;
  private final SeparatorTree separator;
  private final ValueTree value;

  public PropertyTreeImpl(KeyTree key, SeparatorTree separator, @Nullable ValueTree value) {
    this.key = key;
    this.separator = separator;
    this.value = value;
  }

  @Override
  public Kind getKind() {
    return Kind.PROPERTY;
  }

  @Override
  public Iterator<Tree> childrenIterator() {
    return Iterators.forArray(key, separator, value);
  }

  @Override
  public KeyTree key() {
    return key;
  }

  @Override
  public SeparatorTree separator() {
    return separator;
  }

  @Override
  @Nullable
  public ValueTree value() {
    return value;
  }

  @Override
  public void accept(DoubleDispatchVisitor visitor) {
    visitor.visitProperty(this);
  }

}
