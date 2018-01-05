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

package de.monticore.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.expressions.prettyprint.JavaClassExpressionsPrettyPrinter;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.javaclassexpressions._ast.ASTGenericInvocationSuffix;
import de.monticore.javaclassexpressions._ast.ASTSuperSuffix;
import de.monticore.testjavaclassexpressions._ast.ASTELiteral;
import de.monticore.testjavaclassexpressions._ast.ASTEReturnType;
import de.monticore.testjavaclassexpressions._ast.ASTEType;
import de.monticore.testjavaclassexpressions._ast.ASTETypeArguments;
import de.monticore.testjavaclassexpressions._ast.ASTPrimaryExpression;
import de.monticore.testjavaclassexpressions._parser.TestJavaClassExpressionsParser;
import de.monticore.testjavaclassexpressions._visitor.TestJavaClassExpressionsVisitor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class JavaClassExpressionsPrettyPrinterTest{
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  static class PrimaryPrettyPrinter extends JavaClassExpressionsPrettyPrinter
      implements TestJavaClassExpressionsVisitor {
    
    private TestJavaClassExpressionsVisitor realThis;
    
    @Override
    public void visit(ASTPrimaryExpression node) {
      getPrinter().print((node.getName()));
    }
    
    public PrimaryPrettyPrinter(IndentPrinter printer) {
      super(printer);
      realThis = this;
    }
    
    @Override
    public TestJavaClassExpressionsVisitor getRealThis() {
      return realThis;
    }
    
    @Override
    public void visit(ASTELiteral node) {
      getPrinter().print(node.getName());
    }
    
    @Override
    public void visit(ASTEType node) {
      if (node.getDouble().isPresent()) {
        getPrinter().print(node.getDouble().get());
      }
      if (node.getInt().isPresent()) {
        getPrinter().print(node.getInt().get());
      }
      if (node.getLong().isPresent()) {
        getPrinter().print(node.getLong().get());
      }
      if (node.getFloat().isPresent()) {
        getPrinter().print(node.getFloat().get());
      }
    }
    
    @Override
    public void handle(ASTEReturnType node) {
      if (node.getEType().isPresent()) {
        node.getEType().get().accept(this);
      }
      if (node.getVoid().isPresent()) {
        getPrinter().print(node.getVoid().get());
      }
    }
    
    @Override
    public void visit(ASTETypeArguments node) {
      getPrinter().print("<");
      for (String s : node.getNames()) {
        getPrinter().print(s);
      }
      getPrinter().print(">");
    }
  }
  
 
  
  @Test
  public void testPrimarySuperExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("super"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testSuperSuffixArguments() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTSuperSuffix> ast = parser.parseSuperSuffix(new StringReader("(a,b,c)"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTSuperSuffix assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseSuperSuffix(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testSuperSuffixETypeArguments() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTSuperSuffix> ast = parser.parseSuperSuffix(new StringReader(".<Arg>Name(a,b,c)"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTSuperSuffix assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseSuperSuffix(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testSuperExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("expression.super(a,b,c)"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testLiteralExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("Ename"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testClassExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("void.class"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testGenericSuperInvocationSuffix() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("super(a,b,c)"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testGenericThisInvocationSuffix() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTGenericInvocationSuffix> ast = parser.parseGenericInvocationSuffix(new StringReader("this(a,b,c)"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTGenericInvocationSuffix assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseGenericInvocationSuffix(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testGenericNameInvocationSuffix() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("Name(a,b,c)"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testTypeCastExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("(int)expression"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testPrimaryGenericInvocationExpression() throws IOException {    
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("<Arg>super(a,b,c)"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testGenericInvocationExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("exp.<Arg>super(a,b,c)"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
    
    
  }
  
  @Test
  public void testNameExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("Name"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testInstanceofExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("a instanceof int"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
 
}