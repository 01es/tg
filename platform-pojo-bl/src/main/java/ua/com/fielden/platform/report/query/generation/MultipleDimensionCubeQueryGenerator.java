package ua.com.fielden.platform.report.query.generation;

import java.util.ArrayList;
import java.util.List;

import ua.com.fielden.platform.dao.QueryExecutionModel;
import ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager.ICentreDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.domaintree.centre.analyses.IPivotDomainTreeManager;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;

public class MultipleDimensionCubeQueryGenerator<T extends AbstractEntity<?>> extends GroupAnalysisQueryGenerator<T> {

    public MultipleDimensionCubeQueryGenerator(final Class<T> root, final ICentreDomainTreeManagerAndEnhancer cdtme, final IPivotDomainTreeManager adtm) {
	super(root, cdtme, adtm);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AnalysisResultClassBundle<T> generateQueryModel() {
	//Generate analysis result map that is based on analysis domain manager associated with this query generator.
	final AnalysisResultClassBundle<T> classBundle = (AnalysisResultClassBundle<T>) AnalysisResultClass.generateAnalysisQueryClass(//
	(Class<T>) getCdtme().getEnhancer().getManagedType(getRoot()),//
		adtm().getFirstTick().checkedProperties(getRoot()),//
		adtm().getSecondTick().checkedProperties(getRoot()),//
		adtm().getFirstTick().usedProperties(getRoot()),//
		adtm().getSecondTick().usedProperties(getRoot()));

	final List<String> rowDistributionProperties = adtm().getFirstTick().usedProperties(getRoot());
	final List<String> columnDistributionProperties = new ArrayList<>();

	final List<QueryExecutionModel<T, EntityResultQueryModel<T>>> resultQueryList = new ArrayList<>();
	final List<String> rowGroups = new ArrayList<>();
	for(int rowIndex = -1; rowIndex < rowDistributionProperties.size(); rowIndex++){
	    if(rowIndex >= 0){
		rowGroups.add(rowDistributionProperties.get(rowIndex));
	    }
	    final List<String> columnGroups = new ArrayList<>();
	    for(int columnIndex = -1; columnIndex < columnDistributionProperties.size(); columnIndex++){
		if(columnIndex >= 0){
		    columnGroups.add(columnDistributionProperties.get(columnIndex));
		}
		final List<String> groups = new ArrayList<>(rowGroups);
		groups.addAll(columnGroups);
		resultQueryList.add(createQueryAndGroupBy(classBundle.getGeneratedClass(), groups));
	    }
	}
	return new AnalysisResultClassBundle<>(classBundle.getGeneratedClass(), classBundle.getGeneratedClassRepresentation(), resultQueryList);
    }

}
