//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gyqw.grule.core.builder;

import gyqw.grule.core.builder.resource.Resource;
import gyqw.grule.core.builder.resource.ResourceBuilder;
import gyqw.grule.core.builder.resource.ResourceType;
import gyqw.grule.core.builder.table.CrosstabRulesBuilder;
import gyqw.grule.core.builder.table.DecisionTableRulesBuilder;
import gyqw.grule.core.builder.table.ScriptDecisionTableRulesBuilder;
import gyqw.grule.core.dsl.DSLRuleSetBuilder;
import gyqw.grule.core.model.crosstab.CrosstabDefinition;
import gyqw.grule.core.model.decisiontree.DecisionTree;
import gyqw.grule.core.model.flow.FlowDefinition;
import gyqw.grule.core.model.library.ResourceLibrary;
import gyqw.grule.core.model.rete.Rete;
import gyqw.grule.core.model.rete.builder.ReteBuilder;
import gyqw.grule.core.model.rule.Library;
import gyqw.grule.core.model.rule.Rule;
import gyqw.grule.core.model.rule.RuleSet;
import gyqw.grule.core.model.rule.loop.LoopRule;
import gyqw.grule.core.model.rule.loop.LoopRuleUnit;
import gyqw.grule.core.model.scorecard.runtime.ScoreRule;
import gyqw.grule.core.model.table.DecisionTable;
import gyqw.grule.core.model.table.ScriptDecisionTable;
import gyqw.grule.core.runtime.KnowledgePackageWrapper;
import gyqw.grule.core.runtime.service.KnowledgePackageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

public class KnowledgeBuilder extends AbstractBuilder {
    private ResourceLibraryBuilder resourceLibraryBuilder;
    private ReteBuilder reteBuilder;
    private RulesRebuilder rulesRebuilder;
    private DecisionTreeRulesBuilder decisionTreeRulesBuilder;
    private DecisionTableRulesBuilder decisionTableRulesBuilder;
    private ScriptDecisionTableRulesBuilder scriptDecisionTableRulesBuilder;
    private DSLRuleSetBuilder dslRuleSetBuilder;
    private CrosstabRulesBuilder crosstabRulesBuilder;
    public static final String BEAN_ID = "urule.knowledgeBuilder";

    public KnowledgeBuilder() {
    }

