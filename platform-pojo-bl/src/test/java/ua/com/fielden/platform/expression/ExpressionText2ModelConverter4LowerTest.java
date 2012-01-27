package ua.com.fielden.platform.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static ua.com.fielden.platform.entity.query.fluent.query.expr;
import static ua.com.fielden.platform.expression.ast.visitor.TaggingVisitor.ABOVE;
import static ua.com.fielden.platform.expression.ast.visitor.TaggingVisitor.THIS;

import java.math.BigDecimal;

import org.junit.Test;

import ua.com.fielden.platform.entity.query.model.ExpressionModel;
import ua.com.fielden.platform.expression.ast.AstNode;
import ua.com.fielden.platform.expression.ast.visitor.entities.EntityLevel1;
import ua.com.fielden.platform.expression.exception.RecognitionException;
import ua.com.fielden.platform.expression.exception.semantic.IncompatibleOperandException;
import ua.com.fielden.platform.expression.exception.semantic.SemanticException;

public class ExpressionText2ModelConverter4LowerTest {

    @Test
    public void test_case_01() throws RecognitionException, SemanticException {
	final ExpressionText2ModelConverter ev = new ExpressionText2ModelConverter(EntityLevel1.class, "LOWER(strProperty) + strProperty");
	final AstNode root = ev.convert();
	assertEquals("Incorrect expression type", String.class, root.getType());

	final ExpressionModel func = expr().lowerCase().prop("strProperty").model();
	final ExpressionModel model = expr().expr(func).add().prop("strProperty").model();
	assertEquals("Incorrect model.", model, root.getModel());
    }

    @Test
    public void test_case_02() throws RecognitionException, SemanticException {
	final ExpressionText2ModelConverter ev = new ExpressionText2ModelConverter(EntityLevel1.class, "LOWER(MAX(collectional.strProperty)) + strProperty");
	final AstNode root = ev.convert();
	assertEquals("Incorrect expression type", String.class, root.getType());

	final ExpressionModel max = expr().maxOf().prop("collectional.strProperty").model();
	final ExpressionModel func = expr().lowerCase().expr(max).model();
	final ExpressionModel model = expr().expr(func).add().prop("strProperty").model();
	assertEquals("Incorrect model.", model, root.getModel());
    }

    @Test
    public void test_case_03() throws RecognitionException, SemanticException {
	final ExpressionText2ModelConverter ev = new ExpressionText2ModelConverter(EntityLevel1.class, "(COUNT(LOWER(collectional.strProperty)) + intProperty) * 2");
	final AstNode root = ev.convert();
	assertEquals("Incorrect expression type", Integer.class, root.getType());

	final ExpressionModel fun = expr().lowerCase().prop("collectional.strProperty").model();
	final ExpressionModel countOfDays = expr().countOf().expr(fun).model();
	final ExpressionModel plus1 = expr().expr(countOfDays).add().prop("intProperty").model();
	final ExpressionModel model = expr().expr(plus1).mult().val(2).model();
	assertEquals("Incorrect model.", model, root.getModel());
    }

    @Test
    public void test_case_04() throws RecognitionException, SemanticException {
	final ExpressionText2ModelConverter ev = new ExpressionText2ModelConverter(EntityLevel1.class, "intProperty * intProperty / COUNT(LOWER(collectional.strProperty))");
	final AstNode root = ev.convert();
	assertEquals("Incorrect expression type", BigDecimal.class, root.getType());

	final ExpressionModel fun = expr().lowerCase().prop("collectional.strProperty").model();
	final ExpressionModel count = expr().countOf().expr(fun).model();
	final ExpressionModel mult = expr().prop("intProperty").mult().prop("intProperty").model();
	final ExpressionModel model = expr().expr(mult).div().expr(count).model();
	assertEquals("Incorrect model.", model, root.getModel());
    }

