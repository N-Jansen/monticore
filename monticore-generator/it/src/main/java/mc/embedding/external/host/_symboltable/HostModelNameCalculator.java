/*
 * ******************************************************************************
 * MontiCore Language Workbench
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

/* generated from model null*/
/* generated by template symboltable.ModelNameCalculator*/




package mc.embedding.external.host._symboltable;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import de.monticore.symboltable.SymbolKind;

public class HostModelNameCalculator extends de.monticore.CommonModelNameCalculator {

  @Override
  public Set<String> calculateModelNames(final String name, final SymbolKind kind) {
    if (ContentSymbol.KIND.isKindOf(kind)) {
      return calculateModelNameForContent(name);
    }
    if (HostSymbol.KIND.isKindOf(kind)) {
      return calculateModelNameForHost(name);
    }

    return Collections.emptySet();
  }

  protected Set<String> calculateModelNameForContent(String name) {
    return ImmutableSet.of(name);
  }
  protected Set<String> calculateModelNameForHost(String name) {
    return ImmutableSet.of(name);
  }


}
