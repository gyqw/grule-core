package gyqw.grule.core.dsl;

import gyqw.grule.core.exception.RuleException;
import gyqw.grule.core.model.rule.Op;

public class DSLUtils {
    public static Op parseOp(RuleParserParser.OpContext ctx) {
        if (ctx.GreaterThen() != null) {
            return Op.GreaterThen;
        } else if (ctx.GreaterThenOrEquals() != null) {
            return Op.GreaterThenEquals;
        } else if (ctx.LessThen() != null) {
            return Op.LessThen;
        } else if (ctx.LessThenOrEquals() != null) {
            return Op.LessThenEquals;
        } else if (ctx.Equals() != null) {
            return Op.Equals;
        } else if (ctx.NotEquals() != null) {
            return Op.NotEquals;
        } else if (ctx.EndWith() != null) {
            return Op.EndWith;
        } else if (ctx.NotEndWith() != null) {
            return Op.NotEndWith;
        } else if (ctx.StartWith() != null) {
            return Op.StartWith;
        } else if (ctx.NotStartWith() != null) {
            return Op.NotStartWith;
        } else if (ctx.In() != null) {
            return Op.In;
        } else if (ctx.NotIn() != null) {
            return Op.NotIn;
        } else if (ctx.Match() != null) {
            return Op.Match;
        } else if (ctx.NotMatch() != null) {
            return Op.NotMatch;
        } else if (ctx.EqualsIgnoreCase() != null) {
            return Op.EqualsIgnoreCase;
        } else if (ctx.NotEqualsIgnoreCase() != null) {
            return Op.NotEqualsIgnoreCase;
        } else if (ctx.Contain() != null) {
            return Op.Contain;
        } else if (ctx.NotContain() != null) {
            return Op.NotContain;
        }
        throw new RuleException("Operator [" + ctx + "] is invalid.");
    }
}
