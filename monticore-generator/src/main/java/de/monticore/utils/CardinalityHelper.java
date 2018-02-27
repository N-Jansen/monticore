/**
 * 
 */
package de.monticore.utils;

import java.util.ArrayList;
import java.util.List;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTRuleComponent;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.symboltable.Scope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;

/**
 * @author Nico Jansen
 * 
 * The cardinality helper stores the original cardinality values from the
 * parser. So the values are still accessible in the AST generator.
 */
public class CardinalityHelper {
  
  private static final CardinalityHelper instance = new CardinalityHelper();
  private List<ASTMCGrammar> grammarList;
  
  /**
   * Standard constructor.
   */
  private CardinalityHelper() {
    grammarList = new ArrayList<ASTMCGrammar>();
  }
  
  /**
   * Getter for the singleton class.
   * 
   * @return The instance of the CardinalityHelper.
   */
  public static synchronized CardinalityHelper getInstance() {
    return instance;
  }
  
  /**
   * Register grammar elements here to store them for later use.
   * 
   * @param grammar The grammar to be stored.
   */
  public void registerGrammar(ASTMCGrammar grammar) {
    grammarList.add(grammar);
  }
  
  /**
   * Finds the registered grammar with respect to its unique name.
   * 
   * @param grammarName The unique name of a grammar.
   * @return The corresponding grammar.
   */
  private ASTMCGrammar findGrammar(String grammarName) {
    ASTMCGrammar grammar = null;
    for (ASTMCGrammar g : grammarList) {
      if (g.getName().equals(grammarName)) {
        grammar = g;
        break;
      }
    }
    return grammar;
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
   * Derives the cardinality of an ASTCDAttribute by traversing the
   * corresponging nodes in the grammar and refering to the grammar component.
   * 
   * @param attribute The AST attribute.
   * @return The cardinality of the attribute.
   */
  public int getCardinality(ASTCDAttribute attribute) {
    // check if enclosing scope of the attribute is available
    if (!attribute.getEnclosingScope().isPresent()) {
      return 0;
    }
    Scope classScope = attribute.getEnclosingScope().get();
    
    // check if name for the class scope is available is available
    if (!classScope.getName().isPresent()) {
      return 0;
    }
    String className = classScope.getName().get();
    
    // check if enclosing scope of the class is available
    if (!classScope.getEnclosingScope().isPresent()) {
      return 0;
    }
    
    // check if name for the grammar scope is available
    if (!classScope.getEnclosingScope().get().getName().isPresent()) {
      return 0;
    }
    
    // check if corresponding grammar is registered
    String grammarName = classScope.getEnclosingScope().get().getName().get();
    ASTMCGrammar grammar = findGrammar(grammarName);
    if (grammar == null) {
      return 0;
    }
    
    // retrieve corresponding component and check its cardinality
    ASTRuleComponent comp = findComponent(grammar, className, attribute);
    
    // component is instance of ASTTerminal
    if (comp instanceof ASTTerminal) {
      ASTTerminal terminal = (ASTTerminal) comp;
      return terminal.getIteration();
    }
    
    // component is instance of ASTNonTerminal
    if (comp instanceof ASTNonTerminal) {
      ASTNonTerminal nonterminal = (ASTNonTerminal) comp;
      return nonterminal.getIteration();
    }
    
    return 0;
  }
}
