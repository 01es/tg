package ua.com.fielden.platform.example.dynamiccriteria.entities;

import java.util.Date;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.EntityTitle;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyTitle;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.MapEntityTo;
import ua.com.fielden.platform.entity.annotation.MapTo;
import ua.com.fielden.platform.entity.annotation.Observable;
import ua.com.fielden.platform.entity.annotation.Title;
import ua.com.fielden.platform.entity.validation.annotation.CompanionObject;
import ua.com.fielden.platform.example.dynamiccriteria.iao.INestedEntityDao;

@EntityTitle("Simple nested entity type")
@KeyType(String.class)
@KeyTitle(value = "Nested entity", desc = "Nested entity description")
@MapEntityTo("NESTEDENTITY")
@CompanionObject(INestedEntityDao.class)
public class SimpleNestedEntity extends AbstractEntity<String> {

    private static final long serialVersionUID = 1894452161311610171L;

    @IsProperty
    @Title(value = "String property", desc = "String property description")
    @MapTo("STRING_PROPERTY")
    private String stringProperty;

    @IsProperty
    @Title(value = "Init. date", desc = "Date of initiation")
    @MapTo("INIT_DATE")
    private Date initDate;

    @IsProperty
    @Title(value = "active", desc = "determines the activity of simple entity.")
    @MapTo("ACTIVE")
    private boolean active = false;

    @IsProperty
    @Title(value = "Num. value", desc = "Number value ")
    @MapTo("NUM_VALUE")
    private Integer numValue;

    public String getStringProperty() {
        return stringProperty;
    }

    @Observable
    public SimpleNestedEntity setStringProperty(final String stringProperty) {
        this.stringProperty = stringProperty;
        return this;
    }

    public Date getInitDate() {
        return initDate;
    }

    @Observable
    public SimpleNestedEntity setInitDate(final Date initDate) {
        this.initDate = initDate;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    @Observable
    public SimpleNestedEntity setActive(final boolean active) {
        this.active = active;
        return this;
    }

    public Integer getNumValue() {
        return numValue;
    }

    @Observable
    public SimpleNestedEntity setNumValue(final Integer numValue) {
        this.numValue = numValue;
        return this;
    }
}
