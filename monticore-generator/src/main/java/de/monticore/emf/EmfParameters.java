/*
 * *****************************************************************************
 * * MontiCore Language Workbench, www.monticore.de Copyright (c) 2017,
 * MontiCore, All rights reserved. This project is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 3.0 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * *****************************************************************************
 * *
 */
package de.monticore.emf;

/**
 * Contains all attribute paramters needed by emf generator
 *
 * @author Nico Jansen
 */
public class EmfParameters {
  
  private String defaultValue;
  
  private String otherEnd;
  
  private String lowerBound;
  
  private String upperBound;
  
  private boolean changeable;
  
  private boolean derived;
  
  private boolean ordered;
  
  private boolean transient_;
  
  private boolean unique;
  
  private boolean unsettable;
  
  private boolean volatile_;
  
  private boolean id;
  
  private boolean composite;
  
  private boolean resolveProxies;
  
  /**
   * Constructor.
   */
  public EmfParameters() {
    this.defaultValue = "null";
    this.otherEnd = "null";
    this.lowerBound = "0";
    this.upperBound = "1";
    this.changeable = true;
    this.derived = false;
    this.ordered = true;
    this.transient_ = false;
    this.unsettable = false;
    this.volatile_ = false;
    this.id = false;
    this.composite = false;
    this.resolveProxies = false;
  }
  
  /**
   * Emf generator getter for the default value.
   * 
   * @return defaultValue.
   */
  public String getDefaultValue() {
    return defaultValue;
  }
  
  /**
   * Setter for the default value parameter.
   * 
   * @param defaultValue.
   */
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
  
  /**
   * Emf generator getter for the other end of an association.
   * 
   * @return otherEnd.
   */
  public String getOtherEnd() {
    return otherEnd;
  }
  
  /**
   * Setter for the other end of an association.
   * 
   * @param otherEnd.
   */
  public void setOtherEnd(String otherEnd) {
    this.otherEnd = otherEnd;
  }
  
  /**
   * Emf generator getter for the lower bound cardinality.
   * 
   * @return lowerBound.
   */
  public String getLowerBound() {
    return lowerBound;
  }
  
  /**
   * Setter for the lower bound parameter.
   * 
   * @param lowerBound.
   */
  public void setLowerBound(String lowerBound) {
    this.lowerBound = lowerBound;
  }
  
  /**
   * Emf generator getter for the upper bound cardinality.
   * 
   * @return upperBound.
   */
  public String getUpperBound() {
    return upperBound;
  }
  
  /**
   * Setter for the upper bound parameter.
   * 
   * @param upperBound.
   */
  public void setUpperBound(String upperBound) {
    this.upperBound = upperBound;
  }
  
  /**
   * Emf generator getter for the changeable parameter.
   * 
   * @return changeable.
   */
  public String isChangeable() {
    if (changeable) {
      return "IS_CHANGEABLE";
    }
    else {
      return "!IS_CHANGEABLE";
    }
  }
  
  /**
   * Setter for the changeable parameter.
   * 
   * @param changeable.
   */
  public void setChangeable(boolean changeable) {
    this.changeable = changeable;
  }
  
  /**
   * Emf generator getter for the derived parameter.
   * 
   * @return derived.
   */
  public String isDerived() {
    if (derived) {
      return "IS_DERIVED";
    }
    else {
      return "!IS_DERIVED";
    }
  }
  
  /**
   * Setter for the derived parameter.
   * 
   * @param derived.
   */
  public void setDerived(boolean derived) {
    this.derived = derived;
  }
  
  /**
   * Emf generator getter for the ordered parameter.
   * 
   * @return ordered.
   */
  public String isOrdered() {
    if (ordered) {
      return "IS_ORDERED";
    }
    else {
      return "!IS_ORDERED";
    }
  }
  
  /**
   * Setter for the ordered parameter.
   * 
   * @param ordered.
   */
  public void setOrdered(boolean ordered) {
    this.ordered = ordered;
  }
  
  /**
   * Emf generator getter for the transient parameter.
   * 
   * @return transient_.
   */
  public String isTransient() {
    if (transient_) {
      return "IS_TRANSIENT";
    }
    else {
      return "!IS_TRANSIENT";
    }
  }
  
  /**
   * Setter for the transient parameter.
   * 
   * @param transient_.
   */
  public void setTransient(boolean transient_) {
    this.transient_ = transient_;
  }
  
  /**
   * Emf generator getter for the unique parameter.
   * 
   * @return unique.
   */
  public String isUnique() {
    if (unique) {
      return "IS_UNIQUE";
    }
    else {
      return "!IS_UNIQUE";
    }
  }
  
  /**
   * Setter for the unique parameter.
   * 
   * @param unique.
   */
  public void setUnique(boolean unique) {
    this.unique = unique;
  }
  
  /**
   * Emf generator getter for the unsettable parameter.
   * 
   * @return unsettable.
   */
  public String isUnsettable() {
    if (unsettable) {
      return "IS_UNSETTABLE";
    }
    else {
      return "!IS_UNSETTABLE";
    }
  }
  
  /**
   * Setter for the unsettable parameter.
   * 
   * @param unsettable.
   */
  public void setUnsettable(boolean unsettable) {
    this.unsettable = unsettable;
  }
  
  /**
   * Emf generator getter for the volatile parameter.
   * 
   * @return volatile_.
   */
  public String isVolatile() {
    if (volatile_) {
      return "IS_VOLATILE";
    }
    else {
      return "!IS_VOLATILE";
    }
  }
  
  /**
   * Setter for the volatile parameter.
   * 
   * @param volatile_.
   */
  public void setVolatile(boolean volatile_) {
    this.volatile_ = volatile_;
  }
  
  /**
   * Emf generator getter for the id parameter.
   * 
   * @return id.
   */
  public String isId() {
    if (id) {
      return "IS_ID";
    }
    else {
      return "!IS_ID";
    }
  }
  
  /**
   * Setter for the id parameter.
   * 
   * @param id.
   */
  public void setId(boolean id) {
    this.id = id;
  }
  
  /**
   * Emf generator getter for the composite paramter.
   * 
   * @return composite.
   */
  public String isComposite() {
    if (composite) {
      return "IS_COMPOSITE";
    }
    else {
      return "!IS_COMPOSITE";
    }
  }
  
  /**
   * Setter for the composite parameter.
   * 
   * @param composite.
   */
  public void setComposite(boolean composite) {
    this.composite = composite;
  }
  
  /**
   * Emf generator getter for the resolveProxies parameter.
   * 
   * @return resolveProxies.
   */
  public String isResolveProxies() {
    if (resolveProxies) {
      return "IS_RESOLVE_PROXIES";
    }
    else {
      return "!IS_RESOLVE_PROXIES";
    }
  }
  
  /**
   * Setter for the resolveProxies parameter.
   * 
   * @param resolveProxies.
   */
  public void setResolveProxies(boolean resolveProxies) {
    this.resolveProxies = resolveProxies;
  }
  
}
