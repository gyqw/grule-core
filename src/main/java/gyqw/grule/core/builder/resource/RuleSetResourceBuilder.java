package gyqw.grule.core.builder.resource;

import gyqw.grule.core.model.rule.RuleSet;
import gyqw.grule.core.parse.deserializer.RuleSetDeserializer;
import org.dom4j.Element;

/**
 * @author Jacky.gao
 * @since 2014年12月22日
 */
public class RuleSetResourceBuilder implements ResourceBuilder<RuleSet> {

    private RuleSetDeserializer ruleSetDeserializer;

    public RuleSet build(Element root) {
        return ruleSetDeserializer.deserialize(root);
    }

    public boolean support(Element root) {
        return ruleSetDeserializer.support(root);
    }

    public ResourceType getType() {
        return ResourceType.RuleSet;
    }

    public void setRuleSetDeserializer(RuleSetDeserializer ruleSetDeserializer) {
        this.ruleSetDeserializer = ruleSetDeserializer;
    }
}
