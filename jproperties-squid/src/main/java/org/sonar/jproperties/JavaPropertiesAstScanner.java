/*
 * SonarQube Java Properties Plugin
 * Copyright (C) 2015 David RACODON
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
package org.sonar.jproperties;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.sonar.sslr.impl.Parser;

import java.io.File;
import java.util.Collection;
import javax.annotation.Nullable;

import org.sonar.jproperties.api.JavaPropertiesMetric;
import org.sonar.jproperties.ast.visitors.SonarComponents;
import org.sonar.jproperties.ast.visitors.SyntaxHighlighterVisitor;
import org.sonar.jproperties.parser.JavaPropertiesGrammar;
import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.SquidAstVisitorContextImpl;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.api.SourceProject;
import org.sonar.squidbridge.indexer.QueryByType;
import org.sonar.squidbridge.metrics.CommentsVisitor;
import org.sonar.squidbridge.metrics.CounterVisitor;
import org.sonar.squidbridge.metrics.LinesOfCodeVisitor;
import org.sonar.squidbridge.metrics.LinesVisitor;
import org.sonar.sslr.parser.LexerlessGrammar;
import org.sonar.sslr.parser.ParserAdapter;

public final class JavaPropertiesAstScanner {

  private JavaPropertiesAstScanner() {
  }

  @VisibleForTesting
  public static SourceFile scanSingleFile(File file, SquidAstVisitor<LexerlessGrammar>... visitors) {
    if (!file.isFile()) {
      throw new IllegalArgumentException("File '" + file + "' not found.");
    }
    AstScanner scanner = create(new JavaPropertiesConfiguration(), null, visitors);
    scanner.scanFile(file);
    Collection<SourceCode> sources = scanner.getIndex().search(new QueryByType(SourceFile.class));
    if (sources.size() != 1) {
      throw new IllegalStateException("Only one SourceFile was expected whereas " + sources.size() + " has been returned.");
    }
    return (SourceFile) sources.iterator().next();
  }

  public static AstScanner<LexerlessGrammar> create(JavaPropertiesConfiguration conf, @Nullable SonarComponents sonarComponents, SquidAstVisitor<LexerlessGrammar>... visitors) {
    final SquidAstVisitorContextImpl<LexerlessGrammar> context = new SquidAstVisitorContextImpl<>(new SourceProject("Java Properties Project"));
    final Parser<LexerlessGrammar> parser = new ParserAdapter(Charsets.ISO_8859_1, JavaPropertiesGrammar.createGrammar());

    AstScanner.Builder<LexerlessGrammar> builder = AstScanner.builder(context).setBaseParser(parser);

    builder.withMetrics(JavaPropertiesMetric.values());

    builder.setCommentAnalyser(new JavaPropertiesCommentAnalyser());
    builder.withSquidAstVisitor(CommentsVisitor.<LexerlessGrammar>builder()
      .withCommentMetric(JavaPropertiesMetric.COMMENT_LINES)
      .withIgnoreHeaderComment(conf.ignoreHeaderComments()).build());

    builder.setFilesMetric(JavaPropertiesMetric.FILES);

    builder.withSquidAstVisitor(CounterVisitor.<LexerlessGrammar>builder()
      .setMetricDef(JavaPropertiesMetric.STATEMENTS)
      .subscribeTo(JavaPropertiesGrammar.PROPERTY)
      .build());

    builder.withSquidAstVisitor(new LinesVisitor<LexerlessGrammar>(JavaPropertiesMetric.LINES));
    builder.withSquidAstVisitor(new LinesOfCodeVisitor<LexerlessGrammar>(JavaPropertiesMetric.LINES_OF_CODE));

    if (sonarComponents != null) {
      builder.withSquidAstVisitor(new SyntaxHighlighterVisitor(sonarComponents));
    }

    for (SquidAstVisitor<LexerlessGrammar> visitor : visitors) {
      builder.withSquidAstVisitor(visitor);
    }

    return builder.build();
  }
}
