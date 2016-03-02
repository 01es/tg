package ua.com.fielden.platform.serialisation.api.impl;

import static ua.com.fielden.platform.reflection.ClassesRetriever.findClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

import ua.com.fielden.platform.basic.config.IApplicationDomainProvider;
import ua.com.fielden.platform.basic.config.IApplicationSettings;
import ua.com.fielden.platform.security.SecurityRoleAssociationBatchAction;
import ua.com.fielden.platform.security.UserAndRoleAssociationBatchAction;
import ua.com.fielden.platform.security.user.UserRolesUpdater;
import ua.com.fielden.platform.security.user.UserRoleTokensUpdater;
import ua.com.fielden.platform.serialisation.api.ISerialisationClassProvider;
import ua.com.fielden.platform.serialisation.jackson.entities.EmptyEntity;
import ua.com.fielden.platform.serialisation.jackson.entities.Entity1WithEntity2;
import ua.com.fielden.platform.serialisation.jackson.entities.Entity2WithEntity1;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithBigDecimal;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithBoolean;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithColour;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithCompositeKey;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithDate;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithDefiner;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithInteger;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithListOfEntities;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithMapOfEntities;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithMoney;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithOtherEntity;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithPolymorphicAEProp;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithPolymorphicProp;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithSameEntity;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithSetOfEntities;
import ua.com.fielden.platform.serialisation.jackson.entities.EntityWithString;
import ua.com.fielden.platform.serialisation.jackson.entities.OtherEntity;
import ua.com.fielden.platform.serialisation.jackson.entities.SubBaseEntity1;
import ua.com.fielden.platform.serialisation.jackson.entities.SubBaseEntity2;

/**
 * Default implementation of {@link ISerialisationClassProvider}, which relies on the application settings to provide the location of classes to be used in serialisation.
 *
 * @author TG Team
 *
 */
public class DefaultSerialisationClassProvider implements ISerialisationClassProvider {

    protected final List<Class<?>> types = new ArrayList<Class<?>>();

