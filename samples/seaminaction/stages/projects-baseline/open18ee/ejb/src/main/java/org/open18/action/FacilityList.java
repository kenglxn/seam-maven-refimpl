package org.open18.action;

import org.open18.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("facilityList")
public class FacilityList extends EntityQuery {

	/* https://jira.jboss.org/jira/browse/JBSEAM-2562
	private static final String[] RESTRICTIONS = {
			"lower(facility.address) like concat(lower(#{facilityList.facility.address}),'%')",
			"lower(facility.city) like concat(lower(#{facilityList.facility.city}),'%')",
			"lower(facility.country) like concat(lower(#{facilityList.facility.country}),'%')",
			"lower(facility.county) like concat(lower(#{facilityList.facility.county}),'%')",
			"lower(facility.description) like concat(lower(#{facilityList.facility.description}),'%')",
			"lower(facility.name) like concat(lower(#{facilityList.facility.name}),'%')",
			"lower(facility.phone) like concat(lower(#{facilityList.facility.phone}),'%')",
			"lower(facility.state) like concat(lower(#{facilityList.facility.state}),'%')",
			"lower(facility.type) like concat(lower(#{facilityList.facility.type}),'%')",
			"lower(facility.uri) like concat(lower(#{facilityList.facility.uri}),'%')",
			"lower(facility.zip) like concat(lower(#{facilityList.facility.zip}),'%')",};
	*/
	private static final String[] RESTRICTIONS = {};

	private Facility facility = new Facility();

	@Override
	public String getEjbql() {
		return "select facility from Facility facility";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public Facility getFacility() {
		return facility;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
