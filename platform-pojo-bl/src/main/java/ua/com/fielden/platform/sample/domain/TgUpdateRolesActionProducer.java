package ua.com.fielden.platform.sample.domain;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import ua.com.fielden.platform.dao.AbstractFunctionalEntityProducerForCollectionModification;
import ua.com.fielden.platform.dao.IEntityProducer;
import ua.com.fielden.platform.dao.IUserRoleDao;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.factory.ICompanionObjectFinder;
import ua.com.fielden.platform.entity.query.fluent.fetch;
import ua.com.fielden.platform.security.user.IUser;
import ua.com.fielden.platform.security.user.User;
import ua.com.fielden.platform.security.user.UserAndRoleAssociation;
import ua.com.fielden.platform.security.user.UserRole;
import ua.com.fielden.platform.web.centre.CentreContext;

/**
 * A producer for new instances of entity {@link TgUpdateRolesAction}.
 *
 * @author TG Team
 *
 */
public class TgUpdateRolesActionProducer extends AbstractFunctionalEntityProducerForCollectionModification<User, TgUpdateRolesAction> implements IEntityProducer<TgUpdateRolesAction> {
    private final Logger logger = Logger.getLogger(getClass());
    private final IUserRoleDao coUserRole;
    private final IUser coUser;
    
    @Inject
    public TgUpdateRolesActionProducer(final EntityFactory factory, final ICompanionObjectFinder companionFinder, final IUserRoleDao coUserRole, final IUser coUser) {
        super(factory, TgUpdateRolesAction.class, companionFinder);
        this.coUserRole = coUserRole;
        this.coUser = coUser;
    }
    
    @Override
    protected TgUpdateRolesAction provideCurrentlyAssociatedValues(final TgUpdateRolesAction entity, final User masterEntity) {
        final List<UserRole> allAvailableRoles = coUserRole.findAll();
        final Set<UserRole> roles = new LinkedHashSet<>(allAvailableRoles);
        entity.setRoles(roles);
        
        final Set<Long> chosenRoleIds = new LinkedHashSet<>();
        for (final UserAndRoleAssociation association: masterEntity.getRoles()) {
            chosenRoleIds.add(association.getUserRole().getId());
        }
        entity.setChosenIds(chosenRoleIds);
        return entity;
    }
    
    @Override
    protected AbstractEntity<?> getMasterEntityFromContext(final CentreContext<?, ?> context) {
        // this producer is suitable for property actions on User master and for actions on User centre
        return context.getMasterEntity() == null ? context.getCurrEntity() : context.getMasterEntity();
    }

    @Override
    protected fetch<User> fetchModelForMasterEntity() {
        return coUser.getFetchProvider().fetchModel();
    }
}