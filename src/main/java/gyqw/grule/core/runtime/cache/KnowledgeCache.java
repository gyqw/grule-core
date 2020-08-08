package gyqw.grule.core.runtime.cache;

import gyqw.grule.core.runtime.KnowledgePackage;

/**
 * @author Jacky.gao
 * 2015年1月28日
 */
public interface KnowledgeCache {
    String BEAN_ID = "urule.knowledgeCache";

    KnowledgePackage getKnowledge(String packageId);

    void putKnowledge(String packageId, KnowledgePackage knowledgePackage);

    void removeKnowledge(String packageId);
}
