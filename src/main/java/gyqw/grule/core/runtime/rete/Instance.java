package gyqw.grule.core.runtime.rete;

import java.util.Collection;

public interface Instance {
    Collection<FactTracker> enter(EvaluationContext context, Object obj, FactTracker tracker);
}
