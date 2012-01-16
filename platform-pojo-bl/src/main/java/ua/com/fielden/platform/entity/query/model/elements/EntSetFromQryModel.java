package ua.com.fielden.platform.entity.query.model.elements;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class EntSetFromQryModel implements ISetOperand {
    private final EntQuery model;

    public EntSetFromQryModel(final EntQuery model) {
	super();
	this.model = model;
    }

    @Override
    public Set<String> getPropNames() {
	return model.getPropNames();
    }

    @Override
    public List<EntProp> getProps() {
	return model.getProps();
    }

    @Override
    public List<EntQuery> getSubqueries() {
	return Arrays.asList(new EntQuery[]{model});
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((model == null) ? 0 : model.hashCode());
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
	if (!(obj instanceof EntSetFromQryModel)) {
	    return false;
	}
	final EntSetFromQryModel other = (EntSetFromQryModel) obj;
	if (model == null) {
	    if (other.model != null) {
		return false;
	    }
	} else if (!model.equals(other.model)) {
	    return false;
	}
	return true;
    }
}