package ua.com.fielden.platform.eql.meta;


public class EntityTypePropInfo extends AbstractPropInfo  {
    private final EntityInfo propEntityInfo;

    @Override
    public String toString() {
        return super.toString() + ": " + propEntityInfo;
    }

    public EntityTypePropInfo(final String name, final EntityInfo parent, final EntityInfo propEntityInfo) {
	super(name, parent);
	this.propEntityInfo = propEntityInfo;
    }

    protected EntityInfo getPropEntityInfo() {
        return propEntityInfo;
    }

    @Override
    public Object resolve(final String dotNotatedSubPropName) {
	return dotNotatedSubPropName != null ? getPropEntityInfo().resolve(dotNotatedSubPropName) : this;
    }
}