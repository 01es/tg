package ua.com.fielden.platform.swing.review.report.analysis.pivot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.dao.QueryExecutionModel;
import ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager.ICentreDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.domaintree.centre.IOrderingRepresentation.Ordering;
import ua.com.fielden.platform.domaintree.centre.analyses.IPivotDomainTreeManager;
import ua.com.fielden.platform.domaintree.centre.analyses.IPivotDomainTreeManager.IPivotAddToAggregationTickManager;
import ua.com.fielden.platform.domaintree.centre.analyses.IPivotDomainTreeManager.IPivotAddToDistributionTickManager;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.reflection.PropertyTypeDeterminator;
import ua.com.fielden.platform.reflection.development.EntityDescriptor;
import ua.com.fielden.platform.report.query.generation.AnalysisResultClassBundle;
import ua.com.fielden.platform.report.query.generation.IReportQueryGeneration;
import ua.com.fielden.platform.report.query.generation.PivotAnalysisQueryGgenerator;
import ua.com.fielden.platform.swing.checkboxlist.ListCheckingEvent;
import ua.com.fielden.platform.swing.checkboxlist.ListCheckingListener;
import ua.com.fielden.platform.swing.checkboxlist.ListCheckingModel;
import ua.com.fielden.platform.swing.checkboxlist.ListSortingModel;
import ua.com.fielden.platform.swing.checkboxlist.SorterChangedEvent;
import ua.com.fielden.platform.swing.checkboxlist.SorterEventListener;
import ua.com.fielden.platform.swing.review.development.EntityQueryCriteria;
import ua.com.fielden.platform.swing.review.report.analysis.view.AbstractAnalysisReviewModel;
import ua.com.fielden.platform.swing.review.report.analysis.view.DomainTreeListCheckingModel;
import ua.com.fielden.platform.swing.review.report.analysis.view.DomainTreeListSortingModel;
import ua.com.fielden.platform.swing.utils.SwingUtilitiesEx;
import ua.com.fielden.platform.utils.Pair;

public class PivotAnalysisModel<T extends AbstractEntity<?>> extends AbstractAnalysisReviewModel<T, ICentreDomainTreeManagerAndEnhancer, IPivotDomainTreeManager, Void> {

    private final PivotTreeTableModelEx pivotModel;
    private final ListCheckingModel<String> distributionCheckingModel;
    private final ListCheckingModel<String> aggregationCheckingModel;
    private final ListSortingModel<String> aggregationSortingModel;

