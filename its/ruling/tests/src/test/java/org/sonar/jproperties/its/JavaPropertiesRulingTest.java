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
package org.sonar.jproperties.its;

import com.google.common.io.Files;
import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.locator.FileLocation;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonarsource.analyzer.commons.ProfileGenerator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

public class JavaPropertiesRulingTest {

  @ClassRule
  public static Orchestrator orchestrator = Orchestrator.builderEnv()
    .addPlugin(FileLocation.byWildcardMavenFilename(new File("../../../sonar-jproperties-plugin/target"), "sonar-jproperties-plugin-*-SNAPSHOT.jar"))
    .setOrchestratorProperty("litsVersion", "0.6")
    .addPlugin("lits")
    .build();

  @Test
  public void test_iso8859_jproperties_repository() throws Exception {
    ProfileGenerator.RulesConfiguration rulesConfiguration = new ProfileGenerator.RulesConfiguration()
      .add("S2068", "encryptedCredentialsToIgnore", "^(ENC\\(|OBF:).+$")
      .add("maximum-number-keys", "numberKeys", "40");

    Set<String> excludedRules = Collections.emptySet();
    File profile = ProfileGenerator.generateProfile(orchestrator.getServer().getUrl(), "jproperties", "jproperties", rulesConfiguration, excludedRules);
    orchestrator.getServer().restoreProfile(FileLocation.of(profile));

    testProject("iso8859-jproperties", "ISO-8859-1");
  }

  @Test
  public void test_iso8859_sonarscanner_repository() throws Exception {
    ProfileGenerator.RulesConfiguration rulesConfiguration = new ProfileGenerator.RulesConfiguration();
    Set<String> excludedRules = Collections.emptySet();
    File profile = ProfileGenerator.generateProfile(orchestrator.getServer().getUrl(), "jproperties", "sonarscanner", rulesConfiguration, excludedRules);
    orchestrator.getServer().restoreProfile(FileLocation.of(profile));

    testProject("iso8859-sonarscanner", "ISO-8859-1");
  }

  @Test
  public void test_ut8() throws Exception {
    testProject("utf8", "UTF-8");
  }

  private void testProject(String projectKey, String encoding) throws Exception {
    orchestrator.getServer().provisionProject(projectKey, projectKey);
    orchestrator.getServer().associateProjectToQualityProfile(projectKey, "jproperties", "rules");

    SonarScanner build = createScanner(projectKey, encoding);
    orchestrator.executeBuild(build);

    assertThat(Files.toString(FileLocation.of("target/differences-" + projectKey).getFile(), StandardCharsets.UTF_8)).isEmpty();
  }

  private SonarScanner createScanner(String projectKey, String encoding) {
    return SonarScanner.create(FileLocation.of("../projects/" + projectKey).getFile())
      .setProjectKey(projectKey)
      .setLanguage("jproperties")
      .setSourceDirs("./")
      .setSourceEncoding(encoding)
      .setProperty("sonar.analysis.mode", "preview")
      .setProperty("sonar.issuesReport.html.enable", "true")
      .setProperty("dump.old", FileLocation.of("src/test/expected/" + projectKey).getFile().getAbsolutePath())
      .setProperty("dump.new", FileLocation.of("target/actual-" + projectKey).getFile().getAbsolutePath())
      .setProperty("lits.differences", FileLocation.of("target/differences-" + projectKey).getFile().getAbsolutePath())
      .setProperty("sonar.cpd.skip", "true")
      .setProperty("sonar.jproperties.sourceEncoding", encoding);
  }
}
