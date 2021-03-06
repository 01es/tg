package ua.com.fielden.platform.sample.domain;

import java.util.HashSet;
import java.util.Set;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.CompanionObject;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyTitle;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.MapEntityTo;
import ua.com.fielden.platform.entity.annotation.MapTo;
import ua.com.fielden.platform.entity.annotation.Observable;
import ua.com.fielden.platform.entity.annotation.Title;
import ua.com.fielden.platform.entity.annotation.mutator.AfterChange;
import ua.com.fielden.platform.sample.domain.definers.PropDefiner;
import ua.com.fielden.platform.security.user.UserAndRoleAssociation;

/** 
 * Master entity object.
 * 
 * @author Developers
 *
 */
@KeyType(String.class)
@KeyTitle(value = "Key", desc = "Some key description")
@CompanionObject(ITgEntityWithPropertyDependency.class)
@MapEntityTo
public class TgEntityWithPropertyDependency extends AbstractEntity<String> {
    private static final long serialVersionUID = 1L;
    
    @IsProperty
    @MapTo
    @Title(value = "Property", desc = "Property")
    @AfterChange(PropDefiner.class)
    private String property;
    
    @IsProperty
    @MapTo
    @Title(value = "Dependent Prop", desc = "Dependent Prop")
    private String dependentProp;
    
    @IsProperty(value = UserAndRoleAssociation.class, linkProperty = "user")
    private Set<UserAndRoleAssociation> roles = new HashSet<UserAndRoleAssociation>();
    
    public Set<UserAndRoleAssociation> getRoles() {
        return roles;
    }

    @Observable
    public TgEntityWithPropertyDependency setRoles(final Set<UserAndRoleAssociation> roles) {
        this.roles = roles;
        return this;
    }

    @Observable
    public TgEntityWithPropertyDependency setDependentProp(final String dependentProp) {
        this.dependentProp = dependentProp;
        return this;
    }

    public String getDependentProp() {
        return dependentProp;
    }
    
    @Observable
    public TgEntityWithPropertyDependency setProperty(final String property) {
        this.property = property;
        return this;
    }

    public String getProperty() {
        return property;
    }
}