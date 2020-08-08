package gyqw.grule.core.builder.resource;

import gyqw.grule.core.model.crosstab.CrosstabDefinition;
import gyqw.grule.core.parse.deserializer.CrosstableDeserializer;
import org.dom4j.Element;

/**
 * @author fred
 * 2018-11-05 6:52 PM
 */
public class CrosstabResourceBuilder implements ResourceBuilder<CrosstabDefinition> {
    private CrosstableDeserializer crosstableDeserializer;

    public CrosstabResourceBuilder() {
    }

    public CrosstabDefinition build(Element root) {
        return this.crosstableDeserializer.deserialize(root);
    }

    public ResourceType getType() {
        return ResourceType.CrossDecisionTable;
    }

    public boolean support(Element root) {
        return this.crosstableDeserializer.support(root);
    }

    public void setCrosstableDeserializer(CrosstableDeserializer crosstableDeserializer) {
        this.crosstableDeserializer = crosstableDeserializer;
    }
}
