/*******************************************************************************
 * Copyright 2017 Bstek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package gyqw.grule.core.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import gyqw.grule.core.runtime.BuiltInActionLibraryBuilder;
import org.dom4j.Element;

import gyqw.grule.core.builder.resource.Resource;
import gyqw.grule.core.builder.resource.ResourceBuilder;
import gyqw.grule.core.builder.resource.ResourceType;
import gyqw.grule.core.model.library.ResourceLibrary;
import gyqw.grule.core.model.library.action.ActionLibrary;
import gyqw.grule.core.model.library.action.SpringBean;
import gyqw.grule.core.model.library.constant.ConstantLibrary;
import gyqw.grule.core.model.library.variable.Variable;
import gyqw.grule.core.model.library.variable.VariableCategory;
import gyqw.grule.core.model.library.variable.VariableLibrary;
import gyqw.grule.core.model.rule.Library;

/**
 * @author Jacky.gao
 * @author fred
 * 2015年2月16日
 */
public class ResourceLibraryBuilder extends AbstractBuilder {
    private BuiltInActionLibraryBuilder builtInActionLibraryBuilder;

    @SuppressWarnings("unchecked")
    public ResourceLibrary buildResourceLibrary(Collection<Library> libraries) {
        if (libraries == null) {
            libraries = Collections.EMPTY_LIST;
        }
        List<ConstantLibrary> constantLibraryLibs = new ArrayList<>();
        List<ActionLibrary> actionLibraryLibs = new ArrayList<>();
        List<VariableLibrary> variableCategoryLibs = new ArrayList<>();
        List<VariableCategory> parameterVariableCategories = new ArrayList<>();
        ResourceBase resourceBase = newResourceBase();
        for (Library lib : libraries) {
            resourceBase.addResource(lib.getPath(), lib.getVersion());
        }
        for (Resource resource : resourceBase.getResources()) {
            String content = resource.getContent();
            Element root = parseResource(content);
            for (ResourceBuilder<?> builder : resourceBuilders) {
                if (!builder.support(root)) {
                    continue;
                }
                Object object = builder.build(root);
                ResourceType type = builder.getType();
                if (type.equals(ResourceType.ActionLibrary)) {
                    ActionLibrary al = (ActionLibrary) object;
                    actionLibraryLibs.add(al);
                } else if (type.equals(ResourceType.VariableLibrary)) {
                    VariableLibrary vl = (VariableLibrary) object;
                    variableCategoryLibs.add(vl);
                } else if (type.equals(ResourceType.ConstantLibrary)) {
                    ConstantLibrary cl = (ConstantLibrary) object;
                    constantLibraryLibs.add(cl);
                } else if (type.equals(ResourceType.ParameterLibrary)) {
                    VariableCategory category = (VariableCategory) object;
                    parameterVariableCategories.add(category);
                }
                break;
            }
        }
        if (parameterVariableCategories.size() > 0) {
            VariableCategory category = parameterVariableCategories.get(0);
            for (VariableCategory vc : parameterVariableCategories) {
                if (vc.equals(category)) {
                    continue;
                }
                if (vc.getVariables() == null) {
                    continue;
                }
                for (Variable v : vc.getVariables()) {
                    category.addVariable(v);
                }
            }
            VariableLibrary parameterLib = new VariableLibrary();
            parameterLib.addVariableCategory(category);
            variableCategoryLibs.add(parameterLib);
        }
        List<SpringBean> builtInActions = builtInActionLibraryBuilder.getBuiltInActions();
        if (builtInActions.size() > 0) {
            ActionLibrary al = new ActionLibrary();
            al.setSpringBeans(builtInActions);
            actionLibraryLibs.add(al);
        }
        return new ResourceLibrary(variableCategoryLibs, actionLibraryLibs, constantLibraryLibs);
    }

    public void setBuiltInActionLibraryBuilder(
            BuiltInActionLibraryBuilder builtInActionLibraryBuilder) {
        this.builtInActionLibraryBuilder = builtInActionLibraryBuilder;
    }
}
