/**
 * 
 */
package de.monticore.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * ProductionTree for production rules. Contains the nonterminal and block
 * information of productions in form of a tree.
 * 
 * @author Nico Jansen
 */
public class ProductionTree {
  private String attributeName;
  private int attrtibuteIteration;
  private List<ProductionTree> childNodes;
  
  /**
   * Standard constructor for a production tree.
   * 
   * @param attributeName The name of the attribute stored in this node.
   * @param attrtibuteIteration The iteration of the attribute for this node.
   */
  public ProductionTree(String attributeName, int attrtibuteIteration) {
    this.attributeName = attributeName;
    this.attrtibuteIteration = attrtibuteIteration;
    this.childNodes = new LinkedList<ProductionTree>();
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
   * Getter for the iteration of an attribute for this node.
   * 
   * @return The iteration of the attribute.
   */
  public int getAttrtibuteIteration() {
    return attrtibuteIteration;
  }
  
  /**
   * Getter for the children of the current node.
   * 
   * @return List of child nodes.
   */
  public List<ProductionTree> getChildNodes() {
    return childNodes;
  }
  
  /**
   * Adds a node as child for the current node.
   * 
   * @param attributeName The name of the attribute stored in this node.
   * @param attrtibuteIteration The iteration of the attribute for this node.
   */
  public void addChild(String attributeName, int attrtibuteIteration) {
    childNodes.add(new ProductionTree(attributeName, attrtibuteIteration));
  }
}
