package ua.com.fielden.platform.test;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ua.com.fielden.platform.entity.meta.PropertyDescriptor;
import ua.com.fielden.platform.persistence.types.DateTimeType;
import ua.com.fielden.platform.persistence.types.PropertyDescriptorType;
import ua.com.fielden.platform.persistence.types.SimpleMoneyType;
import ua.com.fielden.platform.types.Money;

public class PlatformTestHibernateSetup {
    private static final Map<Class, Class> hibTypeDefaults = new HashMap<Class, Class>();

    static {
        hibTypeDefaults.put(Date.class, DateTimeType.class);
        hibTypeDefaults.put(PropertyDescriptor.class, PropertyDescriptorType.class);
        hibTypeDefaults.put(Money.class, SimpleMoneyType.class);
    }

    public static Map<Class, Class> getHibernateTypes() {
        return Collections.unmodifiableMap(hibTypeDefaults);
    }
}