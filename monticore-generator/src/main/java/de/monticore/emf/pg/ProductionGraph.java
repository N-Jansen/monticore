/**
 * 
 */
package de.monticore.emf.pg;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;

/**
 * ProductionGraph for production rules. Contains the nonterminal and block
 * information of productions in form of a graph.
 * 
 * @author Nico Jansen
 */
public class ProductionGraph {
  
  private ProductionGraph root;
  private List<ProductionGraph> parentNodes;
  private List<ProductionGraph> childNodes;
  private List<ProductionGraph> entries;
  private int iteration;
  private String attributeName;
  private boolean block;
  private boolean empty;
  private String label;
  
  
  
  /**
   * Constructor for an attribute node with default iteration.
   * 
   * @param root
   * @param attributeName
   */
  public ProductionGraph(ProductionGraph root, String attributeName) {
    this.root = root;
    this.parentNodes = new LinkedList<>();
    this.childNodes = new LinkedList<>();
    this.entries = new LinkedList<>();;
    this.iteration = ASTConstantsGrammar.DEFAULT;
    this.attributeName = attributeName;
    this.block = false;
    this.empty = false;
  }
  
  /**
   * Constructor for an attribute node with non-default iteration.
   * 
   * @param root
   * @param attributeName
   * @param iteration
   */
  public ProductionGraph(ProductionGraph root, String attributeName, int iteration) {
    this.root = root;
    this.parentNodes = new LinkedList<>();
    this.childNodes = new LinkedList<>();
    this.entries = new LinkedList<>();
    ProductionGraph entryNode = new ProductionGraph(this, attributeName);
    entryNode.addParent(this);
    this.entries.add(entryNode);
    this.iteration = iteration;
    this.attributeName = null;
    this.block = true;
    this.empty = false;
  }
  
  /**
   * Constructor for a production graph blocks.
   * 
   * @param root 
   * @param iteration 
   */
  public ProductionGraph(ProductionGraph root, int iteration) {
    this.root = root;
    this.parentNodes = new LinkedList<>();
    this.childNodes = new LinkedList<>();
    this.entries = new LinkedList<>();
    this.iteration = iteration;
    this.attributeName = null;
    this.block = true;
    this.empty = false;
  }
  
  /**
   * Constructor for a production graph root.
   */
  public ProductionGraph() {
    this.root = this;
    this.parentNodes = new LinkedList<>();;
    this.childNodes = new LinkedList<>();;
    this.entries = new LinkedList<>();
    this.iteration = ASTConstantsGrammar.DEFAULT;
    this.attributeName = null;
    this.block = true;
    this.empty = false;
  }
  
  /**
   * Constructor for an empty production graph node.
   * 
   * @param root 
   * @param empty 
   */
  public ProductionGraph(ProductionGraph root, boolean empty) {
    this.root = root;
    this.parentNodes = new LinkedList<>();
    this.parentNodes = new LinkedList<>();
    this.childNodes = new LinkedList<>();
    this.entries = new LinkedList<>();;
    this.iteration = ASTConstantsGrammar.DEFAULT;
    this.attributeName = null;
    this.block = false;
    this.empty = empty;
  }
  
  /**
   * Getter for empty flag.
   * 
   * @return The empty flag.
   */
  public boolean isEmpty() {
    return empty;
  }
  
  /**
   * Getter for the root.
   * 
   * @return The root node.
   */
  public ProductionGraph getRoot() {
    return root;
  }
  
  /**
   * Setter for the root.
   * 
   * @param The root node.
   */
  public void setRoot(ProductionGraph root) {
    this.root = root;
  }
  
  /**
   * Getter for the name of an attribute for this node.
   * 
   * @return The name of the attribute.
   */
  public String getAttributeName() {
    return attributeName;
  }
  
