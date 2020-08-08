package gyqw.grule.core.runtime.agenda;

import gyqw.grule.core.action.ActionValue;
import gyqw.grule.core.model.rule.Rule;
import gyqw.grule.core.model.rule.RuleInfo;
import gyqw.grule.core.runtime.rete.Context;

import java.util.List;

public interface Activation extends Comparable<Activation> {
    boolean isProcessed();

    Rule getRule();

    boolean contain(Object var1);

    RuleInfo execute(Context var1, List<RuleInfo> var2, List<ActionValue> var3);
}
