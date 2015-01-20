package ua.com.fielden.platform.web.resources.webui;

import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchAll;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ua.com.fielden.platform.dao.IEntityProducer;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.factory.ICompanionObjectFinder;
import ua.com.fielden.platform.entity.query.fluent.fetch;
import ua.com.fielden.platform.reflection.PropertyTypeDeterminator;
import ua.com.fielden.platform.types.Money;
import ua.com.fielden.platform.utils.EntityUtils;
import ua.com.fielden.platform.web.resources.RestServerUtil;

/**
 * The web resource for entity validation serves as a back-end mechanism of changing entity properties and validating that changes.
 *
 * The server does not keep any state about the entities to be modified.
 *
 * @author TG Team
 *
 */
public class EntityValidationResource<T extends AbstractEntity<?>> extends ServerResource {
    private final EntityResourceMixin<T> mixin;
    private final RestServerUtil restUtil;
    private final Logger logger = Logger.getLogger(getClass());

    public EntityValidationResource(final Class<T> entityType, final IEntityProducer<T> entityProducer, final fetch<T> fetchStrategy, final EntityFactory entityFactory, final RestServerUtil restUtil, final ICompanionObjectFinder companionFinder, final Context context, final Request request, final Response response) {
        init(context, request, response);

        mixin = new EntityResourceMixin<T>(entityType, entityProducer, fetchStrategy, entityFactory, restUtil, companionFinder);
        this.restUtil = restUtil;
    }

    /**
     * Handles POST request resulting from RAO call to method save.
     */
    @Post
    @Override
    public Representation post(final Representation envelope) throws ResourceException {
        final Map<String, Object> modifiedPropertiesHolder = restoreModifiedPropertiesHolderFrom(envelope);
        final T validationPrototype = initValidationPrototype(((Integer) modifiedPropertiesHolder.get(AbstractEntity.ID)).longValue());
        return restUtil.singleJSONRepresentation(apply(modifiedPropertiesHolder, validationPrototype));
    }

    /**
     * Applies the values from <code>dirtyPropertiesHolder</code> into the <code>entity</code>. The values needs to be converted from the client-side component-specific form into
     * the values, which can be set into Java entity's property.
     *
     * @param dirtyPropertiesHolder
     * @param entity
     * @return
     */
    private T apply(final Map<String, Object> dirtyPropertiesHolder, final T entity) {
        for (final Map.Entry<String, Object> nameAndVal : dirtyPropertiesHolder.entrySet()) {
            final String name = nameAndVal.getKey();
            if (!name.equals(AbstractEntity.ID)) {
                entity.set(name, convert(mixin.getEntityType(), name, nameAndVal.getValue()));
            }
        }
        return entity;
    }

    /**
     * Converts <code>reflectedValue</code> (which is a string, number, boolean or null) into a value of appropriate type (the type of actual property).
     *
     * @param type
     * @param propertyName
     * @param reflectedValue
     * @return
     */
    private Object convert(final Class<T> type, final String propertyName, final Object reflectedValue) {
        final Class propertyType = PropertyTypeDeterminator.determinePropertyType(type, propertyName);
        // TODO at this stage only Integer, Money and Entity properties are supported -- implement full support!
        // TODO at this stage only Integer, Money and Entity properties are supported -- implement full support!
        // TODO at this stage only Integer, Money and Entity properties are supported -- implement full support!
        // TODO at this stage only Integer, Money and Entity properties are supported -- implement full support!
        // TODO at this stage only Integer, Money and Entity properties are supported -- implement full support!
        // TODO at this stage only Integer, Money and Entity properties are supported -- implement full support!

        // NOTE: "missing value" for Java entities is also 'null' as for JS entities
        if (EntityUtils.isEntityType(propertyType)) {
            return reflectedValue == null ? null : mixin.getCompanionFinder().find(propertyType).findByKeyAndFetch(/*mixin.getFetchStrategy for [type; propertyName] (), */fetchAll(propertyType), reflectedValue);
        } else if (EntityUtils.isString(propertyType)) {
            return reflectedValue == null ? null : reflectedValue;
        } else if (Integer.class.isAssignableFrom(propertyType)) {
            return reflectedValue == null ? null : reflectedValue;
        } else if (Boolean.class.isAssignableFrom(propertyType)) {
            return reflectedValue == null ? null : reflectedValue;
        } else if (Date.class.isAssignableFrom(propertyType)) {
            return reflectedValue == null ? null : new Date(((Integer) reflectedValue).longValue());
        } else if (BigDecimal.class.isAssignableFrom(propertyType)) {
            return reflectedValue == null ? null : (reflectedValue instanceof Integer ? new BigDecimal((Integer) reflectedValue) : new BigDecimal((Double) reflectedValue));
        } else if (Money.class.isAssignableFrom(propertyType)) {
            if (reflectedValue == null) {
                return null;
            }
            final Map<String, Object> map = (Map<String, Object>) reflectedValue;

            final BigDecimal amount = map.get("amount") instanceof Integer ? new BigDecimal((Integer) map.get("amount")) : new BigDecimal((Double) map.get("amount"));
            final String currencyStr = (String) map.get("currency");
            final Integer taxPercentage = (Integer) map.get("taxPercent");

            return taxPercentage == null ? new Money(amount, Currency.getInstance(currencyStr)) : new Money(amount, taxPercentage, Currency.getInstance(currencyStr));
        } else {
            throw new UnsupportedOperationException(String.format("Unsupported conversion to [%s + %s] from reflected value [%s].", type.getSimpleName(), propertyName, reflectedValue));
        }
    }

    /**
     * Initialises the "validation prototype" entity, which modification will be made upon.
     *
     * @param id
     * @return
     */
    private T initValidationPrototype(final Long id) {
        return mixin.createEntityForRetrieval(id);
    }

    /**
     * Restores the holder of modified properties into the map [propertyName; webEditorSpecificValue].
     *
     * @param envelope
     * @return
     */
    private Map<String, Object> restoreModifiedPropertiesHolderFrom(final Representation envelope) {
        try {
            return (Map<String, Object>) restUtil.restoreJSONMap(envelope);
        } catch (final Exception ex) {
            logger.error("An undesirable error has occured during deserialisation of modified properties holder, which should be validated.", ex);
            throw new IllegalStateException(ex);
        }
    }
}