    private final static List<Class<?>> utilityGeneratedClasses = new ArrayList<Class<?>>();
    static {
        //Generated by tg kryo utility
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.LocatorManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.CalculatedProperty"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.master.impl.MasterDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeManagerAndEnhancer$DomainTreeEnhancerWithPropertiesPopulation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.DomainTreeEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.ILocatorDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IAbstractAnalysisDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IAnalysisDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.ILifecycleDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IPivotDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.AbstractAnalysisDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.AnalysisDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.LifecycleDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.PivotDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeManagerAndEnhancer$CentreDomainTreeRepresentationAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.LocatorDomainTreeManagerAndEnhancer$LocatorDomainTreeRepresentationAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.LocatorDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeManagerAndEnhancer$DomainTreeRepresentationAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.IDomainTreeRepresentationWithMutability"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.IDomainTreeManager$IDomainTreeManagerAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager$ICentreDomainTreeManagerAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.ILocatorDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.ILocatorDomainTreeManager$ILocatorDomainTreeManagerAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IAbstractAnalysisDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IAnalysisDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.ILifecycleDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IPivotDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.AbstractAnalysisDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.AnalysisDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.LifecycleDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.PivotDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeManagerAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.LocatorDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.LocatorDomainTreeManagerAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeManagerAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeRepresentation$IAddToCriteriaTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeRepresentation$IAddToResultTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IAbstractAnalysisDomainTreeRepresentation$IAbstractAnalysisAddToAggregationTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IAbstractAnalysisDomainTreeRepresentation$IAbstractAnalysisAddToDistributionTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IAnalysisDomainTreeRepresentation$IAnalysisAddToAggregationTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IAnalysisDomainTreeRepresentation$IAnalysisAddToDistributionTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.ILifecycleDomainTreeRepresentation$ILifecycleAddToCategoriesTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.ILifecycleDomainTreeRepresentation$ILifecycleAddToDistributionTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IPivotDomainTreeRepresentation$IPivotAddToAggregationTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IPivotDomainTreeRepresentation$IPivotAddToDistributionTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.AbstractAnalysisDomainTreeRepresentation$AbstractAnalysisAddToAggregationTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.AbstractAnalysisDomainTreeRepresentation$AbstractAnalysisAddToDistributionTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.AnalysisDomainTreeRepresentation$AnalysisAddToAggregationTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.AnalysisDomainTreeRepresentation$AnalysisAddToDistributionTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.LifecycleDomainTreeRepresentation$LifecycleAddToCategoriesTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.LifecycleDomainTreeRepresentation$LifecycleAddToDistributionTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.PivotDomainTreeRepresentation$PivotAddToAggregationTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.PivotDomainTreeRepresentation$PivotAddToDistributionTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeManagerAndEnhancer$CentreDomainTreeRepresentationAndEnhancer$AddToCriteriaTickRepresentationAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeManagerAndEnhancer$CentreDomainTreeRepresentationAndEnhancer$AddToResultTickRepresentationAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeRepresentation$AddToCriteriaTick"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeRepresentation$AddToResultSetTick"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeManager$ITickRepresentationWithMutability"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeManagerAndEnhancer$DomainTreeRepresentationAndEnhancer$TickRepresentationAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeRepresentation$AbstractTickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager$IAddToCriteriaTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager$IAddToResultTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IAbstractAnalysisDomainTreeManager$IAbstractAnalysisAddToAggregationTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IAbstractAnalysisDomainTreeManager$IAbstractAnalysisAddToDistributionTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IAnalysisDomainTreeManager$IAnalysisAddToAggregationTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IAnalysisDomainTreeManager$IAnalysisAddToDistributionTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.ILifecycleDomainTreeManager$ILifecycleAddToCategoriesTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.ILifecycleDomainTreeManager$ILifecycleAddToDistributionTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IPivotDomainTreeManager$IPivotAddToAggregationTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IPivotDomainTreeManager$IPivotAddToDistributionTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.AbstractAnalysisDomainTreeManager$AbstractAnalysisAddToAggregationTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.AbstractAnalysisDomainTreeManager$AbstractAnalysisAddToDistributionTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.AnalysisDomainTreeManager$AnalysisAddToAggregationTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.AnalysisDomainTreeManager$AnalysisAddToDistributionTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.LifecycleDomainTreeManager$LifecycleAddToCategoriesTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.LifecycleDomainTreeManager$LifecycleAddToDistributionTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.PivotDomainTreeManager$PivotAddToAggregationTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.PivotDomainTreeManager$PivotAddToDistributionTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeManager$AddToCriteriaTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeManager$AddToResultTickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeManagerAndEnhancer$AddToCriteriaTickManagerAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeManagerAndEnhancer$AddToResultTickManagerAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.impl.LocatorDomainTreeManager$AddToCriteriaTickManagerForLocator"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeManager$ITickManagerWithMutability"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeManager$TickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeManagerAndEnhancer$TickManagerAndEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.ILocatorDomainTreeManager$SearchBy"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeRepresentation$ListenedArrayList"));
        utilityGeneratedClasses.add(findClass("java.util.LinkedHashMap"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.EnhancementSet"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.EnhancementLinkedRootsSet"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.EnhancementRootsMap"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.EnhancementPropertiesMap"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.impl.DomainTreeEnhancer$ByteArray"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.IOrderingRepresentation$Ordering"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.Function"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.serialisation.kryo.dummies.Dummy1"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.serialisation.kryo.dummies.Dummy2"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.serialisation.kryo.dummies.Dummy3"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.serialisation.kryo.dummies.Dummy4"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.serialisation.kryo.dummies.Dummy5"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.serialisation.kryo.dummies.Dummy6"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.serialisation.kryo.dummies.Dummy7"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.serialisation.kryo.dummies.Dummy8"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.serialisation.kryo.dummies.Dummy9"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.serialisation.kryo.dummies.Dummy10"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.serialisation.kryo.dummies.Dummy11"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.ICalculatedProperty$CalculatedPropertyCategory"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.ICalculatedProperty$CalculatedPropertyAttribute"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.ICalculatedProperty"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.master.IMasterDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.IDomainTreeEnhancer"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.IDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.IDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.IDomainTreeRepresentation$ITickRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.IDomainTreeManager$ITickManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IMultipleDecDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.MultipleDecDomainTreeRepresentation"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.IMultipleDecDomainTreeManager"));
        utilityGeneratedClasses.add(findClass("ua.com.fielden.platform.domaintree.centre.analyses.impl.MultipleDecDomainTreeManager"));
    }

    @Inject
    public DefaultSerialisationClassProvider(final IApplicationSettings settings, final IApplicationDomainProvider applicationDomain) throws Exception {
        types.addAll(utilityGeneratedClasses());
        types.add(Exception.class);
        types.add(StackTraceElement[].class);
        types.addAll(typesForSerialisationTesting());
        types.addAll(applicationDomain.entityTypes());
        types.add(UserAndRoleAssociationBatchAction.class);
        types.add(SecurityRoleAssociationBatchAction.class);
        types.add(UserRolesUpdater.class);
        types.add(UserRoleTokensUpdater.class);
    }

    private List<Class<?>> typesForSerialisationTesting() {
        return Arrays.asList(
                EmptyEntity.class,
                EntityWithBigDecimal.class,
                EntityWithInteger.class,
                EntityWithString.class,
                EntityWithBoolean.class,
                EntityWithDate.class,
                EntityWithOtherEntity.class,
                EntityWithSameEntity.class,
                OtherEntity.class,
                Entity1WithEntity2.class,
                Entity2WithEntity1.class,
                EntityWithSetOfEntities.class,
                EntityWithListOfEntities.class,
                EntityWithMapOfEntities.class,
                EntityWithPolymorphicProp.class,
                EntityWithDefiner.class,
                // BaseEntity.class,
                SubBaseEntity1.class,
                SubBaseEntity2.class,
                EntityWithCompositeKey.class,
                EntityWithMoney.class,
                EntityWithPolymorphicAEProp.class,
                EntityWithColour.class
                               
                );
    }

    @Override
    public List<Class<?>> classes() {
        return types;
    }

    /**
     * Returns utility generated list of classes.
     *
     * @return
     */
    public static List<Class<?>> utilityGeneratedClasses() {
        return Collections.unmodifiableList(utilityGeneratedClasses);
    }

}
