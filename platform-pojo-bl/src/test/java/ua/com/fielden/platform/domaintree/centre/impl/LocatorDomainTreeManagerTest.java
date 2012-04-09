package ua.com.fielden.platform.domaintree.centre.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import ua.com.fielden.platform.domaintree.centre.ILocatorDomainTreeManager;
import ua.com.fielden.platform.domaintree.centre.ILocatorDomainTreeManager.ILocatorDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.domaintree.centre.ILocatorDomainTreeManager.SearchBy;
import ua.com.fielden.platform.domaintree.testing.MasterEntity;


/**
 * A test for {@link CentreDomainTreeManager}.
 *
 * @author TG Team
 *
 */
public class LocatorDomainTreeManagerTest extends CentreDomainTreeManagerTest {
    @Override
    protected ILocatorDomainTreeManagerAndEnhancer dtm() {
	return (ILocatorDomainTreeManagerAndEnhancer) super.dtm();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// Test initialisation ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates root types.
     *
     * @return
     */
    protected static Set<Class<?>> createRootTypes_for_LocatorDomainTreeManagerTest() {
	final Set<Class<?>> rootTypes = createRootTypes_for_CentreDomainTreeManagerTest();
	return rootTypes;
    }

    /**
     * Provides a testing configuration for the manager.
     *
     * @param dtm
     */
    protected static void manageTestingDTM_for_LocatorDomainTreeManagerTest(final ILocatorDomainTreeManager dtm) {
	manageTestingDTM_for_CentreDomainTreeManagerTest(dtm);

	dtm.getFirstTick().checkedProperties(MasterEntity.class);
	dtm.getSecondTick().checkedProperties(MasterEntity.class);
    }

    @BeforeClass
    public static void initDomainTreeTest() {
	final ILocatorDomainTreeManagerAndEnhancer dtm = new LocatorDomainTreeManagerAndEnhancer(serialiser(), createRootTypes_for_LocatorDomainTreeManagerTest());
	manageTestingDTM_for_LocatorDomainTreeManagerTest(dtm);
	setDtmArray(serialiser().serialise(dtm));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// End of Test initialisation ////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_that_UseForAutocompletion_can_be_set_and_altered() {
	// THE FIRST TIME -- returns DEFAULT VALUE //
	// default value should be FALSE
	assertFalse("UseForAutocompletion by default should be FALSE", dtm().isUseForAutocompletion());

	// Alter and check //
	assertTrue("The manager reference should be the same.", dtm() == dtm().setUseForAutocompletion(true));
	assertTrue("UseForAutocompletion should be true.", dtm().isUseForAutocompletion());
    }

    @Test
    public void test_that_SearchBy_can_be_set_and_altered() {
	// THE FIRST TIME -- returns DEFAULT VALUE //
	// default value should be SearchBy.KEY
	assertEquals("SearchBy by default should be SearchBy.KEY.", SearchBy.KEY, dtm().getSearchBy());

	// Alter and check //
	assertTrue("The manager reference should be the same.", dtm() == dtm().setSearchBy(SearchBy.DESC_AND_KEY));
	assertEquals("SearchBy should be SearchBy.DESC_AND_KEY.", SearchBy.DESC_AND_KEY, dtm().getSearchBy());
    }

    @Override
    public void test_that_runAutomatically_can_be_set_and_altered() {
	// THE FIRST TIME -- returns DEFAULT VALUE //
	// default value should be TRUE (overridden from CriteriaDTM behaviour)
	assertTrue("Run automatically by default should be TRUE.", dtm().isRunAutomatically());

	// Alter and check //
	assertTrue("The manager reference should be the same",dtm() == dtm().setRunAutomatically(false));
	assertFalse("Run automatically should be FALSE.", dtm().isRunAutomatically());
    }

    @Override
    public void test_that_column_number_for_first_tick_can_be_set_and_altered() {
	// THE FIRST TIME -- returns DEFAULT VALUE //
	// default value should be 1 (overridden from CriteriaDTM behaviour)
	assertEquals("The default column number should be 1.", dtm().getFirstTick().getColumnsNumber(), 1);

	// Alter and check //
	assertTrue("The first tick reference should be the same.",dtm().getFirstTick() == dtm().getFirstTick().setColumnsNumber(3));
	assertEquals("The number of columns should be 3", dtm().getFirstTick().getColumnsNumber(), 3);
    }

    @Override
    public void test_that_serialisation_works() throws Exception {
	super.test_that_serialisation_works();
    }

    @Override
    public void test_that_equality_and_copying_works() {
	super.test_that_equality_and_copying_works();
    }

    @Override
    public void test_that_CHECKed_properties_order_is_correct() throws Exception {
    }

    @Override
    public void test_that_CHECKed_properties_order_is_correct_and_can_be_altered() throws Exception {
    }

    @Override
    public void test_that_CHECKed_properties_Move_Swap_operations_work() throws Exception {
    }

    @Override
    public void test_that_CHECKed_properties_Move_Swap_operations_doesnot_work_for_non_checked_properties() {
    }

    @Override
    public void test_that_domain_changes_are_correctly_reflected_in_CHECKed_properties() {
    }
}
