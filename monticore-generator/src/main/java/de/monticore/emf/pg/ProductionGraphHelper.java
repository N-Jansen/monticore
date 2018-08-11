/**
 * 
 */
package de.monticore.emf.pg;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTBlock;
import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;

/**
 * Creates disjunction trees for production rules and stores them into a grammar
 * specific map.
 * 
 * @author Nico Jansen
 */
public class ProductionGraphHelper {
  
  private static Map<String, Map<String, ProductionGraph>> productionGraphsMap;
  private static int labelCount;
  
  /**
   * Creates and stores the production graph for a production rule given by the
   * alternatives (alts). Stores the graph with respect to grammar and
   * nonterminal.
   * 
   * @param grammar The grammar that contains the production rule.
   * @param nonterminal The nonterminal for which the graph is constructed.
   * @param alts The alternatives that represent the production rule.
   */
  public static void createProductionGraph(String grammar, String nonterminal, List<ASTAlt> alts) {
    
    // initialize map if not initialized
    if (productionGraphsMap == null) {
      productionGraphsMap = new HashMap<String, Map<String, ProductionGraph>>();
    }
    
    // retrieve or create productiongraph-map for grammar
    Map<String, ProductionGraph> productionGraphs = null;
    if (productionGraphsMap.containsKey(grammar)) {
      productionGraphs = productionGraphsMap.get(grammar);
    }
    else {
      productionGraphs = new HashMap<String, ProductionGraph>();
    }
    
    // stop if production graph for nonterminal already created
    if (productionGraphs.containsKey(nonterminal)) {
      return;
    }
    
    // create production graph for nonterminal
    ProductionGraph graph = new ProductionGraph();
    createSubGraph(graph, alts);
    
    // normalize graph
    normalizeGraph(graph);
    
    // label graph for les derivation
    labelProductionGraph(graph);
    
    // store production graph
    productionGraphs.put(nonterminal, graph);
    productionGraphsMap.put(grammar, productionGraphs);
  }
  
  /**
   * Creates the subtree for the given parent with respect to the remaining
   * production rule (alts).
   * 
   * @param parent The parent which is extended by the subtree.
   * @param alts The alternatives that represent the remaining production rule.
   */
  private static void createSubGraph(ProductionGraph encBlock, List<ASTAlt> alts) {
    
    for (ASTAlt alt : alts) {
      // set current parent
      ProductionGraph currentParent = null;
      
      Collection<ASTNode> astNodes = alt.get_Children();
      Object[] nodes = astNodes.toArray();
      for (int i = 0; i < nodes.length; i++) {
        ASTNode astNode = (ASTNode) nodes[i];
        
        // add nonterminals as child
        if (astNode instanceof ASTNonTerminal) {
          ASTNonTerminal nt = (ASTNonTerminal) astNode;
          String usageName = nt.getUsageName().orElse(deriveUsageName(nt.getName()));
          ProductionGraph pg = null;
          if (nt.getIteration() == ASTConstantsGrammar.DEFAULT) {
            pg = new ProductionGraph(encBlock, usageName);
          }
          else {
            pg = new ProductionGraph(encBlock, usageName, nt.getIteration());
          }
          
          // manage parent
          if (currentParent == null) {
            encBlock.addEntry(pg);
            pg.addParent(encBlock);
          }
          else {
            currentParent.addChild(pg);
            pg.addParent(currentParent);
          }
          currentParent = pg;
        }
        // add blocks as child and reorder the remaining alternatives
        else if (astNode instanceof ASTBlock) {
          ASTBlock block = (ASTBlock) astNode;
          ProductionGraph pg = new ProductionGraph(encBlock, block.getIteration());
          // manage parent
          if (currentParent == null) {
            encBlock.addEntry(pg);
            pg.addParent(encBlock);
          }
          else {
            currentParent.addChild(pg);
            pg.addParent(currentParent);
          }
          currentParent = pg;
          
          // reorder remaining alternatives
          List<ASTAlt> remainingAlts = new LinkedList<ASTAlt>();
          
          // add alternatives from block
          for (ASTAlt remAlt : block.getAlts()) {
            remainingAlts.add(remAlt.deepClone());
          }
          
          // call method recursively with reordered alternatives
          createSubGraph(currentParent, remainingAlts);
        }
      }
    }
  }
  
