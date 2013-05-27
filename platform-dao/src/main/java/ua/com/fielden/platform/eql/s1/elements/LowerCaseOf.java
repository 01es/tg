package ua.com.fielden.platform.eql.s1.elements;

import ua.com.fielden.platform.eql.s2.elements.ISingleOperand2;


public class LowerCaseOf extends SingleOperandFunction<ua.com.fielden.platform.eql.s2.elements.LowerCaseOf> {
    public LowerCaseOf(final ISingleOperand<? extends ISingleOperand2> operand) {
	super(operand);
    }

    @Override
    public ua.com.fielden.platform.eql.s2.elements.LowerCaseOf transform() {
	return new ua.com.fielden.platform.eql.s2.elements.LowerCaseOf(getOperand().transform());
    }
}