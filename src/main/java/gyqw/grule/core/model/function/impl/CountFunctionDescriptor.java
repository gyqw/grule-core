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

import java.util.Collection;

import gyqw.grule.core.exception.RuleException;
import gyqw.grule.core.model.function.Argument;
import gyqw.grule.core.model.function.FunctionDescriptor;
import gyqw.grule.core.runtime.WorkingMemory;

/**
 * @author Jacky.gao
 * @since 2015年7月22日
 */
public class CountFunctionDescriptor implements FunctionDescriptor {
	private boolean disabled=false;
	
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	@Override
	public String getLabel() {
		return "统计数量";
	}
	@Override
	public String getName() {
		return "Count";
	}
	@Override
	public Object doFunction(Object object, String property, WorkingMemory workingMemory) {
		Collection<?> list=null;
		if(object instanceof Collection){
			list=(Collection<?>)object;
		}else{
			throw new RuleException("Function[count] parameter must be java.util.Collection type.");
		}
		return list.size();
	}
	@Override
	public Argument getArgument() {
		Argument p=new Argument();
		p.setName("集合对象");
		return p;
	}
}
