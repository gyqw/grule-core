package gyqw.grule.core.model.scorecard.runtime;

import gyqw.grule.core.Utils;
import gyqw.grule.core.debug.MsgType;
import gyqw.grule.core.runtime.rete.Context;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Jacky.gao
 * @since 2016年9月26日
 */
public class ScorecardImpl implements Scorecard {
    private String name;
    private boolean debug;
    private List<RowItem> rowItems;

    public ScorecardImpl(String name, List<RowItem> rowItems, boolean debug) {
        this.name = name;
        this.rowItems = rowItems;
        this.debug = debug;
    }

    public BigDecimal executeSum(Context context) {
        BigDecimal result = new BigDecimal(0);
        for (RowItem row : rowItems) {
            BigDecimal score = Utils.toBigDecimal(row.getScore());
            row.setActualScore(score);
            result = result.add(score);
        }
        String msg = "+++ 求和得分：" + result;
        context.logMsg(msg, MsgType.ScoreCard);
        return result;
    }

    public BigDecimal executeWeightSum(Context context) {
        BigDecimal result = new BigDecimal(0);
        for (RowItem row : rowItems) {
            BigDecimal score = Utils.toBigDecimal(row.getScore());
            BigDecimal weight = Utils.toBigDecimal(row.getWeight());
            BigDecimal actualScore = score.multiply(weight);
            row.setActualScore(actualScore);
            result = result.add(actualScore);
        }
        String msg = "+++ 加权求和得分：" + result;
        context.logMsg(msg, MsgType.ScoreCard);
        return result;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<RowItem> getRowItems() {
        return rowItems;
    }
}
