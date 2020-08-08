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
package gyqw.grule.core.dsl.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gyqw.grule.core.dsl.RuleParserParser;
import gyqw.grule.core.exception.RuleException;
import gyqw.grule.core.model.function.FunctionDescriptor;
import gyqw.grule.core.model.library.variable.VariableCategory;
import gyqw.grule.core.model.rule.Op;
import gyqw.grule.core.model.rule.Parameter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import gyqw.grule.core.dsl.DSLUtils;
import gyqw.grule.core.model.rule.lhs.AllLeftPart;
import gyqw.grule.core.model.rule.lhs.CollectLeftPart;
import gyqw.grule.core.model.rule.lhs.CollectPurpose;
import gyqw.grule.core.model.rule.lhs.CommonFunctionLeftPart;
import gyqw.grule.core.model.rule.lhs.CommonFunctionParameter;
import gyqw.grule.core.model.rule.lhs.Criteria;
import gyqw.grule.core.model.rule.lhs.EvalLeftPart;
import gyqw.grule.core.model.rule.lhs.ExistLeftPart;
import gyqw.grule.core.model.rule.lhs.FunctionLeftPart;
import gyqw.grule.core.model.rule.lhs.JunctionType;
import gyqw.grule.core.model.rule.lhs.Left;
import gyqw.grule.core.model.rule.lhs.LeftPart;
import gyqw.grule.core.model.rule.lhs.LeftType;
import gyqw.grule.core.model.rule.lhs.MethodLeftPart;
import gyqw.grule.core.model.rule.lhs.MultiCondition;
import gyqw.grule.core.model.rule.lhs.PropertyCriteria;
import gyqw.grule.core.model.rule.lhs.StatisticType;
import gyqw.grule.core.model.rule.lhs.VariableLeftPart;

/**
 * @author Jacky.gao
 * @since 2015年2月15日
 */
