package ua.com.fielden.platform.swing.review;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ua.com.fielden.platform.dao.IEntityProducer;
import ua.com.fielden.platform.dao2.IEntityDao2;
import ua.com.fielden.platform.domaintree.IGlobalDomainTreeManager;
import ua.com.fielden.platform.domaintree.master.IMasterDomainTreeManager;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.matcher.IValueMatcherFactory2;
import ua.com.fielden.platform.reflection.TitlesDescsGetter;
import ua.com.fielden.platform.swing.model.DefaultEntityProducer;
import ua.com.fielden.platform.swing.model.IUmViewOwner;
import ua.com.fielden.platform.swing.review.factory.IEntityMasterFactory;
import ua.com.fielden.platform.swing.view.BaseFrame;
import ua.com.fielden.platform.swing.view.IEntityMasterCache;
import ua.com.fielden.platform.swing.view.WeakEntityMasterCache;

import com.google.inject.Inject;

/**
 * {@link IEntityMasterManager} implementation for TG platform.
 *
 * @author TG Team
 *
 */
public class EntityMasterManager implements IEntityMasterManager {

    private final EntityFactory entityFactory;

    private final Map<Class<? extends AbstractEntity>, IEntityMasterCache> entityMasterCaches = Collections.synchronizedMap(new HashMap<Class<? extends AbstractEntity>, IEntityMasterCache>());

    private final IValueMatcherFactory2 vmf;

    private final IGlobalDomainTreeManager gdtm;

    private final Map<Class<? extends AbstractEntity>, IEntityMasterFactory> factories = new HashMap<Class<? extends AbstractEntity>, IEntityMasterFactory>();

    private final Map<Class<? extends AbstractEntity>, IEntityProducer> entityProducers = new HashMap<Class<? extends AbstractEntity>, IEntityProducer>();

    @Inject
    public EntityMasterManager(//
	    final EntityFactory entityFactory, //
	    final IValueMatcherFactory2 vmf,//
	    final IGlobalDomainTreeManager gdtm//
	    ) {
	this.entityFactory = entityFactory;
	this.vmf = vmf;
	this.gdtm = gdtm;
    }

    /**
     * Associates specified {@code entityClass} with the specified {@link IEntityMasterFactory} instance in this manager. When this manager will need to create new master frame for
     * entity with such {@code entityClass} it will use this factory.
     *
     * @param <T>
     * @param <DAO>
     * @param entityClass
     * @param factory
     * @return
     */
    public <T extends AbstractEntity, DAO extends IEntityDao2<T>> EntityMasterManager addFactory(final Class<T> entityClass, final IEntityMasterFactory<T, DAO> factory) {
	factories.put(entityClass, factory);
	return this;
    }

    /**
     * Associates specified {@code entityClass} with the specified {@link IEntityProducer} instance. When this manager will need to produce entity of the specified
     * {@code entityClass}, it will use specified {@link IEntityProducer} instance.
     *
     * @param <T>
     * @param entityClass
     * @param entityProducer
     * @return
     */
    public <T extends AbstractEntity> EntityMasterManager addEntityProducer(final Class<T> entityClass, final IEntityProducer<T> entityProducer) {
	entityProducers.put(entityClass, entityProducer);
	return this;
    }

    private IEntityMasterCache getEntityMasterCache(final Class<? extends AbstractEntity> entityType) {
	final IEntityMasterCache cache = entityMasterCaches.get(entityType);
	if (cache == null) {
	    final IEntityMasterCache newCache = new WeakEntityMasterCache();
	    entityMasterCaches.put(entityType, newCache);
	    return newCache;
	}
	return cache;
    }

    public <T extends AbstractEntity, DAO extends IEntityDao2<T>> BaseFrame showMaster(final T entity, final IUmViewOwner owner) {
	// let's be defencive...
	if (entity == null) {
	    throw new IllegalArgumentException("<html>Master cannot be displayed for <b>null</b> domain entity.</html>");
	}
	// searching in cache first ...

	final IEntityMasterCache cache = getEntityMasterCache(entity.getType());
	BaseFrame frame = cache.get(entity.getId());
	if (frame == null) {
	    // if not found in cache, then creating new master frame and putting it to the cache
	    final IEntityMasterFactory<T, DAO> factory = factories.get(entity.getType());
	    if (factory == null) {
		throw new IllegalArgumentException("No master factory found for " + TitlesDescsGetter.getEntityTitleAndDesc(entity.getType()).getKey() + " domain entity.");
	    }
	    IMasterDomainTreeManager masterManager = gdtm.getEntityMasterManager(entity.getType());
	    if (masterManager == null) {
		gdtm.initEntityMasterManager(entity.getType());
		masterManager = gdtm.getEntityMasterManager(entity.getType());
	    }
	    frame = factory.createMasterFrame(getEntityProducer(entity.getType()), //
		    cache, //
		    entity, //
		    vmf, //
		    masterManager, //
		    owner);
	    cache.put(frame, entity.getId());
	}
	// bringing BaseFrame to front in this manner, because simple call won't do the trick
	frame.setVisible(false);
	frame.setVisible(true);
	return frame;
    }

    /**
     * Returns either specific {@link IEntityProducer} instance (if one was associated with the specified {@code entityClass} using
     * {@link #addEntityProducer(Class, IEntityProducer)} method) or {@link DefaultEntityProducer} instance otherwise.
     */
    @Override
    public <T extends AbstractEntity> IEntityProducer<T> getEntityProducer(final Class<T> entityClass) {
	final IEntityProducer<T> entityProducer = entityProducers.get(entityClass);
	return entityProducer != null ? entityProducer : new DefaultEntityProducer<T>(entityFactory, entityClass);
    }

    /**
     * Returns {@link IEntityMasterCache} used by this manager.
     *
     * @return
     */
    public Map<Class<? extends AbstractEntity>, IEntityMasterCache> getEntityMasterCache() {
	return Collections.unmodifiableMap(entityMasterCaches);
    }

}
