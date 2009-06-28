package org.open18.action;

import org.open18.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("holeList")
public class HoleList extends EntityQuery {

	private static final String[] RESTRICTIONS = {"lower(hole.name) like concat(lower(#{holeList.hole.name}),'%')",};

	private Hole hole = new Hole();

	public HoleList() {
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
	}
	
	@Override
	public String getEjbql() {
		return "select hole from Hole hole";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public Hole getHole() {
		return hole;
	}

/*
 * LOO-20090627, NOTE: This does not work. 
 * Must use setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS)); in constructor
 *
	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}
 */

}
