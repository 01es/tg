package ua.com.fielden.platform.domaintree.impl;

import java.lang.annotation.Annotation;
import java.util.Set;

import ua.com.fielden.platform.domaintree.IDomainTreeEnhancer.IncorrectCalcPropertyKeyException;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.entity.validation.IBeforeChangeEventHandler;
import ua.com.fielden.platform.error.Result;

/**
 * {@link CalculatedProperty} validation for its expression in a provided context.
 *
 * @author TG Team
 *
 */
public class BceContextualExpressionValidation implements IBeforeChangeEventHandler<String> {
    @Override
    public Result handle(final MetaProperty property, final String newContextualExpression, final String oldValue, final Set<Annotation> mutatorAnnotations) {
	final CalculatedProperty cp = (CalculatedProperty) property.getEntity();

	if (!cp.getProperty("root").isValid()) {
	    return new IncorrectCalcPropertyKeyException("Cannot set contextualExpression when the Root type of calculated property is not valid.");
	}
	if (!cp.getProperty("contextPath").isValid()) {
	    return new IncorrectCalcPropertyKeyException("Cannot set contextualExpression when the Context Path of calculated property is not valid.");
	}

	try {
	    cp.initAst(newContextualExpression);
	} catch (final Exception ex) {
	    ex.printStackTrace();
	    return new Result(ex);
	}

	// TODO uncomment
//	if (TaggingVisitor.ABOVE.equals(cp.getAst().getTag())) {
//	    if (AbstractDomainTreeRepresentation.isCollectionOrInCollectionHierarchy(cp.getRoot(), cp.getContextPath())) {
//		return new Result(new IllegalStateException("Aggregated collections are currently unsupported. Please try to use simple expressions under collections (or with ALL / ANY attributes)."));
//	    }
//	}
	return Result.successful(cp.getAst());
    }
}