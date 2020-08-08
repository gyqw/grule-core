package gyqw.grule.core.runtime.rete;

import gyqw.grule.core.model.GeneralEntity;
import gyqw.grule.core.model.rule.lhs.BaseCriteria;
import gyqw.grule.core.runtime.agenda.Activation;
import gyqw.grule.core.runtime.agenda.ActivationImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactTracker {
    private Activation activation;
    private Map<Object, List<BaseCriteria>> objectCriteriaMap = new HashMap<>();

    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        ActivationImpl ac = (ActivationImpl) activation;
        ac.setObjectCriteriaMap(objectCriteriaMap);
        this.activation = activation;
    }

    public Map<Object, List<BaseCriteria>> getObjectCriteriaMap() {
        return objectCriteriaMap;
    }

    public void addObjectCriteria(Object obj, BaseCriteria criteria) {
        if (obj instanceof HashMap && !(obj instanceof GeneralEntity)) {
            obj = HashMap.class.getName();
        }
        if (objectCriteriaMap.containsKey(obj)) {
            List<BaseCriteria> list = objectCriteriaMap.get(obj);
            if (!list.contains(criteria)) {
                list.add(criteria);
            }
        } else {
            List<BaseCriteria> list = new ArrayList<>();
            list.add(criteria);
            objectCriteriaMap.put(obj, list);
        }
    }

    public FactTracker newSubFactTracker() {
        FactTracker tracker = new FactTracker();
        tracker.getObjectCriteriaMap().putAll(objectCriteriaMap);
        return tracker;
    }
}
