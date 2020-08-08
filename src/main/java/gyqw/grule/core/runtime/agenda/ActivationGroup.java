package gyqw.grule.core.runtime.agenda;

import gyqw.grule.core.action.ActionValue;
import gyqw.grule.core.model.rule.RuleInfo;
import gyqw.grule.core.runtime.rete.Context;

import java.util.ArrayList;
import java.util.List;

public class ActivationGroup extends RuleGroup {
    private boolean executed;

    public ActivationGroup(String name, List<RuleInfo> executedRules) {
        super(name, executedRules);
    }

    @Override
    public List<RuleInfo> execute(Context context, AgendaFilter filter, int max, List<ActionValue> actionValues) {
        executed = true;
        Activation activation = fetchNextExecutableActivation(activations);
        if (activation == null) {
            return null;
        }
        activations.clear();
        if (filter == null || filter.accept(activation)) {
            RuleInfo ruleInfo = activation.execute(context, executedRules, actionValues);
            List<RuleInfo> ruleInfos = new ArrayList<>();
            ruleInfos.add(ruleInfo);
            return ruleInfos;
        }
        return null;
    }

    public boolean isExecuted() {
        return executed;
    }
}
