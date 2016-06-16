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
package org.sonar.jproperties.checks;

/**
 * Created by pbshiva on 6/16/2016.
 */

import com.sonar.sslr.api.AstNode;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.jproperties.JavaPropertiesCheck;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Rule(key = "ip-address",
        name = "IP address hardcoded in properties",
        priority = Priority.MAJOR,
        tags = {Tags.CONVENTION})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class IPAddressCheck extends JavaPropertiesCheck {

    private static final String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private static final String NOT_AN_IP = "NOT_AN_IP";

    private String isIP(AstNode firstChild) {
        //System.out.println("Checking if IP " + firstChild.getTokenValue().trim());
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(firstChild.getTokenValue().trim());
        if (matcher.find()) {
            return matcher.group();
        } else {
            return NOT_AN_IP;
        }

    }

    @Override
    public void init() {
        subscribeTo(JavaPropertiesGrammar.PROPERTY);
    }

    @Override
    public void visitNode(AstNode astNode) {
        String ip = isIP(astNode.getFirstChild(JavaPropertiesGrammar.ELEMENT));
        if (!ip.equals(NOT_AN_IP)) {
            addIssue(astNode, ip + " - ip address hardcoded, use domain name instead");
        }
        //else {
        //    System.out.println("Not an IP");
        //}
        super.visitNode(astNode);
    }

}