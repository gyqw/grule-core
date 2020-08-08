package gyqw.grule.core.model.rete.builder;

import gyqw.grule.core.model.library.ResourceLibrary;
import gyqw.grule.core.model.rete.ObjectTypeNode;
import gyqw.grule.core.model.rule.Rule;
import gyqw.grule.core.model.rule.lhs.BaseCriteria;

import java.util.List;

public interface BuildContext {
    List<String> getObjectType(BaseCriteria var1);

    boolean assertSameType(BaseCriteria var1, BaseCriteria var2);

    ResourceLibrary getResourceLibrary();

    ObjectTypeNode buildObjectTypeNode(String var1);

    int nextId();

    void setCurrentRule(Rule var1);

    boolean currentRuleIsDebug();

    IdGenerator getIdGenerator();
}
