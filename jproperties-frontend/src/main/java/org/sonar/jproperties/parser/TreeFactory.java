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
package org.sonar.jproperties.parser;

import com.sonar.sslr.api.typed.Optional;

import java.util.List;

import org.sonar.jproperties.tree.impl.*;
import org.sonar.plugins.jproperties.api.tree.*;

public class TreeFactory {

  public PropertiesTree properties(Optional<SyntaxToken> byteOrderMark, Optional<List<PropertyTree>> properties, SyntaxToken eof) {
    return new PropertiesTreeImpl(byteOrderMark.orNull(), properties.orNull(), eof);
  }

  public PropertyTree property(KeyTree key, SeparatorTree separator, Optional<ValueTree> value) {
    return new PropertyTreeImpl(key, separator, value.orNull());
  }

  public KeyTree key(SyntaxToken key) {
    return new KeyTreeImpl(key);
  }

  public ValueTree value(SyntaxToken value) {
    return new ValueTreeImpl(value);
  }

  public SeparatorTree separator(SyntaxToken separator) {
    return new SeparatorTreeImpl(separator);
  }

}
