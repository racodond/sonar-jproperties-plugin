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
package org.sonar.plugins.jproperties;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JavaPropertiesProfileTest {

  @Test
  public void should_create_sonarqube_way_profile() {
    ValidationMessages validation = ValidationMessages.create();
    JavaPropertiesProfile definition = new JavaPropertiesProfile(universalRuleFinder());
    RulesProfile profile = definition.createProfile(validation);

    assertThat(profile.getName()).isEqualTo(JavaPropertiesProfile.SONARQUBE_WAY_PROFILE_NAME);
    assertThat(profile.getLanguage()).isEqualTo(JavaProperties.KEY);
    assertThat(profile.getActiveRulesByRepository(JavaProperties.KEY)).hasSize(16);
    assertThat(validation.hasErrors()).isFalse();
  }

  private RuleFinder universalRuleFinder() {
    RuleFinder ruleFinder = mock(RuleFinder.class);
    when(ruleFinder.findByKey(anyString(), anyString())).thenAnswer(new Answer<Rule>() {
      @Override
      public Rule answer(InvocationOnMock iom) throws Throwable {
        return Rule.create((String) iom.getArguments()[0], (String) iom.getArguments()[1], (String) iom.getArguments()[1]);
      }
    });

    return ruleFinder;
  }

}
