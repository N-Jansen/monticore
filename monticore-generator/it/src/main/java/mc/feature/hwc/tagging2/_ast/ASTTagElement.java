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

package mc.feature.hwc.tagging2._ast;

import mc.feature.hwc.tagging2._ast.ASTTagElementTOP;

public class ASTTagElement extends ASTTagElementTOP {
  protected  ASTTagElement (String name, String tagValue) {
    super(name, tagValue);
  }

  protected ASTTagElement () {
    super();
  }

  public void setTagValue(String tagValue) {
    if (tagValue != null) {
      if (tagValue.startsWith("=")) {
        tagValue = tagValue.substring(1);
      }
      if (tagValue.endsWith(";")) {
        tagValue = tagValue.substring(0, tagValue.length() - 1);
      }
      tagValue = tagValue.trim();
    }
    super.setTagValue(tagValue);
  }
}


