/*******************************************************************************
 * Copyright 2017 Bstek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package gyqw.grule.core.model.function.impl;

import java.math.BigDecimal;

import gyqw.grule.core.Utils;
import gyqw.grule.core.model.function.Argument;
import gyqw.grule.core.model.function.FunctionDescriptor;
import gyqw.grule.core.runtime.WorkingMemory;

/**
 * @author Jacky.gao
 * @since 2015年7月31日
 */
public class ExpFunctionDescriptor implements FunctionDescriptor {
	private boolean disabled=false;
	@Override
	public Argument getArgument() {
		Argument arg=new Argument();
		arg.setName("对象");
		arg.setNeedProperty(true);
		return arg;
	}

	@Override
	public Object doFunction(Object object, String property, WorkingMemory workingMemory) {
		Object obj= Utils.getObjectProperty(object, property);
		BigDecimal bigobj=Utils.toBigDecimal(obj);
		return Math.exp(bigobj.doubleValue());
	}

	@Override
	public String getName() {
		return "Exp";
	}

	@Override
	public String getLabel() {
		return "求指数";
	}

	@Override
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
