package ua.com.fielden.platform.web.view.master;

import ua.com.fielden.platform.basic.IValueMatcher;
import ua.com.fielden.platform.basic.autocompleter.EntityQueryValueMatcher;
import ua.com.fielden.platform.dao.DefaultEntityProducer;
import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.dao.IEntityProducer;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.factory.ICompanionObjectFinder;
import ua.com.fielden.platform.reflection.PropertyTypeDeterminator;
import ua.com.fielden.platform.web.interfaces.IRenderable;
import ua.com.fielden.platform.web.view.master.api.IMaster;
import ua.com.fielden.platform.web.view.master.api.ISimpleMasterConfig;

import com.google.inject.Injector;

/**
 * Represents entity master.
 *
 * @author TG Team
 *
 */
public class EntityMaster<T extends AbstractEntity<?>> implements IMaster<T> {
    private final Class<T> entityType;
    private final Class<? extends IEntityProducer<T>> entityProducerType;
    private final ISimpleMasterConfig<T> smConfig;
    private final ICompanionObjectFinder coFinder;
    private final Injector injector;

    /**
     * Creates master for the specified <code>entityType</code> and <code>entityProducerType</code>.
     *
     * @param entityType
     * @param entityProducerType
     * @param masterComponent
     *
     */
    public EntityMaster(
            final Class<T> entityType,
            final Class<? extends IEntityProducer<T>> entityProducerType,
                    final ISimpleMasterConfig<T> smConfig,
            final ICompanionObjectFinder coFinder,
            final Injector injector) {
        this.entityType = entityType;
        this.entityProducerType = entityProducerType;
        this.smConfig = smConfig;
        this.coFinder = coFinder;
        this.injector = injector;
    }

    /**
     * Creates master for the specified <code>entityType</code> and default entity producer.
     *
     * @param entityType
     * @param masterComponent
     *
     */
    public EntityMaster(final Class<T> entityType, final ISimpleMasterConfig<T> smConfig, final Injector injector) {
        this(entityType, null, smConfig, injector.getInstance(ICompanionObjectFinder.class), injector);
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    /**
     * Creates an entity producer instance.
     *
     * @param injector
     * @return
     */
    public IEntityProducer<T> createEntityProducer() {
        return entityProducerType == null ? new DefaultEntityProducer<T>(injector.getInstance(EntityFactory.class), this.entityType)
                : injector.getInstance(this.entityProducerType);
    }

    /**
     * Creates value matcher instance.
     *
     * @param injector
     * @return
     */
    public IValueMatcher<AbstractEntity<?>> createValueMatcher(final String propertyName) {
        // TODO: Currently only the default value matcher is used
        //       However, in future the value matcher should be obtained from property editor configuration

        final Class<?> propertyType = PropertyTypeDeterminator.determinePropertyType(entityType, propertyName);
        final IEntityDao<?> co = coFinder.find((Class<AbstractEntity<?>>) propertyType);

        final IValueMatcher<AbstractEntity<?>> matcher = (IValueMatcher<AbstractEntity<?>>) EntityQueryValueMatcher.matchByKey(co);
        return matcher;
    }

    @Override
    public IRenderable build() {
        return smConfig.render();
    }

}
