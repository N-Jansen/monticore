<#--
***************************************************************************************
Copyright (c) 2017, MontiCore
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
***************************************************************************************
-->
${signature("className", "ruleNames", "existsHW")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign grammarName = ast.getName()?cap_first>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign skipSTGen = glex.getGlobalVar("skipSTGen")>

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${package};

import java.util.Optional;
<#if !stHelper.getGrammarSymbol().isComponent()>
import ${fqn}._parser.${grammarName}Parser;
</#if>
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;

public abstract class ${className} extends de.monticore.CommonModelingLanguage {

  public ${className}(String langName, String fileEnding) {
    super(langName, fileEnding);

    initResolvingFilters();
<#if !skipSTGen>
    setModelNameCalculator(new ${grammarName}ModelNameCalculator());
</#if>
  }

  <#if !stHelper.getGrammarSymbol().isComponent()>
  @Override
  public ${grammarName}Parser getParser() {
    return new ${grammarName}Parser();
  }
  </#if>

  <#if !skipSTGen>
  @Override
  public Optional<${grammarName}SymbolTableCreator> getSymbolTableCreator(
      ResolvingConfiguration resolvingConfiguration, MutableScope enclosingScope) {
    return Optional.of(new ${grammarName}SymbolTableCreator(resolvingConfiguration, enclosingScope));
  }
  </#if>

  @Override
  public ${grammarName}ModelLoader getModelLoader() {
    return (${grammarName}ModelLoader) super.getModelLoader();
  }

  <#if existsHW>/*</#if>
  @Override
  protected ${grammarName}ModelLoader provideModelLoader() {
    return new ${grammarName}ModelLoader(this);
  }
  <#if existsHW>*/</#if>

  protected void initResolvingFilters() {
    <#list ruleNames as ruleName>
    addResolvingFilter(new ${ruleName?cap_first}ResolvingFilter());
    </#list>
  }
}