    @Test
    public void test_case_05() throws RecognitionException, SemanticException {
	final ExpressionText2ModelConverter ev = new ExpressionText2ModelConverter(EntityLevel1.class, "entityProperty.intProperty * COUNT(LOWER(collectional.strProperty))");
	final AstNode root = ev.convert();
	assertEquals("Incorrect expression type", Integer.class, root.getType());

	final ExpressionModel func = expr().lowerCase().prop("collectional.strProperty").model();
	final ExpressionModel count = expr().countOf().expr(func).model();
	final ExpressionModel model = expr().prop("entityProperty.intProperty").mult().expr(count).model();
	assertEquals("Incorrect model.", model, root.getModel());
    }

    @Test
    public void test_case_06() throws RecognitionException, SemanticException {
	final ExpressionText2ModelConverter ev = new ExpressionText2ModelConverter(EntityLevel1.class, "LOWER(MAX(entityProperty.collectional.strProperty)) + MAX(collectional.strProperty)");
	final AstNode root = ev.convert();
	assertEquals("Incorrect expression type", String.class, root.getType());

	final ExpressionModel max1 = expr().maxOf().prop("entityProperty.collectional.strProperty").model();
	final ExpressionModel fun = expr().lowerCase().expr(max1).model();
	final ExpressionModel max2 = expr().maxOf().prop("collectional.strProperty").model();
	final ExpressionModel model = expr().expr(fun).add().expr(max2).model();
	assertEquals("Incorrect model.", model, root.getModel());
    }

    @Test
    public void test_case_07() throws RecognitionException, SemanticException {
	final ExpressionText2ModelConverter ev = new ExpressionText2ModelConverter(EntityLevel1.class, "COUNT(LOWER(collectional.strProperty) + LOWER(selfProperty.collectional.dateProperty))");
	try {
	    ev.convert();
	    fail("Should have failed due to incorrect tag.");
	} catch (final IncompatibleOperandException ex) {
	    assertEquals("Incorrect message", "Incompatible operand context for operation '+'", ex.getMessage());
	}
    }

    @Test
    public void test_case_08() throws RecognitionException, SemanticException {
	final ExpressionText2ModelConverter ev = new ExpressionText2ModelConverter(EntityLevel1.class, "COUNT(LOWER(selfProperty.strProperty) + LOWER(entityProperty.strProperty))");
	final AstNode root = ev.convert();
	assertEquals("Incorrect expression type", Integer.class, root.getType());
	assertEquals("Incorrect expression tag", ABOVE, root.getTag());

	final ExpressionModel func1 = expr().lowerCase().prop("selfProperty.strProperty").model();
	final ExpressionModel func2 = expr().lowerCase().prop("entityProperty.strProperty").model();
	final ExpressionModel plus = expr().expr(func1).add().expr(func2).model();
	final ExpressionModel model = expr().countOf().expr(plus).model();
	assertEquals("Incorrect model.", model, root.getModel());
    }

    @Test
    public void test_case_09() throws RecognitionException, SemanticException {
	final ExpressionText2ModelConverter ev = new ExpressionText2ModelConverter(EntityLevel1.class, "COUNT(LOWER(selfProperty.collectional.strProperty)) + COUNT(LOWER(entityProperty.collectional.strProperty))");
	final AstNode root = ev.convert();
	assertEquals("Incorrect expression type", Integer.class, root.getType());
	assertEquals("Incorrect expression tag", THIS, root.getTag());

	final ExpressionModel fun1 = expr().lowerCase().prop("selfProperty.collectional.strProperty").model();
	final ExpressionModel count1 = expr().countOf().expr(fun1).model();
	final ExpressionModel fun2 = expr().lowerCase().prop("entityProperty.collectional.strProperty").model();
	final ExpressionModel count2 = expr().countOf().expr(fun2).model();
	final ExpressionModel model = expr().expr(count1).add().expr(count2).model();
	assertEquals("Incorrect model.", model, root.getModel());
    }

}