package ua.com.fielden.platform.sample.domain;

import ua.com.fielden.platform.dao.CommonEntityDao;
import ua.com.fielden.platform.entity.annotation.EntityType;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.sample.domain.mixin.TgCategoryMixin;

import com.google.inject.Inject;

/**
 * DAO implementation for companion object {@link ITgCategory}.
 *
 * @author Developers
 *
 */
@EntityType(TgCategory.class)
public class TgCategoryDao extends CommonEntityDao<TgCategory> implements ITgCategory {

    private final TgCategoryMixin mixin;

    @Inject
    public TgCategoryDao(final IFilter filter) {
        super(filter);

        mixin = new TgCategoryMixin(this);
    }

    @Override
    public void delete(final TgCategory entity) {
        defaultDelete(entity);
    }

}