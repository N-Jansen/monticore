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
 * Contains all parameters required for defining an annotation in EMF.
 *
 * @author Nico Jansen
 */
public class EmfAnnotation {
  
  private String target;
  
  private String key;
  
  private String value;
  
  /**
   * Constructor.
   * 
   * @param target
   * @param key
   * @param value
   */
  public EmfAnnotation(String target, String key, String value) {
    this.target = target;
    this.key = key;
    this.value = value;
  }
  
  /**
   * Getter for the target.
   * 
   * @return target.
   */
  public String getTarget() {
    return target;
  }
  
  /**
   * Setter for the target.
   * 
   * @param target.
   */
  public void setTarget(String target) {
    this.target = target;
  }
  
  /**
   * Getter for the key.
   * 
   * @return key.
   */
  public String getKey() {
    return key;
  }
  
  /**
   * Setter for the key.
   * 
   * @param key.
   */
  public void setKey(String key) {
    this.key = key;
  }
  
  /**
   * Getter for the value.
   * 
   * @return value.
   */
  public String getValue() {
    return value;
  }
  
  /**
   * Setter for the value.
   * 
   * @param value.
   */
  public void setValue(String value) {
    this.value = value;
  }
}
