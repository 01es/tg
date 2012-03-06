package ua.com.fielden.platform.swing.ei.editors.development;

import java.util.ArrayList;
import java.util.List;

import ua.com.fielden.platform.basic.IValueMatcher;
import ua.com.fielden.platform.criteria.generator.ICriteriaGenerator;
import ua.com.fielden.platform.criteria.generator.impl.CriteriaReflector;
import ua.com.fielden.platform.domaintree.ILocatorManager;
import ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager;
import ua.com.fielden.platform.domaintree.centre.ILocatorDomainTreeManager;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.equery.fetch;
import ua.com.fielden.platform.reflection.AnnotationReflector;
import ua.com.fielden.platform.swing.components.bind.development.BoundedValidationLayer;
import ua.com.fielden.platform.swing.components.bind.development.ComponentFactory;
import ua.com.fielden.platform.swing.components.smart.autocompleter.development.AutocompleterTextFieldLayer;
import ua.com.fielden.platform.swing.ei.editors.LabelAndTooltipExtractor;
import ua.com.fielden.platform.swing.review.annotations.EntityType;
import ua.com.fielden.platform.swing.review.development.EntityQueryCriteria;
import ua.com.fielden.platform.swing.review.report.centre.configuration.LocatorConfigurationModel;
import ua.com.fielden.platform.utils.Pair;

public class EntityPropertyEditorWithLocator extends AbstractEntityPropertyEditor {

    private final BoundedValidationLayer<AutocompleterTextFieldLayer> editor;

    /**
     * Creates standard {@link EntityPropertyEditorWithLocator} editor with entity locator for entity centre.
     * 
     * @return
     */
    public static EntityPropertyEditorWithLocator createEntityPropertyEditorWithLocatorForCentre(final EntityQueryCriteria<ICentreDomainTreeManager, ?, ?> criteria, final String propertyName, final ICriteriaGenerator criteriaGenerator){
	final Pair<Class<?>, String> criteriaParameters = CriteriaReflector.getCriteriaProperty(criteria.getClass(), propertyName);
	final IValueMatcher<?> valueMatcher = criteria.getValueMatcher(propertyName);
	final MetaProperty metaProp = criteria.getProperty(propertyName);

	return createEntityPropertyEditorWithLocator(//
		criteria,//
		propertyName,//
		criteriaParameters.getKey(),//
		criteriaParameters.getValue(),//
		criteria.getDomainTreeManger().getFirstTick(),//
		criteriaGenerator,//
		valueMatcher,//
		LabelAndTooltipExtractor.createCaption(metaProp.getTitle()),//
		LabelAndTooltipExtractor.createTooltip(metaProp.getDesc()));
    }

    public static EntityPropertyEditorWithLocator createEntityPropertyEditorWithLocatorForMaster(final AbstractEntity<?> entity, final String propertyName, final ILocatorManager locatorManager, final ICriteriaGenerator criteriaGenerator, final IValueMatcher<?> valueMatcher, final boolean isSingle){
	//createEditor(entity, propertyName, property.getType(), "", property.getDesc(), entity.getEntityFactory(), entityMasterFactory, vmf, daoFactory, locatorController, locatorRetriever);
	final MetaProperty metaProp = entity.getProperty(propertyName);
	final String toolTip = metaProp.getDesc();

	return createEntityPropertyEditorWithLocator(//
		entity,//
		propertyName,//
		entity.getType(),//
		propertyName,//
		locatorManager, //
		criteriaGenerator,//
		valueMatcher,//
		"",//
		toolTip);
    }

