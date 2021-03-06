package gyqw.grule.core.parse.deserializer;

import gyqw.grule.core.model.crosstab.CrosstabDefinition;
import gyqw.grule.core.parse.crosstab.CrosstabParser;
import org.dom4j.Element;

/**
 * @author fred
 * 2018-11-05 6:42 PM
 */
public class CrosstableDeserializer implements Deserializer<CrosstabDefinition> {
    public static final String BEAN_ID = "urule.crosstableDeserializer";
    private CrosstabParser crosstabParser;

    public CrosstableDeserializer() {
    }

    public CrosstabDefinition deserialize(Element root) {
        return this.crosstabParser.parse(root);
    }

    public boolean support(Element root) {
        return "crosstab".equals(root.getName());
    }

    public void setCrosstabParser(CrosstabParser crosstabParser) {
        this.crosstabParser = crosstabParser;
    }
}