public class CriteriaContextBuilder extends AbstractContextBuilder implements ApplicationContextAware{
	private Collection<FunctionDescriptor> functionDescriptors;
	@Override
	public Criteria build(ParserRuleContext context) {
		RuleParserParser.SingleConditionContext ctx=(RuleParserParser.SingleConditionContext)context;
		RuleParserParser.ConditionLeftContext conditionLeftContext=ctx.conditionLeft();
		RuleParserParser.VariableContext variableContext=conditionLeftContext.variable();
		RuleParserParser.ParameterContext parameterContext=conditionLeftContext.parameter();
		RuleParserParser.FunctionInvokeContext functionInvokeContext=conditionLeftContext.functionInvoke();
		RuleParserParser.CommonFunctionContext commonFunctionContext=conditionLeftContext.commonFunction();
		RuleParserParser.MethodInvokeContext methodInvokeContext=conditionLeftContext.methodInvoke();
		RuleParserParser.ExpEvalContext expEvalContext=conditionLeftContext.expEval();
		RuleParserParser.ExpAllContext expAllContext=conditionLeftContext.expAll();
		RuleParserParser.ExpExistsContext expExistsContext=conditionLeftContext.expExists();
		RuleParserParser.ExpCollectContext expCollectContext=conditionLeftContext.expCollect();
		
		Criteria criteria=new Criteria();
		Left left=new Left();
		LeftPart leftPart=null;
		String variableCategory=null;
		String variableLabel=null;
		if(variableContext!=null){
			variableCategory=variableContext.variableCategory().Identifier().getText();
			variableLabel=variableContext.property().getText();
			VariableLeftPart part = new VariableLeftPart();
			part.setVariableCategory(variableCategory);
			part.setVariableLabel(variableLabel);
			left.setType(LeftType.variable);
			leftPart=part;
		}else if(parameterContext!=null){
			variableCategory= VariableCategory.PARAM_CATEGORY;
			variableLabel=parameterContext.Identifier().getText();
			VariableLeftPart part = new VariableLeftPart();
			part.setVariableCategory(variableCategory);
			part.setVariableLabel(variableLabel);
			left.setType(LeftType.variable);
			leftPart=part;
		}else if(functionInvokeContext!=null){
			FunctionLeftPart part=new FunctionLeftPart();
			String name=functionInvokeContext.Identifier().getText();
			RuleParserParser.ActionParametersContext parametersContext = functionInvokeContext.actionParameters();
			if(parametersContext!=null){
				List<Parameter> parameters=new ArrayList<Parameter>();
				for(RuleParserParser.ComplexValueContext complexValueContext:parametersContext.complexValue()){
					Parameter parameter=new Parameter();
					parameter.setValue(BuildUtils.buildValue(complexValueContext));
					parameters.add(parameter);
				}
				part.setParameters(parameters);
			}
			part.setName(name);
			left.setType(LeftType.function);
			leftPart=part;
		}else if(commonFunctionContext!=null){
			CommonFunctionLeftPart part=new CommonFunctionLeftPart();
			String nameorlabel=commonFunctionContext.Identifier().getText();
			for(FunctionDescriptor fun:functionDescriptors){
				if(nameorlabel.equals(fun.getName())){
					part.setName(fun.getName());
					part.setLabel(fun.getLabel());
					break;
				}else if(nameorlabel.equals(fun.getLabel())){
					part.setName(fun.getName());
					part.setLabel(fun.getLabel());
					break;
				}
			}
			if(part.getName()==null){
				throw new RuleException("Function["+nameorlabel+"] not exist.");
			}
			RuleParserParser.ComplexValueContext value=commonFunctionContext.complexValue();
			CommonFunctionParameter param=new CommonFunctionParameter();
			param.setObjectParameter(BuildUtils.buildValue(value));
			RuleParserParser.PropertyContext propertyContext=commonFunctionContext.property();
			if(propertyContext!=null){
				param.setProperty(propertyContext.getText());
			}
			part.setParameter(param);
			left.setType(LeftType.commonfunction);
			leftPart=part;
		}else if(methodInvokeContext!=null){
			MethodLeftPart part=new MethodLeftPart();
			RuleParserParser.BeanMethodContext beanMethodContext=methodInvokeContext.beanMethod();
			String beanLabel=beanMethodContext.Identifier(0).getText();
			String methodLabel=beanMethodContext.Identifier(1).getText();
			part.setBeanLabel(beanLabel);
			part.setMethodLabel(methodLabel);
			RuleParserParser.ActionParametersContext parametersContext=methodInvokeContext.actionParameters();
			if(parametersContext!=null){
				List<Parameter> parameters=new ArrayList<Parameter>();
				for(RuleParserParser.ComplexValueContext complexValueContext:parametersContext.complexValue()){
					Parameter parameter=new Parameter();
					parameter.setValue(BuildUtils.buildValue(complexValueContext));
					parameters.add(parameter);
				}
				part.setParameters(parameters);
			}
			left.setType(LeftType.method);
			leftPart=part;
		}else if(expEvalContext!=null){
			EvalLeftPart part=new EvalLeftPart();
			RuleParserParser.ExpressionBodyContext bodyContext=expEvalContext.expressionBody();
			part.setExpression(bodyContext.getText());
			left.setType(LeftType.eval);
			leftPart=part;
		}else if(expAllContext!=null){
			AllLeftPart part=new AllLeftPart();
			RuleParserParser.VariableContext vc=expAllContext.variable();
			RuleParserParser.ParameterContext pc=expAllContext.parameter();
			if(vc!=null){
				part.setVariableCategory(vc.variableCategory().getText());
				part.setVariableLabel(vc.property().getText());				
			}else if(pc!=null){
				part.setVariableCategory(VariableCategory.PARAM_CATEGORY);
				part.setVariableLabel(pc.Identifier().getText());				
			}
			TerminalNode numberNode=expAllContext.NUMBER();
			RuleParserParser.PercentContext percentContext=expAllContext.percent();
			if(numberNode!=null){
				part.setAmount(Integer.valueOf(numberNode.getText()));
				part.setStatisticType(StatisticType.amount);
			}else if(percentContext!=null){
				part.setPercent(Integer.valueOf(percentContext.NUMBER().getText()));
				part.setStatisticType(StatisticType.percent);
			}else{
				part.setStatisticType(StatisticType.none);				
			}
			RuleParserParser.ExprConditionContext conditionContext=expAllContext.exprCondition();
			MultiCondition condition=buildMultiCondition(conditionContext);
			part.setMultiCondition(condition);
			left.setType(LeftType.all);
			leftPart=part;
		}else if(expExistsContext!=null){
			ExistLeftPart part=new ExistLeftPart();
			RuleParserParser.VariableContext vc=expExistsContext.variable();
			RuleParserParser.ParameterContext pc=expExistsContext.parameter();
			if(vc!=null){
				part.setVariableCategory(vc.variableCategory().getText());
				part.setVariableLabel(vc.property().getText());				
			}else if(pc!=null){
				part.setVariableCategory(VariableCategory.PARAM_CATEGORY);
				part.setVariableLabel(pc.Identifier().getText());				
			}
			TerminalNode numberNode=expExistsContext.NUMBER();
			RuleParserParser.PercentContext percentContext=expExistsContext.percent();
			if(numberNode!=null){
				part.setAmount(Integer.valueOf(numberNode.getText()));
				part.setStatisticType(StatisticType.amount);
			}else if(percentContext!=null){
				part.setPercent(Integer.valueOf(percentContext.NUMBER().getText()));
				part.setStatisticType(StatisticType.percent);
			}else{
				part.setStatisticType(StatisticType.none);				
			}
			RuleParserParser.ExprConditionContext conditionContext=expExistsContext.exprCondition();
			MultiCondition condition=buildMultiCondition(conditionContext);
			part.setMultiCondition(condition);
			left.setType(LeftType.exist);
			leftPart=part;
		}else if(expCollectContext!=null){
			CollectLeftPart part=new CollectLeftPart();
			RuleParserParser.VariableContext vc=expCollectContext.variable();
			RuleParserParser.ParameterContext pc=expCollectContext.parameter();
			if(vc!=null){
				part.setVariableCategory(vc.variableCategory().getText());
				part.setVariableLabel(vc.property().getText());				
			}else if(pc!=null){
				part.setVariableCategory(VariableCategory.PARAM_CATEGORY);
				part.setVariableLabel(pc.Identifier().getText());				
			}
			RuleParserParser.ExprConditionContext conditionContext=expCollectContext.exprCondition();
			if(conditionContext!=null){
				MultiCondition condition=buildMultiCondition(conditionContext);
				part.setMultiCondition(condition);
			}
			if(expCollectContext.property()!=null){
				part.setProperty(expCollectContext.property().getText());
				if(expCollectContext.SUM()!=null){
					part.setPurpose(CollectPurpose.sum);				
				}else if(expCollectContext.MAX()!=null){
					part.setPurpose(CollectPurpose.max);
				}else if(expCollectContext.MIN()!=null){
					part.setPurpose(CollectPurpose.min);
				}else if(expCollectContext.AVG()!=null){
					part.setPurpose(CollectPurpose.avg);
				}
			}else{
				part.setPurpose(CollectPurpose.count);				
			}
			left.setType(LeftType.collect);
			leftPart=part;
		}
		left.setLeftPart(leftPart);
		criteria.setLeft(left);
		Op op=DSLUtils.parseOp(ctx.op());
		criteria.setOp(op);
		RuleParserParser.NullValueContext nullValueContext=ctx.nullValue();
		if(nullValueContext!=null){
			if(op.equals(Op.Equals)){
				criteria.setOp(Op.Null);
			}else if(op.equals(Op.NotEquals)){
				criteria.setOp(Op.NotNull);				
			}else{
				throw new RuleException("'null' value only support '==' or '!=' operator.");
			}
		}else{
			criteria.setValue(BuildUtils.buildValue(ctx.complexValue()));			
		}
		return criteria;
	}

