<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
-->
${tc.signature("ast", "grammarName", "genHelper")}

  <#list ast.getCDAttributes() as attr>
    if (!eIsSet(${grammarName}Package.${ast.getName()}_${genHelper.getNativeAttributeName(attr.getName())?cap_first})) {
      ${attr.getName()}_size = 0;
    }
  </#list>
  
  int max = 0;
  <#list ast.getCDAttributes() as attr>
  max = max > ${attr.getName()}_size ? max : ${attr.getName()}_size;
  </#list>
  
  <#assign branches = genHelper.getPGBranches(grammarName, ast)>
  <#list branches as branch>
    <#list genHelper.getParameters(branch) as param>
      for (int ${param} = 0; ${param} < max; ${param}++) {
    </#list>
    if (<#list genHelper.extractPGEquations(branch, ast) as eq> ${eq} && </#list>
    <#list genHelper.extractPGConstraints(branch) as con> ${con} && </#list> true) {
      return true;
    }
    <#list genHelper.getParameters(branch) as param>
      }
    </#list>
  </#list>
  
  
  <#if branches?has_content>
    return false;
  <#else>
    return true;
  </#if>