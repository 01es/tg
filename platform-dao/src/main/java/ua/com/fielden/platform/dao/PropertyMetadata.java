package ua.com.fielden.platform.dao;

import static ua.com.fielden.platform.dao.PropertyCategory.COLLECTIONAL;
import static ua.com.fielden.platform.dao.PropertyCategory.COMPONENT_DETAILS;
import static ua.com.fielden.platform.dao.PropertyCategory.COMPONENT_HEADER;
import static ua.com.fielden.platform.dao.PropertyCategory.ENTITY_MEMBER_OF_COMPOSITE_KEY;
import static ua.com.fielden.platform.dao.PropertyCategory.ONE2ONE_ID;
import static ua.com.fielden.platform.dao.PropertyCategory.PRIMITIVE_MEMBER_OF_COMPOSITE_KEY;
import static ua.com.fielden.platform.dao.PropertyCategory.SYNTHETIC;
import static ua.com.fielden.platform.dao.PropertyCategory.SYNTHETIC_COMPONENT_DETAILS;
import static ua.com.fielden.platform.dao.PropertyCategory.SYNTHETIC_COMPONENT_HEADER;
import static ua.com.fielden.platform.dao.PropertyCategory.UNION_ENTITY_DETAILS;
import static ua.com.fielden.platform.dao.PropertyCategory.UNION_ENTITY_HEADER;
import static ua.com.fielden.platform.dao.PropertyCategory.VIRTUAL_OVERRIDE;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.type.Type;

import ua.com.fielden.platform.entity.AbstractUnionEntity;
import ua.com.fielden.platform.entity.annotation.MapTo;
import ua.com.fielden.platform.entity.query.ICompositeUserTypeInstantiate;
import ua.com.fielden.platform.entity.query.IUserTypeInstantiate;
import ua.com.fielden.platform.entity.query.generation.elements.ResultQueryYieldDetails.YieldDetailsType;
import ua.com.fielden.platform.entity.query.model.ExpressionModel;
import ua.com.fielden.platform.reflection.AnnotationReflector;
import ua.com.fielden.platform.utils.EntityUtils;

public class PropertyMetadata implements Comparable<PropertyMetadata> {
    private final String name;
    private final Class javaType;
    private final Object hibType;
    private final PropertyCategory type;
    private final boolean nullable;

    private final List<PropertyColumn> columns;
    private final ExpressionModel expressionModel;
    private final boolean aggregatedExpression; // contains aggregation function on the root level (i.e. Totals in entity centre tree)

    transient private final Logger logger = Logger.getLogger(this.getClass());
    
    private PropertyMetadata(final Builder builder) {
        type = builder.type;
        name = builder.name;
        javaType = builder.javaType;
        hibType = builder.hibType;
        columns = builder.columns;
        nullable = builder.nullable;
        expressionModel = builder.expressionModel;
        aggregatedExpression = builder.aggregatedExpression;
        if (!isCollection() && expressionModel == null && (columns == null || columns.size() == 0)) {
            //System.out.println(" ----------------" + type + " ------------- " +  name + " == " + javaType + " -------------------------------------------------------");
            //throw new RuntimeException(" ----------------" + type + " ------------- " +  name + " == " + javaType + " -------------------------------------------------------");
        }
    }

    public YieldDetailsType getYieldDetailType() {
        if (aggregatedExpression) {
            return YieldDetailsType.AGGREGATED_EXPRESSION;
        }
        return isCompositeProperty() ? YieldDetailsType.COMPOSITE_TYPE_HEADER : (isUnionEntity() ? YieldDetailsType.UNION_ENTITY_HEADER : YieldDetailsType.USUAL_PROP);
    }

    public boolean affectsMapping() {
        return type.affectsMappings();
    }

    public Type getHibTypeAsType() {
        return hibType instanceof Type ? (Type) hibType : null;
    }

    public IUserTypeInstantiate getHibTypeAsUserType() {
        return hibType instanceof IUserTypeInstantiate ? (IUserTypeInstantiate) hibType : null;
    }

    public ICompositeUserTypeInstantiate getHibTypeAsCompositeUserType() {
        return hibType instanceof ICompositeUserTypeInstantiate ? (ICompositeUserTypeInstantiate) hibType : null;
    }

    public String getSinglePropertyOfCompositeUserType() {
        final ICompositeUserTypeInstantiate compositeUserTypeInstance = getHibTypeAsCompositeUserType();
        if (compositeUserTypeInstance != null) {
            final String[] propNames = compositeUserTypeInstance.getPropertyNames();
            if (propNames.length == 1) {
                return propNames[0];
            }
        }
        return null;
    }

