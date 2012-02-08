package ua.com.fielden.platform.entity.query.generation.elements;


public class YearOfModel extends SingleOperandFunctionModel {

    public YearOfModel(final ISingleOperand operand) {
	super(operand);
    }

    @Override
    public String sql() {
	return "YEAR(" + getOperand().sql() + ")";
    }
}