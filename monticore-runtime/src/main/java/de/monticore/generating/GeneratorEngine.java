/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.generating;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.google.common.base.Joiner;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Represents the whole generator engine component.
 * Clients usually need only this class when generating.
 *
 */
public class GeneratorEngine {

  public final static String GENERATED_CLASS_SUFFIX = "TOP";
  private GeneratorSetup setup;

  public GeneratorEngine(GeneratorSetup generatorSetup) {
    Log.errorIfNull(generatorSetup);
    this.setup = generatorSetup;
  }

// Ziel MB, TODO,  XXX
// anstatt "create" kann man das hier verschieben zur TCC-Builder-Klasse
// die das als default instantiations nutzt, wenn die gebaut wird und
// manche Werte nicht gesetzt sind
//
  /* package visibility TemplateControllerConfiguration createTemplateControllerConfiguration(
      GeneratorSetup generatorSetup, TemplateControllerFactory templateControllerFactory,
      FileReaderWriter fileHandler) {
    if (templateControllerFactory == null) {
      templateControllerFactory = new TemplateControllerFactory();
    }

    if (fileHandler == null) {
      fileHandler = new FileReaderWriter();
    }

    Configuration freemarkerConfig = new FreeMarkerConfigurationBuilder()
        .classLoader(generatorSetup.getClassLoader())
        .additionalTemplatePaths(generatorSetup.getAdditionalTemplatePaths())
        .autoImports(generatorSetup.getAutoTemplateImports())
        .build();

    GlobalExtensionManagement glex = generatorSetup.getGlex();

    FreeMarkerTemplateEngine freeMarkerTemplateEngine = new FreeMarkerTemplateEngine(
        freemarkerConfig);

    TemplateControllerConfiguration tcConfig = new TemplateControllerConfigurationBuilder()
        .glex(glex)
        .templateControllerFactory(templateControllerFactory)
        .classLoader(generatorSetup.getClassLoader())
        .fileHandler(fileHandler)
        .outputDirectory(generatorSetup.getOutputDirectory())
        .freeMarkerTemplateEngine(freeMarkerTemplateEngine)
        .tracing(generatorSetup.isTracing())
        .commentStart(
            generatorSetup.getCommentStart())
        .commentEnd(
            generatorSetup.getCommentEnd())
        .modelName(generatorSetup.getModelName())
        .build();
    
    return tcConfig;
  } */

  /**
   * Processes the template <code>templateName</code> with the given <code>templateArguments</code>
   * and returns the content as String.
   *
   * @param templateName the template to be processes
   * @param templateArguments additional template arguments (if needed).
   */
  public String generate(String templateName,
                         Object... templateArguments) {
    TemplateController tc = setup.getNewTemplateController(templateName);
    return tc.includeArgs(templateName, Arrays.asList(templateArguments));
  }

// TODO (BR, 12/17): Es fehlt eine Methode
// public String generate(String templateName,
//                ASTNode node,
//                Object... templateArguments)

  /**
   * Processes the template <code>templateName</code> with the <code>node</code> and the given
   * <code>templateArguments</code> and writes the content into the <code>filePath</code>. Note:
   * Unless not absolute, the <code>filePath</code> is relative to the configured output directory
   * specified in the {@link de.monticore.generating.GeneratorSetup}.
   *
   * @param templateName the template to be processes
   * @param filePath the file path in which the content is to be written
   * @param node the ast node
   * @param templateArguments additional template arguments (if needed).
   */
  public void generate(String templateName,
      Path filePath,
      ASTNode node,
      Object... templateArguments)
  {
    Log.errorIfNull(node);
    checkArgument(!isNullOrEmpty(templateName));
    Log.errorIfNull(filePath);

    TemplateController tc = setup.getNewTemplateController("");
    tc.writeArgs(templateName, filePath, node, Arrays.asList(templateArguments));
  }

// TODO (BR, 12/17): Diese Methode ist unbenutzt: also raus.
// // Insbesondere weil es nichts bringt einfach den Path zu ändern 
// und dann innendrin einen falschen Klassennamen zu haben.
  /**
   * Processes the template <code>templateName</code> with the <code>node</code> and the given
   * <code>templateArguments</code> and writes the content into the <code>filePath</code>. If there
   * is a handwritten file on the handcoded path, the suffix "TOP" is added to the name of the
   * generated file. Note: Unless not absolute, the <code>filePath</code> is relative to the
   * configured output directory specified in the {@link de.monticore.generating.GeneratorSetup}.
   *
   * @param templateName the template to be processes
   * @param filePath the file path in which the content is to be written
   * @param handcodedPath the path for the handwritten code
   * @param node the ast node
   * @param templateArguments additional template arguments (if needed)
   */
  public
  void generateAndConsiderHWC(String templateName,
  			      Path filePath,
			      IterablePath handcodedPath,
                              ASTNode node,
                              Object... templateArguments)
  {
    Log.errorIfNull(filePath);
    if (handcodedPath.exists(filePath)) {
      Reporting.reportUseHandwrittenCodeFile(handcodedPath.getResolvedPath(filePath).get(),
          filePath);
      filePath = getPathIfHWCExists(filePath);
    }
    else {
      Reporting.reportUseHandwrittenCodeFile(null, filePath);
    }
    generate(templateName, filePath, node, templateArguments);
  }

  /**
   * Adds suffix "TOP" to the name of the file to generate
   *
   * @param filePath
   * @return converted file path
   */
  private Path getPathIfHWCExists(Path filePath) {
    String fileName = filePath.getFileName().toString();
    if (fileName.contains(".")) {
      fileName = Joiner.on('.').join(Names.getQualifier(fileName) + GENERATED_CLASS_SUFFIX,
          Names.getSimpleName(fileName));
    }
    else {
      fileName = fileName + GENERATED_CLASS_SUFFIX;
    }
    return Paths.get(filePath.getParent().toString(), fileName);
  }

}
