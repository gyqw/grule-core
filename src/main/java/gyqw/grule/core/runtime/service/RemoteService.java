package gyqw.grule.core.runtime.service;

import gyqw.grule.core.runtime.KnowledgePackage;

/**
 * @author Jacky.gao
 * 2015年1月28日
 */
public interface RemoteService {
    String BEAN_ID = "urule.remoteService";

    KnowledgePackage getKnowledge(String packageId, String timestamp);
}
