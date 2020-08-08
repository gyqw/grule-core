package gyqw.grule.core.runtime;

import gyqw.grule.core.runtime.event.KnowledgeEvent;
import gyqw.grule.core.runtime.event.KnowledgeEventListener;

import java.util.List;

public interface EventManager {
    void addEventListener(KnowledgeEventListener listener);

    boolean removeEventListener(KnowledgeEventListener listener);

    void fireEvent(KnowledgeEvent event);

    List<KnowledgeEventListener> getKnowledgeEventListeners();
}
