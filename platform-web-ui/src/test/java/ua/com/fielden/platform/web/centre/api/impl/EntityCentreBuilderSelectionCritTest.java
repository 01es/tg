package ua.com.fielden.platform.web.centre.api.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ua.com.fielden.platform.web.centre.api.context.impl.EntityCentreContextSelector.context;
import static ua.com.fielden.platform.web.centre.api.impl.EntityCentreBuilder.centreFor;

import org.junit.Test;

import ua.com.fielden.platform.sample.domain.TgOrgUnit1;
import ua.com.fielden.platform.sample.domain.TgVehicle;
import ua.com.fielden.platform.sample.domain.TgWorkOrder;
import ua.com.fielden.platform.web.centre.api.EntityCentreConfig;
import ua.com.fielden.platform.web.centre.api.impl.helpers.CustomOrgUnit1Matcher;
import ua.com.fielden.platform.web.centre.api.impl.helpers.CustomVehicleMatcher;
import ua.com.fielden.platform.web.interfaces.ILayout.Device;
import ua.com.fielden.platform.web.interfaces.ILayout.Orientation;

/**
 * A test case for Entity Centre DSL produced selection criteria.
 *
 * @author TG Team
 *
 */
public class EntityCentreBuilderSelectionCritTest {

    @Test
    public void selection_criteria_should_support_custom_autcompleters_with_custom_context() {
        final EntityCentreConfig<TgWorkOrder> config = centreFor(TgWorkOrder.class)
                .addCrit("vehicle").asMulti().autocompleter(TgVehicle.class).withMatcher(CustomVehicleMatcher.class)
                .also()
                .addCrit("orgUnit1").asMulti().autocompleter(TgOrgUnit1.class).withMatcher(CustomOrgUnit1Matcher.class, context().withCurrentEntity().withSelectionCrit().build())
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', [][]")
                .addProp("desc").build();

        assertTrue(config.getSelectionCriteria().isPresent());
        assertEquals("vehicle", config.getSelectionCriteria().get().get(0));
        assertEquals("orgUnit1", config.getSelectionCriteria().get().get(1));
        assertTrue(config.getValueMatchersForSelectionCriteria().isPresent());
        assertEquals(2, config.getValueMatchersForSelectionCriteria().get().size());
        assertEquals(CustomVehicleMatcher.class, config.getValueMatchersForSelectionCriteria().get().get("vehicle").getKey());
        assertFalse(config.getValueMatchersForSelectionCriteria().get().get("vehicle").getValue().isPresent());
        assertEquals(CustomOrgUnit1Matcher.class, config.getValueMatchersForSelectionCriteria().get().get("orgUnit1").getKey());
        assertTrue(config.getValueMatchersForSelectionCriteria().get().get("orgUnit1").getValue().isPresent());
    }

    @Test
    public void selection_criteria_should_not_permit_invalid_property_expressions() {
        try {
            centreFor(TgWorkOrder.class)
                .addCrit("non.existing.property").asMulti().autocompleter(TgVehicle.class)
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', []")
                .addProp("desc").build();
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals(String.format("Provided value '%s' is not a valid property expression for entity '%s'", "non.existing.property", TgWorkOrder.class.getSimpleName()), ex.getMessage());
        }
    }

    @Test
    public void selection_criteria_should_not_permit_non_critonly_as_single_valued_criteria() {
        try {
            centreFor(TgWorkOrder.class)
                .addCrit("vehicle").asSingle().autocompleter(TgVehicle.class)
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', []")
                .addProp("desc").build();
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals(String.format("Property '%s'@'%s' cannot be used as a single-valued criterion due to missing @CritOnly(SINGLE) in its definition.", "vehicle", TgWorkOrder.class.getSimpleName()), ex.getMessage());
        }
    }

    @Test
    public void selection_criteria_should_not_permit_range_critonly_as_single_valued_criteria() {
        try {
            centreFor(TgWorkOrder.class)
                .addCrit("orgunitCritOnly").asSingle().autocompleter(TgOrgUnit1.class)
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', []")
                .addProp("desc").build();
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals(String.format("Property '%s'@'%s' cannot be used as a single-valued criterion due to its definition as @CritOnly(RANGE).", "orgunitCritOnly", TgWorkOrder.class.getSimpleName()), ex.getMessage());
        }
    }

    @Test
    public void selection_criteria_should_not_permit_single_critonly_as_multi_valued_criteria() {
        try {
            centreFor(TgWorkOrder.class)
                .addCrit("orgunitCritOnlySingle").asMulti().autocompleter(TgOrgUnit1.class)
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', []")
                .addProp("desc").build();
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals(String.format("Property '%s'@'%s' cannot be used as a multi-valued criterion due to its definition as @CritOnly(SINGLE).", "orgunitCritOnlySingle", TgWorkOrder.class.getSimpleName()), ex.getMessage());
        }
    }

