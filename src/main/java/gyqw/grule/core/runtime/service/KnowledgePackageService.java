package gyqw.grule.core.runtime.service;

import gyqw.grule.core.runtime.KnowledgePackage;

import java.io.IOException;

public interface KnowledgePackageService {
    String BEAN_ID = "urule.knowledgePackageService";

    KnowledgePackage buildKnowledgePackage(String packageInfo) throws IOException;
}
