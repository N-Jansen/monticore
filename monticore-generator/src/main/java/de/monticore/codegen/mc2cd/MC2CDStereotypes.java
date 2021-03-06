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

package de.monticore.codegen.mc2cd;


/**
 * Class diagram stereotypes used by MC grammar -> CD transformation
 *
 * @author  Galina Volkova
 *
 */
public enum MC2CDStereotypes {
  /**
   * The rule attribute is defined in a super grammar
   */
  INHERITED("inherited"),
  /**
   * Type defined in the Java language
   */
  EXTERNAL_TYPE("externalType"),
  /**
   * Referenced symbol
   */
  REFERENCED_SYMBOL("referencedSymbol"),
  /**
   * Iteration of an attribute
   */
  MIN_CARD("minCard"),
  /**
   * Maximum cardinality value of an attribute
   */
  MAX_CARD("maxCard"),
  /**
   * The nonterminal is defined as a symbol
   */
  SYMBOL("symbol"),
  /**
   * The attribute is defined as an association
   */
  ASSOCIATION("association");
  
 private final String stereotype;
  
  private MC2CDStereotypes(String stereotype) {
    this.stereotype = stereotype;
  }
  
  @Override
  public String toString() {
    return stereotype;
  }
  
}
