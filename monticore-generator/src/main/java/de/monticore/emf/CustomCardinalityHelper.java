/**
 * 
 */
package de.monticore.emf;

import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAttributeInAST;
import de.monticore.grammar.grammar._ast.ASTCard;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

/**
 * The cardinality helper stores the custom cardinality values from the grammar
 * definition. The values are accessible via stereotypes in the AST generator.
 * 
 * @author Nico Jansen
 */
public class CustomCardinalityHelper {
  
  private final static String AST_CD_PREFIX = "AST";
  
  /**
   * Default constructor.
   */
  public CustomCardinalityHelper() {
    
  }
  
  /**
   * Attaches the minimum and maximum cardinalities from the grammar to the
   * CD-AST for cardinality computation.
   * 
   * @param cdCompilationUnit The class diagram.
   * @param grammar The corresponding grammar.
   */
  public void attachCustomCardinalities(ASTCDCompilationUnit cdCompilationUnit, ASTMCGrammar grammar) {
    
    // find custom cardinalities in AST rules
    for (ASTASTRule rule : grammar.getASTRules()) {
      for (ASTNode child : rule.get_Children()) {
        
        if (child instanceof ASTAttributeInAST) {
          ASTAttributeInAST attrInAst = (ASTAttributeInAST) child;
          Optional<ASTCard> astCard = attrInAst.getCard();
          if (astCard.isPresent()) {
            ASTCard cardValues = astCard.get();
            
            // get corresponding attribute in CD
            ASTCDAttribute cdAttribute = findCdAttribute(cdCompilationUnit, attrInAst);
            
            // if CD attribute is available, attach custom cardinality values
            if (cdAttribute != null) {
              if (cardValues.getMin().isPresent()) {
                TransformationHelper.addStereoType(cdAttribute, MC2CDStereotypes.MIN_CARD.toString(), String.valueOf(cardValues.getMin().get()));
              }
              if (cardValues.getMax().isPresent()) {
                TransformationHelper.addStereoType(cdAttribute, MC2CDStereotypes.MAX_CARD.toString(), String.valueOf(cardValues.getMax().get()));
              }
            }
          }
        }
      }
    }
  }
  
  /**
   * Finds the cdAttibute for the corresponding attribute in the grammar.
   * 
   * @param cdCompilationUnit The class diagram.
   * @param attrInAst The AST attribute in the grammar.
   * @return The cdAttribute of the class diagram.
   */
  private ASTCDAttribute findCdAttribute(ASTCDCompilationUnit cdCompilationUnit, ASTAttributeInAST attrInAst) {
    
    // Check if attribute name is available
    if (!attrInAst.getName().isPresent()) {
      return null;
    }
    
    // Check if enclosing scope (class) is available
    if (!attrInAst.getEnclosingScope().isPresent()) {
      return null;
    }
    
    // Check if class name is available
    if (!attrInAst.getEnclosingScope().get().getName().isPresent()) {
      return null;
    }
    
    String attrName = attrInAst.getName().get();
    String className = attrInAst.getEnclosingScope().get().getName().get();
    
    // find attribute in cd
    for (ASTCDClass clazz : cdCompilationUnit.getCDDefinition().getCDClasses()) {
      if (clazz.getName().equals(AST_CD_PREFIX + className)) {
        for (ASTCDAttribute attr : clazz.getCDAttributes()) {
          if (attr.getName().equals(attrName)) {
            return attr;
          }
        }
      }
    }
    return null;
  }
  
}