    public PivotAnalysisModel(final EntityQueryCriteria<ICentreDomainTreeManagerAndEnhancer, T, IEntityDao<T>> criteria, final IPivotDomainTreeManager adtme) {
	super(criteria, adtme);
	final Class<T> root = getCriteria().getEntityClass();
	final IPivotAddToDistributionTickManager firstTick = adtme().getFirstTick();
	final IPivotAddToAggregationTickManager secondTick = adtme().getSecondTick();

	pivotModel = new PivotTreeTableModelEx();
	pivotModel.addTableHeaderChangedListener(new PivotTableHeaderChanged() {

	    @Override
	    public void tableHeaderChanged(final PivotTableHeaderChangedEvent event) {
		//This is stub implementation.
	    }

	    @Override
	    public void columnOrderChanged(final PivotColumnOrderChangedEvent event) {
		if (pivotModel.aggregationProperties.contains(event.getProperty())) {
		    final List<String> usedProperties = secondTick.usedProperties(root);
		    reorderList(usedProperties, pivotModel.aggregationProperties, event.getProperty(), false);
		    reorderList(usedProperties, pivotModel.aggregationProperties, event.getProperty(), true);
		}
		if (pivotModel.visibleAggregationProperties.contains(event.getProperty())) {
		    reorderList(pivotModel.aggregationProperties, pivotModel.visibleAggregationProperties, event.getProperty(), false);
		    reorderList(pivotModel.aggregationProperties, pivotModel.visibleAggregationProperties, event.getProperty(), true);
		}
	    }
	});

	distributionCheckingModel = new DomainTreeListCheckingModel<T>(root, firstTick);
	distributionCheckingModel.addListCheckingListener(new ListCheckingListener<String>() {

	    @Override
	    public void valueChanged(final ListCheckingEvent<String> e) {
		pivotModel.fireTableHierarchyChangedEvent(new PivotHierarchyChangedEvent(pivotModel, e.getItem(), e.getNewCheck()));
	    }
	});

	aggregationCheckingModel = new DomainTreeListCheckingModel<T>(root, secondTick);
	aggregationCheckingModel.addListCheckingListener(new ListCheckingListener<String>() {

	    @Override
	    public void valueChanged(final ListCheckingEvent<String> e) {
		if (pivotModel.aggregationProperties.contains(e.getItem())) {
		    reorderList(pivotModel.aggregationProperties, pivotModel.visibleAggregationProperties, e.getItem(), e.getNewCheck());
		}
		pivotModel.fireTableHeaderChangedEvent(new PivotTableHeaderChangedEvent(pivotModel, e.getItem(), e.getNewCheck()));
	    }
	});
	aggregationSortingModel = new DomainTreeListSortingModel<T>(root, secondTick, adtme().getRepresentation().getSecondTick());
	aggregationSortingModel.addSorterEventListener(new SorterEventListener<String>() {

	    @SuppressWarnings("unchecked")
	    @Override
	    public void valueChanged(final SorterChangedEvent<String> e) {
		if (pivotModel.getRoot() != null) {
		    ((PivotTreeTableModelEx.PivotTreeTableNodeEx) pivotModel.getRoot()).sort();
		    pivotModel.fireSorterChageEvent(new PivotSorterChangeEvent(pivotModel, e.getNewSortObjectes()));
		}
	    }
	});
    }

    public PivotTreeTableModel getPivotModel() {
        return pivotModel;
    }

    public ListCheckingModel<String> getDistributionCheckingModel() {
	return distributionCheckingModel;
    }

    public ListCheckingModel<String> getAggregationCheckingModel() {
	return aggregationCheckingModel;
    }

    public ListSortingModel<String> getAggregationSortingModel() {
	return aggregationSortingModel;
    }

    @Override
    protected Void executeAnalysisQuery() {
	final Class<T> root = getCriteria().getEntityClass();

	final IReportQueryGeneration<T> pivotQueryGenerator = new PivotAnalysisQueryGgenerator<>(root,//
		getCriteria().getCentreDomainTreeManagerAndEnhnacerCopy(), //
		adtme());

	final AnalysisResultClassBundle<T> classBundle = pivotQueryGenerator.generateQueryModel();
	final List<QueryExecutionModel<T, EntityResultQueryModel<T>>> queryModelList = classBundle.getQueries();

	final List<String> distributionProperties = adtme().getFirstTick().usedProperties(root);

	final Map<String, List<T>> resultMap = new HashMap<String, List<T>>();
	resultMap.put("Grand total", getGroupList(classBundle, 0));
	for(int index = 0; index < distributionProperties.size(); index++){
	    resultMap.put(distributionProperties.get(index), getGroupList(classBundle, index+1));
	}
	pivotModel.loadData(resultMap, distributionProperties, adtme().getSecondTick().usedProperties(root));
	return null;
    }

    @Override
    protected Result canLoadData() {
	final Result result = getCriteria().isValid();
	if(!result.isSuccessful()){
	    return result;
	}
	final Class<T> entityClass = getCriteria().getEntityClass();
	if(adtme().getFirstTick().usedProperties(entityClass).isEmpty() && adtme().getSecondTick().usedProperties(entityClass).isEmpty()){
	    return new Result(new IllegalStateException("Please choose distribution or aggregation properties"));
	}
	return Result.successful(this);
    }

    @Override
    protected void exportData(final String fileName) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    protected String[] getExportFileExtensions() {
        return new String[] {getDefaultExportFileExtension()};
    }

    @Override
    protected String getDefaultExportFileExtension() {
        return "xls";
    }

