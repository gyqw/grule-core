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
 * 2018-11-05 7:08 PM
 */
public class MinValueFunctionDescriptor implements FunctionDescriptor {
    private boolean disabled = false;

    public MinValueFunctionDescriptor() {
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getLabel() {
        return "求最小值";
    }

    public String getName() {
        return "MinValue";
    }

    public Object doFunction(Object object, String property, WorkingMemory workingMemory) {
        Collection<?> list = null;
        if (object instanceof Collection) {
            list = (Collection) object;
            BigDecimal min = null;
            Iterator var6 = list.iterator();

            while (var6.hasNext()) {
                Object obj = var6.next();
                Object pvalue = Utils.getObjectProperty(obj, property);
                BigDecimal a = Utils.toBigDecimal(pvalue);
                if (min == null) {
                    min = a;
                } else {
                    int result = a.compareTo(min);
                    if (result == -1) {
                        min = a;
                    }
                }
            }

            return min;
        } else {
            throw new RuleException("Function[min value] parameter must be java.util.Collection type.");
        }
    }

    public Argument getArgument() {
        Argument p = new Argument();
        p.setName("集合对象");
        p.setNeedProperty(true);
        return p;
    }
}