    public boolean isCalculated() {
        return expressionModel != null && type != COMPONENT_HEADER;
    }

    public boolean isCompositeProperty() {
        return getHibTypeAsCompositeUserType() != null;
    }

    public boolean isEntityOfPersistedType() {
        return EntityUtils.isPersistedEntityType(javaType) && !isCollection()/* && !isId()*/;
    }

    public boolean isCollection() {
        return type.equals(COLLECTIONAL);
    }

//    public boolean isCommonCalculated() {
//        return type.equals(EXPRESSION_COMMON);
//    }

    public boolean isOne2OneId() {
        return type.equals(ONE2ONE_ID);
    }

    public boolean isUnionEntity() {
        return type.equals(UNION_ENTITY_HEADER);
    }

    public boolean isUnionEntityDetails() {
        return type.equals(UNION_ENTITY_DETAILS);
    }

    public boolean isEntityMemberOfCompositeKey() {
        return type.equals(ENTITY_MEMBER_OF_COMPOSITE_KEY);
    }

    public boolean isPrimitiveMemberOfCompositeKey() {
        return type.equals(PRIMITIVE_MEMBER_OF_COMPOSITE_KEY);
    }

    public boolean isVirtual() {
        return type.equals(VIRTUAL_OVERRIDE);
    }

    public boolean isSynthetic() {
        return type.equals(SYNTHETIC) || type.equals(SYNTHETIC_COMPONENT_HEADER) || type.equals(SYNTHETIC_COMPONENT_DETAILS);
    }

    public String getTypeString() {
        if (hibType != null) {
            return hibType.getClass().getName();
        } else {
            return null;
        }
    }

    public Set<PropertyMetadata> getCompositeTypeSubprops() {
        final Set<PropertyMetadata> result = new HashSet<PropertyMetadata>();
        if (COMPONENT_HEADER.equals(type) || SYNTHETIC_COMPONENT_HEADER.equals(type) || getHibTypeAsCompositeUserType() != null) {
            logger.debug("=== (COMPONENT_HEADER.equals(type)) = " + COMPONENT_HEADER.equals(type) + "=== (SYNTHETIC_COMPONENT_HEADER.equals(type)) = " + SYNTHETIC_COMPONENT_HEADER.equals(type) + " ---- (getHibTypeAsCompositeUserType() != null) = " + (getHibTypeAsCompositeUserType() != null));
            final List<String> subprops = Arrays.asList(((ICompositeUserTypeInstantiate) hibType).getPropertyNames());
            final List<Object> subpropsTypes = Arrays.asList(((ICompositeUserTypeInstantiate) hibType).getPropertyTypes());
            PropertyCategory detailsPropCategory = COMPONENT_HEADER.equals(type) ?  COMPONENT_DETAILS : SYNTHETIC_COMPONENT_DETAILS;
            if (subprops.size() == 1) {
                final Object hibType = subpropsTypes.get(0);
                if (expressionModel != null) {
                    result.add(new PropertyMetadata.Builder(name + "." + subprops.get(0), ((Type) hibType).getReturnedClass(), nullable).expression(getExpressionModel()).aggregatedExpression(aggregatedExpression).type(detailsPropCategory).hibType(hibType).build());
                } else if (columns.size() == 0) {
                    result.add(new PropertyMetadata.Builder(name + "." + subprops.get(0), ((Type) hibType).getReturnedClass(), nullable).aggregatedExpression(aggregatedExpression).type(detailsPropCategory).hibType(hibType).build());
                } else {
                    result.add(new PropertyMetadata.Builder(name + "." + subprops.get(0), ((Type) subpropsTypes.get(0)).getReturnedClass(), nullable).column(columns.get(0)).type(detailsPropCategory).hibType(subpropsTypes.get(0)).build());
                }
            } else {
                int index = 0;
                for (final String subpropName : subprops) {
                    final PropertyColumn column = columns.get(index);
                    final Object hibType = subpropsTypes.get(index);
                    result.add(new PropertyMetadata.Builder(name + "." + subpropName, ((Type) hibType).getReturnedClass(), nullable).column(column).type(detailsPropCategory).hibType(hibType).build());
                    index = index + 1;
                }
            }

        }
        return result;
    }

