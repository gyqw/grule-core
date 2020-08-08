package gyqw.grule.core.runtime;

import gyqw.grule.core.runtime.event.KnowledgeEvent;
import gyqw.grule.core.runtime.event.KnowledgeEventListener;

import java.util.List;

public interface KnowledgeEventManager {
    void addEventListener(KnowledgeEventListener var1);

    List<KnowledgeEventListener> getKnowledgeEventListeners();

    boolean removeEventListener(KnowledgeEventListener var1);

    void fireEvent(KnowledgeEvent var1);
}
