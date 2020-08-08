package gyqw.grule.core.parse.scorecard;

import gyqw.grule.core.model.scorecard.ComplexColumn;
import gyqw.grule.core.model.scorecard.ComplexColumnType;
import gyqw.grule.core.parse.Parser;
import org.dom4j.Element;

/**
 * @author fred
 * 2018-12-12 2:10 PM
 */
public class ComplexColumnParser implements Parser<ComplexColumn> {
    public ComplexColumnParser() {
    }

    public ComplexColumn parse(Element element) {
        ComplexColumn col = new ComplexColumn();
        col.setNum(Integer.valueOf(element.attributeValue("num")));
        col.setType(ComplexColumnType.valueOf(element.attributeValue("type")));
        col.setVariableCategory(element.attributeValue("var-category"));
        col.setWidth(Integer.valueOf(element.attributeValue("width")));
        col.setCustomLabel(element.attributeValue("custom-label"));
        return col;
    }

    public boolean support(String name) {
        return name.equals("col");
    }
}
