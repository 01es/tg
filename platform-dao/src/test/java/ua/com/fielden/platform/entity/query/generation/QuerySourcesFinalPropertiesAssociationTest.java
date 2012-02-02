package ua.com.fielden.platform.entity.query.generation;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import ua.com.fielden.platform.entity.query.generation.elements.AbstractEntQuerySource.PropResolutionInfo;
import ua.com.fielden.platform.entity.query.generation.elements.EntQuery;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.entity.query.model.PrimitiveResultQueryModel;
import ua.com.fielden.platform.sample.domain.TgOrgUnit5;
import ua.com.fielden.platform.sample.domain.TgVehicle;
import static org.junit.Assert.assertEquals;
import static ua.com.fielden.platform.entity.query.fluent.query.select;

public class QuerySourcesFinalPropertiesAssociationTest extends BaseEntQueryTCase {

    private final String incP2S = "Inccorect association between properties and query sources";
    private final String incFP2S = "Inccorect association between properties and query sources";


    @Test
    public void test0() {
	final EntityResultQueryModel<TgOrgUnit5> shortcutQry  = select(VEHICLE).as("v").where().prop("v.station.key").eq().val("AA").yield().prop("station").modelAsEntity(ORG5);
	final EntQuery entQry = entQry(shortcutQry);

	final List<PropResolutionInfo> src1FinProps = prepare( //
		propResInf("v.station", "v", "station", false, ORG5, "station"), //
		propResInf("station", null, "station", false, ORG5, "station"));

	final List<PropResolutionInfo> src2FinProps = prepare( //
		propResInf("v.station.id", "v.station", "id", false, LONG, "id"), //
		propResInf("v.station.key", "v.station", "key", false, STRING, "key"));
	assertEquals(incP2S, compose(src1FinProps, src2FinProps), getSourcesFinalReferencingProps(entQry));
    }

    @Test
    public void test1() {
	final EntityResultQueryModel<TgVehicle> sourceQry = select(VEHICLE).where().prop("station.key").eq().val("AA"). //
		yield().prop("id").as("id"). //
		yield().prop("key").as("key"). //
		yield().prop("model").as("model"). //
		yield().prop("model.make").as("model.make"). //
		yield().prop("model.key").as("model.key"). //
		modelAsEntity(VEHICLE);
	final PrimitiveResultQueryModel qry = select(sourceQry).where().prop("model.key").eq().val("AA").yield().prop("model.key").modelAsPrimitive();
	final EntQuery entQry = entQry(qry);

	final List<PropResolutionInfo> src1FinProps = prepare( //
		propResInf("model.key", null, "model.key", false, STRING, "model.key"),
		propResInf("model.key", null, "model.key", false, STRING, "model.key")
		);

	assertEquals(incP2S, compose(src1FinProps), getSourcesFinalReferencingProps(entQry));
    }

    @Test
    @Ignore
    public void test2() {
	final EntityResultQueryModel<TgVehicle> sourceQry = select(VEHICLE).where().prop("station.key").eq().val("AA"). //
		yield().prop("id").as("id"). //
		yield().prop("key").as("key"). //
		yield().prop("model").as("model"). //
		yield().prop("model.make").as("model.make"). //
		yield().prop("model.key").as("model.key"). //
		modelAsEntity(VEHICLE);
	final PrimitiveResultQueryModel qry = select(sourceQry).where().prop("model.make.key").eq().val("AA").yield().prop("model.make.key").modelAsPrimitive();
	final EntQuery entQry = entQry(qry);

	final List<PropResolutionInfo> src1FinProps = prepare( //
		propResInf("model.make", null, "model.make", false, MAKE, "model.make")
		);

	final List<PropResolutionInfo> src2FinProps = prepare( //
		propResInf("model.make.id", "model.make", "id", false, LONG, "id"), //
		propResInf("model.make.key", "model.make", "key", false, STRING, "key"), //
		propResInf("model.make.key", "model.make", "key", false, STRING, "key"));
	assertEquals(incP2S, compose(src1FinProps, src2FinProps), getSourcesFinalReferencingProps(entQry));
    }
}