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

package de.monticore.codegen.cd2java.ast_emf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.manipul.BaseInterfaceAddingManipulation;
import de.monticore.emf._ast.ASTECNode;
import de.monticore.emf._ast.ASTENodePackage;
import de.monticore.emf.pg.ProductionGraph;
import de.monticore.emf.pg.ProductionGraphHelper;
import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereoValue;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

/**
 * A helper for emf-compatible generation
 */
public class AstEmfGeneratorHelper extends AstGeneratorHelper {
  
  public static final String JAVA_MAP = "java.util.Map";
  
  public AstEmfGeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
    super(topAst, symbolTable);
  }
  
  public String getPackageURI() {
    return "http://" + getCdName() + "/1.0";
  }
  
  /**
   * @return externalTypes
   */
  @Override
  public String getAstAttributeValue(ASTCDAttribute attribute, ASTCDType clazz) {
    if (attribute.getValue().isPresent()) {
      return attribute.printValue();
    }
    if (isOptional(attribute)) {
      return "Optional.empty()";
    }
    String typeName = TypesPrinter.printType(attribute.getType());
    if (isListType(typeName)) {
      String attributeName = getPlainName(clazz) + "_"
          + StringTransformations.capitalize(GeneratorHelper.getNativeAttributeName(attribute
              .getName()));
      Optional<ASTSimpleReferenceType> typeArg = TypesHelper
          .getFirstTypeArgumentOfGenericType(attribute.getType(), JAVA_LIST);
      if (typeArg.isPresent()) {
        String typeArgName = TypesHelper.printType(typeArg.get());
        if (Names.getQualifier(typeArgName).equals(getAstPackage())) {
          typeName = Names.getSimpleName(typeArgName);
          return "new EObjectContainmentEList<" + typeName + ">(" + typeName + ".class, this, "
              + this.getCdName() + "Package." + attributeName + ")";
        }
        else {
          typeName = typeArgName;
          return "new EDataTypeEList<" + typeName + ">(" + typeName + ".class, this, "
              + this.getCdName() + "Package." + attributeName + ")";
        }
      }
    }
    if (isMapType(typeName)) {
      return "new java.util.HashMap<>()";
    }
    return "";
  }
  
  public String getNativeTypeName(ASTCDAttribute attribute) {
    if (isOptional(attribute)) {
      return TypesHelper
          .printType(TypesHelper.getSimpleReferenceTypeFromOptional(attribute.getType()));
          
    }
    if (isListAstNode(attribute)) {
      Optional<ASTSimpleReferenceType> typeArg = TypesHelper
          .getFirstTypeArgumentOfGenericType(attribute.getType(), JAVA_LIST);
      if (typeArg.isPresent()) {
        return printType(typeArg.get());
      }
    }
    return attribute.printType();
  }
  
  public List<String> getASTESuperPackages() {
    List<String> ePackages = new ArrayList<>();
    for (String superGrammar : getSuperGrammarCds()) {
      ePackages.add(getEPackageName(superGrammar));
    }
    if (ePackages.isEmpty()) {
      ePackages.add(ASTENodePackage.class.getName());
    }
    return ePackages;
  }
  
  /**
   * Get all native (not created by decorators) cd types
   * 
   * @param cdDefinition
   * @return
   */
  public List<ASTCDType> getNativeTypes(ASTCDDefinition cdDefinition) {
    List<ASTCDType> types = new ArrayList<>(cdDefinition.getCDClasses());
    types.addAll(cdDefinition.getCDInterfaces());
    String genNode = BaseInterfaceAddingManipulation.getBaseInterfaceName(getCdDefinition());
    return types.stream().filter(c -> !c.getName().equals(genNode))
        .collect(Collectors.toList());
  }
  
  public boolean attributeDefinedInOtherCd(ASTCDAttribute attribute) {
    String definedGrammar = getDefinedGrammarName(attribute);
    return !definedGrammar.isEmpty()
        && !definedGrammar.equalsIgnoreCase(getQualifiedCdName());
  }
  
  public String getDefinedGrammarName(ASTCDAttribute attribute) {
    String type = getNativeTypeName(attribute);
    if (isAstNode(attribute) || isListAstNode(attribute) || isOptional(attribute)) {
      return Names.getQualifier(Names.getQualifier(type));
    }
    return type;
  }
  
  /**
   * Gets super types recursively (without duplicates - the first occurrence in
   * the type hierarchy is used) in the order according to the EMF-generator
   * requirements
   * 
   * @param type
   * @return all supertypes (without the type itself)
   */
  public List<CDTypeSymbol> getAllSuperTypesEmfOrder(ASTCDType type) {
    if (!type.getSymbol().isPresent()) {
      Log.error("0xA4097 Could not load symbol information for " + type.getName() + ".");
    }
    
    CDTypeSymbol sym = (CDTypeSymbol) type.getSymbol().get();
    return getAllSuperTypesEmfOrder(sym);
  }
  
  /**
   * Gets super types recursively (without duplicates - the first occurrence in
   * the type hierarchy is used) in the order according to the EMF-generator
   * requirements
   * 
   * @param type
   * @return all supertypes (without the type itself)
   */
  public List<CDTypeSymbol> getAllSuperTypesEmfOrder(CDTypeSymbol type) {
    List<CDTypeSymbol> allSuperTypes = new ArrayList<>();
    for (CDTypeSymbol s : type.getSuperTypes()) {
      List<CDTypeSymbol> supers = getAllSuperTypesEmfOrder(s);
      for (CDTypeSymbol sup : supers) {
        addIfNotContained(sup, allSuperTypes);
      }
      addIfNotContained(s, allSuperTypes);
    }
    return allSuperTypes;
  }
  
  /**
   * Gets super types recursively (without duplicates - the first occurrence in
   * the type hierarchy is used) in the order according to the EMF-generator
   * requirements
   * 
   * @param type
   * @return all supertypes (without the type itself)
   */
  public List<CDTypeSymbol> getAllTypesEmfOrder(ASTCDType type) {
    if (!type.getSymbol().isPresent()) {
      Log.error("0xA4098 Could not load symbol information for " + type.getName() + ".");
    }
    CDTypeSymbol sym = (CDTypeSymbol) type.getSymbol().get();
    List<CDTypeSymbol> types = getAllSuperTypesEmfOrder(sym);
    types.add(sym);
    return types;
  }
  
  /**
   * TODO: Write me!
   * 
   * @param cdType
   * @return
   */
  public Collection<CDFieldSymbol> getAllVisibleFields(ASTCDType type) {
    List<CDFieldSymbol> allSuperTypeFields = new ArrayList<>();
    if (!type.getSymbol().isPresent()) {
      Log.error("0xA4099 Could not load symbol information for " + type.getName() + ".");
      return new ArrayList<>();
    }
    CDTypeSymbol sym = (CDTypeSymbol) type.getSymbol().get();
    for (CDTypeSymbol sup : getAllSuperTypesEmfOrder(sym)) {
      sup.getFields().forEach(a -> addIfNotContained(a, allSuperTypeFields));
    }
    // filter-out all private fields
    List<CDFieldSymbol> allFields = allSuperTypeFields.stream()
        .filter(field -> !field.isPrivate()).collect(Collectors.toList());
    // add own fields if not inherited
    sym.getFields().stream()
        .filter(e -> !isAttributeOfSuperType(e, sym)).forEach(allFields::add);
    return allFields;
  }
  
  // TODO: fix me
  public boolean isExternal(ASTCDAttribute attribute) {
    return getNativeTypeName(attribute).endsWith("Ext");
  }
  
  public String getDefaultValue(CDFieldSymbol attribute) {
    if (isAstNode(attribute)) {
      return "null";
    }
    if (isOptional(attribute)) {
      return "Optional.empty()";
    }
    String typeName = attribute.getType().getName();
    switch (typeName) {
      case "boolean":
        return "false";
      case "int":
        return "0";
      case "short":
        return "(short) 0";
      case "long":
        return "0";
      case "float":
        return "0.0f";
      case "double":
        return "0.0";
      case "char":
        return "'\u0000'";
      default:
        return "null";
    }
  }
  
  /**
   * Computes the lower bound of the cardinality of the given attribute.
   * 
   * @param emfAttribute The attribute to retrieve the cardinality for.
   * @return The corresponding lower bound cardinality of the attribute.
   */
  public String lowerBoundCardinality(EmfAttribute emfAttribute) {
    // check for custom cardinality
    ASTCDAttribute cdAttribute = emfAttribute.getCdAttribute();
    if (cdAttribute.getModifier().isPresent()) {
      ASTModifier modifier = cdAttribute.getModifier().get();
      if (modifier.getStereotype().isPresent()) {
        List<ASTStereoValue> stereoValueList = modifier.getStereotype().get().getValues();
        for (ASTStereoValue stereoValue : stereoValueList) {
          if (stereoValue.getName().equals(MC2CDStereotypes.MIN_CARD.toString())) {
            return stereoValue.getValue().get();
          }
        }
      }
    }
    
    // return default lower bound cardinality
    return "0";
  }
  
  /**
   * Computes the upper bound of the cardinality of the given attribute.
   * 
   * @param emfAttribute The attribute to retrieve the cardinality for.
   * @return The corresponding upper bound cardinality of the attribute.
   */
  public String upperBoundCardinality(EmfAttribute emfAttribute) {
    // check for custom cardinality
    ASTCDAttribute cdAttribute = emfAttribute.getCdAttribute();
    if (cdAttribute.getModifier().isPresent()) {
      ASTModifier modifier = cdAttribute.getModifier().get();
      if (modifier.getStereotype().isPresent()) {
        List<ASTStereoValue> stereoValueList = modifier.getStereotype().get().getValues();
        for (ASTStereoValue stereoValue : stereoValueList) {
          if (stereoValue.getName().equals(MC2CDStereotypes.MAX_CARD.toString())) {
            return stereoValue.getValue().get();
          }
        }
      }
    }
    
    // check for list of return default upper bound cardinality
    if (emfAttribute.isAstNode()) {
      return "1";
    } else if (emfAttribute.isAstList() || istJavaList(emfAttribute.getCdAttribute())) {
      return "-1";
    } else {
      return "1";
    }
  }
  
  public static String getEPackageName(String qualifiedSuperGrammar) {
    return qualifiedSuperGrammar.toLowerCase() + "._ast."
        + StringTransformations.capitalize(Names.getSimpleName(qualifiedSuperGrammar)) + "Package";
  }
  
  public String getIdentifierName(String qualifiedName) {
    return Names.getSimpleName(qualifiedName) + "_"
        + Names.getQualifier(qualifiedName).replace('.', '_');
  }
  
  public static boolean istJavaList(ASTCDAttribute attribute) {
    return TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(attribute.getType())
        .equals(JAVA_LIST);
  }
  
  public static String getEmfRuntimePackage() {
    return "de.monticore.emf._ast";
  }
  
  public static String getEDataType(String typeName) {
    switch (typeName) {
      case JAVA_LIST:
        return "Elist";
      case JAVA_MAP:
        return "EMap";
      case "boolean":
        return "EBoolean";
      case "Boolean":
        return "EBooleanObject";
      case "int":
        return "EInt";
      case "Integer":
        return "EIntegerObject";
      case "byte":
        return "EByte";
      case "Byte":
        return "EByteObject";
      case "short":
        return "EShort";
      case "Short":
        return "EShortObject";
      case "long":
        return "ELong";
      case "Long":
        return "ELongObject";
      case "double":
        return "EDouble";
      case "Double":
        return "EDoubleObject";
      case "float":
        return "EFloat";
      case "Float":
        return "EFloatObject";
      case "char":
        return "EChar";
      case "Character":
        return "ECharacterObject";
      default:
        return "E" + typeName;
    }
  }
  
  public static String getSuperClass(ASTCDClass clazz) {
    if (!clazz.getSuperclass().isPresent()) {
      return ASTECNode.class.getName();
    }
    return clazz.printSuperClass();
  }
  
  public static List<EmfAttribute> getSortedEmfAttributes(List<EmfAttribute> list) {
    List<EmfAttribute> sortedAttributes = new ArrayList<>(list);
    Collections.sort(sortedAttributes, new Comparator<EmfAttribute>() {
      public int compare(EmfAttribute attr1, EmfAttribute attr2) {
        return attr1.getAttributeName().compareTo(attr2.getAttributeName());
      }
    });
    return sortedAttributes;
  }
  
  public static void sortEmfAttributes(List<EmfAttribute> list) {
    Collections.sort(list, new Comparator<EmfAttribute>() {
      public int compare(EmfAttribute attr1, EmfAttribute attr2) {
        return attr1.getAttributeName().compareTo(attr2.getAttributeName());
      }
    });
  }
  
  /**
   * Retrieves the production graph for the class and splits it into its root
   * branches.
   * 
   * @param clazz The class that requires the graph.
   * @return The corresponding production graph branches.
   */
  public static List<ProductionGraph> getPGBranches(String grammar, ASTCDClass clazz) {
    String nonterminal = clazz.getName().substring(3);
    ProductionGraph graph = ProductionGraphHelper.getProductionGraph(grammar, nonterminal);
    
    List<ProductionGraph> branches = new ArrayList<ProductionGraph>();
    if (graph != null) {
      branches.addAll(graph.getEntries());
    }
    return branches;
  }
  
  /**
   * Extracts the set of equations in a production graph.
   * 
   * @param branch The branch to be observed.
   * @param clazz The corresponding class.
   * @return The equations for the corresponding equation system.
   */
  public static List<String> extractPGEquations(ProductionGraph branch, ASTCDClass clazz) {
    List<String> equations = new ArrayList<String>();
    List<ASTCDAttribute> variables = clazz.getCDAttributes();
    
    // perform extraction for each variable
    for (ASTCDAttribute var : variables) {
      // skip resolved nonterminal references
      if (isResolvedNonterminalReference(var)) {
        continue;
      }
      
      List<String> partialEquations = new ArrayList<String>();
      extractEquationsForVariable(var.getName(), partialEquations, branch);
      
      String var_size = var.getName() + "_size";
      String equation = "(" + var_size + " == ";
      for (String eq : partialEquations) {
        equation += eq + " + ";
      }
      equation = equation.substring(0, equation.length() - 3);
      equation += ")";
      equations.add(equation);
    }
    
    return equations;
  }
  
  /**
   * Provides the constraints for the linear equation system.
   * 
   * @param branch The source production graph.
   * @return The constraints of the linear equation system.
   */
  public static List<String> extractPGConstraints(ProductionGraph branch) {
    List<String> constraints = new ArrayList<>();
    extractConstraints(branch, constraints);
    return constraints;
  }
  
  /**
   * Provides the paramters for the linear equation system.
   * 
   * @param branch The source production graph.
   * @return The parameters of the linear equation system.
   */
  public static List<String> getParameters(ProductionGraph branch) {
    List<String> params = new ArrayList<String>();
    extractParams(branch, params);
    return params;
  }
  
  /**
   * Check whether the attribute is a resolved nonterminal reference.
   * 
   * @param attr The investigated attribute.
   * @return The boolean value whether the attribute is a resolved NTref.
   */
  private static boolean isResolvedNonterminalReference(ASTCDAttribute attr) {
    if (attr.getModifier().isPresent()) {
      ASTModifier modifier = attr.getModifier().get();
      if (modifier.getStereotype().isPresent()) {
        List<ASTStereoValue> stereoValueList = modifier.getStereotype().get().getValues();
        for (ASTStereoValue stereoValue : stereoValueList) {
          if (stereoValue.getName().equals(MC2CDStereotypes.ASSOCIATION.toString())) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  /**
   * Retrieves the constraints for the linear equation system and stores these in
   * a list.
   * 
   * @param branch The source production graph.
   * @param constraints The constraint list that contains the les parameters.
   */
  private static void extractConstraints(ProductionGraph branch, List<String> constraints) {
    // extract constraints
    String param1 = branch.getLabel();
    if (param1 != null) {
      // check direct entries
      if (branch.isBlock()) {
        for (ProductionGraph entry : branch.getEntries()) {
          String param2 = "";
          if (entry.getLabel() != null) {
            param2 = entry.getLabel();
          }
          String constraint = "((" + param2 + " == 0) || (" + param1 + " > 0))";
          constraints.add(constraint);
        }
      }
      // children of entries
      ProductionGraph current = branch;
      while (!current.getChildNodes().isEmpty()) {
        current = current.getChildNodes().get(0);
        if (current.isBlock()) {
          for (ProductionGraph entry : current.getEntries()) {
            String param2 = "";
            if (entry.getLabel() != null) {
              param2 = entry.getLabel();
            }
            String constraint = "((" + param2 + " == 0) || (" + param1 + " > 0))";
            constraints.add(constraint);
          }
        }
      }
    }
    
    // apply recursively for subgraphs
    ProductionGraph current = branch;
    if (current.isBlock()) {
      for (ProductionGraph entry : current.getEntries()) {
        extractConstraints(entry, constraints);
      }
    }
    while (!current.getChildNodes().isEmpty()) {
      current = current.getChildNodes().get(0);
      if (current.isBlock()) {
        for (ProductionGraph entry : current.getEntries()) {
          extractConstraints(entry, constraints);
        }
      }
    }
  }
  
  /**
   * Retrieves the parameters for the linear equation system and stores these in
   * a list.
   * 
   * @param branch The source production graph.
   * @param params The parameter list that contains the les parameters.
   */
  private static void extractParams(ProductionGraph branch, List<String> params) {
    // extract paramters in current graph
    ProductionGraph current = branch;
    if (current.isBlock()) {
      for (ProductionGraph entry : current.getEntries()) {
        if (entry.getLabel() != null) {
          params.add(entry.getLabel());
        }
      }
    }
    while (!current.getChildNodes().isEmpty()) {
      current = current.getChildNodes().get(0);
      if (current.isBlock()) {
        for (ProductionGraph entry : current.getEntries()) {
          if (entry.getLabel() != null) {
            params.add(entry.getLabel());
          }
        }
      }
    }
    
    // apply recursively
    current = branch;
    if (current.isBlock()) {
      for (ProductionGraph entry : current.getEntries()) {
        extractParams(entry, params);
      }
    }
    while (!current.getChildNodes().isEmpty()) {
      current = current.getChildNodes().get(0);
      if (current.isBlock()) {
        for (ProductionGraph entry : current.getEntries()) {
          extractParams(entry, params);
        }
      }
    }
  }
  
  /**
   * Adds equations for a given variable in a production graph branch.
   * 
   * @param partialEquations The list of partialEquations that is extracted for
   *          each block.
   * @param branch The observed branch of the production graph.
   */
  private static void extractEquationsForVariable(String name, List<String> partialEquations, ProductionGraph branch) {
    // extract equations
    if (branch.getParentNodes().size() == 1 && branch.getParentNodes().get(0).isRoot()) {
      int count = getPlainCount(name, branch);
      String equation = " " + count;
      partialEquations.add(equation);
    }
    else {
      String param = branch.getLabel();
      int count = getPlainCount(name, branch);
      String equation = count + " * " + param;
      partialEquations.add(equation);
    }
    
    // apply recursively
    ProductionGraph current = branch;
    if (current.isBlock()) {
      for (ProductionGraph entry : current.getEntries()) {
        extractEquationsForVariable(name, partialEquations, entry);
      }
    }
    while (!current.getChildNodes().isEmpty()) {
      current = current.getChildNodes().get(0);
      if (current.isBlock()) {
        for (ProductionGraph entry : current.getEntries()) {
          extractEquationsForVariable(name, partialEquations, entry);
        }
      }
    }
    
  }
  
  /**
   * Extracts the occurrences of the variable identified by its name in the
   * production graph. This extractions works only on the attribute nodes and
   * ignores blocks.
   * 
   * @param name The identifier for the variable.
   * @param branch The observed production graph branch.
   * @return The number of variable instantiations.
   */
  private static int getPlainCount(String name, ProductionGraph branch) {
    int res = 0;
    ProductionGraph current = branch;
    
    // count occurrences in child nodes
    if (current.getAttributeName() != null && current.getAttributeName().equals(name)) {
      res++;
    }
    while (!current.getChildNodes().isEmpty()) {
      current = current.getChildNodes().get(0);
      if (current.getAttributeName() != null && current.getAttributeName().equals(name)) {
        res++;
      }
    }
    
    return res;
  }
}
