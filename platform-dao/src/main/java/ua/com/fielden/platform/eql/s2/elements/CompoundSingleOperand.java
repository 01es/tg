package ua.com.fielden.platform.eql.s2.elements;

import ua.com.fielden.platform.entity.query.fluent.ArithmeticalOperator;


public class CompoundSingleOperand {
    private final ISingleOperand2 operand;
    private final ArithmeticalOperator operator;

    public CompoundSingleOperand(final ISingleOperand2 operand, final ArithmeticalOperator operator) {
	super();
	this.operand = operand;
	this.operator = operator;
    }

    public ISingleOperand2 getOperand() {
        return operand;
    }
    public ArithmeticalOperator getOperator() {
        return operator;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((operand == null) ? 0 : operand.hashCode());
	result = prime * result + ((operator == null) ? 0 : operator.hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof CompoundSingleOperand)) {
	    return false;
	}
	final CompoundSingleOperand other = (CompoundSingleOperand) obj;
	if (operand == null) {
	    if (other.operand != null) {
		return false;
	    }
	} else if (!operand.equals(other.operand)) {
	    return false;
	}
	if (operator != other.operator) {
	    return false;
	}
	return true;
    }
}