  /**
   * Normalizes the production graph. Resolves default, plus, and optional
   * cardinalities for block nodes.
   * 
   * @param graph The graph that should be normalized.
   */
  private static void normalizeGraph(ProductionGraph graph) {
    normalizeOptionalities(graph);
    normalizePlus(graph);
    normalizeDefault(graph);
  }
  
  /**
   * Normalizes the production graph. Resolves optional cardinalities for block
   * nodes.
   * 
   * @param graph The graph that should be normalized.
   */
  private static void normalizeOptionalities(ProductionGraph graph) {
    // normalize block contents
    if (graph.isBlock()) {
      for (int i = 0; i < graph.getEntries().size(); i++) {
        ProductionGraph entry = graph.getEntries().get(i);
        normalizeOptionalities(entry);
      }
      if (graph.getIteration() == ASTConstantsGrammar.QUESTION) {
        graph.setIteration(ASTConstantsGrammar.DEFAULT);
        ProductionGraph emptyNode = new ProductionGraph(graph, true);
        emptyNode.addParent(graph);
        graph.addEntry(emptyNode);
      }
    }
    
    // normalize successor graph
    for (ProductionGraph child : graph.getChildNodes()) {
      normalizeOptionalities(child);
    }
  }
  
  /**
   * Normalizes the production graph. Resolves plus cardinalities for block
   * nodes.
   * 
   * @param graph The graph that should be normalized.
   */
  private static void normalizePlus(ProductionGraph graph) {
    // normalize block contents
    if (graph.isBlock()) {
      for (int i = 0; i < graph.getEntries().size(); i++) {
        ProductionGraph entry = graph.getEntries().get(i);
        normalizePlus(entry);
      }
      if (graph.getIteration() == ASTConstantsGrammar.PLUS) {
        graph.setIteration(ASTConstantsGrammar.STAR);
        ProductionGraph copy = graph.deepCopyBlock();
        copy.setIteration(ASTConstantsGrammar.DEFAULT);
        
        // use copy as new parent for child nodes
        for (int j = 0; j < graph.getChildNodes().size(); j++) {
          ProductionGraph child = graph.getChildNodes().get(j);
          child.getParentNodes().remove(graph);
          child.getParentNodes().add(copy);
          copy.addChild(child);
        }
        
        // clear child node of original entry and attach copy
        graph.getChildNodes().clear();
        graph.addChild(copy);
        copy.addParent(graph);
      }
    }
    
    // normalize successor graph
    for (ProductionGraph child : graph.getChildNodes()) {
      normalizePlus(child);
    }
  }
  
