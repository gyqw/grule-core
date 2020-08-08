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
package gyqw.grule.core.model.rete.jsondeserializer;

import gyqw.grule.core.model.rule.SimpleValue;
import gyqw.grule.core.model.rule.Value;
import gyqw.grule.core.model.rule.ValueType;
import org.codehaus.jackson.JsonNode;

import gyqw.grule.core.model.rete.JsonUtils;

/**
 * @author Jacky.gao
 * @since 2015年3月6日
 */
public class InputValueDeserializer implements ValueDeserializer {

	@Override
	public Value deserialize(JsonNode jsonNode) {
		SimpleValue value=new SimpleValue();
		value.setContent(JsonUtils.getJsonValue(jsonNode, "content"));
		value.setArithmetic(JsonUtils.parseComplexArithmetic(jsonNode));
		return value;
	}

	@Override
	public boolean support(ValueType type) {
		return type.equals(ValueType.Input);
	}
}
