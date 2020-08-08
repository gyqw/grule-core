package gyqw.grule.core.runtime.rete;

import gyqw.grule.core.debug.MessageItem;
import gyqw.grule.core.debug.MsgType;
import gyqw.grule.core.model.rule.Rule;
import gyqw.grule.core.model.rule.RuleInfo;
import gyqw.grule.core.runtime.ElCompute;
import gyqw.grule.core.runtime.WorkingMemory;
import gyqw.grule.core.runtime.assertor.AssertorEvaluator;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextImpl implements Context {
    private ApplicationContext applicationContext;
    private AssertorEvaluator assertorEvaluator;
    private Map<String, String> variableCategoryMap;
    private ValueCompute valueCompute;
    private WorkingMemory workingMemory;
    private List<MessageItem> executeMessageItems;
    private Rule currentRule;
    private StringBuilder tipMsgBuilder = new StringBuilder();

    public ContextImpl(WorkingMemory workingMemory, ApplicationContext applicationContext, Map<String, String> variableCategoryMap, List<MessageItem> executeMessageItems) {
        this.workingMemory = workingMemory;
        this.applicationContext = applicationContext;
        this.assertorEvaluator = (AssertorEvaluator) applicationContext.getBean("urule.assertorEvaluator");
        this.variableCategoryMap = variableCategoryMap;
        this.executeMessageItems = executeMessageItems;
        this.valueCompute = (ValueCompute) applicationContext.getBean("urule.valueCompute");
    }

    public void addTipMsg(String msg) {
        if (this.tipMsgBuilder.length() > 0) {
            this.tipMsgBuilder.append(">>");
        }

        this.tipMsgBuilder.append(msg);
    }

    public void cleanTipMsg() {
        this.tipMsgBuilder.delete(0, this.tipMsgBuilder.length());
    }

    public String getTipMsg() {
        return this.tipMsgBuilder.length() > 0 ? this.tipMsgBuilder.toString() : null;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public AssertorEvaluator getAssertorEvaluator() {
        return this.assertorEvaluator;
    }

    public Object parseExpression(String expression) {
        return (new ElCompute()).doCompute(expression);
    }

    public void logMsg(String msg, MsgType type) {
        MessageItem item = new MessageItem(msg, type);
        this.executeMessageItems.add(item);
    }

    public void logMsg(String msg, MsgType type, String leftVariable, String leftVariableValue, String rightVariable, String rightVariableValue) {
        MessageItem item = new MessageItem(msg, type, leftVariable, leftVariableValue, rightVariable, rightVariableValue);
        this.executeMessageItems.add(item);
    }

    public List<MessageItem> getExecuteMessageItems() {
        return executeMessageItems;
    }

    public String getVariableCategoryClass(String variableCategory) {
        String clazz = this.variableCategoryMap.get(variableCategory);
        if (StringUtils.isEmpty(clazz)) {
            clazz = HashMap.class.getName();
        }

        return clazz;
    }

    public ValueCompute getValueCompute() {
        return this.valueCompute;
    }

    public void setCurrentRule(Rule currentRule) {
        this.currentRule = currentRule;
    }

    public RuleInfo getCurrentRule() {
        return this.currentRule;
    }
}
