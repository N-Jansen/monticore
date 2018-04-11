/**
 * 
 */
package de.monticore.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTBlock;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTRuleComponent;

/**
 * Creates disjunction trees for production rules and stores them into a grammar
 * specific map.
 * 
 * @author Nico Jansen
 */
public class ProductionTreeHelper {
  
  private final static String ROOT = "root";
  private final static String BLOCK = "block";
  
  private static Map<String, Map<String, ProductionTree>> productionTreesMap;
  
  /**
   * Creates and stores the production tree for a production rule given by the
   * alternatives (alts). Stores the tree with respect to grammar and
   * nonterminal.
   * 
   * @param grammar The grammar that contains the production rule.
   * @param nonterminal The nonterminal for which the tree is constructed.
   * @param alts The alternatives that represent the production rule.
   */
  public static void createProductionTree(String grammar, String nonterminal, List<ASTAlt> alts) {
    
    // initialize map if not initialized
    if (productionTreesMap == null) {
      productionTreesMap = new HashMap<String, Map<String, ProductionTree>>();
    }
    
    // retrieve or create productiontree-map for grammar
    Map<String, ProductionTree> productionTrees = null;
    if (productionTreesMap.containsKey(grammar)) {
      productionTrees = productionTreesMap.get(grammar);
    }
    else {
      productionTrees = new HashMap<String, ProductionTree>();
    }
    
    // stop if production tree for nonterminal already created
    if (productionTrees.containsKey(nonterminal)) {
      return;
    }
    
    // create production tree for nonterminal
    ProductionTree tree = new ProductionTree(ROOT, -1);
    createSubTree(tree, alts);
    
    // store production tree
    productionTrees.put(nonterminal, tree);
    productionTreesMap.put(grammar, productionTrees);
  }
  
  /**
   * Creates the subtree for the given parent with respect to the remaining
   * production rule (alts).
   * 
   * @param parent The parent which is extended by the subtree.
   * @param alts The alternatives that represent the remaining production rule.
   */
  private static void createSubTree(ProductionTree parent, List<ASTAlt> alts) {
    
    for (ASTAlt alt : alts) {
      // set current parent
      ProductionTree currentParent = parent;
      
      Collection<ASTNode> astNodes = alt.get_Children();
      Object[] nodes = astNodes.toArray();
      for (int i = 0; i < nodes.length; i++) {
        ASTNode astNode = (ASTNode) nodes[i];
        
        // add nonterminals as child
        if (astNode instanceof ASTNonTerminal) {
          ASTNonTerminal nt = (ASTNonTerminal) astNode;
          String usageName = nt.getUsageName().orElse(deriveUsageName(nt.getName()));
          ProductionTree pt = new ProductionTree(usageName, nt.getIteration());
          currentParent.getChildNodes().add(pt);
          currentParent = pt;
          
          // add blocks as child and reorder the remaining alternatives
        }
        else if (astNode instanceof ASTBlock) {
          ASTBlock block = (ASTBlock) astNode;
          ProductionTree pt = new ProductionTree(BLOCK, block.getIteration());
          currentParent.getChildNodes().add(pt);
          currentParent = pt;
          
          // reorder remaining alternatives
          List<ASTAlt> remainingAlts = new LinkedList<ASTAlt>();
          
          // add alternatives from block
          for (ASTAlt remAlt : block.getAlts()) {
            remainingAlts.add(remAlt.deepClone());
          }
          
          // add unprocessed alternatives from parent of block
          for (ASTAlt remAlt : remainingAlts) {
            for (int j = i + 1; j < nodes.length; j++) {
              remAlt.getComponents().add((ASTRuleComponent) nodes[j]);
            }
          }
          
          // call method recursively with reordered alternatives
          createSubTree(currentParent, remainingAlts);
          break;
        }
        
      }
    }
  }
  
  /**
   * Derives the usage name of a nonterminal.
   * 
   * @param name The name of the nonterminal.
   * @return The usage name derived from the name of the nonterminal.
   */
  private static String deriveUsageName(String name) {
    return name.substring(0, 1).toLowerCase() + name.substring(1);
  }
}
