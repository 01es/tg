package ua.com.fielden.platform.sample.domain;

import org.junit.Ignore;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.DescTitle;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.MapEntityTo;
import ua.com.fielden.platform.entity.annotation.MapTo;
import ua.com.fielden.platform.entity.annotation.Observable;
import ua.com.fielden.platform.entity.annotation.Title;

@KeyType(String.class)
@MapEntityTo
@DescTitle("Description")
@Ignore
public class TgOrgUnit4 extends AbstractEntity<String> {
    private static final long serialVersionUID = 1L;

    @IsProperty
    @MapTo
    @Title(value = "Parent", desc = "Parent")
    private TgOrgUnit3 parent;

    @Observable
    public TgOrgUnit4 setParent(final TgOrgUnit3 parent) {
	this.parent = parent;
	return this;
    }

    public TgOrgUnit3 getParent() {
	return parent;
    }
    /**
     * Constructor for (@link EntityFactory}.
     */
    protected TgOrgUnit4() {
    }
}