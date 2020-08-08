package gyqw.grule.core.runtime.agenda;

import gyqw.grule.core.action.Action;
import gyqw.grule.core.action.ActionType;
import gyqw.grule.core.action.ActionValue;
import gyqw.grule.core.exception.RuleAssertException;
import gyqw.grule.core.model.rule.Rhs;
import gyqw.grule.core.model.rule.Rule;
import gyqw.grule.core.model.rule.RuleInfo;
import gyqw.grule.core.model.rule.lhs.BaseCriteria;
import gyqw.grule.core.model.rule.loop.LoopRule;
import gyqw.grule.core.model.scorecard.runtime.ScoreRule;
import gyqw.grule.core.runtime.KnowledgeSession;
import gyqw.grule.core.runtime.event.impl.ActivationAfterFiredEventImpl;
import gyqw.grule.core.runtime.event.impl.ActivationBeforeFiredEventImpl;
import gyqw.grule.core.runtime.rete.Context;
import gyqw.grule.core.runtime.rete.ContextImpl;

import java.util.*;

public class ActivationImpl implements Activation {
    private boolean processed;
    private Rule rule;
    private Map<Object, List<BaseCriteria>> objectCriteriaMap = new HashMap<>();

    public ActivationImpl(Rule rule) {
        this.rule = rule;
    }

    public RuleInfo execute(Context context, List<RuleInfo> executedRules, List<ActionValue> actionValues) {
        try {
            context.addTipMsg("执行规则[" + this.rule.getName() + "(" + this.rule.getFile() + ")]动作");
            ((ContextImpl) context).setCurrentRule(this.rule);
            KnowledgeSession session = (KnowledgeSession) context.getWorkingMemory();
            session.fireEvent(new ActivationBeforeFiredEventImpl(this, session));
            executedRules.add(this.rule);
            boolean enabled = true;
            if (this.rule.getEnabled() != null) {
                enabled = this.rule.getEnabled();
            }

            if (!enabled) {
                return null;
            } else {
                Date now = new Date();
                Date effectiveDate = this.rule.getEffectiveDate();
                if (effectiveDate != null && effectiveDate.getTime() > now.getTime()) {
                    return null;
                } else {
                    Date expiresDate = this.rule.getExpiresDate();
                    if (expiresDate != null && expiresDate.getTime() < now.getTime()) {
                        return null;
                    } else {
                        List<Object> matchedObjects = new ArrayList<>(this.objectCriteriaMap.keySet());
                        List actions;
                        if (this.rule instanceof LoopRule) {
                            LoopRule loopRule = (LoopRule) this.rule;
                            actions = loopRule.execute(context, this.objectCriteriaMap.keySet(), matchedObjects);
                            if (actions != null) {
                                actionValues.addAll(actions);
                            }
                        } else if (this.rule instanceof ScoreRule) {
                            ScoreRule scoreRule = (ScoreRule) this.rule;
                            scoreRule.execute(context, this.objectCriteriaMap.keySet(), matchedObjects);
                        } else {
                            Rhs rhs = this.rule.getRhs();
                            if (rhs != null) {
                                actions = rhs.getActions();
                                if (actions != null) {
                                    int index = 1;

                                    for (Iterator var13 = actions.iterator(); var13.hasNext(); ++index) {
                                        Action action = (Action) var13.next();
                                        if (this.rule.getDebug() != null) {
                                            action.setDebug(this.rule.getDebug());
                                        }

                                        context.addTipMsg("动作" + index + "." + this.transferActionType(action.getActionType()) + "");
                                        ActionValue actionValue = action.execute(context, this.objectCriteriaMap.keySet(), matchedObjects);
                                        if (actionValue != null) {
                                            actionValues.add(actionValue);
                                        }
                                    }
                                }
                            }
                        }

                        session.fireEvent(new ActivationAfterFiredEventImpl(this, session));
                        this.processed = true;
                        context.cleanTipMsg();
                        return this.rule;
                    }
                }
            }
        } catch (Exception var16) {
            String tipMsg = context.getTipMsg();
            throw new RuleAssertException(tipMsg, var16);
        }
    }

    private String transferActionType(ActionType type) {
        String action = "未知";
        switch (type) {
            case ConsolePrint:
                action = "控制台输出";
                break;
            case ExecuteCommonFunction:
                action = "执行函数";
                break;
            case ExecuteMethod:
                action = "执行方法";
                break;
            case Scoring:
                action = "评分卡得分计算";
                break;
            case VariableAssign:
                action = "变量赋值";
        }

        return action;
    }

    public void setObjectCriteriaMap(Map<Object, List<BaseCriteria>> objectCriteriaMap) {
        this.objectCriteriaMap = objectCriteriaMap;
    }

    public boolean contain(Object obj) {
        return this.objectCriteriaMap.containsKey(obj);
    }

    public Rule getRule() {
        return this.rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public boolean isProcessed() {
        return this.processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public int compareTo(Activation o) {
        Integer o1 = o.getRule().getSalience();
        Integer o2 = this.rule.getSalience();
        if (o1 != null && o2 != null) {
            return o1 - o2;
        } else if (o1 != null) {
            return 1;
        } else {
            return o2 != null ? -1 : 0;
        }
    }
}
