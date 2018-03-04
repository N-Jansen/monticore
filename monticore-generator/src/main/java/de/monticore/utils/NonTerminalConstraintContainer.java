package de.monticore.utils;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

/**
 * The container stores all necessary data to support the generation of an OCL
 * constraint for a referenced nonterminal.
 * 
 * @author Nico Jansen
 */
public class NonTerminalConstraintContainer {
  
  private ASTCDAttribute referencingAttribute;
  private ASTCDAttribute referencedAttribute;
  private ASTCDClass enclosingClass;
  
  /**
   * Standard constructor.
   * 
   * @param referencingAttribute The attribute that has a nonterminal reference
   *          to another attribute.
   * @param referencedAttribute The attribute being referenced.
   * @param enclosingClass The class containing both attributes.
   */
  public NonTerminalConstraintContainer(ASTCDAttribute referencingAttribute, ASTCDAttribute referencedAttribute, ASTCDClass enclosingClass) {
    this.referencingAttribute = referencingAttribute;
    this.referencedAttribute = referencedAttribute;
    this.enclosingClass = enclosingClass;
  }
  
  /**
   * Getter for referencingAttribute.
   * @return referencingAttribute
   */
  public ASTCDAttribute getReferencingAttribute() {
    return referencingAttribute;
  }

  /**
   * Getter for referencedAttribute.
   * @return referencedAttribute
   */
  public ASTCDAttribute getReferencedAttribute() {
    return referencedAttribute;
  }

  /**
   * Getter for enclosingClass.
   * @return enclosingClass
   */
  public ASTCDClass getEnclosingClass() {
    return enclosingClass;
  }
}
