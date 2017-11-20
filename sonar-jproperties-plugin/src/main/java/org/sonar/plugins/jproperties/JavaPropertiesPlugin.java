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
package org.sonar.plugins.jproperties;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

@Properties({
  @Property(
    key = JavaPropertiesPlugin.JAVA_PROPERTIES_SOURCE_ENCODING_KEY,
    category = "Java Properties",
    defaultValue = JavaPropertiesPlugin.JAVA_PROPERTIES_SOURCE_ENCODING_DEFAULT_VALUE,
    name = "File Encoding",
    description = "Encoding of the .properties files. See the <a target=\"_blank\" href=\"http://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html\">full list of supported encodings</a> (<i>Canonical Name for java.nio API</i> column).",
    global = true, project = true),
})
public class JavaPropertiesPlugin implements Plugin {

  public static final String JAVA_PROPERTIES_SOURCE_ENCODING_KEY = "sonar.jproperties.sourceEncoding";
  public static final String JAVA_PROPERTIES_SOURCE_ENCODING_DEFAULT_VALUE = "ISO-8859-1";

  @Override
  public void define(Context context) {
    context.addExtensions(
      JavaPropertiesLanguage.class,
      JavaPropertiesSquidSensor.class,
      JavaPropertiesProfile.class,
      GenericJavaPropertiesRulesDefinition.class,
      SonarScannerJavaPropertiesRulesDefinition.class);
  }

}
