package gyqw.grule.core.model.flow;

import gyqw.grule.core.action.Action;
import gyqw.grule.core.action.VariableAssignAction;
import gyqw.grule.core.model.flow.ins.FlowContext;
import gyqw.grule.core.model.flow.ins.ProcessInstance;
import gyqw.grule.core.model.rule.Rule;
import gyqw.grule.core.model.rule.RuleInfo;
import gyqw.grule.core.model.rule.SimpleValue;
import gyqw.grule.core.runtime.KnowledgePackage;
import gyqw.grule.core.runtime.KnowledgePackageWrapper;
import gyqw.grule.core.runtime.KnowledgeSession;
import gyqw.grule.core.runtime.KnowledgeSessionFactory;
import gyqw.grule.core.runtime.response.ExecutionResponseImpl;
import gyqw.grule.core.runtime.response.FlowExecutionResponse;
import gyqw.grule.core.runtime.response.NodeExecutionResponseImpl;
import gyqw.grule.core.runtime.response.RuleExecutionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Jacky.gao
 * @author fred
 * 2015年4月20日
 */
public abstract class BindingNode extends FlowNode {
    private Logger logger = LoggerFactory.getLogger(BindingNode.class);
    private KnowledgePackageWrapper knowledgePackageWrapper;

    public BindingNode() {
    }

    public BindingNode(String name) {
        super(name);
    }

    protected KnowledgeSession executeKnowledgePackage(FlowContext context, ProcessInstance instance) {
        KnowledgeSession parentSession = (KnowledgeSession) context.getWorkingMemory();
        KnowledgePackage knowledgePackage = this.knowledgePackageWrapper.getKnowledgePackage();
        KnowledgeSession session = KnowledgeSessionFactory.newKnowledgeSession(this.knowledgePackageWrapper, context, parentSession);

        if (knowledgePackage.getFlowMap() != null && knowledgePackage.getFlowMap().size() != 0) {
            String processId = knowledgePackage.getFlowMap().values().iterator().next().getId();
            FlowExecutionResponse flowExecutionResponse = session.startProcess(processId, context.getVariables());
            ((ExecutionResponseImpl) context.getResponse()).addFlowExecutionResponse(flowExecutionResponse);
        } else {
            ExecutionResponseImpl executionResponse = (ExecutionResponseImpl) context.getResponse();

            RuleExecutionResponse ruleExecutionResponse = session.fireRules(context.getVariables());
            executionResponse.addRuleExecutionResponse(ruleExecutionResponse);

            if (this instanceof RuleNode) {
                try {
                    NodeExecutionResponseImpl nodeExecutionResponse = new NodeExecutionResponseImpl();
                    nodeExecutionResponse.setSort(executionResponse.getNodeExecutionResponseList().size() + 1);
                    nodeExecutionResponse.setRuleNodeName(this.name);
                    if (ruleExecutionResponse.getMatchedRules() != null && ruleExecutionResponse.getMatchedRules().size() > 0) {
                        RuleInfo ruleInfo = ruleExecutionResponse.getMatchedRules().get(ruleExecutionResponse.getMatchedRules().size() - 1);
                        if (ruleInfo != null) {
                            Rule rule = (Rule) ruleInfo;
                            nodeExecutionResponse.setMatchedRuleKey(rule.getName());
                            nodeExecutionResponse.setMatchedRuleName(rule.getRemark());
                            for (Action action : rule.getRhs().getActions()) {
                                if (action instanceof VariableAssignAction) {
                                    VariableAssignAction variableAssignAction = (VariableAssignAction) action;
                                    if (variableAssignAction.getValue() instanceof SimpleValue) {
                                        SimpleValue simpleValue = (SimpleValue) variableAssignAction.getValue();
                                        nodeExecutionResponse.setMatchedRuleAction(simpleValue.getContent());
                                    }
                                }
                            }

                        }
                    }
                    executionResponse.addNodeExecutionResponse(nodeExecutionResponse);
                } catch (Exception e) {
                    logger.error("addNodeExecutionResponse error", e);
                }
            }
        }

        Map<String, Object> parameters = session.getParameters();
        Map<String, Object> variables = context.getVariables();

        for (String key : parameters.keySet()) {
            if (!key.equals("return_to__")) {
                variables.put(key, parameters.get(key));
            }
        }

        return session;
    }

    public KnowledgePackageWrapper getKnowledgePackageWrapper() {
        return this.knowledgePackageWrapper;
    }

    public void setKnowledgePackageWrapper(KnowledgePackageWrapper knowledgePackageWrapper) {
        this.knowledgePackageWrapper = knowledgePackageWrapper;
    }
}
