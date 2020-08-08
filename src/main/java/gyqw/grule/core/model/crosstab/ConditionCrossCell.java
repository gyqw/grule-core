package gyqw.grule.core.model.crosstab;

import gyqw.grule.core.model.table.Joint;

/**
 * @author fred
 * 2018-11-05 6:45 PM
 */
public class ConditionCrossCell extends CrossCell {
    private Joint joint;

    public ConditionCrossCell() {
    }

    public String getType() {
        return "condition";
    }

    public Joint getJoint() {
        return this.joint;
    }

    public void setJoint(Joint joint) {
        this.joint = joint;
    }

}
