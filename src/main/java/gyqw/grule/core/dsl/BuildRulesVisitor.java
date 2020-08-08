package gyqw.grule.core.dsl;

import gyqw.grule.core.Configure;
import gyqw.grule.core.action.Action;
import gyqw.grule.core.dsl.builder.BuildUtils;
import gyqw.grule.core.dsl.builder.ContextBuilder;
import gyqw.grule.core.dsl.builder.NamedConditionBuilder;
import gyqw.grule.core.exception.RuleException;
import com.bstek.urule.model.rule.*;
import com.bstek.urule.model.rule.lhs.*;
import gyqw.grule.core.model.rule.loop.LoopEnd;
import gyqw.grule.core.model.rule.loop.LoopRule;
import gyqw.grule.core.model.rule.loop.LoopStart;
import gyqw.grule.core.model.rule.loop.LoopTarget;
import gyqw.grule.core.model.rule.*;
import gyqw.grule.core.model.rule.lhs.*;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BuildRulesVisitor extends RuleParserBaseVisitor<Object> {
    private Map<ParseTree, Junction> map = new HashMap<ParseTree, Junction>();
    private Collection<ContextBuilder> builders;
    private NamedConditionBuilder namedConditionBuilder = new NamedConditionBuilder();
    private CommonTokenStream tokenStream;

    public BuildRulesVisitor(Collection<ContextBuilder> builders, CommonTokenStream tokenStream) {
        this.builders = builders;
        this.tokenStream = tokenStream;
    }

    @Override
    public RuleSet visitRuleSet(RuleParserParser.RuleSetContext ctx) {
        RuleSet ruleSet = new RuleSet();
        RuleParserParser.RuleSetHeaderContext ruleSetHeaderContext = ctx.ruleSetHeader();
        List<RuleParserParser.ResourceContext> resourcesContext = ruleSetHeaderContext.resource();
        if (resourcesContext != null) {
            for (RuleParserParser.ResourceContext context : resourcesContext) {
                ruleSet.addLibrary(visitResource(context));
            }
        }
        StringBuffer sb = null;
        List<RuleParserParser.FunctionImportContext> functionImportContextList = ruleSetHeaderContext.functionImport();
        if (functionImportContextList != null) {
            sb = new StringBuffer();
            for (RuleParserParser.FunctionImportContext importContext : functionImportContextList) {
                sb.append("import ");
                sb.append(importContext.packageDef().getText());
                sb.append(";");
            }
        }
        RuleParserParser.RuleSetBodyContext ruleSetBodyContext = ctx.ruleSetBody();
        List<RuleParserParser.RulesContext> rulesContextList = ruleSetBodyContext.rules();
        if (rulesContextList != null) {
            List<Rule> rules = new ArrayList<>();
            ruleSet.setRules(rules);
            for (RuleParserParser.RulesContext ruleContext : rulesContextList) {
                RuleParserParser.RuleDefContext ruleDefContext = ruleContext.ruleDef();
                if (ruleDefContext != null) {
                    Rule rule = visitRuleDef(ruleDefContext);
                    rules.add(rule);
                }
                RuleParserParser.LoopRuleDefContext loopRuleDefContext = ruleContext.loopRuleDef();
                if (loopRuleDefContext != null) {
                    LoopRule rule = visitLoopRuleDef(loopRuleDefContext);
                    rules.add(rule);
                }
            }
        }
        return ruleSet;
    }

    @SuppressWarnings("unused")
    private String buildFunctionBody(RuleParserParser.ExpressionBodyContext expressionBodyContext) {
        StringBuffer sb = new StringBuffer();
        for (ParseTree node : expressionBodyContext.children) {
            Interval interval = node.getSourceInterval();
            int index = interval.a;
            List<Token> leftTokens = tokenStream.getHiddenTokensToLeft(index);
            if (leftTokens != null) {
                Token token = leftTokens.get(0);
                String text = token.getText();
                sb.append(text);
            }
            sb.append(node.getText());
            List<Token> rightTokens = tokenStream.getHiddenTokensToRight(index);
            if (rightTokens != null) {
                Token token = rightTokens.get(0);
                String text = token.getText();
                sb.append(text);
            }
        }
        return sb.toString();
    }

    @Override
    public Library visitResource(RuleParserParser.ResourceContext ctx) {
        return (Library) doBuilder(ctx);
    }

    @Override
    public LoopRule visitLoopRuleDef(RuleParserParser.LoopRuleDefContext ctx) {
        SimpleDateFormat sd = new SimpleDateFormat(Configure.getDateFormat());
        LoopRule rule = new LoopRule();
        String name = ctx.STRING().getText();
        name = name.substring(1, name.length() - 1);
        rule.setName(name);

        RuleParserParser.LoopTargetContext target = ctx.loopTarget();
        RuleParserParser.ComplexValueContext valueContext = target.complexValue();
        LoopTarget loopTarget = new LoopTarget();
        loopTarget.setValue(BuildUtils.buildValue(valueContext));
        rule.setLoopTarget(loopTarget);

        RuleParserParser.LoopStartContext startContext = ctx.loopStart();
        if (startContext != null) {
            List<RuleParserParser.ActionContext> actionContextList = startContext.action();
            if (actionContextList != null) {
                LoopStart loopStart = new LoopStart();
                loopStart.setActions(buildActions(actionContextList));
                rule.setLoopStart(loopStart);
            }
        }

        RuleParserParser.LoopEndContext endContext = ctx.loopEnd();
        if (endContext != null) {
            List<RuleParserParser.ActionContext> actionContextList = endContext.action();
            if (actionContextList != null) {
                LoopEnd loopEnd = new LoopEnd();
                loopEnd.setActions(buildActions(actionContextList));
                rule.setLoopEnd(loopEnd);
            }
        }


        List<RuleParserParser.AttributeContext> attributesContext = ctx.attribute();
        if (attributesContext != null) {
            for (RuleParserParser.AttributeContext context : attributesContext) {
                if (context.salienceAttribute() != null) {
                    rule.setSalience(Integer.valueOf(context.salienceAttribute().NUMBER().getText()));
                } else if (context.loopAttribute() != null) {
                    rule.setLoop(Boolean.valueOf(context.loopAttribute().Boolean().getText()));
                } else if (context.effectiveDateAttribute() != null) {
                    try {
                        String dateValue = context.effectiveDateAttribute().STRING().getText();
                        dateValue = dateValue.substring(1, dateValue.length() - 1);
                        rule.setEffectiveDate(sd.parse(dateValue));
                    } catch (ParseException e) {
                        throw new RuleException(e);
                    }
                } else if (context.expiresDateAttribute() != null) {
                    try {
                        String dateValue = context.expiresDateAttribute().STRING().getText();
                        dateValue = dateValue.substring(1, dateValue.length() - 1);
                        rule.setExpiresDate(sd.parse(dateValue));
                    } catch (ParseException e) {
                        throw new RuleException(e);
                    }
                } else if (context.enabledAttribute() != null) {
                    rule.setEnabled(Boolean.valueOf(context.enabledAttribute().Boolean().getText()));
                } else if (context.debugAttribute() != null) {
                    rule.setDebug(Boolean.valueOf(context.debugAttribute().Boolean().getText()));
                } else if (context.activationGroupAttribute() != null) {
                    String value = context.activationGroupAttribute().STRING().getText();
                    value = value.substring(1, value.length() - 1);
                    rule.setActivationGroup(value);
                } else if (context.agendaGroupAttribute() != null) {
                    String value = context.agendaGroupAttribute().STRING().getText();
                    value = value.substring(1, value.length() - 1);
                    rule.setAgendaGroup(value);
                } else if (context.autoFocusAttribute() != null) {
                    rule.setAutoFocus(Boolean.valueOf(context.autoFocusAttribute().Boolean().getText()));
                } else if (context.ruleflowGroupAttribute() != null) {
                    String value = context.ruleflowGroupAttribute().STRING().getText();
                    value = value.substring(1, value.length() - 1);
                    rule.setRuleflowGroup(value);
                }
            }
        }
        RuleParserParser.LeftContext leftContext = ctx.left();
        ParseTree parseTree = leftContext.getChild(1);
        Lhs lhs = new Lhs();
        rule.setLhs(lhs);
        Criterion criterion = buildCriterion(parseTree);
        lhs.setCriterion(criterion);
        Rhs rhs = new Rhs();
        rhs.setActions(visitRight(ctx.right()));
        rule.setRhs(rhs);

        Other other = new Other();
        other.setActions(visitOther(ctx.other()));
        rule.setOther(other);
        return rule;
    }

    @Override
    public Rule visitRuleDef(RuleParserParser.RuleDefContext ctx) {
        SimpleDateFormat sd = new SimpleDateFormat(Configure.getDateFormat());
        Rule rule = new Rule();
        String name = ctx.STRING().getText();
        name = name.substring(1, name.length() - 1);
        rule.setName(name);
        List<RuleParserParser.AttributeContext> attributesContext = ctx.attribute();
        if (attributesContext != null) {
            for (RuleParserParser.AttributeContext context : attributesContext) {
                if (context.salienceAttribute() != null) {
                    rule.setSalience(Integer.valueOf(context.salienceAttribute().NUMBER().getText()));
                } else if (context.loopAttribute() != null) {
                    rule.setLoop(Boolean.valueOf(context.loopAttribute().Boolean().getText()));
                } else if (context.effectiveDateAttribute() != null) {
                    try {
                        String dateValue = context.effectiveDateAttribute().STRING().getText();
                        dateValue = dateValue.substring(1, dateValue.length() - 1);
                        rule.setEffectiveDate(sd.parse(dateValue));
                    } catch (ParseException e) {
                        throw new RuleException(e);
                    }
                } else if (context.expiresDateAttribute() != null) {
                    try {
                        String dateValue = context.expiresDateAttribute().STRING().getText();
                        dateValue = dateValue.substring(1, dateValue.length() - 1);
                        rule.setExpiresDate(sd.parse(dateValue));
                    } catch (ParseException e) {
                        throw new RuleException(e);
                    }
                } else if (context.enabledAttribute() != null) {
                    rule.setEnabled(Boolean.valueOf(context.enabledAttribute().Boolean().getText()));
                } else if (context.debugAttribute() != null) {
                    rule.setDebug(Boolean.valueOf(context.debugAttribute().Boolean().getText()));
                } else if (context.activationGroupAttribute() != null) {
                    String value = context.activationGroupAttribute().STRING().getText();
                    value = value.substring(1, value.length() - 1);
                    rule.setActivationGroup(value);
                } else if (context.agendaGroupAttribute() != null) {
                    String value = context.agendaGroupAttribute().STRING().getText();
                    value = value.substring(1, value.length() - 1);
                    rule.setAgendaGroup(value);
                } else if (context.autoFocusAttribute() != null) {
                    rule.setAutoFocus(Boolean.valueOf(context.autoFocusAttribute().Boolean().getText()));
                } else if (context.ruleflowGroupAttribute() != null) {
                    String value = context.ruleflowGroupAttribute().STRING().getText();
                    value = value.substring(1, value.length() - 1);
                    rule.setRuleflowGroup(value);
                }
            }
        }

        RuleParserParser.LeftContext leftContext = ctx.left();
        ParseTree parseTree = leftContext.getChild(1);
        Lhs lhs = new Lhs();
        rule.setLhs(lhs);
        Criterion criterion = buildCriterion(parseTree);
        lhs.setCriterion(criterion);
        Rhs rhs = new Rhs();
        rhs.setActions(visitRight(ctx.right()));
        rule.setRhs(rhs);

        Other other = new Other();
        other.setActions(visitOther(ctx.other()));
        rule.setOther(other);
        return rule;
    }

    @Override
    public Criteria visitSingleCondition(RuleParserParser.SingleConditionContext ctx) {
        return (Criteria) doBuilder(ctx);
    }

    @Override
    public Criterion visitParenConditions(RuleParserParser.ParenConditionsContext ctx) {
        ParseTree parseTree = ctx.getChild(1);
        return buildCriterion(parseTree);
    }

    @Override
    public Criterion visitSingleNamedConditionSet(RuleParserParser.SingleNamedConditionSetContext ctx) {
        NamedCriteria criteria = new NamedCriteria();
        RuleParserParser.NamedConditionSetContext conditionSet = ctx.namedConditionSet();
        if (conditionSet.refName() != null) {
            criteria.setReferenceName(conditionSet.refName().getText());
        }
        criteria.setVariableCategory(conditionSet.refObject().getText());
        RuleParserParser.NamedConditionContext namedConditionContext = conditionSet.namedCondition();
        CriteriaUnit unit = namedConditionBuilder.buildNamedCriteria(namedConditionContext, criteria.getVariableCategory());
        criteria.setUnit(unit);
        return criteria;
    }

    @Override
    public Criterion visitMultiConditions(RuleParserParser.MultiConditionsContext ctx) {
        Junction topJunction = null;
        Criterion criterion = null;
        Junction junction = map.get(ctx);
        int childCount = ctx.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTree = ctx.getChild(i);
            if (parseTree instanceof RuleParserParser.JoinContext) {
                RuleParserParser.JoinContext joinContext = (RuleParserParser.JoinContext) parseTree;
                if (joinContext.AND() != null) {
                    if (junction == null) {
                        junction = new And();
                        topJunction = junction;
                        junction.addCriterion(criterion);
                    } else if (!(junction instanceof And)) {
                        And newAnd = new And();
                        junction.addCriterion(newAnd);
                        junction = newAnd;
                    }
                } else {
                    if (junction == null) {
                        junction = new Or();
                        topJunction = junction;
                        junction.addCriterion(criterion);
                    } else if (!(junction instanceof Or)) {
                        Or newOr = new Or();
                        junction.addCriterion(newOr);
                        junction = newOr;
                    }
                }
            } else {
                boolean isMulti = false;
                if (parseTree instanceof RuleParserParser.MultiConditionsContext) {
                    isMulti = true;
                }
                if (junction != null && isMulti) {
                    map.put(parseTree, junction);
                }
                criterion = buildCriterion(parseTree);
                if (junction != null && !isMulti) {
                    junction.addCriterion(criterion);
                }
            }
        }
        if (topJunction != null) {
            return topJunction;
        }
        return criterion;
    }

    @Override
    public List<Action> visitRight(RuleParserParser.RightContext ctx) {
        if (ctx == null || ctx.action() == null) {
            return null;
        }
        List<RuleParserParser.ActionContext> actionContexts = ctx.action();

        return buildActions(actionContexts);
    }

    private List<Action> buildActions(List<RuleParserParser.ActionContext> actionContexts) {
        List<Action> actions = new ArrayList<Action>();
        for (RuleParserParser.ActionContext actionContext : actionContexts) {
            Action action = (Action) doBuilder(actionContext);
            actions.add(action);
        }
        return actions;
    }

    @Override
    public List<Action> visitOther(RuleParserParser.OtherContext ctx) {
        if (ctx == null || ctx.action() == null) {
            return null;
        }
        List<Action> actions = new ArrayList<Action>();
        for (RuleParserParser.ActionContext actionContext : ctx.action()) {
            Action action = (Action) doBuilder(actionContext);
            actions.add(action);
        }
        return actions;
    }

    private Criterion buildCriterion(ParseTree parseTree) {
        Criterion criterion = null;
        if (parseTree instanceof RuleParserParser.ParenConditionsContext) {
            criterion = visitParenConditions((RuleParserParser.ParenConditionsContext) parseTree);
        } else if (parseTree instanceof RuleParserParser.SingleConditionContext) {
            criterion = visitSingleCondition((RuleParserParser.SingleConditionContext) parseTree);
        } else if (parseTree instanceof RuleParserParser.MultiConditionsContext) {
            criterion = visitMultiConditions((RuleParserParser.MultiConditionsContext) parseTree);
        } else if (parseTree instanceof RuleParserParser.SingleNamedConditionSetContext) {
            criterion = visitSingleNamedConditionSet((RuleParserParser.SingleNamedConditionSetContext) parseTree);
        }
        return criterion;
    }

    private Object doBuilder(ParserRuleContext context) {
        for (ContextBuilder builder : builders) {
            if (builder.support(context)) {
                return builder.build(context);
            }
        }
        return null;
    }
}
