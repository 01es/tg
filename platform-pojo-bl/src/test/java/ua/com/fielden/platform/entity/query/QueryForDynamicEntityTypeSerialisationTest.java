package ua.com.fielden.platform.entity.query;

import static org.junit.Assert.assertEquals;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ua.com.fielden.platform.dao.QueryExecutionModel;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.Calculated;
import ua.com.fielden.platform.entity.annotation.factory.CalculatedAnnotation;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.ioc.ApplicationInjectorFactory;
import ua.com.fielden.platform.reflection.asm.api.NewProperty;
import ua.com.fielden.platform.reflection.asm.impl.DynamicEntityClassLoader;
import ua.com.fielden.platform.sample.domain.TgOrgUnit5;
import ua.com.fielden.platform.sample.domain.TgVehicle;
import ua.com.fielden.platform.sample.domain.TgVehicleMake;
import ua.com.fielden.platform.sample.domain.TgVehicleModel;
import ua.com.fielden.platform.serialisation.impl.ProvidedSerialisationClassProvider;
import ua.com.fielden.platform.serialisation.impl.TgKryo;
import ua.com.fielden.platform.test.CommonTestEntityModuleWithPropertyFactory;
import ua.com.fielden.platform.types.Money;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Ensures correct serialisation/deserialisation of EQL when created for dynamically generated entity types.
 *
 * @author TG Team
 *
 */
public class QueryForDynamicEntityTypeSerialisationTest {

    private final Module module = new CommonTestEntityModuleWithPropertyFactory();
    private final Injector injector = new ApplicationInjectorFactory().add(module).getInjector();
    private final EntityFactory factory = injector.getInstance(EntityFactory.class);

    private final List<Class<?>> types = new ArrayList<Class<?>>();
    {
	types.add(TgVehicle.class);
	types.add(TgVehicleMake.class);
	types.add(TgVehicleModel.class);
	types.add(TgOrgUnit5.class);
    }

    private final TgKryo kryoWriter = new TgKryo(factory, new ProvidedSerialisationClassProvider(types.toArray(new Class[]{})));
    private final TgKryo kryoReader = new TgKryo(factory, new ProvidedSerialisationClassProvider(types.toArray(new Class[]{})));



    private static final String NEW_PROPERTY_DESC = "Description  for new money property";
    private static final String NEW_PROPERTY_TITLE = "New money property";
    private static final String NEW_PROPERTY_EXPRESSION = "2 * 3 - [integerProp]";
    private static final String NEW_PROPERTY_ORIGINATION = "integerProp";
    private static final String NEW_PROPERTY_1 = "newProperty_1";
    private static final String NEW_PROPERTY_2 = "newProperty_2";
    private boolean observed = false;
    private DynamicEntityClassLoader cl;

    private final Calculated calculated = new CalculatedAnnotation().contextualExpression(NEW_PROPERTY_EXPRESSION).origination(NEW_PROPERTY_ORIGINATION).newInstance();

    private final NewProperty pd1 = new NewProperty(NEW_PROPERTY_1, Money.class, false, NEW_PROPERTY_TITLE, NEW_PROPERTY_DESC, calculated);
    private final NewProperty pd2 = new NewProperty(NEW_PROPERTY_2, Money.class, false,  NEW_PROPERTY_TITLE, NEW_PROPERTY_DESC, calculated);



    @Before
    public void setUp() {
	observed = false;
	cl = new DynamicEntityClassLoader(ClassLoader.getSystemClassLoader());
    }

    @Test
    public void seralisation_of_simple_query_should_not_have_failed() throws Exception {
	final Class<? extends AbstractEntity> newType1 = (Class<? extends AbstractEntity>) cl.startModification(TgVehicle.class.getName()).addProperties(pd1).endModification();

	final EntityResultQueryModel q =  select(newType1).model();
	final QueryExecutionModel original = from(q).build();

	final byte[] data = cl.getCachedByteArray(newType1.getName());
	final List<byte[]> listOfClasses = new ArrayList<byte[]>();
	listOfClasses.add(data);

	final DynamicallyTypedQueryContainer container = new DynamicallyTypedQueryContainer(listOfClasses, original);

	final DynamicallyTypedQueryContainer restored = serialiseAndRestore(container);

	assertEquals(container, restored);
	assertEquals(container.hashCode(), restored.hashCode());
    }



    /**
     * A convenient method to serialise and deserialise the passed in model allocating a buffer of the specified size.
     */
    private DynamicallyTypedQueryContainer serialiseAndRestore(final DynamicallyTypedQueryContainer originalQuery) {
	try {
	    return kryoReader.deserialise(kryoWriter.serialise(originalQuery), DynamicallyTypedQueryContainer.class);
	} catch (final Exception e) {
	    throw new IllegalStateException(e);
	}
    }

}