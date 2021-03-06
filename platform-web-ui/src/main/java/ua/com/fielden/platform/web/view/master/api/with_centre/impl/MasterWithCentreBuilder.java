package ua.com.fielden.platform.web.view.master.api.with_centre.impl;

import ua.com.fielden.platform.entity.AbstractFunctionalEntityWithCentreContext;
import ua.com.fielden.platform.web.centre.EntityCentre;
import ua.com.fielden.platform.web.view.master.api.IMaster;
import ua.com.fielden.platform.web.view.master.api.IMasterWithCentreBuilder;
import ua.com.fielden.platform.web.view.master.api.helpers.IComplete;
import ua.com.fielden.platform.web.view.master.api.with_centre.IMasterWithCentre0;

public class MasterWithCentreBuilder<T extends AbstractFunctionalEntityWithCentreContext<?>> implements IMasterWithCentreBuilder<T>, IMasterWithCentre0<T>, IComplete<T> {

    private Class<T> type;
    private EntityCentre<?> entityCentre;
    private boolean saveOnActivate = false;

    @Override
    public IMasterWithCentre0<T> forEntityWithSaveOnActivate(final Class<T> type) {
        this.type = type;
        this.saveOnActivate = true;
        return this;
    }

    @Override
    public IComplete<T> withCentre(final EntityCentre<?> entityCentre) {
        this.entityCentre = entityCentre;
        return this;
    }

    @Override
    public IMaster<T> done() {
        return new MasterWithCentre<T>(type, saveOnActivate, entityCentre);
    }

}
