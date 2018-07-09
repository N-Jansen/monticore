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

package de.monticore.grammar.cocos;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that the names of associations for nonterminal references are
 * available.
 *
 * @author Nico Jansen
 */
public class NTRefAssociationNameNotAssigned implements GrammarASTNonTerminalCoCo {
  
  public static final String ERROR_CODE = "0xA4039";
  
  public static final String ERROR_MSG_FORMAT = " The name \"%s\" used by the association for the nonterminal reference \"%s\" must not be assigned to another attribute.";
  
  @Override
  public void check(ASTNonTerminal nt) {
    
    // Check if NT references a symbol and if enclosing scope is available
    if (!nt.getReferencedSymbol().isPresent() || !nt.getEnclosingScope().isPresent()) {
      return;
    }
    
    // Retrieve usage name of later association
    String associationName = getUsageName(nt) + nt.getReferencedSymbol().get();
    
    // Get names of used nonterminals in enclosing scope
    Scope encScope = nt.getEnclosingScope().get();
    Set<String> ntNames = encScope.getLocalSymbols().keySet();
    
    for (String name : ntNames) {
      // Implied usage name
      String usageName = toUsageName(name);
      
      if (associationName.equals(usageName)) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, associationName, getUsageName(nt)));
      }
    }
  }
  
  /**
   * Retrieves the usage name for a nonterminal if non-existent.
   * 
   * @param nt The nonterminal to find a usage name for.
   * @return The usage name of the nonterminal.
   */
  private String getUsageName(ASTNonTerminal nt) {
    if (nt.getUsageName().isPresent()) {
      return nt.getUsageName().get();
    }
    else {
      return nt.getName().substring(0, 1).toLowerCase() + nt.getName().substring(1);
    }
  }
  
  /**
   * Converts a given name to its corresponding usage name.
   * 
   * @param name Input name.
   * @return Transformed input name.
   */
  private String toUsageName(String name) {
    return name.substring(0, 1).toLowerCase() + name.substring(1);
  }
}