    public Set<PropertyMetadata> getComponentTypeSubprops() {
        final Set<PropertyMetadata> result = new HashSet<PropertyMetadata>();
        if (UNION_ENTITY_HEADER.equals(type)) {
            final List<Field> propsFields = AbstractUnionEntity.unionProperties(javaType);
            for (final Field subpropField : propsFields) {
                final MapTo mapTo = AnnotationReflector.getAnnotation(subpropField, MapTo.class);
                if (mapTo == null) {
                    throw new IllegalStateException("Property [" + subpropField.getName() + "] in union entity type [" + javaType + "] is not annotated  no MapTo ");
                }
                final PropertyColumn column = new PropertyColumn(getColumn() + "_" + (StringUtils.isEmpty(mapTo.value()) ? subpropField.getName() : mapTo.value()));
                result.add(new PropertyMetadata.Builder(name + "." + subpropField.getName(), subpropField.getType(), true).column(column).type(PropertyCategory.UNION_ENTITY_DETAILS).hibType(Hibernate.LONG).build());
            }
        }
        return result;
    }

    public PropertyColumn getColumn() {
        return columns.size() > 0 ? columns.get(0) : null;
    }

    
    public String getName() {
        return name;
    }

    public Class getJavaType() {
        return javaType;
    }

    public Object getHibType() {
        return hibType;
    }

    public PropertyCategory getType() {
        return type;
    }

    public List<PropertyColumn> getColumns() {
        return columns;
    }

    public boolean isNullable() {
        return nullable;
    }

    public ExpressionModel getExpressionModel() {
        return expressionModel;
    }

    public boolean isAggregatedExpression() {
        return aggregatedExpression;
    }

    @Override
    public String toString() {
        return "\nname = " + name + " javaType = " + (javaType != null ? javaType.getSimpleName() : javaType) + " hibType = "
                + (hibType != null ? hibType/*.getClass().getSimpleName()*/: hibType) + " type = " + type + " aggregatedExpression = " + isAggregatedExpression() + "\ncolumn(s) = " + columns + " nullable = " + nullable
                + " calculated = " + isCalculated() + (isCalculated() ? " exprModel = " + expressionModel : "");
    }

    @Override
    public int compareTo(final PropertyMetadata o) {
        final boolean areEqual = this.equals(o);
        final int nameComp = name.compareTo(o.name);
        return nameComp != 0 ? nameComp : (areEqual ? 0 : 1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (aggregatedExpression ? 1231 : 1237);
        result = prime * result + ((columns == null) ? 0 : columns.hashCode());
        result = prime * result + ((expressionModel == null) ? 0 : expressionModel.hashCode());
        result = prime * result + ((hibType == null) ? 0 : hibType.hashCode());
        result = prime * result + ((javaType == null) ? 0 : javaType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (nullable ? 1231 : 1237);
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        if (!(obj instanceof PropertyMetadata)) {
            return false;
        }
        final PropertyMetadata other = (PropertyMetadata) obj;
        if (aggregatedExpression != other.aggregatedExpression) {
            return false;
        }
        if (columns == null) {
            if (other.columns != null) {
                return false;
            }
        } else if (!columns.equals(other.columns)) {
            return false;
        }
        if (expressionModel == null) {
            if (other.expressionModel != null) {
                return false;
            }
        } else if (!expressionModel.equals(other.expressionModel)) {
            return false;
        }
        if (hibType == null) {
            if (other.hibType != null) {
                return false;
            }
        } else if (!hibType.equals(other.hibType)) {
            return false;
        }
        if (javaType == null) {
            if (other.javaType != null) {
                return false;
            }
        } else if (!javaType.equals(other.javaType)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (nullable != other.nullable) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

    public static class Builder {
        private final String name;
        private final Class javaType;
        private final boolean nullable;

        private Object hibType;
        private List<PropertyColumn> columns = new ArrayList<PropertyColumn>();
        private PropertyCategory type;// = PropertyCategory.PROP;
        private ExpressionModel expressionModel;
        private boolean aggregatedExpression = false;

        public PropertyMetadata build() {
            return new PropertyMetadata(this);
        }

        public Builder(final String name, final Class javaType, final boolean nullable) {
            this.name = name;
            this.javaType = javaType;
            this.nullable = nullable;
        }

        public Builder hibType(final Object val) {
            hibType = val;
            return this;
        }

        public Builder expression(final ExpressionModel val) {
            expressionModel = val;
            return this;
        }

        public Builder type(final PropertyCategory val) {
            type = val;
            return this;
        }

        public Builder column(final PropertyColumn column) {
            columns.add(column);
            return this;
        }

        public Builder aggregatedExpression(final boolean val) {
            aggregatedExpression = val;
            return this;
        }

        public Builder columns(final List<PropertyColumn> columns) {
            this.columns.addAll(columns);
            return this;
        }
    }
}