  /**
   * Setter for the name of an attribute for this node.
   * 
   * @param attributeName The name of the attribute.
   */
  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }
  
  /**
   * Getter for the iteration of a block node.
   * 
   * @return The iteration of the block node.
   */
  public int getIteration() {
    return iteration;
  }
  
  /**
   * Setter for the iteration of a block node.
   * 
   * @param iteration The new iteration value.
   */
  public void setIteration(int iteration) {
    this.iteration = iteration;
  }
  
  /**
   * Getter for the parents of the current node.
   * 
   * @return List of parent nodes.
   */
  public List<ProductionGraph> getParentNodes() {
    return parentNodes;
  }
  
  /**
   * Setter for the parents of the current node.
   * 
   * @param List of parent nodes.
   */
  public void setParentNodes(List<ProductionGraph> parentNodes) {
    this.parentNodes = parentNodes;
  }
  
  /**
   * Adds a node as parent for the current node.
   * 
   * @param parentNode parentNode The new parent.
   */
  public void addParent(ProductionGraph parentNode) {
    parentNodes.add(parentNode);
  }
  
  /**
   * Getter for the children of the current node.
   * 
   * @return List of child nodes.
   */
  public List<ProductionGraph> getChildNodes() {
    return childNodes;
  }
  
  /**
   * Setter for the children of the current node.
   * 
   * @param List of child nodes.
   */
  public void setChildNodes(List<ProductionGraph> childNodes) {
    this.childNodes = childNodes;
  }
  
  /**
   * Adds a node as child for the current node.
   * 
   * @param childNode parentNode The new parent.
   */
  public void addChild(ProductionGraph childNode) {
    childNodes.add(childNode);
  } 
  
  /**
   * Getter for the entries of the current block node.
   * 
   * @return List of child nodes.
   */
  public List<ProductionGraph> getEntries() {
    return entries;
  }
  
  /**
   * Setter for the entries of the current block node.
   * 
   * @param List of child nodes.
   */
  public void setEntries(List<ProductionGraph> entries) {
    this.entries = entries;
  }
  
  /**
   * Adds a node as entry for the current block node.
   * 
   * @param entry The new entry.
   */
  public void addEntry(ProductionGraph entry) {
    entries.add(entry);
  } 
  
  /**
   * Getter for the block property.
   * 
   * @return Whether the node is a block node.
   */
  public boolean isBlock() {
    return block;
  }
  
  /**
   * Checks whether current block node is the root node.
   * 
   * @return Whether the node is the root node.
   */
  public boolean isRoot() {
    return this.equals(this.root);
  }
  
  /**
   * Getter for the label of a branch.
   * 
   * @return The label.
   */
  public String getLabel() {
    return label;
  }
  
  /**
   * Setter for the label of a branch.
   * 
   * @param label The new label.
   */
  public void setLabel(String label) {
    this.label = label;
  }
  
  /**
   * Creates a copy of this graph recursively.
   * 
   * @param the copy parent.
   * @return Copy of the graph.
   */
  public ProductionGraph deepCopy(ProductionGraph copyRoot, ProductionGraph copyParent) {
    ProductionGraph copy = null;
    List<ProductionGraph> newParentNodes = new LinkedList<ProductionGraph>();
    newParentNodes.add(copyParent);
    
    if (isEmpty()) {
      copy = new ProductionGraph(copyRoot, true);
      copy.addParent(copyParent);
      for (ProductionGraph ch : getChildNodes()) {
        copy.addChild(ch.deepCopy(copyRoot, copy));
      }
    }
    else if (isBlock()) {
      copy = new ProductionGraph(copyRoot, iteration);
      copy.addParent(copyParent);
      for (ProductionGraph entry : entries) {
        copy.addEntry(entry.deepCopy(copy, copy));
      }
      for (ProductionGraph child : childNodes) {
        copy.addChild(child.deepCopy(copyRoot, copy));
      }
    }
    else {
      copy = new ProductionGraph(copyRoot, attributeName);
      copy.addParent(copyParent);
      for (ProductionGraph ch : getChildNodes()) {
        copy.addChild(ch.deepCopy(copyRoot, copy));
      }
    }
    
    return copy;
    
//    if (attributeName.equals("block")) {
//      copy = new ProductionGraph(attributeName, attrtibuteIteration, blockID);
//      for (ProductionGraph child : childNodes) {
//        copy.getChildNodes().add(child.deepCopy());
//      }
//    }
//    else if (attributeName.equals("block_end")) {
//      copy = new ProductionGraph(attributeName, attrtibuteIteration, blockID);
//      for (ProductionGraph child : childNodes) {
//        copy.getChildNodes().add(child.deepCopy());
//      }
//    }
//    else {
//      copy = new ProductionGraph(attributeName, attrtibuteIteration);
//      for (ProductionGraph child : childNodes) {
//        copy.getChildNodes().add(child.deepCopy());
//      }
//    }
  }
  
  
  /**
   * Creates a copy of this block.
   * 
   * @return Copy of the graph.
   */
  public ProductionGraph deepCopyBlock() {
    ProductionGraph copy = null;
    if (isBlock()) {
      copy = new ProductionGraph(this.root, iteration);
      for (ProductionGraph entry : entries) {
        copy.addEntry(entry.deepCopy(copy, copy));
      }
    }
    return copy;
  }
  
