package gyqw.grule.core.dsl;

import gyqw.grule.core.builder.RulesRebuilder;
import gyqw.grule.core.builder.resource.Resource;
import gyqw.grule.core.dsl.builder.ContextBuilder;
import gyqw.grule.core.exception.RuleException;
import gyqw.grule.core.model.rule.Library;
import gyqw.grule.core.model.rule.Rule;
import gyqw.grule.core.model.rule.RuleSet;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class DSLRuleSetBuilder implements ApplicationContextAware {
    public static final String BEAN_ID = "urule.dslRuleSetBuilder";
    private Collection<ContextBuilder> contextBuilders;
    private RulesRebuilder rulesRebuilder;

    public RuleSet build(String script) throws IOException {
        ANTLRInputStream antlrInputStream = new ANTLRInputStream(script);
        RuleParserLexer lexer = new RuleParserLexer(antlrInputStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        RuleParserParser parser = new RuleParserParser(tokenStream);
        ScriptDecisionTableErrorListener errorListener = new ScriptDecisionTableErrorListener();
        parser.addErrorListener(errorListener);
        BuildRulesVisitor visitor = new BuildRulesVisitor(contextBuilders, tokenStream);
        RuleSet ruleSet = visitor.visitRuleSet(parser.ruleSet());
        rebuildRuleSet(ruleSet);
        String error = errorListener.getErrorMessage();
        if (error != null) {
            throw new RuleException("Script parse error:" + error);
        }
        return ruleSet;
    }

    private void rebuildRuleSet(RuleSet ruleSet) {
        List<Library> libraries = ruleSet.getLibraries();
        List<Rule> rules = ruleSet.getRules();
        rulesRebuilder.rebuildRulesForDSL(libraries, rules);
    }

    public void setRulesRebuilder(RulesRebuilder rulesRebuilder) {
        this.rulesRebuilder = rulesRebuilder;
    }


    public boolean support(Resource resource) {
        String path = resource.getPath();
        return path.toLowerCase().endsWith(Constant.UL_SUFFIX);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        contextBuilders = applicationContext.getBeansOfType(ContextBuilder.class).values();
    }
}
