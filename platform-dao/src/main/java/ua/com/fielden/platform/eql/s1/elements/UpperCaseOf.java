package ua.com.fielden.platform.eql.s1.elements;

import ua.com.fielden.platform.eql.s2.elements.ISingleOperand2;


public class UpperCaseOf extends SingleOperandFunction<ua.com.fielden.platform.eql.s2.elements.UpperCaseOf> {
    public UpperCaseOf(final ISingleOperand<? extends ISingleOperand2> operand) {
	super(operand);
    }

    @Override
    public ua.com.fielden.platform.eql.s2.elements.UpperCaseOf transform() {
	return new ua.com.fielden.platform.eql.s2.elements.UpperCaseOf(getOperand().transform());
    }
}