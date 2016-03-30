/*
 * SonarQube Java Properties Plugin
 * Copyright (C) 2016 David RACODON
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.jproperties.sslr.toolkit;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.sonar.sslr.impl.Parser;
import org.sonar.colorizer.*;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.sslr.parser.ParserAdapter;
import org.sonar.sslr.toolkit.AbstractConfigurationModel;
import org.sonar.sslr.toolkit.ConfigurationProperty;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class JavaPropertiesConfigurationModel extends AbstractConfigurationModel {

  private static final String END_TAG = "</span>";

  @Override
  public Charset getCharset() {
    return Charsets.ISO_8859_1;
  }

  @Override
  public List<ConfigurationProperty> getProperties() {
    return new ArrayList<>();
  }

  @Override
  public Parser doGetParser() {
    return new ParserAdapter<>(getCharset(), JavaPropertiesGrammar.createGrammar());
  }

  @Override
  public List<Tokenizer> doGetTokenizers() {
    return ImmutableList.of(
      new StringTokenizer("<span class=\"s\">", END_TAG),
      new CDocTokenizer("<span class=\"cd\">", END_TAG),
      new JavadocTokenizer("<span class=\"cppd\">", END_TAG),
      new CppDocTokenizer("<span class=\"cppd\">", END_TAG));
  }

}
