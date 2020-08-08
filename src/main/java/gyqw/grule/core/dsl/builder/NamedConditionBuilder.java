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
import java.util.List;

import gyqw.grule.core.exception.RuleException;
import gyqw.grule.core.dsl.DSLUtils;
import gyqw.grule.core.model.rule.Op;
import gyqw.grule.core.model.rule.lhs.Criteria;
import gyqw.grule.core.model.rule.lhs.CriteriaUnit;
import gyqw.grule.core.model.rule.lhs.JunctionType;
import gyqw.grule.core.model.rule.lhs.Left;
import gyqw.grule.core.model.rule.lhs.LeftType;
import gyqw.grule.core.model.rule.lhs.VariableLeftPart;
import gyqw.grule.core.dsl.RuleParserParser;

/**
 * @author Jacky.gao
 * @since 2016年8月15日
 */
public class NamedConditionBuilder {
	public CriteriaUnit buildNamedCriteria(RuleParserParser.NamedConditionContext namedConditionContext, String variableCategory){
		CriteriaUnit unit=null;
		if(namedConditionContext instanceof RuleParserParser.MultiNamedConditionsContext){
			unit=visitMultiNamedConditions((RuleParserParser.MultiNamedConditionsContext)namedConditionContext, variableCategory);
		}else if(namedConditionContext instanceof RuleParserParser.SingleNamedConditionsContext){
			unit=visitSingleNamedConditions((RuleParserParser.SingleNamedConditionsContext)namedConditionContext, variableCategory);
		}else if(namedConditionContext instanceof RuleParserParser.ParenNamedConditionsContext){
			unit=visitParenNamedConditions((RuleParserParser.ParenNamedConditionsContext)namedConditionContext, variableCategory);
		}else{
			throw new RuleException("Unsupport context : +namedConditionContext+");
		}
		return unit;
	}
	
	private CriteriaUnit visitSingleNamedConditions(RuleParserParser.SingleNamedConditionsContext ctx, String variableCategory) {
		Criteria criteria=new Criteria();
		VariableLeftPart leftPart=new VariableLeftPart();
		Left left=new Left();
		left.setLeftPart(leftPart);
		left.setType(LeftType.NamedReference);
		criteria.setLeft(left);
		String variableName=ctx.property().getText();
		leftPart.setVariableLabel(variableName);
		leftPart.setVariableCategory(variableCategory);
		Op op= DSLUtils.parseOp(ctx.op());
		criteria.setOp(op);
		if(ctx.complexValue()==null){
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
		CriteriaUnit unit=new CriteriaUnit();
		unit.setCriteria(criteria);
		return unit;
	}
	private CriteriaUnit visitMultiNamedConditions(RuleParserParser.MultiNamedConditionsContext ctx, String variableCategory) {
		List<CriteriaUnit> nextUnits=new ArrayList<CriteriaUnit>();
		List<RuleParserParser.NamedConditionContext> namedConditions=ctx.namedCondition();
		if(namedConditions!=null){
			for(int i=0;i<namedConditions.size();i++){
				RuleParserParser.NamedConditionContext context=namedConditions.get(i);
				CriteriaUnit nextUnit=buildNamedCriteria(context, variableCategory);
				nextUnits.add(nextUnit);
				RuleParserParser.JoinContext joinContext=ctx.join(i);
				if(joinContext!=null){
					if(joinContext.AND()!=null){
						nextUnit.setJunctionType(JunctionType.and);
					}else{
						nextUnit.setJunctionType(JunctionType.or);				
					}
				}
			}
		}
		CriteriaUnit unit=new CriteriaUnit();
		unit.setNextUnits(nextUnits);
		return unit;
	}
	
	private CriteriaUnit visitParenNamedConditions(RuleParserParser.ParenNamedConditionsContext ctx, String variableCategory) {
		RuleParserParser.NamedConditionContext namedConditionContext=ctx.namedCondition();
		CriteriaUnit unit=buildNamedCriteria(namedConditionContext, variableCategory);
		return unit;
	}
}
