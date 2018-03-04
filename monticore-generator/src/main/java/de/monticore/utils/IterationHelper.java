/**
 * 
 */
package de.monticore.utils;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTRuleComponent;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

/**
 * The cardinality helper stores the original cardinality values from the
 * parser. So the values are still accessible in the AST generator.
 * 
 * @author Nico Jansen
 */
public class IterationHelper {
  
  
  /**
   * Standard constructor.
   */
  public IterationHelper() {
    
  }
  
  /**
   * Finds the AST class node in a grammar by name.
   * 
   * @param grammar The grammar that contains the AST node.
   * @param className The name of the class for the AST node.
   * @return The corresponding AST class node.
   */
  private ASTNode findClass(ASTMCGrammar grammar, String className) {
    for (ASTNode clazz : grammar.get_Children()) {
      if (className.equals("AST" + clazz.toString())) {
        return clazz;
      }
    }
    return null;
  }
  
  /**
   * Finds the corresponding grammar component for an attribute.
   * 
   * @param grammar The grammar that contains the component.
   * @param className The name of the AST node that contains the component.
   * @param attribute The attribute for which the component needs to be found.
   * @return The corrsponding grammar component.
   */
  private ASTRuleComponent findComponent(ASTMCGrammar grammar, String className, ASTCDAttribute attribute) {
    ASTNode clazz = findClass(grammar, className);
    if (clazz == null) {
      return null;
    }
    
    for (ASTNode attr : clazz.get_Children()) {
      if (attr instanceof ASTAlt) {
        ASTAlt alt = (ASTAlt) attr;
        
        // traverse components
        for (ASTRuleComponent comp : alt.getComponents()) {
          // comp is instance of ASTTerminal
          if (comp instanceof ASTTerminal) {
            ASTTerminal terminal = (ASTTerminal) comp;
            String compName = terminal.getName();
            if (terminal.getUsageName().isPresent()) {
              compName = terminal.getUsageName().get();
            }
            else {
              compName = compName.substring(0, 1).toLowerCase() + compName.substring(1);
            }
            if (compName.equals(attribute.getName())) {
              return terminal;
            }
          }
          
          // comp is instance of ASTNonTerminal
          if (comp instanceof ASTNonTerminal) {
            ASTNonTerminal nonterminal = (ASTNonTerminal) comp;
            String compName = nonterminal.getName();
            if (nonterminal.getUsageName().isPresent()) {
              compName = nonterminal.getUsageName().get();
            }
            else {
              compName = compName.substring(0, 1).toLowerCase() + compName.substring(1);
            }
            if (compName.equals(attribute.getName())) {
              return nonterminal;
            }
          }
        }
      }
    }
    return null;
  }
  
  /**
   * Attaches the iteration from the grammar to the CD-AST.
   * @param attribute The attribute to which the iteration is attached.
   * @param clazz The class containing the attribute.
   * @param grammar The corresponding grammar to retrieve the iteration from.
   */
  public void attachIteration(ASTCDAttribute attribute, ASTCDClass clazz, ASTMCGrammar grammar) {
    // retrieve corresponding component and check its cardinality
    ASTRuleComponent comp = findComponent(grammar, clazz.getName(), attribute);
    
    int iteration = ASTConstantsGrammar.DEFAULT;
    
    // component is instance of ASTTerminal
    if (comp instanceof ASTTerminal) {
      ASTTerminal terminal = (ASTTerminal) comp;
      iteration = terminal.getIteration();
    }
    
    // component is instance of ASTNonTerminal
    if (comp instanceof ASTNonTerminal) {
      ASTNonTerminal nonterminal = (ASTNonTerminal) comp;
      iteration = nonterminal.getIteration();
    }
    
    TransformationHelper.addStereoType(attribute, MC2CDStereotypes.ITERATION.toString(), String.valueOf(iteration));
  }

}
