package gyqw.grule.core.model.flow;

import gyqw.grule.core.model.rule.Library;

import java.util.List;

/**
 * @author Jacky.gao
 * @since 2015年7月20日
 */
public interface ProcessDefinition {
    List<Library> getLibraries();

    String getId();

    boolean isDebug();

    List<FlowNode> getNodes();

}