    private static EntityPropertyEditorWithLocator createEntityPropertyEditorWithLocator(final AbstractEntity<?> entity, //
	    final String propertyName, //
	    final Class<?> rootType, //
	    final String locatorName, //
	    final ILocatorManager locatorManager, //
	    final ICriteriaGenerator criteriaGenerator, //
	    final IValueMatcher<?> valueMatcher, //
	    final String caption, //
	    final String toolTip){
	final MetaProperty metaProp = entity.getProperty(propertyName);
	final IsProperty propertyAnnotation = AnnotationReflector.getPropertyAnnotation(IsProperty.class, entity.getType(), propertyName);
	final EntityType entityTypeAnnotation = AnnotationReflector.getPropertyAnnotation(EntityType.class, entity.getType(), propertyName);
	final boolean isSingle = isSingle(entity, propertyName);
	final boolean stringBinding = isStringBinded(entity, propertyName);
	final Class<?> elementType = isSingle ? metaProp.getType() : (stringBinding ? entityTypeAnnotation.value() : propertyAnnotation.value());
	if(!AbstractEntity.class.isAssignableFrom(elementType)){
	    throw new IllegalArgumentException("The property: " + propertyName + " of " + entity.getType().getSimpleName() + " type, can not be bind to the autocompleter!");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	final LocatorConfigurationModel locatorConfigurationModel = new LocatorConfigurationModel(
		elementType,//
		rootType,//
		locatorName,//
		locatorManager,//
		entity.getEntityFactory(),//
		criteriaGenerator);

	return new EntityPropertyEditorWithLocator(entity, propertyName, locatorConfigurationModel, elementType, valueMatcher, caption, toolTip);
    }

    private static boolean isSingle(final AbstractEntity<?> entity, final String propertyName){
	final MetaProperty metaProp = entity.getProperty(propertyName);
	return !metaProp.isCollectional();
    }

    private static boolean isStringBinded(final AbstractEntity<?> entity, final String propertyName){
	final IsProperty propertyAnnotation = AnnotationReflector.getPropertyAnnotation(IsProperty.class, entity.getType(), propertyName);
	final boolean isSingle = isSingle(entity, propertyName);
	return isSingle ? false : String.class.isAssignableFrom(propertyAnnotation.value());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public EntityPropertyEditorWithLocator(final AbstractEntity<?> entity, final String propertyName, final LocatorConfigurationModel locatorConfigurationModel, final Class<?> elementType, final IValueMatcher<?> valueMatcher, final String caption, final String toolTip) {
	super(entity, propertyName, new EntityLocatorValueMatcher(valueMatcher, locatorConfigurationModel.locatorManager, locatorConfigurationModel.rootType, locatorConfigurationModel.name));
	getValueMatcher().setBindedEntity(entity);
	editor = createEditorWithLocator(entity, propertyName, locatorConfigurationModel, elementType,//
		caption, toolTip, isSingle(entity, propertyName), isStringBinded(entity, propertyName));
    }

    @Override
    public BoundedValidationLayer<AutocompleterTextFieldLayer> getEditor() {
	return editor;
    }

    @Override
    public EntityLocatorValueMatcher getValueMatcher() {
	return (EntityLocatorValueMatcher)super.getValueMatcher();
    }

    @Override
    public void bind(final AbstractEntity<?> entity) {
	super.bind(entity);
	getValueMatcher().setBindedEntity(entity);
    }

    public void highlightFirstHintValue(final boolean highlight) {
	getEditor().getView().highlightFirstHintValue(highlight);
    }

    public void highlightSecondHintValue(final boolean highlight) {
	getEditor().getView().highlightSecondHintValue(highlight);
    }

    private BoundedValidationLayer<AutocompleterTextFieldLayer> createEditorWithLocator(//
	    final AbstractEntity bindingEntity,//
	    final String bindingPropertyName,//
	    final LocatorConfigurationModel locatorConfigurationModel, //
	    final Class entityType,//
	    final String caption,//
	    final String toolTip,//
	    final boolean isSingle,//
	    final boolean stringBinding//
	    ){
	if (!AbstractEntity.class.isAssignableFrom(entityType)) {
	    throw new RuntimeException("Could not determined an editor for property " + getPropertyName() + " of type " + entityType + ".");
	}
	return ComponentFactory.createOnFocusLostAutocompleterWithEntityLocator(bindingEntity, bindingPropertyName, locatorConfigurationModel, entityType, getValueMatcher(), "key", "desc", caption, isSingle ? null : ",", toolTip, stringBinding);
    }

    public static class EntityLocatorValueMatcher<T extends AbstractEntity, R extends AbstractEntity> implements IValueMatcher<T>{

	private final IValueMatcher<T> autocompleterValueMatcher;

	private final ILocatorManager locatorManager;

	//private final Class<T> entityType;

	private final Class<R> rootType;

	private final String propertyName;

	private AbstractEntity<?> bindedEntity;

	public EntityLocatorValueMatcher(//
		final IValueMatcher<T> autocompleterValueMatcher,//
		final ILocatorManager locatorManager,//
		//	final Class<T> entityType,//
		final Class<R> rootType,//
		final String propertyName){
	    this.autocompleterValueMatcher = autocompleterValueMatcher;
	    this.locatorManager = locatorManager;
	    //  this.entityType = entityType;
	    this.rootType = rootType;
	    this.propertyName = propertyName;
	}

	public void setBindedEntity(final AbstractEntity<?> bindedEntity) {
	    this.bindedEntity = bindedEntity;
	}

	public AbstractEntity<?> getBindedEntity() {
	    return bindedEntity;
	}

	@Override
	public fetch<?> getFetchModel() {
	    return autocompleterValueMatcher.getFetchModel();
	}

	@Override
	public void setFetchModel(final fetch<?> fetchModel) {
	    autocompleterValueMatcher.setFetchModel(fetchModel);
	}

	@Override
	public List<T> findMatches(final String value) {
	    return findMatches(value, null);
	}

	@Override
	public List<T> findMatchesWithModel(final String value) {
	    return findMatches(value, getFetchModel());
	}

	private List<T> findMatches(final String value, final fetch<?> fetchModel) {
	    final ILocatorDomainTreeManager ldtm = ldtm();
	    if(ldtm.isUseForAutocompletion()){
		//TODO implement this method when the ldtm is used for autocompleter.
		return new ArrayList<T>();
	    }else{
		if(fetchModel == null){
		    return autocompleterValueMatcher.findMatches(value);
		} else {
		    return autocompleterValueMatcher.findMatchesWithModel(value);
		}
	    }

	}

	private ILocatorDomainTreeManager ldtm(){
	    ILocatorDomainTreeManager ldtm = locatorManager.getLocatorManager(rootType, propertyName);
	    if(ldtm == null){
		locatorManager.initLocatorManagerByDefault(rootType, propertyName);
		ldtm = locatorManager.getLocatorManager(rootType, propertyName);
		if(ldtm == null){
		    throw new IllegalStateException("The locator manager must be initialised");
		}
	    }
	    return ldtm;
	}

	@Override
	public Integer getPageSize() {
	    return autocompleterValueMatcher.getPageSize();
	}
    }

}