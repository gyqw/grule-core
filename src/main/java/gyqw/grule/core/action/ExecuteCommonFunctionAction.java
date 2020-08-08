package gyqw.grule.core.action;

import gyqw.grule.core.Utils;
import gyqw.grule.core.debug.MsgType;
import gyqw.grule.core.exception.RuleException;
import gyqw.grule.core.model.function.FunctionDescriptor;
import gyqw.grule.core.model.rule.Value;
import gyqw.grule.core.model.rule.lhs.CommonFunctionParameter;
import gyqw.grule.core.runtime.rete.Context;

import java.util.List;

/**
 * @author Jacky.gao
 * 2015年7月31日
 */
public class ExecuteCommonFunctionAction extends AbstractAction {
    private String name;
    private String label;
    private CommonFunctionParameter parameter;

    @Override
    public ActionValue execute(Context context, Object matchedObject, List<Object> allMatchedObjects) {
        FunctionDescriptor function = null;
        if (Utils.getFunctionDescriptorMap().containsKey(name)) {
            function = Utils.findFunctionDescriptor(name);
        } else if (Utils.getFunctionDescriptorLabelMap().containsKey(label)) {
            function = Utils.getFunctionDescriptorLabelMap().get(label);
        }
        if (function == null) {
            throw new RuleException("Function[" + name + "] not exist.");
        }
        String info = (label == null) ? name : label;
        Value value = null;
        Object object = null;
        if (parameter != null) {
            value = parameter.getObjectParameter();
            object = context.getValueCompute().complexValueCompute(value, matchedObject, context, allMatchedObjects);
        }
        String property = null;
        if (function.getArgument() != null && function.getArgument().isNeedProperty()) {
            property = parameter.getProperty();
        }
        Object result = function.doFunction(object, property, context.getWorkingMemory());
        info = info + (object == null ? "" : object);

        // 执行信息
        String msg = "*** 执行函数：" + info;
        context.logMsg(msg, MsgType.ExecuteFunction);

        if (result != null) {
            return new ActionValueImpl(name, result);
        } else {
            return null;
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ExecuteCommonFunction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public CommonFunctionParameter getParameter() {
        return parameter;
    }

    public void setParameter(CommonFunctionParameter parameter) {
        this.parameter = parameter;
    }
}
