package gyqw.grule.core.runtime;

import gyqw.grule.core.model.flow.FlowDefinition;
import gyqw.grule.core.model.rete.Rete;
import gyqw.grule.core.model.rule.Rule;
import gyqw.grule.core.runtime.rete.ReteInstance;

import java.util.List;
import java.util.Map;

public interface KnowledgePackage {
    Rete getRete();

    Map<String, String> getVariableCateogoryMap();

    Map<String, FlowDefinition> getFlowMap();

    Map<String, String> getParameters();

    ReteInstance newReteInstance();

    long getTimestamp();

    void resetTimestamp();

    List<Rule> getNoLhsRules();

    List<Rule> getWithElseRules();

    String getId();
}
