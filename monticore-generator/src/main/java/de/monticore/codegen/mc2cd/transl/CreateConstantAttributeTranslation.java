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

package de.monticore.codegen.mc2cd.transl;

import java.util.Optional;
import java.util.function.UnaryOperator;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.Link;
import de.se_rwth.commons.logging.Log;

public class CreateConstantAttributeTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(
        ASTClassProd.class,
        ASTCDClass.class)) {
      createConstantAttributes(link);
    }
    
    return rootLink;
  }
  
  // TODO SO <- GV : please change and move to the ConstantTypeTranslation
  private void createConstantAttributes(Link<ASTClassProd, ASTCDClass> link) {
    Optional<MCProdSymbol> typeProd = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(link.source()).get()
        .getSpannedScope()
        .resolve(link.source().getName(), MCProdSymbol.KIND);
    if (!typeProd.isPresent()) {
      Log.debug("Unknown type of the grammar rule "
          + link.source().getName() + " in the grammar "
          + MCGrammarSymbolTableHelper.getMCGrammarSymbol(link.source()).get()
              .getFullName()
          + "\n Check if this a kind of rule A:B=... ",
          CreateConstantAttributeTranslation.class.getName());
      return;
    }
    
    MCProdSymbol prodSymbol = typeProd.get();
    for (MCProdComponentSymbol prodComponent : prodSymbol.getProdComponents()) {
      if (prodComponent.isConstantGroup() && prodComponent.getAstNode().isPresent()
          && prodComponent.getAstNode().get() instanceof ASTConstantGroup) {
        boolean iterated = MCGrammarSymbolTableHelper.isConstGroupIterated(prodComponent);
        ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory
            .createASTCDAttribute();
        cdAttribute
            .setName(MCGrammarSymbolTableHelper.getConstantName(prodComponent).orElse(""));
        int constantType = iterated ? ASTConstantsTypes.INT : ASTConstantsTypes.BOOLEAN;
        cdAttribute.setType(TypesNodeFactory
            .createASTPrimitiveType(constantType));
        link.target().getCDAttributes().add(cdAttribute);
      }
    }
  }
  
}