    /**
     * (Adds/removes) specified item (in to/from) the destination list in order that elements of the destination corresponds the order of source list.
     *
     * @param sourceList
     * @param destinationList
     * @param item
     * @param newCheck - determines whether to add or remove item from/to destination list.
     */
    private void reorderList(final List<String> sourceList, final List<String> destinationList, final String item, final Boolean newCheck) {
	if (!newCheck) {
	    destinationList.remove(item);
	} else {
	    int visibleIndex = 0;
	    for(int index = 0; index < sourceList.size(); index++){
		if(visibleIndex >= destinationList.size()){
		    destinationList.add(item);
		    return;
		}else{
		    if(sourceList.get(index).equals(destinationList.get(visibleIndex))){
			visibleIndex++;
		    }else if(sourceList.get(index).equals(item)){
			destinationList.add(visibleIndex, item);
			return;
		    }
		}
	    }
	}
    }

    /**
     * Returns the page for the pivot analysis query grouped by specified list of properties.
     *
     * @param groups
     * @return
     */
    private List<T> getGroupList(final AnalysisResultClassBundle<T> classBundle, final int index){
	return getCriteria().run(classBundle.getQueries().get(index), classBundle.getGeneratedClass(), classBundle.getGeneratedClassRepresentation());
    }

    private class PivotTreeTableModelEx extends PivotTreeTableModel {

	private static final char RIGHT_ARROW = '\u2192';

	private final List<String> distributionProperties = new ArrayList<>();
	private final List<String> aggregationProperties = new ArrayList<>();

	private final List<String> visibleAggregationProperties = new ArrayList<>();

	private EntityDescriptor aggregationDescriptor, distributionDescriptor;

	private final Comparator<MutableTreeTableNode> sorter = new AggregationSorter();

	@Override
	public int getColumnCount() {
	    if(!visibleAggregationProperties.isEmpty()){
		return 1 + visibleAggregationProperties.size();
	    }
	    return distributionProperties.isEmpty() ? 0 : 1;
	}

	@Override
	public Class<?> getColumnClass(final int column) {
	    if (column == 0) {
		return String.class;
	    }
	    final Class<?> managedType = getCriteria().getManagedType();
	    final String property = visibleAggregationProperties.get(column - 1);
	    return PropertyTypeDeterminator.determineClass(managedType, property, true, true);
	}

	@Override
	public String getColumnName(final int column) {
	    if (column == 0) {
		String name = "";
		for (final String columnEntry : distributionProperties) {
		    name += RIGHT_ARROW + "(" + distributionDescriptor.getTitle(columnEntry) + ")";
		}
		return name.isEmpty() ? "<html><i>(Distribution properties)</i></html>" : name.substring(1);
	    }
	    final String property = aggregationDescriptor.getTitle(visibleAggregationProperties.get(column - 1));
	    return property;
	}

	@Override
	void setColumnWidth(final int column, final int width) {
	    final Class<T> root = getCriteria().getEntityClass();
	    final IPivotAddToDistributionTickManager firstTick = adtme().getFirstTick();
	    final IPivotAddToAggregationTickManager secondTick = adtme().getSecondTick();
	    if(column == 0 && !distributionProperties.isEmpty()){
		firstTick.setWidth(root, distributionProperties.get(0), width);
	    } else if(column > 0) {
		secondTick.setWidth(root, visibleAggregationProperties.get(column-1), width);
	    }
	}

	@Override
	int getColumnWidth(final int column) {
	    final Class<T> root = getCriteria().getEntityClass();
	    final IPivotAddToDistributionTickManager firstTick = adtme().getFirstTick();
	    final IPivotAddToAggregationTickManager secondTick = adtme().getSecondTick();
	    if(column == 0 && !distributionProperties.isEmpty()){
		return firstTick.getWidth(root, distributionProperties.get(0));
	    } else if(column > 0) {
		return secondTick.getWidth(root, visibleAggregationProperties.get(column-1));
	    }
	    return 0;
	}

	@Override
	String getColumnTooltipAt(final int column) {
	    return getColumnName(column);
	}

	@Override
	List<String> categoryProperties() {
	    return Collections.unmodifiableList(distributionProperties);
	}

	@Override
	List<String> aggregatedProperties() {
	    return Collections.unmodifiableList(aggregationProperties);
	}


