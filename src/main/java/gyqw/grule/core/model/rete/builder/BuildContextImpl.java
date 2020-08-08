//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gyqw.grule.core.model.rete.builder;

import gyqw.grule.core.exception.RuleException;
import gyqw.grule.core.model.library.ResourceLibrary;
import gyqw.grule.core.model.library.variable.VariableCategory;
import gyqw.grule.core.model.rete.ObjectTypeNode;
import gyqw.grule.core.model.rule.CommonFunctionValue;
import gyqw.grule.core.model.rule.ComplexArithmetic;
import gyqw.grule.core.model.rule.MethodValue;
import gyqw.grule.core.model.rule.Parameter;
import gyqw.grule.core.model.rule.ParameterValue;
import gyqw.grule.core.model.rule.ParenValue;
import gyqw.grule.core.model.rule.Rule;
import gyqw.grule.core.model.rule.Value;
import gyqw.grule.core.model.rule.VariableCategoryValue;
import gyqw.grule.core.model.rule.VariableValue;
import gyqw.grule.core.model.rule.lhs.AbstractLeftPart;
import gyqw.grule.core.model.rule.lhs.BaseCriteria;
import gyqw.grule.core.model.rule.lhs.CommonFunctionLeftPart;
import gyqw.grule.core.model.rule.lhs.CommonFunctionParameter;
import gyqw.grule.core.model.rule.lhs.Criteria;
import gyqw.grule.core.model.rule.lhs.FunctionLeftPart;
import gyqw.grule.core.model.rule.lhs.LeftPart;
import gyqw.grule.core.model.rule.lhs.MethodLeftPart;
import gyqw.grule.core.model.rule.lhs.NamedCriteria;
import gyqw.grule.core.model.rule.lhs.VariableLeftPart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BuildContextImpl implements BuildContext {
    private ResourceLibrary resourceLibrary;
    private List<ObjectTypeNode> objectTypeNodes;
    private IdGenerator idGenerator;
    private Rule currentRule;

    public BuildContextImpl(ResourceLibrary resourceLibrary, List<ObjectTypeNode> objectTypeNodes) {
        this.resourceLibrary = resourceLibrary;
        this.objectTypeNodes = objectTypeNodes;
        this.idGenerator = new IdGenerator();
    }

    public BuildContextImpl(List<ObjectTypeNode> objectTypeNodes, BuildContext parentContent) {
        this.resourceLibrary = parentContent.getResourceLibrary();
        this.objectTypeNodes = objectTypeNodes;
        this.idGenerator = parentContent.getIdGenerator();
    }

    public boolean assertSameType(BaseCriteria left, BaseCriteria right) {
        VariableCategory leftCategory = this.fetchVariableCategory(left);
        VariableCategory rightCategory = this.fetchVariableCategory(right);
        return leftCategory != null && rightCategory != null ? leftCategory.getClazz().equals(rightCategory.getClazz()) : false;
    }

    private VariableCategory fetchVariableCategory(BaseCriteria criteria) {
        VariableCategory vc = null;
        if (criteria instanceof Criteria) {
            Criteria c = (Criteria) criteria;
            LeftPart leftLeftPart = c.getLeft().getLeftPart();
            if (leftLeftPart instanceof VariableLeftPart) {
                VariableLeftPart part = (VariableLeftPart) leftLeftPart;
                vc = this.resourceLibrary.getVariableCategory(part.getVariableCategory());
            }
        } else {
            if (!(criteria instanceof NamedCriteria)) {
                throw new RuleException("Unknow Criteria : " + criteria);
            }

            NamedCriteria c = (NamedCriteria) criteria;
            vc = this.resourceLibrary.getVariableCategory(c.getVariableCategory());
        }

        return vc;
    }

    public List<String> getObjectType(BaseCriteria criteria) {
        List<String> typeList = new ArrayList();
        if (!(criteria instanceof Criteria)) {
            throw new RuleException("Unknow Criteria : " + criteria);
        } else {
            Criteria c = (Criteria) criteria;
            LeftPart leftPart = c.getLeft().getLeftPart();
            if (leftPart instanceof VariableLeftPart) {
                VariableLeftPart varPart = (VariableLeftPart) leftPart;
                String variableCategory = varPart.getVariableCategory();
                VariableCategory category = this.resourceLibrary.getVariableCategory(variableCategory);
                typeList.add(category.getClazz());
            } else if (leftPart instanceof AbstractLeftPart) {
                AbstractLeftPart lp = (AbstractLeftPart) leftPart;
                VariableCategory category = this.resourceLibrary.getVariableCategory(lp.getVariableCategory());
                typeList.add(category.getClazz());
            } else if (leftPart instanceof CommonFunctionLeftPart) {
                CommonFunctionLeftPart funPart = (CommonFunctionLeftPart) leftPart;
                CommonFunctionParameter parameter = funPart.getParameter();
                Value value = parameter.getObjectParameter();
                this.buildTypes(value, typeList);
            } else {
                Parameter param;
                Value value;
                List params;
                Iterator var19;
                if (leftPart instanceof MethodLeftPart) {
                    MethodLeftPart methodLeftPart = (MethodLeftPart) leftPart;
                    params = methodLeftPart.getParameters();
                    if (params != null) {
                        var19 = params.iterator();

                        while (var19.hasNext()) {
                            param = (Parameter) var19.next();
                            value = param.getValue();
                            this.buildTypes(value, typeList);
                        }
                    }
                } else if (leftPart instanceof FunctionLeftPart) {
                    FunctionLeftPart funLeftPart = (FunctionLeftPart) leftPart;
                    params = funLeftPart.getParameters();
                    if (params != null) {
                        var19 = params.iterator();

                        while (var19.hasNext()) {
                            param = (Parameter) var19.next();
                            value = param.getValue();
                            this.buildTypes(value, typeList);
                        }
                    }
                }
            }

            ComplexArithmetic arithmetic = c.getLeft().getArithmetic();
            if (arithmetic != null) {
                this.buildTypes(arithmetic.getValue(), typeList);
            }

            if (typeList.size() == 0) {
                typeList.add("*");
            }

            return typeList;
        }
    }

    private void buildTypes(Value value, List<String> list) {
        if (value != null) {
            Value pValue;
            if (value instanceof CommonFunctionValue) {
                CommonFunctionValue funValue = (CommonFunctionValue) value;
                CommonFunctionParameter funParam = funValue.getParameter();
                Value paramValue = funParam.getObjectParameter();
                this.buildTypes(paramValue, list);
            } else if (value instanceof MethodValue) {
                MethodValue methodValue = (MethodValue) value;
                List<Parameter> params = methodValue.getParameters();
                if (params != null) {
                    Iterator var17 = params.iterator();

                    while (var17.hasNext()) {
                        Parameter param = (Parameter) var17.next();
                        Value v = param.getValue();
                        this.buildTypes(v, list);
                    }
                }
            } else {
                String categoryName;
                if (value instanceof ParameterValue) {
                    VariableCategory category = this.resourceLibrary.getVariableCategory("参数");
                    categoryName = category.getClazz();
                    if (!list.contains(categoryName)) {
                        list.add(categoryName);
                    }
                } else if (value instanceof ParenValue) {
                    ParenValue parenValue = (ParenValue) value;
                    pValue = parenValue.getValue();
                    this.buildTypes(pValue, list);
                } else {
                    VariableCategory category;
                    String className;
                    if (value instanceof VariableCategoryValue) {
                        VariableCategoryValue varValue = (VariableCategoryValue) value;
                        categoryName = varValue.getVariableCategory();
                        category = this.resourceLibrary.getVariableCategory(categoryName);
                        className = category.getClazz();
                        if (!list.contains(className)) {
                            list.add(className);
                        }
                    } else if (value instanceof VariableValue) {
                        VariableValue variableValue = (VariableValue) value;
                        categoryName = variableValue.getVariableCategory();
                        category = this.resourceLibrary.getVariableCategory(categoryName);
                        className = category.getClazz();
                        if (!list.contains(className)) {
                            list.add(className);
                        }
                    }
                }
            }

            ComplexArithmetic arithmetic = value.getArithmetic();
            if (arithmetic != null) {
                pValue = arithmetic.getValue();
                this.buildTypes(pValue, list);
            }

        }
    }

    public ObjectTypeNode buildObjectTypeNode(String className) {
        ObjectTypeNode targetObjectTypeNode = null;
        Iterator var3 = this.objectTypeNodes.iterator();

        while (var3.hasNext()) {
            ObjectTypeNode typeNode = (ObjectTypeNode) var3.next();
            if (typeNode.support(className)) {
                targetObjectTypeNode = typeNode;
                break;
            }
        }

        if (targetObjectTypeNode == null) {
            targetObjectTypeNode = new ObjectTypeNode(className, this.nextId());
            this.objectTypeNodes.add(targetObjectTypeNode);
        }

        return targetObjectTypeNode;
    }

    public ResourceLibrary getResourceLibrary() {
        return this.resourceLibrary;
    }

    public int nextId() {
        return this.idGenerator.nextId();
    }

    public IdGenerator getIdGenerator() {
        return this.idGenerator;
    }

    public void setCurrentRule(Rule rule) {
        this.currentRule = rule;
    }

    public boolean currentRuleIsDebug() {
        if (this.currentRule == null) {
            return false;
        } else {
            return this.currentRule.getDebug() != null && this.currentRule.getDebug();
        }
    }
}
