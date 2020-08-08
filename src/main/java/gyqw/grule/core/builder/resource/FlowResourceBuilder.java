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
package gyqw.grule.core.builder.resource;

import org.dom4j.Element;

import gyqw.grule.core.model.flow.FlowDefinition;
import gyqw.grule.core.parse.deserializer.FlowDeserializer;

/**
 * @author Jacky.gao
 * @since 2014年12月22日
 */
public class FlowResourceBuilder implements ResourceBuilder<FlowDefinition> {
	private FlowDeserializer flowDeserializer;
	public FlowDefinition build(Element root) {
		return flowDeserializer.deserialize(root);
	}
	public ResourceType getType() {
		return ResourceType.Flow;
	}
	public boolean support(Element root) {
		return flowDeserializer.support(root);
	}
	public void setFlowDeserializer(FlowDeserializer flowDeserializer) {
		this.flowDeserializer = flowDeserializer;
	}
}