package gyqw.grule.core.parse.deserializer;

import gyqw.grule.core.model.scorecard.ComplexScorecardDefinition;
import gyqw.grule.core.parse.scorecard.ComplexScorecardParser;
import org.dom4j.Element;

/**
 * @author fred
 * 2018-12-12 2:05 PM
 */
public class ComplexScorecardDeserializer implements Deserializer<ComplexScorecardDefinition> {
    public static final String BEAN_ID = "urule.complexScorecardDeserializer";
    private ComplexScorecardParser complexScorecardParser;

    public ComplexScorecardDeserializer() {
    }

    public ComplexScorecardDefinition deserialize(Element root) {
        return this.complexScorecardParser.parse(root);
    }

    public void setComplexScorecardParser(ComplexScorecardParser complexScorecardParser) {
        this.complexScorecardParser = complexScorecardParser;
    }

    public boolean support(Element root) {
        return this.complexScorecardParser.support(root.getName());
    }
}
