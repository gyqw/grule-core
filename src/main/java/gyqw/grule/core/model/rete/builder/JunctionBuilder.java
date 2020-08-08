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
package gyqw.grule.core.model.rete.builder;

import gyqw.grule.core.model.rete.BaseReteNode;
import gyqw.grule.core.model.rete.ConditionNode;
import gyqw.grule.core.model.rule.lhs.Criteria;
import gyqw.grule.core.model.rule.lhs.Criterion;
import gyqw.grule.core.model.rule.lhs.Junction;

import java.util.List;

public abstract class JunctionBuilder extends CriterionBuilder {
    public JunctionBuilder() {
    }

    protected List<BaseReteNode> buildCriterion(Criterion criterion, BuildContext context, List<ConditionNode> prevCriteriaNodes) {
        if (criterion instanceof Junction) {
            Junction junction = (Junction) criterion;
            return ReteBuilder.buildCriterion(context, junction);
        } else if (criterion instanceof Criteria) {
            Criteria criteria = (Criteria) criterion;
            return this.buildCriteria(criteria, prevCriteriaNodes, context);
        } else {
            return null;
        }
    }
}
