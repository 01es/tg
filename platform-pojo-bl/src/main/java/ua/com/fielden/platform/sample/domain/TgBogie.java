package ua.com.fielden.platform.sample.domain;

import org.junit.Ignore;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.DescTitle;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.MapEntityTo;
import ua.com.fielden.platform.entity.validation.annotation.DefaultController;
import ua.com.fielden.platform.sample.domain.controller.ITgBogie;

@KeyType(String.class)
@MapEntityTo
@DescTitle("Description")
@Ignore
@DefaultController(ITgBogie.class)
public class TgBogie extends AbstractEntity<String> {
    private static final long serialVersionUID = 1L;

    protected TgBogie() {
    }

//    @IsProperty
//    @MapTo
//    @Title(value = "Location", desc = "Location")
//    private TgBogieLocation location;
//
//    @Observable
//    public TgBogie setLocation(final TgBogieLocation location) {
//	this.location = location;
//	return this;
//    }
//
//    public TgBogieLocation getLocation() {
//	return location;
//    }
}