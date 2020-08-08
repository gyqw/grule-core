package gyqw.grule.core.model.function.impl;

import gyqw.grule.core.Utils;
import gyqw.grule.core.exception.RuleException;
import gyqw.grule.core.model.function.Argument;
import gyqw.grule.core.model.function.FunctionDescriptor;
import gyqw.grule.core.runtime.WorkingMemory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author fred
 * 2018-11-05 7:09 PM
 */
public class MaxValueFunctionDescriptor implements FunctionDescriptor {
    private boolean disabled = false;

    public MaxValueFunctionDescriptor() {
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getLabel() {
        return "求最大值";
    }

    public String getName() {
        return "MaxValue";
    }

    public Object doFunction(Object object, String property, WorkingMemory workingMemory) {
        Collection<?> list = null;
        if (object instanceof Collection) {
            list = (Collection) object;
            BigDecimal max = null;
            Iterator var6 = list.iterator();

            while (var6.hasNext()) {
                Object obj = var6.next();
                Object pvalue = Utils.getObjectProperty(obj, property);
                BigDecimal a = Utils.toBigDecimal(pvalue);
                if (max == null) {
                    max = a;
                } else {
                    int result = a.compareTo(max);
                    if (result == 1) {
                        max = a;
                    }
                }
            }

            return max;
        } else {
            throw new RuleException("Function[max value] parameter must be java.util.Collection type.");
        }
    }

    public Argument getArgument() {
        Argument p = new Argument();
        p.setName("集合对象");
        p.setNeedProperty(true);
        return p;
    }
}