    @Test
    public void selection_criteria_should_not_permit_single_critonly_as_range_valued_criteria() {
        try {
            centreFor(TgWorkOrder.class)
                .addCrit("intSingle").asRange().integer()
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', []")
                .addProp("desc").build();
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals(String.format("Property '%s'@'%s' cannot be used as a range-valued criterion due to its definition as @CritOnly(SINGLE).", "intSingle", TgWorkOrder.class.getSimpleName()), ex.getMessage());
        }
    }

    @Test
    public void selection_criteria_should_permit_single_critonly_as_single_valued_criteria() {
        final EntityCentreConfig<TgWorkOrder> config = centreFor(TgWorkOrder.class)
                .addCrit("intSingle").asSingle().integer()
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', []")
                .addProp("desc").build();

            assertTrue(config.getSelectionCriteria().isPresent());
            assertEquals("intSingle", config.getSelectionCriteria().get().get(0));
    }

    @Test
    public void selection_criteria_should_permit_range_critonly_as_range_valued_criteria() {
        final EntityCentreConfig<TgWorkOrder> config = centreFor(TgWorkOrder.class)
                .addCrit("intRange").asRange().integer()
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', []")
                .addProp("desc").build();

            assertTrue(config.getSelectionCriteria().isPresent());
            assertEquals("intRange", config.getSelectionCriteria().get().get(0));
    }

    @Test
    public void selection_criteria_should_not_permit_multi_valued_crit_as_autocompleter_with_null_type() {
        try {
            centreFor(TgWorkOrder.class)
                .addCrit("orgunitCritOnly").asMulti().autocompleter(null) // trying to trick the system
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', []")
                .addProp("desc").build();
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals("Property type is a required argument and cannot be omitted.", ex.getMessage());
        }
    }

    @Test
    public void selection_criteria_should_not_permit_multi_valued_crit_as_autocompleter_for_non_entity_type() {
        try {
            centreFor(TgWorkOrder.class)
                .addCrit("key").asMulti().autocompleter(TgWorkOrder.class) // trying to trick the system
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', []")
                .addProp("desc").build();
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals(String.format("Property '%s'@'%s' cannot be used for autocompletion as it is not of an entity type (%s).", "key", TgWorkOrder.class.getSimpleName(), String.class.getSimpleName()), ex.getMessage());
        }
    }

    @Test
    public void selection_criteria_should_not_permit_multi_valued_crit_as_autocompleter_with_non_matching_property_type() {
        try {
            centreFor(TgWorkOrder.class)
                .addCrit("orgunitCritOnly").asMulti().autocompleter(TgWorkOrder.class)
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', []")
                .addProp("desc").build();
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals(String.format("Property '%s'@'%s' has type %s, but type %s is has been specified instead.", "orgunitCritOnly", TgWorkOrder.class.getSimpleName(), TgOrgUnit1.class.getSimpleName(), TgWorkOrder.class.getSimpleName()), ex.getMessage());
        }
    }

    @Test
    public void selection_criteria_should_not_permit_multi_valued_crit_as_text_for_non_string_property() {
        try {
            centreFor(TgWorkOrder.class)
                .addCrit("vehicle").asMulti().text()
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', []")
                .addProp("desc").build();
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals(String.format("Property '%s'@'%s' cannot be used for a text component as it is not of type String (%s).", "vehicle", TgWorkOrder.class.getSimpleName(), TgVehicle.class.getSimpleName()), ex.getMessage());
        }
    }

    @Test
    public void selection_criteria_should_not_permit_multi_valued_crit_as_bool_for_non_boolean_property() {
        try {
            centreFor(TgWorkOrder.class)
                .addCrit("vehicle.key").asMulti().bool()
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', []")
                .addProp("desc").build();
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals(String.format("Property '%s'@'%s' cannot be used for a boolean component as it is not of type boolean (%s).", "vehicle.key", TgWorkOrder.class.getSimpleName(), String.class.getSimpleName()), ex.getMessage());
        }
    }

    @Test
    public void selection_criteria_should_with_all_possible_multi_valued_criteria_should_be_constructed_successfully() {
        final EntityCentreConfig<TgWorkOrder> config = centreFor(TgWorkOrder.class)
                .addCrit("vehicle.active").asMulti().bool()
                .also()
                .addCrit("vehicle.desc").asMulti().text()
                .also()
                .addCrit("vehicle").asMulti().autocompleter(TgVehicle.class)
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "['vertical', 'justified', 'margin:20px', [][][]")
                .addProp("desc").build();

        assertTrue(config.getSelectionCriteria().isPresent());
        assertEquals(3, config.getSelectionCriteria().get().size());
        assertEquals("vehicle.active", config.getSelectionCriteria().get().get(0));
        assertEquals("vehicle.desc", config.getSelectionCriteria().get().get(1));
        assertEquals("vehicle", config.getSelectionCriteria().get().get(2));
    }


}
