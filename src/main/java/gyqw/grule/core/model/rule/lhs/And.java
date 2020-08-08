package gyqw.grule.core.model.rule.lhs;

public class And extends Junction {
	@Override
	public String getJunctionType() {
		return JunctionType.and.name();
	}
}
