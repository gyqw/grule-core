package gyqw.grule.core.parse.crosstab;

import gyqw.grule.core.model.crosstab.ConditionCrossCell;
import gyqw.grule.core.parse.Parser;
import gyqw.grule.core.parse.table.JointParser;
import org.dom4j.Element;

import java.util.Iterator;

/**
 * @author fred
 * 2018-11-05 6:49 PM
 */
public class ConditionCrossCellParser extends CrossCellParser implements Parser<ConditionCrossCell> {
    private JointParser jointParser;

    public ConditionCrossCellParser() {
    }

    public ConditionCrossCell parse(Element element) {
        ConditionCrossCell cell = new ConditionCrossCell();
        this.parseCrossCell(cell, element);
        Iterator var3 = element.elements().iterator();

        while (var3.hasNext()) {
            Object obj = var3.next();
            if (obj != null && obj instanceof Element) {
                Element ele = (Element) obj;
                if (this.jointParser.support(ele.getName())) {
                    cell.setJoint(this.jointParser.parse(ele));
                }
            }
        }

        return cell;
    }

    public boolean support(String name) {
        return "condition-cell".equals(name);
    }

    public void setJointParser(JointParser jointParser) {
        this.jointParser = jointParser;
    }
}
