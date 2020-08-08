package gyqw.grule.core.model.flow;

import gyqw.grule.core.model.flow.ins.FlowContext;
import gyqw.grule.core.model.flow.ins.FlowInstance;


/**
 * @author Jacky.gao
 * 2015年2月28日
 */
public class ActionNode extends FlowNode {
    private String actionBean;
    private FlowNodeType type = FlowNodeType.Action;

    public ActionNode() {
    }

    public ActionNode(String name) {
        super(name);
    }

    @Override
    public void enterNode(FlowContext context, FlowInstance instance) {
        instance.setCurrentNode(this);
        executeNodeEvent(EventType.enter, context, instance);
        FlowAction action = (FlowAction) context.getApplicationContext().getBean(actionBean);
        action.execute(this, context, instance);
        executeNodeEvent(EventType.leave, context, instance);
        leave(null, context, instance);
    }

    @Override
    public FlowNodeType getType() {
        return type;
    }

    public String getActionBean() {
        return actionBean;
    }

    public void setActionBean(String actionBean) {
        this.actionBean = actionBean;
    }
}