  /**
   * Normalizes the production graph. Resolves default cardinalities for block
   * nodes.
   * 
   * @param graph The graph that should be normalized.
   */
  private static void normalizeDefault(ProductionGraph graph) {
 // normalize block contents
    if (graph.isBlock()) {
      
      // reapet normalizing entries until graph remains unchanged
      int entryIter = 0;
      while (entryIter < graph.getEntries().size()) {
        ProductionGraph entry = graph.getEntries().get(entryIter);
        normalizeDefault(entry);
        if (entry.equals(graph.getEntries().get(entryIter))) {
          entryIter++;
        } else {
          entryIter = 0;
        }
      }
      
      if (graph.getIteration() == ASTConstantsGrammar.DEFAULT) {
        // if graph is entry of a parent, which is a (local) root
        if (graph.getParentNodes().size() == 1 && graph.getParentNodes().get(0).isBlock() && graph.getParentNodes().get(0).getEntries().contains(graph)) {
          ProductionGraph parent = graph.getParentNodes().get(0);
          Set<ProductionGraph> blockLeafs = graph.getLeafsInBlock();
          
          // remove block and attach contents to parent
          parent.getEntries().remove(graph);
          for (ProductionGraph subgraph : graph.getEntries()) {
            subgraph.getParentNodes().remove(graph);
            parent.addEntry(subgraph);
            subgraph.addParent(parent);
            subgraph.updateRootRec(graph.getRoot());
          }
          
          // attach children to root for empty blocks
          if (blockLeafs.isEmpty()) {
            graph.getRoot().getEntries().remove(graph);
            for (ProductionGraph child : graph.getChildNodes()) {
              child.getParentNodes().remove(graph);
              child.addParent(graph.getRoot());
              graph.getRoot().addEntry(child);
            }
          }
          else {
            // add block's children at leaf of block contents for each nonempty block
            for (ProductionGraph child : graph.getChildNodes()) {
              child.getParentNodes().remove(graph);
              for (ProductionGraph leaf : blockLeafs) {
                leaf.addChild(child);
                child.addParent(leaf);
              }
            }
          }
        }
        // current block node is in between the graph
        else {
          Set<ProductionGraph> rootBranches = graph.getRootBranches();
          
          // remove block from graph
          for (int i = 0; i < graph.getParentNodes().size(); i++) {
            ProductionGraph parent = graph.getParentNodes().get(i);
            parent.getChildNodes().remove(graph);
          }
          for (int i = 0; i < graph.getChildNodes().size(); i++) {
            ProductionGraph child = graph.getChildNodes().get(i);
            child.getParentNodes().remove(graph);
          }
          // attach children of block to parents of block
          for (ProductionGraph parent : graph.getParentNodes()) {
            for (ProductionGraph child : graph.getChildNodes()) {
              parent.addChild(child);
              child.addParent(parent);
            }
          }
          
          for (ProductionGraph rb : rootBranches) {
            rb.getParentNodes().clear();
            for (ProductionGraph bb : graph.getEntries()) {
              ProductionGraph copy_bb = bb.deepCopy(rb.getRoot(), rb.getRoot());
              ProductionGraph bb_leaf = (ProductionGraph) copy_bb.getLeafs().toArray()[0];
              
              rb.getRoot().addEntry(copy_bb);
              rb.getRoot().getEntries().remove(rb);
              
              bb_leaf.addChild(rb);
              rb.addParent(bb_leaf);
            }
          }
        }
      }
    }
    
 // normalize successor graph
    for (int i = 0; i < graph.getChildNodes().size(); i++) {
      ProductionGraph child = graph.getChildNodes().get(i);
      normalizeDefault(child);
    }
  }
  
  /**
   * Labels the branch nodes of the production graph.
   * 
   * @param pg The production graph.
   * @param labelCount The current label count.
   */
  private static void labelProductionGraph(ProductionGraph pg) {
    if (pg.isBlock()) {
      // no labeling for root branches
      if (pg.equals(pg.getRoot())) {
        for (ProductionGraph entry : pg.getEntries()) {
          labelCount = 0;
          labelProductionGraph(entry);
        }
      }
      else {
        for (ProductionGraph entry : pg.getEntries()) {
          labelCount++;
          entry.setLabel("x_" + labelCount);
          labelProductionGraph(entry);
        }
      }
    }
    // apply recursively
    for (ProductionGraph child : pg.getChildNodes()) {
      labelProductionGraph(child);
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
  
  /**
   * Getter for production graphs.
   * 
   * @param grammar The grammar of the nonterminal definition.
   * @param nonterminal The corresponding nonterminal.
   * 
   * @return The production graph for the grammar and nonterminal.
   */
  public static ProductionGraph getProductionGraph(String grammar, String nonterminal) {
    Map<String, ProductionGraph> graphs = productionGraphsMap.get(grammar);
    if (graphs != null) {
      return graphs.get(nonterminal);
    }
    return null;
  }
}
