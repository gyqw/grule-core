package gyqw.grule.core.model.flow;

import gyqw.grule.core.model.flow.ins.FlowContext;
import gyqw.grule.core.model.flow.ins.ProcessInstance;

/**
 * @author Jacky.gao
 * 2015年2月28日
 */
public interface FlowAction {
    /**
     * @param node     当前节点对象
     * @param context  规则流上下文件对象
     * @param instance 当前规则流实例对象
     */
    void execute(ActionNode node, FlowContext context, ProcessInstance instance);
}