    public KnowledgeBase buildKnowledgeBase(ResourceBase resourceBase) throws IOException {
        KnowledgePackageService knowledgePackageService = (KnowledgePackageService) this.applicationContext.getBean("urule.knowledgePackageService");
        List<Rule> rules = new ArrayList<>();
        Map<String, Library> libMap = new HashMap<>();
        Map<String, FlowDefinition> flowMap = new HashMap<>();
        Iterator resourceIterator = resourceBase.getResources().iterator();

        while (true) {
            while (resourceIterator.hasNext()) {
                Resource resource = (Resource) resourceIterator.next();
                String path = resource.getPath();
                if (this.dslRuleSetBuilder.support(resource)) {
                    RuleSet ruleSet = this.dslRuleSetBuilder.build(resource.getContent());
                    this.addToLibraryMap(libMap, ruleSet.getLibraries());
                    if (ruleSet.getRules() != null) {
                        this.buildRulesPath(ruleSet.getRules(), path);
                        rules.addAll(ruleSet.getRules());
                    }
                } else {
                    Element root = this.parseResource(resource.getContent());
                    Iterator var10 = this.resourceBuilders.iterator();

                    while (var10.hasNext()) {
                        ResourceBuilder<?> builder = (ResourceBuilder) var10.next();
                        if (builder.support(root)) {
                            Object object = builder.build(root);
                            ResourceType type = builder.getType();
                            List tableRules;
                            if (type.equals(ResourceType.RuleSet)) {
                                RuleSet ruleSet = (RuleSet) object;
                                this.addToLibraryMap(libMap, ruleSet.getLibraries());
                                if (ruleSet.getRules() != null) {
                                    tableRules = ruleSet.getRules();
                                    this.buildRulesPath(tableRules, path);
                                    this.rulesRebuilder.convertNamedJunctions(tableRules);
                                    rules.addAll(tableRules);
                                }
                            } else {
                                RuleSet ruleSet;
                                if (type.equals(ResourceType.DecisionTree)) {
                                    DecisionTree tree = (DecisionTree) object;
                                    this.addToLibraryMap(libMap, tree.getLibraries());
                                    ruleSet = this.decisionTreeRulesBuilder.buildRules(tree);
                                    this.addToLibraryMap(libMap, ruleSet.getLibraries());
                                    if (ruleSet.getRules() != null) {
                                        this.buildRulesPath(ruleSet.getRules(), path);
                                        rules.addAll(ruleSet.getRules());
                                    }
                                } else if (type.equals(ResourceType.DecisionTable)) {
                                    DecisionTable table = (DecisionTable) object;
                                    this.addToLibraryMap(libMap, table.getLibraries());
                                    tableRules = this.decisionTableRulesBuilder.buildRules(table);
                                    this.buildRulesPath(tableRules, path);
                                    rules.addAll(tableRules);
                                } else if (type.equals(ResourceType.CrossDecisionTable)) {
                                    CrosstabDefinition crosstab = (CrosstabDefinition) object;
                                    this.addToLibraryMap(libMap, crosstab.getLibraries());
                                    tableRules = this.crosstabRulesBuilder.buildRules(crosstab);
                                    this.buildRulesPath(tableRules, path);
                                    rules.addAll(tableRules);
                                } else if (type.equals(ResourceType.ScriptDecisionTable)) {
                                    ScriptDecisionTable table = (ScriptDecisionTable) object;
                                    ruleSet = this.scriptDecisionTableRulesBuilder.buildRules(table);
                                    this.addToLibraryMap(libMap, ruleSet.getLibraries());
                                    if (ruleSet.getRules() != null) {
                                        this.buildRulesPath(ruleSet.getRules(), path);
                                        rules.addAll(ruleSet.getRules());
                                    }
                                } else if (type.equals(ResourceType.Flow)) {
                                    FlowDefinition fd = (FlowDefinition) object;
                                    fd = fd.newFlowDefinitionForSerialize(this, knowledgePackageService, this.dslRuleSetBuilder);
                                    this.addToLibraryMap(libMap, fd.getLibraries());
                                    flowMap.put(fd.getId(), fd);
                                } else {
                                    ScoreRule rule;
                                    ArrayList listRules;
                                    if (type.equals(ResourceType.Scorecard)) {
                                        rule = (ScoreRule) object;
                                        listRules = new ArrayList<>();
                                        listRules.add(rule);
                                        this.buildRulesPath(listRules, path);
                                        rules.add(rule);
                                        this.addToLibraryMap(libMap, rule.getLibraries());
                                    } else if (type.equals(ResourceType.ComplexScorecard)) {
                                        rule = (ScoreRule) object;
                                        listRules = new ArrayList();
                                        listRules.add(rule);
                                        this.buildRulesPath(listRules, path);
                                        rules.add(rule);
                                        this.addToLibraryMap(libMap, rule.getLibraries());
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }

            ResourceLibrary resourceLibrary = this.resourceLibraryBuilder.buildResourceLibrary(libMap.values());
            this.buildLoopRules(rules, resourceLibrary);
            Rete rete = this.reteBuilder.buildRete(rules, resourceLibrary);
            return new KnowledgeBase(rete, flowMap);
        }
    }

    private void buildRulesPath(List<Rule> rules, String path) {
        Iterator var3 = rules.iterator();

        while (var3.hasNext()) {
            Rule rule = (Rule) var3.next();
            rule.setFile(path);
        }

    }

    private void buildLoopRules(List<Rule> rules, ResourceLibrary resourceLibrary) {
        Iterator var3 = rules.iterator();

        while (var3.hasNext()) {
            Rule rule = (Rule) var3.next();
            if (rule instanceof LoopRule) {
                LoopRule loopRule = (LoopRule) rule;
                List<Rule> ruleList = this.buildRules(loopRule);
                Rete rete = this.reteBuilder.buildRete(ruleList, resourceLibrary);
                KnowledgeBase base = new KnowledgeBase(rete);
                KnowledgePackageWrapper knowledgeWrapper = new KnowledgePackageWrapper(base.getKnowledgePackage());
                loopRule.setKnowledgePackageWrapper(knowledgeWrapper);
            }
        }

    }

    private List<Rule> buildRules(LoopRule loopRule) {
        List<Rule> rules = new ArrayList<>();
        List<LoopRuleUnit> units = loopRule.getUnits();
        Iterator var4 = units.iterator();

        while (var4.hasNext()) {
            LoopRuleUnit unit = (LoopRuleUnit) var4.next();
            Rule rule = new Rule();
            rule.setDebug(loopRule.getDebug());
            rule.setName(loopRule.getName() + "->" + unit.getName());
            rule.setLhs(unit.getLhs());
            rule.setRhs(unit.getRhs());
            rule.setOther(unit.getOther());
            rules.add(rule);
        }

        loopRule.setUnits(null);
        return rules;
    }

    public KnowledgeBase buildKnowledgeBase(RuleSet ruleSet) {
        List<Rule> rules = new ArrayList<>();
        Map<String, Library> libMap = new HashMap<>();
        this.addToLibraryMap(libMap, ruleSet.getLibraries());
        if (ruleSet.getRules() != null) {
            rules.addAll(ruleSet.getRules());
        }

        ResourceLibrary resourceLibrary = this.resourceLibraryBuilder.buildResourceLibrary(libMap.values());
        Rete rete = this.reteBuilder.buildRete(rules, resourceLibrary);
        return new KnowledgeBase(rete, null);
    }

    private void addToLibraryMap(Map<String, Library> map, List<Library> libraries) {
        if (libraries != null) {
            Iterator var3 = libraries.iterator();

            while (var3.hasNext()) {
                Library lib = (Library) var3.next();
                String path = lib.getPath();
                if (!map.containsKey(path)) {
                    map.put(path, lib);
                }
            }

        }
    }

    public void setRulesRebuilder(RulesRebuilder rulesRebuilder) {
        this.rulesRebuilder = rulesRebuilder;
    }

    public void setReteBuilder(ReteBuilder reteBuilder) {
        this.reteBuilder = reteBuilder;
    }

    public void setDecisionTableRulesBuilder(DecisionTableRulesBuilder decisionTableRulesBuilder) {
        this.decisionTableRulesBuilder = decisionTableRulesBuilder;
    }

    public void setScriptDecisionTableRulesBuilder(ScriptDecisionTableRulesBuilder scriptDecisionTableRulesBuilder) {
        this.scriptDecisionTableRulesBuilder = scriptDecisionTableRulesBuilder;
    }

    public void setDslRuleSetBuilder(DSLRuleSetBuilder dslRuleSetBuilder) {
        this.dslRuleSetBuilder = dslRuleSetBuilder;
    }

    public void setResourceLibraryBuilder(ResourceLibraryBuilder resourceLibraryBuilder) {
        this.resourceLibraryBuilder = resourceLibraryBuilder;
    }

    public void setDecisionTreeRulesBuilder(DecisionTreeRulesBuilder decisionTreeRulesBuilder) {
        this.decisionTreeRulesBuilder = decisionTreeRulesBuilder;
    }

    public void setCrosstabRulesBuilder(CrosstabRulesBuilder crosstabRulesBuilder) {
        this.crosstabRulesBuilder = crosstabRulesBuilder;
    }
}