	private MultiCondition buildMultiCondition(RuleParserParser.ExprConditionContext conditionContext){
		MultiCondition multiCondition=new MultiCondition();
		buildPropertyCriteria(conditionContext,multiCondition);
		return multiCondition;
	}

	private void buildPropertyCriteria(RuleParserParser.ExprConditionContext conditionContext, MultiCondition multiCondition) {
		List<RuleParserParser.JoinContext> joins=conditionContext.join();
		if(joins==null || joins.size()==0){
			multiCondition.addCondition(newPropertyCriteria(conditionContext));
		}else{
			RuleParserParser.JoinContext joinContext=joins.get(0);
			if(joinContext.AND()!=null){
				multiCondition.setType(JunctionType.and);
			}else{
				multiCondition.setType(JunctionType.or);				
			}
			List<ParseTree> children=conditionContext.children;
			for(ParseTree parseTree:children){
				if(parseTree instanceof RuleParserParser.ExprConditionContext){
					RuleParserParser.ExprConditionContext ecc=(RuleParserParser.ExprConditionContext)parseTree;
					if(ecc.property()==null){
						buildPropertyCriteria(ecc,multiCondition);
					}else{
						multiCondition.addCondition(newPropertyCriteria(ecc));						
					}
				}
			}			
		}
	}

	private PropertyCriteria newPropertyCriteria(RuleParserParser.ExprConditionContext conditionContext) {
		String property=conditionContext.property().getText();
		PropertyCriteria pc=new PropertyCriteria();
		pc.setProperty(property);
		Op op=DSLUtils.parseOp(conditionContext.op());
		pc.setOp(op);
		RuleParserParser.ComplexValueContext complexValueContext=conditionContext.complexValue();
		RuleParserParser.NullValueContext nullValueContext=conditionContext.nullValue();
		if(nullValueContext!=null && !op.equals(Op.Equals) && !op.equals(Op.NotEquals)){
			throw new RuleException("'$null' value only support '==' or '!=' operator.");
		}else{
			pc.setValue(BuildUtils.buildValue(complexValueContext));
		}
		return pc;
	}
	
	@Override
	public boolean support(ParserRuleContext context) {
		return context instanceof RuleParserParser.SingleConditionContext;
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		functionDescriptors=applicationContext.getBeansOfType(FunctionDescriptor.class).values();
	}
}
