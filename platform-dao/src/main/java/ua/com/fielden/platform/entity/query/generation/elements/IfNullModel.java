package ua.com.fielden.platform.entity.query.generation.elements;


public class IfNullModel extends TwoOperandsFunctionModel {

    public IfNullModel(final ISingleOperand operand1, final ISingleOperand operand2) {
	super(operand1, operand2);
    }

    @Override
    public String sql() {
	return "COALESCE(" + getOperand1().sql() + ", " + getOperand2().sql() + ")";
    }
}