	@SuppressWarnings("unchecked")
	private final void loadData(final Map<String, List<T>> loadedData, final List<String> distributionProperties, final List<String> aggregationProperties){

	    //Loading the alias maps for the aggregation and category properties.
	    if(distributionProperties == null || aggregationProperties == null){
		return;
	    }
	    this.distributionProperties.clear();
	    this.distributionProperties.addAll(distributionProperties);
	    this.aggregationProperties.clear();
	    this.aggregationProperties.addAll(aggregationProperties);
	    this.visibleAggregationProperties.clear();
	    this.visibleAggregationProperties.addAll(aggregationProperties);

	    final Class<T> entityType = getCriteria().getEntityClass();
	    final Class<T> managedType = getCriteria().getManagedType();

	    this.distributionDescriptor = new EntityDescriptor(managedType, adtme().getFirstTick().checkedProperties(entityType));
	    this.aggregationDescriptor = new EntityDescriptor(managedType, adtme().getSecondTick().checkedProperties(entityType));

	    final PivotTreeTableNodeEx root = new PivotTreeTableNodeEx("root", null);
	    final PivotTreeTableNodeEx grand = new PivotTreeTableNodeEx("Grand total", loadedData.get("Grand total").get(0));

	    final Map<Object, Pair<PivotTreeTableNodeEx,Map<?, ?>>> nodes = new HashMap<Object, Pair<PivotTreeTableNodeEx,Map<?, ?>>>();
	    for(int categoryIndex = 0; categoryIndex < distributionProperties.size(); categoryIndex++){
		final List<T> listToLoad = loadedData.get(distributionProperties.get(categoryIndex));
		for(final T data : listToLoad){
		    Map<Object, Pair<PivotTreeTableNodeEx,Map<?, ?>>> currentLevel = nodes;
		    PivotTreeTableNodeEx currentNode = grand;
		    for(int dataIndex = 0; dataIndex < categoryIndex; dataIndex++){
			final Object key = data.get(distributionProperties.get(dataIndex));
			final Pair<PivotTreeTableNodeEx,Map<?, ?>> nodePair = currentLevel.get(key);
			if(nodePair == null){
			    throw new IllegalStateException("The node with " + key + " wasn't found!");
			}
			currentLevel = (Map<Object, Pair<PivotTreeTableNodeEx,Map<?, ?>>>)nodePair.getValue();
			currentNode = nodePair.getKey();
		    }
		    final Object currentKey = data.get(distributionProperties.get(categoryIndex));
		    final PivotTreeTableNodeEx child = new PivotTreeTableNodeEx(currentKey, data);
		    final Pair<PivotTreeTableNodeEx, Map<?, ?>> childPair = new Pair<PivotTreeTableNodeEx, Map<?, ?>>(child, new HashMap<Object, Pair<PivotTreeTableNodeEx, Map<?, ?>>>());
		    currentLevel.put(currentKey, childPair);
		    currentNode.add(child);
		}
	    }
	    root.add(grand);
	    SwingUtilitiesEx.invokeLater(new Runnable() {

		@Override
		public void run() {
		    setRoot(root);
		    firePivotDataLoaded(new PivotDataLoadedEvent(PivotTreeTableModelEx.this));
		}
	    });
	}

	private class PivotTreeTableNodeEx extends PivotTreeTableNode {

	    //private final static String NULL_USER_OBJECT = "UNKNOWN";

	    private final T aggregatedData;

	    public PivotTreeTableNodeEx(final Object userObject, final T aggregatedData){
		super(userObject);
		this.aggregatedData = aggregatedData;
	    }

	    @Override
	    public int getColumnCount() {
		if(!visibleAggregationProperties.isEmpty()){
		    return 1 + visibleAggregationProperties.size();
		}
		return distributionProperties.isEmpty() ? 0 : 1;
	    }

	    @Override
	    public Object getValueAt(final int column) {
		if (column == 0) {
		    if (getUserObject() instanceof AbstractEntity) {
			final AbstractEntity<?> entity = (AbstractEntity<?>) getUserObject();
			return entity.getKey().toString() + (StringUtils.isEmpty(entity.getDesc()) ? "" : " - " + entity.getDesc());
		    }
		    return getUserObject();
		}
		final String property = visibleAggregationProperties.get(column - 1);
		return aggregatedData.get(property);
	    }

