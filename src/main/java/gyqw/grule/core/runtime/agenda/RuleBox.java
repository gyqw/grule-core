package gyqw.grule.core.runtime.agenda;

import gyqw.grule.core.action.ActionValue;
import gyqw.grule.core.model.rule.Rule;
import gyqw.grule.core.model.rule.RuleInfo;

import java.util.List;

public interface RuleBox {

    List<RuleInfo> execute(AgendaFilter filter, int max, List<ActionValue> actionValues);

    boolean add(Activation activation);

    RuleBox next();

    List<Rule> getRules();

    void retract(Object obj);

    void clean();
}
