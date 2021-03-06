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
package de.monticore.codegen.emf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.ecore.EObject;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.emf.util.AST2ModelFiles;
import de.monticore.emf.util.compare.AstEmfDiffUtility;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.GrammarPackage;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 */
public class GrammarSerDeserTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSerializeDesirializeASTMCGrammarInstance() {
    try {
      
      String path1 = "de/monticore/emf/Automaton.mc4";
      Optional<ASTMCGrammar> automatonGrammar = new Grammar_WithConceptsParser()
          .parse("src/test/resources/" + path1);
      assertTrue(automatonGrammar.isPresent());
      AST2ModelFiles.get().serializeASTInstance(automatonGrammar.get(), "Automaton");
      
      EObject deserAutomatonGrammar = AST2ModelFiles.get().deserializeASTInstance("ASTMCGrammar_Automaton",
          GrammarPackage.eINSTANCE);
      assertNotNull(deserAutomatonGrammar);
      assertTrue(deserAutomatonGrammar instanceof ASTMCGrammar);
      
      assertTrue(automatonGrammar.get().deepEquals(deserAutomatonGrammar));
      assertEquals("Automaton", ((ASTMCGrammar) deserAutomatonGrammar).getName());
      
      List<DiffElement> diffs = AstEmfDiffUtility.getAllAstDiffs(automatonGrammar.get(),
          (ASTMCGrammar) deserAutomatonGrammar);
      assertTrue(diffs.isEmpty());
    }
    catch (RecognitionException | IOException e) {
      fail("Should not reach this, but: " + e);
    }
    catch (InterruptedException e) {
      fail("Should not reach this, but: " + e);
    }
  }
  
//  @Test
//  public void testSerializeDesirializeASTMCGrammarInstance1() {
//    try {
//      
//      String path1 = "de/monticore/emf/Family.mc4";
//      Optional<ASTMCGrammar> automatonGrammar = new Grammar_WithConceptsParser()
//          .parse("src/test/resources/" + path1);
//      assertTrue(automatonGrammar.isPresent());
//      AST2ModelFiles.get().serializeASTInstance(automatonGrammar.get(), "Family");
//      
//      EObject deserAutomatonGrammar = AST2ModelFiles.get().deserializeASTInstance("ASTMCGrammar_Family",
//          GrammarPackage.eINSTANCE);
//      assertNotNull(deserAutomatonGrammar);
//      assertTrue(deserAutomatonGrammar instanceof ASTMCGrammar);
//      
//      assertTrue(automatonGrammar.get().deepEquals(deserAutomatonGrammar));
//      assertEquals("Family", ((ASTMCGrammar) deserAutomatonGrammar).getName());
//      
//      List<DiffElement> diffs = AstEmfDiffUtility.getAllAstDiffs(automatonGrammar.get(),
//          (ASTMCGrammar) deserAutomatonGrammar);
//      assertTrue(diffs.isEmpty());
//    }
//    catch (RecognitionException | IOException e) {
//      fail("Should not reach this, but: " + e);
//    }
//    catch (InterruptedException e) {
//      fail("Should not reach this, but: " + e);
//    }
//  }
  
//  //@Test
//  public void testSerializeDesirializeASTMCGrammarInstance2() {
//    try {
//      
//      String path1 = "de/monticore/emf/Family.mc4";
//      Optional<ASTMCGrammar> automatonGrammar = new Grammar_WithConceptsParser()
//          .parse("src/test/resources/" + path1);
//      assertTrue(automatonGrammar.isPresent());
//      AST2ModelFiles.get().serializeAST(FamilyPackage.eINSTANCE);
//      
//      EObject deserAutomatonGrammar = AST2ModelFiles.get().deserializeASTInstance("Mysample-Families",
//          FamilyPackage.eINSTANCE);
//      assertNotNull(deserAutomatonGrammar);
////      assertTrue(deserAutomatonGrammar instanceof ASTMCGrammar);
////      
////      assertTrue(automatonGrammar.get().deepEquals(deserAutomatonGrammar));
////      assertEquals("Family", ((ASTMCGrammar) deserAutomatonGrammar).getName());
////      
////      List<DiffElement> diffs = AstEmfDiffUtility.getAllAstDiffs(automatonGrammar.get(),
////          (ASTMCGrammar) deserAutomatonGrammar);
////      assertTrue(diffs.isEmpty());
//    }
//    catch (RecognitionException | IOException e) {
//      fail("Should not reach this, but: " + e);
//    }
////    catch (InterruptedException e) {
////      fail("Should not reach this, but: " + e);
////    }
//  }
  
}