	    /**
	     * Sort children of this node, using comparator defined in the model.
	     *
	     * @param treeTableSorter
	     */
	    @SuppressWarnings("unchecked")
	    private void sort() {
		for (final MutableTreeTableNode child : children) {
		    ((PivotTreeTableNodeEx) child).sort();
		}
		Collections.sort(children, sorter);
	    }

	    @SuppressWarnings("rawtypes")
	    @Override
	    public String getTooltipAt(final int column) {
		if (column == 0) {
		    if (getUserObject() instanceof AbstractEntity) {
			return ((AbstractEntity) getUserObject()).getDesc();
		    }
		    return getUserObject().toString();
		}
		final Object value = getValueAt(column);
		return value != null ? value.toString() : null;
	    }
	}

	private class AggregationSorter implements Comparator<MutableTreeTableNode> {

	    @SuppressWarnings("rawtypes")
	    @Override
	    public int compare(final MutableTreeTableNode o1, final MutableTreeTableNode o2) {

		final Class<T> root = getCriteria().getEntityClass();
		final IPivotAddToAggregationTickManager secondTick = adtme().getSecondTick();

		final List<Pair<String, Ordering>> sortObjects = secondTick.orderedProperties(root);
		if (sortObjects == null || sortObjects.isEmpty()) {
		    return defaultCompare(o1, o2);
		}
		final List<Pair<Integer, Ordering>> sortOrders = new ArrayList<Pair<Integer, Ordering>>();
		final List<String> columns = visibleAggregationProperties;
		for (final Pair<String, Ordering> aggregationProperty : sortObjects) {
		    final int sortOrder = getIndex(aggregationProperty.getKey(), columns);
		    if (sortOrder >= 0) {
			sortOrders.add(new Pair<Integer, Ordering>(Integer.valueOf(sortOrder), aggregationProperty.getValue()));
		    }
		}
		if (sortOrders.isEmpty()) {
		    return defaultCompare(o1, o2);
		}
		for (final Pair<Integer, Ordering> sortingParam : sortOrders) {
		    final Comparable<?> value1 = (Comparable) o1.getValueAt(sortingParam.getKey().intValue() + 1);
		    final Comparable<?> value2 = (Comparable) o2.getValueAt(sortingParam.getKey().intValue() + 1);
		    int result = 0;
		    if (value1 == null) {
			if (value2 != null) {
			    return -1;
			}
		    } else {
			if (value2 == null) {
			    return 1;
			} else {
			    result = compareValues(value1, value2, sortingParam.getValue());
			}
		    }
		    if (result != 0) {
			return result;
		    }
		}
		return defaultCompare(o1, o2);
	    }

	    @SuppressWarnings({ "rawtypes", "unchecked" })
	    private int compareValues(final Comparable value1, final Comparable value2, final Ordering sortingParam) {
		final int sortMultiplier = sortingParam == Ordering.ASCENDING ? 1 : (sortingParam == Ordering.DESCENDING ? -1 : 0);
		return value1.compareTo(value2) * sortMultiplier;
	    }

	    private int defaultCompare(final MutableTreeTableNode o1, final MutableTreeTableNode o2) {
		if (o1.getUserObject().equals(PivotTreeTableNodeEx.NULL_USER_OBJECT)) {
		    if (o2.getUserObject().equals(PivotTreeTableNodeEx.NULL_USER_OBJECT)) {
			return 0;
		    } else {
			return -1;
		    }
		} else {
		    if (o2.getUserObject().equals(PivotTreeTableNodeEx.NULL_USER_OBJECT)) {
			return 1;
		    } else {
			return o1.getUserObject().toString().compareTo(o2.getUserObject().toString());
		    }
		}

	    }

	    private int getIndex(final String string, final List<String> properties) {
		for (int index = 0; index < properties.size(); index++) {
		    final String anotherAggregation = properties.get(index);
		    if (anotherAggregation.equals(string)) {
			return index;
		    }
		}
		return -1;
	    }

	}
    }
}