//  /**
//   * Adds a child node at the leaf of the graph.
//   * 
//   * @param child The child that is added at leaf level.
//   */
//  public void addAtLeaf(ProductionGraph child) {
//    Set<ProductionGraph> leafNodes = getLeafs();
//    for (ProductionGraph node : leafNodes) {
//      node.getChildNodes().add(child);
//    }
//  }
//  
  /**
   * Retrieves all leafs of the block or graph.
   * 
   * @return All leaf nodes as a set.
   */
  public Set<ProductionGraph> getLeafsInBlock() {
    Set<ProductionGraph> leafNodes = new HashSet<ProductionGraph>();
    if (!entries.isEmpty()) {
      for (ProductionGraph subGraph : entries) {
        leafNodes.addAll(subGraph.getLeafs());
      }
    }
    return leafNodes;
  }
  
  /**
   * Retrieves all leafs of the graph.
   * 
   * @return All leaf nodes as a set.
   */
  public Set<ProductionGraph> getLeafs() {
    Set<ProductionGraph> leafNodes = new HashSet<ProductionGraph>();
    if (childNodes.isEmpty()) {
      leafNodes.add(this);
    } else {
      for (ProductionGraph subGraph : childNodes) {
        leafNodes.addAll(subGraph.getLeafs());
      }
    }
    return leafNodes;
  }
  
  /**
   * Traverses the graph to find all root branches.
   * 
   * @return The root branches from the current node.
   */
  public Set<ProductionGraph> getRootBranches() {
    Set<ProductionGraph> branches = new HashSet<ProductionGraph>();
    
    // current node is the only root branch
    if (getParentNodes().size() == 1 && getParentNodes().get(0).isBlock() && getParentNodes().get(0).getEntries().contains(this)) {
      branches.add(this);
      return branches;
    }
    else {
      List<ProductionGraph> parents = getParentNodes();
      for (ProductionGraph parent : parents) {
        // parent is root branch
        if (parent.getParentNodes().size() == 1 && parent.getParentNodes().get(0).isBlock() && parent.getParentNodes().get(0).getEntries().contains(parent)) {
          branches.add(parent);
        }
        // backtrace for root branch
        else {
          branches.addAll(parent.getRootBranches());
        }
      }
    }
    return branches;
  }
  
  /**
   * Updates the root for the subgraph.
   * 
   * @param root The new root.
   */
  public void updateRootRec(ProductionGraph root) {
    this.setRoot(root);
    for (ProductionGraph child : getChildNodes()) {
      child.updateRootRec(root);
    }
  }
}
